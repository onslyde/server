#!/bin/sh

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
