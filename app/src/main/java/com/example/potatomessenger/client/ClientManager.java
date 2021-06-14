package com.example.potatomessenger.client;

import android.util.Log;

import com.example.potatomessenger.listeners.SendData;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class ClientManager {
    public static String username;
    public static String chatID = "";
    public static String friendChatID = "";
    public static boolean isLoggedIn = false;

    public static boolean talkingToFriend = false;

    public static boolean menuActivityActive = false;
    public static boolean chatActivityActive = false;
    public static boolean friendRequestsActive = false;


    // Connection variables
    public static boolean isConnected = false;
    public static Socket socket;

    public static ArrayList<Friend> friendList = new ArrayList<Friend>();
    public static ArrayList<String> roomIds = new ArrayList<String>();

    public static HashMap<Integer, Boolean> roomIdNotifications = new HashMap<Integer, Boolean>();
    public static HashMap<Integer, Boolean> friendNotifications = new HashMap<Integer, Boolean>();

    public static ArrayList<Message> currentChatroomMessages = new ArrayList<Message>();
    public static ArrayList<Message> currentFriendMessages = new ArrayList<Message>();
    public static ArrayList<FriendRequest> friendRequests = new ArrayList<FriendRequest>();


    public synchronized static void removeChatId(String name, String chatID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("chatID", chatID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new SendData("Remove Chat ID: " + jsonObject.toString())).start();
    }

    public synchronized static void checkForNewMessages(String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new SendData("Check Chatroom Notifications: " + jsonObject.toString())).start();
    }

    public synchronized static void checkForNewFriendMessages(String name) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new SendData("Check Friend Notifications: " + jsonObject.toString())).start();
    }

    public synchronized static void removeFriend(String name, String friendName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("friendName", friendName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new SendData("Remove Friend: " + jsonObject.toString())).start();
    }

    public synchronized static void addFriend(String name, String friendName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("friendName", friendName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Thread(new SendData("Request to add Friend: " + jsonObject.toString())).start();
    }

    public synchronized static void addChatID(String name, String chatID) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("chatID", chatID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(new SendData("Add Chat ID: " + jsonObject.toString())).start();
    }

    public synchronized static void retrieveRoomMessages(String chatID) {
        new Thread(new SendData("Retrieve Messages: " + chatID)).start();
    }

    public synchronized static void retrieveFriendMessages(String name, String friendName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("friendName", friendChatID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jsonStr = jsonObject.toString();
        new Thread(new SendData("Retrieve Friend Messages: " + jsonStr)).start();
    }

    public synchronized static void deleteRecentChatIDCache() {
        new Thread(new SendData("Delete recent cache")).start();
    }

    public synchronized static void deleteRecentFriendCache() {
        new Thread(new SendData("Delete recent friend cache")).start();
    }

    public synchronized static void getFriendList() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", ClientManager.username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(new SendData("Retrieve Friends: " + jsonObject.toString())).start();
    }

    public synchronized static void getChatRooms() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", ClientManager.username);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread(new SendData("Retrieve ChatIDs: " + jsonObject.toString())).start();
    }

    public synchronized static void sendMessageToChatID(String msg, String username) {
        JSONObject jsonObject = new JSONObject();
        Message message = new Message(ClientManager.username, msg,null);
        try {
            jsonObject.put("chatID", ClientManager.chatID);
            jsonObject.put("user", message.getName());
            jsonObject.put("msg", message.getMsg());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonAsString = jsonObject.toString();
        new Thread(new SendData("Send Message: " + jsonAsString)).start();
    }

    public synchronized static void sendMessageToFriend(String msg, String username, String friendName) {
        JSONObject jsonObject = new JSONObject();
        Message message = new Message(ClientManager.username, msg,null);
        try {
            jsonObject.put("name", ClientManager.username);
            jsonObject.put("friendName", friendName);
            jsonObject.put("msg", message.getMsg());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonAsString = jsonObject.toString();
        new Thread(new SendData("Send Friend Message: " + jsonAsString)).start();
    }

    public synchronized static void acceptFriendRequest(String clientName, String requestingName) {
        JSONObject jsonObject = new JSONObject();
        FriendRequest friendRequest = new FriendRequest(clientName, requestingName);
        try {
            jsonObject.put("friendName", clientName);
            jsonObject.put("requestingName", requestingName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonAsString = jsonObject.toString();
        new Thread(new SendData("Accept Friend Request: " + jsonAsString)).start();
    }

    public synchronized static void declineFriendRequest(String clientName, String requestingName) {
        JSONObject jsonObject = new JSONObject();
        FriendRequest friendRequest = new FriendRequest(clientName, requestingName);
        try {
            jsonObject.put("friendName", clientName);
            jsonObject.put("requestingName", requestingName);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonAsString = jsonObject.toString();
        new Thread(new SendData("Decline Friend Request: " + jsonAsString)).start();
    }

    public synchronized static void retrieveFriendRequests(String clientName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", clientName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonAsString = jsonObject.toString();
        new Thread(new SendData("Retrieve Friend Requests: " + jsonAsString)).start();
    }
}
