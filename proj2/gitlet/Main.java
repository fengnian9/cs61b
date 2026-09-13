package gitlet;

import java.io.File;

import static gitlet.Utils.*;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author fengnian
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw error("Please enter a command.");
            }
            String firstArg = args[0];
            switch (firstArg) {
                case "init":
                    checkArgs(args, 1);
                    Repository.init();
                    break;

                case "add":
                    checkArgs(args, 2);
                    checkGitletDirectory();
                    Repository.add(args[1]);
                    break;

                case "commit":
                    checkArgs(args, 2);
                    checkGitletDirectory();
                    Repository.commit(args[1]);
                    break;

                case "rm":
                    checkArgs(args, 2);
                    checkGitletDirectory();
                    Repository.rm(args[1]);
                    break;

                case "log":
                    checkArgs(args, 1);
                    checkGitletDirectory();
                    Repository.log();
                    break;

                case "global-log":
                    checkArgs(args, 1);
                    checkGitletDirectory();
                    Repository.globalLog();
                    break;

                case "find":
                    checkArgs(args, 2);
                    checkGitletDirectory();
                    Repository.find(args[1]);
                    break;

                case "status":
                    checkArgs(args, 1);
                    checkGitletDirectory();
                    Repository.status();
                    break;

                case "checkout":
                    checkGitletDirectory();
                    handleCheckout(args);
                    break;

                case "branch":
                    checkArgs(args, 2);
                    checkGitletDirectory();
                    Repository.branch(args[1]);
                    break;

                case "rm-branch":
                    checkArgs(args, 2);
                    Repository.rmBranch(args[1]);
                    break;

                case "reset":
                    checkArgs(args, 2);
                    checkGitletDirectory();
                    Repository.reset(args[1]);
                    break;

                case "merge":
                    checkArgs(args, 2);
                    checkGitletDirectory();
                    Repository.merge(args[1]);
                    break;

                default:
                    throw error("No command with that name exists.");
            }
        } catch (GitletException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }

    /**
     * check for separator for the checkout command
     * @param arg
     */
    private static void checkSeparator(String arg) {
        if (!(arg.equals("--"))) {
            throw error("Incorrect operands");
        }
    }

    /**
     * confirm that right number of args has been passed to gitlet
     * @param args
     * @param expectedLength
     */
    private static void checkArgs(String[] args, int expectedLength) {
        if (!(args.length == expectedLength)) {
            throw  error("Incorrect operands.");
        }
    }

    /**
     * calls different checkout function based on the input args
     * @param args
     */
    private static void handleCheckout(String[] args) {
        switch (args.length) {
            case 2:
                Repository.checkoutBranch(args[1]);
                break;

            case 3:
                checkSeparator(args[1]);
                Repository.checkoutFile(args[2]);
                break;

            case 4:
                checkSeparator(args[2]);
                Repository.checkoutFileFromCommit(args[1], args[3]);
                break;

            default:
                throw error("Incorrect operands.");
        }
    }

    private static void checkGitletDirectory() {
        File gitletDir = Repository.GITLET_DIR;
        if (gitletDir.exists()) {
            return;
        }
        throw error("Not in an initialized Gitlet directory.");
    }

}



