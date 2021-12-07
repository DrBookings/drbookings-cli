package com.github.drbookings.cli;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;

import com.github.drbookings.Booking;
import com.github.drbookings.CleaningBeanSer2;
import com.github.drbookings.ExpenseSerFactory;
import com.github.drbookings.io.ToXMLWriter;
import com.github.drbookings.ser.BookingBeanSer2Factory;
import com.github.drbookings.ser.DataStoreCoreSer2;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "cleanings", descriptionHeading = "Print (and modify) cleanings in given range.", description = { "..",
	".." }, subcommands = {})
public class CleaningsCommand extends AbstractCleaningsCommand implements Runnable {

    @Option(names = { "-S",
	    "--print-sum-tax-relevant" }, description = "Print also the sum of all tax-relevant cleaning costs.")
    boolean printSumTaxRelevant;

    @Option(names = { "-s",
	    "--print-sum-tax-irrelevant" }, description = "Print also the sum of all tax-irrelevant cleaning costs.")
    boolean printSumTaxIrrelevant;

    public CleaningsCommand() {
	super();

    }

    @Override
    public void run() {
	super.run();
	if (writeChanges != null) {
	    writeToFile(getBookingsInRange().getAllElements(), getCleaningsInRange(), writeChanges);
	}
	if (writeAll != null) {
	    writeToFile(getAllBookings().getAllElements(), getAllCleanings(), writeAll);
	}
	if (printSumTaxIrrelevant) {
	    System.out.println("Sum tax irrelevant: " + getCleaningsInRange().stream().filter(cb -> !cb.tax)
		    .mapToDouble(cb -> Double.parseDouble(cb.cleaningCosts)).sum());
	}

    }

    private static void writeToFile(final Collection<Booking> bookings, final Collection<CleaningBeanSer2> cleanings,
	    final String path) {
	final DataStoreCoreSer2 data = new DataStoreCoreSer2();
	new ExpenseSerFactory();
	data.getExpenses().addAll(ExpenseSerFactory.build(StatisticsCommand.getCommonExpenses2018August()));
	data.getExpenses().addAll(ExpenseSerFactory.build(StatisticsCommand.getCommonExpenses2018Sept()));
	data.getExpenses().addAll(ExpenseSerFactory.build(StatisticsCommand.getCommonExpenses2018Okt()));
	data.getExpenses().addAll(ExpenseSerFactory.build(StatisticsCommand.getCommonExpenses2018Nov()));
	data.getExpenses().addAll(ExpenseSerFactory.build(StatisticsCommand.getCommonExpenses2018Dec()));
	data.getExpenses().addAll(ExpenseSerFactory.build(StatisticsCommand.getCommonExpenses2019Jan()));
	data.setCleaningSer(cleanings);
	data.setBookingSer(new BookingBeanSer2Factory().build(bookings));
	try {
	    new ToXMLWriter().write(data, Paths.get(path));
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println("Wrote (modified) data to " + Paths.get(path));

    }

    @Override
    protected void handleCleaningEntry(final int index, final CleaningBeanSer2 cleaningBeanSer2) {
	System.out.println(String.format("%4d", (index + 1)) + "\t" + toString(cleaningBeanSer2));

    }

    String toString(final CleaningBeanSer2 c) {
	final StringBuilder sb = new StringBuilder();
	sb.append(String.format("%4s", "room " + c.room));
	sb.append("\t");
	sb.append(String.format("%12s", c.date.toString()));
	sb.append("\t");
	sb.append(String.format("%8s", c.name));
	sb.append("\t");
	sb.append(String.format("%6s", c.cleaningCosts));
	sb.append("\t");
	sb.append(String.format("%12s", (c.tax ? "tax-relevant " : " ")));
	return sb.toString();
    }

}
