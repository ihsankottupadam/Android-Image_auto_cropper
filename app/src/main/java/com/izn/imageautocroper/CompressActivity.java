package com.izn.imageautocroper;

import android.os.*;
import android.widget.*;
import android.content.*;
import java.io.*;
import java.util.*;
import android.support.v7.app.*;
import java.text.*;
import android.animation.*;
import android.view.animation.*;
import android.view.*;
import android.net.*;
import com.izn.imageautocroper.View.*;

public class CompressActivity extends AppCompatActivity
{
	
	private Context mContext;
	private String path;
	private boolean DeleteOg;
	private int quality;
	private boolean isFinished;
	private long skipSize;
	private long mbBefor;
	private long mbAfter;
	
	private ProgressBar progressbar;
	private TextView txtProgress;
	private TextView txtTitle;
	private LVNews lodingView;
	private RelativeLayout liChart;
	private ProgressBar progressBefore;
	private ProgressBar progressAfter;
	private TextView txtBefore;
	private TextView txtAfter;
	private TextView titlebefore;
	private TextView titleAfter;

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
		Bundle extras=getIntent().getExtras();
		progressbar=(ProgressBar)findViewById(R.id.progress);
		txtProgress=(TextView)findViewById(R.id.txtProgress);
		txtTitle=(TextView)findViewById(R.id.title);
		lodingView=(LVNews)findViewById(R.id.LV);
		txtFileCount=(TextView)findViewById(R.id.txtFileCount);
		liChart=(RelativeLayout)findViewById(R.id.liResult);
		progressBefore=(ProgressBar)findViewById(R.id.progressBefore);
		progressAfter=(ProgressBar)findViewById(R.id.progressAfter);
		txtBefore=(TextView)findViewById(R.id.sizeBefore);
		txtAfter=(TextView)findViewById(R.id.sizeAfter);
		titlebefore=(TextView)findViewById(R.id.titleBefore);
		titleAfter=(TextView)findViewById(R.id.titleAfter);
		DeleteOg=extras.getBoolean("deleteOg",false);
		path=extras.getString("path","");
		quality=extras.getInt("quality",80);
		mContext=this;
		txtTitle.setText("COMPRESSSING..");
		compress();
	}

	private void compress()
	{
		new AsyncTask<Void, Integer, Integer>(){

			private int fileCount;
			private File FileFolder;

			private File savepath;
			@Override
			protected void onPreExecute() {
				lodingView.setViewColor(0xAA7E0FFFF);
				lodingView.startAnim(1500);
            }
			@Override
			protected Integer doInBackground(Void... params){
				publishProgress(0,0);
				FileFolder=new File(path);
				savepath=new File(FileFolder,"Compressed");
				if(!savepath.exists()){
					savepath.mkdirs();
				}
				String StrSavePath=savepath.toString();
				ArrayList<File> filelist=getImageFiles(FileFolder);
				fileCount=filelist.size();
				int k=0;
				boolean renamed=false;
				Compressor compressor=new Compressor(mContext);
				compressor.setDestinationDirectoryPath(StrSavePath);
				compressor.setQuality(quality);
				for(File file:filelist){
					if(file.length()>skipSize){
					try
					{
						File compressed=compressor.compressToFile(file);
						mbAfter+=compressed.length();
					}
					catch (IOException ignored){}
					}
					else{
						mbAfter+=file.length();
						file.renameTo(new File(savepath+"/"+file.getName()));
						renamed=true;
					}
					try{Thread.sleep(1);}
					catch (InterruptedException ignored){}
					if(DeleteOg&&!renamed){
						file.delete();
					}
					k++;
					publishProgress(k*100/fileCount,k);
				}
				return 0;
			}

			@Override
			protected void onPostExecute(Integer Result){
				lodingView.stopAnim();
				txtTitle.setText("COMPLEATED");
				showChart();
				isFinished=true;
				refreshMedia(FileFolder);
			}
			@Override
			protected void onProgressUpdate(Integer... progress) {
				setProgress(progressbar,progress[0]);
				txtProgress.setText(progress[0]+"%");
				txtFileCount.setText(progress[1]+"/"+fileCount);
			}
		}.execute();
	}

	@Override
	public void onBackPressed()
	{
		if(isFinished){
		    startActivity(new Intent(getApplicationContext(),MainActivity.class));
			overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_exit);
		}
	}
	private ArrayList<File> getImageFiles(File path){
		File[] fileList=path.listFiles();
		ArrayList<File> files=new ArrayList<File>();
		for(File file: fileList){
			String name=file.getName().toLowerCase();
			if(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")){
				mbBefor+=file.length();
				files.add(file);
			}
		}
		return files;
	}
	private void showChart()
	{
		Animation scale=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scale_res);
		liChart.setVisibility(View.VISIBLE);
		liChart.startAnimation(scale);
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
		if (size <= 0){
			return "0";
			}
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	private void setProgress(ProgressBar progressBar,int progress){
		if(Build.VERSION.SDK_INT >= 11){
			ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progress); 
			animation.setInterpolator(new LinearInterpolator());
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
}
