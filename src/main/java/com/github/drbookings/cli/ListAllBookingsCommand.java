package com.github.drbookings.cli;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.drbookings.Booking;
import com.github.drbookings.BookingOrigin;
import com.github.drbookings.BookingsByOrigin;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

@Command(name = "bookings", descriptionHeading = "Print a list of all bookings in given range.", description = { "..",
	".." })
public class ListAllBookingsCommand extends AbstractListCommand implements Runnable {

    @ParentCommand
    DrBookingsCLI parent;

    @Option(names = { "-n", "--sort-by-guest-name" }, description = "Sort the list by guest name.")
    boolean sortByGuestName;

    @Option(names = { "-p", "--payments" }, description = "Print also the gross payments.")
    boolean printGrossPayments;

    public ListAllBookingsCommand() {
	super();

    }

    @Override
    public void run() {
	try {
	    init(parent);
	    Stream<Booking> stream = getBookingsInRange().getAllElements().stream();
	    if (sortByGuestName) {
		stream = stream.sorted(Comparator.comparing(Booking::getGuest).thenComparing(Booking::getCheckOutDate));
	    } else {
		stream = stream.sorted(Comparator.comparing(Booking::getRoom).thenComparing(Booking::getCheckOutDate));
	    }
	    System.out.println(stream.map(this::bookingToString).collect(Collectors.joining("\n")));
	    printBookingCounts(getBookingsInRange());
	} catch (final Exception e) {
	    e.printStackTrace();
	}

    }

    private void printBookingCounts(final BookingsByOrigin<Booking> bookings) {
	for (final Map.Entry<BookingOrigin, Collection<Booking>> e : bookings.getMap().entrySet()) {

	    System.out.println((e.getKey().getName().isEmpty() ? "<>" : e.getKey()) + "\t"
		    + String.format("%4d", e.getValue().size()));
	}

    }

    private String bookingToString(final Booking booking) {
	final StringBuilder sb = new StringBuilder(String.format("%4d", ++bookingCount));
	sb.append("\t");
	sb.append(String.format("%8s", booking.getBookingOrigin()));
	sb.append("\t");
	sb.append(booking.getCheckInDate());
	sb.append(" -> ");
	sb.append(booking.getCheckOutDate());
	sb.append("\t");
	sb.append(String.format("%26s", booking.getGuest()));

	if (printGrossPayments) {
	    sb.append("\t");
	    sb.append(String.format("%6.2f",
		    booking.getPayments().stream().mapToDouble(p -> p.getAmount().getNumber().doubleValue()).sum()));
	}

	return sb.toString();
    }

}
