package gitlet;

import java.io.File;
import java.util.TreeMap;

import static gitlet.Utils.*;

/* Represents a gitlet repository.
 *  structure:
 *  .gitlet/ -- top level folder for all persistent data
 */
public class Repository {
    /*
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");
    public static final File COMMITS_DIR = join(OBJECTS_DIR, "commits");
    public static final File BLOBS_DIR = join(OBJECTS_DIR, "blobs");
    public static final File REFS_DIR = join(GITLET_DIR, "refs"); //branches
    public static final File HEADS_DIR = join(REFS_DIR, "heads"); // folder to store heads
    public static final File HEAD = join(GITLET_DIR, "HEAD");
    public static final File STAGINGAREA = join(GITLET_DIR, "StagingArea");    // staging area


    public static void setupPersistence() {
        GITLET_DIR.mkdir();
        OBJECTS_DIR.mkdir();
        COMMITS_DIR.mkdir();
        BLOBS_DIR.mkdir();
        REFS_DIR.mkdir();
        HEADS_DIR.mkdir();

    }

    public static void init() {

        if (GITLET_DIR.exists()) {
            throw new GitletException("A Gitlet version-control system already exists in the current directory.");
        }
        setupPersistence();
        Commit initCommit = new Commit("initial commit", null, new TreeMap<>());
        String initHash = initCommit.getSHA1();


        File initialCommit = join(COMMITS_DIR, initHash);
        File main = join(HEADS_DIR, "master");
        writeObject(initialCommit, initCommit);

        writeContents(main, initHash);
        writeContents(HEAD, "master");

        StagingArea stagingArea = new StagingArea();
        writeObject(STAGINGAREA, stagingArea);
    }

    private static String getCurrentBranch() {
        return readContentsAsString(HEAD).trim();
    }

    private static String getCurrentCommitHash() {
        String branch = getCurrentBranch();
        File branchFile = join(HEADS_DIR, branch);
        return readContentsAsString(branchFile).trim();
    }

    private static Commit getCurrentCommit() {
        String hash = getCurrentCommitHash();
        File commitFile = join(COMMITS_DIR, hash);
        return readObject(commitFile, Commit.class);
    }

    public static void add(String fileName) {
        File file = join(CWD, fileName);

        if (!file.exists()) {
            throw new GitletException("File does not exist.");
        }
        byte[] contents = readContents(file);
        String hash = Utils.sha1(contents);

        Commit currCommit = getCurrentCommit();
        String oldHash = currCommit.getFileHash(fileName);

        StagingArea stagingArea = readObject(STAGINGAREA, StagingArea.class);
        if (hash.equals(oldHash)) {
            stagingArea.unstage(fileName);
        } else {
            stagingArea.stage(fileName, hash);
        }
        File blob = join(BLOBS_DIR, hash);
        writeContents(blob, contents);
    }

    public static void commit(String message) {
        if (message.isEmpty()) {
            throw new GitletException("Please enter a commit message");
        }

        StagingArea stagingArea = readObject(STAGINGAREA,StagingArea.class);
        if (stagingArea.noChanges()) {
            throw new GitletException("No changes added to the commit.");
        }

        String currBranch = getCurrentBranch();
        Commit parentCommit = getCurrentCommit();
        String prevHash = parentCommit.getSHA1();

        TreeMap<String, String> files = parentCommit.getTrackedFiles();
        files.putAll(stagingArea.getAdditions());
        files.remove(stagingArea.getRemovals());


        Commit newCommit = new Commit(message, prevHash, files);
        File newCommitFile = join(COMMITS_DIR,newCommit.getSHA1());
        writeObject(newCommitFile, newCommit);

        String newHash = newCommit.getSHA1();
        File branchPointer = join(HEADS_DIR, currBranch);
        writeContents(branchPointer, newHash);

        stagingArea = new StagingArea();
        writeObject(STAGINGAREA, stagingArea);
    }

    public static void rm(String fileName) {
        File file = join(CWD, fileName);
        StagingArea stagingArea = readObject(STAGINGAREA, StagingArea.class);
        Commit currCommit = getCurrentCommit();

        if (!stagingArea.isStagedForAddition(fileName) && !currCommit.isTracked(fileName)) {
            throw new GitletException("No reason to remove the file.");
        }

        if (currCommit.isTracked(fileName)) {
            stagingArea.stageForRemoval(fileName);
        }

        stagingArea.unstage(fileName);
        stagingArea.stageForRemoval(fileName);


    }
}
