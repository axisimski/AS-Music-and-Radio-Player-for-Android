package com.example.axisimski.curr2;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class CheckNetwork {

 //   private static final String TAG = NetworkAvailibility.class.getSimpleName();

    public boolean connectionAvailible(Context context)
    {
        NetworkInfo info = (NetworkInfo) ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

        return info != null;
    }

}
