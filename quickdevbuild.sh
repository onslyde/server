#!/bin/sh

#This script is responsible for setting up the hard links on a local environment
#allows for easy start server, and run gruntfile from panel or deck for UI builds that don't require a mvn run and war deploy


#TODO - check to see if dirs exist
#rm -rf ~/dev/onslyde/server/src/main/webapp/panel/ ~/dev/onslyde/server/src/main/webapp/deck

#unlink the files so mvn clean doesn't wipe the directory
hardlink -u ~/dev/onslyde/server/target/onslyde-hosted/panel &&
hardlink -u ~/dev/onslyde/server/target/onslyde-hosted/deck &&
hardlink -u ~/dev/onslyde/server/target/onslyde-hosted/screenshots &&

mvn clean install &&
gulp &&

#create the hard links in the temporary target directory
hardlink ~/www/jboss-as-7.1.1.Final/onslyde/screenshots ~/dev/onslyde/server/target/onslyde-hosted/screenshots &&
hardlink ~/dev/onslyde/onslyde-panel ~/dev/onslyde/server/target/onslyde-hosted/panel &&
hardlink ~/dev/onslyde/onslyde ~/dev/onslyde/server/target/onslyde-hosted/deck
