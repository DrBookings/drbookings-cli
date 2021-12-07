package com.github.drbookings.cli;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.github.drbookings.BookingOrigin;
import com.github.drbookings.SettingsManager;

import picocli.CommandLine.Command;

@Command(name = "nights", descriptionHeading = "..", description = { "..", ".." })
public class NightsCommand extends AbstractFilterableByOriginCommand implements Runnable {

    @Override
    public void run() {
	super.run();
	try {

	    final LocalDate a = getDateRange().lowerEndpoint();
	    final LocalDate b = getDateRange().upperEndpoint().plusDays(1);

	    final double allNights = ChronoUnit.DAYS.between(a, b);
	    final double allBookedNights = new SimpleNumberOfNightsCounter(getDateRange(), getBookings()).apply();
	    final double bookingNights = new SimpleNumberOfNightsCounter(getDateRange(), getBookings())
		    .setOrigin(new BookingOrigin("Booking")).apply();
	    final double airbnbNights = new SimpleNumberOfNightsCounter(getDateRange(), getBookings())
		    .setOrigin(new BookingOrigin("Airbnb")).apply();
	    final double otherNights = new SimpleNumberOfNightsCounter(getDateRange(), getBookings())
		    .setOrigin(new BookingOrigin("")).apply();

	    final double occupancyRateAll = (allBookedNights
		    / (allNights * SettingsManager.getInstance().getNumberOfRooms())) * 100;
	    final double occupancyRateBooking = (bookingNights / allBookedNights) * 100;
	    final double occupancyRateAirbnb = (airbnbNights / allBookedNights) * 100;
	    final double occupancyRateOther = (otherNights / allBookedNights) * 100;

	    System.out.println("Total   occupancy rate of " + String.format("%6.2f", occupancyRateAll) + "% ("
		    + String.format("%3.0f", allBookedNights) + "/"
		    + String.format("%3.0f", (allNights * SettingsManager.getInstance().getNumberOfRooms()))
		    + " room nights busy)");

	    System.out.println("Booking occupancy rate of " + String.format("%6.2f", occupancyRateBooking) + "% ("
		    + String.format("%3.0f", bookingNights) + "/" + String.format("%3.0f", allBookedNights)
		    + " room nights busy)");

	    System.out.println("Airbnb  occupancy rate of " + String.format("%6.2f", occupancyRateAirbnb) + "% ("
		    + String.format("%3.0f", airbnbNights) + "/" + String.format("%3.0f", allBookedNights)
		    + " room nights busy)");

	    System.out.println("Other  occupancy rate of " + String.format("%6.2f", occupancyRateOther) + "% ("
		    + String.format("%3.0f", otherNights) + "/" + String.format("%3.0f", allBookedNights)
		    + " room nights busy)");

	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }
}
