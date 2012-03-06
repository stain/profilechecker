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

	private static OWLProfile DEFAULT_PROFILE = new OWL2Profile();
	private static List<OWLProfile> PROFILES = Arrays.asList(new OWL2DLProfile(),
			new OWL2ELProfile(), new OWL2Profile(), new OWL2QLProfile(),
			new OWL2RLProfile());

	private OWLOntologyManager m = OWLManager.createOWLOntologyManager();

	public static void main(String[] args) throws OWLOntologyCreationException {
        if (args.length == 0 || args[0].equals("-h")) {
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
            System.exit(1);
        }
	    
		System.exit(new ProfileChecker().check(args));
	}

	public int check(String[] args) throws OWLOntologyCreationException {

		IRI documentIRI = IRI.create(args[0]);
		if (!documentIRI.isAbsolute()) {
			// Assume it's a file
			documentIRI = IRI.create(new File(args[0]));
		}
		OWLOntology o = m.loadOntologyFromOntologyDocument(documentIRI);
		OWLProfile profile = null;

		if (args.length > 1) {
			String profileName = args[1];
			for (OWLProfile p : PROFILES) {
				if (p.getClass().getSimpleName().equals(profileName)) {
					profile = p;
				}
			}
			if (profile == null && !profileName.equals("--all")) {
				throw new IllegalArgumentException("Unknown profile: "
						+ profileName);
			}
		} else {
			profile = DEFAULT_PROFILE;
		}
		
		return check(o, System.out, System.err, profile);
	}
	
	public int check(OWLOntology o, PrintStream out, PrintStream err, OWLProfile ... profiles) {
		if (profiles == null || profiles.length == 0) {
			boolean anyFailed = false;
			for (OWLProfile p : PROFILES) {
				out.print(p.getClass().getSimpleName() + ": ");
				OWLProfileReport report = p.checkOntology(o);
				if (report.isInProfile()) {
					out.println("OK");
				} else {
					err.println(report.getViolations().size()
							+ " violations");
					anyFailed = true;
				}
			}
			return anyFailed ? 1 : 0;
		} else {
            for (OWLProfile profile : profiles) {
    			OWLProfileReport report = profile.checkOntology(o);
    			for (OWLProfileViolation v : report.getViolations()) {
    				err.println(v.toString());
    			}
    			if (!report.isInProfile()) {
    				return 1;
    			}
            }
			return 0;
		}

	}

}
