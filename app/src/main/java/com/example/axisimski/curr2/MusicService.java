package com.example.axisimski.curr2;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.widget.Toast;

import java.io.IOException;

public class MusicService extends Service {

    MediaPlayer mediaPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //====================================================================================================
    @Override
    public void onCreate(){
        super.onCreate();
    }

    //====================================================================================================
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){

       mediaPlayer=new MediaPlayer();
       // Uri uri=Uri.parse(intent.getStringExtra("URI"));

        String data=(String) intent.getExtras().getString("URI");

        Toast.makeText(MusicService.this,"OK", Toast.LENGTH_SHORT).show();

       /*  try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            if (mediaPlayer != null) {
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        return super.onStartCommand(intent,flags,startID);
    }

    @Override
    public void onDestroy(){
        mediaPlayer.release();
    }


}
