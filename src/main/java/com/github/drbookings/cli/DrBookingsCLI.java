package com.github.drbookings.cli;

import java.nio.file.Path;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.SettingsManager;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "drbookings", mixinStandardHelpOptions = true, subcommands = { ListAllBookingsCommand.class,
	PrintGrossPaymentsCommand.class, PrintCommissionCommand.class, CleaningsCommand.class, NightsCommand.class,
	PayoutCommand.class,
	StatisticsCommand.class }, headerHeading = "@|bold,underline Usage|@:%n%n", synopsisHeading = "%n", descriptionHeading = "%n@|bold,underline Description|@:%n%n", parameterListHeading = "%n@|bold,underline Parameters|@:%n", optionListHeading = "%n@|bold,underline Options|@:%n", header = "Some summary header.", description = "A longer or shorter description of awesomeness. ")
public class DrBookingsCLI implements Runnable {

    @Parameters(index = "0", description = "The Dr.Bookings data file to read.")
    Path file;
    @Parameters(index = "1", description = "The first date (check-out) of processing.")
    LocalDate startDate;
    @Parameters(index = "2", description = "The last date (check-out) of processing.")
    LocalDate endDate;

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DrBookingsCLI.class);

    public static void main(final String[] args) {
	initSettings();
	CommandLine.run(new DrBookingsCLI(), args);
    }

    @Override
    public void run() {

    }

    private static void initSettings() {
	SettingsManager.getInstance().setNumberOfRooms(4);

    }
}
