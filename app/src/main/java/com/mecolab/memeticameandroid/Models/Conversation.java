package com.mecolab.memeticameandroid.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tristan on 14-09-16.
 */
@Table(name="Conversations")
public class Conversation extends Model {
    @Column(name = "Title")
    public String mTitle;
    @Column(name="ConversationId")
    public int mChatId;
    @Column(name="IsGroup")
    public boolean mGroup;
    @Column(name="Admin")
    public String mAdmin;
    @Column(name="Users")
    public ArrayList<User> mUsers;

    public Conversation() {
        super();
    }

    public Conversation(int chatId, String title, String admin, ArrayList<User> users, boolean group) {
        mTitle = title;
        mChatId = chatId;
        mAdmin = admin;
        mUsers = users;
        mGroup = group;
    }

    public static Conversation from_jsonObject(JSONObject jsonobject) throws JSONException {

        ArrayList<User> usersList = User.from_jsonArray(jsonobject.getJSONArray("users"));

        Conversation conversation = new Select().from(Conversation.class)
                    .where("ConversationId = ?", jsonobject.getInt("id"))
                    .executeSingle();
        if (conversation == null) {
            conversation = new Conversation(
                    jsonobject.getInt("id"),
                    jsonobject.getString("title"),
                    jsonobject.getString("admin"),
                    usersList,
                    jsonobject.getBoolean("group")
            );
            conversation.save();
        }

        return conversation;
    }

    public static ArrayList<Conversation> from_jsonArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Conversation> conversationsList = new ArrayList<Conversation>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Conversation conversation = Conversation.from_jsonObject(jsonObject);
            conversationsList.add(conversation);
        }

        return conversationsList;
    }
}
