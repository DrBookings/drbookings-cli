package com.github.drbookings.cli;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import com.github.drbookings.ExpenseBean;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "statistics", descriptionHeading = "..", description = { "..", ".." })
public class StatisticsCommand extends AbstractFilterableByOriginCommand implements Runnable {

    public static Collection<? extends ExpenseBean> getCommonExpenses2018August() {

	/*
	 * Nurnoch laufende Kosten. Was unterscheidet eine mehrere-tausend-Euro
	 * Investition von neuen Glästern?
	 */

	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 8, 31), 0.45),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 8, 31), 42.57),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 8, 31), 154.35),
		//
		new ExpenseBean("KNAPPSCHAFT-BAHN-SEE", LocalDate.of(2018, 8, 16), 30.16),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 8, 10), 4 * 375),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 07, 5), 4 * 45)
	//
	));

	return expenses;
    }

    public static Collection<? extends ExpenseBean> getCommonExpenses2018Sept() {

	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 9, 28), 0.27),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 9, 28), 6.57),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 9, 28), 154.35),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 9, 10), 4 * 375),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 9, 5), 4 * 45)
	//
	));

	return expenses;
    }

    public static Collection<? extends ExpenseBean> getCommonExpenses2018Okt() {

	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 10, 31), 0.63),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 10, 31), 21.80),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 10, 31), 154.35),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 10, 10), 4 * 375),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 10, 5), 4 * 45)
	//
	));

	return expenses;
    }

    public static Collection<? extends ExpenseBean> getCommonExpenses2019Jan() {
	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2019, 01, 31), .81),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2019, 01, 31), 21.80),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2019, 01, 31), 154.35),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2019, 01, 7), 4 * 45),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2019, 01, 02), 4 * 375)
	//

	));

	return expenses;
    }

    public static Collection<? extends ExpenseBean> getCommonExpenses2018Dec() {

	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 12, 31), 1.80),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 12, 31), 21.80),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 12, 31), 154.35),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 12, 10), 4 * 375),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 12, 5), 4 * 45)
	//

	));

	return expenses;
    }

    public static Collection<? extends ExpenseBean> getCommonExpenses2018Nov() {

	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 11, 30), 0.90),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 11, 30), 21.80),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 11, 30), 154.35),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 11, 12), 4 * 375),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 11, 5), 4 * 45),
		//
		new ExpenseBean("Stadt Heidelberg, Grundsteuer", LocalDate.of(2018, 11, 7), 173.04)
	//
	));

	return expenses;
    }

    @Option(names = { "-c", "--cheat" }, description = "Put Other's costs to Booking and Airbnb.")
    boolean cheat;

    public static String getServiceFeesString(final String origin, final MonetaryAmount serviceFees) {
	return getServiceFeesString(origin, serviceFees.with(Monetary.getDefaultRounding()).getNumber().doubleValue());
    }

    public static String getServiceFeesString(final String origin, final double serviceFees) {
	final StringBuilder sb = new StringBuilder();
	sb.append(String.format("%-8s", origin) + "Service Fees:    " + String.format("%8.2f", serviceFees));
	return sb.toString();
    }

    @Override
    public void run() {
	super.run();
	try {
	    System.out.println(getExpensesInRange().stream().map(Object::toString).collect(Collectors.joining("\n")));
	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

}
