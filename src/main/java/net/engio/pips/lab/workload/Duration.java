package net.engio.pips.lab.workload;

import java.util.concurrent.TimeUnit;

/**
 * A duration defines when to stop a workload from being executed.
 *
 * @author bennidi
 *         Date: 3/9/14
 */
public class Duration {

    private int timeout;

    private TimeUnit unit;

    private Workload dependingOn;

    private int repetitions = -1;


    public Duration(int timeout, TimeUnit unit) {
        if(timeout < 1 || unit == null)
            throw new IllegalArgumentException("Illegal timeout condition:" + timeout + unit);
        this.timeout = timeout;
        this.unit = unit;
    }

    public Duration(Workload dependingOn) {
        this.dependingOn = dependingOn;
    }

    public Duration(int repetitions) {
        this.repetitions = repetitions;
    }

    public long inMillisecs(){
        return TimeUnit.MILLISECONDS.convert(timeout, unit);
    }

    public Workload getDependingOn() {
        return dependingOn;
    }

    public boolean isTimeBased(){
        return  timeout  != -1 && unit != null;
    }

    public boolean isDependent(){
        return dependingOn != null;
    }

    public int getRepetitions() {
        return repetitions;
    }

    public boolean isRepetitive() {
        return repetitions > 0;
    }

    @Override
    public String toString() {
        if(isTimeBased()) return "run for " + timeout + unit;
        if(isRepetitive()) return "run " + repetitions + " times";
        if(isDependent()) return "run until " + dependingOn.getName() + " ends";
        return "Unknown duration";
    }
}
