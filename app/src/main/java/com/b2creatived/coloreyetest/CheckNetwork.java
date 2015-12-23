package com.b2creatived.coloreyetest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;

public class CheckNetwork {

    private static final String TAG = CheckNetwork.class.getSimpleName();

    public static boolean isInternetAvailable(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        if (info == null) {
            Log.d(TAG, "no internet connection");
            // Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            return false;
        }
        else
        {
            if(info.isConnected()) {
                //Toast.makeText(context, "Internet connection", Toast.LENGTH_SHORT).show();
                Log.d(TAG," internet connection available...");
                return true;
            }
            else {
                // Toast.makeText(context, "No internet connection2", Toast.LENGTH_SHORT).show();
                Log.d(TAG," internet connection");
                return true;
            }

        }
    }

    public static boolean isOnline() {

        Runtime runtime = Runtime.getRuntime();
        try {

            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);

        } catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }

        return false;
    }

    public static boolean isOnlany() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);
            return reachable;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public static void ShowDialog(Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("Check your internet connection");
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setMessage("Please make sure your device is connected to the internet.");
        alertDialogBuilder.setIcon(R.mipmap.icon);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialogBuilder.show();
    }
}