package net.engio.pips.lab.workload;

import net.engio.pips.lab.ExecutionContext;

/**
 * Execution handlers are used to react to execution events
 * such as
 *
 * @author bennidi
 *         Date: 3/9/14
 */
public interface ExecutionHandler {

    void handle(ExecutionContext context);
}
