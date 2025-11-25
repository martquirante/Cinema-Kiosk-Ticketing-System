import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KioskMain {

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel; 
    private Booking currentBooking;
    private List<Movie> allMovies; 

    private AttractScreenPanel attractScreenPanel;
    private WelcomePanel welcomePanel; 
    private CustomerInfoPanel customerInfoPanel;
    private AgeVerificationPanel ageVerificationPanel; 
    private MovieSelectionPanel movieSelectionPanel; 
    
    private ShowtimeSelectionPanel showtimeSelectionPanel;
    private DiscountEligibilityPanel discountEligibilityPanel;
    private PaymentPanel paymentPanel;
    
    // Declare ang BookingReceiptPanel
    private BookingReceiptPanel bookingReceiptPanel;

    public KioskMain() {
        initializeMovies(); 
        
        //Frame
        frame = new JFrame("Cinema Ticket Reservation System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 800); 
        frame.setLocationRelativeTo(null); 
        frame.setResizable(false);
        
        // For Card Layout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Create new booking
        this.currentBooking = new Booking();
        
        //Initialize ung mga Panels sa mga class
        attractScreenPanel = new AttractScreenPanel(this, allMovies);
        welcomePanel = new WelcomePanel(this);
        customerInfoPanel = new CustomerInfoPanel(this);
        ageVerificationPanel = new AgeVerificationPanel(this); 
        movieSelectionPanel = new MovieSelectionPanel(this); 
        
        // Initialize additional panels
        showtimeSelectionPanel = new ShowtimeSelectionPanel(this);
        discountEligibilityPanel = new DiscountEligibilityPanel(this);
        paymentPanel = new PaymentPanel(this);
        
        // Initialize ang Receipt Panel
        bookingReceiptPanel = new BookingReceiptPanel(this);

        // Add panels to CardLayout
        mainPanel.add(attractScreenPanel, "ATTRACT_SCREEN");
        mainPanel.add(welcomePanel, "WELCOME"); 
        mainPanel.add(customerInfoPanel, "CUSTOMER_INFO");
        mainPanel.add(ageVerificationPanel, "AGE_VERIFY"); 
        mainPanel.add(movieSelectionPanel, "MOVIE_SELECTION"); 
        mainPanel.add(showtimeSelectionPanel, "SHOWTIME_SELECTION");
        mainPanel.add(discountEligibilityPanel, "DISCOUNT_ELIGIBILITY");
        mainPanel.add(paymentPanel, "PAYMENT");
        
        // Add Receipt Panel to CardLayout
        mainPanel.add(bookingReceiptPanel, "RECEIPT");
        
        frame.add(mainPanel);
        frame.setVisible(true);
        
        //To show ung mga ad or mga slide na movie
        cardLayout.show(mainPanel, "ATTRACT_SCREEN");
        attractScreenPanel.startSlideshow();
    }
    
    // Method ng mga poster ng bawat movie ung mga image niya & rated niya
    private void initializeMovies() {
        this.allMovies = new ArrayList<>();
        String path = "images/"; 
        // Siguraduhin na tama ang spelling ng mga file names mo sa folder
        allMovies.add(new genMovie("Coco", Movie.Rating.G, 250, 400, path + "coco_poster.jpeg"));
        allMovies.add(new genMovie("Your Name.", Movie.Rating.PG, 280, 450, path + "ypurName_poster.jpg"));
        allMovies.add(new genMovie("Weathering With You", Movie.Rating.PG, 280, 450, path + "weathringWithYou_poster.jpg"));
        allMovies.add(new genMovie("Quezon", Movie.Rating.PG, 300, 480, path + "quezon_poster.jpg"));
        allMovies.add(new genMovie("F1: The Movie", Movie.Rating.PG, 350, 550, path + "F1Movie_poster.jpg"));
        allMovies.add(new genMovie("The Last 10 Years", Movie.Rating.PG, 280, 450, path + "The Last 10yrs_poster.jpg"));
        allMovies.add(new genMovie("Meet, Greet & Bye", Movie.Rating.PG, 300, 480, path + "meet,greet,bye_poster.jpg"));
        
        // Rated Movies
        allMovies.add(new RatedMovie("Now You See Me: Now You Don't", Movie.Rating.R13, 320, 500, path + "NowYouSeeMeNowYouDont_poster.jpg"));
        allMovies.add(new RatedMovie("World War Z", Movie.Rating.R13, 350, 550, path + "WordWarZ_Poster.jpg"));
        allMovies.add(new RatedMovie("My First Client", Movie.Rating.R16, 350, 550, path + "my1stClient_poster.jpg"));
        allMovies.add(new RatedMovie("Oldboy", Movie.Rating.R18, 350, 550, path + "oldBoy_poster.jpg"));
    }
    
    // Getter para ma-access ng ibang panel ang booking data
    public Booking getCurrentBooking() {
        return currentBooking;
    }

    //Navigation Methods
    
    public void startBooking() {
        attractScreenPanel.stopSlideshow();
        welcomePanel.resetPanel(); 
        cardLayout.show(mainPanel, "WELCOME"); 
    }

    public void startNewBooking() {
        this.currentBooking = new Booking();
        welcomePanel.resetPanel(); 
        cardLayout.show(mainPanel, "ATTRACT_SCREEN");
        attractScreenPanel.startSlideshow(); 
    }
 
    public void showCustomerInfo(int count) {
        currentBooking.setNumberOfViewers(count); 
        System.out.println("Number of viewers set to: " + count); 
        customerInfoPanel.setupPanel(count); 
        cardLayout.show(mainPanel, "CUSTOMER_INFO"); 
    }
    
    public void showWelcome() {
        welcomePanel.resetPanel(); 
        cardLayout.show(mainPanel, "WELCOME");
    }
    
    public void showCustomerInfoPanel() {
        customerInfoPanel.setupPanel(currentBooking.getNumberOfViewers());
        cardLayout.show(mainPanel, "CUSTOMER_INFO");
    }
    
    public void showAgeVerificationPanel() {
        ageVerificationPanel.setupPanel(currentBooking.getCustomerNames()); 
        cardLayout.show(mainPanel, "AGE_VERIFY");
    }
   
    public void showAgeVerification(List<String> allNames) {
       currentBooking.setCustomerNames(allNames);
       System.out.println("Customer names set to: " + allNames);
       ageVerificationPanel.setupPanel(allNames); 
       cardLayout.show(mainPanel, "AGE_VERIFY"); 
    }
   
    public void showMovieSelection(List<Integer> ages) {
        // Kung galing sa back button at null ang ages, wag i-clear
        if (ages != null) {
            currentBooking.getAges().clear(); 
            for (int age : ages) {
                currentBooking.addAge(age);
            }
            System.out.println("Ages saved to booking: " + currentBooking.getAges());
            movieSelectionPanel.filterAndDisplayMovies(currentBooking.getAges(), allMovies); 
        }
        cardLayout.show(mainPanel, "MOVIE_SELECTION"); 
    }
    
    public void showShowtimeSelection(Movie movie) {
        if (movie != null) {
            currentBooking.setSelectedMovie(movie); 
            System.out.println("Selected movie for booking: " + movie.getTitle());
            showtimeSelectionPanel.setupPanel(movie);
            cardLayout.show(mainPanel, "SHOWTIME_SELECTION");
        } else {
            // Back button logic
            cardLayout.show(mainPanel, "MOVIE_SELECTION");
        }
    }
    
    public void showDiscountEligibility(int totalTickets) {
        discountEligibilityPanel.setTotalTickets(totalTickets);
        cardLayout.show(mainPanel, "DISCOUNT_ELIGIBILITY");
    }

    void proceedToPayment(int studentSeniorTickets) {
        // Set discount info
        currentBooking.setDiscounted(studentSeniorTickets > 0);
        currentBooking.setDiscountCount(studentSeniorTickets); 
        
        // Calculate final price (Kailangan ito para makuha ng payment panel ang total)
        currentBooking.computeTotalPrice();
        
        // Show Payment Panel
        paymentPanel.setupPanel();
        cardLayout.show(mainPanel, "PAYMENT");
    }

    // ❗ BAGO: Ito ang tatawagin ng PaymentPanel pagkatapos magbayad
    public void completeBooking() {
        // Generate Receipt data
        bookingReceiptPanel.generateReceipt();
        
        // Show the Receipt screen
        cardLayout.show(mainPanel, "RECEIPT");
    }
}