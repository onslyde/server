Onslyde Server Setup
======

##Important Message
So this server is a bit complex to setup. It requires a mysql database, binding to ports 80 and 443 (with sudo), setting up a keystore for SSL/SPDY/WSS, and a lot of custom config in the JBoss server. I'm leaving this message to let you know that my current focus is on getting everything working properly and things are changing drastically on every deployment. It will be a waste a time to write docs until I'm done with the final setup.

If you'd like to run this in it's current state, shoot an email to wesleyhales at gmail.com and I will put a zip file up on dropbox with everything you need in its current state.


##Setup
This server is running in the following environment:

java version "1.7.0_25"
Java(TM) SE Runtime Environment (build 1.7.0_25-b15)
Java HotSpot(TM) 64-Bit Server VM (build 23.25-b01, mixed mode)

mysql is:
Version: '5.5.23'

Create a new mysql DB:
 mysql -u root
    CREATE database onslyde;
    CREATE USER 'onslyde'@'localhost' IDENTIFIED BY 'password';
    GRANT ALL PRIVILEGES ON onslyde.* TO 'onslyde'@'localhost' WITH GRANT OPTION;

then:
import script

run the server:
./bin/standalone -b 0.0.0.0
