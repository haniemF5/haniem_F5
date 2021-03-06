package com.example.ourblackbox2;

import java.io.IOException;
import java.util.List;

import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BServiceRecorder
{
	
	private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private static Camera mServiceCamera;
	
	protected MediaRecorder bRecorder;
	protected BIOstream biostream;
	protected static String Path;
	protected static String Folder;
	protected static String Name;
	//
	protected boolean isRecording;//현재녹화중인지나타내는것
	protected boolean isVideotimerRunning;//비디오 타이머가 작동중인지 아닌지

	
	
	
	public BServiceRecorder(SurfaceView sv)
	{
		mSurfaceView=sv;
		mSurfaceHolder=sv.getHolder();

		isRecording=false;
		isVideotimerRunning=false;
		Path="";
		Folder="";
		
		bRecorder= new MediaRecorder();
		biostream=new BIOstream();
		mServiceCamera = Camera.open();
	}
	
	
	// 동영상 촬영 관련  메소드
	public void initRecorder()
	{
				
		   Camera.Parameters params = mServiceCamera.getParameters();
           mServiceCamera.setParameters(params);
           Camera.Parameters p = mServiceCamera.getParameters();

           final List<Size> listSize = p.getSupportedPreviewSizes();
           Size mPreviewSize = listSize.get(2);
           
           p.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
           p.setPreviewFormat(PixelFormat.YCbCr_420_SP);
           mServiceCamera.setParameters(p);

           try {
            /**/   mServiceCamera.setPreviewDisplay(mSurfaceHolder);
               mServiceCamera.startPreview();
           }
           catch (IOException e) {
              
               e.printStackTrace();
           }	
		
           Folder=biostream.createInternalFolder();
           Name=biostream.createName(System.currentTimeMillis());
           Path=Folder+"/"+ Name;
		
		if(bRecorder==null){
			bRecorder= new MediaRecorder();
		}else{
			bRecorder.reset();
		}
		
		
		mServiceCamera.unlock();
		bRecorder.setCamera(mServiceCamera);
		
		bRecorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		bRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		
		bRecorder.setProfile(CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_FRONT, CamcorderProfile.QUALITY_LOW));
		
		bRecorder.setMaxDuration(60000);//최대캡쳐시간 60초
		
		bRecorder.setOutputFile(Path);
		bRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
		
	}
	
	
	
	public void startRecorder()
	{	
		
		try{	
			bRecorder.prepare();
			bRecorder.start();
			Log.v("is playing?","really?="+20000);
		}catch(IllegalStateException e){
			e.printStackTrace();
			return;
		}catch(IOException e){
			e.printStackTrace();
			return;
		}catch(Exception e){
			e.printStackTrace();
			return;
		}	
		isRecording=true;
		isVideotimerRunning=true;

	}
	
	public void resetRecorder(){
	
		bRecorder.stop();
		bRecorder.reset();
		isRecording=false;
	}

	public void stopRecorder()
	{	
		bRecorder.stop();
		bRecorder.reset();
		isVideotimerRunning=false;
		bRecorder=null;
		isRecording=false;
		mServiceCamera.lock();//카메라객체 잠금
		
	}
	
	public void destroyRecorder()
	{
		
		if(bRecorder !=null)
		{
			bRecorder.release();
			bRecorder=null;
		}
		if(mServiceCamera!=null)
		{
		mServiceCamera.release();
		mServiceCamera=null;
		}
		Log.v("서비스레코더","카메라파괴");
		
		
		
	}
}
