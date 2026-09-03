package gitlet;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/** Represents a gitlet commit object.
 *
 *  does at a high level.
 *
 *  @author fengnian
 */
public class Commit implements Serializable {
    /**
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /**
     * The message of this Commit.
     */
    private static final long serialVersionUID = 123456789L;
    private String message;
    private Date timeStamp;
    private List<String> parents;
    private TreeMap<String, String> trackedFiles; // <name, hash>


    public Commit(String message, String parentHash, TreeMap<String, String> files) {
        this.message = message;
        this.parents = new ArrayList<>();
        if (parentHash != null) {
            this.parents.add(parentHash);
        }
        this.trackedFiles = new TreeMap<>(files);
        if (parentHash == null) {
            this.timeStamp = new Date(0);
        } else {
            this.timeStamp = new Date();
        }
    }

    public String getFileHash(String fileName) {
        return trackedFiles.get(fileName);

    }


    public String getSHA1() {
        return Utils.sha1(Utils.serialize(this));
    }

    public boolean hasParent() {
        return parents.isEmpty();
    }

    public String getFirstParentHash() {
        return parents.get(0);
    }


    public TreeMap<String, String> getTrackedFiles() {
        return new TreeMap<>(this.trackedFiles);
    }

    public boolean isTracked(String file) {
        return this.trackedFiles.containsKey(file);
    }

    public void printLog() {
        System.out.println("===");
        System.out.println("commit " + this.getSHA1());
        if (this.parents.size() > 1) {
            System.out.printf("Merge: %.7s %.7s%n", parents.get(0),parents.get(1));
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        System.out.println("Date: " + formatter.format(this.timeStamp));
        System.out.println(this.message);
        System.out.println();
    }

}