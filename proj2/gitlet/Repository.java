package gitlet;

import java.io.File;
import java.util.*;

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
            throw error("A Gitlet version-control system already exists in the current directory.");
        }
        setupPersistence();
        Commit initCommit = new Commit("initial commit", new ArrayList<>(), new TreeMap<>());
        String initHash = initCommit.getSHA1();


        File initialCommit = join(COMMITS_DIR, initHash);
        File main = join(HEADS_DIR, "master");
        writeObject(initialCommit, initCommit);

        writeContents(main, initHash);
        writeContents(HEAD, "master");

        StagingArea stagingArea = new StagingArea();
        writeObject(STAGINGAREA, stagingArea);
    }

    /**
     * @return returns set A.removeAll setB;
     */
    private static Set<String> difference(Set<String> A, Set<String> B) {
        Set<String> result = new TreeSet<>(A);
        result.removeAll(B);
        return result;
    }


    private static Set<String> intersection(Set<String> A, Set<String> B) {
        Set<String> result = new TreeSet<>(A);
        result.retainAll(B);
        return result;
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

    private static Commit getCommit(String commitHash) {
        File commitFile = join(COMMITS_DIR, commitHash);
        return readObject(commitFile, Commit.class);
    }

    private static String getFileHash(File file) {
        if (!file.exists()) {
            throw error("File does not exist.");
        }
        byte[] contents = readContents(file);
        String hash = Utils.sha1(contents);
        return hash;
    }

    public static void add(String fileName) {
        File file = join(CWD, fileName);

        String hash = getFileHash(file);
        byte[] contents = readContents(file);

        // get hash of the file from previous commit to compare
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

        writeObject(STAGINGAREA, stagingArea);

    }

    public static void commit(String message) {
        if (message.isEmpty()) {
            throw error("Please enter a commit message");
        }

        StagingArea stagingArea = readObject(STAGINGAREA, StagingArea.class);
        if (stagingArea.noChanges()) {
            throw error("No changes added to the commit.");
        }

        // get the previous parent's sha1
        String currBranch = getCurrentBranch();
        Commit parentCommit = getCurrentCommit();
        String prevHash = parentCommit.getSHA1();

        ArrayList<String> parents = new ArrayList<>();
        parents.add(prevHash);

        TreeMap<String, String> files = parentCommit.getTrackedFiles();
        files.putAll(stagingArea.getAdditions());
        files.keySet().removeAll(stagingArea.getRemovals());

        Commit newCommit = new Commit(message, parents, files);
        File newCommitFile = join(COMMITS_DIR, newCommit.getSHA1());
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

        boolean staged = stagingArea.isStagedForAddition(fileName);
        boolean tracked = currCommit.isTracked(fileName);

        if (!staged && !tracked) {
            throw error("No reason to remove the file.");
        }

        if (staged) {
            stagingArea.unstage(fileName);
        }

        if (tracked) {
            stagingArea.stageForRemoval(fileName);
            if (file.exists()) {
                restrictedDelete(file);
            }
        }

        writeObject(STAGINGAREA, stagingArea);

    }

    public static void log() {
        Commit currCommit = getCurrentCommit();
        while (true) {
            currCommit.printLog();

            if (!currCommit.hasParent()) {
                break;
            }
            currCommit = getCommit(currCommit.getFirstParentHash());
        }
    }

    public static void globalLog() {
        List<String> files = plainFilenamesIn(COMMITS_DIR);
        for (String commitHash : files) {
            getCommit(commitHash).printLog();
        }

    }

    public static void find(String commitMessage) {
        List<String> files = plainFilenamesIn(COMMITS_DIR);
        for (String commitHash : files) {
            Commit commit = getCommit(commitHash);
            if (commit.getMessage().equals(commitMessage)) {
                System.out.println(commit.getSHA1());
            }
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        String currBranch = getCurrentBranch();

        List<String> branchName = plainFilenamesIn(HEADS_DIR);
        for (String branch : branchName) {
            if (branch.equals(currBranch)) {
                System.out.println("*" + branch);
            } else {
                System.out.println(branch);
            }
        }
        System.out.println();

        System.out.println("=== Staged Files ===");
        StagingArea stagingArea = readObject(STAGINGAREA, StagingArea.class);
        TreeMap<String, String> additions = stagingArea.getAdditions();
        for (String stagedFile : additions.keySet()) {
            System.out.println(stagedFile);
        }
        System.out.println();

        System.out.println("=== Removed Files ===");

        Set<String> removals = stagingArea.getRemovals();
        for (String removedFile : removals) {
            System.out.println(removedFile);
        }
        System.out.println();


        System.out.println("=== Modifications Not Staged For Commit ===");

        Commit currCommit = getCurrentCommit();
        TreeMap<String, String> expectedState = new TreeMap<>(currCommit.getTrackedFiles());

        expectedState.putAll(additions);
        expectedState.keySet().removeAll(removals);

        List<String> f = plainFilenamesIn(CWD);
        TreeSet<String> workingFiles =  new TreeSet<>(f);
        TreeSet<String> expectedFiles = new TreeSet<>(expectedState.keySet());

        TreeSet<String> untrackedFiles = (TreeSet<String>) difference(workingFiles, expectedFiles);
        TreeSet<String> removedFiles = (TreeSet<String>) difference(expectedFiles, workingFiles);
        TreeSet<String> commonFiles = (TreeSet<String>) intersection(expectedFiles, workingFiles);

        TreeMap<String, String> printLog = new TreeMap<String, String>();

        for (String file: commonFiles) {
            File fileAddress = join(CWD, file);
            if (!Objects.equals(expectedState.get(file), getFileHash(fileAddress))) {
                printLog.put(file, "modified");
            }
        }

        for (String removedFile : removedFiles) {
            printLog.put(removedFile, "deleted");
        }

        for (Map.Entry<String, String> log : printLog.entrySet()) {
            System.out.printf("%s (%s)%n", log.getKey(), log.getValue());
        }

        System.out.println();

        System.out.println("=== Untracked Files ===");
        for (String untracked : untrackedFiles) {
            System.out.println(untracked);
        }
        System.out.println();
    }

    /**
     * takes all files of the branch's latest commit, and puts them in the CWD, overwriting files that already exist.
     * delete files that are untracked by the branch's commit.
     * if a working file is untracked and would be overwritten by the checkout, throw error.
     * @param branchName
     */
    public static void checkoutBranch(String branchName) {

        File branch = join(HEADS_DIR, branchName);
        if (!branch.exists()) {
            throw error("No such branch exists.");
        } else if (branchName.equals(getCurrentBranch())) {
            throw error("No need to checkout the current branch.");
        }

        // get branch's latest commit
        String commitHash = readContentsAsString(branch);
        Commit checkoutCommit = getCommit(commitHash);


        checkoutCommitSnapshot(checkoutCommit);

        // change branch to the given branch
        writeContents(HEAD, branchName);


    }

    /**
     * takes in the commit, restore the working directory back to the commit's time.
     * @param desiredCommit the commit user wants to checkout to.
     */
    private static void checkoutCommitSnapshot(Commit desiredCommit) {

        TreeMap<String, String> trackedFiles = desiredCommit.getTrackedFiles();

        StagingArea stagingArea = readObject(STAGINGAREA, StagingArea.class);
        Set<String> removals = stagingArea.getRemovals();
        TreeMap<String, String> additions = stagingArea.getAdditions();

        Commit currCommit = getCurrentCommit();
        TreeMap<String, String> committedFiles = new TreeMap<>(currCommit.getTrackedFiles());
        TreeMap<String, String> expectedState = new TreeMap<>(currCommit.getTrackedFiles());

        expectedState.putAll(additions);
        expectedState.keySet().removeAll(removals);

        List<String> f = plainFilenamesIn(CWD);
        TreeSet<String> workingFiles = new TreeSet<>(f);
        TreeSet<String> expectedFiles = new TreeSet<>(expectedState.keySet());

        TreeSet<String> untrackedFiles = (TreeSet<String>) difference(workingFiles, expectedFiles);
        TreeSet<String> dangerousFiles = (TreeSet<String>) intersection(untrackedFiles, trackedFiles.keySet());
        TreeSet<String> filesToBeRemoved = (TreeSet<String>) difference(committedFiles.keySet(), trackedFiles.keySet());


        if (!dangerousFiles.isEmpty()) {
            throw error("There is an untracked file in the way; delete it, or add and commit it first.");

        }

        // overwriting commit content
        for (Map.Entry<String, String> file : trackedFiles.entrySet()) {
            String fileName = file.getKey();
            String blobHash = file.getValue();

            File newFile = join(CWD, fileName);
            File content = join(BLOBS_DIR, blobHash);

            byte[] commitContent = readContents(content);
            writeContents(newFile, commitContent);
        }

        // delete file
        for (String deleteFile : filesToBeRemoved) {
            restrictedDelete(deleteFile);
        }

        stagingArea = new StagingArea();
        writeObject(STAGINGAREA, stagingArea);
    }

    public static void checkoutFile(String fileName) {

        Commit currCommit = getCurrentCommit();
        TreeMap<String, String> trackedFiles = currCommit.getTrackedFiles();

        if (!trackedFiles.containsKey(fileName)) {
            throw error("File does not exist in that commit");
        }

        File content = join(BLOBS_DIR, trackedFiles.get(fileName));
        byte[] commitContent = readContents(content);
        File file = join(CWD, fileName);

        writeContents(file, commitContent);
    }

    public static void checkoutFileFromCommit(String commitHash, String fileName) {

        Commit givenCommit = resolveCommit(commitHash);

        TreeMap<String, String> trackedFiles = givenCommit.getTrackedFiles();

        if (!trackedFiles.containsKey(fileName)) {
            throw error("File does not exist in that commit");
        }

        File content = join(BLOBS_DIR, trackedFiles.get(fileName));
        byte[] commitContent = readContents(content);

        File file = join(CWD, fileName);
        writeContents(file, commitContent);

    }

    /**
     * finds the commit given a shorten/full commit id
     * @param commitHash commit id
     * @return
     */
    private static Commit resolveCommit(String commitHash) {

        Commit givenCommit = null;

        List<String> commits = plainFilenamesIn(COMMITS_DIR);

        for (String commit : commits) {
            if (commit.startsWith(commitHash)) {
                givenCommit = readObject(join(COMMITS_DIR, commit), Commit.class);
                return givenCommit;
            }
        }

        throw error("No commit with that id exists");

    }

    public static void branch(String branchName) {
        File newBranch = join(HEADS_DIR, branchName);
        if (newBranch.exists()) {
            throw error("A branch with that name already exists.");
        }
        String commitHash = getCurrentCommitHash();
        writeContents(newBranch, commitHash);

    }

    public static void rmBranch(String branchName) {
        File deleteBranch = join(HEADS_DIR, branchName);
        if (!deleteBranch.exists()) {
            throw error("A branch with that name does not exist.");
        }

        String currBranch = getCurrentBranch();
        if (Objects.equals(branchName, currBranch)) {
            throw error("Cannot remove the current branch.");
        }

        restrictedDelete(deleteBranch);
    }

    /**
     * go back to arbitrary commit, given by the user,
     * change current branch's pointer to the given commit.
     * @param commitHash
     */
    public static void reset(String commitHash) {

        Commit givenCommit = resolveCommit(commitHash);
        String commitSHA1 = givenCommit.getSHA1();

        checkoutCommitSnapshot(givenCommit);

        String branchName = getCurrentBranch();
        writeContents(join(HEADS_DIR, branchName), commitSHA1);
    }

    public static void merge(String branchName) {

        return;
    }

}
