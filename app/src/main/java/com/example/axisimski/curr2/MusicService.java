package com.example.axisimski.curr2;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.GetChars;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MusicService extends Service {

    MediaPlayer mediaPlayer=new MediaPlayer();
    private IBinder dataBinder=new serviceBinder();

    int seekMax=100;

    class serviceBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }

    //====================================================================================================
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return dataBinder;
    }

    //====================================================================================================
    @Override
    public void onCreate(){
        super.onCreate();

    }

    //====================================================================================================
    @Override
    public int onStartCommand(final Intent intent, int flags, int startID){

        new Thread(new Runnable() {
          @Override
           public void run() {
            int bar=0;

               try {

                   bar=intent.getIntExtra("SEEK",0);
                   String songDataLocation= intent.getStringExtra("URI");
                   Uri uri=Uri.parse(songDataLocation);
                   mediaPlayer.reset();
                   mediaPlayer.setDataSource(getApplicationContext(), uri);
                   mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);




                   if (mediaPlayer != null) {
                       mediaPlayer.prepare();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
             //  mediaPlayer.start();




               //Stuff bellow probably needs to be changed
               if(bar!=0) {
                  mediaPlayer.seekTo(bar);
               }

              mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                  @Override
                  public void onPrepared(MediaPlayer mp) {
                      seekMax=mediaPlayer.getDuration();
                      mediaPlayer.start();
                  }
              });


           }
       }).start();




        return super.onStartCommand(intent,flags,startID);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
    }

    //------------------------------------------------------------

    public int getMaxDuration(){

        if(mediaPlayer!=null){
            return seekMax;
        }

        return 0;
    }


    public int getCurrentPosition(){

        if(mediaPlayer!=null){
            return mediaPlayer.getCurrentPosition();
        }

        return 0;
    }


    public void onPause()
    {
        mediaPlayer.pause();
    }



}//end class
