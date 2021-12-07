package com.github.drbookings.cli;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.github.drbookings.CleaningBeanSer2;

import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

public abstract class AbstractCleaningsCommand extends AbstractListCommand implements Runnable {

    @ParentCommand
    DrBookingsCLI parent;

    @Option(names = { "-n", "--filter-by-name" }, description = "Filter by name.")
    String[] filterByName;

    @Option(names = { "-T",
	    "--set-tax-relevant-true" }, description = "Set the 'tax-relevant' flag for listed cleanings to 'true'.")
    String[] setTaxRelevantTrue;

    @Option(names = { "-t",
	    "--set-tax-relevant-false" }, description = "Set the 'tax-relevant' flag for listed cleanings to 'false'.")
    String[] setTaxRelevantFalse;

    @Option(names = { "-w", "--write-changes" }, description = "Write changes to file.")
    String writeChanges;

    public AbstractCleaningsCommand() {
	super();

    }

    protected void init() throws Exception {
	init(parent);
    }

    @Override
    public void run() {
	try {
	    init();
	    final List<CleaningBeanSer2> cleanings = getCleaningsInRange().stream()
		    .sorted((a, b) -> a.date.compareTo(b.date)).collect(Collectors.toList());
	    System.out.println("cnt\troom\t\tdate\t\tname\tamount\ttax");
	    for (int i = 0; i < cleanings.size(); i++) {

		final CleaningBeanSer2 currentCleaning = cleanings.get(i);

		if ((filterByName != null) && (filterByName.length > 0)) {
		    if (!Arrays.asList(filterByName).stream().anyMatch(s -> s.equalsIgnoreCase(currentCleaning.name))) {
			continue;
		    }
		}

		if ((setTaxRelevantTrue != null) && (setTaxRelevantTrue.length > 0)) {
		    if (Arrays.asList(setTaxRelevantTrue).stream()
			    .anyMatch(s -> s.equalsIgnoreCase(currentCleaning.name))) {
			currentCleaning.tax = true;
		    }
		}

		if ((setTaxRelevantFalse != null) && (setTaxRelevantFalse.length > 0)) {
		    if (Arrays.asList(setTaxRelevantFalse).stream()
			    .anyMatch(s -> s.equalsIgnoreCase(currentCleaning.name))) {
			currentCleaning.tax = false;
		    }
		}

		handleCleaningEntry(i, currentCleaning);

	    }

	} catch (final Exception e) {
	    e.printStackTrace();
	}

    }

    protected abstract void handleCleaningEntry(final int index, final CleaningBeanSer2 currentCleaning);

}
