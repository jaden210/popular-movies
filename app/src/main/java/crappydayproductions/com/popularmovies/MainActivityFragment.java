package crappydayproductions.com.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
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
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> mTitleAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateTitles();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mTitleAdapter = new ArrayAdapter<String>(
                //current context
                getActivity(),
                //grid item ID
                R.layout.grid_item_title,
                //textview ID
                R.id.grid_item_title_textview,
                //dataset
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_title);
        gridView.setAdapter(mTitleAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String detail = mTitleAdapter.getItem(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, detail);
                startActivity(intent);
            }
        });


        return rootView;
    }


    private void updateTitles() {
        FetchTitleTask titleTask = new FetchTitleTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sort_by = prefs.getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_default));
        titleTask.execute(sort_by);
    }


    @Override
    public void onStart() {
        super.onStart();
        updateTitles();
    }


    public class FetchTitleTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchTitleTask.class.getSimpleName();


        //Parsing is done here
        private String[] getTitleDataFromJson(String titleJsonStr)
                throws JSONException {

            //Names of JSON to extract
            final String RESULTS = "results";
            final String TITLE = "title";
            final String PICTURE = "poster_path";
            final String RELEASE = "release_date";
            final String DESCRIPTION = "overview";
            final String RATING = "vote_average";
            final String numResults = "20";

            JSONObject titleJson = new JSONObject(titleJsonStr);
            JSONArray titleArray = titleJson.getJSONArray(RESULTS);

            String[] resultStrs = new String[20];
            for (int i = 0; i < titleArray.length(); i++) {

                String title;
                String image;
                String release;
                String description;
                String rating;

                JSONObject singeTitle = titleArray.getJSONObject(i);

                title = singeTitle.getString(TITLE);
                image = "https://image.tmdb.org/t/p/w185/" + singeTitle.getString(PICTURE);
                release = singeTitle.getString(RELEASE);
                description = singeTitle.getString(DESCRIPTION);
                rating = singeTitle.getString(RATING);


                resultStrs[i] = image;
            }

            for (String s : resultStrs) {

            }
            return resultStrs;
        }


        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String titleJsonStr = null;

            String apiKey = "5c50c47fea062190f9f743911ae71820";
            //String sortBy = prefs.getString("popularity.desc", "popularity.desc");

            try {
                // Construct the URL for the movie query
                final String MOVIE_BASE_URL =
                        "https://api.themoviedb.org/3/discover/movie?";
                final String API_PARAM = "api_key";
                final String SORT_PARAM = "sort_by";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_PARAM, apiKey)
                        .appendQueryParameter(SORT_PARAM, params[0])
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
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                titleJsonStr = buffer.toString();

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
            try {

                return getTitleDataFromJson(titleJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            mTitleAdapter.clear();
            if (result != null) {
                for (String singleTitle : result) {
                    mTitleAdapter.add(singleTitle);

                }
            }
        }
    }
}
//this ends the udacity code