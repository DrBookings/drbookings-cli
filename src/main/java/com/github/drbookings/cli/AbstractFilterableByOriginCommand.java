package com.github.drbookings.cli;

import java.util.ArrayList;
import java.util.Collection;

import com.github.drbookings.Booking;

import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

public class AbstractFilterableByOriginCommand extends AbstractCommand implements Runnable {

    @ParentCommand
    DrBookingsCLI parent;

    @Option(names = { "-b", "--booking-origin" }, description = "Filter for Bookings from Booking.com.")
    boolean booking;

    @Option(names = { "-a", "--airbnb-origin" }, description = "Filter for Bookings from Airbnb.com.")
    boolean airbnb;

    @Option(names = { "-o", "--other-origin" }, description = "Filter for Other Bookings.")
    boolean other;

    private Collection<Booking> bookings = new ArrayList<>();

    protected Collection<Booking> getBookings() {
	return bookings;
    }

    @Override
    public void run() {
	try {
	    init(parent);
	    bookings = new ArrayList<>();
	    if (booking) {
		bookings.addAll(getBookingsInRange().getBookingElements());
	    }
	    if (airbnb) {
		bookings.addAll(getBookingsInRange().getAirbnbElements());
	    }
	    if (other) {
		bookings.addAll(getBookingsInRange().getOtherElements());
	    }
	    if (!booking && !airbnb && !other) {
		bookings.addAll(getBookingsInRange().getAllElements());
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}

    }

}
