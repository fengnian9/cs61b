package gitlet;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class StagingArea implements Serializable {
    private TreeMap<String, String> additions;
    private Set<String> removals;

    /**
     * initiate a new stagingarea, it should have additions and removals
     */
    public StagingArea() {
        additions = new TreeMap<>();
        removals =  new TreeSet<>();
    }

    /**
     * stage the file, record its hash
     * @param fileName
     * @param hash
     */
    public void stage(String fileName, String hash) {
        additions.put(fileName, hash);
    }

    /**
     * unstage the file
     * @param fileName
     */
    public void unstage(String fileName) {
        additions.remove(fileName);
        removals.remove(fileName);
    }

    /**
     * stage the file for removal
     * @param fileName
     */
    public void stageForRemoval(String fileName) {
        removals.add(fileName);
    }

    /**
     * returns the stagingarea instance's additions
     * @return
     */
    public TreeMap<String, String> getAdditions() {
        return new TreeMap<>(additions);
    }

    /**
     *
     * returns the stagingarea instance's removals
     * @return
     */
    public Set<String> getRemovals() {
        return new TreeSet<>(removals);
    }

    /**
     * there are no change, if additions and removals are both empty
     * @return
     */
    public boolean isEmpty() {
        return (additions.isEmpty() && removals.isEmpty());
    }

    /**
     * returns if a file is in additions
     * @param file
     * @return
     */
    public boolean isStagedForAddition(String file) {
        return additions.containsKey(file);
    }


}
