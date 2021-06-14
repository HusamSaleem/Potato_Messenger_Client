# A messenger replica AKA "Potato Messenger" :)

# Why I created it
- A nice simplified replica of a chat system like Facebook's messenger. I created this to learn more about networking, MySQL, AWS and Android Studio so it isn't 
really meant for anything outside of personal use or just for fun. Almost 5k lines of code combined with the client side :o

# Description 
- This is the client side of the app. It is made in Android Studio using the Java programming language. Users can create accounts, enter global chat rooms,
send friend requests, and essentially chat!

# Client Side Features
- Accounts with unique usernames and hashed passwords (PLUS salt) for security
- A nice clean dark mode UI 
- Chat rooms where anyone can join (They are also saved until removed by the user)
- Private messaging as well to friends only
- Basic Notifications for both global chat rooms and private messaging
- Friend Requests
- Know when your friends are online

# Known Bugs
- When you click the same chat (Either a global chat room or private) it will duplicate the messages
- The knowing when your friends are online is sometimes glitchy and doesn't work

# How to setup
- The client side setup should be done after setting up the server side first
- You do need Android Studio and so you can just import this repository directly into Android Studio. 
- All you need to do is change the host address in app/src/potatomessenger/client/ServerConnectionInfo.java. You can also change the PORT address there too if you changed the default port which was 9663 in the server side. 
- And thats it! If you have not set up the server side yet, please go here: https://github.com/HusamSaleem/Potato_Messenger_Server


# Pictures!

- Account screen
- ![image](https://user-images.githubusercontent.com/60799172/121840956-387deb80-cc92-11eb-9e42-41e7e2bcaade.png)



- Menu Screen
- ![image](https://user-images.githubusercontent.com/60799172/121841228-c0fc8c00-cc92-11eb-8c54-0e45c58d26b4.png)



- Global Chatroom Screen
- ![image](https://user-images.githubusercontent.com/60799172/121841248-ceb21180-cc92-11eb-8117-4e259242b353.png)



- Private Messaging Screen
- ![image](https://user-images.githubusercontent.com/60799172/121841276-dbcf0080-cc92-11eb-96cb-c930ea4064a6.png)



- Friend Requests Screen
- ![image](https://user-images.githubusercontent.com/60799172/121841307-e5586880-cc92-11eb-842e-f879a7f01b15.png)
