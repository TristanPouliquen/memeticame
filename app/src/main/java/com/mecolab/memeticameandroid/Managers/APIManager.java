package com.mecolab.memeticameandroid.Managers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mecolab.memeticameandroid.Models.Conversation;
import com.mecolab.memeticameandroid.Models.MemeFile;
import com.mecolab.memeticameandroid.Models.Message;
import com.mecolab.memeticameandroid.Models.User;
import com.mecolab.memeticameandroid.Utils.CustomJsonArrayRequest;
import com.mecolab.memeticameandroid.Utils.CustomJsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by tristan on 14-09-16.
 */
public class APIManager {

    private static APIManager mInstance;
    private static Context mContext;

    public APIManager(Context context) {
        mContext = context;
    }

    public interface OnConversationReceive {
        void conversationReceive(ArrayList<Conversation> conversations);
    }
    public void getConversationsOfUser(String phoneNumber, final OnConversationReceive listener) {
        String url = NetworkingManager.BASE_URL + "users/get_conversations?phone_number=" + phoneNumber;

        CustomJsonArrayRequest request = new CustomJsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<Conversation> conversationsList = Conversation.from_jsonArray(response);
                            listener.conversationReceive(conversationsList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        NetworkingManager.getInstance(mContext).addToRequestQueue(request);
    }

    public interface OnConversationCreate {
        void conversationCreated(Conversation conversation);
    }

    public void createConversation(String chatTitle, String admin, ArrayList<String> users,
                                   final OnConversationCreate listener) {
        String url = NetworkingManager.BASE_URL + "conversations";
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("title", chatTitle);
            jsonBody.put("admin", admin);
            JSONArray usersArray = new JSONArray(users);
            jsonBody.put("users", usersArray);
            if (users.size() > 1) {
                jsonBody.put("group", true);
            } else {
                jsonBody.put("group", false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomJsonObjectRequest request = new CustomJsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Conversation conversation = Conversation.from_jsonObject(response);
                            listener.conversationCreated(conversation);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        NetworkingManager.getInstance(mContext).addToRequestQueue(request);
    }

    public interface OnUsersReceive {
        void usersReceived(ArrayList<User> backEndUsers);
    }

    public void getUsers(final OnUsersReceive listener) {
        String url = NetworkingManager.BASE_URL + "users";
        CustomJsonArrayRequest request = new CustomJsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<User> backEndUsers = User.from_jsonArray(response);
                            listener.usersReceived(backEndUsers);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        NetworkingManager.getInstance(mContext).addToRequestQueue(request);
    }

    public void addUserToConversation(int chatId, String phoneNumber) {
        String url = NetworkingManager.BASE_URL + "conversations/add_user";
        // POST
        // { "phone_number": ,"conversation_id": }
    }

    public void removeUserOfConversation(int chatId, String phoneNumber) {
        String url = NetworkingManager.BASE_URL + "conversations/remove_user?phone_number=";
        url += phoneNumber + "&conversation_id=" + chatId;
        // DELETE
    }

    public void removeAdminOfConversation(int chatId, String phoneNumber) {
        String url = NetworkingManager.BASE_URL + "conversations/remove_admin?phone_number=";
        url += phoneNumber + "&conversation_id=" + chatId;
        // DELETE
    }

    public interface OnMessageSend {
        void messageSent(Message message);
    }

    public void sendMessage(int chatId, String senderNumber, String message,
                            String base64Image, String imageName, String base64Audio, String audioName,
                            final OnMessageSend listener) {
        String url = NetworkingManager.BASE_URL + "conversations/send_message";
        JSONObject jsonBody = new JSONObject();
        String fileTitle = null;
        try {
            jsonBody.put("conversation_id", chatId);
            jsonBody.put("sender", senderNumber);
            if (message != null) {
                jsonBody.put("content", message);
            }
            if (base64Image !=null) {
                JSONObject fileBody = new JSONObject();
                fileBody.put("file_name", imageName);
                fileBody.put("content", base64Image);
                fileBody.put("mime_type", "image/jpeg");
                jsonBody.put("file", fileBody);
            } else if (base64Audio !=null) {
                JSONObject fileBody = new JSONObject();
                fileBody.put("file_name", audioName);
                fileBody.put("content", base64Audio);
                fileBody.put("mime_type", "audio/aac");
                jsonBody.put("file", fileBody);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        CustomJsonObjectRequest request = new CustomJsonObjectRequest
                (Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Message message = Message.from_jsonObject(response);
                            listener.messageSent(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        NetworkingManager.getInstance(mContext).addToRequestQueue(request);
    }

    public void getUsersOfConversation(int chatId) {
        String url = NetworkingManager.BASE_URL + "conversations/get_users?conversation_id=" + chatId;
        // GET
    }

    public interface OnMessagesConversationReceive {
        void messagesReceived(ArrayList<Message> messages);
    }

    public void getMessagesOfConversation(int chatId, final OnMessagesConversationReceive listener) {
        String url = NetworkingManager.BASE_URL + "conversations/get_messages?conversation_id=" + chatId;

        CustomJsonArrayRequest request = new CustomJsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            ArrayList<Message> messagesList = Message.from_jsonArray(response);

                            Collections.sort(messagesList, new Comparator<Message>() {
                                @Override
                                public int compare(Message lhs, Message rhs) {
                                    return lhs.mCreatedAt.compareTo(rhs.mCreatedAt);
                                }
                            });
                            listener.messagesReceived(messagesList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        NetworkingManager.getInstance(mContext).addToRequestQueue(request);
    }

    public static synchronized APIManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new APIManager(context);
        }
        return mInstance;
    }
}
