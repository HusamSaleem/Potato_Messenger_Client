package com.example.potatomessenger.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potatomessenger.R;
import com.example.potatomessenger.allAdapters.ChatAdapter;
import com.example.potatomessenger.client.ClientManager;
import com.example.potatomessenger.client.Message;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends Activity {
    private Button goBackBtn;

    private Button sendMsg;
    private TextView msgInput;
    private TextView roomIDTxt;

    private static RecyclerView chatView;
    public static ChatAdapter chatAdapter;

    private static ArrayList<Message> msgList;

    // Progress bar animation
    ProgressBar progressBar;
    public static boolean progressBarDone = false;
    int loopCount = 0;

    private boolean buttonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        progressBar = findViewById(R.id.loadingBar2);
        startLoadingBar();

        setGoBackListener();
        setUpChat();
        setSendMsgListener();
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
        ClientManager.chatActivityActive = false;
        ClientManager.menuActivityActive = true;
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }


    private void setUpChat() {
        chatView = findViewById(R.id.chatView);
        roomIDTxt = findViewById(R.id.chatID);

        msgList = new ArrayList<Message>();

        if (!ClientManager.talkingToFriend) {
            msgList.addAll(ClientManager.currentChatroomMessages);
            roomIDTxt.setText("Room ID: " + ClientManager.chatID);
        }
        else {
            msgList.addAll(ClientManager.currentFriendMessages);
            roomIDTxt.setText("Room ID: " + ClientManager.friendChatID);
        }

        chatAdapter = new ChatAdapter(this, msgList);
        chatView.setItemViewCacheSize(20);
        chatView.setAdapter(chatAdapter);
        chatView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setSendMsgListener() {
        msgInput = findViewById(R.id.chatInput);
        sendMsg = findViewById(R.id.sendMsgBtn);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = msgInput.getText().toString();
                if (!buttonClicked && msg.trim().length() != 0) {
                    buttonClicked = true;

                    if (ClientManager.talkingToFriend)
                        insertNewMsg(msg, ClientManager.username, 2);
                    else
                        insertNewMsg(msg, ClientManager.username, 1);

                    msgInput.setText("");
                }
            }
        });
    }

    // Sends the message to server
    private void insertNewMsg(String msg, String username, int type) {
        startLoadingBar();

        if (type == 1)
            ClientManager.sendMessageToChatID(msg, username);
        else
            ClientManager.sendMessageToFriend(msg,username, ClientManager.friendChatID);

        hideSoftKeyboard();
    }

    public static void newMessagesFromServer(ArrayList<Message> list) {
        msgList.clear();
        msgList.addAll(list);
        ChatActivity.chatAdapter.notifyDataSetChanged();
        chatView.scrollToPosition(msgList.size() - 1);
    }

    private void setGoBackListener() {
        goBackBtn = findViewById(R.id.goBackBtn1);

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonClicked)
                    return;
                goToMenu(view);
            }
        });
    }

    private void goToMenu(View view) {
        ClientManager.chatActivityActive = false;
        ClientManager.menuActivityActive = true;
        Intent intent = new Intent(view.getContext(), MenuActivity.class);
        startActivity(intent);
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

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(this.getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}