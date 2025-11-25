import java.util.List;

public abstract class Movie {

    public enum Rating {G, PG, R13, R16, R18}
    
    protected String title;
    protected Rating rating;
    protected double regPrice;
    protected double vipPrice;
    protected String posterImagePath;
    
    public Movie(String title,
            Rating rating,
            double regPrice,
            double vipPrice,
            String posterImagePath) {
        this.title = title;
        this.rating = rating;
        this.regPrice = regPrice;
        this.vipPrice = vipPrice;
        this.posterImagePath = posterImagePath;
    }
    
    // Getters
    public String getTitle() { return title; }
    public Rating getRating() { return rating; }
    
    public double getRegularPrice() { return regPrice; } 
    
    public double getVipPrice() { return vipPrice; }
    public String getPosterImagePath() { return posterImagePath; }
    
    //If legal na ung age niya if 18+ na ung manunood
    public abstract boolean isAgeLegal(List<Integer> ages);
    
}