package net.engio.pips.lab;

import net.engio.pips.lab.workload.*;

import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @Author bennidi
 */
public class Laboratory {

    public void run(Experiment... experiments) throws Exception {
        for(Experiment experiment : experiments){
            // TODO: verify workload configuration   (at least one timebased/immediate wl)
            measure(experiment);
            PrintWriter log = new PrintWriter(experiment.getLogStream(), true);
            log.println("Generating reports....");
            experiment.generateReports();
        }
    }


    public void measure(final Experiment experiment) {
        // each workload will run in its own thread
        final ExecutorService executor = Executors.newFixedThreadPool(experiment.getWorkloads().size());

        // keeping track of workloads and their corresponding executables
        final Map<Workload, WorkloadManager> workloads = new HashMap<Workload, WorkloadManager>(experiment.getWorkloads().size());
        //final Map<Workload, Future<Long>> scheduled = Collections.synchronizedMap(new HashMap<Workload, Future<Long>>(experiment.getWorkloads().size()));
        final AtomicInteger finishedWorkloads = new AtomicInteger(0);

        final PrintWriter log  = new PrintWriter(experiment.getLogStream(), true);
        final Timer timer = new Timer(true);

        Date start = new Date(System.currentTimeMillis());
        log.println("Starting experiment at " + start );
        // prepare workloads
        for(final Workload workload : experiment.getWorkloads()){
            workloads.put(workload, new WorkloadManager(workload, experiment));

            // keep track of finished workloads
            workload.handle(ExecutionEvent.WorkloadCompletion, new ExecutionHandler() {
                @Override
                public void handle(ExecutionContext context) {
                    finishedWorkloads.incrementAndGet();
                }
            });

            // cancel workloads when duration is exceeded
            if(workload.getDuration().isTimeBased()){
                workload.handle(ExecutionEvent.WorkloadInitialization, new ExecutionHandler() {
                    @Override
                    public void handle(ExecutionContext context) {
                        Date timeout = new Date(System.currentTimeMillis() + workload.getDuration().inMillisecs());
                        log.println("Scheduling timertask to cancel " + workload + " in " + timeout);
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                workloads.get(workload).stop();
                            }
                        }, timeout);
                    }
                });
            }

            // wire up dependent workloads to be started when their predecessor completes
            if(workload.getStartCondition().isDependent()){
               workload.getStartCondition().getPreceedingWorkload().handle(ExecutionEvent.WorkloadCompletion, new ExecutionHandler() {
                   @Override
                   public void handle(ExecutionContext context) {
                       workloads.get(workload).start(executor);
                   }
               });
            }

            // wire up dependent workloads to be stopped when their predecessor completes
            if(workload.getDuration().isDependent()){
                workload.getDuration().getDependingOn().handle(ExecutionEvent.WorkloadCompletion, new ExecutionHandler() {
                    @Override
                    public void handle(ExecutionContext context) {
                        // interrupt the task
                        workloads.get(workload).stop();
                    }
                });
            }
        }

        // schedule workloads
        for(final Workload workload : experiment.getWorkloads()){
            // either now
            if(workload.getStartCondition().isImmediately()){
                workloads.get(workload).start(executor);
            }
            // or in the future based on specified start condition
            else if(workload.getStartCondition().isTimebased()){
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        workloads.get(workload).start(executor);
                    }
                }, new Date(System.currentTimeMillis() + workload.getStartCondition().inMillisecs()));
            }
        }

        // wait until all tasks have been executed
        try {
            while(finishedWorkloads.get() < experiment.getWorkloads().size())
                Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            log.println("Finished experiment");

            // merge contexts
            Executions executions = new Executions();
            for(WorkloadManager workMan : workloads.values())
                 executions.addAll(workMan.contexts);
            experiment.setExecutions(executions);
        }

    }



    private static class WorkloadManager{

        private Workload workload;
        private Callable<Long> scheduler;
        private ExecutorService workloadExecutor;
        private List<Future> scheduledTasks = new LinkedList<Future>();
        private Future scheduledWorkload;
        private List<ExecutionContext> contexts = new LinkedList<ExecutionContext>();
        private volatile boolean stopped = false;

        private WorkloadManager(Workload workload, Experiment experiment) {
            this.workload = workload;
            createScheduler(experiment, experiment.getClobalContext().getChild());
        }

        private void stop(){
            stopped = true;
            for(Future task : scheduledTasks)
               task.cancel(true); // this doesn't seem to have any effect
            System.out.println("Canceling workload" + workload);
            scheduledWorkload.cancel(true);
            workloadExecutor.shutdown();
        }

        private Future start(ExecutorService executor){
            return scheduledWorkload = executor.submit(scheduler);
        }

        // create a single executable unit which will run the tasks from the given workload
        // in its own thread pool
        private Callable<Long> createScheduler(final Experiment experiment, final ExecutionContext workloadContext){
            workloadExecutor = Executors.newFixedThreadPool(workload.getParallelUnits());
            scheduler =  new Callable<Long>() {
                @Override
                public Long call() {
                    final AtomicInteger scheduled = new AtomicInteger(0);// number of scheduled tasks
                    final AtomicInteger finished = new AtomicInteger(0); // number of finished tasks
                    //final ResultCollector collector = experiment.getResults();
                    final ITaskFactory tasks = workload.getITaskFactory();
                    final PrintWriter log = new PrintWriter(experiment.getLogStream(), true);

                    log.println("Starting workload " + workload.getName());
                    // call initialization handlers before scheduling the actual tasks
                    workload.started();
                    workload.getHandler(ExecutionEvent.WorkloadInitialization).handle(workloadContext);
                    // create the tasks and schedule for execution
                    for (int i = 0; i < workload.getParallelUnits() ; i++) {
                        log.println("Scheduling unit " + scheduled.incrementAndGet());
                        final int taskNumber = i+1;
                        final ExecutionContext taskContext = workloadContext.getChild();
                        contexts.add(taskContext);
                        // simply submit a runnable as return values are not important
                        scheduledTasks.add(workloadExecutor.submit(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    ITask task = tasks.create(taskContext);
                                    log.println("Executing task " + taskNumber);
                                    if (workload.getDuration().isRepetitive()) {
                                        for (int i = 0; i < workload.getDuration().getRepetitions(); i++)
                                            task.run(taskContext);
                                    } else {
                                        while (!stopped) {
                                            task.run(taskContext);
                                        }
                                    }
                                } catch (Exception e) {
                                    log.println("Task" + taskNumber + "  threw an exception will orderly execution: " + e.toString());
                                    e.printStackTrace();
                                    throw new RuntimeException(e);
                                } finally {
                                    finished.incrementAndGet();
                                    log.println("Finished task: " + taskNumber);
                                    log.println("Tasks left:" + (scheduled.get() - finished.get()));
                                }
                            }
                        }));
                    }

                    // wait until all tasks have been executed
                    try {
                        while(scheduled.get() > finished.get())
                            Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        if(workload.getDuration().isDependent() && !workload.getDuration().getDependingOn().isFinished()){
                            log.println(workload + " interrupted although dependent workload not finished");
                            e.printStackTrace(); // something was wrong here
                        }

                        if(!workload.getDuration().isTimeBased()
                                && !workload.getDuration().isDependent()){
                            log.println(workload + " interrupted although no time based duration specified");
                            e.printStackTrace(); // something was wrong here
                        }
                        if(workload.getDuration().isTimeBased()
                                // interrupted before duration ends
                                && System.currentTimeMillis() < workload.getDuration().inMillisecs() + workload.getStarted()){
                            log.println(workload + " interrupted before timer finished");
                            e.printStackTrace(); // something was wrong here
                        }

                    }finally {
                        // signal end
                        workload.finished();
                        log.println("Finished workload: " + workload);
                        workload.getHandler(ExecutionEvent.WorkloadCompletion).handle(workloadContext);
                    }
                    return 1L;
                }

            };
          return scheduler;
        }


    }

}

