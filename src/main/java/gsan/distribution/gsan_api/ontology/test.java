package gsan.distribution.gsan_api.ontology;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class test {
public static void main(String[] args) throws UnsupportedEncodingException {
	OWLOntologyManager ontomanager = OWLManager.createOWLOntologyManager();
	OWLDataFactory  factory = ontomanager.getOWLDataFactory();
	IRI iriclinicalDrug = IRI.create(URLDecoder.decode("http://snomed.info/id/A", "UTF-8"));
	OWLClass rxnormcd=factory.getOWLClass(iriclinicalDrug);
	IRI iriMPSome = IRI.create(URLDecoder.decode("http://www./OntoRxNorm/RxIngredient_MP_Some", "UTF-8"));
	OWLClass rxnormMPSome=factory.getOWLClass(iriMPSome);

	String a="A";
	String b="B";
	String c="C";
	String d="D";
	String e="E";
	List<String> ddd= Arrays.asList(a,b,c,d,e);
	Stream sp=ddd.stream();
	sp.filter(x->!x.equals("A")).forEach(System.out::println);
	List<OWLClass> CLL=Arrays.asList(rxnormMPSome,rxnormcd);
	Stream<OWLClass> test=CLL.stream();
	Set<OWLClass> SNomedEqivalent=test.filter(x-> x.getIRI().toString().startsWith("http://snomed.info/id/")).collect(Collectors.toSet());
	System.out.println(SNomedEqivalent);
}
}
