package app.calcounterapplication.com.socialmediaapp.fragments;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.net.URI;

import app.calcounterapplication.com.socialmediaapp.MainActivity;
import app.calcounterapplication.com.socialmediaapp.R;
import app.calcounterapplication.com.socialmediaapp.model.Story;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomePageFragment extends Fragment {


    private RecyclerView mStoriesList;
    private DatabaseReference mStoriesRef;
    private FirebaseAuth mAuth;
    private Query query;
    public String postKey;
    private String current_user_online;
    private final String TAG = "HomePageFramgment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);
        mStoriesList = view.findViewById(R.id.user_story_list);
        initCollapsingToolBar(view);
        mAuth = FirebaseAuth.getInstance();
        current_user_online = mAuth.getCurrentUser().getUid();
        mStoriesRef = FirebaseDatabase.getInstance().getReference().child("Stories");
         query = mStoriesRef.orderByChild("uid").equalTo(current_user_online);


        mStoriesList.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        mStoriesList.setLayoutManager(mLayoutManager);
        mStoriesList.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mStoriesList.setItemAnimator(new DefaultItemAnimator());
        DisplayUserStoriest();
        return view;
    }

    private void initCollapsingToolBar(View view) {
        final CollapsingToolbarLayout collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = view.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollrange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollrange == -1) {
                    scrollrange = appBarLayout.getTotalScrollRange();
                }
                if (scrollrange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Home");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }


            }
        });
    }

    public void DisplayUserStoriest() {
        FirebaseRecyclerAdapter<Story, StoryViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Story, StoryViewHolder>
                (Story.class, R.layout.stories_layout, StoryViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final StoryViewHolder viewHolder, Story model, int position) {
                postKey= getRef(position).getKey();
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

            }

        };
        Log.v(TAG, "Left the Methood");
        mStoriesList.setAdapter(firebaseRecyclerAdapter);
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        View mView;
        private VideoView mVideo;
        private ImageView menuButton;
        private String postKey;
        private DatabaseReference mStoriesRef;


        public StoryViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            final Context context1=mView.getContext();
            menuButton=(ImageView) mView.findViewById(R.id.image_menu_but);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu mPopUpMenu=new PopupMenu(context1,menuButton);
                    mPopUpMenu.getMenuInflater().inflate(R.menu.popup_menu,mPopUpMenu.getMenu());
                    mPopUpMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            deleteCurrentStory();
                            return true;
                        }
                    });
                mPopUpMenu.show();
                }
            });


        }

        private void deleteCurrentStory() {
            mStoriesRef=FirebaseDatabase.getInstance().getReference().child("Stories").child(postKey);
            mStoriesRef.removeValue();
            

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
