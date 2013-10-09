package recsys.pb.main;

public class ratingsData {
	
	public int userId;
    public int movieId;
    public double ratingValue;

    // Getter
    public int getUserId() {
        return userId;
    }
    
    public int getMovieId() {
        return movieId;
    }
    
    public double getRatingValue() {
        return ratingValue;
    }

    // Setter
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public void setRatingValue(double ratingValue) {
        this.ratingValue = ratingValue;
    }
}
