package com.mecolab.memeticameandroid.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mecolab.memeticameandroid.Models.Conversation;
import com.mecolab.memeticameandroid.Models.Message;
import com.mecolab.memeticameandroid.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by tristan on 14-09-16.
 */
public class MessageArrayAdapter extends ArrayAdapter<Message> {

    Context context;
    int layoutResourceId;
    ArrayList<Message> data;
    String currentUser;

    public MessageArrayAdapter (Context context, int layoutResourceId, String currentUser, ArrayList<Message> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        this.currentUser = currentUser;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MessageHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new MessageHolder();
            holder.txtContent = (TextView)row.findViewById(R.id.message_content);
            holder.txtSender = (TextView)row.findViewById(R.id.message_sender);
            holder.txtCreatedAt = (TextView)row.findViewById(R.id.message_created_at);

            row.setTag(holder);
        }
        else
        {
            holder = (MessageHolder)row.getTag();
        }

        Message message = data.get(position);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm -- dd/MM/yyyy");
        holder.mMessageId = message.mMessageId;
        if (!message.mContent.equals("null")) {
            holder.txtContent.setText(message.mContent);
        } else if (message.mFileUrl != null) {
            holder.txtContent.setText(message.mFileUrl);
        }
        holder.txtSender.setText((message.mSender));
        holder.txtCreatedAt.setText(dateFormat.format(message.mCreatedAt));

        if (message.mSender.equals(currentUser)) {
            holder.txtContent.setGravity(Gravity.RIGHT);
        } else {
            holder.txtContent.setGravity(Gravity.LEFT);
        }

        return row;
    }

    public static class MessageHolder
    {
        public int mMessageId;
        TextView txtContent;
        TextView txtSender;
        TextView txtCreatedAt;
    }
}

