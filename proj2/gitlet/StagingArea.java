package gitlet;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class StagingArea implements Serializable {
    private TreeMap<String, String> additions;
    private Set<String> removals;

    public StagingArea() {
        additions = new TreeMap<>();
        removals =  new TreeSet<>();
    }

    public void stage(String fileName, String hash) {
        additions.put(fileName, hash);
    }

    public void unstage(String fileName) {
        additions.remove(fileName);
    }

    public void stageForRemoval(String fileName) {
        removals.add(fileName);
    }


    public TreeMap<String, String> getAdditions() {
        return new TreeMap<>(additions);
    }

    public Set<String> getRemovals() {
        return new TreeSet<>(removals);
    }

    public boolean noChanges() {
        return (additions.isEmpty() && removals.isEmpty());
    }

    public boolean isStagedForAddition(String file) {
        return additions.containsKey(file);
    }


}
