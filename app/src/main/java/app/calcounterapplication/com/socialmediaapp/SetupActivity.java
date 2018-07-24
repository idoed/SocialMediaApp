package app.calcounterapplication.com.socialmediaapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import app.calcounterapplication.com.socialmediaapp.utils.PermissionManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class SetupActivity extends AppCompatActivity {
    private EditText mUserName, mFullName, mCountry;
    private Button mSaveInfoBut;
    private CircleImageView mProfilePic;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private ProgressDialog mLoadingBar;
    private final int GALLERY_PICK = 1;
    private StorageReference mUserProfileImageRef;
    private PermissionManager permissionManager;
    private int ANSWER=0,YES=1,NO=2,EXTERNAL_STORAGE=20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        permissionManager=new PermissionManager(getBaseContext(),SetupActivity.this);
        mUserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        mUserName = findViewById(R.id.setup_user_name);
        mFullName = findViewById(R.id.setup_fullname);
        mCountry = findViewById(R.id.setup_country_name);
        mSaveInfoBut = findViewById(R.id.setup_save_information);
        mProfilePic = findViewById(R.id.setup_profile_image);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUserId);
        mLoadingBar = new ProgressDialog(this);
        mSaveInfoBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAccountSetupInformation();
            }
        });

        mProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ANSWER=permissionManager.checkPermission(EXTERNAL_STORAGE);
                if(ANSWER==YES) {
                    Intent mGalleryIntent = new Intent();
                    mGalleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    mGalleryIntent.setType("image/*");
                    startActivityForResult(mGalleryIntent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if(dataSnapshot.hasChild("profileImage")) {
                        String image = dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.with(SetupActivity.this).load(image).placeholder(R.drawable.profile).into(mProfilePic);
                    }
                    else {
                        Toast.makeText(SetupActivity.this, "Please Select Profile Image first", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data != null) {


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            Uri imageUri = data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).start(this);
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mLoadingBar.setTitle("Profile Image");
                mLoadingBar.setMessage("Please wait till we are updating your profile image");
                mLoadingBar.setCanceledOnTouchOutside(true);
                mLoadingBar.show();

                // Uri resultUri = result.getUri();
                StorageReference filePath = mUserProfileImageRef.child(mCurrentUserId + ".jpg");
                filePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {

                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            mUserRef.child("profileImage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mLoadingBar.dismiss();
                                    if (task.isSuccessful()) {
                                        Intent mSelfIntent = new Intent(SetupActivity.this, SetupActivity.class);
                                        startActivity(mSelfIntent);


                                    } else {
                                        String message = task.getException().getMessage().toString();
                                        Toast.makeText(SetupActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                                        mLoadingBar.dismiss();
                                    }
                                }
                            });
                        } else {
                            mLoadingBar.dismiss();
                            String message = task.getException().getMessage().toString();
                            Toast.makeText(SetupActivity.this, "Error Occurred: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                Toast.makeText(this, "Error Occurred: Image can't be Cropped try again ", Toast.LENGTH_SHORT).show();
                mLoadingBar.dismiss();
            }
        }
    }

    private void SaveAccountSetupInformation() {
        String userName = mUserName.getText().toString().trim();
        String fullName = mFullName.getText().toString().trim();
        String country = mCountry.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Please write your User Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(this, "Please write your Full Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(country)) {
            Toast.makeText(this, "Please write your Country", Toast.LENGTH_SHORT).show();
        } else {
            mLoadingBar.setTitle("Saving Information!");
            mLoadingBar.setMessage("Please wait till we are saving your details");
            mLoadingBar.show();
            mLoadingBar.setCanceledOnTouchOutside(true);
            HashMap userMap = new HashMap();
            userMap.put("userName", userName);
            userMap.put("fullName", fullName);
            userMap.put("country", country);
            userMap.put("status", "Hey there , i am using ShareStory");
            userMap.put("userType", "Admin");

            mUserRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        mLoadingBar.dismiss();
                        Toast.makeText(SetupActivity.this, "Your Account is created Successfully!", Toast.LENGTH_SHORT).show();
                        SendUserToMainActivity();

                    } else {
                        mLoadingBar.dismiss();
                        String messege = task.getException().toString();
                        Toast.makeText(SetupActivity.this, "Error Occurred: " + messege, Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

    private void SendUserToMainActivity() {
        Intent mainActivityIntent = new Intent(SetupActivity.this, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivityIntent);
        finish();
    }
}
