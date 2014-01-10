#hardlink ~/dev/onslyde/onslyde-panel ~/dev/onslyde/server/src/main/webapp/panel &&
#hardlink ~/dev/onslyde/onslyde ~/dev/onslyde/server/src/main/webapp/deck &&
mvn clean install &&
hardlink ~/www/jboss-as-7.1.1.Final/onslyde/screenshots ~/dev/onslyde/server/target/onslyde-hosted/screenshots
