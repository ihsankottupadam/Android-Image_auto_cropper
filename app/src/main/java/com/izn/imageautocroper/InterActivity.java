package com.izn.imageautocroper;

import android.os.*;
import android.view.*;
import android.content.*;
import android.support.v7.app.*;
import android.widget.*;
import android.support.design.widget.*;

public class InterActivity extends AppCompatActivity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_inter);
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);
		getSupportActionBar().setHomeButtonEnabled(false);
		
	
	}
	public void manualCrop(View v){
		startActivity(new Intent(InterActivity.this,MultyCropActivity.class));
		overridePendingTransition(R.anim.zoom_x_enter, R.anim.zoom_x_exit);
	}
	public void saveAll(View v){
		startActivity(new Intent(getApplicationContext(),SaveActivity.class));
		overridePendingTransition(R.anim.zoom_x_enter, R.anim.zoom_x_exit);
	}
}
