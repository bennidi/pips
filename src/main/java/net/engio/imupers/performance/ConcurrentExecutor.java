package net.engio.imupers.performance;

import net.engio.imupers.performance.data.ResultCollector;

import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @Author bennidi
 */
public class ConcurrentExecutor {

    public void run(Benchmark ...benchmarks) throws Exception {
        for(Benchmark benchmark : benchmarks){
            measure(benchmark);
            benchmark.generateReports();
        }
    }

    public void measure(final Benchmark benchmark) {
        ExecutorService executor = Executors.newFixedThreadPool(benchmark.getParallelUnitCount());
        final AtomicInteger scheduled = new AtomicInteger(0);// number of scheduled execution units
        final AtomicInteger finished = new AtomicInteger(0); // number of finished execution units
        final ResultCollector collector = benchmark.getResults();
        final UnitFactory units = benchmark.getUnits(); // get the factory that creates the units
        final PrintWriter log = new PrintWriter(benchmark.getLogStream(), true);

        benchmark.start();
        // create the tasks and schedule for execution
        for (int i = 0; i < benchmark.getTotalUnitCount() ; i++) {
            log.println("Scheduling unit " + scheduled.incrementAndGet());
            final int unitNumber = i+1;
            // simply submit a runnable as return values are not important
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    ResultCollector.Frame frame = null;
                    try {
                        frame = collector.newFrame();
                        ExecutionUnit unit = units.create(new ExecutionContext(benchmark, frame));
                        frame.start();
                        log.println("Executing unit " + unitNumber);
                        unit.run();
                        frame.end();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }finally {
                        finished.incrementAndGet();
                        log.println("Units left:" + (scheduled.get() - finished.get()));
                        log.println("Finished: " + frame);
                    }
                }
            });
        }

        // wait until all tasks have been executed
        try {
            while(scheduled.get() > finished.get())
                Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
