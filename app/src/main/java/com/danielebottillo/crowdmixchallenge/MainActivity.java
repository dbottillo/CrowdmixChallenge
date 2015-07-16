package com.danielebottillo.crowdmixchallenge;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.danielebottillo.crowdmixchallenge.adapter.TweetAdapter;
import com.danielebottillo.crowdmixchallenge.util.NetworkUtil;
import com.danielebottillo.segmentedloader.Segment;
import com.danielebottillo.segmentedloader.SegmentedLoader;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "T5ih6qRh6awRZJyepLsi3qBOM";
    private static final String TWITTER_SECRET = "uBX8AVSdA125reCyuMcHIbYzORXRPqZXxF3EoCrlzjE2W0fUIK";

    TwitterLoginButton signIn;
    TweetAdapter tweetAdapter;
    ArrayList<Tweet> tweets;
    ListView tweetsList;
    SwipeRefreshLayout mSwipeRefreshLayout;

    SegmentedLoader loader;
    private boolean isLoading;

    TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.tweets_list_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        tweetsList = (ListView) findViewById(R.id.tweets_list);

        errorMessage = (TextView) findViewById(R.id.error_message);
        errorMessage.setVisibility(View.GONE);

        loader = (SegmentedLoader) findViewById(R.id.loader);
        setupLoader();

        signIn = (TwitterLoginButton) findViewById(R.id.sign_in);
        signIn.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                signIn.setVisibility(View.GONE);
                loadTweets();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        tweets = new ArrayList<>();
        tweetAdapter = new TweetAdapter(this, tweets);
        tweetsList.setAdapter(tweetAdapter);
    }

    private void loadTweets() {
        if (isLoading) {
            return;
        }
        boolean isConnected = NetworkUtil.isConnected(getBaseContext());
        if (isConnected) {
            isLoading = true;
            if (tweets.size() == 0){
                showLoader();
            }
            TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
            StatusesService statusesService = twitterApiClient.getStatusesService();
            statusesService.homeTimeline(20, null, null, null, false, true, true, tweetCallback);
        } else {
            // show offline
            showErrorMessage(getString(R.string.offline));
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private Callback<List<Tweet>> tweetCallback = new Callback<List<Tweet>>() {
        @Override
        public void success(Result<List<Tweet>> result) {
            isLoading = false;
            loader.hide();
            mSwipeRefreshLayout.setRefreshing(false);
            tweets.clear();
            for (Tweet tweet : result.data) {
                tweets.add(tweet);
            }
            tweetAdapter.notifyDataSetChanged();
        }

        public void failure(TwitterException exception) {
            isLoading = false;
            loader.hide();
            mSwipeRefreshLayout.setRefreshing(false);
            showErrorMessage(exception.getLocalizedMessage());
        }
    };

    private void showErrorMessage(String message) {
        errorMessage.setVisibility(View.VISIBLE);
        errorMessage.setText(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        signIn.onActivityResult(requestCode, resultCode, data);
    }

    private void showLoader() {
        errorMessage.setVisibility(View.GONE);
        errorMessage.setText("");
        loader.show();
    }

    private void setupLoader() {
        loader.addSegment(new Segment().setStartLeftPoint(10, 0).setStartRightPoint(10, 2).setEndRightPoint(0, 2).setEndLeftPoint(0, 0));
        loader.addSegment(new Segment().setStartLeftPoint(0, 2).setStartRightPoint(2, 2).setEndRightPoint(2, 8).setEndLeftPoint(0, 8));
        loader.addSegment(new Segment().setStartLeftPoint(0, 8).setStartRightPoint(0, 10).setEndRightPoint(10, 10).setEndLeftPoint(10, 8));
        loader.hide();
    }

    @Override
    public void onRefresh() {
        loadTweets();
    }
}
