package com.izn.imageautocroper;
import android.support.v7.app.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.content.*;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.support.v7.widget.*;
import android.support.v4.util.*;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
	private LinearLayout liCompress;
	private LinearLayout liCrop;
	private AppCompatTextView textComp;
	private AppCompatTextView textCrop;

	private boolean doubleBackToExitPressedOnce;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		textCrop=(AppCompatTextView)findViewById(R.id.txtCrop);
		textComp=(AppCompatTextView)findViewById(R.id.txtCompress);
		liCrop=(LinearLayout)findViewById(R.id.layoutCrop);
		liCompress=(LinearLayout)findViewById(R.id.layoutCompress);
		
	}
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_about:
				Intent i = new Intent(this,AboutActivity.class);
				this.startActivity(i);
				overridePendingTransition(R.anim.zoom_x_enter, R.anim.zoom_x_exit);
				return true;
			case R.id.action_exit:
				finishAffinity();
				finish();
				overridePendingTransition(R.anim.zoom_in_enter, R.anim.zoom_exit);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public void openCrop(View v){
		Intent intent = new Intent(this,ActivityCropFrag.class);
		Pair<View, String> p1 = Pair.create((View)textCrop, "crop");
		Pair<View, String> p2 = Pair.create((View)liCrop, "cropLayout");
		ActivityOptionsCompat options = ActivityOptionsCompat.
		makeSceneTransitionAnimation(this, p1,p2);
		startActivity(intent, options.toBundle());
	}
	public void openCompress(View v){
		Intent intent = new Intent(this,ActivityCompressFrag.class);
		Pair<View, String> p1 = Pair.create((View)textComp, "compress");
		Pair<View, String> p2 = Pair.create((View)liCompress, "compressLayout");
		ActivityOptionsCompat options = ActivityOptionsCompat.
		makeSceneTransitionAnimation(this, p1,p2);
		startActivity(intent, options.toBundle());
	}
	@Override
	public void onBackPressed() {
		if (doubleBackToExitPressedOnce) {
			finishAffinity();
			finish();
			return;
		}
		this.doubleBackToExitPressedOnce = true;
		Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					doubleBackToExitPressedOnce=false;                       
				}
			}, 2000);
	} 
	
}
