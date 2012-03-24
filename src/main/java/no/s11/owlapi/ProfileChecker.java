package no.s11.owlapi;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

public class ProfileChecker {

	public static final OWLProfile DEFAULT_PROFILE = new OWL2Profile();
	public static final OWLProfile[] PROFILES = new OWLProfile[] {new OWL2DLProfile(),
			new OWL2ELProfile(), new OWL2Profile(), new OWL2QLProfile(),
			new OWL2RLProfile()};

	public static void main(String[] args) throws OWLOntologyCreationException {
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            System.out
                    .println("Usage: profilechecker.jar <ontology.owl> [profile]");
            System.out.println();
            System.out.println("Available profiles:");
            for (OWLProfile p : PROFILES) {
                System.out.print(p.getClass().getSimpleName());
                System.out.print(" (" + p.getName() + ")");
                if (p.getClass().equals(DEFAULT_PROFILE.getClass())) {
                    System.out.print(" -default-");
                }
                // Can't use p.getName() as it contains spaces
                System.out.println();
            }
            System.out.println("--all");
            System.exit(-1);
        }
	    
		System.exit(ProfileChecker.parseAndCheck(args));
	}

	private static int parseAndCheck(String[] args) throws OWLOntologyCreationException {

	    if(args.length == 0 || "--all".equals(args[0])) {
	        throw new IllegalArgumentException("Did not specify an ontology to check.");
	    }
	    
		IRI documentIRI = IRI.create(args[0]);
		if (!documentIRI.isAbsolute()) {
			// Assume it's a file
			documentIRI = IRI.create(new File(args[0]));
		}
	    OWLOntologyManager m = OWLManager.createOWLOntologyManager();

		OWLOntology o = m.loadOntologyFromOntologyDocument(documentIRI);

		if (args.length > 1) {
			String profileName = args[1];
            if (profileName.equals("--all")) {
                return ProfileChecker.check(o, System.out, System.err, PROFILES);
            } else {
            
    			for (OWLProfile p : PROFILES) {
    				if (p.getClass().getSimpleName().equals(profileName)) {
    	                return ProfileChecker.check(o, System.out, System.err, p);
    				}
    			}
				throw new IllegalArgumentException("Unknown profile: "
						+ profileName);
            }
		} else {
	        return ProfileChecker.check(o, System.out, System.err, DEFAULT_PROFILE);
		}
	}
	
	/**
	 * Checks the given ontology to determine whether it fits all of the given profiles.
	 * 
	 * @param o The ontology to check
	 * @param out The standard output stream for printing informational messages
	 * @param err The error output stream for printing error messages
	 * @param profiles The profiles to check the ontology against.
	 * @return 0 if the ontology is in all of the given profiles, and otherwise return the number of profiles that failed.
	 * @throws IllegalArgumentException if no profiles were specified or the ontology was null
	 */
	public static int check(OWLOntology o, PrintStream out, PrintStream err, OWLProfile ... profiles) {
	    
	    if(profiles == null || profiles.length == 0) {
	        throw new IllegalArgumentException("No profiles specified to check");
	    }
	    
	    if(o == null) {
            throw new IllegalArgumentException("No ontology specified to check");
	    }
	    
        int anyFailed = 0;
        for (OWLProfile profile : profiles) {
			OWLProfileReport report = profile.checkOntology(o);
			for (OWLProfileViolation v : report.getViolations()) {
				err.println(v.toString());
			}
			
			if (!report.isInProfile()) {
                anyFailed++;
			}
        }
        
        return anyFailed;
	}

}
