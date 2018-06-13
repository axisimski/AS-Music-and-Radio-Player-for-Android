package com.example.axisimski.curr2;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;

import java.io.IOException;

public class PlayBackActivity extends AppCompatActivity {

    Handler handler;
    Runnable runnable;
    SeekBar seekBar;
    Button playButton;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_back);

        handler=new Handler();
        seekBar=findViewById(R.id.seekBar);
        playButton=findViewById(R.id.playButton);


        mediaPlayer=new MediaPlayer();
        Uri uri=Uri.parse(getIntent().getStringExtra("URI"));

        try {
            mediaPlayer.setDataSource(getApplicationContext(), uri);

            if (mediaPlayer != null) {
                mediaPlayer.prepare();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }


    }
    //end of OnCreate();
}
