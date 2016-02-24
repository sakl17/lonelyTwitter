package ca.ualberta.cs.lonelytwitter;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

/**
 * Created by esports on 2/17/16.
 */
public class ElasticsearchTweetController {
    private static JestDroidClient client;

    //TODO: A function that gets tweets
    public static class GetTweetsTask extends AsyncTask<String, Void, ArrayList<Tweet>> {
        @Override
        protected ArrayList<Tweet> doInBackground(String... search_strings) {
            verifyClient();

            // Start our initial array list (empty)
            ArrayList<Tweet> tweets = new ArrayList<Tweet>();

            // NOTE: I'm a making a huge assumption here, that only the first search term
            // will be used.
            String search_string;
            if(search_strings[0] != "") {
                search_string = "{\"query\":{\"match\":{\"message\":\"" + search_strings[0] + "\"}}}";
            } else {
                search_string = "";
            }

            Search search = new Search.Builder(search_string)
                    .addIndex("testing")
                    .addType("tweet")
                    .build();

            try {
                SearchResult execute = client.execute(search);
                if(execute.isSucceeded()) {
                    // Return our list of tweets
                    List<NormalTweet> returned_tweets = execute.getSourceAsObjectList(NormalTweet.class);
                    tweets.addAll(returned_tweets);
                } else {
                    // TODO: Add an error message, because that other thing was puzzling.
                    // TODO: Right here it will trigger if the search fails
                    Log.i("TODO", "We actually failed here, searching for tweets");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return tweets;
        }
    }
    //public static ArrayList<Tweet> getTweets() {
    //    verifyClient();
    //}

    //TODO: A function that adds a tweet
    public static class AddTweetTask extends AsyncTask<NormalTweet, Void, Void> {
        @Override
        protected Void doInBackground(NormalTweet... tweets) {
            verifyClient();

            // Since AsyncTasks work on arrays, we need to work with arrays as well (>= 1 tweet)
            for(int i = 0; i < tweets.length; i++) {
                NormalTweet tweet = tweets[i];

                Index index = new Index.Builder(tweet).index("testing").type("tweet").build();
                try {
                    DocumentResult result = client.execute(index);
                    if(result.isSucceeded()) {
                        // Set the ID to tweet that elasticsearch told me it was
                        tweet.setId(result.getId());
                    } else {
                        // TODO: Add an error message, because this was puzzling.
                        // TODO: Right here it will trigger if the insert fails
                        Log.i("TODO", "We actually failed here, adding a tweet");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    public static void verifyClient() {
        // 1. Verify that 'client' exists.
        if(client == null) {
            // 2. If it doesn't, make it.
            // TODO: Put this URL somewhere it makes sense (e.g. class variable?)
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.es:8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
