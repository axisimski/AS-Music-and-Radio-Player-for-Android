package com.example.axisimski.curr2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlayMusic {

    //Start and bind music Service;
    public void playMusic(String link, List<String> titlelist, List<String> list,
                          Intent intent, Context context, ServiceConnection serviceConnection
    ){
         //Put whole list so MusicService can play next...
        intent.putStringArrayListExtra("songList",(ArrayList<String>)list);
        intent.putStringArrayListExtra("songTitleList",(ArrayList<String>)titlelist);

        intent.putExtra("URI",link);
        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


    }//=============================================================================================end playMusic

}//play Music()
