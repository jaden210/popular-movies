package crappydayproductions.com.popularmovies;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jaden_000 on 3/15/2016.
 */
public class DetailFragment extends Fragment {

    private final String LOG_TAG = DetailFragment.class.getSimpleName();
    private Movie movieData1;
    private Menu menu;

    @Nullable
    @Bind(R.id.trailer_container)
    LinearLayout container;
    @Nullable
    @Bind(R.id.review_container)
    LinearLayout rContainer;

    //mine
    private static final String SHARE_HASHTAG = " #PopularMoviesApp";

    //public DetailFragment() {setHasOptionsMenu(true);}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);
        this.menu = menu;
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
    public void onPrepareOptionsMenu(Menu menu) {
        SharedPreferences mPrefs = getActivity().getSharedPreferences("SHARED_KEY", Context.MODE_PRIVATE);
        MenuItem mi = (MenuItem) menu.findItem(R.id.action_favorite);
        if (!mPrefs.contains(movieData1.getId())) {
            mi.setIcon(R.drawable.like);
        }else {
            mi.setIcon(R.drawable.likefilled);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        int i = 1;
        if (id == R.id.action_share) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            Intent shareMovie = getActivity().getIntent();
            Movie shareMovieTitle = shareMovie.getParcelableExtra("movie");
            if (shareMovieTitle == null) {
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, SHARE_HASHTAG);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)));
            }else {
                String movieTitle = shareMovieTitle.getTitle();
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "I watched " + movieTitle + " and I want you to watch it too! " + SHARE_HASHTAG);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)));
            }return true;
        }
        if (id == R.id.action_favorite) {
            SharedPreferences mPrefs = getActivity().getSharedPreferences("SHARED_KEY", Context.MODE_PRIVATE);
            SharedPreferences.Editor prefsEditor = mPrefs.edit();
            if (!mPrefs.contains(movieData1.getId())) {
                Gson gson = new Gson();
                String json = gson.toJson(movieData1);
                prefsEditor.putString(movieData1.getId(), json);
                prefsEditor.apply();
                Toast.makeText(getActivity().getApplicationContext(), "Added!", Toast.LENGTH_LONG).show();
                getActivity().invalidateOptionsMenu();
            }else {
                prefsEditor.remove(movieData1.getId());
                prefsEditor.apply();
                Toast.makeText(getActivity().getApplicationContext(), "This movie is now removed from your favorites", Toast.LENGTH_LONG).show();
                getActivity().invalidateOptionsMenu();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle bundle = getArguments();
        ButterKnife.bind(this, rootView);
        if (bundle != null) {
            movieData1 = bundle.getParcelable("movie");
            setHasOptionsMenu(true);
            final String movieTitle = movieData1.getTitle();
            //String movieImage = movieData.getImage();
            final String movieDescription = movieData1.getDescription();
            final String movieRating = movieData1.getRating();
            final String movieRelease = movieData1.getRelease();
            final String moviePoster = movieData1.getPoster();
            final String id = movieData1.getId();

            Log.v(LOG_TAG, "Movie Data: " + movieTitle);
            ((TextView) rootView.findViewById(R.id.movie_title))
                    .setText(movieTitle);

            ((TextView) rootView.findViewById(R.id.movie_description))
                    .setText(movieDescription);

            ((TextView) rootView.findViewById(R.id.movie_rating))
                    .setText(movieRating);

            ((TextView) rootView.findViewById(R.id.movie_release))
                    .setText(movieRelease);

            ((TextView) rootView.findViewById(R.id.overview))
                    .setText("Overview");

            ((TextView) rootView.findViewById(R.id.reviews_title))
                    .setText("User Reviews");


            ImageView poster = ((ImageView) rootView.findViewById(R.id.poster_image));
            Picasso.with(getActivity()).load(moviePoster).into(poster);

            try {
                FetchDataTask fetchDataTask = new FetchDataTask();
                String API_KEY = "";
                fetchDataTask.execute(API_KEY);
            }catch (Exception e) {

            }
        } else {
            //Toast.makeText(getContext(), "FAIL", Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }



    public class FetchDataTask extends AsyncTask<String, Void, Movie> {

        private final String LOG_TAG = FetchDataTask.class.getSimpleName();

        private void playTrailerIntent(String key) {

            final String VALUE = "v";
            final String BASE_YOUTUBE_URI = "http://www.youtube.com/watch?";
            Uri builtUri = Uri.parse(BASE_YOUTUBE_URI).buildUpon()
                    .appendQueryParameter(VALUE, key)
                    .build();

            // Build the intent
            Intent playIntent = new Intent(Intent.ACTION_VIEW, builtUri);

            // Verify it resolves
            PackageManager packageManager = getActivity().getPackageManager();
            // TODO: Why the example in documentation set flag = 0?
            List<ResolveInfo> activities = packageManager.queryIntentActivities(playIntent, PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;

            // Start an activity if it is safe
            if (isIntentSafe) {
                startActivity(playIntent);
            }
        }


        private void getTrailerFromJson(String trailerJsonStr)
                throws JSONException {

            final String MOVIE_ID = "id";
            final String TRAILER_NAME = "name";
            final String KEY = "key";
            final String RESULTS = "results";
            // Convert JSON string to JSON object
            JSONObject jsonObject = new JSONObject(trailerJsonStr);
            JSONArray resultArray = jsonObject.getJSONArray(RESULTS);

            String movieId = jsonObject.getString(MOVIE_ID);

            int count = resultArray.length();
            for (int i = 0; i < count; i++) {
                // Get JSON object for each trailer
                JSONObject trailerJson = resultArray.getJSONObject(i);

                String name = trailerJson.getString(TRAILER_NAME);
                String key = trailerJson.getString(KEY);

                Movie.Trailer trailer = new Movie.Trailer(movieId, name, key);
                // Add each trailer to the trailers List.
                movieData1.trailers.add(trailer);
            }
        }

        private void getReviewFromJson(String reviewJsonStr)
                throws JSONException {

            final String RESULTS = "results";
            final String RID = "id";
            final String RAUTHOR = "author";
            final String RCONTENT = "content";
            final String RURL = "url";

            // Convert JSON String to JSON
            JSONObject jsonObject = new JSONObject(reviewJsonStr);

            String movieID = jsonObject.getString(RID);
            JSONArray resultArray = jsonObject.getJSONArray(RESULTS);
            int count = resultArray.length();

            for (int i = 0; i < count; i++) {
                // Get JSON object for each review
                JSONObject reviewJson = resultArray.getJSONObject(i);

                String author = reviewJson.getString(RAUTHOR);
                String content = reviewJson.getString(RCONTENT);
                String url = reviewJson.getString(RURL);

                Movie.Review review = new Movie.Review(movieID, author, content, url);

                movieData1.reviews.add(review);
            }
        }

        @Override
        protected Movie doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            // get connection
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String trailerJsonStr = null;
            String reviewJsonStr = null;
            final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
            final String API_PARAM = "api_key";
            final String apiKey = "5c50c47fea062190f9f743911ae71820";
            final String VIDEOS = "videos";
            final String REVIEWS = "reviews";
            final String MOVIE_ID = movieData1.id;


            try {
                //build uri
                final String TRAILER_URL = MOVIE_BASE_URL + "/" + MOVIE_ID + "/" + VIDEOS;
                Uri builtTrailerUri = Uri.parse(TRAILER_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, apiKey)
                        .build();

                URL trailerUrl = new URL(builtTrailerUri.toString());
                Log.v(LOG_TAG, "URI = " + builtTrailerUri.toString());

                // Create the request to OpenmoviesAPI, and open the connection
                urlConnection = (HttpURLConnection) trailerUrl.openConnection();
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

                trailerJsonStr = buffer.toString();
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

            //Reviews
            try {
                // Construct URI for reviews query.
                final String REVIEW_URL = MOVIE_BASE_URL + "/" + MOVIE_ID + "/" + REVIEWS;
                Uri builtReviewUri = Uri.parse(REVIEW_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, apiKey)
                        .build();

                // Construct URL
                URL reviewURL = new URL(builtReviewUri.toString());

                // Open connection
                urlConnection = (HttpURLConnection) reviewURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Step 2: Read response from input stream (String of JSON)
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                // Read input stream into string;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer == null) {
                    return null;
                }

                // Get JSON String out of buffer
                reviewJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                e.printStackTrace();
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                        e.printStackTrace();
                    }
                }
            }

            //ends http request

            try {
                getTrailerFromJson(trailerJsonStr);
                getReviewFromJson(reviewJsonStr);
                return movieData1;

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }

        @Override
        protected void onPostExecute(Movie result) {
            //R.id.detail_container.removeAllViews();
            container.removeAllViews();
            rContainer.removeAllViews();

            if (result.trailers.size() != 0) {
                for (final Movie.Trailer video : movieData1.trailers) {
                    View trailerItem = LayoutInflater.from(getActivity()).inflate(R.layout.trailer_single, null);
                    TextView trailerUrl = (TextView) trailerItem.findViewById(R.id.trailer_name);
                    trailerUrl.setText(video.tSource);
                    trailerItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            playTrailerIntent(video.key);
                        }
                    });
                    //Toast.makeText(getContext(), "FAILED", Toast.LENGTH_SHORT).show();

                    container.removeAllViews();
                    container.addView(trailerItem);
                }
            }

            if (result.reviews.size() != 0) {
                for (final Movie.Review review : movieData1.reviews) {
                    View reviewItem = LayoutInflater.from(getActivity()).inflate(R.layout.review_single, null);
                    TextView reviewAuthor = (TextView) reviewItem.findViewById(R.id.review_author);
                    TextView reviewContent = (TextView) reviewItem.findViewById(R.id.review_name);
                    reviewAuthor.setText(review.rAuthor);
                    reviewContent.setText(review.rContent);
                    //rContainer.addView(reviewItem);
                    rContainer.addView(reviewItem);
                }
            }
        }
    }
}
