package com.example.potatomessenger.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.potatomessenger.R;
import com.example.potatomessenger.client.ClientManager;
import com.example.potatomessenger.client.ServerConnectionInfo;
import com.example.potatomessenger.listeners.SendData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private Button loginBtn;
    private Button registerBtn;

    private static Context thisContext;
    private final int RETRY_CONNECTION_INTERVAL = 5000; // Every 5 seconds
    public static Timer timer;

    // Error msg
    static TextView errorLog;

    private boolean buttonClicked = false;

    // Progress bar animation
    ProgressBar progressBar;
    public static boolean progressBarDone = false;
    int loopCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.loadingBar1);
        errorLog = findViewById(R.id.errorLog);

        setLoginBtnListener();
        setRegisterBtnListener();
        thisContext = this.getApplicationContext();

        tryConnectToServer();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // DO NOTHING
        finish();
    }

    private void tryConnectToServer() {
        TimerTask serverConnectTask = new ServerConnectionInfo();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(serverConnectTask, 100, RETRY_CONNECTION_INTERVAL);
    }

    private void setLoginBtnListener() {
        loginBtn = findViewById(R.id.logInBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!buttonClicked && ClientManager.isConnected && checkUsername() && checkPassword()) {
                    buttonClicked = true;
                    tryLogIn();
                } else {
                    if (ClientManager.isConnected && !buttonClicked)
                        setErrorLog("Username must be 3-16 characters\nPassword must be 6-16 characters");
                }
            }
        });
    }

    private void setRegisterBtnListener() {
        registerBtn = findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (!buttonClicked && ClientManager.isConnected && checkUsername() && checkPassword()) {
                    buttonClicked = true;
                    tryRegistering();
                } else {
                    if (ClientManager.isConnected && !buttonClicked)
                        setErrorLog("Username must be 3-16 characters\nPassword must be 6-16 characters");
                }
            }
        });
    }

    private void tryRegistering() {
        startLoadingBar();
        String username = getUsernameInput();
        String password = getPasswordInput();

        JSONObject json = new JSONObject();

        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonAsString = json.toString();

        String dataToSend = "Register: " + jsonAsString;
        new Thread(new SendData(dataToSend)).start();
    }

    private void tryLogIn() {
        startLoadingBar();
        String username = getUsernameInput();
        String password = getPasswordInput();

        JSONObject json = new JSONObject();

        try {
            json.put("username", username);
            json.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonAsString = json.toString();

        String dataToSend = "Login: " + jsonAsString;
        new Thread(new SendData(dataToSend)).start();
    }

    private void startLoadingBar() {
        final Timer timer = new Timer();
        progressBar.setVisibility(View.VISIBLE);
        progressBarDone = false;
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Handler handler = new Handler(Looper.getMainLooper());

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        loopCount++;
                        progressBar.setProgress(loopCount);

                        if (progressBarDone) {
                            progressBar.setVisibility(View.INVISIBLE);
                            buttonClicked = false;
                            timer.cancel();
                        }

                    }
                };

                handler.post(runnable);
            }
        };
        timer.schedule(timerTask, 0, 100);
    }

    public static void setErrorLog(String msg) {
        errorLog.setText(msg);
    }

    // Has to be 3-16 characters long and not contain whitespace
    private boolean checkUsername() {
        String name = getUsernameInput();

        if ((name.length() >= 3 && name.length() <= 16) && !name.equals(" ")) {
            return true;
        }

        return false;
    }

    private boolean checkPassword() {
        String pass = getPasswordInput();

        if ((pass.length() >= 6 && pass.length() <= 16) && !pass.equals(" ")) {
            return true;
        }
        return false;
    }

    private String getUsernameInput() {
        TextView userName = findViewById(R.id.nameInput);
        return userName.getText().toString().trim().toLowerCase();
    }

    private String getPasswordInput() {
        TextView password = findViewById(R.id.passwordInput);
        return password.getText().toString().trim();
    }

    /**
     * Switches to the menu after successfully logging in
     */
    public static void switchActivities(int id) throws IOException {
        Intent intent;
        switch (id) {
            case 1:
                intent = new Intent(thisContext, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ClientManager.menuActivityActive = true;
                thisContext.startActivity(intent);
                break;
            default:
                break;
        }
    }
}