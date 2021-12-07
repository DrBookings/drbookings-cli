package com.github.drbookings.cli;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.github.drbookings.Booking;
import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.CleaningBeanSer2;
import com.github.drbookings.PaymentDateFilter5;
import com.github.drbookings.SynchronousReadFileTask;
import com.github.drbookings.ser.ExpenseSer;
import com.google.common.collect.Range;

import picocli.CommandLine.Option;

public class AbstractCommand {

    private final BookingsByOrigin<Booking> bookingsInRange;

    private final BookingsByOrigin<Booking> allBookings;

    private final List<CleaningBeanSer2> cleaningsInRange;

    private final List<CleaningBeanSer2> allCleanings;

    private final List<ExpenseSer> allExpenses;

    private final List<ExpenseSer> expensesInRange;

    private DrBookingsCLI parent;

    @Option(names = { "-v", "--file-version" }, description = "Read file in specified format.", defaultValue = "1")
    int fileVersion;

    @Option(names = { "-W", "--write-all" }, description = "Write all to file.")
    String writeAll;

    public AbstractCommand() {
	bookingsInRange = new BookingsByOrigin<>();
	allBookings = new BookingsByOrigin<>();
	cleaningsInRange = new ArrayList<>();
	allCleanings = new ArrayList<>();
	allExpenses = new ArrayList<>();
	expensesInRange = new ArrayList<>();
    }

    public BookingsByOrigin<Booking> getBookingsInRange() {
	return bookingsInRange;
    }

    public BookingsByOrigin<Booking> getAllBookings() {
	return allBookings;
    }

    public List<CleaningBeanSer2> getCleaningsInRange() {
	return cleaningsInRange;
    }

    List<CleaningBeanSer2> getAllCleanings() {
	return allCleanings;
    }

    public List<ExpenseSer> getAllExpenses() {
	return allExpenses;
    }

    public List<ExpenseSer> getExpensesInRange() {
	return expensesInRange;
    }

    protected Range<LocalDate> getDateRange() {
	return Range.closed(Objects.requireNonNull(parent.startDate), Objects.requireNonNull(parent.endDate));
    }

    protected DrBookingsCLI getParent() {
	return parent;
    }

    protected void init(final DrBookingsCLI parent) throws Exception {
	setParent(parent);

	new SynchronousReadFileTask(parent.file) {

	    @Override
	    protected void handleNewBooking(final Booking bbb) {
		allBookings.add(bbb);
		if (PaymentDateFilter5.bookingInRange(getDateRange(), bbb)) {
		    bookingsInRange.add(bbb);
		}
	    }

	    @Override
	    protected void handleNewCleaning(final CleaningBeanSer2 c) {
		allCleanings.add(c);
		if (getDateRange().contains(c.date)) {
		    cleaningsInRange.add(c);
		}
	    }

	    @Override
	    protected void handleNewExpense(final ExpenseSer c) {
		allExpenses.add(c);
		if (getDateRange().contains(c.getDate())) {
		    expensesInRange.add(c);
		}
	    };

	    @Override
	    protected int getFileVersion() {
		return fileVersion;
	    }
	}.run();

    }

    protected void setParent(final DrBookingsCLI parent) {
	this.parent = parent;
    }

}
