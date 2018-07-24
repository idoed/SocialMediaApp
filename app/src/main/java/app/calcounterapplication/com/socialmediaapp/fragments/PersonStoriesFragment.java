package app.calcounterapplication.com.socialmediaapp.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import app.calcounterapplication.com.socialmediaapp.MainActivity;
import app.calcounterapplication.com.socialmediaapp.R;
import app.calcounterapplication.com.socialmediaapp.model.Story;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonStoriesFragment extends Fragment {


    private RecyclerView mStoriesList;
    private DatabaseReference mStoriesRef,mUsersRef;
    private CircleImageView mPersonProfilePic;
    private Query query;
    public String postKey;
    private String current_user_id;
    private TextView username;
    private final String TAG = "PersonStoriesFragment";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_person_stories, container, false);
        Log.v(TAG,"onCreateView");
        mStoriesList=view.findViewById(R.id.friends_stories_Stories_list);
        mPersonProfilePic=view.findViewById(R.id.friends_stories_profile_image);
        current_user_id= ((MainActivity)getActivity()).sendingPersonKey();
        mStoriesRef= FirebaseDatabase.getInstance().getReference().child("Stories");
        mUsersRef=FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
        query = mStoriesRef.orderByChild("uid").equalTo(current_user_id);
        mStoriesList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        mStoriesList.setLayoutManager(mLayoutManager);
        mStoriesList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mStoriesList.setItemAnimator(new DefaultItemAnimator());
        username=view.findViewById(R.id.friends_stories_full_name);
        mUsersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    String userName=dataSnapshot.child("userName").getValue().toString();
                    String profileImage=dataSnapshot.child("profileImage").getValue().toString();

                    username.setText(userName+"'s Stories");
                    Picasso.with(getContext()).load(profileImage).placeholder(R.drawable.profile).into(mPersonProfilePic);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        displayUserStories();

        return view;
    }

    private void displayUserStories() {

        FirebaseRecyclerAdapter<Story, PersonStoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Story, PersonStoryViewHolder>
                (
                        Story.class, R.layout.stories_layout, PersonStoryViewHolder.class, query
                ) {
            @Override
            protected void populateViewHolder(final PersonStoryViewHolder viewHolder, Story model, int position) {
                try {
                    postKey = getRef(position).getKey();
                    viewHolder.getPostKey(postKey);
                    viewHolder.setDate(model.getDate());
                    viewHolder.setTime(model.getTime());
                    viewHolder.setVideoStory(getContext(), model.getStory());
                    viewHolder.mVideo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            viewHolder.mVideo.start();
                        }
                    });
                }catch (NullPointerException e){
                    Toast.makeText(getContext(), "No Data", Toast.LENGTH_SHORT).show();
                }
            }
        };
        mStoriesList.setAdapter(firebaseRecyclerAdapter);

    }


    public static class PersonStoryViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private VideoView mVideo;
        private String postKey;
        private DatabaseReference mStoriesRef;
        public PersonStoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            final Context context1=mView.getContext();

        }

        public void getPostKey(String postKey1){
            postKey=postKey1;
        }
        public void setDate(String date) {
            TextView mDate = mView.findViewById(R.id.cardview_date_text);
            mDate.setText("Date: " + date);
        }

        public void setVideoStory(Context context, String story) {
            mVideo = mView.findViewById(R.id.story_image_view);
            Uri videoUri = Uri.parse(story);
            mVideo.setVideoURI(videoUri);
            mVideo.start();

//            Glide.with(context).load(story).into(mVideo);
            Log.v("Story Adress:", story);
        }

        public void setTime(String time) {
            TextView mTime = mView.findViewById(R.id.cardview_time_text);
            mTime.setText("Time: " + time);
        }

        }





    //Design the Spaces between the stories
    private int dpToPx(int dp) {
        Resources resources = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }

    //Override class to give Spaces for the items on the list.
    private class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;
                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spanCount / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

}
