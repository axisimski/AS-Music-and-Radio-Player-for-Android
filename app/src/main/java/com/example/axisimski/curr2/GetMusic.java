package com.example.axisimski.curr2;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import java.util.Collections;
import java.util.List;

public class GetMusic{

    //take in two lists and populate them with song titles (titlelist) and locations (list)
    public void getMusic(List<String> list, List<String> titlelist, Context context){

        ContentResolver contentResolver=context.getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor=  contentResolver.query(songUri, null,
                null, null, null);

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

            songCursor.close();
            Collections.reverse(list);
            Collections.reverse(titlelist);
        }
    }//---------------------------------------------------------------------------------------------end getMusic();

}//end class
