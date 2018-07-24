package app.calcounterapplication.com.socialmediaapp.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import app.calcounterapplication.com.socialmediaapp.MainActivity;
import app.calcounterapplication.com.socialmediaapp.R;
import app.calcounterapplication.com.socialmediaapp.SetupActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class SettingFragment extends Fragment {
    private Toolbar mToolBar;
    private EditText mUserName, mUserProfName, mUserStatus, mUserCountry, mUserGender, mUserRelation, mUserDOB;
    private Button mUpdateButton;
    private CircleImageView mUserProfilePic;
    private DatabaseReference mSettingsUserRef;
    private FirebaseAuth mAuth;
    private String current_user_id;
    private final int GALLERY_PICK = 1;
    private ProgressDialog mLoadingBar;
    private StorageReference mUserProfileImageRef;
    private final String TAG = "SettingsFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        mUserName = view.findViewById(R.id.settings_user_name);
        mUserProfName = view.findViewById(R.id.settings_profile_full_name);
        mUserStatus = view.findViewById(R.id.settings_status);
        mUserCountry = view.findViewById(R.id.settings_country);
        mUserGender = view.findViewById(R.id.settings_gender);
        mUserRelation = view.findViewById(R.id.settings_relationship_status);
        mUserDOB = view.findViewById(R.id.setting_dob);
        mUpdateButton = view.findViewById(R.id.settings_update_details_but);
        mUserProfilePic = view.findViewById(R.id.setting_profile_image);
        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        mLoadingBar = new ProgressDialog(getContext());
        mSettingsUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        mUserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        mUserProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mGalleryIntent = new Intent();
                mGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                mGalleryIntent.setType("image/*");
                startActivityForResult(mGalleryIntent, GALLERY_PICK);
            }
        });
        mSettingsUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String myProfileImage = dataSnapshot.child("profileImage").getValue().toString();
                    String myUserName = dataSnapshot.child("userName").getValue().toString();
                    String myStatus = dataSnapshot.child("status").getValue().toString();
                    String myProfileName = dataSnapshot.child("fullName").getValue().toString();
                    String myCountry = dataSnapshot.child("country").getValue().toString();
                    Picasso.with(getContext()).load(myProfileImage).placeholder(R.drawable.profile).into(mUserProfilePic);
                    mUserName.setText(myUserName);
                    mUserStatus.setText(myStatus);
                    mUserCountry.setText(myCountry);
                    mUserProfName.setText(myProfileName);
                    try{
                        String myGender=dataSnapshot.child("gender").getValue().toString();
                        mUserGender.setText(myGender);
                    }catch (NullPointerException e){

                    }
                    try{
                        String myDOB=dataSnapshot.child("dateofbirth").getValue().toString();
                        mUserDOB.setText(myDOB);
                    }catch (NullPointerException e){

                    }
                    try{
                        String myrelation=dataSnapshot.child("relationStatus").getValue().toString();
                        mUserRelation.setText(myrelation);
                    }catch (NullPointerException e){

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAccountInfo();
            }
        });

        return view;
    }

    private void validateAccountInfo() {
        String username=mUserName.getText().toString();
        String fullname=mUserProfName.getText().toString();
        String country=mUserCountry.getText().toString();
        String gender=mUserGender.getText().toString();
        String dateofbirth=mUserDOB.getText().toString();
        String relation=mUserRelation.getText().toString();
        String status=mUserStatus.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(getContext(), "Please write your User Name..!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(fullname)){
            Toast.makeText(getContext(), "Please write your Full Name..!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(country)){
            Toast.makeText(getContext(), "Please write your Country..!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(gender)){
            Toast.makeText(getContext(), "Please write your Gender..!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(dateofbirth)){
            Toast.makeText(getContext(), "Please write your Date of Birth..!", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(relation)){
            Toast.makeText(getContext(), "Please write your Relationship status..!", Toast.LENGTH_SHORT).show();
        }
       else if(TextUtils.isEmpty(status)){
            Toast.makeText(getContext(), "Please write your status..!", Toast.LENGTH_SHORT).show();
        }
        else{
            mLoadingBar.setTitle("Profile Image");
            mLoadingBar.setMessage("Please wait till we are updating your profile image");
            mLoadingBar.setCanceledOnTouchOutside(true);
            mLoadingBar.show();
           updateAccountInformation(username,fullname,country,gender,dateofbirth,relation,status);
        }


    }

    private void updateAccountInformation(String username, String fullname, String country, String gender, String dateofbirth, String relation, String status) {
        HashMap userMap=new HashMap();
        userMap.put("userName",username);
        userMap.put("country",country);
        userMap.put("fullName",fullname);
        userMap.put("status",status);
        userMap.put("gender",gender);
        userMap.put("dateofbirth",dateofbirth);
        userMap.put("relationStatus",relation);
        mSettingsUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    mLoadingBar.dismiss();
                    Toast.makeText(getContext(), "Account Settings Updated Successfully", Toast.LENGTH_SHORT).show();

                }else{
                    mLoadingBar.dismiss();
                    Toast.makeText(getContext(), "Error Occurred, While updating the account information", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(getActivity());

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mLoadingBar.setTitle("Profile Image");
                mLoadingBar.setMessage("Please wait till we are updating your profile image");
                mLoadingBar.setCanceledOnTouchOutside(true);
                mLoadingBar.show();

                Uri resultUri = result.getUri();
                StorageReference filePath = mUserProfileImageRef.child(current_user_id + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            mSettingsUserRef.child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mLoadingBar.dismiss();
                                    if (task.isSuccessful()) {
                                        Intent mSelfIntent = new Intent(getContext(), MainActivity.class);
                                        startActivity(mSelfIntent);


                                    } else {
                                        String message = task.getException().getMessage().toString();
                                        Toast.makeText(getContext(), "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                                        mLoadingBar.dismiss();
                                    }
                                }
                            });
                        } else {
                            mLoadingBar.dismiss();
                            String message = task.getException().getMessage().toString();
                            Toast.makeText(getContext(), "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Toast.makeText(getContext(), "Error Occurred: Image can't be Cropped try again ", Toast.LENGTH_SHORT).show();
                mLoadingBar.dismiss();
            }

        }


    }
}
