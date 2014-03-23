top-cat
=======

This project helps work out what a user's favorite categories are. It's designed for apps like Metro 10 that have hits (e.g. reading an article) and misses (e.g. skipping or down voting an article). It uses basic probability: hits / (hits + misses). It adjusts probabilities to compensate for small sample sizes.

To see what it does view the [HTML demo](http://s.metro.co.uk/dev/lab/) - the source lives in this project's html folder.

A java version is also provided in this project (in the src folder). There are only a few classes - the tests (under src/test) serve as examples.
