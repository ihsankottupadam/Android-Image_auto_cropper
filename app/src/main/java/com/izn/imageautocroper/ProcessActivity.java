package com.izn.imageautocroper;

import android.os.*;
import android.widget.*;
import android.view.animation.*;
import java.util.*;
import android.graphics.*;
import android.media.*;
import android.view.*;
import java.io.*;
import android.content.*;
import android.util.*;
import android.support.v7.app.*;
import android.animation.*;
import android.support.v4.app.*;

public class ProcessActivity extends AppCompatActivity
{

	private ImageView imagePrev;

	
	private ArrayList<ImageItem> MultyItems=new ArrayList<ImageItem>();
	private ArrayList<ImageItem> cropItems=new ArrayList<ImageItem>();
	public static ArrayList<ImageItem> AllImages=new ArrayList<ImageItem>();
	private int fileCount;

	
	
	protected ProgressBar progressBar;
	
	private String path;

	private int z;

	public static ArrayList<ImageItem> files;

	protected TextView txtProgress;

	protected TextView txtFileCount;
	private boolean isFinished;
	protected TextView txtFileName;

	private Context mContext;

	private Handler handlerSlide;

	private Runnable runnableSlide;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process);
		path = getIntent().getExtras().getString("path", "");
		
		init();
		Handler handler=new Handler(getMainLooper());
		handler.postDelayed(new Runnable(){public void run(){
			startPr();}},10);
	}

	private void init()
	{
		
		mContext=this;
		progressBar=(ProgressBar)findViewById(R.id.progress);
		imagePrev=(ImageView)findViewById(R.id.image_preview);
		txtProgress=(TextView)findViewById(R.id.txtProgress);
		txtFileCount=(TextView)findViewById(R.id.txtFileCount);
		txtFileName=(TextView)findViewById(R.id.txtFileName);
	}
	private void startSlideAnim(){
		final Animation slideRep=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_repate);
		final Animation scanning=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.scanner);
		final Animation animscanbox=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.boc_scan);
		final ImageView scanbox=(ImageView)findViewById(R.id.scanBox);
		if(fileCount==0){ return;}
		
		
		final ImageView Iscanner=(ImageView)findViewById(R.id.scanner);
		if(z-1<1){Handler h=new Handler(getMainLooper());
		Runnable r=new Runnable(){public void run(){startSlideAnim();}};
		h.postDelayed(r,100);return;}
		//final Animation slStart=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_start);
		//final Animation slEnd=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.slide_end);
		Handler h1=new Handler(getMainLooper());
		h1.postDelayed(new Runnable(){
				public void run(){
					
				 Iscanner.setVisibility(View.VISIBLE);
				}}
			,1500);
		handlerSlide=new Handler();
		runnableSlide=new Runnable(){
			public void run(){
				runOnUiThread(new Runnable(){public void run(){
							final int currIt=z-1;
							final Bitmap bitmap=ImageUtil.decodeSampledBitmap(files.get(currIt).Path,90,150);
							imagePrev.setVisibility(View.VISIBLE);
							imagePrev.setImageBitmap(bitmap);
							imagePrev.startAnimation(slideRep);
							Iscanner.startAnimation(scanning);
							Handler h1=new Handler(getMainLooper());
						    h1.postDelayed(new Runnable(){
								public void run(){
									scanbox.setVisibility(View.VISIBLE);
										scanbox.startAnimation(animscanbox);
										scanbox.setVisibility(View.INVISIBLE);
									    Bitmap a=RectangleDetector.drawRectangles(bitmap,files.get(currIt).Rectangles,files.get(currIt).Matrix,0xFf5BF8F9);
										imagePrev.setImageBitmap(a);
										}}
										,1500);
							
							if(!isFinished){
								handlerSlide.postDelayed(runnableSlide,2600);
							}
				}});
			}
		};
		
		handlerSlide.postDelayed(runnableSlide,10);
	}
	private void startPr(){
	AsyncTaskRunner runner = new AsyncTaskRunner();
	runner.execute(path);
	
		}
	private class AsyncTaskRunner extends AsyncTask<String, String, String>
	{

		private long sizeBefore;
		
		@Override
		protected String doInBackground(String... params) {
			try {
				String fn=new File(files.get(0).Path).getName();
				z=0;
				publishProgress("0","0",fn);
				for(ImageItem curItem:files){
					String cpath = curItem.Path;
					RectangleDetector detector=new RectangleDetector(ProcessActivity.this,cpath);
					detector.detectRectangles();
					ImageItem item=detector.getImageItem();
					detector.close();
					if(item.isMulty){
						MultyItems.add(item);
					}
					else{
						cropItems.add(item);
					}
					files.set(z,item);
					z++;
					//if (isCancelled()) break;
				    String filename=new File(cpath).getName();
					int progress=z*100/fileCount;
				    Thread.sleep(10);
					publishProgress(String.valueOf(progress),String.valueOf(z),filename);
					
				}
				
			
			} catch (Exception e) {
				Log.e("Errr ",e.toString());
				
			}
			return null;
		}
		private ArrayList<ImageItem> listImageFiles(File dir){
			File[] filelist = dir.listFiles();
			int i=0;
			ArrayList<ImageItem> imgfiles=new ArrayList<ImageItem>();
			for (File file:filelist){
			  if(isImageFormat(file.getName())){
				String fPath=file.toString();
				File cfile=new File(fPath);
				sizeBefore+=cfile.length();
				ImageItem fl=new ImageItem();
				fl.Path=cfile.toString();
				imgfiles.add(fl);
				i++;
			  }
			}
			SingleTon.getInstance().setSizeBefore(sizeBefore);
			return imgfiles;
		}
		private boolean isImageFormat(String name){
			name=name.toLowerCase();
			return (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png"));
		}
		@Override
		protected void onPostExecute(String result) {
			isFinished=true;
			txtFileName.setText("COMPLEATED");
			AllImages.clear();
			AllImages.addAll(MultyItems);
			AllImages.addAll(cropItems);
			SingleTon.getInstance().setFiles(AllImages);
			new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						openInter();
					}
				}, 2000);
		}
		@Override
		protected void onPreExecute() {
			
			File dir = new File(path);
			files = listImageFiles(dir);
			fileCount=files.size();
			startSlideAnim();
			
		}
		@Override
		protected void onProgressUpdate(String... pars) {
			setProgress(progressBar,Integer.parseInt(pars[0]));
			txtProgress.setText(pars[0]+"%");
			txtFileCount.setText(pars[1]+"/"+fileCount);
			txtFileName.setText(pars[2]);
		}
	}

	@Override
	public void onBackPressed()
	{
		if(isFinished){
			super.onBackPressed();
		}
	}
	
	@Override
	protected void onDestroy()
	{
		
		super.onDestroy();
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
	private void openInter(){
		Intent intent = new Intent(this,MultyCropActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.zoom_x_enter, R.anim.zoom_x_exit);
	}
}
