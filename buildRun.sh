#!/bin/bash
cd file-management
mvn clean install
cd target
java -jar file-management-0.0.1-SNAPSHOT.jar