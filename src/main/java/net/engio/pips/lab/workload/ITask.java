package net.engio.pips.lab.workload;

import net.engio.pips.lab.ExecutionContext;

/**
 *
 * @author bennidi
 *         Date: 2/11/14
 */
public interface ITask {

    public void run(final ExecutionContext context) throws Exception;

}
