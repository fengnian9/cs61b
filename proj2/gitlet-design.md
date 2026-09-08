# Gitlet Design Document

**Name**:
fengnian

# Gitlet Devlog

## 1. System Model

Repository layout / core state:

- Blob
    - filename = SHA-1(contents)
    - contents = snapshot of actual file contents

- Commit
    - filename = SHA-1(serialized Commit)
    - stores:
        - message
        - timestamp
        - parents
        - trackedFiles: filename -> blob hash (this is done using treemap)

- StagingArea
    - additions: filename -> blob hash
    - removals: set of filenames

- HEAD
    - stores current branch name

- refs/heads/<branch>
    - stores current commit hash


## 2. Command Semantics

### init ✅
- Create .gitlet structure
- Create initial commit
    - parents = []
    - trackedFiles = {}
    - timestamp = epoch (date(0))
- HEAD -> master
- master -> initial commit
- create empty StagingArea

### add ✅
1. Read working file contents (now it only supports 1 file per add command)
2. Compute blob hash
3. Compare against current commit's version
4. If identical:
    - unstage it if previously staged
5. Otherwise:
    - stage filename -> blob hash
    - persist blob in BLOBS_DIR
6. persist StagingArea

### commit ✅
1. Reject empty message / empty staging area
2. Get current commit
3. Copy parent's trackedFiles
4. Apply staged additions
5. Apply staged removals
6. Create new Commit
    - normal commit has one parent
7. Save new commit
8. Move current branch pointer to new commit
9. Clear staging area

### rm ✅
- If neither staged-for-addition nor tracked:
    - error
- If staged for addition:
    - unstage it
- If tracked:
    - stage for removal
    - delete working file if it still exists
- persist staging area

### log ✅
- Start from current commit
- print current commit
- follow first parent repeatedly
- stop after printing initial commit

### global-log ✅
- go in to the commit dir
- loop through the commit using the method given in utils.(order doesnt matter)
- print the logs out

### find ✅
- pretty much the same with global log, just loop through the commits in the commit directory and compare.

### status ✅ 
- go to head, find out which one is the current branch
- loop through all the branches in refs/heads.
- print out all branches, mark the current branch with a *. 
- print out staged files, both addition and removal
- print out removed files

### checkout 
- do input handling first
- if input is a file name only, get currCommit, search for the file, and restore it by finding the hash of the file.
- if commit id is passed along , find the commit first , then find the file.
- for branch name, fetch the latest commit of the given branch, get all its tracked files and check the file's hashes, if there are different file hashes, print msg and exit.
- overwrite the files. 
- after that, if file does not exist in the commit but are in the CWD, delete them. clear the staging area if checked out branch is not current branch. 
- change HEAD to the curr branch.



## 3. Important Invariants

- Old Commit objects must never be modified.
- `trackedFiles` should be copied, not shared by reference.
- Commit snapshot =
  parent snapshot + additions - removals.
- `add` stores the file version at add-time, not commit-time.
- `HEAD` points to branch name, not commit hash.
- Current branch file points to current commit hash.
- Initial commit has an empty parent list, not null.


## 4. Current Status

Done:
- init
- add
- commit
- rm
- log
- global log
- status

Currently working on:
- 

Known issues:
- none / ...

Tests passed:
- test02
  Tests failing:
- test01 and 03


## 5. NEXT SESSION — START HERE

1. Finish global log
3. Test:
    - staged-only file
    - tracked-only file
    - tracked + staged file
    - neither staged nor tracked
4. Then move to global-log / find

