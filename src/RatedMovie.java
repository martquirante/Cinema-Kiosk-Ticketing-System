import java.util.List;

public class RatedMovie extends Movie {

    public RatedMovie(String title, Rating rating, double regPrice, double vipPrice, String posterImagePath) {
        super(title, rating, regPrice, vipPrice, posterImagePath);
    }

    @Override
    public boolean isAgeLegal(List<Integer> ages) {
        int movieMinAge = 1;
        // Kunin ang minimum age requirement ng movie base sa rating
        switch (this.getRating()) {
            case G:
            case PG:
                movieMinAge = 0; // G at PG ay para sa lahat
                break;
            case R13:
                movieMinAge = 13;
                break;
            case R16:
                movieMinAge = 16;
                break;
            case R18:
                movieMinAge = 18;
                break;
        }

        // Kung may isang viewer na mas bata sa minimum age ng movie, hindi legal
        for (int age : ages) {
            if (age < movieMinAge) {
                return false; 
            }
        }
        return true; // Legal kung walang viewer na mas bata
    }
}