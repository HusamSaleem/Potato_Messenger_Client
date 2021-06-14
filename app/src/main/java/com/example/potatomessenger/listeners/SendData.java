package com.example.potatomessenger.listeners;

import android.util.Log;

import com.example.potatomessenger.client.ClientManager;

import java.io.DataOutputStream;
import java.io.IOException;

public class SendData implements Runnable {
    String data = "";

    public SendData(String data) {
        this.data = data;
    }

    @Override
    public synchronized void run() {
        try {
            DataOutputStream writer = new DataOutputStream(ClientManager.socket.getOutputStream());
            // "`" means its the end of the data line
            writer.write((data + "`").getBytes());
            writer.flush();
        } catch (IOException e) {
            Log.i("ERROR", e.getMessage());
        }
    }

}
