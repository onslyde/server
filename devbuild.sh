#!/bin/sh
rm -rf src/main/webapp/panel src/main/webapp/deck &&
#hardlink -u ~/dev/onslyde/server/target/onslyde-hosted/screenshots &&
grunt &&
gulp &&
mvn clean install &&
hardlink ~/www/jboss-as-7.1.1.Final/onslyde/screenshots ~/dev/onslyde/server/target/onslyde-hosted/screenshots
