package crappydayproductions.com.popularmovies;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.BinderThread;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);

        DetailFragment detailFragment = new DetailFragment();
        Intent intent = getIntent();
        Movie movieData = intent.getParcelableExtra("movie");
        Bundle bundle = new Bundle();
        bundle.putParcelable("movie", movieData);
        detailFragment.setArguments(bundle);

        //This can probably be removed.
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.add(R.id.fragment,detailFragment).commit();

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            //noinspection SimplifiableIfStatement
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {

        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private Movie movieData;

        //mine
        private static final String SHARE_HASHTAG = " #PopularMoviesApp";

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.detailfragment, menu);
            // Retrieve the share menu item
            MenuItem menuItem = menu.findItem(R.id.action_share);
            // Get the provider and hold onto it to set/change the share intent.
            ShareActionProvider mShareActionProvider =
                    (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
            // Attach an intent to this ShareActionProvider.  You can update this at any time,
            // like when the user selects a new piece of data they might like to share.
            if (mShareActionProvider != null) {
                //mShareActionProvider.setShareIntent(createShareForecastIntent());
            } else {
                Log.d(LOG_TAG, "Share Action Provider is null?");
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_share) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                Intent shareMovie = getActivity().getIntent();
                Movie shareMovieTitle = shareMovie.getParcelableExtra("movie");
                String movieTitle = shareMovieTitle.getTitle();
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I watched " + movieTitle + " and I want you to watch it too! " + SHARE_HASHTAG);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            Intent intent = getActivity().getIntent();
            if (intent != null) {
                movieData = intent.getParcelableExtra("movie");
                String movieTitle = movieData.getTitle();
                //String movieImage = movieData.getImage();
                String movieDescription = movieData.getDescription();
                String movieRating = movieData.getRating();
                String movieRelease = movieData.getRelease();
                String moviePoster = movieData.getPoster();
                //String movieId = movieData.getId();

                Log.v(LOG_TAG, "Movie Data: " + movieTitle);

                ((TextView) rootView.findViewById(R.id.movie_description))
                        .setText(movieDescription);

                ((TextView) rootView.findViewById(R.id.movie_rating))
                        .setText(movieRating);

                ((TextView) rootView.findViewById(R.id.movie_release))
                        .setText(movieRelease);

                ImageView poster = ((ImageView) rootView.findViewById(R.id.poster_image));
                Picasso.with(getActivity()).load(moviePoster).into(poster);

            } else {
                Toast.makeText(getContext(), "FAIL", Toast.LENGTH_SHORT).show();
            }
            return rootView;
        }

        @Override
        public void onStart() {
            super.onStart();
            FetchDataTask fetchDataTask = new FetchDataTask();
            String API_KEY = "5c50c47fea062190f9f743911ae71820";
            fetchDataTask.execute(API_KEY);
        }

        public class FetchDataTask extends AsyncTask<String, Void, Movie> {

            private final String LOG_TAG = FetchDataTask.class.getSimpleName();

            //change to show in the top banner
            private void playYouTube(String key) {

                Uri builtUri = Uri.parse(key);

                Intent intent = new Intent(Intent.ACTION_VIEW, builtUri);
                startActivity(intent);
            }


            private void getTrailerFromJson(String detailJsonStr)
                    throws JSONException {

                final String TNAME = "name";
                final String TSOURCE = "source";
                final String TTYPE = "type";

                JSONObject detailJson = new JSONObject(detailJsonStr);
                JSONObject trailerJson = detailJson.getJSONObject("trailers");
                JSONArray trailerArray = trailerJson.getJSONArray("youtube");


                for (int i = 0; i < trailerArray.length(); i++) {
                    String tName;
                    String tSource;
                    String tType;

                    JSONObject trailerData = trailerArray.getJSONObject(i);
                    tName = trailerData.getString(TNAME);
                    tSource = trailerData.getString(TSOURCE);
                    tType = trailerData.getString(TTYPE);

                    Movie.Trailer trailer = new Movie.Trailer(tName, tSource, tType);

                    //There are Errors here.
                    movieData.trailers.add(trailer);
                }
            }

            private void getReviewFromJson(String detailJsonStr)
                    throws JSONException {

                final String RESULTS = "results";
                final String RID = "id";
                final String RAUTHOR = "author";
                final String RCONTENT = "content";
                final String RURL = "url";

                JSONObject detailJson = new JSONObject(detailJsonStr);
                JSONObject reviewJson = detailJson.getJSONObject("reviews");
                JSONArray reviewArray = reviewJson.getJSONArray(RESULTS);

                for (int i = 0; i < reviewArray.length(); i++) {
                    String rId;
                    String rAuthor;
                    String rContent;
                    String rUrl;

                    JSONObject reviewData = reviewArray.getJSONObject(i);
                    rId = reviewData.getString(RID);
                    rAuthor = reviewData.getString(RAUTHOR);
                    rContent = reviewData.getString(RCONTENT);
                    rUrl = reviewData.getString(RURL);
                    Movie.Review review = new Movie.Review(rId, rAuthor, rContent, rUrl);

                    //There are Errors here.
                    movieData.reviews.add(review);
                }
            }

            @Override
            protected Movie doInBackground(String... params) {

                if (params.length == 0) return null;

                // get connection
                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;

                // Will contain the raw JSON response as a string.
                String detailJsonStr = null;
                String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/" + params[0] + "?";
                String API_PARAM = "api_key";
                String apiKey = "5c50c47fea062190f9f743911ae71820";
                String append = "trailers,reviews";
                String APPEND = "append_to_response";


                try {
                    //build uri
                    Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                            .appendQueryParameter(API_PARAM, apiKey)
                            .appendQueryParameter(APPEND, append)
                            .build();

                    URL url = new URL(builtUri.toString());
                    Log.v(LOG_TAG, "URI = " + builtUri.toString());
                    // Create the request to OpenmoviesAPI, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;

                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }

                    detailJsonStr = buffer.toString();
                } catch (IOException e) {
                    Log.e("PlaceholderFragment", "Error ", e);
                    // If the code didn't successfully get the movie data, there's no point in attemping
                    // to parse it.
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }

                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }
                //ends http request
                try {
                    getTrailerFromJson(detailJsonStr);
                    getReviewFromJson(detailJsonStr);
                    return movieData;

                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Movie result) {
                //R.id.detail_container.removeAllViews();

                if (result.trailers.size() != 0) {
                    for (final Movie.Trailer video : movieData.trailers) {
                        View trailerItem = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail, null);
                        TextView trailerUrl = (TextView) trailerItem.findViewById(R.id.trailerLink);
                        trailerUrl.setText(video.tSource);
                        trailerItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                playYouTube(video.key);
                            }
                        });
                        //Toast.makeText(getContext(), "FAILED", Toast.LENGTH_SHORT).show();
                       // R.id.detail_container.addView(trailerItem);
                    }
                }

                if (result.reviews.size() != 0) {
                    for (final Movie.Review review : movieData.reviews) {
                        View reviewItem = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_detail, null);
                        //TextView reviewAuthor = (TextView) reviewItem.findViewById(R.id.review_author);
                        //TextView reviewContent = (TextView) reviewItem.findViewById(R.id.review_content);
                        //reviewAuthor.setText(review.rAuthor);
                        //reviewContent.setText(review.rContent);
                        //detailContainer.addView(reviewItem);
                    }
                }
            }
        }
    }
}
