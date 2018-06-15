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
import java.util.ArrayList;

public class MusicService extends Service {

    MediaPlayer mediaPlayer=new MediaPlayer();
    private IBinder dataBinder=new serviceBinder();
    int loc=0;
    int tlc=1;



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


        final ArrayList<String> songList = intent.getStringArrayListExtra("songList");

        try {
            final String songDataLocation= intent.getStringExtra("URI");
            tlc=0;
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


            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {

             /*       int songIndex=songList.indexOf(songDataLocation)+tlc;
                    tlc++;

                    mediaPlayer.reset();
                    try {
                       mediaPlayer.setDataSource(songList.get(songIndex));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    try {
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/


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

    //If mediaplayer is playing seekbar will be updated every half a second.
    public void seekBarUpdater(){
        Handler handler=new Handler();

        if(mediaPlayer.isPlaying()){
            Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    seekBarUpdater();
                    loc=mediaPlayer.getCurrentPosition();

                    String temp=Integer.toString(loc);
                    MainActivity.seekBar.setProgress(loc);
                }
            };
            handler.postDelayed(runnable, 500);
        }

    }//end seekBarUpdate()



    public void playNext(ArrayList<String> songList){

        for(int i=0;i<songList.size();i++){


        }

    }

}//end class
