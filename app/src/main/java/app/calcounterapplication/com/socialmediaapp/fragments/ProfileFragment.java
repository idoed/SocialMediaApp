package app.calcounterapplication.com.socialmediaapp.fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import app.calcounterapplication.com.socialmediaapp.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private TextView mUserName, mUserProfName, mUserStatus, mUserCountry, mUserGender, mUserRelation, mUserDOB;
    private CircleImageView mProfilePic;
    private DatabaseReference mProfileUserRef;
    private FirebaseAuth mAuth;
    private String current_user_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        mUserName = view.findViewById(R.id.my_user_name);
        mUserProfName = view.findViewById(R.id.my_profile_full_name);
        mUserStatus = view.findViewById(R.id.my_profile_status);
        mUserCountry = view.findViewById(R.id.my_country);
        mUserGender = view.findViewById(R.id.my_gender);
        mUserRelation = view.findViewById(R.id.my_relation_status);
        mUserDOB = view.findViewById(R.id.my_dob);
        mProfilePic=view.findViewById(R.id.my_profile_pic);
        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid().toString();
        mProfileUserRef= FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mProfileUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String myProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String myUserName = dataSnapshot.child("userName").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullName").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    Picasso.with(getContext()).load(myProfileImage).placeholder(R.drawable.profile).into(mProfilePic);
                    try{
                        String myGender=dataSnapshot.child("gender").getValue().toString();
                        mUserGender.setText("Gender: "+myGender);
                    }catch (NullPointerException e){

                    }
                    try{
                        String myDOB=dataSnapshot.child("dateofbirth").getValue().toString();
                        mUserDOB.setText("Date Of Birth: "+myDOB);
                    }catch (NullPointerException e){

                    }
                    try{
                        String myrelation=dataSnapshot.child("relationStatus").getValue().toString();
                        mUserRelation.setText("Relationship: "+myrelation);
                    }catch (NullPointerException e){

                    }
                    mUserName.setText("@"+myUserName);
                    mUserStatus.setText(myStatus);
                    mUserCountry.setText("Country: "+myCountry);
                    mUserProfName.setText(myProfileName);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return view;
    }


}
