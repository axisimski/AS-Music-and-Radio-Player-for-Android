package com.example.axisimski.curr2;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RadioActivity extends AppCompatActivity {

    private Button play_button;  //Buttons for playing/pause, adding a radio station
    private ServiceConnection serviceConnection=getServiceConnection();
    private MusicService MusicService=new MusicService();
    private boolean isPlaying=false; //Is music playing? Play/Pause
    private ListView listView; //UI Radio Station list
    private List<String> list; //list containing Radio Station URLs
    private List <String> titlelist; //list containing Radio Station Titles
    private ListAdapter adapter; //ListView adapter
    private Intent intent; //Intent
    private PlayMusic play= new PlayMusic(); //Player
    private boolean bound=false;
    private int indexLastStation=0;
    private TextView station_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        //Initialize Variables
        play_button=findViewById(R.id.button_play);
        listView=findViewById(R.id.listView);
        station_tv=findViewById(R.id.stationName_tv);
        list=new ArrayList<>();
        titlelist=new ArrayList<>();
        intent=new Intent(RadioActivity.this, MusicService.class);

        //loadList();
        adapter=new ArrayAdapter<>(this, R.layout.cust_list, titlelist);
        listView.setAdapter(adapter);

        //Load list of radio stations from shared prefs and enable UI
         loadList();

         //Info passed on from MainActivity()
         //Determines what goes in the text box etc..
        if(MusicService.isPlaying()) {
            SharedPreferences sp=getApplicationContext().getSharedPreferences("Settings", Context.MODE_PRIVATE);
            station_tv.setText(sp.getString("TitleLastPlayed", ""));
            if(!titlelist.contains(sp.getString("TitleLastPlayed", ""))) {
                play_button.setText("⌷⌷");
            }

            else{play_button.setText("■");}
        }

         userInput();
    }
    //==============================================================================================end onCreate()
    //Keeps the onClickListeners for the UI elements
    public void userInput(){

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(list.get(0)!=null) {

                    String x=Boolean.toString(isPlaying);
                    Toast.makeText(getApplicationContext(), x+"FFF", Toast.LENGTH_SHORT).show();
                  //  MusicService.mediaPlayer.stop();

                    if (!isPlaying) {
                        play.playMusic(list.get(indexLastStation), titlelist, list,intent,getApplicationContext(),
                                serviceConnection);
                        updateValues(indexLastStation);
                        isPlaying = true;
                    }
                    else {
                        MusicService.pause();
                        isPlaying = false;
                        play_button.setText("▶");
                    }
                }
            }
        });


        //On item click play.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                play.playMusic(list.get(position), titlelist, list,intent,getApplicationContext(),
                        serviceConnection);
                isPlaying=true;
                updateValues(position);
                saveList();
            }
        });

        //Remove item from list (Delete Radio Station on long hold)
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                list.remove(pos);
                titlelist.remove(pos);
                ((BaseAdapter)adapter).notifyDataSetChanged();

                //Save list when items are removed
                saveList();

                return true;
            }
        });

    }
    //==============================================================================================end userInput()

    //Load and save lists in Shared Preferences
    public void saveList(){
        SharedPreferences sharedPreferences=getApplicationContext().
                getSharedPreferences("LIST", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        Gson gson=new Gson();
        String jsonURL =gson.toJson(list);
        String jsonName=gson.toJson(titlelist);
        editor.putString("jsonURL", jsonURL);
        editor.putString("jsonName", jsonName);
        editor.putInt("indexLastSong",indexLastStation);
        editor.apply();
    }

    @SuppressWarnings("unchecked")
    public void loadList(){
        SharedPreferences sharedPreferences=getApplicationContext().
                getSharedPreferences("LIST", Context.MODE_PRIVATE);
        Gson gson=new Gson();
        String jsonURL=sharedPreferences.getString("jsonURL","");
        String jsonName=sharedPreferences.getString("jsonName", "");
        indexLastStation=sharedPreferences.getInt("indexLastSong",0);

        if(!jsonURL.equals("")) {
            List tempName = gson.fromJson(jsonName, new TypeToken<List<String>>() {
            }.getType());
            titlelist.clear();
            titlelist.addAll((List) tempName);
            List tempURL = gson.fromJson(jsonURL, new TypeToken<List<String>>() {
            }.getType());
            list.clear();
            list.addAll((List) tempURL);
            ((BaseAdapter) adapter).notifyDataSetChanged();
        }
    }
    //==============================================================================================end userInput()

    //Establish Service Connection
    public ServiceConnection getServiceConnection() {

        if(serviceConnection==null){
            serviceConnection=new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder iBinder) {
                    MusicService.serviceBinder myServiceBinder=(MusicService.serviceBinder)iBinder;
                    MusicService=myServiceBinder.getService();
                    bound=true;
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    bound=false;
                }
            };

        }
        return serviceConnection;
    }//=============================================================================================end getServiceConnection

    //Read in the URL and name of custom Radio Stations by using two Alert Dialog boxes.
    //Add them to their respective lists.
    public void addStation(){

        AlertDialog.Builder addURL= new AlertDialog.Builder(RadioActivity.this);

        addURL.setTitle("Enter URL Here:");
        final EditText edt=new EditText(RadioActivity.this);
        addURL.setView(edt);

        addURL.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                list.add(edt.getText().toString());
                saveList();
            }
        });
        addURL.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog URLAlertDialog=addURL.create();
        URLAlertDialog.show();

        //------------------------------------------------------------------------------------------//end adding URL
        AlertDialog.Builder addName= new AlertDialog.Builder(RadioActivity.this);

        addName.setTitle("Enter Station Name:");
        final EditText edtName=new EditText(RadioActivity.this);
        addName.setView(edtName);

        addName.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                titlelist.add(edtName.getText().toString());
            }
        });
        addName.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog NameAlertDialog=addName.create();
        NameAlertDialog.show();

    }
    //==============================================================================================end addStation()
    //Update last song, set song name text box. (ONlY call on play Music)
    public void updateValues(int i){
        indexLastStation=list.indexOf(list.get(i));
        play_button.setText("⌷⌷");
        station_tv.setText(titlelist.get(i));

    }//=============================================================================================end updateValues();

    //Populate Action Bar
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.menu_radio, menu);

        //Button for Radio Activity
        MenuItem radioItem= menu.findItem(R.id.item_music);
        radioItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent radioIntent=new Intent(RadioActivity.this, MainActivity.class);
                startActivity(radioIntent);
                return false;
            }
        });

        //Add station
        MenuItem addItem= menu.findItem(R.id.item_add);
        addItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                addStation();
                ((BaseAdapter)adapter).notifyDataSetChanged();
                saveList();
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
    //==============================================================================================end SearchMenu();



}//end class()
