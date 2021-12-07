package com.github.drbookings.cli;

import java.time.LocalDate;
import java.util.Collection;

import com.github.drbookings.DatesSupplier;
import com.google.common.collect.Range;

public class BusyRoomNightsSupplier {

    private final Range<LocalDate> dateRange;

    private final short roomCount;

    public BusyRoomNightsSupplier(final Range<LocalDate> dateRange, final short roomCount) {
	super();
	this.dateRange = dateRange;
	this.roomCount = roomCount;
    }

    public long apply(final Collection<? extends DatesSupplier> dateSuplier) {
	final long allBookedNights = new SimpleNumberOfNightsCounter(getDateRange(), dateSuplier).apply();
	return allBookedNights * getRoomCount();

    }

    public Range<LocalDate> getDateRange() {
	return dateRange;
    }

    public short getRoomCount() {
	return roomCount;
    }

}
