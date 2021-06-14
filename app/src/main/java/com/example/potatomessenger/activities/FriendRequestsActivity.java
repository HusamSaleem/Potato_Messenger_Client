package com.example.potatomessenger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potatomessenger.R;
import com.example.potatomessenger.allAdapters.FriendRequestAdapter;
import com.example.potatomessenger.client.ClientManager;
import com.example.potatomessenger.client.FriendRequest;
import com.example.potatomessenger.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class FriendRequestsActivity extends AppCompatActivity {
    private Button goBackBtn;

    private RecyclerView friendRequestView;
    private static FriendRequestAdapter friendAdapter;
    private static ArrayList<FriendRequest> friendRequests;

    // Progress bar animation
    ProgressBar progressBar;
    public static boolean progressBarDone = false;
    int loopCount = 0;

    private boolean buttonClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_requests);

        progressBar = findViewById(R.id.loadingBar3);

        setUpRequestView();
        setGoBackListener();
        getfriendRequests();
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
        ClientManager.friendRequestsActive = false;
        ClientManager.menuActivityActive = true;
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    private void getfriendRequests() {
        ClientManager.retrieveFriendRequests(ClientManager.username);
    }

    private void setUpRequestView() {
        friendRequestView = findViewById(R.id.friendRequestView);

        friendRequests = new ArrayList<FriendRequest>();

        friendAdapter = new FriendRequestAdapter(this, friendRequests);

        friendRequestView.setItemViewCacheSize(20);
        friendRequestView.setAdapter(friendAdapter);
        friendRequestView.setLayoutManager(new LinearLayoutManager(this));

        friendAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                acceptRequest(pos);
            }

            @Override
            public void onDeleteClick(int pos) {
                deleteRequest(pos);
            }
        });
    }

    private void acceptRequest(int pos) {
        if (buttonClicked)
            return;

        buttonClicked = true;

        startLoadingBar();

        ClientManager.acceptFriendRequest(friendRequests.get(pos).getFriendName(), friendRequests.get(pos).getRequestingName());
        friendRequests.remove(friendRequests.get(pos));
        friendAdapter.notifyItemRemoved(pos);
    }

    private void deleteRequest(int pos) {
        if (buttonClicked)
            return;

        buttonClicked = true;

        startLoadingBar();

        ClientManager.declineFriendRequest(friendRequests.get(pos).getFriendName(), friendRequests.get(pos).getRequestingName());
        friendRequests.remove(friendRequests.get(pos));
        friendAdapter.notifyItemRemoved(pos);
    }

    private void setGoBackListener() {
        goBackBtn = findViewById(R.id.goBackBtn2);

        goBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonClicked)
                    return;

                Intent intent = new Intent(view.getContext(), MenuActivity.class);
                startActivity(intent);

                ClientManager.friendRequestsActive = false;
                ClientManager.menuActivityActive = true;
            }
        });
    }

    public static void newFriendRequestFromServer(ArrayList<FriendRequest> list) {
        FriendRequestsActivity.friendRequests.clear();
        FriendRequestsActivity.friendRequests.addAll(list);
        FriendRequestsActivity.friendAdapter.notifyDataSetChanged();
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
}