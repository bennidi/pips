package net.engio.pips.lab;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bennidi
 *         Date: 3/13/14
 */
public class Executions {

    private List<ExecutionContext> contexts = new LinkedList<ExecutionContext>();

    public boolean addAll(Collection<? extends ExecutionContext> executionContexts) {
        return contexts.addAll(executionContexts);
    }

    public boolean add(ExecutionContext executionContext) {
        return contexts.add(executionContext);
    }



    public <T> Collection<T> getAll(Object key) {
        return getAll(key.toString());
    }

    public <T> Collection<T> getAll(String key) {
        LinkedList<T> all = new LinkedList<T>();
        for(ExecutionContext ctx : contexts){
            all.addAll(ctx.<T>getAll(key));
        }
        return all;
    }

    public <T> Collection<T> getMatching(String key) {
        LinkedList<T> matching = new LinkedList<T>();
        for(ExecutionContext ctx : contexts){
            matching.addAll(ctx.<T>getMatching(key));
        }
        return matching;
    }


}
