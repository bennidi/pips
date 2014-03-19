package net.engio.pips.lab.workload;

import net.engio.pips.lab.ExecutionContext;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * A single workload defines a set of {@link ITask} to be executed as part of an {@link net.engio.pips.lab.Experiment}.
 * Tasks are potentially run in parallel depending on the configuration of {@code setParallelTasks}.
 * Tasks are created using the corresponding {@link net.engio.pips.lab.workload.ITaskFactory}.
 *
 *
 * @author bennidi
 *         Date: 3/6/14
 */
public class Workload {

    private int parallelUnits = 1;

    private ITaskFactory ITaskFactory;

    private Duration duration;

    private StartCondition starting = new StartCondition();

    private String name;

    private volatile long started;

    private volatile long finished;

    private Map<ExecutionEvent, ExecutionHandlerWrapper> handlers = new HashMap<ExecutionEvent, ExecutionHandlerWrapper>();


    public Workload(String name) {
        this.name = name;
    }

    public void started(){
        started = System.currentTimeMillis();
    }

    public void finished(){
        finished = System.currentTimeMillis();
    }

    public boolean isFinished(){
        return finished != -1;
    }

    public long getExecutionTime(){
        return isFinished() ? finished - started : -1;
    }

    public int getParallelUnits() {
        return parallelUnits;
    }

    public Workload setParallelTasks(int parallelUnits) {
        if(parallelUnits < 1 )throw new IllegalArgumentException("At least one task must run");
        this.parallelUnits = parallelUnits;
        return this;
    }

    public String getName() {
        return name;
    }

    public ITaskFactory getITaskFactory() {
        return ITaskFactory;
    }

    public Workload setITaskFactory(ITaskFactory ITaskFactory) {
        this.ITaskFactory = ITaskFactory;
        return this;
    }


    public Workload handle(ExecutionEvent event, ExecutionHandler handler){
        if(handlers.containsKey(event)){
            handlers.get(event).delegate.add(handler);
        }
        else handlers.put(event, new ExecutionHandlerWrapper(handler));
        return this;
    }

    public Duration getDuration() {
        return duration;
    }

    public ExecutionHandler getHandler(ExecutionEvent event) {
        return handlers.containsKey(event) ? handlers.get(event) : Empty;
    }

    public StartCondition getStartCondition() {
        return starting;
    }

    public StartSpecification starts(){
        return new StartSpecification();
    }

    public DurationSpecification duration(){
        return new DurationSpecification();
    }

    private static final ExecutionHandler Empty = new ExecutionHandler() {
        @Override
        public void handle(ExecutionContext context) {
            // do nothing;
        }
    };

    @Override
    public String toString() {
        StringBuilder wl = new StringBuilder();
        wl.append(name);
        wl.append("(" + getExecutionTime() + "ms)");
        wl.append("->");
        wl.append("Parallel tasks:" + getParallelUnits());
        wl.append(",");
        wl.append(getStartCondition());
        wl.append(",");
        wl.append(getDuration());
        wl.append("\n");
        return wl.toString();
    }

    public long getStarted() {
        return started;
    }

    public class StartSpecification{

        public Workload after(int timeout, TimeUnit unit){
            starting = new StartCondition(timeout,unit);
            return Workload.this;
        }

        public Workload after(Workload preceeding){
            starting = new StartCondition(preceeding);
            return Workload.this;
        }

        public Workload immediately(){
            starting = new StartCondition();
            return Workload.this;
        }

    }

    public class DurationSpecification{

        public Workload lasts(int timeout, TimeUnit unit){
            duration = new Duration(timeout, unit);
            return Workload.this;
        }

        public Workload depends(Workload preceeding) {
            duration = new Duration(preceeding);
            return Workload.this;
        }

        public Workload repetitions(int repetitions) {
            duration = new Duration(repetitions);
            return Workload.this;
        }

    }

    public  static class ExecutionHandlerWrapper implements ExecutionHandler{

        private List<ExecutionHandler> delegate = new LinkedList<ExecutionHandler>();

        public ExecutionHandlerWrapper(ExecutionHandler handler) {
            delegate.add(handler);
        }

        @Override
        public void handle(ExecutionContext context) {
            for(ExecutionHandler handler : delegate){
                try{
                    handler.handle(context);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }
}
