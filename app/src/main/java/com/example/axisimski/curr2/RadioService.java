package com.example.axisimski.curr2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;

import static android.media.AudioManager.STREAM_MUSIC;

public class RadioService extends Service {


    protected MediaPlayer mediaPlayer= new MediaPlayer();
    private IBinder sBinder=new serviceBinder();

    @Override
    public int onStartCommand(final Intent intent, int flags, int startID){

        String URL=intent.getStringExtra("link");

        try{
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(URL);
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setAudioStreamType(STREAM_MUSIC);

            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mediaPlayer.start();
                }
            });
        }finally {

        }


        return super.onStartCommand(intent, flags, startID);

    }




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sBinder;
    }

    public class  serviceBinder extends Binder{
        public RadioService getService(){
            return RadioService.this;
        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }




}
