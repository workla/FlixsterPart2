package com.codepath.flixter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.flixter.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.YouTubeThumbnailView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {


    String movieID;
    TextView tvDetailTitle;
    TextView tvDetailOverview;
    RatingBar ratingBar;
    YouTubePlayerView youTubePlayerView;
    public static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        //link to correct spots in xml layout
        tvDetailTitle = findViewById(R.id.tvDetailPageTitle);
        tvDetailOverview = findViewById(R.id.tvDetailOverview);
        ratingBar = findViewById(R.id.ratingBar);
        youTubePlayerView = findViewById(R.id.player);

        //get movie passed in intent
        Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        tvDetailTitle.setText(movie.getTitle());
        tvDetailOverview.setText(movie.getOverview());
        ratingBar.setRating((float)movie.getRating());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieID(), getString(R.string.nowPlayingAPIkey)), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    if (results.length() == 0)
                    {
                        Log.d("detailActivity", "size of json result of get is 0");
                        return;
                    }
                    else
                    {
                        String youtubekey = results.getJSONObject(0).getString("key");
                        initializeYoutube(youtubekey);
                    }
                } catch (JSONException e) {
                    Log.e("detailActivity", "failed to parse JSON");
                }
            };

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d("detailActivity", "failed to get");
            }
        });

    }

    private void initializeYoutube(final String youtubekey) {
        youTubePlayerView.initialize(getString(R.string.youtubeAPIkey), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.cueVideo(youtubekey);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("detailActivity", "youbtube player OnInitializationFailure");
            }
        });
    }
}