package com.izn.imageautocroper;

import java.util.*;

public class SingleTon {
	ArrayList<ImageItem> Files=new ArrayList<ImageItem>();
	boolean isDelete;
	boolean isImagesVerified;
	long SizeBefore;
	int LastPos;
	private static final SingleTon ourInstance = new SingleTon();
	public static SingleTon getInstance() {
		return ourInstance;
	}
	private SingleTon() { }
	public void setFiles(ArrayList<ImageItem> files) {
		this.Files = files;
	}
	
	public ArrayList<ImageItem> getFiles() {
		return Files;
	}
	public void setDeleteOg(boolean delete){
		this.isDelete=delete;
	}
	public boolean isDeleteOg(){
		return this.isDelete;
	}
	public void setVerified(boolean value){
		this.isImagesVerified=value;
	}
	public boolean isImagesVerified(){
		return this.isImagesVerified;
	}
	public void setLastPos(int position){
		this.LastPos=position;
	}
	public int getLastPos(){
		return this.LastPos;
	}
	public void setSizeBefore(long size){
		this.SizeBefore=size;
	}
	public long getSizeBefore(){
		return this.SizeBefore;
	}
	public void reset(){
		this.isDelete=false;
		this.SizeBefore=0;
		this.Files.clear();
		this.LastPos=0;
	}
}
