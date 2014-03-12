#hardlink ~/dev/onslyde/onslyde-panel ~/dev/onslyde/server/target/onslyde-hosted/panel &&
#hardlink ~/dev/onslyde/onslyde ~/dev/onslyde/server/target/onslyde-hosted/deck &&

#hardlink ~/dev/onslyde/onslyde-panel ~/dev/onslyde/server/src/main/webapp/panel &&
#hardlink ~/dev/onslyde/onslyde ~/dev/onslyde/server/src/main/webapp/deck &&
mvn clean install &&
gulp &&
hardlink ~/www/jboss-as-7.1.1.Final/onslyde/screenshots ~/dev/onslyde/server/target/onslyde-hosted/screenshots
