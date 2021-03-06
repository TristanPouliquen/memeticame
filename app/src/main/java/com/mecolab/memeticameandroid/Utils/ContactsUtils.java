package com.mecolab.memeticameandroid.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import java.lang.reflect.Array;
import java.util.ArrayList;

import com.mecolab.memeticameandroid.Managers.APIManager;
import com.mecolab.memeticameandroid.Models.User;

/**
 * Created by Andres Matte on 8/10/2016.
 */
public class ContactsUtils {
    public static void getContacts(Context context, ContactsProviderListener listener){
        new GetContactsTask(context, listener).execute();
    }

    public interface ContactsProviderListener{
        void OnContactsReady(ArrayList<User> users);
    }

    /**
     * AsyncTask enables proper and easy use of the UI thread. This class allows to perform
     * background operations and publish results on the UI thread without having to manipulate
     * threads and/or handlers.
     * See <a href="https://developer.android.com/reference/android/os/AsyncTask.html">AsyncTask</a>
     */
    private static class GetContactsTask extends AsyncTask<String, Void, ArrayList<User>> {

        private Context context;
        private ContactsProviderListener listener;
        private ContentResolver mResolver;

        public GetContactsTask(Context context, ContactsProviderListener listener){
            super();
            this.context = context;
            this.listener = listener;
            this.mResolver = context.getContentResolver();
        }

        @Override
        protected ArrayList<User> doInBackground(String... params) {
            //return getPhoneContacts(context);
            return getRegisteredContacts();
        }

        @Override
        protected void onPostExecute(ArrayList<User> users) {
            listener.OnContactsReady(users);
        }

        public ArrayList<User> getRegisteredContacts() {
            final ArrayList<User> filteredContacts = new ArrayList<User>();
            APIManager.getInstance(context).getUsers(new APIManager.OnUsersReceive() {
                @Override
                public void usersReceived(ArrayList<User> backEndUsers) {
                    filteredContacts.addAll(backEndUsers);
                }
            });

            return filteredContacts;
        }

        public ArrayList<User> getPhoneContacts(Context context) {
            if (mResolver == null) mResolver = context.getContentResolver();
            ArrayList<User> contacts = new ArrayList<>();
            Cursor cursor = mResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if(cursor != null && cursor.getCount() != 0 && cursor.moveToFirst()) {
                do {
                    User contact = getContact(cursor);
                    if (contact != null) {
                        contacts.add(contact);
                    }
                } while (cursor.moveToNext()) ;
                cursor.close();
            }
            return contacts;
        }

        private User getContact(Cursor cursor) {
            User contact = null;
            String id = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID));
            if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(
                    ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                Cursor c = mResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        new String[]{id},
                        null);
                if(c != null && c.getCount() != 0 && c.moveToFirst()) {
                    String phoneNumber = c.getString(c.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String name = c.getString(c.getColumnIndex(
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    contact = new User(name, phoneNumber);
                    c.close();
                }
            }
            return contact;
        }
    }
}

