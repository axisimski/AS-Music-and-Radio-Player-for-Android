package com.example.axisimski.curr2;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;

public class PlayBackActivity extends AppCompatActivity {

    Handler handler;
    Runnable runnable;
    SeekBar seekBar;
    Button playButton, pauseButton;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_back);

        handler=new Handler();
        seekBar=findViewById(R.id.seekBar);
        playButton=findViewById(R.id.playButton);
        pauseButton=findViewById(R.id.pauseButton);

        //Prep Media Player, including URI info from intent
        mediaPlayer=new MediaPlayer();
        Uri uri=Uri.parse(getIntent().getStringExtra("URI"));

        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            if (mediaPlayer != null) {
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (mediaPlayer != null) {
            mediaPlayer.start();
        }

        seekBar.setMax(mediaPlayer.getDuration());


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mediaPlayer.getDuration());
                playCycle();
                mediaPlayer.start();
            }
        });


        //Play Button temp onClickListener();
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    handler.removeCallbacks(runnable);
                }

                else mediaPlayer.start();*/

              startService(new Intent(PlayBackActivity.this, MusicService.class));

            }
        });


        //++++++++++++++++++++++++++++++++++++++++++++++

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    handler.removeCallbacks(runnable);
                }

                else mediaPlayer.start();*/

                stopService(new Intent(PlayBackActivity.this, MusicService.class));

            }
        });


    }
    //=============================================================================================//end of OnCreate();



    protected void onResume(){
        super.onResume();
        mediaPlayer.start();
    }
    /*protected void onPause(){
        super.onPause();
        mediaPlayer.stop();
    }
    protected void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
        handler.removeCallbacks(runnable);
    }*/


    public void playCycle(){
        seekBar.setProgress(mediaPlayer.getCurrentPosition());

        if(mediaPlayer.isPlaying()){
            runnable=new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable, 10);
        }

    }



}//end class
