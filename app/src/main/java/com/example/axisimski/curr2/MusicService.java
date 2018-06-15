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


        try {
            String songDataLocation= intent.getStringExtra("URI");
            Uri uri=Uri.parse(songDataLocation);

            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    if(intent.getIntExtra("SEEK",0)>1) {
                        mediaPlayer.seekTo(intent.getIntExtra("SEEK",0));
                    }

                        mediaPlayer.start();
                }
            });


            } catch (IOException e) {
            e.printStackTrace();
        }



        return super.onStartCommand(intent,flags,startID);
    }//---------------------------------------------------------------------------------------------

    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
    }

    //------------------------------------------------------------



}//end class
