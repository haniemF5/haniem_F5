package com.example.ourblackbox2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GalleryActivity extends Activity implements OnItemClickListener{
	
	Cursor cursor;

	//MediaStore.Video.Media 로 되어있는 값들을 다 BIOstream에서 만든 함수나 변수들로 바꿔주면 되지않을까...
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀 없애기
//	    requestWindowFeature(Window.FEATURE_NO_TITLE);// 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);//
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//

		setContentView(R.layout.gallery);//
	    // TODO Auto-generated method stub
		
		ListView listView = (ListView) this.findViewById(R.id.ListView);
		
		String[] thumbColumns = {
				MediaStore.Video.Thumbnails.DATA,
				MediaStore.Video.Thumbnails.VIDEO_ID
		};
		
		String[] mediaColumns = {
				MediaStore.Video.Media._ID,
				MediaStore.Video.Media.DATA,
				MediaStore.Video.Media.TITLE,
				MediaStore.Video.Media.MIME_TYPE
		};
		
		cursor = managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);
		
		ArrayList<VideoViewInfo> videoRows = new ArrayList<VideoViewInfo>();
		
		if(cursor.moveToFirst())
		{
			do{
				VideoViewInfo newVVI = new VideoViewInfo();
				//Outer.Inner oi = ot.new Inner();


				
				int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media._ID));
				
				Cursor thumbCursor = managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=" + id, null, null);
				
				if(thumbCursor.moveToFirst())
				{
					newVVI.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
					Log.v("VideoGallery", "Thumb" + newVVI.thumbPath);
				}
				
				newVVI.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));//////////////////////////////////////
				newVVI.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
				Log.v("VideoGallery", "Title" + newVVI.title);
				newVVI.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
				Log.v("VideoGallery", "Mime" + newVVI.mimeType);
				
				videoRows.add(newVVI);
			}while (cursor.moveToNext());
		}
		
		listView.setAdapter(new VideoGalleryAdapter(this, videoRows));
		listView.setOnItemClickListener(this);
	}
	
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		if(cursor.moveToPosition(position)){
			int fileColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			int mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE);
			
			String VideoFilePath = cursor.getString(fileColumn);
			String mimeTye = cursor.getString(mimeColumn);
			
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW);
			
			File newFile = new File(VideoFilePath);
			intent.setDataAndType(Uri.fromFile(newFile), mimeTye);
			
			startActivity(intent);
		}
	}
	
	 class VideoViewInfo {
			
			String filePath;
			String mimeType;
			String thumbPath;
			String title;

		}

	
	/*class VideoViewInfo {
		String filePath;
		String mimeType;
		String thumbPath;
		String title;
	}
	
	class VideoGalleryAdapter extends BaseAdapter{
		private Context context;
		private List<VideoViewInfo> videoItems;
		
		LayoutInflater inflater;
		
		public VideoGalleryAdapter(Context _context, ArrayList<VideoViewInfo> _items) {
			context = _context;
			videoItems = _items;
			
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		public int getCount(){
			return videoItems.size();
		
	    }
		
		public Object getItem(int position){
			return videoItems.get(position);
		}
		public long getItemId(int position){
			return position;
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			View videoRow = inflater.inflate(R.layout.list_item, null);
			
			ImageView videoThumb = (ImageView) videoRow.findViewById(R.id.ImageView);
			if(videoItems.get(position).thumbPath != null) {
				videoThumb.setImageURI(Uri.parse(videoItems.get(position).thumbPath));
			}
			TextView videoTitle = (TextView) videoRow.findViewById(R.id.TextView);
			videoTitle.setText(videoItems.get(position).title);
			
			return videoRow;
		}
	}*/
}
