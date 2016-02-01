package crappydayproductions.com.popularmovies;

import android.content.Intent;
import android.os.Bundle;
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

public class DetailActivity extends AppCompatActivity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_detail);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        private static final String MOVIE = "Movie";
        private static final String LOG_TAG = DetailFragment.class.getSimpleName();
        private static final String SHARE_HASHTAG = " #SunshineApp";
        private String mDetailStr;

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
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();
            if (id == R.id.action_share) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, mDetailStr + " " + SHARE_HASHTAG);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share)));

                //noinspection SimplifiableIfStatement
                return true;
            }

            return super.onOptionsItemSelected(item);
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            Intent intent = getActivity().getIntent();
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

            if (intent != null && intent.hasExtra("movie")) {
                Movie movie = intent.getParcelableExtra("movie");
                String movieTitle = movie.getTitle();
                String movieImage = movie.getImage();
                String movieDescription = movie.getDescription();
                String movieRating = movie.getRating();
                String movieRelease = movie.getRelease();

                ((TextView) rootView.findViewById(R.id.movie_title))
                        .setText(movieTitle);
                Log.v(LOG_TAG,"Movie Data: " + movieTitle);

                ImageView image = ((ImageView) rootView.findViewById(R.id.movie_image));
                Picasso.with(getActivity()).load(movieImage).into(image);

                ((TextView) rootView.findViewById(R.id.movie_description))
                        .setText(movieDescription);

                ((TextView) rootView.findViewById(R.id.movie_rating))
                        .setText(movieRating);

                ((TextView) rootView.findViewById(R.id.movie_release))
                        .setText(movieRelease);
            } else {
                Toast.makeText(getContext(), "FAIL",Toast.LENGTH_SHORT).show();
            }
            return rootView;
        }
    }
}
