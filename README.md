# OWL API profile checker

(c) 2012-2017 The University of Manchester

License: [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0) 
  (see `LICENSE` and `NOTICE` for required notices)

Author: Stian Soiland-Reyes <soiland-reyes@manchester.ac.uk>


## Requirements

* [Java](https://java.com/en/download/) 8 or [OpenJDK](http://openjdk.java.net/) 8
* [Apache Maven](https://maven.apache.org/download.cgi) 3.3 or later 


## Building

Note: you don't need to compile from source code, you can also use one of the 
[releases](https://github.com/stain/profilechecker/releases).


    stain@ralph-ubuntu:~/src/profilechecker$ mvn clean package
    [INFO] Scanning for projects...
    [INFO]                                                                         
    [INFO] ------------------------------------------------------------------------
    [INFO] Building OWL API profile checker 1.1.0
    [INFO] 
    (..)
    [INFO] Replacing /home/stain/src/profilechecker/target/profilechecker-1.1.0.jar with /home/stain/src/profilechecker/target/profilechecker-1.0-shaded.jar
    [INFO] Dependency-reduced POM written at: /home/stain/src/profilechecker/dependency-reduced-pom.xml
    [INFO] ------------------------------------------------------------------------
    [INFO] BUILD SUCCESS
    [INFO] ------------------------------------------------------------------------
    [INFO] Total time: 29.912s
    [INFO] Finished at: Thu Feb 07 15:34:12 GMT 2013
    [INFO] Final Memory: 22M/169M
    [INFO] ------------------------------------------------------------------------



## Usage

### Help

    $ java -jar target/profilechecker-1.1.0.jar -h
    Usage: profilechecker.jar <ontology.owl> [profile]
    
    Available profiles:
    OWL2_DL (OWL 2 DL)
    OWL2_QL (OWL 2 QL)
    OWL2_EL (OWL 2 EL)
    OWL2_RL (OWL 2 RL)
    OWL2_FULL (OWL 2 DL) -default-
    --all

(Modify the version number `1.1.0` above to correspond to the output of your build)

The `<ontology.owl>` parameter can be given as a local file name or an
absolute IRI.

### Default profile

With only ontology IRI or file name, will check against default profile
(OWL 2 Full):

    $ java -jar target/profilechecker-1.1.0.jar https://cdn.rawgit.com/owlcs/pizza-ontology/v1.5.0/pizza.owl

Exit code is 0 if the ontology conforms to OWL 2 Full, in which case there should be no output on STDERR.    


### Specify OWL2 profile

Checking against a specific profile:    

    $ java -jar target/profilechecker-1.1.0.jar https://cdn.rawgit.com/owlcs/pizza-ontology/v1.5.0/pizza.owl OWL2QLProfile

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

Exit code is 0 if the ontology conforms to the specified profile, with errors logged to STDERR.

The ontology profile can be specified in any of these forms (in order of preference):

* `OWL2_DL` ([`Profiles`](http://owlcs.github.io/owlapi/apidocs_5/org/semanticweb/owlapi/profiles/Profiles.html) enum value)
* `http://www.w3.org/ns/owl-profile/DL` (IRI of OWL profile)
* `DL` (relative IRI of OWL profile)
* `OWL2DLProfile` (classname)


### All profiles

Checking against all profiles:


    $ java -jar target/profilechecker-1.1.0.jar https://cdn.rawgit.com/owlcs/pizza-ontology/v1.5.0/pizza.owl --all
    OWL2_DL: OK
    OWL2_QL: 52 violations
    OWL2_EL: 66 violations
    OWL2_RL: 188 violations
    OWL2_FULL: OK


Exit code is 0 if the ontology conforms to all profiles. 
The violation count per profile is output to STDOUT.


### Warnings

Note that any warnings or errors logged from the OWLAPI (prefix `[main]`)
during ontology loading do not necessarily mean violation against the profile:

    $ java -jar target/profilechecker-1.1.0.jar ~/Desktop/annotated.ttl --all
    [main] ERROR uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl - Illegal redeclarations of entities: reuse of entity http://example.com/annotatedOntology#property1 in punning not allowed [Declaration(AnnotationProperty(<http://example.com/annotatedOntology#property1>)), Declaration(ObjectProperty(<http://example.com/annotatedOntology#property1>))]
    OWL2_DL: 1 violations
    OWL2_QL: 1 violations
    OWL2_EL: 1 violations
    OWL2_RL: 1 violations
    OWL2_FULL: OK


