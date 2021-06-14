package com.example.potatomessenger.client;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.potatomessenger.activities.MainActivity;
import com.example.potatomessenger.listeners.ListenForData;
import com.example.potatomessenger.listeners.RetrievingData;

import java.io.IOException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class ServerConnectionInfo extends TimerTask {
    private final int RETRIEVE_INFO_INTERVAL = 5000; // 5 seconds
    // 50 milisecond delay
    private final int DATA_LISTEN_INTERVAL = 50;
    private final int INIT_DELAY = 10000; // 10 seconds

    private final String HOST = "potatoserverdns.ddns.net"; // CHANGE ME!!!
    private final int PORT_NUMBER = 9663; // CHANGE ME!!!


    @Override
    public void run() {
        startConnection();
        publishProgress();
    }

    public boolean startConnection() {
        if (ClientManager.socket != null && ClientManager.isConnected) {
            return true;
        }
        try {
            ClientManager.socket = new Socket(HOST, PORT_NUMBER);
            // Validates the connection integrity by checking certificate
            ClientManager.isConnected = true;

            // Make a thread that will listen and process data
            TimerTask listenTask = new ListenForData(ClientManager.socket);
            Timer timer1 = new Timer(true);
            timer1.scheduleAtFixedRate(listenTask, INIT_DELAY, DATA_LISTEN_INTERVAL);

            TimerTask retrieveDataTask = new RetrievingData();
            Timer timer2 = new Timer(true);
            timer2.scheduleAtFixedRate(retrieveDataTask, INIT_DELAY, RETRIEVE_INFO_INTERVAL);
            return true;
        } catch (IOException e) {
            Log.e("Error", e.getMessage());
            ClientManager.isConnected = false;
            return false;
        }
    }

    private void publishProgress() {
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (ClientManager.isConnected) {
                    MainActivity.setErrorLog("Successfully connected to the server!");
                    MainActivity.timer.cancel();
                }
                else {
                    MainActivity.setErrorLog("Failed to connect to the server");
                }

            }
        };

        handler.post(runnable);
    }
}
