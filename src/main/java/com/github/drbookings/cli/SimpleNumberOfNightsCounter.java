package com.github.drbookings.cli;

import java.time.LocalDate;
import java.util.Collection;

import com.github.drbookings.BookingOrigin;
import com.github.drbookings.DateRange;
import com.github.drbookings.DatesSupplier;
import com.github.drbookings.OriginProvider;
import com.google.common.collect.Range;

public class SimpleNumberOfNightsCounter {

    private final Range<LocalDate> dateRange;

    private final Collection<? extends DatesSupplier> dateSuplier;

    private BookingOrigin origin;

    public SimpleNumberOfNightsCounter(final Range<LocalDate> dateRange,
	    final Collection<? extends DatesSupplier> dateSuplier) {
	super();
	this.dateRange = dateRange;
	this.dateSuplier = dateSuplier;

    }

    public BookingOrigin getOrigin() {
	return origin;
    }

    public SimpleNumberOfNightsCounter setOrigin(final BookingOrigin origin) {
	this.origin = origin;
	return this;
    }

    protected Range<LocalDate> getDateRange() {
	return dateRange;
    }

    public long apply() {
	long cnt = 0;
	for (final DatesSupplier bb : dateSuplier) {
	    for (final LocalDate d : new DateRange(bb.getCheckInDate(), bb.getCheckOutDate())) {
		if (d.equals(bb.getCheckOutDate())) {
		    // do not count check out date
		    continue;
		}
		if ((bb instanceof OriginProvider)) {
		    if ((origin != null)) {
			if (!origin.equals(((OriginProvider) bb).getBookingOrigin())) {
			    // ignore mismatching origin
			    continue;
			}
		    }
		}

		if (getDateRange().contains(d)) {
		    cnt++;
		}
	    }
	}
	return cnt;
    }

}
