package net.engio.graph;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {RelationStateTest.class,
        UserRelationTest.class,
        SocialGraphTest.class,
        RelationStateTest.class,
        RangeTest.class,
        PartitionManagerTest.class})
public class AllTests {
}
