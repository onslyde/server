Onslyde Server Setup
======

(this doc is not complete)
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
