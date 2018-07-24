package app.calcounterapplication.com.socialmediaapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import app.calcounterapplication.com.socialmediaapp.fragments.AddStoryFragm;
import app.calcounterapplication.com.socialmediaapp.fragments.FindFriendsFragment;
import app.calcounterapplication.com.socialmediaapp.fragments.FriendsFrag;
import app.calcounterapplication.com.socialmediaapp.fragments.HomePageFragment;
import app.calcounterapplication.com.socialmediaapp.fragments.PersonProfileFragment;
import app.calcounterapplication.com.socialmediaapp.fragments.PersonStoriesFragment;
import app.calcounterapplication.com.socialmediaapp.fragments.ProfileFragment;
import app.calcounterapplication.com.socialmediaapp.fragments.SettingFragment;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private RecyclerView postlist;
    private android.support.v7.widget.Toolbar mToolbar;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;
    private CircleImageView mNavProfileImage;
    private TextView mNavProfileuUserName;
    private String mCurrentUserId,mCurrentKey;
    private ImageButton mAddNewStory;
    private final String TAG = "MainActivity";
    private final int NEW_STORY_FRAGMENT = 1, HOME_PAGE_FRAGMENT = 2, SETTINGS_FRAGMENT = 5,PROFILE_FRAGMENT=6,FIND_FRIENDS=4,PERSON_PROFILE=10,MY_FRIENDS=7,PERSON_STORIES=11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.v(TAG, "");
        mAuth = FirebaseAuth.getInstance();
        try {
            mCurrentUserId = mAuth.getCurrentUser().getUid();
        } catch (Exception e) {
            onStart();
        }
;
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mToolbar = findViewById(R.id.main_page_toolBar);
        mAddNewStory = findViewById(R.id.add_new_story_button);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, drawerLayout, R.string.drawer_open, R.string.drawer_close);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView = findViewById(R.id.navigation_view);
        View navView = navigationView.inflateHeaderView(R.layout.navigation_header);
        mNavProfileImage = navView.findViewById(R.id.nav_profile_img);
        mNavProfileuUserName = navView.findViewById(R.id.nav_user_full_name);
        //set the userInfo on the Navigation bar.
        mUserRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.hasChild("fullName")) {
                        String mFullName = dataSnapshot.child("fullName").getValue().toString();
                        mNavProfileuUserName.setText(mFullName);
                    }
                    if (dataSnapshot.hasChild("profileImage")) {
                        String mImageUrl = dataSnapshot.child("profileImage").getValue().toString();
                        Picasso.with(MainActivity.this).load(mImageUrl).placeholder(R.drawable.profile).into(mNavProfileImage);
                    } else {
                        Toast.makeText(MainActivity.this, "Profile name do not exists..", Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserMenuSelector(item);
                return false;
            }
        });
        changeFragment(HOME_PAGE_FRAGMENT);
        mAddNewStory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(NEW_STORY_FRAGMENT);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            Log.v("MainActivity-onStart:", "user is existence");
            CheckUserExistence();
        }
    }

    private void CheckUserExistence() {
        final String mCurrentUserId = mAuth.getCurrentUser().getUid();
        mUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(mCurrentUserId)) {
                    SendUserToSetupActivity();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivty.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.nav_home:
                changeFragment(HOME_PAGE_FRAGMENT);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_profile:
                changeFragment(PROFILE_FRAGMENT);
                drawerLayout.closeDrawers();
                break;
            case R.id.add_new_story_nav:
                changeFragment(NEW_STORY_FRAGMENT);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_friends:
                changeFragment(MY_FRIENDS);
                drawerLayout.closeDrawers();
                break;

            case R.id.nav_find_friends:
                changeFragment(FIND_FRIENDS);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_massaging:
                Toast.makeText(this, "Massaging", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_setting:
                changeFragment(SETTINGS_FRAGMENT);
                drawerLayout.closeDrawers();
                break;
            case R.id.nav_log_out:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;

        }

    }

    public void changeFragment(int index) {
        // get fragment manager
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        switch (index) {
            case NEW_STORY_FRAGMENT:
                ft.replace(R.id.main_container,
                        new AddStoryFragm(), NEW_STORY_FRAGMENT + "");
                break;
            case HOME_PAGE_FRAGMENT:
                ft.replace(R.id.main_container,
                        new HomePageFragment(), HOME_PAGE_FRAGMENT + "");
                getSupportActionBar().setTitle("Home");
                break;
            case SETTINGS_FRAGMENT:
                ft.replace(R.id.main_container, new SettingFragment(), SETTINGS_FRAGMENT + "");
                getSupportActionBar().setTitle("Settings");
                break;
            case PROFILE_FRAGMENT:
                ft.replace(R.id.main_container, new ProfileFragment(), PROFILE_FRAGMENT + "");
                getSupportActionBar().setTitle("My Profile");
                break;
            case FIND_FRIENDS:
                ft.replace(R.id.main_container, new FindFriendsFragment(), FIND_FRIENDS + "");
                getSupportActionBar().setTitle("Find Friends");
                break;
            case PERSON_PROFILE:
                ft.replace(R.id.main_container, new PersonProfileFragment(), PERSON_PROFILE + "");
                break;
            case MY_FRIENDS:
                ft.replace(R.id.main_container, new FriendsFrag(), MY_FRIENDS + "");
                getSupportActionBar().setTitle("Friends");
                break;
            case PERSON_STORIES:
                ft.replace(R.id.main_container, new PersonStoriesFragment(), PERSON_STORIES + "");
                break;


        }
        // replace
        ft.commit();
    }
    public void receivePersonKey(String key){
         mCurrentKey=key;
    }
    public String sendingPersonKey(){

        return mCurrentKey;
    }
}
