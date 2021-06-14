package com.example.potatomessenger.listeners;

import com.example.potatomessenger.client.ClientManager;

import java.util.TimerTask;

public class RetrievingData extends TimerTask {
    @Override
    public void run() {
        retrieveInfo();
    }

    private void retrieveInfo() {
        if (ClientManager.menuActivityActive) {
            updateMenu();
        } else if (ClientManager.chatActivityActive) {
            updateChat();
        } else if (ClientManager.friendRequestsActive) {
            updateFriendRequests();
        }
    }

    private void updateMenu() {
        ClientManager.getFriendList();
        ClientManager.checkForNewMessages(ClientManager.username);
        ClientManager.checkForNewFriendMessages(ClientManager.username);
    }

    private void updateChat() {
        if (ClientManager.talkingToFriend)
            ClientManager.retrieveFriendMessages(ClientManager.username, ClientManager.friendChatID);
        else
            ClientManager.retrieveRoomMessages(ClientManager.chatID);
    }

    private void updateFriendRequests() {
        ClientManager.retrieveFriendRequests(ClientManager.username);
    }
}
