#!/bin/sh

mvn install:install-file -Dfile=spigot-1.9.jar -DgroupId=spigot -DartifactId=spigot -Dversion=1.9 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.9.4.jar -DgroupId=spigot2 -DartifactId=spigot -Dversion=1.9.4 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.10.jar -DgroupId=spigot3 -DartifactId=spigot -Dversion=1.10 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.11.2.jar -DgroupId=spigot4 -DartifactId=spigot -Dversion=1.11.2 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.12.jar -DgroupId=spigot5 -DartifactId=spigot -Dversion=1.12 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.13.jar -DgroupId=spigot6 -DartifactId=spigot -Dversion=1.13 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.14.4.jar -DgroupId=spigot7 -DartifactId=spigot -Dversion=1.14.4 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.15.2.jar -DgroupId=spigot8 -DartifactId=spigot -Dversion=1.15.2 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.16.1.jar -DgroupId=spigot9 -DartifactId=spigot -Dversion=1.16.1 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.16.2.jar -DgroupId=spigot10 -DartifactId=spigot -Dversion=1.16.2 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.17.jar -DgroupId=spigot11 -DartifactId=spigot -Dversion=1.17 -Dpackaging=jar
mvn install:install-file -Dfile=spigot-1.18.1.jar -DgroupId=spigot12 -DartifactId=spigot -Dversion=1.18.1 -Dpackaging=jar

echo "Maven Repository creating success."