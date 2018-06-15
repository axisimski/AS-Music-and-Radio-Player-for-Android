package com.example.axisimski.curr2;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    ListView listView; //listView to display song names
    List <String> list; //list storing song location strings
    List <String> titlelist; //list storing song titles
    ListAdapter adapter;
    Button play_button; //Play/Pause
    SeekBar seekBar; //Seekbar (hopefully it will end up working).
    boolean isPlaying=false; //is the song currently playing (for pause, changeing songs etc)
    String lastSong=""; //string location of last song been played.

    private MusicService MusicService;
    private boolean bound; //Is the Service currently bound
    private ServiceConnection serviceConnection;
    private Intent intent;




    ListView listView2; //This populates the adapter on Search //see menu function

    //temporary varriables until sharedPref is implemented
    int seek=1;
    Runnable runnable;
    Handler handlerSB;


    //==============================================================================================Begin onCreate()
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize variables
        intent= new Intent(MainActivity.this,MusicService.class);
        play_button=findViewById(R.id.play_button);
        seekBar=findViewById(R.id.seekBar);
        listView=findViewById(R.id.listView);
        listView2=findViewById(R.id.listView); //hook up both here?
        //------------------------------------------------------------------------------------------end var Declaration
        if (ContextCompat.checkSelfPermission(MainActivity.this,
         Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MPR);
        }
        else{
            populateList(); //Function call to populate ListView;
        }//-----------------------------------------------------------------------------------------end CheckPermissions


        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isPlaying) {
                    unbindService();
                    stopService(intent);
                    isPlaying=false;
                }

                else {
                    playMusic(lastSong);
                    setSeekBar();
                    isPlaying=true;
                }
            }
        });//---------------------------------------------------------------------------------------


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser){
                    unbindService();
                    stopService(intent);

                    intent.putExtra("URI",lastSong);
                    intent.putExtra("SEEK", progress);
                    startService(intent);
                    bindService();
                    isPlaying=true;

                    Toast.makeText(MainActivity.this,"OKSOFAR", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });//---------------------------------------------------------------------------------------end SeekBar()



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                playMusic(list.get(i));
                setSeekBar();
            }
        });//---------------------------------------------------------------------------------------end LVOCL


     }//==================================================================================================end onCreate();


    public void playMusic(String link){

        lastSong=link;
        //stop previous service so two songs don't play at the same time
        unbindService();
        stopService(intent);

        //Start new service and pass song location trough intent
        intent.putExtra("URI",link);
        startService(intent);
        bindService();
        isPlaying=true;



    }//=============================================================================================end playMusic


    public void setSeekBar(){

        //Delay execution so service could properly start up!
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                seek=MusicService.getMaxDuration();

                seekBar.setMax(seek);


            }
        }, 20);

    }//=============================================================================================end setSeekBar()





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
    }//==================================================================================================end getMusic();

    //On click sends song uri to new activity and opens said activity
    public void populateList(){

        list=new ArrayList<>();
        titlelist=new ArrayList<>();

        getMusic();

        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titlelist);
        listView.setAdapter(adapter);

    }//==================================================================================================end populateList();

    private void bindService(){
        if(serviceConnection==null){
            serviceConnection=new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    MusicService.serviceBinder myServiceBinder=(MusicService.serviceBinder)iBinder;
                    MusicService=myServiceBinder.getService();
                    bound=true;
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    bound=false;
                }
            };
        }
           bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }//==================================================================================================//end bindService();

    private void unbindService(){
        if(bound){
            unbindService(serviceConnection);
            bound=false;
        }
    }//==================================================================================================//end unbindService();

    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater =getMenuInflater();

        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem=menu.findItem(R.id.item_search);
        SearchView searchView= (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextChange(String newText) {

                ArrayList<String> templist=new ArrayList<>();
                final ArrayList<String> templist2=new ArrayList<>();

                for(String temp:titlelist){

                    if(temp.toLowerCase().contains(newText.toLowerCase())){

                      String s=  list.get(titlelist.indexOf(temp));

                        templist2.add(s);
                        templist.add(temp);
                    }
                }
                adapter=new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, templist);
                listView2.setAdapter(adapter);

                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {


                        lastSong=templist2.get(i);

                        //stop previous service so two songs don't play at the same time

                        unbindService();
                        stopService(intent);
                        intent.removeExtra("URI");

                        //Start new service and pass song location trough intent
                        intent.putExtra("URI",templist2.get(i));
                        startService(intent);
                        bindService();
                        isPlaying=true;

                        //Delay execution so service could properly start up!
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                seek=MusicService.getMaxDuration();

                                seekBar.setMax(seek);


                            }
                        }, 250);

                    }
                });//---------------------------------------------------------------------------------------end LVOCL


                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }//==================================================================================================end SearchMenu();



}//End class();
