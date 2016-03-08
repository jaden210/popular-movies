package crappydayproductions.com.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jaden_000 on 1/29/2016.
 */
public class Movie implements Parcelable {
    private String title;
    private String image;
    private String description;
    private String rating;
    private String release;
    private String poster;
    long id;
    boolean favorites;
    List<Trailer> trailers;
    List<Review> reviews;

    public String getTitle() {
        return title;
    }

    public String getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public String getRating() {
        return rating;
    }

    public String getRelease() {
        return release;
    }

    public String getPoster() {
        return poster;
    }

    public long getId() {
        return id;
    }

    public Movie(String title, String image, String description, String rating, String release, String poster, long id) {
        this.title = title;
        this.image = image;
        this.description = description;
        this.rating = rating;
        this.release = release;
        this.poster = poster;
        this.id = id;
        this.favorites = false;
        trailers = new ArrayList<Trailer>();
        reviews = new ArrayList<Review>();
    }

    //Movie constructor used by parcelable
    private Movie(Parcel in) {
        title = in.readString();
        image = in.readString();
        description = in.readString();
        rating = in.readString();
        release = in.readString();
        poster = in.readString();
        id = in.readLong();
        favorites = in.readByte() != 0;
        trailers = in.readArrayList(Trailer.class.getClassLoader());
        reviews = in.readArrayList(Review.class.getClassLoader());
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
        out.writeString(poster);
        out.writeLong(id);
        out.writeByte((byte) (favorites ? 1 : 0));
        out.writeTypedList(trailers);
        out.writeTypedList(reviews);
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    //Trailer
    public static class Trailer implements Parcelable {
        long movieId;
        String tSource;
        String key;

        public Trailer(long movieId, String tSource, String key) {
            this.movieId = movieId;
            this.tSource = tSource;
            this.key = key;
        }

        private Trailer(Parcel in) {
            movieId = in.readLong();
            tSource = in.readString();
            key = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(movieId);
            dest.writeString(tSource);
            dest.writeString(key);
        }

        public static final Parcelable.Creator<Trailer> CREATOR = new Parcelable.Creator<Trailer>() {
            @Override
            public Trailer createFromParcel(Parcel src) {
                return new Trailer(src);
            }

            @Override
            public Trailer[] newArray(int size) {
                return new Trailer[size];
            }
        };
    }

    //Review
    public static class Review implements Parcelable {
        long rId;
        String rAuthor;
        String rContent;
        String rUrl;

        public Review(long rId, String rAuthor, String rContent, String rUrl) {
            this.rId = rId;
            this.rAuthor = rAuthor;
            this.rContent = rContent;
            this.rUrl = rUrl;
        }

        private Review(Parcel in) {
            rId = in.readLong();
            rAuthor = in.readString();
            rContent = in.readString();
            rUrl = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flag) {
            dest.writeLong(rId);
            dest.writeString(rAuthor);
            dest.writeString(rContent);
            dest.writeString(rUrl);
        }

        public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {

            @Override
            public Review createFromParcel(Parcel src) {
                return new Review(src);
            }

            @Override
            public Review[] newArray(int size) {
                return new Review[size];
            }
        };
    }
}

