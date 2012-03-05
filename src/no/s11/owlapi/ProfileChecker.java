package no.s11.owlapi;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;
import org.semanticweb.owlapi.profiles.OWLProfile;
import org.semanticweb.owlapi.profiles.OWLProfileReport;
import org.semanticweb.owlapi.profiles.OWLProfileViolation;

public class ProfileChecker {

	public static void main(String[] args) throws OWLOntologyCreationException {

		OWLOntologyManager m = OWLManager.createOWLOntologyManager();
		File file = new File(args[0]);
		OWLOntology o = m
				.loadOntologyFromOntologyDocument(file);
		OWLProfile p = new OWL2RLProfile();
		OWLProfileReport report = p.checkOntology(o);

		for (OWLProfileViolation v : report.getViolations()) {
			System.out.println(v);
		}

	}

}
