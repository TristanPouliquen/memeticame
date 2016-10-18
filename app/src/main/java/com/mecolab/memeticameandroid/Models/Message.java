package com.mecolab.memeticameandroid.Models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by tristan on 15-09-16.
 */
@Table(name="Messages")
public class Message extends Model {
    @Column(name="MessageId")
    public int mMessageId;
    @Column(name="Content")
    public String mContent;
    @Column(name="FileUrl")
    public String mFileUrl;
    @Column(name="Sender")
    public String mSender;
    @Column(name="ConversationId")
    public int mConversationId;
    @Column(name="CreatedAt")
    public Date mCreatedAt;

    public Message() {
        super();
    }

    public Message(int messageId, String content, String sender, int conversationId,
                   String createdAt, MemeFile file) {
        mMessageId = messageId;
        mContent = content;
        mSender = sender;
        mConversationId = conversationId;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            mCreatedAt = dateFormat.parse(createdAt);
        } catch (ParseException e){
            mCreatedAt = new Date();
        }
        mFileUrl = file != null ? file.mUrl : null;
    }

    public static ArrayList<Message> from_jsonArray(JSONArray jsonArray)  throws JSONException{
        ArrayList<Message> messagesList = new ArrayList<Message>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            Message message = Message.from_jsonObject(jsonObject);
            messagesList.add(message);
        }
        return messagesList;
    }

    public static Message from_jsonObject(JSONObject jsonObject) throws JSONException {
        MemeFile file = null;
        if (!jsonObject.isNull("file")) {
             file = MemeFile.from_JsonObject(jsonObject.getJSONObject("file"));
        }

        Message message = new Select().from(Message.class)
                    .where("MessageId = ?", jsonObject.getInt("id"))
                    .executeSingle();
        if (message == null) {
            message = new Message(
                    jsonObject.getInt("id"),
                    jsonObject.getString("content"),
                    jsonObject.getString("sender"),
                    jsonObject.getInt("conversation_id"),
                    jsonObject.getString("created_at"),
                    file
            );
            message.save();
        }
        return message;
    }
}
