package net.engio.pips.lab.workload;

import net.engio.pips.lab.ExecutionContext;

/**
 * A factory that provides an iterator like interface to construct
 * execution units that can be run by the executor
 *
 * @author bennidi
 *         Date: 2/11/14
 */
public interface ITaskFactory {

    /**
     * Create a new task to be scheduled as part of an associated workload. This method
     * might return a new or the same task instance for each call.
     *
     *
     * @param context - The execution context provided by the {@link net.engio.pips.lab.Laboratory}
     * @return - A task instance
     */
    ITask create(ExecutionContext context);

}
