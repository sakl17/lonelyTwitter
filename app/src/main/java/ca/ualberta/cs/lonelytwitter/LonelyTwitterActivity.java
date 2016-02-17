package ca.ualberta.cs.lonelytwitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LonelyTwitterActivity extends Activity {
	private EditText bodyText;
	private ListView oldTweetsList;

	private ArrayList<Tweet> tweets = new ArrayList<Tweet>();

	public ArrayAdapter<Tweet> getAdapter() {
		return adapter;
	}

	private ArrayAdapter<Tweet> adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		bodyText = (EditText) findViewById(R.id.body);
		Button saveButton = (Button) findViewById(R.id.save);
		oldTweetsList = (ListView) findViewById(R.id.oldTweetsList);

		saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Create a tweet out of the text that was entered
                String text = bodyText.getText().toString();
                final Tweet latestTweet;
                try {
                    latestTweet = new NormalTweet(text);

                    // Add the tweet
                    tweets.add(latestTweet);
                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            ElasticsearchTweetController.addTweet(latestTweet);
                        }
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    adapter.notifyDataSetChanged();

                    // Everything is OK!
                    setResult(RESULT_OK);
                } catch (TweetTooLongException e) {
                    e.printStackTrace();
                }
            }
        });
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

        // Load the latest tweets (in a new thread, because of networkonmainthread stuff...
        Thread thread = new Thread(new Runnable() {
            public void run() {
                tweets = ElasticsearchTweetController.getLatestTweets();
            }
        });
        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<Tweet>(this, R.layout.list_item, tweets);
		oldTweetsList.setAdapter(adapter);
	}
}