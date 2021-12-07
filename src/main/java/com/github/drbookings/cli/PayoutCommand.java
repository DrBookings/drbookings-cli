package com.github.drbookings.cli;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.javamoney.moneta.Money;

import com.github.drbookings.BookingOrigin;
import com.github.drbookings.CleaningBeanFactory;
import com.github.drbookings.CleaningEntriesByOrigin;
import com.github.drbookings.CleaningExpensesFactory2;
import com.github.drbookings.DefaultGrossPaymentsSupplier2;
import com.github.drbookings.DefaultServiceFeesSupplier2;
import com.github.drbookings.Payments2;
import com.github.drbookings.SettingsManager;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "payout", descriptionHeading = "..", description = { "..", ".." })
public class PayoutCommand extends AbstractFilterableByOriginCommand implements Runnable {

    @Option(names = { "-c", "--cheat" }, description = "Put Other's costs to Booking and Airbnb.")
    boolean cheat;

    @Override
    public void run() {
	super.run();

	try {
	    final MonetaryAmount grossPaymentsTotal = new DefaultGrossPaymentsSupplier2(getDateRange())
		    .apply(getBookings());
	    final MonetaryAmount grossPaymentsBooking = new DefaultGrossPaymentsSupplier2(getDateRange())
		    .apply(getBookingsInRange().getBookingElements());
	    final MonetaryAmount grossPaymentsAirbnb = new DefaultGrossPaymentsSupplier2(getDateRange())
		    .apply(getBookingsInRange().getAirbnbElements());
	    final MonetaryAmount grossPaymentsOther = new DefaultGrossPaymentsSupplier2(getDateRange())
		    .apply(getBookingsInRange().getOtherElements());

	    System.out.println();
	    System.out.println(
		    "Payout Chain " + getDateRange().lowerEndpoint() + " -> " + getDateRange().upperEndpoint() + ":");
	    System.out.println();
	    System.out.println("Total   Gross Payments:  " + String.format("%8.2f",
		    grossPaymentsTotal.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));
	    // System.out.println("----------------------------------------------------");
	    System.out.println("Booking Gross Payments:  " + String.format("%8.2f",
		    grossPaymentsBooking.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));
	    System.out.println("Airbnb  Gross Payments:  " + String.format("%8.2f",
		    grossPaymentsAirbnb.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));
	    System.out.println("Other   Gross Payments:  " + String.format("%8.2f",
		    grossPaymentsOther.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));

	    final MonetaryAmount serviceFeesAll = new DefaultServiceFeesSupplier2(getDateRange()).apply(getBookings());
	    final MonetaryAmount serviceFeesBooking = new DefaultServiceFeesSupplier2(getDateRange())
		    .apply(getBookingsInRange().getBookingElements());
	    final MonetaryAmount serviceFeesAirbnb = new DefaultServiceFeesSupplier2(getDateRange())
		    .apply(getBookingsInRange().getAirbnbElements());
	    final MonetaryAmount serviceFeesOther = new DefaultServiceFeesSupplier2(getDateRange())
		    .apply(getBookingsInRange().getOtherElements());

	    System.out.println(StatisticsCommand.getServiceFeesString("Total", serviceFeesAll));
	    System.out.println(StatisticsCommand.getServiceFeesString("Booking", serviceFeesBooking));
	    System.out.println("Airbnb  Service Fees:    " + String.format("%8.2f",
		    serviceFeesAirbnb.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));
	    System.out.println("Other   Service Fees:    " + String.format("%8.2f",
		    serviceFeesOther.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));

	    final double allNights = new NightsCounter(getDateRange()).apply();
	    final double allBookedNights = new SimpleNumberOfNightsCounter(getDateRange(), getBookings()).apply();
	    double allBookedNightsCheat = 0;

	    final double bookingNights = new SimpleNumberOfNightsCounter(getDateRange(), getBookings())
		    .setOrigin(new BookingOrigin("Booking")).apply();
	    final double airbnbNights = new SimpleNumberOfNightsCounter(getDateRange(), getBookings())
		    .setOrigin(new BookingOrigin("Airbnb")).apply();
	    final double otherNights = new SimpleNumberOfNightsCounter(getDateRange(), getBookings())
		    .setOrigin(new BookingOrigin("")).apply();

	    if (cheat) {
		allBookedNightsCheat = allBookedNights - otherNights;
	    } else {

	    }

	    final double occupancyRateAll = (allBookedNights
		    / (allNights * SettingsManager.getInstance().getNumberOfRooms())) * 100;
	    double occupancyRateAllCheat = 0;
	    final double occupancyRateBooking = (bookingNights / (cheat ? allBookedNightsCheat : allBookedNights))
		    * 100;
	    final double occupancyRateAirbnb = (airbnbNights / (cheat ? allBookedNightsCheat : allBookedNights)) * 100;
	    double occupancyRateOther = (otherNights / allBookedNights) * 100;
	    if (cheat) {
		occupancyRateAllCheat = (allBookedNightsCheat
			/ (allNights * SettingsManager.getInstance().getNumberOfRooms())) * 100;
		occupancyRateOther = 0;
	    }

	    System.out.println("Total   occupancy rate:   " + String.format("%6.2f", occupancyRateAll) + "% ("
		    + String.format("%3.0f", allBookedNights) + "/"
		    + String.format("%3.0f", (allNights * SettingsManager.getInstance().getNumberOfRooms()))
		    + " room nights busy)");

	    System.out.println("Cheat Tot. occup. rate:   " + String.format("%6.2f", occupancyRateAllCheat) + "% ("
		    + String.format("%3.0f", allBookedNightsCheat) + "/"
		    + String.format("%3.0f", (allNights * SettingsManager.getInstance().getNumberOfRooms()))
		    + " room nights busy)");

	    System.out.println("Booking occupancy rate:   " + String.format("%6.2f", occupancyRateBooking) + "% ("
		    + String.format("%3.0f", bookingNights) + "/"
		    + String.format("%3.0f", cheat ? allBookedNightsCheat : allBookedNights) + " room nights busy)");

	    System.out.println("Airbnb  occupancy rate:   " + String.format("%6.2f", occupancyRateAirbnb) + "% ("
		    + String.format("%3.0f", airbnbNights) + "/"
		    + String.format("%3.0f", cheat ? allBookedNightsCheat : allBookedNights) + " room nights busy)");

	    System.out.println("Other   occupancy rate:   " + String.format("%6.2f", occupancyRateOther) + "% ("
		    + String.format("%3.0f", otherNights) + "/" + String.format("%3.0f", cheat ? 0 : allBookedNights)
		    + " room nights busy)");

	    final MonetaryAmount commonExpenses = Payments2.getSum(getExpensesInRange());
	    final MonetaryAmount commonExpensesBooking = (Double.isNaN(occupancyRateBooking)
		    || Double.isInfinite(occupancyRateBooking) || (occupancyRateBooking == 0)) ? Money.of(0, "EUR")
			    : commonExpenses.multiply(occupancyRateBooking / 100);

	    final MonetaryAmount commonExpensesAirbnb = (Double.isNaN(occupancyRateAirbnb)
		    || Double.isInfinite(occupancyRateAirbnb) || (occupancyRateAirbnb == 0)) ? Money.of(0, "EUR")
			    : commonExpenses.multiply(occupancyRateAirbnb / 100);

	    final MonetaryAmount commonExpensesOther = (Double.isNaN(occupancyRateOther)
		    || Double.isInfinite(occupancyRateOther) || (occupancyRateOther == 0)) ? Money.of(0, "EUR")
			    : commonExpenses.multiply(occupancyRateOther / 100);
	    if (cheat) {

	    }
	    System.out.println("Total   Common Expenses: " + String.format("%8.2f",
		    commonExpenses.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));
	    System.out.println("Booking Common Expenses: " + String.format("%8.2f",
		    commonExpensesBooking.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));
	    System.out.println("Airbnb  Common Expenses: " + String.format("%8.2f",
		    commonExpensesAirbnb.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));
	    System.out.println("Other   Common Expenses: " + String.format("%8.2f",
		    commonExpensesOther.with(Monetary.getDefaultRounding()).getNumber().doubleValue()));

	    final CleaningEntriesByOrigin cbo = new CleaningEntriesByOrigin(new CleaningBeanFactory()
		    .setBookings(getAllBookings().getAllElements()).build(getCleaningsInRange()));

	    final MonetaryAmount totalCleanings = Payments2
		    .getSum(CleaningExpensesFactory2.build(cbo.getAllElements(), true));

	    final MonetaryAmount bookingCleanings = Payments2
		    .getSum(CleaningExpensesFactory2.build(cbo.getBookingElements(), true));

	    final MonetaryAmount airbnbCleanings = Payments2
		    .getSum(CleaningExpensesFactory2.build(cbo.getAirbnbElements(), true));

	    final double otherCleanings = Payments2.getSum(CleaningExpensesFactory2.build(cbo.getOtherElements(), true))
		    .getNumber().doubleValue();

	    System.out.println(
		    "Total   Cleanings:       " + String.format("%8.2f", totalCleanings.getNumber().doubleValue()));

	    System.out.println(
		    "Booking Cleanings:       " + String.format("%8.2f", bookingCleanings.getNumber().doubleValue()));

	    System.out.println(
		    "Airbnb  Cleanings:       " + String.format("%8.2f", airbnbCleanings.getNumber().doubleValue()));

	    System.out.println("Other   Cleanings:       " + String.format("%8.2f", otherCleanings));

	    if (cheat) {
		final double additionalBookingCleanings = otherCleanings * occupancyRateBooking;
		final double additionalAirbnbCleanings = otherCleanings * occupancyRateAirbnb;
	    }

	    System.out.println();
	    System.out.println(
		    "Total profit:            " + String.format("%8.2f", grossPaymentsTotal.subtract(serviceFeesAll)
			    .subtract(commonExpenses).subtract(totalCleanings).getNumber().doubleValue()));
	    System.out.println("----------------------------------------------------");

	    System.out.println(getPayoutString("Booking", grossPaymentsBooking, serviceFeesBooking,
		    commonExpensesBooking, bookingCleanings, 1f));

	    System.out.println(getPayoutString("Airbnb", grossPaymentsAirbnb, serviceFeesAirbnb, commonExpensesAirbnb,
		    airbnbCleanings, 1f));

	    System.out.println(getPayoutString("Sum", grossPaymentsAirbnb.add(grossPaymentsBooking),
		    serviceFeesAirbnb.add(serviceFeesBooking), commonExpensesAirbnb.add(commonExpensesBooking),
		    airbnbCleanings.add(bookingCleanings), 1f));

	} catch (final Exception e) {
	    e.printStackTrace();
	}
    }

    String getPayoutString(final String origin, final MonetaryAmount grossPayments, final MonetaryAmount serviceFees,
	    final MonetaryAmount commonExpenses, final MonetaryAmount cleaningExpenses, final float payoutFactor) {
	final StringBuilder sb = new StringBuilder();
	final MonetaryAmount payout = grossPayments.subtract(serviceFees).subtract(commonExpenses)
		.subtract(cleaningExpenses);
	sb.append(getPayoutString(origin, payout, payoutFactor));
	return sb.toString();
    }

    private String getPayoutString(final String origin, final MonetaryAmount payout, final float f) {
	final StringBuilder sb = new StringBuilder();
	sb.append("Payout");
	sb.append(String.format("%9s", origin));
	sb.append(" (");
	sb.append(String.format("%3.0f", f * 100));
	sb.append("%):   ");
	sb.append(String.format("%7.2f", payout.multiply(f).getNumber().doubleValue()));
	return sb.toString();
    }
}
