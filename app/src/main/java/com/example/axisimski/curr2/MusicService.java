package com.example.axisimski.curr2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.GetChars;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicService extends Service {

    private static MediaPlayer mediaPlayer=new MediaPlayer();
    private IBinder dataBinder=new serviceBinder();
    protected int loc=0; //Variable for current location of seek bar
    protected int tlc=1; //Variable for next song.
    protected String currentSong="";

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

        final ArrayList<String> songList=new ArrayList<String>();
        final ArrayList<String> songTitleList= new ArrayList<String>();

        SharedPreferences sharedPreferences=getApplicationContext().
                getSharedPreferences("spList", Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String jsonURL=sharedPreferences.getString("jsonURL","");
        String jsonName=sharedPreferences.getString("jsonName", "");

        if(!jsonURL.equals("")) {
            List tempName = gson.fromJson(jsonName, new TypeToken<List<String>>() {
            }.getType());
            songTitleList.clear();
            songTitleList.addAll((List) tempName);
            List tempURL = gson.fromJson(jsonURL, new TypeToken<List<String>>() {
            }.getType());
            songList.clear();
            songList.addAll((List) tempURL);
         }

        try {
            final String songDataLocation= sharedPreferences.getString("URI", "");;

            tlc=0;
            Uri uri=Uri.parse(songDataLocation);

            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepare();
            currentSong=songTitleList.get(songList.indexOf(songDataLocation)) ;

            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

//                    if(intent.getIntExtra("SEEK",0)>1) {
                       // mediaPlayer.seekTo(intent.getIntExtra("SEEK",0));
  //                  }
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

    @Override
    public void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
    }

    //MediaPlayer Functions
    public void pause(){
        mediaPlayer.pause();
    }
    public void seekTo(int progress){
        mediaPlayer.seekTo(progress);
    }
    public int getDuration(){
        return mediaPlayer.getDuration();
    }
    public void start(){
        mediaPlayer.start();
    }
    public boolean isPlaying(){
            return mediaPlayer.isPlaying();
    }
    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    //====================================================================================================


}//end class
