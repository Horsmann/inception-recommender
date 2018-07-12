/**
 * Copyright 2018
 * Ubiquitous Knowledge Processing (UKP) Lab
 * Technische Universität Darmstadt
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses/.
 */
package de.unidue.ltl.recommender.core.train;

import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.tc.api.type.TextClassificationOutcome;
import org.dkpro.tc.api.type.TextClassificationTarget;

import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathUtils;

public class OutcomeAnnotator
    extends JCasAnnotator_ImplBase
{
    public static final String PARAM_ANNOTATION_TARGET = "inputAnnotation";
    @ConfigurationParameter(name = PARAM_ANNOTATION_TARGET, mandatory = true)
    private String annotation;
    
    public static final String PARAM_ANNOTATION_TARGET_TYPE = "inputAnnotationType";
    @ConfigurationParameter(name = PARAM_ANNOTATION_TARGET_TYPE, mandatory = true)
    private String annoValue;
    
    int tcId = 0;
    
    Type annotationType=null;
    Feature feature=null;
    
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException
    {
        if (annotationType == null) {
            annotationType = CasUtil.getAnnotationType(aJCas.getCas(), annotation);
            feature = FeaturePathUtils.getType(aJCas.getTypeSystem(), annotation)
                    .getFeatureByBaseName(annoValue);
            //
            // Collection<AnnotationFS> select = CasUtil.select(jcas.getCas(), annotationType);
            // for(AnnotationFS afs : select) {
            // System.out.println(afs.getFeatureValueAsString(feature) + "| " + afs.getBegin() + "/"
            // + afs.getEnd());
            // }
        }
        
        List<AnnotationFS> select = new ArrayList<AnnotationFS>(CasUtil.select(aJCas.getCas(), annotationType));
        for(AnnotationFS a : select) {
            
            TextClassificationTarget aTarget = new TextClassificationTarget(aJCas, a.getBegin(),
                    a.getEnd());
            aTarget.setId(tcId++);
            aTarget.addToIndexes();
            
            TextClassificationOutcome outcome = new TextClassificationOutcome(aJCas,
                    a.getBegin(), a.getEnd());
            outcome.setOutcome(a.getFeatureValueAsString(feature));
            outcome.addToIndexes();
            
        }
    }

}
