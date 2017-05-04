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

import java.io.File;
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
    OWLProfile DEFAULT_PROFILE = Profiles.OWL2_FULL;
	OWLOntologyManager m = OWLManager.createOWLOntologyManager();

	public static void main(String[] args) throws OWLOntologyCreationException {
		System.exit(new ProfileChecker().check(args));
	}

	public int check(String[] args) throws OWLOntologyCreationException {

		if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
			System.out
					.println("Usage: profilechecker.jar <ontology.owl> [profile]");
			System.out.println();
			System.out.println("Available profiles:");
			for (Profiles p : Profiles.values()) {
				System.out.print(p.name());
				System.out.print(" (" + p.getName() + ")");
				if (p.equals(DEFAULT_PROFILE)) {
					System.out.print(" -default-");
				}
				// Can't use p.getName() as it contains spaces
				System.out.println();
			}
			System.out.println("--all");
			return 0;
		}

		IRI documentIRI = IRI.create(args[0]);
		if (!documentIRI.isAbsolute()) {
			// Assume it's a file
			documentIRI = IRI.create(new File(args[0]));
		}
		OWLOntology o = m.loadOntologyFromOntologyDocument(documentIRI);
		boolean verbose = false;

		Optional<OWLProfile> profile = Optional.empty();
		if (args.length > 1) {
			// Name of profile to match
		    String profileName = args[1];
			profile = owlProfilebyName(profileName);
			if (profile == null && !profileName.equals("--all")) {
				throw new IllegalArgumentException("Unknown profile: "
						+ profileName);
			}
			if (args.length > 2 && args[2].equals("--verbose")) {
				verbose = true;
			}
		} else {
			profile = Optional.of(DEFAULT_PROFILE);
		}

		if (! profile.isPresent()) {
			// --all 
		    boolean anyFailed = false;
			for (Profiles p : Profiles.values()) {
				System.out.print(p.name() + ": ");
				OWLProfileReport report = p.checkOntology(o);
				if (report.isInProfile()) {
					System.out.println("OK");
				} else {
					System.out.println(report.getViolations().size()
							+ " violations");
					anyFailed = true;
				}
			}
			return anyFailed ? 1 : 0;
		} else {
			OWLProfileReport report = profile.get().checkOntology(o);
			for (OWLProfileViolation v : report.getViolations()) {
				System.err.println(v.toString());
			}
			if (!report.isInProfile()) {
				return 1;
			}
			return 0;
		}
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
}

