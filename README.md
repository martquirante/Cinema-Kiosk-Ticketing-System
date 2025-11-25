# 🎬 CinemaBook: Kiosk Movie Reservation System

**CinemaBook** is a Java-based desktop application that simulates a modern self-service cinema kiosk. Built using **Java Swing** and **AWT**, this project demonstrates Object-Oriented Programming (OOP) principles to create a seamless booking experience—from movie selection to digital receipt generation.

Compalition of our project in OOP 2nd year, 1st of Studying of BSIT

Develop by: Raymart Quirante

Also my group members:
Edgar Guimayen,
Mark James Malayas, &
John Viel Padayao

## 🚀 Project Overview

Unlike standard Java Swing applications, CinemaBook features a customized UI with rounded corners, gradients, and a card-based navigation flow. It handles complex logic such as age restrictions (MTRCB ratings), seat availability, and discount computations for students and senior citizens.

## ✨ Key Features

* **📺 Attract & Welcome Screen:** A slideshow loop to attract customers and a dynamic grouping selection (Solo vs. Companion).
* **🔞 Smart Age Verification:** Automatically filters available movies based on the ages of the viewers.
    * *Logic:* If a viewer is underage for an R-13, R-16, or R-18 movie, the system prevents booking for that specific title.
* **💺 Interactive Seat Selection:**
    * Visual grid layout for seat selection.
    * Support for **Regular** and **VIP** seat tiers with different pricing.
    * Prevents selecting occupied seats or exceeding the viewer count.
* **💰 Discount Logic:** Automatically calculates a **20% discount** for eligible Students and Senior Citizens.
* **💳 Payment Simulation:** Supports "Cash at Counter" and "GCash" payment flows with a dynamic UI.
* **🧾 Digital Receipt Generation:**
    * Generates a visual ticket receipt at the end of the transaction.
    * **Auto-Save:** Automatically saves the receipt as a `.png` file in the `SOLD_TICKET_FILES` folder for record-keeping.

## 🛠️ Tech Stack

* **Language:** Java (JDK 8+)
* **GUI Framework:** Java Swing (JFrame, JPanel, JButton)
* **Graphics:** Java AWT (Graphics2D for custom rounded panels and rendering)
* **Architecture:** Modular design using `CardLayout` for screen navigation.

## 📂 Project Structure

The project is organized into modular panels managed by a central controller:

* `KioskMain.java` - The main controller handling navigation and data state.
* `Movie.java` / `RatedMovie.java` - OOP classes handling polymorphism for movie ratings.
* `Booking.java` - Encapsulates transaction details (seats, price, customers).
* **Panels:**
    * `AttractScreenPanel` - Slideshow.
    * `AgeVerificationPanel` - Dynamic input fields for age checks.
    * `ShowtimeSelectionPanel` - Seat grid and time selection.
    * `BookingReceiptPanel` - Final summary and image generation.

## 🚀 How to Run

1.  **Clone the repository** or download the source code.
2.  **Verify Assets:** Ensure the `images/` folder exists in the root directory and contains the required assets (posters, icons, background).
3.  **Compile and Run:**
    * Main Class: `runKoisk_MovieReservationSytem.java`

```bash
javac *.java
java runKoisk_MovieReservationSytem
