/*
 * (c) 2012-2017 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *     http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.s11.owlapi;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;
import org.semanticweb.owlapi.profiles.Profiles;

public class ProfileChecker {

	private static final IRI PROFILE_BASE = IRI.create("http://www.w3.org/ns/owl-profile/");
    
	private static final OWLProfile DEFAULT_PROFILE = Profiles.OWL2_FULL;
	
	private final OWLOntologyManager ontologyManager;

	public ProfileChecker() {
	    this(OWLManager.createOWLOntologyManager());
	}
	
	public ProfileChecker(OWLOntologyManager ontologyManager) {
	    this.ontologyManager = ontologyManager;
	}
	    

	public int check(String[] args) throws OWLOntologyCreationException {
		if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
			printHelp();
			return 0;
		}
		// args must be non-empty by now, first argument must be
		// the ontology to check
		String iri = args[0];

        
        // Optional second argument: OWL Profile to check against
		final Optional<OWLProfile> profile;
		if (args.length < 2) {
		    profile = Optional.of(DEFAULT_PROFILE);
		} else if (args[1].equals("--all")) {
            profile = Optional.empty();
		} else {
            // Look up profile name
            String profileName = args[1];
            profile = owlProfilebyName(profileName);                
            if (! profile.isPresent()) {
                throw new IllegalArgumentException("Unknown profile: "
                        + profileName);
            }       
		}
        final boolean anyFailed = checkOntology(iri, profile);
		return anyFailed ? 1 : 0;
	}

    public boolean checkOntology(String iri, final Optional<OWLProfile> profile) throws OWLOntologyCreationException {
        OWLOntology ontology = loadOntology(iri);		
		final boolean anyFailed;
		if (profile.isPresent()) {
		    anyFailed = checkAgainstProfile(ontology, profile, System.err);
		} else {
			// --all prints to System.out as each ontology is checked in order
		    anyFailed = checkAllProfiles(ontology, System.out);
		}
        return anyFailed;
    }

    public boolean checkAgainstProfile(OWLOntology o, final Optional<OWLProfile> profile, PrintStream out) {
        OWLProfileReport report = profile.get().checkOntology(o);
        for (OWLProfileViolation v : report.getViolations()) {
            out.println(v.toString());
        }
        // true if failed
        return !report.isInProfile();
    }

    public boolean checkAllProfiles(OWLOntology o, PrintStream out) {
        boolean anyFailed = false;
        for (Profiles p : Profiles.values()) {
        	out.print(p.name() + ": ");
        	OWLProfileReport report = p.checkOntology(o);
        	if (report.isInProfile()) {
        		out.println("OK");
        	} else {        		
                out.println(report.getViolations().size()
        				+ " violations");
                // Don't return early, we'll check all profiles
        		anyFailed  = true;
        	}
        }
        return anyFailed;
    }

    public void printHelp() {
        System.out
        		.println("Usage: profilechecker.jar <ontology.owl> [profile]");
        System.out.println();
        System.out.println("Available profiles:");
        for (Profiles p : Profiles.values()) {
            // enum name (possible argument)
        	System.out.print(p.name());
        	// descriptive name (may contain spaces)
        	System.out.print(" (" + p.getName() + ")");
        	if (p.equals(DEFAULT_PROFILE)) {
        		System.out.print(" -default-");
        	}
        	System.out.println();
        }
        System.out.println("--all");
    }

    public OWLOntology loadOntology(String pathOrIri) throws OWLOntologyCreationException {
        Path path = Paths.get(pathOrIri);
        final IRI documentIRI;
        if (Files.isReadable(path)) {
            // NOTE: This is a lazy way to check if the user meant a filename.
            // If the user gives a filename that does not exist as a file, we will
            // try to load it as a IRI. This avoids distinguishing between 
            // "C:/WIDOWS/ontology.html" as a URI or filename
            documentIRI = IRI.create(path.toUri());
        } else {
            documentIRI = IRI.create(pathOrIri);
            if (! (documentIRI.isAbsolute())) {
                // Perhaps the user meant a 
                throw new OWLOntologyCreationException("Can't find: " + pathOrIri);
            }
        }
		return ontologyManager.loadOntologyFromOntologyDocument(documentIRI);        
    }

    public Optional<OWLProfile> owlProfilebyName(String profileName) {
        // e.g. "DL" -> <http://www.w3.org/ns/owl-profile/DL> 
        IRI profileIRI;
        try { 
            profileIRI = PROFILE_BASE.resolve(profileName);
        } catch (IllegalArgumentException ex) {
            profileIRI = null;
        }
        
        for (Profiles p : Profiles.values()) {
            if (p.name().equals(profileName) || // short enum-name
                p.getIRI().equals(profileIRI) ||
                p.getName().equals(profileName) || // might contain spaces 
                // compatibility with profilechecker <1.1 
                p.getOWLProfile().getClass().getSimpleName().equals(profileName)) {
                    return Optional.of(p);
            }
        }
        return Optional.empty();
    }
    
    public static void main(String[] args) throws OWLOntologyCreationException {
        System.exit(new ProfileChecker().check(args));
    }
    
}

