package app.calcounterapplication.com.socialmediaapp.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.VideoView;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import app.calcounterapplication.com.socialmediaapp.R;
import app.calcounterapplication.com.socialmediaapp.utils.DateFormat;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
//Fragment that handles the new story by recording later Edit him and than upload it to the server.

public class AddStoryFragm extends Fragment {

    private static final String TAG = "AddStoryFragment";
    private static final int REQUEST_CODE = 101;
    private Uri videoUri;
    private StorageReference mStoryRef;
    private DateFormat mCurrentTimeAndDate;
    private String downloadUrl,current_user_id;
    private DatabaseReference userRef,storyRef;
    private FirebaseAuth mAuth;
    private ProgressDialog mLoadingBar;
    private long countStories=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_story, container, false);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE);
        mStoryRef = FirebaseStorage.getInstance().getReference();
        mCurrentTimeAndDate = new DateFormat();
        mLoadingBar = new ProgressDialog(getContext());
        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users");
        storyRef= FirebaseDatabase.getInstance().getReference().child("Stories");

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        videoUri = data.getData();
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (videoUri != null) {
                    mLoadingBar.setTitle("Hold On!");
                    mLoadingBar.setMessage("Please wait while your Story is being Update..");
                    mLoadingBar.show();
                    mLoadingBar.setCanceledOnTouchOutside(true);

                    storingStoryToFireBaseStorage();
                } else {
                    Toast.makeText(getContext(), "Please Record video", Toast.LENGTH_SHORT).show();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.v(TAG, "Video Recording Canceled");
            } else {
                Log.v(TAG, "Failed to Record Video");
            }
        }
    }

    private void storingStoryToFireBaseStorage() {
        //initiallize the Date Format Class (mRandomName) to save the story on the currentTime with unique name;
        StorageReference mFilePath = mStoryRef.child("Stories").child(videoUri.getLastPathSegment() + mCurrentTimeAndDate.getCurrentStoryDate() + ".3gp");
        mFilePath.putFile(videoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
               if(task.isSuccessful()){
                   downloadUrl=task.getResult().getDownloadUrl().toString();
                   Log.v(TAG,"Video Successfully upload to FireBase storage");
                   savingStoryInformationForUser();
               }else{
                   String massage=task.getException().getMessage();
                   mLoadingBar.dismiss();
                   Toast.makeText(getContext(), "Error Occurred: "+massage, Toast.LENGTH_SHORT).show();
               }
            }
        });

    }

    private void savingStoryInformationForUser() {
        storyRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                countStories=dataSnapshot.getChildrenCount();
            }else{
                countStories=0;
            }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        userRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String userFullName=dataSnapshot.child("fullName").getValue().toString();
                    String userpProfileImage=dataSnapshot.child("profileImage").getValue().toString();
                    HashMap storiesMap=new HashMap();
                    storiesMap.put("counter",countStories);
                    storiesMap.put("uid",current_user_id);
                    storiesMap.put("date",mCurrentTimeAndDate.getCurrentDate());
                    storiesMap.put("time",mCurrentTimeAndDate.getCurrentTime());
                    storiesMap.put("story",downloadUrl);
                    storiesMap.put("fullName",userFullName);
                    storyRef.child(current_user_id+mCurrentTimeAndDate.getCurrentStoryDate()).updateChildren(storiesMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if(task.isSuccessful()){
                                /**
                                 * TODO: Send the user to Home Activity where he can see his story.
                                 */
                                Log.v(TAG,"Stories Upload to Database successfully");
                                mLoadingBar.dismiss();

                            }else{
                                String massage=task.getException().getMessage().toString();
                                Toast.makeText(getContext(), "Error Occurred:"+massage, Toast.LENGTH_SHORT).show();
                                mLoadingBar.dismiss();
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
}
