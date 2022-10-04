package com.izn.imageautocroper;

import android.os.*;
import java.util.*;
import android.widget.*;
import java.io.*;
import android.graphics.*;
import android.util.*;
import android.media.*;
import android.view.*;
import android.view.animation.*;
import android.widget.RelativeLayout.*;
import android.support.v7.app.*;
import java.text.*;
import android.animation.*;
import android.content.*;
import android.net.*;
import com.izn.imageautocroper.View.*;

public class SaveActivity extends AppCompatActivity
{

	ArrayList<ImageItem> Images=new ArrayList<ImageItem>();
	boolean DeleteOg;
	protected ProgressBar progressbar;
	protected TextView txtProgress;
	private TextView txtTitle;
	private LVNews lodingView;
	private long mbBefor;
	private long mbAfter;
	private TextView txtBefore;
	private TextView txtAfter;
	private RelativeLayout liChart;
	private TextView titlebefore;
	private TextView titleAfter;
	private ProgressBar progressBefore;
	private ProgressBar progressAfter;
	private boolean isCompleated;
	private TextView txtFileCount;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		init();
	}
	private void init()
	{
		progressbar=(ProgressBar)findViewById(R.id.progress);
		txtProgress=(TextView)findViewById(R.id.txtProgress);
		txtTitle=(TextView)findViewById(R.id.title);
		txtFileCount=(TextView)findViewById(R.id.txtFileCount);
		Images=SingleTon.getInstance().getFiles();
		DeleteOg=SingleTon.getInstance().isDeleteOg();
		lodingView=(LVNews)findViewById(R.id.LV);
		liChart=(RelativeLayout)findViewById(R.id.liResult);
		progressBefore=(ProgressBar)findViewById(R.id.progressBefore);
		progressAfter=(ProgressBar)findViewById(R.id.progressAfter);
		txtBefore=(TextView)findViewById(R.id.sizeBefore);
		txtAfter=(TextView)findViewById(R.id.sizeAfter);
		titlebefore=(TextView)findViewById(R.id.titleBefore);
		titleAfter=(TextView)findViewById(R.id.titleAfter);
		save();
	}
	private void save(){
		
		new AsyncTask<Void, Integer, Integer>(){
			private int fileCount;
			private Bitmap bitmap;
			private Bitmap croppedBmp;
			private File fileFolder;
			private File savepath;

			
			
			@Override
			protected void onPreExecute() {
				lodingView.setViewColor(0xAA7E0FFFF);
				lodingView.startAnim(1500);
            }
			@Override
			protected Integer doInBackground(Void... params){
				publishProgress(0,0);
				File a=new File(Images.get(0).Path);
			    fileFolder=new File(a.getParent());
				fileCount=Images.size();
				savepath=new File(fileFolder,"Croped");
				if(!savepath.exists()){
				   savepath.mkdirs();
				}
				int k=0;
				for(ImageItem item:Images){
					File currFile=new File(item.Path);
					if(item.isFullWidth&&DeleteOg){
						currFile.renameTo(new File(savepath,currFile.getName()));
						continue;
					}
					 bitmap=ImageUtil.GetOrientedBitmap(currFile);
					ArrayList<Rectangle>rects=item.Rectangles;
					if(rects.size()==0&&DeleteOg){
						currFile.renameTo(new File(savepath,currFile.getName()));
					}
					int i=0;
					for(Rectangle rect:rects){
						String det=i==0?"":String.valueOf(i);
					    croppedBmp = Bitmap.createBitmap(bitmap, rect.x1, rect.y1,rect.getWidth(), rect.getHeight());
						String fn=currFile.getName();
						File file = new File(savepath,fn.substring(0,fn.length()-4)+det+".jpg");
						try
						{
							FileOutputStream fOut= new FileOutputStream(file);
							croppedBmp.compress(Bitmap.CompressFormat.JPEG, 100,fOut);
							mbAfter+=file.length();
							if(fOut!=null){fOut.flush();fOut.close();}
						}
					
						catch (FileNotFoundException ignored){}
						catch(IOException ignored){}
						finally{croppedBmp.recycle();}
						i++;
					}
					k++;
					try
					{
						Thread.sleep(10);
					}
					catch (InterruptedException e)
					{}
					publishProgress(k*100/fileCount,k);
				}
				return 0;
			}
			@Override
			protected void onPostExecute(Integer Result){
				lodingView.stopAnim();
				txtTitle.setText("COMPLEATED");
				isCompleated=true;
				showChart();
				refreshMedia(fileFolder);
			}
			@Override
			protected void onProgressUpdate(Integer... progress) {
				setProgress(progressbar,progress[0]);
				txtProgress.setText(progress[0]+"%");
				txtFileCount.setText(progress[1]+"/"+fileCount);
			}
		}.execute();
	}
	private void showChart()
	{
		Animation scale=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_res);
		liChart.setVisibility(View.VISIBLE);
		liChart.startAnimation(scale);
		mbBefor=SingleTon.getInstance().getSizeBefore();
		Handler handler=new Handler();
		Runnable r=new Runnable(){
			public void run(){
				float pers=(float)mbAfter/(float)mbBefor*100;
				titlebefore.setVisibility(View.VISIBLE);
				titleAfter.setVisibility(View.VISIBLE);
	            setProgress(progressBefore,100);
				setProgress(progressAfter,(int)pers);
				txtBefore.setText(getFileSize(mbBefor));
				txtAfter.setText(getFileSize(mbAfter));
			}
		};
		handler.postDelayed(r,800);
	}
	public static String getFileSize(long size) {
		if (size <= 0)
			return "0";

		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	private void setProgress(ProgressBar progressBar,int progress){
		if(android.os.Build.VERSION.SDK_INT >= 11){
			ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progress); 
			//animation.setDuration(500); // 0.5 second
			animation.setInterpolator(new DecelerateInterpolator());
			animation.start();
		}
		else 
			progressBar.setProgress(progress);
	}
	public  void refreshMedia(File Dir){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			final Uri contentUri = Uri.fromFile(Dir); 
			scanIntent.setData(contentUri);
			sendBroadcast(scanIntent);
		} else {
			final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
			sendBroadcast(intent);
		}
		
	}

	@Override
	public void onBackPressed()
	{
		if(isCompleated){
			startActivity(new Intent(getApplicationContext(),MainActivity.class));
		    overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_exit);
		}
	}
	
}
