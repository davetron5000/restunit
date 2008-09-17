#!/bin/sh
echo "git fetch git://github.com/davetron5000/restunit.git"
git fetch git://github.com/davetron5000/restunit.git
echo "cd trunk/"
cd trunk/
echo "git fetch git://github.com/davetron5000/restunit.git"
git fetch git://github.com/davetron5000/restunit.git
echo "git branch tmp $(cut -b-40 .git/FETCH_HEAD)"
git branch tmp $(cut -b-40 .git/FETCH_HEAD)
echo "git tag -a -m "Last fetch" newlast tmp"
git tag -a -m "Last fetch" newlast tmp
echo "git rebase --onto master last tmp"
git rebase --onto master last tmp
echo "git branch -M tmp master"
git branch -M tmp master
echo "git-svn dcommit"
git-svn dcommit
echo "mv .git/refs/tags/newlast .git/refs/tags/last "
mv .git/refs/tags/newlast .git/refs/tags/last 
