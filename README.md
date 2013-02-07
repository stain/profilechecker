OWL API profile checker
=======================

(c) 2012-2013 University of Manchester

License: Apache License 2.0 (see LICENSE.TXT)

Author: Stian Soiland-Reyes <soiland-reyes@cs.manchester.ac.uk>



Building
--------

    stain@ralph-ubuntu:~/src/profilechecker$ mvn clean package
    [INFO] Scanning for projects...
    [INFO]                                                                         
    [INFO] ------------------------------------------------------------------------
    [INFO] Building OWL API profile checker 1.0
    [INFO] 
    (..)
    [INFO] Replacing /home/stain/src/profilechecker/target/profilechecker-1.0.jar with /home/stain/src/profilechecker/target/profilechecker-1.0-shaded.jar
    [INFO] Dependency-reduced POM written at: /home/stain/src/profilechecker/dependency-reduced-pom.xml
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 29.912s
    [INFO] Finished at: Thu Feb 07 15:34:12 GMT 2013
    [INFO] Final Memory: 22M/169M
    [INFO] ------------------------------------------------------------------------



Installation
------------    

On Linux, a JAR file can be made executable.

    stain@ralph-ubuntu:~/src/profilechecker$ chmod 755 target/profilechecker-1.0.jar
    stain@ralph-ubuntu:~/src/profilechecker$ sudo cp target/profilechecker-1.0.jar /usr/local/bin/profilechecker
    stain@ralph-ubuntu:~/src/profilechecker$ profilechecker 
    Usage: profilechecker.jar <ontology.owl> [profile]
    (..)


Creating shell/batch scripts for launching on OS X and Windows is left
as exercise to the reader.


Usage
-----

Help:

    $ java -jar target/profilechecker-1.0.jar -h
    Usage: profilechecker.jar <ontology.owl> [profile]

    Available profiles:
    OWL2DLProfile (OWL 2 DL)
    OWL2ELProfile (OWL 2 EL)
    OWL2Profile (OWL 2) -default-
    OWL2QLProfile (OWL 2 QL)
    OWL2RLProfile (OWL 2 RL)
    --all


The <ontology.owl> parameter can be given as a local file name or an
absolute IRI.

With only ontology IRI or file name, will check against default profile
(OWL 2 Full):

    $ java -jar target/profilechecker-1.0.jar http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl

Exit code is 0 if the ontology conforms to OWL 2 Full.    


Checking against a specific profile:    

    $ java -jar target/profilechecker-1.0.jar http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl OWL2QLProfile

    Use of non-superclass expression in position that requires a
      superclass expression:
      ObjectAllValuesFrom(<http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping>
      ObjectUnionOf(<http://www.co-ode.org/ontologies/pizza/pizza.owl#MozzarellaTopping>
      <http://www.co-ode.org/ontologies/pizza/pizza.owl#TomatoTopping>))
      [SubClassOf(<http://www.co-ode.org/ontologies/pizza/pizza.owl#Margherita>
      ObjectAllValuesFrom(<http://www.co-ode.org/ontologies/pizza/pizza.owl#hasTopping>
      ObjectUnionOf(<http://www.co-ode.org/ontologies/pizza/pizza.owl#MozzarellaTopping>
      <http://www.co-ode.org/ontologies/pizza/pizza.owl#TomatoTopping>)))
      in <http://www.co-ode.org/ontologies/pizza/pizza.owl>] 
    (..)


Exit code is 0 if the ontology conforms to the specified profile.


Checking against all profiles:


    $ java -jar target/profilechecker-1.0.jar http://www.co-ode.org/ontologies/pizza/2007/02/12/pizza.owl --all
    OWL2DLProfile: OK
    OWL2ELProfile: 187 violations
    OWL2Profile: OK
    OWL2QLProfile: 52 violations
    OWL2RLProfile: 188 violations

Exit code is 0 if the ontology conforms to all profiles.

