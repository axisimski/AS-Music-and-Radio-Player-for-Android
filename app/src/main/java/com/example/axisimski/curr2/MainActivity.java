package com.example.axisimski.curr2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MPR=1;

    ListView listView;
    List <String> list;
    ListAdapter adapter;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      if(ContextCompat.checkSelfPermission(MainActivity.this,
              Manifest.permission.READ_EXTERNAL_STORAGE)
          !=PackageManager.PERMISSION_GRANTED){

          if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                  Manifest.permission.READ_EXTERNAL_STORAGE)){
              ActivityCompat.requestPermissions(MainActivity.this,
                      new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MPR);
          }

          else{
              ActivityCompat.requestPermissions(MainActivity.this,
                      new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},MPR);
          }

      }else{doStuff();}
    }

//=================================================================================================================

    public void getMusic(){

        ContentResolver contentResolver=getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor=  contentResolver.query(songUri, null, null, null, null);

        if(songCursor!=null&&songCursor.moveToFirst()){

            int songTitle=songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songLocation=songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do{
                String currentTitle=songCursor.getString(songTitle);
                String currentLocation=songCursor.getString(songLocation);
                list.add(currentLocation);

            }while(songCursor.moveToNext());
        }
    }

//===================================================================================================================end getMusic()

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
    public void doStuff(){

        listView=findViewById(R.id.listView);
        list=new ArrayList<>();

        getMusic();

        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                if(mediaPlayer!=null){
                    mediaPlayer.release();
                }

             //   int resID=getResources().getIdentifier(list.get(i), "raw",getPackageName());
               // mediaPlayer=MediaPlayer.create(this, );


                try {
                    mediaPlayer.setDataSource(list.get(i));
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();


            }
        });



    }
//===================================================================================================================end doStuff();










}
