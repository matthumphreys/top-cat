Top Cat
=======

This project helps work out what a user's favorite categories are. 
It's designed for apps like Metro 10 that have hits (e.g. reading an article) 
and misses (e.g. skipping or down voting an article). 

It uses basic probability: hits / (hits + misses). 

It adjusts probabilities to compensate for small sample sizes.

HTML/JS demo
------------

See this project's demo folder.

Java code
---------

Provided in the src folder. There are only a few classes. 
To see how's it's used view src/test/java/topcat/CategoryManagerTest.java 

Maven
-----

The project is built using Maven. Until we put the jar in a maven repo you can download it from this project's dist folder.

Anything else?
--------------

Just this...

![Top Cat](http://img4.wikia.nocookie.net/__cb20110424163028/topcat/images/5/50/Topcat002-1-.gif)
