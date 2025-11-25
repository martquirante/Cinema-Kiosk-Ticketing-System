import java.util.List;
import java.util.ArrayList;

public class Booking {

    private List<String> customerNames;
    private int numberOfViewers;
    private List<Integer> ages;
    private Movie selectedMovie;
    private String selectedShowtime;
    private List<String> selectedSeats;
    private String seatType; // "Regular" or "VIP"
    private boolean isDiscounted;
    private int discountCount;
    private double totalAmount;
    private String paymentMethod;
    private String bookingReference;

    // Constructor
    public Booking() {
        this.customerNames = new ArrayList<>();
        this.ages = new ArrayList<>();
        this.selectedSeats = new ArrayList<>();
        this.numberOfViewers = 1; 
        this.isDiscounted = false;
        this.discountCount = 0;
        System.out.println("New blank booking created."); 
    }

    //Getters at Setters
    public List<String> getCustomerNames() { return customerNames; }
    public void setCustomerNames(List<String> names) { 
        this.customerNames.clear();
        this.customerNames.addAll(names);
    }
    
    public int getNumberOfViewers() { return numberOfViewers; }
    public void setNumberOfViewers(int num) { this.numberOfViewers = num; }

    public List<Integer> getAges() { return ages; }
    public void addAge(int age) { this.ages.add(age); }

    public Movie getSelectedMovie() { return selectedMovie; }
    public void setSelectedMovie(Movie movie) { this.selectedMovie = movie; }
    
    public String getSelectedShowtime() { return selectedShowtime; }
    public void setSelectedShowtime(String showtime) { this.selectedShowtime = showtime; }

    public boolean isDiscounted() { return isDiscounted; }
    public void setDiscounted(boolean discounted) { isDiscounted = discounted; }
    
    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }
    
    public List<String> getSelectedSeats() { return selectedSeats; }
    public void addSelectedSeat(String seat) { this.selectedSeats.add(seat); }
    
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String method) { this.paymentMethod = method; }
    
    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String ref) { this.bookingReference = ref; }

    // --- FIX: Implemented this method correctly ---
    public void setDiscountCount(int count) {
        this.discountCount = count;
    }
    public int getDiscountCount() {
        return discountCount;
    }

    // Method para sa Step 14: Compute Total Price
    public double computeTotalPrice() {
        if (selectedMovie == null) return 0;

        double basePrice = (seatType != null && seatType.equals("VIP")) ? 
                           selectedMovie.getVipPrice() : 
                           selectedMovie.getRegularPrice(); 
        
        double discountAmount = 0;
        
        // FIX: Logic for calculation using discountCount
        if (isDiscounted && discountCount > 0) {
            // 20% discount per eligible ticket
            discountAmount = (basePrice * 0.20) * discountCount;
        }

        this.totalAmount = (basePrice * numberOfViewers) - discountAmount;
        return this.totalAmount;
    }
}