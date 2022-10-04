package com.izn.imageautocroper;
import android.graphics.*;
import android.view.animation.*;

public class Rectangle
{
	public int x1;
	public int x2;
	public int y1;
	public int y2;
	
	public Rectangle(int x1,int y1,int x2,int y2){
		this.x1=x1;
		this.y1=y1;
		this.x2=x2;
		this.y2=y2;
	}
	public Rectangle(Rectangle other) {
        this.x1 = other.x1;
        this.x2 = other.x2;
		this.y1=other.y1;
		this.y2=other.y2;
    }
	@Override
	public Rectangle clone() {
        Rectangle newRect = new Rectangle(this.x1, this.y1,this.x2,this.y2);
        return newRect;
    }
	@Override
	public String toString()
	{
	   return String.format("Rectangle"+"( %s,%s) (%s,%s)",x1,y1,x2,y2);
	}

	public int getWidth(){
		return x2-x1;
	}
	public int getHeight(){
		return y2-y1;
	}
	public int area(){
		return getWidth()*getHeight();
	}
	public void reset(){
		this.reset();
	}
	public boolean isPointInRectangle(int x, int y){
		if(x<=x1||x>=x2){
			return false;
		}
		if(y<=y1||y>=y2){
			return false;
		}
		return true;
	}
   public boolean contains(Rectangle rectangle){
	   if(isPointInRectangle(rectangle.x1,rectangle.y1)&&
	   isPointInRectangle(rectangle.x2,rectangle.y2)){
		   return true;
	   }
	   return false;
   }
   public Rect toRect(){
	return new Rect(x1,y1,x2,y2);
    }
	public RectF toRectF(){
		return new RectF(x1,y1,x2,y2);
    }
	
}
