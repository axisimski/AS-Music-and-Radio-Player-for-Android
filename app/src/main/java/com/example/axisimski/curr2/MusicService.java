package com.example.axisimski.curr2;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.GetChars;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class MusicService extends Service {

    MediaPlayer mediaPlayer=new MediaPlayer();
    private IBinder dataBinder=new serviceBinder();

    int fuck=0;


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
    public int onStartCommand( Intent intent, int flags, int startID){

      //  new Thread(new Runnable() {
         // @Override
         //  public void run() {

               try {
                   String songDataLocation= intent.getStringExtra("URI");
                   Uri uri=Uri.parse(songDataLocation);
                   mediaPlayer.setDataSource(getApplicationContext(), uri);
                   mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);




                   if (mediaPlayer != null) {
                       mediaPlayer.prepare();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
               mediaPlayer.start();

               int x=mediaPlayer.getDuration();
               fuck=x;

       //    }
      // }).start();




        return super.onStartCommand(intent,flags,startID);
    }

    @Override
    public void onDestroy(){
        mediaPlayer.release();
    }

    //------------------------------------------------------------

    public int getMaxDuration(){

         if(mediaPlayer!=null){

            return fuck;
        }
        return 9;
    }




}//end class
