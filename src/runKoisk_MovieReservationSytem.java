import javax.swing.SwingUtilities;

public class runKoisk_MovieReservationSytem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Tatawagin ung KioskMain
                // Gumagawa ng instance ng KioskMain class.
                new KioskMain(); 
            }
        });
    }
}