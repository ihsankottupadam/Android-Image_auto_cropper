package com.izn.imageautocroper;
import android.os.*;
import android.support.v7.app.*;
import android.support.v7.widget.*;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import android.content.*;
import android.view.*;
import android.net.*;
import android.widget.Toast;
import android.widget.SeekBar;
import java.io.*;
import android.database.*;
import android.provider.*;
import android.text.*;
import android.support.design.widget.*;

public class ActivityCompressFrag extends AppCompatActivity
{

	private EditText txtFolder;
	private CheckBox deleteOg;
	private String path;
	private String Fpath="";
	private SeekBar seekQuality;
	private TextView textQuality;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_compress);
		Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);
		getSupportActionBar().setTitle("");
        final TextView mTextView = (AppCompatTextView) mToolbar.findViewById(R.id.toolbar_title);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextView.setTransitionName("compress");
        }
		init();

	}

	private void init()
	{
		txtFolder=(EditText)findViewById(R.id.pathEdit);
		deleteOg=(CheckBox)findViewById(R.id.delOg);
		seekQuality=(SeekBar)findViewById(R.id.quality);
		textQuality=(TextView)findViewById(R.id.txtQuality);
		seekQuality.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
					textQuality.setText(seekBar.getProgress()+"%");
				}
				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {
				}
				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
				}
			});
		
	}
	public void choose(View v){
		this.startActivityForResult(new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
	}
	public void start(View v){
		if(TextUtils.isEmpty(Fpath)){
			Snackbar.make(v,"Folder not selected",Snackbar.LENGTH_SHORT).show();
			return;
		}
		else{
		Intent intent = new Intent(getApplicationContext(),CompressActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("path",Fpath);
		intent.putExtra("deleteOg",deleteOg.isChecked());
		intent.putExtra("quality",seekQuality.getProgress());
		startActivity(intent);
		overridePendingTransition(R.anim.zoom_x_enter,  R.anim.zoom_x_exit);
		}

	}
	public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        Cursor cursor = this.managedQuery(uri, new String[]{"_data"}, null, null, null);
        if (cursor != null) {
            int n = cursor.getColumnIndexOrThrow("_data");
            cursor.moveToFirst();
            return cursor.getString(n);
        }
        return uri.getPath();
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if(requestCode==1&&data!=null){
		    Uri selFile=data.getData();
			path=getPath(selFile);
			File file= new File (path,"");
			Fpath = file.getParent();
		    txtFolder.setText(Fpath);
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	

}
