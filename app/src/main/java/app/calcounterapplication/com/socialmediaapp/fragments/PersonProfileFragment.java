package app.calcounterapplication.com.socialmediaapp.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import app.calcounterapplication.com.socialmediaapp.MainActivity;
import app.calcounterapplication.com.socialmediaapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonProfileFragment extends Fragment {

    private TextView mUserName, mUserProfName, mUserStatus, mUserCountry, mUserGender, mUserRelation, mUserDOB;
    private CircleImageView mProfilePic;
    private Button mSendFriendRequest, mDeclineFriendRequest;
    private DatabaseReference mProfileUserRef, mUserRef, mFriendRequestRef, mFriendRef;
    private FirebaseAuth mAuth;
    private String senderUserId, receiverUserId, CURRENT_STATE, saveCurrentDate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person_profile, container, false);
        receiverUserId = ((MainActivity) getActivity()).sendingPersonKey();
        mAuth = FirebaseAuth.getInstance();
        mFriendRequestRef = FirebaseDatabase.getInstance().getReference().child("FriendRequests");
        senderUserId = mAuth.getCurrentUser().getUid().toString();
        mProfileUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        initializeFields(view);
        mProfileUserRef.child(receiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String myUserName = dataSnapshot.child("userName").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullName").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    Picasso.with(getContext()).load(myProfileImage).placeholder(R.drawable.profile).into(mProfilePic);
                    try {
                        String myGender = dataSnapshot.child("gender").getValue().toString();
                        mUserGender.setText("Gender: " + myGender);
                    } catch (NullPointerException e) {

                    }
                    try {
                        String myDOB = dataSnapshot.child("dateofbirth").getValue().toString();
                        mUserDOB.setText("Date Of Birth: " + myDOB);
                    } catch (NullPointerException e) {

                    }
                    try {
                        String myrelation = dataSnapshot.child("relationStatus").getValue().toString();
                        mUserRelation.setText("Relationship: " + myrelation);
                    } catch (NullPointerException e) {

                    }
                    mUserName.setText("@" + myUserName);
                    mUserStatus.setText(myStatus);
                    mUserCountry.setText("Country: " + myCountry);
                    mUserProfName.setText(myProfileName);

                    maintananceOfButtons();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDeclineFriendRequest.setVisibility(View.INVISIBLE);
        mDeclineFriendRequest.setEnabled(false);

        if (!senderUserId.equals(receiverUserId)) {
            mSendFriendRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSendFriendRequest.setEnabled(false);
                    if (CURRENT_STATE.equals("not_friends")) {
                        sendFriendRequestToUser();
                    }
                    if (CURRENT_STATE.equals("request_sent")) {
                        cancelFriendRequestToUser();
                    }
                    if (CURRENT_STATE.equals("request_received")) {
                        acceptFriendRequest();
                    }
                    if(CURRENT_STATE.equals("friends")){
                        existingFriendOptions();
                    }
                }
            });
        } else {
            mDeclineFriendRequest.setVisibility(View.INVISIBLE);
            mSendFriendRequest.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    private void existingFriendOptions() {
        mFriendRef.child(senderUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFriendRef.child(receiverUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mSendFriendRequest.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                mSendFriendRequest.setText("Friend Request");
                                mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                mDeclineFriendRequest.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void acceptFriendRequest() {
        Calendar calendarForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yyyy");
        saveCurrentDate = currentDate.format(calendarForDate.getTime());
        mFriendRef.child(senderUserId).child(receiverUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFriendRef.child(receiverUserId).child(senderUserId).child("date").setValue(saveCurrentDate).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mFriendRequestRef.child(senderUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            mFriendRequestRef.child(receiverUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        mSendFriendRequest.setEnabled(true);

                                                        CURRENT_STATE = "friends";
                                                        mSendFriendRequest.setText("Unfriend");
                                                        mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                                        mDeclineFriendRequest.setEnabled(false);
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });

                }
            }
        });


    }

    private void cancelFriendRequestToUser() {
        mFriendRequestRef.child(senderUserId).child(receiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFriendRequestRef.child(receiverUserId).child(senderUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mSendFriendRequest.setEnabled(true);
                                CURRENT_STATE = "not_friends";
                                mSendFriendRequest.setText("Friend Request");
                                mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                mDeclineFriendRequest.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }

    private void maintananceOfButtons() {
        mFriendRequestRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(receiverUserId)) {
                    String request_type = dataSnapshot.child(receiverUserId).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        CURRENT_STATE = "request_sent";
                        mSendFriendRequest.setText("Pending..");
                        mDeclineFriendRequest.setEnabled(false);
                        mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                    } else if (request_type.equals("received")) {
                        CURRENT_STATE = "request_received";
                        mSendFriendRequest.setText("Accept");
                        mDeclineFriendRequest.setVisibility(View.VISIBLE);
                        mDeclineFriendRequest.setEnabled(true);
                        mDeclineFriendRequest.setText("Decline");
                        mDeclineFriendRequest.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                cancelFriendRequestToUser();
                            }
                        });

                    }
                }
                else {
                    mFriendRef.child(senderUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(receiverUserId)){
                                CURRENT_STATE="friends";
                                mSendFriendRequest.setText("Unfriend");

                                mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                mDeclineFriendRequest.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sendFriendRequestToUser() {
        mFriendRequestRef.child(senderUserId).child(receiverUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mFriendRequestRef.child(receiverUserId).child(senderUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                mSendFriendRequest.setEnabled(true);
                                CURRENT_STATE = "request_sent";
                                mSendFriendRequest.setText("Pending...");
                                mDeclineFriendRequest.setVisibility(View.INVISIBLE);
                                mDeclineFriendRequest.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void initializeFields(View view) {

        mUserName = view.findViewById(R.id.person_user_name);
        mUserProfName = view.findViewById(R.id.person_profile_full_name);
        mUserStatus = view.findViewById(R.id.person_profile_status);
        mUserCountry = view.findViewById(R.id.person_country);
        mUserGender = view.findViewById(R.id.person_gender);
        mUserRelation = view.findViewById(R.id.person_relation_status);
        mUserDOB = view.findViewById(R.id.person_dob);
        mProfilePic = view.findViewById(R.id.person_profile_pic);
        mSendFriendRequest = view.findViewById(R.id.person_send_friend_request_but);
        mDeclineFriendRequest = view.findViewById(R.id.person_decline_send_friend_request_but);
        CURRENT_STATE = "not_friends";


    }

}
