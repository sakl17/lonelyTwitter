package ca.ualberta.cs.lonelytwitter;

import android.os.AsyncTask;
import android.util.Log;

import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

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

    public static class GetTweetsTask extends AsyncTask<String, Void, ArrayList<NormalTweet>>{
        @Override
        protected ArrayList<NormalTweet> doInBackground(String... search_parameters) {
            verifySettings();

            ArrayList<NormalTweet> tweets = new ArrayList<NormalTweet>();

            //String search_string = "{\"from\"
            String search_string = "{\from\": 0, \"size\": 10000, \"query\": {\"match\": {\"message\": \"" + search_parameters[0] + "\"}}}";

            // assume that search_parameter[0] is the only search term we are interested in using

            Search search = new Search.Builder(search_parameters[0])
                    .addIndex("T09")
                    .addType("tweet")
                    .build();

            try {
                SearchResult result = client.execute(search);
                if (result.isSucceeded()) {
                    List<NormalTweet> foundTweets = result.getSourceAsObjectList(NormalTweet.class);
                    tweets.addAll(foundTweets);
                }
                else {
                    Log.i("Error", "cant find tweets");
                }
            }
            catch (Exception e) {
                Log.i("Error", "can communicate with server");
            }
            return null;
        }
    }

    public static class AddTweetsTask extends AsyncTask<NormalTweet, Void, Void> {

        protected Void doInBackground(NormalTweet... tweets) {
            verifySettings();

            for (NormalTweet tweet : tweets) {
                Index index = new Index.Builder(tweet).index("T09").type("tweet").build();

                try {
                    DocumentResult result = client.execute(index);
                    if (result.isSucceeded()) {
                        tweet.setId(result.getId());
                    }
                    else {
                        Log.i("Error", "We couldnt add tweet");
                    }

                } catch (Exception e) {
                    Log.i("Bro", "Tweet didn't add");
                    e.printStackTrace();
                }
            }
            return null;
        }
    }

    private static void verifySettings(){
        if(client == null){
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://cmput301.softwareprocess.ex.8080");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();
        }
    }
}
