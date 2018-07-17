/*******************************************************************************
 * Copyright 2018
 * Language Technology Lab
 * University of Duisburg-Essen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.unidue.ltl.recommender.core.predict;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.tc.api.type.TextClassificationOutcome;

import de.tudarmstadt.ukp.dkpro.core.api.featurepath.FeaturePathUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.unidue.ltl.recommender.core.util.CoreUtil;

public class ResultWriterAnnotator
    extends JCasAnnotator_ImplBase
{
    public static final String PARAM_ANNOTATION_TARGET_NAME = "annotationName";
    @ConfigurationParameter(name = PARAM_ANNOTATION_TARGET_NAME, mandatory = true)
    private String annotation;

    public static final String PARAM_ANNOTATION_TARGET_FIELD_NAME = "annotationFieldName";
    @ConfigurationParameter(name = PARAM_ANNOTATION_TARGET_FIELD_NAME, mandatory = true)
    private String annoValue;

    public static final String PARAM_OUTPUT_FOLDER = "outputFolder";
    @ConfigurationParameter(name = PARAM_OUTPUT_FOLDER, mandatory = true)
    private File outputFolder;

    public static final String PARAM_DEBUG_SYS_OUT = "debugSysOut";
    @ConfigurationParameter(name = PARAM_DEBUG_SYS_OUT, mandatory = false, defaultValue = "false")
    private boolean debug;

    @Override
    public void initialize(final UimaContext context) throws ResourceInitializationException
    {
        super.initialize(context);
    }

    int casCounter = 0;

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException
    {
        List<Sentence> sentences = new ArrayList<Sentence>(JCasUtil.select(aJCas, Sentence.class));

        for (Sentence s : sentences) {
            List<TextClassificationOutcome> outcomes = JCasUtil.selectCovered(aJCas,
                    TextClassificationOutcome.class, s);

            for (int j = 0; j < outcomes.size(); j++) {
                Type annotationType = CasUtil.getAnnotationType(aJCas.getCas(), annotation);
                AnnotationFS targetAnno = aJCas.getCas().createAnnotation(annotationType,
                        outcomes.get(j).getBegin(), outcomes.get(j).getEnd());
                Feature featureByBaseName = FeaturePathUtils
                        .getType(aJCas.getTypeSystem(), annotation).getFeatureByBaseName(annoValue);
                targetAnno.setFeatureValueFromString(featureByBaseName,
                        outcomes.get(j).getOutcome());
                ((Annotation) targetAnno).addToIndexes();

                outcomes.get(j).removeFromIndexes();
            }
        }

        debugSysOut(aJCas);

        try {
            serializeCas(aJCas, casCounter++);
        }
        catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

    private void debugSysOut(JCas aJCas)
    {
        if (!debug) {
            return;
        }

        Type annotationType = CasUtil.getAnnotationType(aJCas.getCas(), annotation);

        Collection<AnnotationFS> select = CasUtil.select(aJCas.getCas(), annotationType);
        for (AnnotationFS s : select) {
            Feature featureByBaseName = FeaturePathUtils.getType(aJCas.getTypeSystem(), annotation)
                    .getFeatureByBaseName(annoValue);
            System.out.println(
                    s.getCoveredText() + " " + s.getFeatureValueAsString(featureByBaseName));
        }
    }

    private void serializeCas(JCas aJCas, int c) throws Exception
    {
        CoreUtil.serialize(aJCas, new File(outputFolder, "cas_" + c + ".txt"));
    }

    @Override
    public void collectionProcessComplete()
    {
    }

}