package com.izn.imageautocroper;

import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.v4.view.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.theartofdev.edmodo.cropper.*;
import java.io.*;
import java.util.*;
import android.widget.RelativeLayout.*;
import javax.xml.xpath.*;
import android.graphics.drawable.*;
import android.support.v7.app.*;
import android.support.design.widget.*;
import com.izn.imageautocroper.View.*;



public class MultyCropActivity extends AppCompatActivity
{

	private RelativeLayout baseView;
	
	
	static ArrayList<ImageItem> Images=new ArrayList<ImageItem>();
	int currPos=0;
	private boolean showaTutorial=true;

	private CropImageView cropView;

	private ViewPager viewPager;

	

	private MyGlideAdapter myGlideAdapter;

	private ImageItem currImage;
	private ArrayList<Rectangle> currRectanglesBackup=new ArrayList<Rectangle>();
	private LinearLayout cropLayout;
	private LinearLayout menuView;
    Context mContext;
	private ImageView closeDot;

	private QuickAction quickAction;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_multycrop);
		init();
	}
	private void init(){
		baseView=(RelativeLayout)findViewById(R.id.BaseLayout);
		cropLayout=(LinearLayout)findViewById(R.id.CropLayout);
		cropView=new CropImageView(this);
		mContext=this;
		menuView=(LinearLayout)findViewById(R.id.liBottomMenus);
		setMenu(R.layout.menu_multycrop);
		viewPager= (ViewPager)findViewById(R.id.pager);
		//cropView.setImageUriAsync(uri);
		cropView.setId(R.id.cropImageView);
		cropView.setBackgroundColor(0x00000000);
		cropView.setAutoZoomEnabled(false);
	    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(-1, -1);
		cropLayout.setVisibility(View.INVISIBLE);
		cropLayout.addView(cropView, params);
		Images=SingleTon.getInstance().getFiles();
		myGlideAdapter=new MyGlideAdapter(MultyCropActivity.this,Images);
		viewPager.setAdapter(myGlideAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
		closeDot=(ImageView)findViewById(R.id.close_dot);
		closeDot.setOnClickListener(new OnClickListener(){
				public void onClick(View v){
					if((int)closeDot.getTag()!=-1){
					currImage.Rectangles.remove((int)closeDot.getTag());
					currImage.invalidate();
					Images.set(currPos,currImage);
					closeDot.setTag(-1);
					closeDot.setVisibility(View.INVISIBLE);
					Redraw();
					}
				}
			});
		currPos=SingleTon.getInstance().getLastPos();
		if(Images.size()<=currPos){
		currPos=0;
		}
        setCurrentItem(currPos);
		update(currPos);
		if(showaTutorial){
			RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			lps.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
			lps.setMargins(margin, margin*2, margin, margin);
			

		}
	}
	public void onMenuClick(View v){
		switch(v.getId()){
			case R.id.ACTION_CROP:
				Rectangle rect=currImage.Rectangles.get(0);
				Uri uri=Uri.fromFile(new File(currImage.Path));
				if(cropView.getImageUri()!=uri){
					cropView.setImageUriAsync(uri);
				}
				cropView.setTag(0);
				cropView.setCropRect(rect.toRect());
				cropLayout.setVisibility(View.VISIBLE);
				setMenu(R.layout.menu_crop);
				break;
			case R.id.ACTION_TAKEALL:
				if(!currImage.isFullWidth){
					currImage.Rectangles.clear();
					currImage.Rectangles.add(new Rectangle(0,0,currImage.Matrix[0],currImage.Matrix[1]));
				    currImage.isFullWidth=true;
				    Images.set(currPos,currImage);
					Redraw();
				}
				break;
			case R.id.ACTION_DONE:
				SingleTon.getInstance().setFiles(Images);
				SingleTon.getInstance().setLastPos(currPos);
				startActivity(new Intent(getApplicationContext(),InterActivity.class));
				overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_exit);
				break;
			case R.id.ACTION_DELETE:
				File file=new File(currImage.Path);
				myGlideAdapter.removeItem(currPos);
				if(file.delete()){
					showMessage("Deleted");
				}
				//else{
					//showMessage("File not deleted");
				//}
				
				break;
			case R.id.ACTION_CROP_DONE:
				Rect rec=cropView.getCropRect();
				int index=(int)cropView.getTag();
				Rectangle newRect=new Rectangle(rec.left,rec.top,rec.right,rec.bottom);
				if(index>=0){
				currImage.Rectangles.set(index,newRect);
				}
				else if(index==-4){currImage.Rectangles.add(newRect);}
				ArrayList<Rectangle> Rectangles=currImage.Rectangles;
				for(int i=0;i<Rectangles.size();i++){
				if(i==index){continue;}
				  if( newRect.contains(Rectangles.get(i))){
					  currImage.Rectangles.remove(i);
				  }
				}
				currImage.invalidate();
				Images.set(currPos,currImage);
				cropLayout.setVisibility(View.INVISIBLE);
				setMenu(R.layout.menu_multycrop);
				closeDot.setTag(-1);
			    closeDot.setVisibility(View.INVISIBLE);
				Redraw();
			
				break;			case R.id.ACTION_CANCEL:
				cropLayout.setVisibility(View.INVISIBLE);
				setMenu(R.layout.menu_multycrop);
			    break;
			default:break;
		}
	}
	private void setMenu(int layout){
    menuView.removeAllViewsInLayout();
	//LayoutInflater mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	View menu=new View(this);
	menu.inflate(mContext,layout,menuView);
	//menuView.addView(menu);
	}
	private void showMessage(Object p0)
	{
		Toast.makeText(MultyCropActivity.this,String.valueOf(p0),Toast.LENGTH_SHORT).show();
	}
	
	final GestureDetector pagerListene = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
			int x;
			int y;
			ImageView iv;
			Matrix matrix;
			Matrix imageMetrix;
			@Override
			public boolean onDown(MotionEvent event)
			{
				closeDot.setVisibility(View.GONE);
				
				matrix = new Matrix();
				View pagerView = viewPager.findViewWithTag("pagerView" + viewPager.getCurrentItem());
				iv=(ImageView)pagerView.findViewById(R.id.image_preview);
				imageMetrix=iv.getImageMatrix();
				imageMetrix.invert(matrix);
				float[] touchPoint = new float[] {event.getX(),event.getY()};
				matrix.mapPoints(touchPoint);

				x = Integer.valueOf((int)touchPoint[0]);
				y = Integer.valueOf((int)touchPoint[1]);
				float scale=currImage.Matrix[0]/currImage.getBitmap().getWidth();
				x*=scale;y*=scale;
				return super.onDown(event);
			}

		
			@Override
			public void onLongPress(MotionEvent e)
			{
				int index=getIndexofRectangle(x,y);

				if(index>=0){
					Rectangle rect=currImage.Rectangles.get(index);
					if(rect!=null){
						Uri uri=Uri.fromFile(new File(currImage.Path));
						//if(cropView.getImageUri()!=uri){
						cropView.setImageUriAsync(uri);
						//}
						cropView.setCropRect(rect.toRect());
						cropView.setTag(index);
						cropLayout.setVisibility(View.VISIBLE);
						setMenu(R.layout.menu_crop);
					}
				}
				
			}

		
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
			{
				
				return super.onScroll(e1, e2, distanceX, distanceY);
			}
			@Override
			public boolean onDoubleTap(MotionEvent e)
			{
				
				final int index=getIndexofRectangle(x,y);
				if(index<0){
				
				int[] ImgPosition=getBitmapPositionInsideImageView(iv);

				final float tX=e.getX();final float tY=e.getY();
				
				ActionItem ActionFind 	= new ActionItem(0, "FIND RECTANGLE", getResources().getDrawable(R.drawable.ic_search_100));
				ActionItem ActionCrop 	= new ActionItem(1, "CROP", getResources().getDrawable(R.drawable.ic_crop_100));
				//ActionCrop.setSticky(true);
				//ActionFind.setSticky(true);
				quickAction = new QuickAction(mContext, QuickAction.VERTICAL);
				quickAction.addActionItem(ActionFind);
				quickAction.addActionItem(ActionCrop);
				quickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {			
						@Override
						public void onItemClick(QuickAction source, int pos, int actionId) {				
							ActionItem actionItem = quickAction.getActionItem(pos);

							//here we can filter which action item was clicked with pos or actionId parameter
							if (actionId == 0) {
								
								RectangleDetector detector=new RectangleDetector(mContext,currImage.Path);
								ArrayList<Rectangle> rects=detector.detectRectangles();
								boolean isDetected=false;
						
								for(Rectangle rect:rects){
									if(rect.isPointInRectangle(x,y)){
										currImage.Rectangles.add(rect);
										currImage.invalidate();
										Images.set(currPos,currImage);
										isDetected=true;
										return;
									}
								}
								if(!isDetected){
									showMessage("No content found");
								}
								
							} else if (actionId == 1) {
								Uri uri=Uri.fromFile(new File(currImage.Path));
								cropView.setImageUriAsync(uri);
								cropView.setCropRect(new Rect(x-30,y-30,x+30,y+30));
								cropView.setTag(-4);
								cropLayout.setVisibility(View.VISIBLE);
								setMenu(R.layout.menu_crop);
							}
						}
					});
				
				
				if((tX>ImgPosition[0]&&tX<ImgPosition[2])&&(tY>ImgPosition[1]&&tY<ImgPosition[3])){
				quickAction.show(baseView,(int)e.getX(),(int)e.getY());
				}
				}
				return true;
			}

			@Override
			public boolean onSingleTapConfirmed(MotionEvent e)
			{
				final int index=getIndexofRectangle(x,y);
				if(index>=0){
					if (currImage.isMulty){
						matrix.invert(imageMetrix);
						Rectangle curr=currImage.Rectangles.get(index);
						float[] centerPoint = new float[] {curr.x1+(curr.x2-curr.x1)/2, curr.y1+(curr.y2-curr.y1)/2};
						imageMetrix.mapPoints(centerPoint);
						x = Integer.valueOf((int)centerPoint[0]);
						y = Integer.valueOf((int)centerPoint[1]);
						closeDot.setX(x-dp2px(12));
						closeDot.setY(y-dp2px(12));
						closeDot.setVisibility(View.VISIBLE);
						closeDot.setTag(index);
					}
				}
				else{
					int indeX=getIndexofBackupRect(x,y);
					if(indeX>=0){
						Rectangle rec=currRectanglesBackup.get(indeX);
						if(!currImage.Rectangles.contains(rec)){
							currImage.Rectangles.add(rec);
							currImage.invalidate();
							Images.set(currPos,currImage);
							Redraw();
						}
					}
					
				}
				return true;
			}
			
			
		});

	public boolean onTouchEvent(MotionEvent event) {
		return pagerListene.onTouchEvent(event);
	};
	
	public static int[] getBitmapPositionInsideImageView(ImageView imageView) {
		int[] ret = new int[4];

		if (imageView == null || imageView.getDrawable() == null)
			return ret;

		// Get image dimensions
		// Get image matrix values and place them in an array
		float[] f = new float[9];
		imageView.getImageMatrix().getValues(f);

		// Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
		final float scaleX = f[Matrix.MSCALE_X];
		final float scaleY = f[Matrix.MSCALE_Y];

		// Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
		final Drawable d = imageView.getDrawable();
		final int origW = d.getIntrinsicWidth();
		final int origH = d.getIntrinsicHeight();
		// Calculate the actual dimensions
		final int actW = Math.round(origW * scaleX);
		final int actH = Math.round(origH * scaleY);

		// Get image position
		// We assume that the image is centered into ImageView
		int imgViewW = imageView.getWidth();
		int imgViewH = imageView.getHeight();

		int top = (int) (imgViewH - actH)/2;
		int left = (int) (imgViewW - actW)/2;

		ret[0] = left;
		ret[1] = top;
		ret[2]=left+actW;
		ret[3]=top+actH;
		
		return ret;
	}
	private OnTouchListener pagerlistener= new View.OnTouchListener(){
		@Override
		public boolean onTouch(View view, MotionEvent event)
		{
		 pagerListene.onTouchEvent(event );
		 return true;
		}
		
	};
    private void setCurrentItem(int position) {
        viewPager.setCurrentItem(position, false);
       
    }
	public void Redraw(){
		View pagerView = viewPager.findViewWithTag("pagerView" + viewPager.getCurrentItem());
		ImageView img=(ImageView)pagerView.findViewById(R.id.image_preview);
		Bitmap bmp=RectangleDetector.drawRectangles(currImage.getBitmap(),Images.get(currPos).Rectangles,currImage.Matrix,Color.RED);
		img.setImageBitmap(bmp);
	}
	private void update(int position)
	{
		if(closeDot.getVisibility()==View.VISIBLE){closeDot.setVisibility(View.INVISIBLE);closeDot.setTag(-1);}
		
		currPos = position;
		currImage = Images.get(position);
		currRectanglesBackup.clear();
		for(Rectangle rect : Images.get(position).Rectangles) {
			currRectanglesBackup.add(rect.clone());
		}
		//showMessage("IsMulty : " + currImage.isMulty + "   IsFullWidth : " + currImage.isFullWidth);
	}
	
	private int getIndexofRectangle(int x,int y){
		
		ArrayList<Rectangle>rects= currImage.Rectangles;
		for(int i=0;i<rects.size();i++){
			Rectangle rect=rects.get(i);
			if(rect.isPointInRectangle(x,y)){
				return i ;
			}
		}
		return -1;

	}
	private int getIndexofBackupRect(int x,int y){
		
		for(int i=0;i<currRectanglesBackup.size();i++){
			Rectangle rect=currRectanglesBackup.get(i);
			if(rect.isPointInRectangle(x,y)){
				return i ;
			}
		}
		return -1;

	}
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
		   update(position);
        }

		

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
	class MyGlideAdapter extends PagerAdapter {

		Context mContext;
		LayoutInflater mLayoutInflater;
		
		private ArrayList<ImageItem> Images;

		public MyGlideAdapter(Context context,ArrayList<ImageItem> images) {
			mContext = context;
			this.Images=images;
			mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return Images.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View itemView = mLayoutInflater.inflate(R.layout.imagefullscreen, container, false);

			ImageView imageView = (ImageView) itemView.findViewById(R.id.image_preview);
			
			
			Bitmap bitmap=ImageUtil.decodeSampledBitmap(Images.get(position).Path,240,506);
			Images.get(position).setBitmap(bitmap);
			Bitmap drawn=RectangleDetector.drawRectangles(bitmap,Images.get(position).Rectangles,Images.get(position).Matrix,Color.RED);
			//Glide.with(MultyCropActivity.this).load(Images.get(position).Path).into(imageView);
			imageView.setImageBitmap(drawn);
			imageView.setOnTouchListener(pagerlistener);
			container.addView(itemView);
			itemView.setTag("pagerView"+position);
			return itemView;
		}
		@Override
		public int getItemPosition(Object object){
			return PagerAdapter.POSITION_NONE;
		}
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		public void removeItem(int position) {
			Images.remove(position);
			
			notifyDataSetChanged();
			if(Images.size()==0){finish();}
		}

       /* public int addView(View view){
            return addView(view, views.size());
        }


        public int addView(View view, int position){
            views.add(position, view);
			return position;
        }

        public int removeView(ViewPager pager, View view){
            return removeView(pager, views.indexOf(view));
        }

        public int removeView(ViewPager pager, int position){
            pager.setAdapter(null);
            views.remove(position);
            pager.setAdapter(this);
            return position;
        }

        public View getView(int position){
            return views.get(position);
        }*/

   
		/*public void removeItem(int position){
			
			Images.remove(position);
			notifyDataSetChanged();
			if(viewPager.getChildCount()!=position){
				viewPager.setCurrentItem(position,true);
			}
			else if(position!=0){viewPager.setCurrentItem(position-1);}
			else{finish();}
		}*/
	}
	
	private int dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return(int)( dp * scale +0.5);
    }
	@Override
	public void onBackPressed()
	{
		if(cropLayout.getVisibility()==View.VISIBLE){
			cropLayout.setVisibility(View.INVISIBLE);
			setMenu(R.layout.menu_multycrop);
			return ;
		}
		SingleTon.getInstance().setLastPos(currPos);
		startActivity(new Intent(getApplicationContext(),InterActivity.class));
		overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_exit);
	}
	
	
	
}
