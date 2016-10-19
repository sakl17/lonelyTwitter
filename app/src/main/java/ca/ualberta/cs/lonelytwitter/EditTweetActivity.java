package ca.ualberta.cs.lonelytwitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class EditTweetActivity extends Activity {

    private ListView tweetView;
    private ArrayList<Tweet> tweets = new ArrayList<Tweet>();
    private ArrayAdapter<Tweet> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tweet);

        tweetView = (ListView) findViewById(R.id.tweetView);

        Intent intent = getIntent();
        String wut = intent.getExtras().getString("daTweet");
        Tweet editTweet = new NormalTweet(wut);
        tweets.add(editTweet);
        adapter.notifyDataSetChanged();
    }

    protected void onStart() {
        super.onStart();

        // Get latest tweets
        // TODO: Replace with Elasticsearch

        // Binds tweet list with view, so when our array updates, the view updates with it
        adapter = new ArrayAdapter<Tweet>(this, R.layout.list_item, tweets);
        tweetView.setAdapter(adapter);
    }
}
