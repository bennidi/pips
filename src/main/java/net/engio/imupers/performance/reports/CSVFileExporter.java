package net.engio.imupers.performance.reports;

import net.engio.imupers.performance.Benchmark;
import net.engio.imupers.performance.data.IDataCollector;

import java.io.File;
import java.io.PrintWriter;

/**
 * @author bennidi
 *         Date: 2/27/14
 */
public class CSVFileExporter implements IReporter {

    public void generate(Benchmark benchmark) throws Exception {
        String reportDirectory = benchmark.getReportBaseDir();
        File report = new File(reportDirectory + "report.txt");
        PrintWriter writer = new PrintWriter(report);
        try {

            // write report header
            writer.println("###### PARAMETER ##########");
            writer.println(benchmark);

            // write data of collectors
            writer.println();
            writer.println("##### Collector data ########");
            for (IDataCollector collector: benchmark.getResults().getCollectors()) {
                writer.println(collector);
            }
        } finally {
            writer.close();
        }
    }

}
