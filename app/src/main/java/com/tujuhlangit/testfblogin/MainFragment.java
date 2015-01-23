package com.tujuhlangit.testfblogin;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.ProfilePictureView;

/**
 * Created by Timothy on 1/22/2015.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private UiLifecycleHelper uiHelper;
    private TextView userInfoTextView;
    private ProfilePictureView profilePicView;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }
    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        userInfoTextView = (TextView) view.findViewById(R.id.userTV);
        profilePicView = (ProfilePictureView) view.findViewById(R.id.profilePic);
        if(savedInstanceState==null) {
            profilePicView.setVisibility(View.INVISIBLE);
        }
        else {
            if(userInfoTextView.getText().equals(null)) {
                profilePicView.setVisibility(View.INVISIBLE);
            }
            else {
                profilePicView.setVisibility(View.VISIBLE);
            }
        }
        return view;
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in...");
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if(user!=null) {
                        Log.i(TAG,"The User is=" + user.getName());
                        userInfoTextView.setText("Hi " + user.getName());
                        profilePicView.setVisibility(View.VISIBLE);
                        profilePicView.setProfileId(user.getId());
                    }
                }
            }).executeAsync();
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out...");
            userInfoTextView.setText("");
            profilePicView.setProfileId(null);
            profilePicView.setVisibility(View.INVISIBLE);
        }
    }
}
