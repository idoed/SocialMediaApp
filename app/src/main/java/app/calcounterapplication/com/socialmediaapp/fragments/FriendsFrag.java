package app.calcounterapplication.com.socialmediaapp.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import app.calcounterapplication.com.socialmediaapp.MainActivity;
import app.calcounterapplication.com.socialmediaapp.R;
import app.calcounterapplication.com.socialmediaapp.model.Friend;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFrag extends Fragment {

    private RecyclerView myFriendList;
    private DatabaseReference mFriendsRef, mUsersRef;
    private FirebaseAuth mAuth;
    private String online_user_id;
    private int  PERSON_PROFILE=10,PERSON_STORIES=11;
    private final String TAG = "FriendsFrag";


    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        myFriendList = view.findViewById(R.id.friend_list);
        myFriendList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myFriendList.setLayoutManager(linearLayoutManager);
        mAuth = FirebaseAuth.getInstance();
        online_user_id = mAuth.getCurrentUser().getUid().toString();
        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(online_user_id);
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        displayAllFriends();


        return view;
    }

    private void displayAllFriends() {
        final FirebaseRecyclerAdapter<Friend, FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Friend, FriendsViewHolder>
                        (
                                Friend.class,
                                R.layout.all_users_display_layout,
                                FriendsViewHolder.class,
                                mFriendsRef
                        ) {
                    @Override
                    protected void populateViewHolder(final FriendsViewHolder viewHolder, Friend model, int position) {
                        viewHolder.setDate(model.getDate());
                        final String user_ids = getRef(position).getKey();
                        mUsersRef.child(user_ids).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    final String userName = dataSnapshot.child("fullName").getValue().toString();
                                    final String profileImage = dataSnapshot.child("profileImage").getValue().toString();
                                    viewHolder.setFullName(userName);
                                    viewHolder.setProfileImage(getContext(), profileImage);
                                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                        //When Pressing on Friends View The user has to choose between those options
                                        @Override
                                        public void onClick(View view) {
                                            CharSequence options[] = new CharSequence[]
                                                    {
                                                            userName + "'s Profile",
                                                            "Check " + userName + "'s Stories"
                                                    };
                                            try {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                                builder.setTitle("Select Option");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                   if(i==0){
                                                       ((MainActivity)getActivity()).receivePersonKey(user_ids);
                                                       ((MainActivity)getActivity()).changeFragment(PERSON_PROFILE);
                                                   }else if(i==1){
                                                       ((MainActivity)getActivity()).receivePersonKey(user_ids);
                                                       ((MainActivity)getActivity()).changeFragment(PERSON_STORIES);


                                                   }
                                                    }
                                                });
                                                builder.show();
                                            } catch (NullPointerException e) {
                                                Log.v(TAG, "No Context for dialog");
                                            }

                                        }
                                    });


                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }
                };
        myFriendList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {
        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setProfileImage(Context context, String profileImage) {
            CircleImageView myImage = mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(context).load(profileImage).placeholder(R.drawable.profile).into(myImage);
        }

        public void setFullName(String fullName) {
            TextView myName = mView.findViewById(R.id.all_users_profile_name);
            myName.setText(fullName);
        }

        public void setDate(String date) {
            TextView myFriendsDate = mView.findViewById(R.id.all_users_status);
            myFriendsDate.setText("Friends Since: " + date);
        }
    }

}
