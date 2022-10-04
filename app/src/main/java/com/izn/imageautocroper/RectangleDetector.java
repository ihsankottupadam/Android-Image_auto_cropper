package com.izn.imageautocroper;

import android.graphics.*;
import java.util.*;
import android.widget.*;
import android.content.*;
import android.util.*;
import java.io.*;
import android.media.*;

public class RectangleDetector
{
	Bitmap bitmap;
	int height;
	int width;
	int minHeight;
	int minWidth;
	int scanSy;
	int scanSx;

	private ArrayList<Rectangle> Rectangles=new ArrayList<Rectangle>();
	private int ImageType;
	private Context mContext;
	private String Path;
	private int Orientation;
	private int pColor =Color.RED;
	
	public RectangleDetector(Context context,String path){
		
		bitmap= null;
	    File imgfile=new File(path);
		try {
			bitmap=BitmapFactory.decodeFile(imgfile.toString());
			//bitmap=ImageUtil.GetOrientedBitmap(bitmap,path);
			// rotating bitmap
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		mContext=context;
		if(bitmap==null){
			return;
		}
		this.Path=path;
		this.height=bitmap.getHeight();
		this.width=bitmap.getWidth();
	    this.ImageType=getImageType();
		this.minHeight=height*1/10;
		this.minWidth=width*1/10;
		
	}
	public RectangleDetector(Context context,Bitmap bitmap){
		this.bitmap= bitmap;
		mContext=context;
		if(bitmap==null){
			return;
		}
		this.height=bitmap.getHeight();
		this.width=bitmap.getWidth();
	    this.ImageType=getImageType();
		this.minHeight=height*1/10;
		this.minWidth=width*1/10;
		Log.i("Detector","width:"+this.width+"height"+this.height);

	}
	public void set(int startX,int startY){
		scanSx=startX;
		scanSy=startY;
	}
	public void setMinWidth(int minWidth){
		this.minWidth=minWidth;
	}
	public void setMinHeight(int minHeight){
		this.minHeight=minHeight;
	}
	public ArrayList<Rectangle> detectRectangles(){
		if(bitmap==null){return null;}
		
		int recheight=0;
		int ly=0;
		int i=0;
		do{
			Rectangle rec;
			rec=detectRectangle(0,ly);
			ly=rec.y2;
			recheight=rec.getHeight();
			/*showMessage(i+". Height: "+rec.getHeight());
			showMessage(i+". width: "+rec.getWidth());
			showMessage(i+". x1: "+rec.x1+" x2:  "+rec.x2);
			showMessage(i+". y1: "+rec.y1+" y2:  "+rec.y2);
			*/
			i++;
	
		  if(rec.getHeight()>minHeight&&rec.getWidth()>minWidth){
		      	Rectangles.add(rec);
		    }
	    //  Log.i("Rest .......","rest.  "+ly+"   height"+height);
		  }
		while(i<6&&recheight!=0&&ly!=height&&ImageType!=2);
	    if(Rectangles.size()==0){
			Rectangles.add(new Rectangle(0,0,width,height));
		}
		return Rectangles;
	}

	private void showMessage(Object p0)
	{
		Toast.makeText(mContext,String.valueOf(p0),Toast.LENGTH_SHORT).show();
	}


	private Rectangle detectRectangle(int sX, int scanY){
		int x=width*70/100;

		int startY=GetStartingContentY(x,scanY);
		int endY=0;
		if(ImageType==2){
			endY=GetEndingContentReverseY(width*11/20,startY);
		}
		else{
			endY=GetEndingContentY(x,startY);
		}
		int startX=GetStartingContentX(0,startY,endY);
		int endX= GetEndingContentReverseX(startX,startY,endY);
		Rectangle rect=new Rectangle(startX,startY,endX,endY);
		return rect;
	}
	//-------------------y1-------------------------y1----------------------
	private int GetStartingContentY(int x, int sY)
	{
		for(int y=sY;y<height;y++){
			int pixel=bitmap.getPixel(x, y);
			if (pixelIsContent(pixel)){
				if (hasContinuingContentY(x,y))
				{
					int aY=checkAnotherStartY(width*3/10,y);
					if(aY==y){
					//Log.i("","y1 detected "+y+ " pxl:"+ pixel);
						return y;
					}
				}
			}
		}
	    
		//Log.i("g","y1 not detected y1"+ sY);
		return sY;
	}
	private boolean hasContinuingContentY(int x, int dY)
	{
		int content=0;
		int fraction=10;
		for(int i=0;i<11;i++){
			dY=dY+fraction;
			if(dY<height){
				int pixel=bitmap.getPixel(x,dY);
				if(pixelIsContent(pixel)){
					content++;
				}
			}
		}
		if(content>7){
			return true;
		}
		return false;
	}
	private int checkAnotherStartY(int x, int ny)
	{
		int cY=ny-10;if(cY<0){cY=0;}
		int bY=ny+11;if(bY>height){bY=height-1;}
		for(int y=cY;y<bY;y++){
			int pixel=bitmap.getPixel(x,y);
			if(pixelIsContent(pixel)){
				if(hasContinuingContentY(x,y)){
					return y;
				}
			}
		}
		return 0;
	}
	//-------------------y2-------------------------y2----------------------
	private int GetEndingContentY(int x, int startingy)
	{
		int prevPix=0;
		for (int y=startingy;y<height;y++)
		{
		    int pixel=bitmap.getPixel(x, y);
			if (pixelIsBlank(pixel))
			{
				//if(prevPix!=pixel){
				if (hasContinuingBlankY(x,y))
				{
					int aY=checkAnotherEndY(bitmap,width*1/2,y);
					//if(aY==y){
						//Log.i("Det","y2 detected "+y+ " pxl:"+ pixel);
						return y;
						
					//}
				}
				//}
				//prevPix=pixel;
			}
		}
	   // Log.i("Detector","y2 not detected y2"+height);
		return height;
	}
	private boolean hasContinuingBlankY(int x, int y)
	{
		int blank=0;
		int fraction=10;
		for(int i=0;i<6;i++){
			y=y+fraction;
			if(y<bitmap.getHeight()){
				int pixel=bitmap.getPixel(x,y);
				if(pixelIsBlank(pixel)){
					blank++;
				}
			}
		}
		if(blank>3){
			return true;
		}
		return false;
	}
	private int checkAnotherEndY(Bitmap bitmap, int x,int ny)
	{

		int cY=ny-10;if(cY<0){cY=0;}
		int bY=ny+11;if(bY>bitmap.getHeight()){bY=bitmap.getHeight();}
		for(int y=cY;y<bY;y++){
			int pixel=bitmap.getPixel(x,y);
			if(pixelIsBlank(pixel)){

				if(hasContinuingBlankY(x,y)){
					return y;
				}
			}
		}
		return 0;
	}
    ///-----y2-----type2-------------------------y2 type2-------
	private int GetEndingContentReverseY(int x, int startingy)
	{
		for (int y=height-1;y>=startingy;y--)
		{
		    int pixel=bitmap.getPixel(x, y);
			if (pixel!=-16777216)
			{
				if(!HascontinuingBlackYRevers(x,y)){
					return y;
				}

			}
		}
		return height;
	}
	private boolean HascontinuingBlackYRevers(int x,int y){
		int black=0;
		int fraction=15;
		for(int i=0;i<11;i++){
			y=y-fraction;
			if(y<0){
				int pixel=bitmap.getPixel(x,y);
				if(pixel==Color.BLACK){
					black++;
				}
			}
		}
		if(black>7){
			return true;
		}
		return false;
	}
	//-------------------------------x1-------------------------------------
	private int GetStartingContentX(int sX, int sY,int eY)
	{
		
		int y=sY+((eY-sY)*1/4);
		for(int x=sX;x<width;x++){
			int pixel=bitmap.getPixel(x, y);
			if (pixelIsContent(pixel)){
				if (hasContinuingContentX(x,y)){
					int aX=checkAnotherStartX(x,sY+((eY-sY)*3/4));
					if(aX==x){
						//Log.i("","x1 detected "+x+ " pxl:"+ pixel);
						return x;
						
					}
				}
			}
		}
		//Log.i("","x1 not detected "+0);
		return 0;
	}
	private int checkAnotherStartX(int nX, int y)
	{
		int cX=0;
		int bX=nX+11;if(bX>width){bX=width;}
		for(int x=cX;x<bX;x++){
			int pixel=bitmap.getPixel(x,y);
			if(pixelIsContent(pixel)){
				if(hasContinuingContentX(x,y)){
					return x;
				}
			}
		}
		return 0;
	}

	private boolean hasContinuingContentX(int x, int y)
	{
		int content=0;
		//int fraction =bitmap.getHeight()* 8/1000;
		int fraction=20;
		for(int i=0;i<11;i++){
			x=x+fraction;
			if(x<width){
				int pixel=bitmap.getPixel(x,y);
				if(pixelIsContent(pixel)){
					content++;
				}
			}
		}
		if(content>7){
			return true;
		}
		return false;
	}
	//-----------------------------------x2-----------------------x2------------
	private int GetEndingContentReverseX(int sX, int sY, int eY)
	{
	
		int y=sY+((eY-sY)*1/4);
		for (int x=width-1;x>=sX;x--)
		{
		    int pixel=bitmap.getPixel(x, y);
			if (pixelIsContent(pixel))
			{
				if(HascontinuingContentXRevers(x,y)){
					int aX1=checkAnotherEndX(x,sY+((eY-sY)*3/4));
					if(aX1==x){
						int aX2=checkAnotherEndX(x,sY+((eY-sY)*1/2));
						if(aX2==x){
							//Log.i("","x2 detected "+(x+1)+ " pxl:"+ pixel);
							return x+1;
						}
				    }
				}

			}
		}
		//Log.i("tg","X2 notdetected "+y);
		return width;
	}

	private int checkAnotherEndX(int nX, int y)
	{
		int cX=nX+10;if(cX>=width){cX=width-1;}
		int bX=nX-11;if(bX<0){bX=0;}
		for(int x=cX;x>bX;x--){
			int pixel=bitmap.getPixel(x,y);
			if(pixelIsContent(pixel)){
				if(HascontinuingContentXRevers(x,y)){
					return x;
				}
			}
		}
		return 0;
	}

	private boolean HascontinuingContentXRevers(int x, int y)
	{
		int content=0;
		int fraction=15;
		for(int i=0;i<11;i++){
			x=x-fraction;
			if(x>0){
				int pixel=bitmap.getPixel(x,y);
				if(pixelIsContent(pixel)){
					content++;
				}
			}
		}
		if(content>7){
			return true;
		}

		return false;
	}
	//-------------------------COMMON--------------------------------------
	private int getImageType(){
		int x=width/2,y=0;
		int black=0;
		int fraction=15;
		for(int i=0;i<11;i++){
			y=y+fraction;
			if(y<bitmap.getHeight()){
				int pixel=bitmap.getPixel(x,y);
				if(pixel==Color.BLACK){
					black++;
				}
			}
		}
		if(black>7){
			return 2;
		}
		return 1;
	}
	private boolean pixelIsContent(int pixel)
	{
		if( pixel<-16711422){
			return false;
		}
		if(pixel==-986896||pixel==-328966||pixel==-1){
			return false;
		}
		return true;
	}
	private boolean pixelIsBlank(int pixel)
	{
		if(pixel==-986896||pixel==-328966||pixel==-1){
			return true;
		}
		return false;
	}
	public Bitmap getDrawnBitmap(){
		if(bitmap==null){
			return null;
		}
		Bitmap PreviewBitmap = Bitmap.createBitmap(bitmap.getWidth() ,bitmap.getHeight(), bitmap.getConfig());
		Canvas canvas = new Canvas(PreviewBitmap);
		canvas.drawBitmap(bitmap,0,0,null);
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(pColor);
		paint.setStrokeWidth(10);
		for(Rectangle rec:Rectangles){
			int startY=rec.y1;
			int endY=rec.y2;
			int startX=rec.x1;
			int endX=rec.x2;
			canvas.drawRect(startX,startY,endX,endY,paint);
		}
		return PreviewBitmap;
	}
	public static Bitmap drawRectangles(Bitmap bitmap,ArrayList<Rectangle> Rectangles,int[] dMatrix,int paintColor){
		if(bitmap==null){
			return null;
		}
		Bitmap PreviewBitmap = Bitmap.createBitmap(bitmap.getWidth() ,bitmap.getHeight(), bitmap.getConfig());
		Matrix matrix= new Matrix();
		float scale=(float)bitmap.getWidth()/dMatrix[0];
		matrix.setScale(scale,scale);
		Canvas canvas = new Canvas(PreviewBitmap);
		canvas.drawBitmap(bitmap,0,0,null);
		Paint paint=new Paint();
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(paintColor);
		paint.setStrokeWidth(10);
		for(Rectangle rec:Rectangles){
			RectF rect=rec.toRectF();
			matrix.mapRect(rect);
			canvas.drawRect(rect,paint);
		}
		return PreviewBitmap;
	}
	private boolean isFullWidth(){
		if(this.Rectangles.size()==1&&(this.Rectangles.get(0).getHeight()==this.height&&Rectangles.get(0).getWidth()==height)){
			return true;
		}
	     else{
			 return false;}
	}
	public void setPainColor(int color){
		this.pColor=color;
		
	}
	public ImageItem getImageItem(){
		return new ImageItem(Path,Rectangles,Rectangles.size()>1,isFullWidth(),Orientation,new int[]{width,height});
	}
	public Bitmap getDrawnBitmap(int position){
		if(Rectangles.size()<position){
			return null;
		}
		if(bitmap==null){
			return null;
		}
		try{
			Bitmap PreviewBitmap = Bitmap.createBitmap(bitmap.getWidth() ,bitmap.getHeight(), bitmap.getConfig());
			Canvas canvas = new Canvas(PreviewBitmap);
			canvas.drawBitmap(bitmap,0,0,null);
			Paint paint=new Paint();
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.RED);
			paint.setStrokeWidth(10);
			int startY=Rectangles.get(position).y1;
			int endY=Rectangles.get(position).y2;
			int startX=Rectangles.get(position).x1;
			int endX=Rectangles.get(position).x2;
			canvas.drawRect(startX,startY,endX,endY,paint);
			/*canvas.drawLine(0,startY,width,startY,paint);
			canvas.drawLine(0,endY,width,endY,paint);
			canvas.drawLine(startX,0,startX,height,paint);
			canvas.drawLine(endX,0,endX,height,paint);*/
			return PreviewBitmap;
		}catch(Exception e){
			return null;
		}
	}
	public void close(){
		this.bitmap.recycle();
	}
}
