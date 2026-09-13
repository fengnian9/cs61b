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

    /**
     * intializes a commit
     * @param message the commit's message
     * @param parentHashes the commit's parents, 0 if it doesnt have one, 2 if there is a merge
     * @param files
     */
    public Commit(String message, List<String> parentHashes, TreeMap<String, String> files) {
        this.message = message;
        this.parents = new ArrayList<>(parentHashes);

        this.trackedFiles = new TreeMap<>(files);
        if (parents.isEmpty()) {
            this.timeStamp = new Date(0);
        } else {
            this.timeStamp = new Date();
        }
    }

    /**
     * returns the hash of the file tracked by the commit
     * @param fileName
     * @return
     */
    public String getFileHash(String fileName) {
        return trackedFiles.get(fileName);

    }

    /**
     *
     * @return returns the SHA1 hash of the commit
     */
    public String getSHA1() {
        return Utils.sha1(Utils.serialize(this));
    }

    /**
     *
     * @return true if the commit's parent is not empty
     */
    public boolean hasParent() {
        return !parents.isEmpty();
    }

    /**
     *
     * @return the first parent commit's hash
     */
    public String getFirstParentHash() {
        return parents.get(0);
    }

    public ArrayList<String> getParents() {
        return new ArrayList<String> (this.parents);
    }


    /**
     *
     * @return return the trackedfiles of the commit
     */
    public TreeMap<String, String> getTrackedFiles() {
        return new TreeMap<>(this.trackedFiles);
    }

    /**
     *
     * @param file
     * @return returns true if a file is tracked by the commit
     */
    public boolean isTracked(String file) {
        return this.trackedFiles.containsKey(file);
    }

    /**
     * prints the log for the commit
     */
    public void printLog() {
        System.out.println("===");
        System.out.println("commit " + this.getSHA1());
        if (this.parents.size() > 1) {
            System.out.printf("Merge: %.7s %.7s%n", parents.get(0), parents.get(1));
        }

        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        System.out.println("Date: " + formatter.format(this.timeStamp));
        System.out.println(this.message);
        System.out.println();
    }

    /**
     *
     * @return the message of the commit
     */
    public String getMessage() {
        return this.message;
    }

}
