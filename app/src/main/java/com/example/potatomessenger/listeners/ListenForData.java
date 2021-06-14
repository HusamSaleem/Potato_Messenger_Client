package com.example.potatomessenger.listeners;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;

import com.example.potatomessenger.activities.ChatActivity;
import com.example.potatomessenger.activities.FriendRequestsActivity;
import com.example.potatomessenger.activities.MainActivity;
import com.example.potatomessenger.activities.MenuActivity;
import com.example.potatomessenger.client.ClientManager;
import com.example.potatomessenger.client.Friend;
import com.example.potatomessenger.client.FriendRequest;
import com.example.potatomessenger.client.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.TimerTask;

import javax.net.ssl.SSLSocket;

// Listens and processes the data received
public class ListenForData extends TimerTask {
    Socket socket;

    public ListenForData(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        processData();
    }

    /**
     * Checks for incoming data through the socket connection from the client
     *
     * @return a String[] array that contains all the data that the client sent
     * @throws IOException
     * @throws InterruptedException
     */
    public String[] recieveData() throws IOException, InterruptedException {
        int red = -1;
        byte[] buffer = new byte[5 * 1024]; // A read buffer of 5 KiB
        byte[] redData;

        String clientData = "";
        String redDataText;

        // While there is still data available
        while ((red = socket.getInputStream().read(buffer)) > -1) {
            redData = new byte[red];
            System.arraycopy(buffer, 0, redData, 0, red);
            redDataText = new String(redData, "UTF-8"); // The client sends UTF-8 Encoded
            clientData += redDataText;

            if (clientData.indexOf("`") != -1) {
                break;
            }
        }

        // Turn all the sent commands into an array in case if they get combined
        String[] data = clientData.split("`");
        return data;
    }

    private void processData() {
        try {
            String data[] = recieveData();

            for (String s : data) {
                Log.v("Data", " Data recieved by server: " + s);

                if (s.equals("Ping!")) {
                    new Thread(new SendData("Ping")).start();
                } else if (s.contains(":Login Success")) {
                    ClientManager.username = s.substring(0, s.indexOf(":")).toLowerCase();
                    ClientManager.isLoggedIn = true;
                    MainActivity.progressBarDone = true;
                    MainActivity.switchActivities(1);
                } else if (s.contains(":Register Success")) {
                    ClientManager.username = s.substring(0, s.indexOf(":")).toLowerCase();
                    ClientManager.isLoggedIn = true;
                    MainActivity.progressBarDone = true;
                    MainActivity.switchActivities(1);
                    ClientManager.menuActivityActive = true;
                } else if (s.equals("Login Failure")) {
                    loginFailure();
                } else if (s.equals("Register Failure")) {
                    registerFailure();
                } else if (s.contains("JSON CHATROOM: ")) {
                    String json = s.substring(s.indexOf(":") + 2).trim();

                    if (!json.equals(""))
                        deserializeChatroomMessagesJsonObject(json);
                    else
                        ChatActivity.progressBarDone = true;
                } else if (s.contains("JSON SAVED CHATROOMS: ")) {
                    ClientManager.roomIds.clear();
                    String json = s.substring(s.indexOf(":") + 2).trim();

                    if (!json.equals(""))
                        deserializeChatIDSJsonObject(json);
                } else if (s.contains("JSON FRIEND CHATROOM: ")) {
                    String json = s.substring(s.indexOf(":") + 2).trim();

                    if (!json.equals(""))
                        deserializeFriendMessagesJsonObject(json);
                    else
                        ChatActivity.progressBarDone = true;
                } else if (s.contains("JSON FRIEND LIST: ")) {
                    ClientManager.friendList.clear();
                    String json = s.substring(s.indexOf(":") + 2).trim();

                    if (!json.equals(""))
                        deserializeFriendListJsonObject(json);
                } else if (s.contains("JSON FRIEND REQUESTS: ")) {
                    ClientManager.friendRequests.clear();
                    String json = s.substring(s.indexOf(":") + 2).trim();

                    if (!json.equals(""))
                        deserializeFriendRequestListJsonObject(json);
                    else
                        FriendRequestsActivity.progressBarDone = true;
                } else if (s.contains("NEW NOTIFICATIONS: ")) {
                    String json = s.substring(s.indexOf(":") + 2).trim();

                    if (!json.equals(""))
                        deserializeNotificationsJsonObject(json);
                } else if (s.contains("NEW FRIEND NOTIFICATIONS: ")) {
                    String json = s.substring(s.indexOf(":") + 2).trim();

                    if (!json.equals(""))
                        deserializeFriendNotificationsJsonObject(json);
                } else if (s.equals("Failed to accept friend request") || s.equals("Failed to decline friend request")) {
                    FriendRequestsActivity.progressBarDone = true;
                } else if (s.equals("Friend request successfully accepted") || s.equals("Friend request successfully declined")) {
                    FriendRequestsActivity.progressBarDone = true;
                } else if (s.equals("Message successfully sent!")) {
                    ChatActivity.progressBarDone = true;
                }
            }
        } catch (IOException | InterruptedException | JSONException e) {
            Log.e("error", e.getMessage());
        }
    }

    /** START OF MAIN ACTIVITY DESERIALIZATION METHODS **/
    private void loginFailure() {
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                MainActivity.progressBarDone = true;
                MainActivity.setErrorLog("Failed to Log in to account (Wrong credentials)");
            }
        };

        handler.post(runnable);
    }

    private void registerFailure() {
        Handler handler = new Handler(Looper.getMainLooper());

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                MainActivity.progressBarDone = true;
                MainActivity.setErrorLog("Failed to register account (Duplicate username)");
            }
        };

        handler.post(runnable);
    }
    /** END OF MAIN ACTIVITY DESERIALIZATION METHODS **/

    /** START OF FRIEND REQUESTS DESERIALIZATION METHODS **/
    private void deserializeFriendRequestListJsonObject(String json) throws JSONException {
        JSONArray jsonObject = new JSONArray(json);

        for (int i = 0; i < jsonObject.length(); i++) {
            JSONObject item = jsonObject.getJSONObject(i);

            FriendRequest friend = new FriendRequest(item.getString("friendName"), item.getString("requestingName"));
            ClientManager.friendRequests.add(friend);
        }

        // To handle UI THREAD
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                FriendRequestsActivity.newFriendRequestFromServer(ClientManager.friendRequests);
                FriendRequestsActivity.progressBarDone = true;
            }
        };
        mainHandler.post(myRunnable);
    }
    /** END OF FRIEND REQUESTS DESERIALIZATION METHODS **/

    /** START OF CHAT ACTIVITY DESERIALIZATION METHODS**/
    private void deserializeChatroomMessagesJsonObject(String json) throws JSONException {
        JSONArray jsonObject = new JSONArray(json);

        for (int i = 0; i < jsonObject.length(); i++) {
            JSONObject item = jsonObject.getJSONObject(i);

            Message message = new Message(item.get("name").toString(), item.get("msg").toString(), item.get("date").toString());
            ClientManager.currentChatroomMessages.add(message);
        }

        // To handle UI THREAD
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                ChatActivity.newMessagesFromServer(ClientManager.currentChatroomMessages);
                ChatActivity.progressBarDone = true;
            }
        };
        mainHandler.post(myRunnable);
    }

    private void deserializeFriendMessagesJsonObject(String json) throws JSONException {
        JSONArray jsonObject = new JSONArray(json);

        for (int i = 0; i < jsonObject.length(); i++) {
            JSONObject item = jsonObject.getJSONObject(i);

            Message message = new Message(item.get("name").toString(), item.get("msg").toString(), item.get("date").toString());
            ClientManager.currentFriendMessages.add(message);
        }

        // To handle UI THREAD
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                ChatActivity.newMessagesFromServer(ClientManager.currentFriendMessages);
                ChatActivity.progressBarDone = true;
            }
        };
        mainHandler.post(myRunnable);
    }
    /** END OF CHAT ACTIVITY DESERIALIZATION METHODS**/

    /** START OF MENU ACTIVITY DESERIALIZATION METHODS**/
    private void deserializeFriendNotificationsJsonObject(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        for (int i = 0; i < jsonObject.length(); i++) {
            String item = jsonObject.get(Integer.toString(i)).toString();

            int index;
            for (index = 0; i < ClientManager.friendList.size(); i++) {
                if (ClientManager.friendList.get(index).getName().toLowerCase().equals(item)) {
                    ClientManager.friendNotifications.put(index, true);
                    break;
                }
            }
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MenuActivity.newFriendListFromServer(ClientManager.friendList);
            }
        };
        mainHandler.post(myRunnable);
    }

    private void deserializeNotificationsJsonObject(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        for (int i = 0; i < jsonObject.length(); i++) {
            String item = jsonObject.get(Integer.toString(i)).toString();

            int index = ClientManager.roomIds.indexOf(item);
            ClientManager.roomIdNotifications.put(index, true);
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MenuActivity.newRoomIdsFromServer(ClientManager.roomIds);
            }
        };
        mainHandler.post(myRunnable);
    }

    private void deserializeChatIDSJsonObject(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        for (int i = 0; i < jsonObject.length(); i++) {
            String item = jsonObject.get(Integer.toString(i)).toString();
            ClientManager.roomIds.add(item);
        }

        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MenuActivity.newRoomIdsFromServer(ClientManager.roomIds);
                MenuActivity.progressBarDone = true;
            }
        };
        mainHandler.post(myRunnable);
    }

    private void deserializeFriendListJsonObject(String json) throws JSONException {
        JSONArray jsonObject = new JSONArray(json);

        for (int i = 0; i < jsonObject.length(); i++) {
            JSONObject item = jsonObject.getJSONObject(i);

            boolean active = (item.getString("active").equals(true)) ? true : false;

            Friend friend = new Friend(item.getString("name"), active);
            ClientManager.friendList.add(friend);
        }

        // To handle UI THREAD
        Handler mainHandler = new Handler(Looper.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                MenuActivity.newFriendListFromServer(ClientManager.friendList);
                MenuActivity.progressBarDone1 = true;
            }
        };
        mainHandler.post(myRunnable);
    }

}
