package com.mecolab.memeticameandroid.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.mecolab.memeticameandroid.Managers.APIManager;
import com.mecolab.memeticameandroid.Models.Conversation;
import com.mecolab.memeticameandroid.R;

import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import java.util.ArrayList;

public class CreateChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MaterialMenuInflater.with(this).setDefaultColor(Color.WHITE)
                .inflate(R.menu.menu_create, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_create_chat) {
            TextView chatTitleView = (TextView) findViewById(R.id.new_chat_title);
            String chatTitle = chatTitleView.getText().toString();
            String admin = "56995011589";
            ArrayList<String> users = new ArrayList<>();
            //TODO dynamise users
            users.add("99731378");
            APIManager.getInstance(this).createConversation(chatTitle, admin, users, new APIManager.OnConversationCreate() {
                @Override
                public void conversationCreated(Conversation conversation) {
                    if (conversation != null) {
                        Bundle b = new Bundle();
                        b.putInt("chatId", conversation.mChatId);
                        Intent intent = new Intent(CreateChatActivity.this, ChatActivity.class);
                        intent.putExtras(b);
                        startActivity(intent);
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
