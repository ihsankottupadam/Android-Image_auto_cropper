package com.izn.imageautocroper;

import java.util.*;
import android.graphics.*;
public class ImageItem
{
	public String Path;
	public ArrayList<Rectangle> Rectangles;
	public boolean isMulty=false;
	public boolean isFullWidth=false;
	public int orientation;
	public int[] Matrix;
	
	private Bitmap bitmap;
	public ImageItem(){
		
	}
	public ImageItem(String path,ArrayList<Rectangle>Rectangles,boolean isMulty,boolean isFullWidth,int orientation,int[] matrix){
	this.Path=path;
	this.Rectangles=Rectangles;
	this.isMulty=isMulty;
	this.isFullWidth=isFullWidth;
	this.orientation=orientation;
	this.Matrix=matrix;
	}
	public void invalidate(){
		if(Rectangles.size()>1){
			isMulty=true;
		}
		else{
			isMulty=false;
		}
		isFullWidth=funisFullWidth();
	}
	
	private boolean funisFullWidth(){
		if(this.Rectangles.size()==1&&(this.Rectangles.get(0).getHeight()==bitmap.getHeight()&&Rectangles.get(0).getWidth()==bitmap.getWidth())){
			return true;
		}
		else{
			return false;}
	}
	public void setBitmap(Bitmap bitmap){
		this.bitmap=bitmap;
	}
	public Bitmap getBitmap(){
		return bitmap;
	}
}
