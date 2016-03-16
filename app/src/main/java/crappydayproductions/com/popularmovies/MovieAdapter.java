package crappydayproductions.com.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jaden_000 on 1/29/2016.
 */
public class MovieAdapter extends ArrayAdapter<Movie> {

    private Context mContext;
    private int layoutResource;
    private ArrayList<Movie> mGridData = new ArrayList<Movie>();
    private int position;
    private View convertView;
    private ViewGroup parent;

    public MovieAdapter(Context mContext, int layoutResource, ArrayList<Movie> mGridData) {
        super(mContext, layoutResource, mGridData);
        this.layoutResource = layoutResource;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    public void setGridData (ArrayList<Movie> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;


        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResource,parent,false);
            holder = new ViewHolder();
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_title_textview);
            holder.imageView.setAdjustViewBounds(true);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Movie item = mGridData.get(position);
        Picasso.with(mContext).load(item.getImage()).into(holder.imageView);
        return row;
    }

    static class ViewHolder {
        ImageView imageView;
    }
}
