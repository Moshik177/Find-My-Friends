package com.sadna.app.findmyfriends.activities;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;
import com.sadna.app.findmyfriends.BaseActivity;
import com.sadna.app.findmyfriends.MyApplication;
import com.sadna.app.findmyfriends.R;
import com.sadna.app.findmyfriends.utils.ChatUtils;
import com.sadna.app.findmyfriends.utils.Message;
import com.sadna.app.findmyfriends.utils.MessagesListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by avihoo on 22/08/2015.
 */


public class ChatGroupActivity extends BaseActivity {

    // LogCat tag
    private static final String TAG = "ChatGroupActivity";

    private static final String CHAT_SOCKET_URL = "vmedu68.mtacloud.co.il";

    public static final String URL_WEBSOCKET = "ws://" + CHAT_SOCKET_URL + ":8081/sadna.chat-server/chat?name=%s&room_name=%s";

    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";

    private Button btnSend;
    private EditText inputMsg;

    private WebSocketClient client;

    // Chat messages list adapter
    private MessagesListAdapter adapter;
    private List<Message> listMessages;
    private ListView listViewMessages;

    private ChatUtils utils;

    // Client name
    private String name = null;

    // Client room
    private String room = null;

    private String mTitle = "FindMyFriends";
    private int mNotificationId = 1;
    private static final int MAX_NOTIFICATIONS = 5;
    NotificationManager mNotifyMgr;

    @Override

    protected void onResume() {
        super.onResume();
        mNotifyMgr.cancelAll();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        ((TextView) findViewById(R.id.groupNameTitle)).setText(((MyApplication) getApplication()).getSelectedGroupName());
        btnSend = (Button) findViewById(R.id.btnSend);
        inputMsg = (EditText) findViewById(R.id.inputMsg);
        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        utils = new ChatUtils(getApplicationContext());

        // Getting the person name from previous screen
        Intent i = getIntent();
        name = i.getStringExtra("name");
        room = i.getStringExtra("room_name");

        listMessages = new ArrayList<>();

        adapter = new MessagesListAdapter(this, listMessages);
        listViewMessages.setAdapter(adapter);

        /**
         * Creating web socket client. This will have callback methods
         * */
        client = new WebSocketClient(URI.create(String.format(URL_WEBSOCKET, URLEncoder.encode(name), URLEncoder.encode(room))), new WebSocketClient.Listener() {
            @Override
            public void onConnect() {

            }

            /**
             * On receiving the message from web socket server
             * */
            @Override
            public void onMessage(String message) {
                Log.d(TAG, String.format("Got string message! %s", message));

                parseMessage(message);

            }

            @Override
            public void onMessage(byte[] data) {
                Log.d(TAG, String.format("Got binary message! %s",
                        bytesToHex(data)));

                // Message will be in JSON format
                parseMessage(bytesToHex(data));
            }

            /**
             * Called when the connection is terminated
             * */
            @Override
            public void onDisconnect(int code, String reason) {

                String message = String.format(Locale.US,
                        "Disconnected! Code: %d Reason: %s", code, reason);

                showToast(message);

                // clear the session id from shared preferences
                utils.storeSessionId(null);
            }

            @Override
            public void onError(Exception error) {
                Log.e(TAG, "Error! : " + error);

                //showToast("Error! : " + error);
            }

        }, null);

        client.connect();
    }

    public void onButtonSendClick(View v) {
        if (!inputMsg.getText().toString().trim().isEmpty()) {
            // Sending message to web socket server
            sendMessageToServer(utils.getSendMessageJSON(inputMsg.getText()
                    .toString()));

            // Clearing the input filed once message was sent
            inputMsg.setText("");
        }
    }

    /**
     * Method to send message to web socket server
     */
    private void sendMessageToServer(String message) {
        if (client != null && client.isConnected()) {
            client.send(message);
        }
    }

    /**
     * Parsing the JSON message received from server The intent of message will
     * be identified by JSON node 'flag'. flag = self, message belongs to the
     * person. flag = new, a new person joined the conversation. flag = message,
     * a new message received from server. flag = exit, somebody left the
     * conversation.
     */
    private void parseMessage(final String msg) {

        try {
            JSONObject jObj = new JSONObject(msg);

            // JSON node 'flag'
            String flag = jObj.getString("flag");

            // if flag is 'self', this JSON contains session id
            if (flag.equalsIgnoreCase(TAG_SELF)) {

                String sessionId = jObj.getString("sessionId");

                // Save the session id in shared preferences
                utils.storeSessionId(sessionId);

                Log.e(TAG, "Your session id: " + utils.getSessionId());

            } else if (flag.equalsIgnoreCase(TAG_NEW)) {
                // If the flag is 'new', new person joined the room
                String name = jObj.getString("name");
                String message = jObj.getString("message");

                // number of people online
                String onlineCount = jObj.getString("onlineCount");

                showToast(name + message + ". Currently " + onlineCount
                        + " people online!");

            } else if (flag.equalsIgnoreCase(TAG_MESSAGE)) {
                // if the flag is 'message', new message received
                String fromName = name;
                String message = jObj.getString("message");
                String sessionId = jObj.getString("sessionId");
                boolean isSelf = true;

                // Checking if the message was sent by you
                if (!sessionId.equals(utils.getSessionId())) {
                    fromName = jObj.getString("name");
                    isSelf = false;
                }

                Message m = new Message(fromName, message, isSelf);

                // Appending the message to chat list
                appendMessage(m);

            } else if (flag.equalsIgnoreCase(TAG_EXIT)) {
                // If the flag is 'exit', somebody left the conversation
                String name = jObj.getString("name");
                String message = jObj.getString("message");

                showToast(name + message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (client != null && client.isConnected()) {
            client.disconnect();
        }
    }

    /**
     * Appending message to list view
     */
    private void appendMessage(final Message m) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                listMessages.add(m);

                adapter.notifyDataSetChanged();

                // Playing device's notification
                playBeep(m.isSelf());
                if (!((MyApplication) getApplication()).isActivityVisible()) {
                    notifySendingMessage(m);
                }
            }
        });
    }

    private void showToast(final String message) {
        if (((MyApplication) getApplication()).isActivityVisible()) {
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), message,
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    /**
     * Plays device's default notification sound
     */
    public void playBeep(boolean isSelf) {

        try {
            Uri notification;
            if (isSelf) {
                MediaPlayer player = MediaPlayer.create(this, R.raw.chat_ringtone_pop);
                player.setLooping(false);
                player.start();
            } else {
                if (((MyApplication) getApplication()).isActivityVisible()) {
                    MediaPlayer player = MediaPlayer.create(this, R.raw.message_incoming);
                    player.setLooping(false);
                    player.start();
                } else {
                    notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    MediaPlayer player = MediaPlayer.create(this, notification);
                    player.setLooping(false);
                    player.start();
                }
            }
        } catch (Exception e) {
            Log.e("ChatGroupActivity", e.getMessage());
        }
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private void notifySendingMessage(Message message) {
        Intent i = getIntent();
        String name = message.getFromName();
        String text = message.getMessage();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.find_my_friends_app_logo)
                .setContentTitle(mTitle)
                .setContentText(name + ": " + text).setAutoCancel(true);

        mNotificationId = (mNotificationId == MAX_NOTIFICATIONS) ? 1 : mNotificationId+1;
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, mNotificationId, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}