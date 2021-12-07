package com.github.drbookings.cli;

import com.github.drbookings.DefaultGrossPaymentsSupplier2;

import picocli.CommandLine.Command;

@Command(name = "gross-payments", descriptionHeading = "Print a list of gross payments.", description = { "..", ".." })
public class PrintGrossPaymentsCommand extends AbstractFilterableByOriginCommand implements Runnable {

    @Override
    public void run() {
	super.run();
	try {
	    System.out.println(new DefaultGrossPaymentsSupplier2(getDateRange()).apply(getBookings()));
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

}
