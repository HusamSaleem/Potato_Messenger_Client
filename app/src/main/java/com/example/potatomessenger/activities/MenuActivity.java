package com.example.potatomessenger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.potatomessenger.R;
import com.example.potatomessenger.allAdapters.FriendAdapter;
import com.example.potatomessenger.allAdapters.RoomIDAdapter;
import com.example.potatomessenger.client.ClientManager;
import com.example.potatomessenger.client.Friend;
import com.example.potatomessenger.interfaces.OnItemClickListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MenuActivity extends AppCompatActivity {
    private Button logOutBtn;
    private Button friendReqBtn;
    private TextView userNameTxt;

    // For adding/ removing chat rooms
    private Button insertItemBtn;
    private TextView roomIdInput;

    // For the chat ids
    private RecyclerView savedChatRoomsView;
    static RoomIDAdapter chatIDAdapter;

    // For the friend views
    private RecyclerView friendsView;
    static FriendAdapter friendAdapter;

    // For adding/ removing friends
    private Button insertItemBtn1;
    private TextView friendNameInput;

    public static ArrayList<String> roomIds;
    public static ArrayList<Friend> friendList;

    private boolean buttonClicked = false;

    // Progress bar animation
    ProgressBar progressBar;
    public static boolean progressBarDone = false; // For loading chat ids
    public static boolean progressBarDone1 = false; // For loading friend list
    int loopCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        userNameTxt = findViewById(R.id.userNameText);
        userNameTxt.setText("Logged in as: " + ClientManager.username.toLowerCase());
        progressBar = findViewById(R.id.loadingBar1);

        setLogOutListener();
        setUpChatRoomView();
        setFriendViews();

        getInfoFromServer();
        setChatRoomBtnListener();
        setFriendsBtnListener();
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
        ClientManager.menuActivityActive = false;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void getInfoFromServer() {
        startLoadingBar();
        ClientManager.getFriendList();
        ClientManager.getChatRooms();
        ClientManager.checkForNewMessages(ClientManager.username);
        ClientManager.checkForNewFriendMessages(ClientManager.username);
    }

    private void startLoadingBar() {
        final Timer timer = new Timer();
        progressBar.setVisibility(View.VISIBLE);
        progressBarDone = false;
        progressBarDone1 = false;
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


    private void setFriendViews() {
        friendsView = findViewById(R.id.friendsView);

        // Friend view
        MenuActivity.friendList = new ArrayList<Friend>();

        friendAdapter = new FriendAdapter(this, MenuActivity.friendList);
        friendsView.setItemViewCacheSize(20);
        friendsView.setAdapter(friendAdapter);
        friendsView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setUpChatRoomView() {
        savedChatRoomsView = findViewById(R.id.savedChatRoomsView);

        // Saved chat ids
        MenuActivity.roomIds = new ArrayList<String>();

        chatIDAdapter = new RoomIDAdapter(this,   MenuActivity.roomIds);
        savedChatRoomsView.setItemViewCacheSize(20);
        savedChatRoomsView.setAdapter(chatIDAdapter);
        savedChatRoomsView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setFriendsBtnListener() {
        insertItemBtn1 = findViewById(R.id.addFriendName);
        friendNameInput = findViewById(R.id.friendNameInput);
        friendAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                friendNameClicked(pos);
            }

            @Override
            public void onDeleteClick(int pos) {
                ClientManager.removeFriend(ClientManager.username, ClientManager.friendList.get(pos).getName());
                removeItem(ClientManager.friendList, pos, friendAdapter);
            }
        });

        insertItemBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = friendNameInput.getText().toString();

                Friend friend = hasFriend(name);
                if (friend == null && name.length() >= 3 &&!name.toLowerCase().equals(ClientManager.username.toLowerCase())) {
                    ClientManager.addFriend(ClientManager.username, name);
                    friendNameInput.setText("");

                    hideSoftKeyboard();
                }
            }
        });
    }

    private Friend hasFriend(String name) {
        for (Friend f : ClientManager.friendList) {
            if (f.getName().equals(name))
                return f;
        }
        return null;
    }

    /**
     * Sets up all the buttons on the menu
     */
    private void setChatRoomBtnListener() {
        insertItemBtn = findViewById(R.id.addRoomID);
        roomIdInput = findViewById(R.id.roomIdInput);
        friendReqBtn = findViewById(R.id.friendReqBtn);

        chatIDAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                roomIdClicked(pos);
            }

            @Override
            public void onDeleteClick(int pos) {
                ClientManager.removeChatId(ClientManager.username, ClientManager.roomIds.get(pos));
                removeItem(ClientManager.roomIds, pos, chatIDAdapter);
            }
        });

        insertItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = roomIdInput.getText().toString().trim();

                if (data.length() >= 2 && !ClientManager.roomIds.contains(data)) {
                    ClientManager.addChatID(ClientManager.username, data);
                    insertItem(ClientManager.roomIds, data, chatIDAdapter);
                    roomIdInput.setText("");

                    hideSoftKeyboard();
                }
            }
        });

        friendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFriendReq();
            }
        });
    }

    private void friendNameClicked(int pos) {
        if (buttonClicked)
            return;

        startLoadingBar();

        if (!buttonClicked)
            buttonClicked = true;

        if (!ClientManager.friendChatID.equals(ClientManager.friendList.get(pos).getName())) {
            ClientManager.currentFriendMessages.clear();
            ClientManager.deleteRecentFriendCache();
        }

        resetChatCache();
        switchingToChatActivity();

        ClientManager.friendChatID = ClientManager.friendList.get(pos).getName();
        ClientManager.friendNotifications.put(pos,false);
        ClientManager.talkingToFriend = true;

        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
        ClientManager.retrieveFriendMessages(ClientManager.username, ClientManager.friendChatID);
    }

    /**
     * Switches to the appropiate chat room.
     */
    private void roomIdClicked(int pos) {
        if (buttonClicked)
            return;

        startLoadingBar();

        if (!buttonClicked)
            buttonClicked = true;

        if (!ClientManager.chatID.equals(ClientManager.roomIds.get(pos))) {
            ClientManager.currentChatroomMessages.clear();
            ClientManager.deleteRecentChatIDCache();
        }
        resetFriendCache();
        switchingToChatActivity();

        ClientManager.chatID = ClientManager.roomIds.get(pos);
        ClientManager.roomIdNotifications.put(pos, false);
        ClientManager.talkingToFriend = false;

        Intent intent = new Intent(this, ChatActivity.class);
        startActivity(intent);
        ClientManager.retrieveRoomMessages(ClientManager.chatID);
    }

    private void resetFriendCache() {
        ClientManager.chatID = "";
        ClientManager.currentFriendMessages.clear();
        ClientManager.deleteRecentFriendCache();
    }

    private void resetChatCache() {
        ClientManager.chatID = "";
        ClientManager.currentChatroomMessages.clear();
        ClientManager.deleteRecentChatIDCache();
    }

    private void resetNotifications() {
        ClientManager.friendNotifications.clear();
        ClientManager.roomIdNotifications.clear();
    }

    private void switchingToChatActivity() {
        ClientManager.chatActivityActive = true;
        ClientManager.menuActivityActive = false;
    }

    private void hideSoftKeyboard() {
        View view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(this.getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void goToFriendReq() {
        if (buttonClicked)
            return;

        Intent intent = new Intent(this, FriendRequestsActivity.class);
        startActivity(intent);

        ClientManager.menuActivityActive = false;
        ClientManager.friendRequestsActive = true;
    }

    private void setLogOutListener() {
        logOutBtn = findViewById(R.id.logOutBtn);

        ClientManager.deleteRecentChatIDCache();
        ClientManager.deleteRecentFriendCache();

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (buttonClicked)
                    return;

                ClientManager.isLoggedIn = false;
                ClientManager.talkingToFriend = false;
                ClientManager.menuActivityActive = false;

                resetChatCache();
                resetFriendCache();
                resetNotifications();

                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public static void newFriendListFromServer(ArrayList<Friend> list) {
        MenuActivity.friendList.clear();
        MenuActivity.friendList.addAll(list);
        MenuActivity.friendAdapter.notifyDataSetChanged();
    }

    public static void newRoomIdsFromServer(ArrayList<String> list) {
        MenuActivity.roomIds.clear();
        MenuActivity.roomIds.addAll(list);
        MenuActivity.chatIDAdapter.notifyDataSetChanged();
    }

    // For adding & removing stuff
    public <T> void insertItem(ArrayList<T> arr, T data, RecyclerView.Adapter adapter) {
        arr.add(data);
        adapter.notifyItemInserted(arr.size() - 1);
    }
    public <T> void removeItem(ArrayList<T> arr, int index, RecyclerView.Adapter adapter) {
        arr.remove(index);
        adapter.notifyItemRemoved(index);
    }
}