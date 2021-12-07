package com.github.drbookings.cli;

import javax.money.MonetaryAmount;

import com.github.drbookings.DefaultServiceFeesSupplier2;

import picocli.CommandLine.Command;

@Command(name = "commission", descriptionHeading = "Print the overall commission.", description = { "..", ".." })
public class PrintCommissionCommand extends AbstractFilterableByOriginCommand implements Runnable {

    @Override
    public void run() {
	super.run();
	try {
	    final MonetaryAmount serviceFees = new DefaultServiceFeesSupplier2(getDateRange()).apply(getBookings());
	    System.out.println(serviceFees);
	} catch (final Exception e) {
	    e.printStackTrace();
	}

    }

}
