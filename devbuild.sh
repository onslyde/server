rm -rf src/main/webapp/panel src/main/webapp/deck &&
grunt &&
gulp &&
mvn clean install &&
hardlink ~/www/jboss-as-7.1.1.Final/onslyde/screenshots ~/dev/onslyde/server/target/onslyde-hosted/screenshots
