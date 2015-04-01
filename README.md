############################################################
##
##  EECS 4481 Term Project Phase 1, HDv1
##  README.txt
##
##  Written by Aditya Murray (cse92012@cse.yorku.ca)
##  Revised on Febuary 1, 2015
##
############################################################

The files attached hold the source code fo the Computer
Security Laboratory, EECS 4481 Winter 2015, Term Project
Phase 1, with Professor Mark Shtern

    1. https://wiki.eecs.yorku.ca/course_archive/2014-15/W/4481/project
    2. https://wiki.eecs.yorku.ca/course_archive/2014-15/W/4481/_media/project.pdf

The project consists of a helpdesk application, that can be used by users, to get,
help from helpdesk agents. The application runs on Java 6, and has been developed
for Apache/Tomcat 7.

========================================
Getting Started
========================================

Basic Requirements:

* Java 6 and J2EE or later

Installation:

1.  Get the war file, and extract it into the webapps folder or in your
    wtpwebapps folder, under tomcat 7.

2.  Get the helpdesk.db file and change the context.xml file to reflect
    where the database file has been placed.
    The line that you need to change is
      "url="jdbc:sqlite://home/adityam/workspace/Chat/helpdesk.db" />"
                            to
      url="jdbc:sqlite:<YOUR LOCATION>/helpdesk.db" />
    Currently there are 5 entries in the databases for testing purposes
    =========================================================
    Id|Name    |Username   |Passwords
    _______________________________________________

    1 |Aditya  |cse92012   |I2013hwaBBS.!
    2 |Mark    |cse00128   |598Miagp.21
    3 |Matthew |cse82318   |02hdmt0.&
    4 |Ethan   |cse23891   |Eiwmi4.161!
    5 |Dimitri |cse13509   |detsh2s&*
    
    
3.  Get the supporting tools provided, and place them
    in your lib folder of the tomcat instance.
    The jar files that are needed are standard.jar, 
    owasp-java-html-sanitizer-r239.jar, guava-18.0, simplecaptcha-1.2.1.jar
    jstl.jar and sqlite-jdbc-3.8.6.jar. These files
    are provided in the zip file jars.zip

4.  The extracted WAR file folder must be called Chat

6.  Navigate to URL http://localhost:8080/Chat/Chat/login to login as a helper.
    or http://localhost:8080/Chat/Chat/help, to be helped.