package net.engio.imupers.performance;

import net.engio.imupers.performance.data.ResultCollector;
import net.engio.imupers.performance.data.utils.Registry;
import net.engio.imupers.performance.reports.IReporter;

import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
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
public class Benchmark {

    public static final String ParallelUnits = "Parallel units";
    public static final String TotalUnits = "Total number of units";
    public static final String TimeoutInSeconds = "Timeout in seconds";
    public static final String SampleInterval = "Sample interval";
    public static final String BasePath = "Base path";
    public static final String LogStream = "Log stream";
    public static final String Title = "Title";
    public static final String ReportBaseDir = "Report base dir";

    private Map<String, Object> properties = new HashMap<String, Object>();

    private UnitFactory units;

    private ResultCollector results;

    private long startingTime;

    private Registry registry;

    private List<IReporter> reporters = new LinkedList<IReporter>();

    public Benchmark(String title) {
        if(title == null || title.isEmpty())
             throw new IllegalArgumentException("Please provide a title that is a valid identifier for a directory");
        this.results = new ResultCollector(this);
        setTitle(title);
    }

    public Benchmark setUnitFactory(UnitFactory units) {
        this.units = units;
        return this;
    }

    public Benchmark addReport(IReporter reporter) {
        reporters.add(reporter);
        return this;
    }

    public void generateReports() throws Exception{
       setProperty(ReportBaseDir,prepareDirectory());
       for(IReporter reporter : reporters)
           reporter.generate(this);
    }

    public String getReportBaseDir(){
        return getProperty(ReportBaseDir);
    }

    private String prepareDirectory() {
        //create directory
        File baseDir = new File(getProperty(BasePath) + File.separator + getTitle() + File.separator + System.currentTimeMillis());
        baseDir.mkdirs();
        return baseDir.getAbsolutePath() + File.separator;
    }

    public Registry getRegistry() {
        return registry;
    }

    public Benchmark setRegistry(Registry registry) {
        this.registry = registry;
        return this;
    }

    public void start() {
        this.startingTime = System.currentTimeMillis();
    }

    public long getStartingTime() {
        return startingTime;
    }

    public UnitFactory getUnits() {
        return units;
    }

    public ResultCollector getResults() {
        return results;
    }

    public boolean isDefined(String key){
        return properties.containsKey(key);
    }

    public <T> T getProperty(String key){
        return (T)properties.get(key);
    }

    public int getSampleInterval() {
        return getProperty(SampleInterval);
    }

    public Benchmark setBasePath(String basePath) {
        return setProperty(BasePath, basePath);
    }

    public Benchmark setSampleInterval(int sampleInterval) {
        return setProperty(SampleInterval, sampleInterval);
    }

    public int getTimeoutInSeconds() {
        return getProperty(TimeoutInSeconds);
    }

    public Benchmark setTimeoutInSeconds(int timeoutInSeconds) {
        return setProperty(TimeoutInSeconds, timeoutInSeconds);
    }

    public boolean hasTimeoutSet(){
        return getTimeoutInSeconds() > 0;
    }

    public int getParallelUnitCount() {
        return getProperty(ParallelUnits);
    }

    public Benchmark setParallelUnitCount(int parallelUnitCount) {
        return setProperty(ParallelUnits, parallelUnitCount);
    }

    public int getTotalUnitCount() {
        return getProperty(TotalUnits);
    }

    public Benchmark setTotalUnitCount(int totalUnitCount) {
        return setProperty(TotalUnits, totalUnitCount);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public Benchmark addProperties(Map<String, Object> properties) {
        this.properties.putAll(properties);
        return this;
    }

    public Benchmark setProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }


    public OutputStream getLogStream() {
        return isDefined(LogStream) ? (OutputStream)getProperty(LogStream) : System.out;
    }

    public Benchmark setLogStream(OutputStream out) {
        return setProperty(LogStream, out);
    }

    @Override
    public String toString() {
        return "Benchmark{" +
                "properties=" + properties +
                '}';
    }

    public String getTitle() {
        return getProperty(Title);
    }

    public Benchmark setTitle(String title) {
        return setProperty(Title, title);
    }
}

