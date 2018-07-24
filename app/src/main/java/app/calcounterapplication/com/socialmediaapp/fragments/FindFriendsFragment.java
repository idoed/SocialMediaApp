package app.calcounterapplication.com.socialmediaapp.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import app.calcounterapplication.com.socialmediaapp.MainActivity;
import app.calcounterapplication.com.socialmediaapp.R;
import app.calcounterapplication.com.socialmediaapp.model.FindFriends;
import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsFragment extends Fragment {
    private ImageButton mSearchButton;
    private EditText mSearchInputText;
    private RecyclerView mSearchResultList;
    private DatabaseReference mSearchUsersRef;
    private final int PERSON_PROFILE = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_find_friends, container, false);
        mSearchResultList = (RecyclerView) view.findViewById(R.id.search_result_list);
        mSearchUsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        mSearchResultList.setHasFixedSize(true);
        mSearchResultList.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchButton=view.findViewById(R.id.search_people_friends_button);
        mSearchInputText=view.findViewById(R.id.search_box_input);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mSearchBoxInput=mSearchInputText.getText().toString();

                searchUsers(mSearchBoxInput);
            }
        });




        return view;
    }

    private void searchUsers(String mSearchBoxInput) {
        Toast.makeText(getContext(), "Searching..", Toast.LENGTH_LONG).show();
        Query mSearchUsersQuery=mSearchUsersRef.orderByChild("fullName").startAt(mSearchBoxInput).endAt(mSearchBoxInput+ "\uf8ff");
        FirebaseRecyclerAdapter<FindFriends,FindFriendsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<FindFriends, FindFriendsViewHolder>(
                FindFriends.class,
                R.layout.all_users_display_layout,
                FindFriendsViewHolder.class,
                mSearchUsersQuery
        ) {
            @Override
            protected void populateViewHolder(FindFriendsViewHolder viewHolder, FindFriends model, final int position) {
                viewHolder.setFullName(model.getFullName());
                viewHolder.setStatus(model.getStatus());
                viewHolder.setProfileImage(getContext(),model.getProfileImage());
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String visit_user_id=getRef(position).getKey();
                        ((MainActivity)getActivity()).receivePersonKey(visit_user_id);
                        ((MainActivity)getActivity()).changeFragment(PERSON_PROFILE);


                    }
                });

            }
        };
        mSearchResultList.setAdapter(firebaseRecyclerAdapter);
    }
    public static class FindFriendsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public FindFriendsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

        }
        public void setProfileImage(Context context, String profileImage){
            CircleImageView myImage= mView.findViewById(R.id.all_users_profile_image);
            Picasso.with(context).load(profileImage).placeholder(R.drawable.profile).into(myImage);
        }
        public void setFullName(String fullName){
            TextView myName=mView.findViewById(R.id.all_users_profile_name);
            myName.setText(fullName);
        }
        public void setStatus(String status){
            TextView myStatus=mView.findViewById(R.id.all_users_status);
            myStatus.setText(status);
        }

    }

}
