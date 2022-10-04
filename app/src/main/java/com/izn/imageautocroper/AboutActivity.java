package com.izn.imageautocroper;

import android.os.*;
import android.widget.*;
import android.view.*;
import android.widget.RelativeLayout.*;
import android.support.v7.app.*;
import com.izn.imageautocroper.View.*;

public class AboutActivity extends AppCompatActivity
{

	private TextView textDeveloper;
	private RelativeLayout Mainlayout;
	private MatrixEffectView me;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_view);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		textDeveloper = (TextView) findViewById(R.id.textview5);
		textDeveloper.setText("Ihsan Kottupadam");
		Mainlayout=(RelativeLayout)findViewById(R.id.BaseLayout);
		
		me=new MatrixEffectView(this,null);
		Handler h= new Handler();
		h.postDelayed(new Runnable(){public void run(){
			me.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			Mainlayout.addView(me);
		}},1500);
		
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_exit);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	public void onBackPressed()
	{
		overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_exit);
		// TODO: Implement this method
		super.onBackPressed();
		overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_exit);
	}
	
}
