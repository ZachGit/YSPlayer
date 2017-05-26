package com.bsi.dms.video;

import java.io.IOException;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class AudioPlayer implements OnBufferingUpdateListener, 
OnCompletionListener, MediaPlayer.OnPreparedListener{ 
public MediaPlayer mediaPlayer; 
private boolean loop = false;
//private SeekBar skbProgress; 
//private Timer mTimer=new Timer(); 

public AudioPlayer() 
{ 
//this.skbProgress=skbProgress; 
 
try { 
    mediaPlayer = new MediaPlayer(); 
    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); 
    mediaPlayer.setOnBufferingUpdateListener(this); 
    mediaPlayer.setOnPreparedListener(this); 
    mediaPlayer.setOnCompletionListener(this);
} catch (Exception e) { 
    Log.e("mediaPlayer", "error", e); 
} 
 
} 

/******************************************************* 
* 通过定时器和Handler来更新进度条 
******************************************************/  
//***************************************************** 

public void play() 
{ 
mediaPlayer.start(); 
} 

public void playUrl(String videoUrl) 
{ 
try { 
	loop = false;
    mediaPlayer.reset(); 
    mediaPlayer.setDataSource(videoUrl); 
    mediaPlayer.prepare();//prepare之后自动播放 
    //mediaPlayer.start(); 
} catch (IllegalArgumentException e) { 
    // TODO Auto-generated catch block 
    e.printStackTrace(); 
} catch (IllegalStateException e) { 
    // TODO Auto-generated catch block 
    e.printStackTrace(); 
} catch (IOException e) { 
    // TODO Auto-generated catch block 
    e.printStackTrace(); 
} 
} 

public void playLoopUrl(String videoUrl) 
{ 
	try { 
		loop = true;
		mediaPlayer.reset(); 
		mediaPlayer.setDataSource(videoUrl); 
		mediaPlayer.prepare();//prepare之后自动播放 
		//mediaPlayer.start(); 		
	} catch (IllegalArgumentException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
	} catch (IllegalStateException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
	} catch (IOException e) { 
		// TODO Auto-generated catch block 
		e.printStackTrace(); 
	} 
} 



public void pause() 
{ 
mediaPlayer.pause(); 
} 

public void stop() 
{ 
if (mediaPlayer != null) {  
    mediaPlayer.stop(); 
    mediaPlayer.release();  
    mediaPlayer = null;  
}  
} 

@Override 
/** 
* 通过onPrepared播放 
*/ 
public void onPrepared(MediaPlayer arg0) { 
arg0.start(); 
Log.e("mediaPlayer", "onPrepared"); 
} 

@Override 
public void onCompletion(MediaPlayer arg0) { 
	Log.e("mediaPlayer", "audio onCompletion--------- "); 
	if(loop ){
		mediaPlayer.start();		
	}
} 

@Override 
public void onBufferingUpdate(MediaPlayer arg0, int bufferingProgress) { 
// Log.e(currentProgress+"% play", bufferingProgress + "% buffer"); 
} 

} 
