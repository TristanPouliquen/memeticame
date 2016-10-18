package com.mecolab.memeticameandroid.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mecolab.memeticameandroid.Activities.ChatActivity;
import com.mecolab.memeticameandroid.Managers.APIManager;
import com.mecolab.memeticameandroid.Models.Conversation;
import com.mecolab.memeticameandroid.R;
import com.mecolab.memeticameandroid.Utils.ConversationArrayAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConversationsFragment.OnConversationSelected} interface
 * to handle interaction events.
 * Use the {@link ConversationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversationsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PHONE_NUMBER = "56995011589";

    // TODO: Rename and change types of parameters
    private String mPhoneNumber;

    private OnConversationSelected mListener;

    public ConversationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ConversationsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConversationsFragment newInstance() {
        ConversationsFragment fragment = new ConversationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPhoneNumber = getArguments().getString(PHONE_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        final ListView conversationListView = (ListView) view.findViewById(R.id.conversation_list);

        APIManager.getInstance(getContext()).getConversationsOfUser("56995011589", new APIManager.OnConversationReceive() {

            @Override
            public void conversationReceive(ArrayList<Conversation> conversations) {
                ConversationArrayAdapter conversationArrayAdapter = new ConversationArrayAdapter(
                        getContext(),
                        R.layout.conversation_list_item,
                        conversations
                );
                conversationListView.setAdapter(conversationArrayAdapter);
            }
        });

        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Conversation conversation = (Conversation)parent.getAdapter().getItem(position);
                Bundle b = new Bundle();
                b.putInt("chatId",conversation.mChatId);
                b.putString("chatTitle", conversation.mTitle);
                Intent chatActivityIntent = new Intent(getContext(), ChatActivity.class);
                chatActivityIntent.putExtras(b);
                startActivity(chatActivityIntent);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    //public void onButtonPressed(Uri uri) {
    //    if (mListener != null) {
    //        mListener.onConversationSelected(uri);
    //    }
    //}

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnConversationSelected) {
            mListener = (OnConversationSelected) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnConversationSelected {
        // TODO: Update argument type and name
        void onConversationSelected(Conversation conversatio);
    }
}
