package com.example.axisimski.curr2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;

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

        //Load and save lists in Shared Preferences

            SharedPreferences sharedPreferences=context.
                    getSharedPreferences("spList", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor=sharedPreferences.edit();
            Gson gson=new Gson();
            String jsonURL =gson.toJson(list);
            String jsonName=gson.toJson(titlelist);
            editor.putString("jsonURL", jsonURL);
            editor.putString("jsonName", jsonName);
            editor.putString("URI", link);
            editor.apply();



        context.startService(intent);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);


    }//=============================================================================================end playMusic

}//play Music()
