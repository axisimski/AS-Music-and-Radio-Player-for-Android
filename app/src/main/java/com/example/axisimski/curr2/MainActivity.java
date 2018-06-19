package com.example.axisimski.curr2;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private ListView listView; //listView to display song names
    private List <String> list; //list storing song location strings
    private List <String> titlelist; //list storing song titles
    private ListView listView2; //This populates the adapter on Search //see menu function
    private ListAdapter adapter; //Adapter for listView
    private Button play_button, shuffle_button, next_button; //Play/Pause, Shuffle, Next buttons
    private SeekBar seekBar; //Seek bar
    private TextView songName_tv; //Display the song name while playing
    private boolean firstPlay=true; //Has a song been played yet? relevant for what the play button does.
    private boolean firstUse=true;
    private int indexLastSong=0; //Keeps track of the last song which was played (default val=1)
    private MusicService MusicService;
    private Intent intent;
    private PlayMusic play= new PlayMusic();
    private ServiceConnection serviceConnection=getServiceConnection();
    private boolean bound; //Is the Service currently bound
    //==============================================================================================end Declarations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Temp
        Button button=(Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openRadio=new Intent(MainActivity.this, RadioActivity.class);
                startActivity(openRadio);
            }
        });
//=================================================================#######################################
        //Initialize variables
        intent= new Intent(MainActivity.this,MusicService.class);
        play_button=findViewById(R.id.play_button);
        shuffle_button=findViewById(R.id.shuffle_btn);
        next_button=findViewById(R.id.next_button);
        seekBar=findViewById(R.id.seekBar);
        listView=findViewById(R.id.listView);
        listView2=listView;
        list=new ArrayList<>();
        titlelist=new ArrayList<>();
        songName_tv=findViewById(R.id.songName_tv);

        //------------------------------------------------------------------------------------------end var Declaration
        if (ContextCompat.checkSelfPermission(MainActivity.this,
         Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        else{
            GetMusic getMusic = new GetMusic();
            getMusic.getMusic(list, titlelist, getApplicationContext());
            adapter=new ArrayAdapter<>(this, R.layout.cust_list, titlelist);
            listView.setAdapter(adapter);
        }//-----------------------------------------------------------------------------------------end CheckPermissions

        loadSettings();//load whether the app has been used before and the last song play
        //First time after installing the app files are not visible, must be restarted
        if(firstUse){
            Toast.makeText(getApplicationContext(),"Scanning complete...Restart app to view music files", Toast.LENGTH_LONG).show();
        }
        saveSettings(indexLastSong);
        userInput();

    }
    //==================================================================================================end onCreate();

    //Keeps the onClickListeners for the UI elements
    public void userInput(){

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(firstPlay){
                    play.playMusic(list.get(indexLastSong), titlelist, list,intent,getApplicationContext(),
                            serviceConnection);
                    updateValues(indexLastSong);
                    setSeekBar();
                    saveSettings(indexLastSong);
                }
                else {
                    if(MusicService.mediaPlayer.isPlaying()) {
                        MusicService.mediaPlayer.pause();
                        play_button.setText("▶");
                    }
                    else {
                        MusicService.mediaPlayer.start();
                        setSeekBar();
                        play_button.setText("⌷⌷");
                    }
                }

            }
        });//---------------------------------------------------------------------------------------

        shuffle_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random=new Random();
                int songIndex=random.nextInt(list.size()-1);
                play.playMusic(list.get(songIndex), titlelist, list,intent,getApplicationContext(),
                        serviceConnection);
                updateValues(songIndex);
                setSeekBar();
                saveSettings(songIndex);
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onClick(View v) {
                if(!firstPlay){
                    String song=list.get(indexLastSong);
                    updateValues(indexLastSong);
                    setSeekBar();
                    MusicService.playNext((ArrayList)list, (ArrayList)titlelist, song);
                    saveSettings(indexLastSong);  //######################################################################
                }
            }
        });//---------------------------------------------------------------------------------------

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(!firstPlay){
                    if(fromUser){
                        MusicService.mediaPlayer.seekTo(progress);
                    }
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){}
        });//---------------------------------------------------------------------------------------end SeekBar()

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
                play.playMusic(list.get(i), titlelist, list,intent,getApplicationContext(), serviceConnection);
                updateValues(i);
                setSeekBar();
                saveSettings(i);
            }
        });//---------------------------------------------------------------------------------------end LVOCL
    }

    //Update last song, set song name text box. (ONlY call on play Music)
    public void updateValues(int i){
        indexLastSong=list.indexOf(list.get(i));
        songName_tv.setText(titlelist.get(i));
        play_button.setText("⌷⌷");
        firstPlay=false;
    }//=============================================================================================end updateValues();

    //set SeekBar by polling MediaService (Also sets title textView)
    public void setSeekBar(){
        //Delay execution so service could properly start up!
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                songName_tv.setText(MusicService.currentSong);
                seekBar.setMax(MusicService.mediaPlayer.getDuration());
                setSeekBar();
                MusicService.loc=MusicService.mediaPlayer.getCurrentPosition();
                seekBar.setProgress(MusicService.loc);
            }
        }, 500);
    }
    //==============================================================================================end setSeekBar();

    //Create drop down search menu (Clickable)
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem searchItem=menu.findItem(R.id.item_search);
        final SearchView searchView= (SearchView) searchItem.getActionView();

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
                adapter=new ArrayAdapter<>(MainActivity.this, R.layout.cust_list, templist);
                listView2.setAdapter(adapter);

                listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {

                        play.playMusic(templist2.get(i), titlelist, list,intent,getApplicationContext(),
                                serviceConnection);
                        updateValues( list.indexOf(templist2.get(i)));
                        saveSettings(list.indexOf(templist2.get(i)));
                        setSeekBar();
                        searchView.clearFocus();
                        searchView.onActionViewCollapsed();
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
    }
    //==============================================================================================end SearchMenu();

    //get connection to music Service
    public ServiceConnection getServiceConnection() {

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
        return serviceConnection;
    }
    //==============================================================================================end ServiceConnection();

    //Settings
    public void saveSettings(int i){
        SharedPreferences sp= getApplicationContext().getSharedPreferences("Setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        indexLastSong=i;
        firstUse=false;
        editor.putInt("indexLastSong",indexLastSong);
        editor.putBoolean("firstPlay", firstUse);
        editor.apply();
    }
    //==============================================================================================end ServiceConnection();

    public void loadSettings(){
        SharedPreferences sp= getApplicationContext().getSharedPreferences("Setting", Context.MODE_PRIVATE);
        if(sp!=null){
            indexLastSong=sp.getInt("indexLastSong",0);
            firstUse=sp.getBoolean("firstPlay", true);
        }
    }
    //==============================================================================================end ServiceConnection();
}//End class();
