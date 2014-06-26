package net.engio.pips.data;

import java.util.*;

/**
 *
 * @author bennidi
 *         Date: 3/5/14
 */
public class DataCollectorManager {

    private static final String GroupDelimiter = ":";

    private Map<String, CollectorGroup> collectorsByGroup = new HashMap<String, CollectorGroup>();

    public Set<IDataCollector> getCollectorSet(String groupId){
        Set<IDataCollector> collectors = new TreeSet<IDataCollector>(new Comparator<IDataCollector>() {
            @Override
            public int compare(IDataCollector iDataCollector, IDataCollector iDataCollector2) {
                return iDataCollector.getId().compareTo(iDataCollector2.getId());
            }
        });
        for(Map.Entry<String, CollectorGroup> entry : collectorsByGroup.entrySet()){
            if(entry.getKey().startsWith(groupId) || groupId.isEmpty())collectors.addAll(entry.getValue().collectors);
        }
        return collectors;
    }

    public List<IDataCollector> getCollectors(String groupId, int samples){
        List<IDataCollector> collectors = new LinkedList<IDataCollector>();
        for(Map.Entry<String, CollectorGroup> entry : collectorsByGroup.entrySet()){
            if(entry.getKey().startsWith(groupId) || groupId.isEmpty())
                collectors.addAll(entry.getValue().collectors);
        }
        if(samples > 0){
            List<IDataCollector> reduced = new ArrayList<IDataCollector>();
           int total=0, modulo= collectors.size() / samples;
           for(IDataCollector collector : collectors){
               total++;
               if(total % modulo == 0)reduced.add(collector);
           }
            collectors = reduced;
        }
        return collectors;
    }

    public List<IDataCollector> getCollectors(String groupId){
        return getCollectors(groupId, -1); // get all
    }

    public synchronized <V> IDataCollector<V> addCollector(IDataCollector<V> collector){
        addToGroup(collector.getId(), collector);
        /*
        String[] groups = getGroups(collector);
        for(String groupId : groups){

        } */
        return collector;
    }

    private void addToGroup(String groupId, IDataCollector collector){
        CollectorGroup group = collectorsByGroup.get(groupId);
        if(group == null){
            group = new CollectorGroup(groupId, collector);
            collectorsByGroup.put(groupId, group);
        }
        group.add(collector);
    }


    // returns an empty array if the id is only a collector label
    private String[] getGroups(IDataCollector collector){
        String[] parts = collector.getId().split(GroupDelimiter); // get single parts of id
        String[] groups = new String[parts.length-1]; // the last part is the collector label
        // rebuild linear combinations of group/subgroup
        for(int i = 0; i < groups.length ; i++)
            if(i==0)groups[i] = parts[i];
            else groups[i] = groups[i-1] + GroupDelimiter + parts[i];
        return groups;
    }

    public Collection<CollectorGroup> getGroups() {
        return collectorsByGroup.values();
    }

    public class CollectorGroup{

        private String groupId;

        private List<IDataCollector> collectors = new LinkedList<IDataCollector>();

        public CollectorGroup(String groupId, IDataCollector ...collectors) {
            this.groupId = groupId;
            for(IDataCollector col : collectors)
                this.collectors.add(col);
        }

        public String getParentGroup(){
            if(groupId.contains(":")) return groupId.substring(0, groupId.lastIndexOf(":"));
            else return "";
        }

        public boolean add(IDataCollector iDataCollector) {
            return collectors.add(iDataCollector);
        }

        public String getGroupId() {
            return groupId;
        }

        public List<IDataCollector> getCollectors() {
            return collectors;
        }

        public String toString(){
            StringBuilder collectors = new StringBuilder();
            for(IDataCollector collector : this.collectors){
                collectors.append(collector.getId());
                collectors.append(",");
                collectors.append(collector.getDatapoints());
                collectors.append("\n");
            }
            return collectors.toString();
        }
    }
}
