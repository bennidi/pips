package net.engio.graph;

import net.engio.graph.common.UnitTest;
import net.engio.pips.lab.ExecutionContext;
import net.engio.pips.lab.Executions;
import net.engio.pips.lab.Experiment;
import org.junit.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author bennidi
 *         Date: 3/13/14
 */
public class ExecutionContextTest extends UnitTest{

    private Map<String, Object> bindings(){
        Map<String, Object> bindings = new HashMap<String, Object>();
        bindings.put(ExecutionContext.class.toString(), ExecutionContext.class);
        bindings.put(UnitTest.class.toString(), UnitTest.class);
        bindings.put(Experiment.class.toString(), Experiment.class);
        return bindings;
    }

    private ExecutionContext getInitialContext(Map<String, Object> bindings){
        ExecutionContext ctx = new ExecutionContext(new Experiment("test"));
        ctx.bindAll(bindings);

        assertBindingsExist(bindings, ctx);

        return ctx;
    }

    private void assertBindingsExist(Map<String, Object> bindings, ExecutionContext ctx){
        for(String key : bindings.keySet())
            assertEquals(bindings.get(key), ctx.get(key));
    }

    private void assertBindindsExist(Object[] bindings, ExecutionContext ctx){
        for(Object value : bindings)
            assertEquals(value, ctx.get(value.toString()));
    }

    private void assertBindingsAbsent(Map<String, Object> bindings, ExecutionContext ctx){
        for(String key : bindings.keySet())
            assertNull(ctx.get(key));
    }

    private void assertBindingsAbsent(Object[] bindings, ExecutionContext ctx){
        for(Object value : bindings)
            assertNull(ctx.get(value.toString()));
    }

    @Test
    public void testRootBindGet(){
        ExecutionContext ctx = getInitialContext(bindings());
        Object[] bindings = new Object[]{"Object1", 2, new Object()};

        // bind values
        for(Object value : bindings)
            ctx.bind(value);

        // retrieve using the two available methods
        for(Object value : bindings){
            assertEquals(value, ctx.get(value.toString()));
            assertEquals(value, ctx.get(value));
        }

    }




    @Test
    public void testChildAccess(){
        ExecutionContext ctx = getInitialContext(bindings());
        ExecutionContext child = ctx.getChild();

        assertBindingsExist(bindings(), child);

        Object[] bindings = new Object[]{"Object1", 2, new Object()};
        child.bind(bindings);
        assertBindindsExist(bindings, child);

        assertBindingsAbsent(bindings, ctx);

    }


    @Test
    public void testGetAll(){
        ExecutionContext ctx = getInitialContext(bindings());
        ExecutionContext child = ctx.getChild();
        child.bind(bindings());
        assertBindingsExist(bindings(), child);

        for(String key : bindings().keySet()){
            assertEquals(2, child.getAll(key).size());
            assertEquals(1, ctx.getAll(key).size());
        }
    }

    @Test
    public void testGetMatching(){
        ExecutionContext ctx = new ExecutionContext(new Experiment("test"));
        Object[] bindings = new Object[]{"root", "root:lvl1", "root:lvl1:lvl2", "none"};
        ctx.bind(bindings);


        assertEquals(3, ctx.getMatching("root").size());
        assertEquals(2, ctx.getMatching("root:lvl1").size());
        assertEquals(1, ctx.getMatching("root:lvl1:lvl").size());
        assertEquals(1, ctx.getMatching("root:lvl1:lvl2").size());
        assertEquals(3, ctx.getMatching("ro").size());
        assertEquals(0, ctx.getMatching("null").size());
        assertEquals(1, ctx.getMatching("no").size());

        ExecutionContext child = ctx.getChild();
        child.bind(bindings);
        assertEquals(6, child.getMatching("root").size());
        assertEquals(4, child.getMatching("root:lvl1").size());
        assertEquals(2, child.getMatching("root:lvl1:lvl").size());
        assertEquals(2, child.getMatching("root:lvl1:lvl2").size());
        assertEquals(6, child.getMatching("ro").size());
        assertEquals(0, child.getMatching("null").size());
        assertEquals(2, child.getMatching("no").size());
    }

    @Test
    public void testExecutions(){
        ExecutionContext ctx = new ExecutionContext(new Experiment("test"));
        Object[] bindings = new Object[]{"root", "root:lvl1", "root:lvl1:lvl2", "none"};
        ctx.bind(bindings);

        ExecutionContext child = ctx.getChild();
        child.bind(bindings);

        ExecutionContext child2 = ctx.getChild();

        Executions executions = new Executions();
        executions.add(child);
        executions.add(child2);

        Collection matching = executions.getMatching("root");
        assertEquals(9, matching.size());
    }

}
