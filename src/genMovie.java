import java.util.List;

//TYis class is for G or genral rated na movie
public class genMovie extends Movie { //  Dapat 'extends Movie'

    public genMovie(String title, Rating rating, double regPrice, double vipPrice, String posterImagePath) {
        super(title, rating, regPrice, vipPrice, posterImagePath);
    }

    @Override
    public boolean isAgeLegal(List<Integer> ages) {
        // Ang General movies ay laging legal, regardless of age
        // Ang 'G' rated movies ay para sa lahat ng edad
        return true; 
    }
}