package net.engio.pips.reports;

import net.engio.pips.data.IDataCollector;
import net.engio.pips.lab.Experiment;

import java.io.File;
import java.io.PrintWriter;

/**
 * @author bennidi
 *         Date: 2/27/14
 */
public class CSVFileExporter implements IReporter {

    public void generate(Experiment experiment) throws Exception {
        String reportDirectory = experiment.getReportBaseDir();
        File report = new File(reportDirectory + "report.txt");
        PrintWriter writer = new PrintWriter(report);
        try {

            // write report header
            writer.println("###### EXPERIMENT ##########");
            writer.println(experiment);

            // write data of collectors
            writer.println();
            writer.println("##### COLLECTORS ########");
            for (IDataCollector collector: experiment.getCollectors()) {
                writer.println(collector);
            }


        } finally {
            writer.close();
        }
    }

}
