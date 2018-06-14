package com.example.axisimski.curr2;

import android.Manifest;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MPR=1;

    ListView listView;
    List <String> list;
    List <String> titlelist;
    ListAdapter adapter;
    MediaPlayer mediaPlayer;
    Button play_button;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         //------------------------------------------------------------------------------------------
        if (ContextCompat.checkSelfPermission(MainActivity.this,
         Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MPR);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MPR);
            }
        } else  {
            doStuff(); //Function call();
        }
        //------------------------------------------------------------------------------------------

        play_button=findViewById(R.id.play_button);
        seekBar=findViewById(R.id.seekBar);


        //------------------------------------------------------------------------------------------
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  startService(new Intent(MainActivity.this, MusicService.class));

            }
        });


    }//=======end of OnCreate()

//=================================================================================================================
    //Get mp3 file names/locations  (Puts all data in a string and inserts it into list and titlelist
    public void getMusic(){

        ContentResolver contentResolver=getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor=  contentResolver.query(songUri, null, null, null, null);

        if(songCursor!=null&&songCursor.moveToFirst()){

            int songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songLocation=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                //Get the location and title of all songs from storage and put them in two different lists.
                //list holds the path (for playback) and titlelist holds the title (to be displayed).
                //Indexes on both lists are the same.
                String currentTitle=songCursor.getString(songTitle);
                String currentLocation=songCursor.getString(songLocation);
                list.add(currentLocation);
                titlelist.add(currentTitle);

            }while(songCursor.moveToNext());
        }
    }

//===================================================================================================================end getMusic()
    //Permissions
    public void onRequestPermissionResult(int requestCode, String[]permissions, int[]grantResults){

        switch (requestCode){
            case MPR:{
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(this,"PMG!", Toast.LENGTH_SHORT).show();
                        doStuff();
                    }else {
                        Toast.makeText(this, "NPMG!",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    return;

                }
            }
        }

    }

//===================================================================================================================end ORPR();
    //On click sends song uri to new activity and opens said activity
    public void doStuff(){

        listView=findViewById(R.id.listView);
        list=new ArrayList<>();
        titlelist=new ArrayList<>();

        getMusic();

        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titlelist);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                Intent intent=new Intent(MainActivity.this, Service.class);
                intent.putExtra("URI",list.get(i));
                startService(intent);

                startService(new Intent(MainActivity.this, MusicService.class));

                Toast.makeText(MainActivity.this,"Dostuffhapp", Toast.LENGTH_SHORT).show();

            }
        });




    }
//===================================================================================================================end doStuff();










}//End class();
