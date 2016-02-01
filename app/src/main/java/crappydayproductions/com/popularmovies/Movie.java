package crappydayproductions.com.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jaden_000 on 1/29/2016.
 */
public class Movie implements Parcelable {
    private String title;
    private String image;
    private String description;
    private String rating;
    private String release;

    public String getTitle() {
        return title;
    }
    public String getImage() {
        return image;
    }
    public String getDescription() {
        return description;
    }
    public String getRating(){
        return rating;
    }
    public String getRelease(){
        return release;
    }

    public Movie(String title, String image, String description,String rating, String release) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.rating = rating;
        this.release = release;
    }
    public Movie(Parcel parcel){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(title);
        out.writeString(image);
        out.writeString(description);
        out.writeString(rating);
        out.writeString(release);
    }
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };
}
