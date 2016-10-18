package com.mecolab.memeticameandroid.Utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mecolab.memeticameandroid.Models.Conversation;
import com.mecolab.memeticameandroid.R;

import java.util.ArrayList;

/**
 * Created by tristan on 14-09-16.
 */
public class ConversationArrayAdapter extends ArrayAdapter<Conversation> {

    Context context;
    int layoutResourceId;
    ArrayList<Conversation> data;

    public ConversationArrayAdapter (Context context, int layoutResourceId, ArrayList<Conversation> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ConversationHolder holder = null;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ConversationHolder();
            holder.txtTitle = (TextView)row.findViewById(R.id.conversation_title);

            row.setTag(holder);
        }
        else
        {
            holder = (ConversationHolder)row.getTag();
        }

        Conversation conversation = data.get(position);
        holder.txtTitle.setText(conversation.mTitle);
        holder.mConversationId = conversation.mChatId;

        return row;
    }

    public static class ConversationHolder
    {
        TextView txtTitle;
        public int  mConversationId;

        public int getConversationId() {
            return mConversationId;
        }
    }
}
