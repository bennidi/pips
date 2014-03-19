package net.engio.pips.lab.workload;

import java.util.concurrent.TimeUnit;

/**
 * @author bennidi
 *         Date: 3/9/14
 */
public class StartCondition {

    private boolean immediately = false;

    private int timeout;

    private TimeUnit unit;

    private Workload after;


    // TODO: add argument verification
    public StartCondition(int timeout, TimeUnit unit) {
        this.timeout = timeout;
        this.unit = unit;
    }

    public long inMillisecs(){
        return unit.convert(timeout, TimeUnit.MILLISECONDS);
    }

    public StartCondition() {
        immediately = true;
    }

    public StartCondition(Workload after) {
        this.after = after;
    }

    public boolean isTimebased(){
       return unit != null;
    }

    public boolean isImmediately(){
        return immediately;
    }

    public boolean isDependent(){
         return after != null ;
    }

    public Workload getPreceedingWorkload() {
        return after;
    }

    @Override
    public String toString() {
        if(isTimebased())return "start after " + timeout + unit;
        if(isDependent()) return "start after workload " + after.getName();
        if(isImmediately()) return "start immediately";
        return "Unknown startcondition";
    }
}
