#!/bin/bash
set -e # exit with nonzero exit code if anything fails

outputfolder=app/build/reports

# clear and re-create the report directory
rm -rf "$outputfolder" || exit 0;
mkdir -p "$outputfolder";

# run our compile script, discussed above
./gradlew clean build spoon

#### Move any straggler reports into the report folder -- I'm looking at you, spoon and lint!
mv -r "$outputfolder/../spoon" "$outputfolder/"
mv -r "$outputfolder/../outputs" "$outputfolder/"

# go to the out directory
cd "$outputfolder"

#Compress all reports down to a single index.html, perhaps?
find . -iname "*.js" -o -iname "*.html*" -o -iname "*.css" -type f | parallel htmlcompressor --compress-js --compress-css -o {} {}

#..and create a *new* Git repo
git init

# inside this git repo we'll pretend to be a new user
git config user.name "Travis CI"
git config user.email "chris.paiano@gmail.com"

# The first and only commit to this new Git repo contains all the
# files present with the commit message "Deploy to GitHub Pages".
git add .
git commit -m "Deploy to GitHub Pages"

# Force push from the current repo's master branch to the remote
# repo's gh-pages branch. (All previous history on the gh-pages branch
# will be lost, since we are overwriting it.) We redirect any output to
# /dev/null to hide any sensitive credential data that might otherwise be exposed.
git push --force --quiet "https://${GH_TOKEN}@${GH_REF}" master:gh-pages > /dev/null 2>&1
