package ru.mpei.brics.extention.knowledgeBase.jena;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.PrintUtil;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.knowledgeBase.KnowledgeBaseCommunicator;

import java.util.ArrayList;
import java.util.List;


public class JenaCommunicator extends KnowledgeBaseCommunicator {
    String kpURI;
    List<Rule> rules;
    Reasoner reasoner;

    public JenaCommunicator(String rulesetPath) {
        this.kpURI = "http://jena.hpl.hp.com/kp#";
        PrintUtil.registerPrefix("kp", kpURI);

        this.rules = Rule.rulesFromURL(rulesetPath);
        this.reasoner = new GenericRuleReasoner(this.rules);
    }

    @Override
    public double getFitnessValue(NetworkElementConfiguration cfg) {
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, ModelFactory.createDefaultModel());

        Resource fitnessModel = ontModel.createResource(kpURI + "fitnessModel");

        Property haveFrequency = ontModel.createObjectProperty(kpURI + "frequency");
        Property maxPower = ontModel.createObjectProperty(kpURI + "maxPower");
        Property minPower = ontModel.createObjectProperty(kpURI + "minPower");
        Property currentPower = ontModel.createObjectProperty(kpURI + "currentPower");
        Property fitnessVal = ontModel.createObjectProperty(kpURI + "fitness");

        fitnessModel.addProperty(haveFrequency, String.valueOf(cfg.getF()), XSDDatatype.XSDdouble);
        fitnessModel.addProperty(maxPower, String.valueOf(cfg.getMaxP()), XSDDatatype.XSDdouble);
        fitnessModel.addProperty(minPower, String.valueOf(cfg.getMinP()), XSDDatatype.XSDdouble);
        fitnessModel.addProperty(currentPower, String.valueOf(cfg.getCurrentP()), XSDDatatype.XSDdouble);

        InfModel infmodel = ModelFactory.createInfModel(reasoner, ontModel);

        StmtIterator iterator2 = infmodel.listStatements(fitnessModel, fitnessVal, (RDFNode)null);

        List<Double> fitneses = new ArrayList<>();
        while(iterator2.hasNext()) {
            System.out.println("find");
            Statement s = iterator2.nextStatement();
            System.out.println(s);
            fitneses.add(s.getDouble());
        }

        return fitneses.stream().min(Double::compareTo).get();
    }

    @Override
    public boolean getAllowSignal(NetworkElementConfiguration cfg) {
        OntModel ontModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, ModelFactory.createDefaultModel());

        Resource allowModel = ontModel.createResource(kpURI + "allowModel");

        Property haveFrequency = ontModel.createObjectProperty(kpURI + "frequency");
        Property maxPower = ontModel.createObjectProperty(kpURI + "maxPower");
        Property minPower = ontModel.createObjectProperty(kpURI + "minPower");
        Property currentPower = ontModel.createObjectProperty(kpURI + "currentPower");
        Property allow = ontModel.createObjectProperty(kpURI + "allow");

        allowModel.addProperty(haveFrequency, String.valueOf(cfg.getF()), XSDDatatype.XSDdouble);
        allowModel.addProperty(maxPower, String.valueOf(cfg.getMaxP()), XSDDatatype.XSDdouble);
        allowModel.addProperty(minPower, String.valueOf(cfg.getMinP()), XSDDatatype.XSDdouble);
        allowModel.addProperty(currentPower, String.valueOf(cfg.getCurrentP()), XSDDatatype.XSDdouble);

        InfModel infmodel = ModelFactory.createInfModel(reasoner, ontModel);

        StmtIterator iterator2 = infmodel.listStatements(allowModel, allow, (RDFNode)null);

        List<Boolean> inferences = new ArrayList<>();
        while(iterator2.hasNext()) {
            boolean allowResult = iterator2.nextStatement().toString().contains("true");
            inferences.add(allowResult);
        }

        return !inferences.contains(false);
    }
}
