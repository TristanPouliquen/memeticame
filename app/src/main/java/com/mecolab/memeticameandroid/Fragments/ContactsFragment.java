package com.mecolab.memeticameandroid.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import com.mecolab.memeticameandroid.Activities.ChatActivity;
import com.mecolab.memeticameandroid.Activities.CreateChatActivity;
import com.mecolab.memeticameandroid.Activities.MainActivity;
import com.mecolab.memeticameandroid.Managers.APIManager;
import com.mecolab.memeticameandroid.Models.Conversation;
import com.mecolab.memeticameandroid.Models.User;
import com.mecolab.memeticameandroid.R;
import com.mecolab.memeticameandroid.Utils.ContactsUtils;
import com.mecolab.memeticameandroid.Views.ContactsAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {

    public static final String TAG = "contacts_fragment";
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 101;

    private ArrayList<User> mContacts;
    private OnContactSelected mListener;
    private ContactsAdapter mAdapter;
    private ListView mContactsListView;

    public interface OnContactSelected {
        void onContactSelected(User user);
    }

    public ContactsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        mContactsListView = (ListView) view.findViewById(R.id.contacts_list_view);
        mContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onContactSelected(mContacts.get(position));
            }
        });

        showContacts();

        mContactsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User contact = (User)parent.getAdapter().getItem(position);
                ArrayList<String> users = new ArrayList<>();
                users.add(contact.mPhoneNumber);
                APIManager.getInstance(getContext()).createConversation(
                        contact.mName,
                        "56995011589",
                        users,
                        new APIManager.OnConversationCreate() {
                            @Override
                            public void conversationCreated(Conversation conversation) {
                                if (conversation != null) {
                                    Bundle b = new Bundle();
                                    b.putInt("chatId", conversation.mChatId);
                                    b.putString("chatTitle", conversation.mTitle);
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtras(b);
                                    startActivity(intent);
                                }
                            }
                        }
                );
            }
        });

        setHasOptionsMenu(true);

        return view;
    }

    /**
     * Checks if the app has permission to read phone contacts.
     * Only for SDK > 23.
     */
    private boolean hasContactsPermissions() {
        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED;
    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && hasContactsPermissions()) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            ContactsUtils.getContacts(getContext(), new ContactsUtils.ContactsProviderListener() {
                @Override
                public void OnContactsReady(ArrayList<User> contacts) {
                    mContacts = contacts;
                    mAdapter = new ContactsAdapter(getContext(), R.layout.contact_list_item, mContacts);
                    mContactsListView.setAdapter(mAdapter);
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showContacts();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contacts_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_add_contact){
            Intent intent = new Intent(Intent.ACTION_INSERT,ContactsContract.Contacts.CONTENT_URI);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnContactSelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onViewSelected");
        }
    }
}
