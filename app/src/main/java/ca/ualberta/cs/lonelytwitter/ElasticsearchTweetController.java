package ca.ualberta.cs.lonelytwitter;

import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.searchly.jestdroid.DroidClientConfig;
import com.searchly.jestdroid.JestClientFactory;
import com.searchly.jestdroid.JestDroidClient;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import java.io.Console;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.mapping.PutMapping;
import io.searchbox.params.SearchType;

/**
 * Created by esports on 2/16/16.
 */
public class ElasticsearchTweetController {
    private static JestDroidClient client;
    private static Gson gson;
    private static HttpClient http;

    // TODO: Get tweets
    public static ArrayList<Tweet> getLatestTweets() {
        verifyClient();

        // Base arraylist to hold tweets
        ArrayList<Tweet> tweets = new ArrayList<Tweet>();

        String query = "{\n" +
                       "\"query\": {\n" +
                       "\"term\": { \"tweet\" : \"love\" }\n" +
                       "}\n" +
                       "}\n";

        Search search = new Search.Builder("")
                .addIndex("testing")
                .addType("tweet")
                .build();

        try {
            JestResult result = client.execute(search);
            if(result.isSucceeded()) {
                List<NormalTweet> fun = result.getSourceAsObjectList(NormalTweet.class);
                tweets.addAll(fun);
            }
        } catch (IOException e) {
            // TODO: Something more useful
            throw new RuntimeException();
        }

        return tweets;
    }

    // TODO: Add tweet
    public static void addTweet(Tweet tweet) {
        verifyClient();

        Index index = new Index.Builder(tweet).index("testing").type("tweet").build();

        try {
            DocumentResult execute = client.execute(index);
            if(execute.isSucceeded()) {
                tweet.setId(execute.getId());
            } else {
                // TODO: Something more useful
                Log.i("what", execute.getJsonString());
                Log.i("what", Integer.toString(execute.getResponseCode()));
            }
            return;
        } catch (IOException e) {
            // TODO: Something more useful
            e.printStackTrace();
        }
    }

    public static void verifyClient() {
        if(client == null) {
            // TODO: Consider moving this URL in to some config class
            DroidClientConfig.Builder builder = new DroidClientConfig.Builder("http://krasmuss-cmput301.rhcloud.com");
            DroidClientConfig config = builder.build();

            JestClientFactory factory = new JestClientFactory();
            factory.setDroidClientConfig(config);
            client = (JestDroidClient) factory.getObject();

            gson = new Gson();
            client.setGson(gson);
        }
    }
}
