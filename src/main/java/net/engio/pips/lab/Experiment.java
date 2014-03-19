package net.engio.pips.lab;

import net.engio.pips.data.IDataCollector;
import net.engio.pips.lab.workload.Workload;
import net.engio.pips.reports.IReporter;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A benchmark is the container for all information of a formerly executed performance
 * measurement. It provides access to all collected data and means to create reports and
 * export them to persistent storage
 *
 * @author bennidi
 *         Date: 2/11/14
 */
public class Experiment {

    public static final class Properties{
        public static final String TimeoutInSeconds = "Timeout in seconds";
        public static final String SampleInterval = "Sample interval";
        public static final String BasePath = "Base path";
        public static final String LogStream = "Log stream";
        public static final String Title = "Title";
        public static final String ReportBaseDir = "Report base dir";
        public static final String Collectors = "collectors:";
        public static final String ExecutionTimers = Collectors +  "execution-timer:";
    }

    private ExecutionContext context = new ExecutionContext(this);

    private List<IReporter> reporters = new LinkedList<IReporter>();

    private List<Workload> workloads = new LinkedList<Workload>();

    private Executions executions;

    private String title;

    public Experiment(String title) {
        if (title == null || title.isEmpty())
            throw new IllegalArgumentException("Please provide a title that is a valid identifier for a directory");
        this.title = title;
    }

    public void setExecutions(Executions executions) {
        this.executions = executions;
    }

    public Executions getExecutions() {
        return executions;
    }

    public Collection<IDataCollector> getCollectors(){
        return executions.getMatching(Properties.Collectors);
    }

    public Collection<IDataCollector> getCollectors(String collectorId){
        return executions.getMatching(Experiment.Properties.Collectors + collectorId);
    }



    /**
     * Register a global object that can be accessed from the {@link ExecutionContext}
     *
     * @param key   - The identifier to be used for subsequent lookups using {@code get(key)}
     * @param value - The value to associate with the key
     * @return
     */
    public Experiment register(String key, Object value) {
        context.bind(key, value);
        return this;
    }

    public Experiment addWorkload(Workload... workload) {
        for (Workload wl : workload)
            workloads.add(wl);
        return this;
    }



    public Experiment addReport(IReporter reporter) {
        reporters.add(reporter);
        return this;
    }

    public void generateReports() throws Exception {
        PrintWriter log = new PrintWriter(getLogStream(), true);
        if (reporters.isEmpty()) {
            log.println("Skipping report generation because no reporters have been registered");
            return;
        }
        setProperty(Properties.ReportBaseDir, prepareDirectory());

        for (IReporter reporter : reporters) {
            log.println("Report" + reporter);
            reporter.generate(this);
        }
    }

    public String getReportBaseDir() {
        return getProperty(Properties.ReportBaseDir);
    }

    private String prepareDirectory() {
        //create directory
        File baseDir = new File(getProperty(Properties.BasePath) + File.separator + getTitle() + File.separator + System.currentTimeMillis());
        baseDir.mkdirs();
        return baseDir.getAbsolutePath() + File.separator;
    }

    public <T> T get(String key) {
        return context.get(key);
    }

    public List<Workload> getWorkloads() {
        return workloads;
    }

    public boolean isDefined(String key) {
        return context.containsKey(key);
    }

    public <T> T getProperty(String key) {
        return (T) context.get(key);
    }

    public int getSampleInterval() {
        return getProperty(Properties.SampleInterval);
    }

    public Experiment setBasePath(String basePath) {
        return setProperty(Properties.BasePath, basePath);
    }

    public Experiment setSampleInterval(int sampleInterval) {
        return setProperty(Properties.SampleInterval, sampleInterval);
    }

    public int getTimeoutInSeconds() {
        return getProperty(Properties.TimeoutInSeconds);
    }

    public Experiment setProperty(String key, Object value) {
        context.bind(key, value);
        return this;
    }


    public OutputStream getLogStream() {
        return isDefined(Properties.LogStream) ? (OutputStream) getProperty(Properties.LogStream) : System.out;
    }

    public Experiment setLogStream(OutputStream out) {
        return setProperty(Properties.LogStream, out);
    }

    @Override
    public String toString() {
        StringBuilder exp = new StringBuilder();
        exp.append("Experiment ");
        exp.append(title);
        exp.append("with ");
        exp.append(workloads.size() + " workloads");
        exp.append("\n");

        for(Workload load : workloads){
            exp.append("\t");
            exp.append(load);
        }
        exp.append("\n");
        exp.append("and additional parameters:\n");
        for(Map.Entry entry : context.getProperties().entrySet()){
            exp.append("\t");
            exp.append(entry.getKey());
            exp.append(":");
            exp.append(entry.getValue());
            exp.append("\n");
        }

        return exp.toString();
    }

    public String getTitle() {
        return title;
    }



    public ExecutionContext getClobalContext() {
        return context;
    }
}

