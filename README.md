# buchi-complementation
Java implementation of a complementation algorithm for Büchi automata.

## Specification
The algorithm is specified in [this article](http://diuf.unifr.ch/drupal/tns/sites/diuf.unifr.ch.drupal.tns/files/Publications/draft.pdf).

## Build
Compilation is usually done using the NetBeans configuration.
To be able to view the original and complemented automata, a version of GOAL must be available. It can be downloaded from:
http://goal.im.ntu.edu.tw/wiki/doku.php. The path to the `goal` executable must be set in the `BuchiComp/config.xml` file.

## Run the program
Execute the jar file: `BuchiComp/dist/BuchiComp.jar`, e.g. `java -jar BuchiComp.jar`

## External resources
A set of randomly generated automata can be found here: http://goal.im.ntu.edu.tw/wiki/lib/exe/fetch.php?media=goal:ciaa2010_automata.tar.gz
