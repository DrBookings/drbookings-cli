package com.github.drbookings.cli;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.google.common.collect.Range;

public class NightsCounter {

    private final Range<LocalDate> dateRange;

    public NightsCounter(final Range<LocalDate> dateRange) {
	super();
	this.dateRange = dateRange;
    }

    public long apply() {
	final LocalDate a = getDateRange().lowerEndpoint();
	// upper endpoint is exclusive
	final LocalDate b = getDateRange().upperEndpoint().plusDays(1);
	final long allNights = ChronoUnit.DAYS.between(a, b);
	return allNights;
    }

    public Range<LocalDate> getDateRange() {
	return dateRange;
    }

}
