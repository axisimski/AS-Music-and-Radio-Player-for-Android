package com.example.axisimski.curr2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RadioActivity extends AppCompatActivity {

    private Button play_button, add_button;  //Buttons for playing/pause, adding a radio station
    private ServiceConnection serviceConnection=getServiceConnection();
    private RadioService RadioService=new RadioService();
    private boolean isPlaying=false; //Is music playing? Play/Pause
    private EditText link_edt; //Edit text for user inputed links...may move latter
    private ListView listView; //UI Radio Station list
    private List<String> list; //list containing Radio Station URLs
    private List <String> titlelist; //list containing Radio Station Titles
    private ListAdapter adapter; //ListView adapter
    private Intent intent; //Intent
    private int numStations=0;
    private boolean bound=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        //Initialize Variables
        play_button=findViewById(R.id.button_play);
        add_button=findViewById(R.id.button_add);
        link_edt=findViewById(R.id.link_edt);
        listView=findViewById(R.id.listView);
        list=new ArrayList<>();
        titlelist=new ArrayList<>();
        intent=new Intent(RadioActivity.this, RadioService.class);

        titlelist.add("Radio Ultra");


        adapter=new ArrayAdapter<>(this, R.layout.cust_list, titlelist);
        listView.setAdapter(adapter);

        //User Input
        userInput();
    }
    //==============================================================================================end onCreate()

    //Keeps the onClickListeners for the UI elements
    public void userInput(){

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isPlaying){
                    playRadio("http://88.80.96.25:8020", intent);
                    isPlaying=true;
                }
                else {
                    RadioService.mediaPlayer.stop();
                    isPlaying=false;
                }
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRadio(link_edt.getText().toString(), intent);
            }
        });

    }
    //==============================================================================================end userInput()

    //Start Radio Service
    public void playRadio(String link, Intent intent){
        intent.putExtra("link", link);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    //==============================================================================================end playRadio()





    //Establish Service Connection
    public ServiceConnection getServiceConnection() {

        if(serviceConnection==null){
            serviceConnection=new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    RadioService.serviceBinder myServiceBinder=(RadioService.serviceBinder)service;
                    RadioService=myServiceBinder.getService();
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



}//end class()
