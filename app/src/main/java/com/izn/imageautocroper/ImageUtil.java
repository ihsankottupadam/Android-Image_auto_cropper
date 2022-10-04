package com.izn.imageautocroper;
import android.graphics.*;
import android.media.*;
import android.util.*;
import java.io.*;
import android.widget.*;

public class ImageUtil
{
	public ImageUtil(){}
	public static Bitmap GetOrientedBitmap(File imgfile){
		Bitmap bitmap=null;
		
		try {
			bitmap=BitmapFactory.decodeFile(imgfile.toString());
			if(imgfile.toString().toLowerCase().endsWith(".png")){
				//Log.i("ImageUtil",imgfile.toString()+"|not Need to orien");
				return bitmap;
			}
			//Log.i("ImageUtil",imgfile.toString()+"|oriented");
			ExifInterface exif = new ExifInterface(imgfile.getAbsolutePath());
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
			}
			else if (orientation == 3) {
				matrix.postRotate(180);
			}
			else if (orientation == 8) {
				matrix.postRotate(270);
			}
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
		}
		catch (Exception e) {

		}

		return bitmap;

	}
	public static Bitmap GetOrientedBitmap(Bitmap bitmap,String path){
		
		try {
			if(path.toLowerCase().endsWith(".png")){
				return bitmap;
			}
			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
			Matrix matrix = new Matrix();
			if (orientation == 6) {
				matrix.postRotate(90);
			}
			else if (orientation == 3) {
				matrix.postRotate(180);
			}
			else if (orientation == 8) {
				matrix.postRotate(270);
			}
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
		}
		catch (Exception e) {

		}

		return bitmap;

	}
	static Bitmap decodeSampledBitmap(String fileName, int reqWidth,int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(fileName,options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap bitmap= BitmapFactory.decodeFile(fileName, options);
		if(!fileName.toLowerCase().endsWith(".png")){
			bitmap=GetOrientedBitmap(bitmap,fileName);
		}
		return bitmap;
    }
    
	static File compressImage(File imageFile, float reqWidth, float reqHeight, Bitmap.CompressFormat compressFormat, int quality, String destinationPath) throws IOException {
        FileOutputStream fileOutputStream = null;
        File file = new File(destinationPath).getParentFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            fileOutputStream = new FileOutputStream(destinationPath);
            // write the compressed bitmap at the destination specified by destinationPath.
            decodeSampledBitmapFromFile(imageFile, reqWidth, reqHeight).compress(compressFormat, quality, fileOutputStream);
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        return new File(destinationPath);
    }

    static Bitmap decodeSampledBitmapFromFile(File imageFile, float reqWidth, float reqHeight) throws IOException {
        // First decode with inJustDecodeBounds=true to check dimensions

        Bitmap scaledBitmap = null, bmp = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = reqWidth / reqHeight;

        if (actualHeight > reqHeight || actualWidth > reqWidth) {
            //If Height is greater
            if (imgRatio < maxRatio) {
                imgRatio = reqHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) reqHeight;

            }  //If Width is greater
            else if (imgRatio > maxRatio) {
                imgRatio = reqWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) reqWidth;
            } else {
                actualHeight = (int) reqHeight;
                actualWidth = (int) reqWidth;
            }
        }

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2,
						  middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        bmp.recycle();
        ExifInterface exif;
        try {
            exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(),
											   scaledBitmap.getHeight(), matrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scaledBitmap;


    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            inSampleSize *= 2;
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
   }
	
}
