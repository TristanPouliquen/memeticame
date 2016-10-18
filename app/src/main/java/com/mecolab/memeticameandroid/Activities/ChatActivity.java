package com.mecolab.memeticameandroid.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.mecolab.memeticameandroid.Managers.APIManager;
import com.mecolab.memeticameandroid.Models.Message;
import com.mecolab.memeticameandroid.R;
import com.mecolab.memeticameandroid.Utils.MessageArrayAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    private int mChatId;
    private String mChatTitle;
    private String mSender;
    private ArrayList<Message> mMessageList;
    private ArrayAdapter<Message> mAdapter;
    private ListView mMessageListView;
    private TextView mMessageText;

    String mCurrentPhotoPath;
    Uri mPhotoURI;
    String mImageFileName;

    private static final String LOG_TAG = "AudioRecordTest";
    private static String mAudioFileNameBase = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static String mAudioFileName;

    private MediaRecorder mRecorder = null;
    boolean mStartRecording = true;

    final int SAMPLE_RATE = 44100;
    boolean mShouldContinue;
    ByteArrayOutputStream mAudioBaos;

    void recordAudio() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_AUDIO);

                // buffer size in bytes
                int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT);

                if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) {
                    bufferSize = SAMPLE_RATE * 2;
                }

                short[] audioBuffer = new short[bufferSize / 2];

                AudioRecord record = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                        SAMPLE_RATE,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize);

                if (record.getState() != AudioRecord.STATE_INITIALIZED) {
                    Log.e(LOG_TAG, "Audio Record can't initialize!");
                    return;
                }
                record.startRecording();

                Log.v(LOG_TAG, "Start recording");

                long shortsRead = 0;
                mAudioBaos = new ByteArrayOutputStream(bufferSize);
                while (mShouldContinue) {
                    int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
                    shortsRead += numberOfShort;
                    //mAudioBaos.write(audioBuffer);
                }

                record.stop();
                record.release();

                Log.v(LOG_TAG, String.format("Recording stopped. Samples read: %d", shortsRead));
            }
        }).start();
    }

    private void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    private void startRecording() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mAudioFileName = "/audio_" + timeStamp + ".aac";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setOutputFile(mAudioFileNameBase + mAudioFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        // TODO convert audio to Base64
        APIManager.getInstance(getBaseContext()).sendMessage(mChatId, mSender,null,
                null, null, "", mAudioFileName,
                new APIManager.OnMessageSend(){
                    @Override
                    public void messageSent(Message message) {
                        mMessageList.add(message);
                        mAdapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        mImageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                mImageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mPhotoURI = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    sendImageMessage();
                }
                break;
        }
    }

    public void sendImageMessage() {
        // TODO convert image to Base64
        //Bitmap bm = BitmapFactory.decodeFile(photoURI.toString());
        Bitmap bm = null;
        try {
            bm =MediaStore.Images.Media.getBitmap(getContentResolver(), mPhotoURI);
        } catch(IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        bm.recycle();
        bm = null;

        byte[] b = baos.toByteArray();
        String b64= Base64.encodeToString(b, Base64.DEFAULT);
        APIManager.getInstance(getBaseContext()).sendMessage(mChatId, mSender,null,
                b64, mImageFileName + ".jpg", null, null,
                new APIManager.OnMessageSend(){
                    @Override
                    public void messageSent(Message message) {
                        mMessageList.add(message);
                        mAdapter.notifyDataSetChanged();
                        scrollToBottom();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Bundle b = getIntent().getExtras();
        if (b!= null) {
            mChatId = b.getInt("chatId");
            mChatTitle = b.getString("chatTitle");
            setTitle(mChatTitle);
            // TODO pass sender number in bundle
            mSender = "56995011589";
        }

        mMessageListView = (ListView) findViewById(R.id.conversation_messages);
        mMessageText = (TextView) findViewById(R.id.send_message);

        APIManager.getInstance(this).getMessagesOfConversation(mChatId, new APIManager.OnMessagesConversationReceive() {
            @Override
            public void messagesReceived(ArrayList<Message> messages) {
                mMessageList = messages;
                mAdapter = new MessageArrayAdapter(
                        ChatActivity.this,
                        R.layout.message_list_item,
                        "56995011589", //TODO dynamise current_user
                        mMessageList
                );

                mMessageListView.setAdapter(mAdapter);
                scrollToBottom();
            }
        });

        ImageButton photo = (ImageButton)findViewById(R.id.action_take_picture);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        final ImageButton audio = (ImageButton)findViewById(R.id.action_grab_audio);
        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    audio.setImageResource(R.drawable.ic_mic_off_black_24dp);
                } else {
                    audio.setImageResource(R.drawable.ic_mic_black_24dp);
                }
                mStartRecording = !mStartRecording;
            }
        });
        ImageButton submit = (ImageButton)findViewById(R.id.action_send_message);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessageText.getText().toString();
                //TODO add photo/audio management
                //File sound = new File(mAudioFileName);
                //String soundName = mAudioFileName;
                if (message.equals("")) {
                    return;
                }
                mMessageText.setText("");
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                Date date = new Date();
                Message messageInstance = new Message(0, message, "56995011589", mChatId, dateFormat.format(date), null);
                //mMessageList.add(messageInstance);
                //mAdapter.notifyDataSetChanged();
                //scrollToBottom();

                APIManager.getInstance(getBaseContext()).sendMessage(mChatId, mSender,message,
                        null, null, null, null,
                        new APIManager.OnMessageSend(){
                    @Override
                    public void messageSent(final Message message) {
                        ChatActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mMessageList.add(message);
                                mAdapter.notifyDataSetChanged();
                                scrollToBottom();
                            }
                        });
                    }
                });
            }
        });
    }

    public void scrollToBottom() {
        mMessageListView.setSelection(mAdapter.getCount() -1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
