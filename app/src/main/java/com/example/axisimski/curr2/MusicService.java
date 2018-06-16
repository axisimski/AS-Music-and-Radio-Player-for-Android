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
    int loc=0; //Variable for current location of seek bar
    int tlc=1; //Variable for next song.
    String currentSong="dddd";

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
        final ArrayList<String> songTitleList = intent.getStringArrayListExtra("songTitleList");

        try {
            final String songDataLocation= intent.getStringExtra("URI");

            tlc=0;
            Uri uri=Uri.parse(songDataLocation);

            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            currentSong=songTitleList.get(songList.indexOf(songDataLocation));

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
                    playNext(songList, songTitleList,songDataLocation);
                }
            });



        } catch (IOException e) {
            e.printStackTrace();
        }


        return super.onStartCommand(intent,flags,startID);
    }
    //====================================================================================================

    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
    }
    //====================================================================================================

    public void playNext(ArrayList<String> songList, ArrayList<String>songTitleList, String songDataLocation){

        tlc++;
        int songIndex=songList.indexOf(songDataLocation)+tlc;

        if(songIndex<songList.size()) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(songList.get(songIndex));
                currentSong=songTitleList.get(songIndex);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.prepare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    //====================================================================================================


}//end class
