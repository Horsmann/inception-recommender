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

package de.unidue.ltl.recommender.core.train;

import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.pear.util.FileUtil;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.TypeSystemUtil;
import org.codehaus.plexus.util.Base64;
import org.dkpro.tc.api.features.TcFeatureFactory;
import org.dkpro.tc.features.tcu.TargetSurfaceFormContextFeature;
import org.dkpro.tc.ml.builder.ExperimentBuilder;
import org.dkpro.tc.ml.builder.ExperimentType;
import org.dkpro.tc.ml.builder.FeatureMode;
import org.dkpro.tc.ml.builder.LearningMode;
import org.dkpro.tc.ml.builder.MLBackend;
import org.dkpro.tc.ml.crfsuite.CrfSuiteAdapter;

import com.google.common.io.Files;

import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import de.unidue.ltl.recommender.core.util.CoreUtil;

public class TrainModel
{
    private File typeSystemXML;
    private File binCasInputFolder;
    private File dkproHomeFallback;

    public TrainModel() throws Exception
    {
        typeSystemXML = FileUtil.createTempFile("typeSystemTmp", ".txt");
        binCasInputFolder = Files.createTempDir();
    }

    public void run(String casBase64, String typeSystemBase64, String annotationName,
            String annotationFieldName, File modelLocation)
        throws Exception
    {
        dkproHome();

        TypeSystemDescription typeSystem = prepare(casBase64, typeSystemBase64);

        startTraining(binCasInputFolder, typeSystem, modelLocation, annotationName,
                annotationFieldName);

        cleanUp();
    }

    private void dkproHome()
    {
        String property = System.getProperty("DKPRO_HOME");
        if (property == null || property.isEmpty()) {
            dkproHomeFallback = Files.createTempDir();
            System.setProperty("DKPRO_HOME", dkproHomeFallback.getAbsolutePath());
        }
    }

    private TypeSystemDescription prepare(String casBase64, String typeSystemBase64)
        throws Exception
    {
        writeTypeSystemToFile(decodeBase64(typeSystemBase64));
        JCas jCas = CoreUtil.deserialize(decodeBase64(casBase64), typeSystemXML);

        TypeSystemDescription typeSystemDesc = TypeSystemUtil
                .typeSystem2TypeSystemDescription(jCas.getTypeSystem());
        CoreUtil.writeCasBinary(jCas, typeSystemDesc, binCasInputFolder);

        return typeSystemDesc;
    }

    private String decodeBase64(String v)
    {
        return new String(Base64.decodeBase64(v.getBytes()));
    }

    private void cleanUp() throws IOException
    {
        FileUtils.deleteQuietly(typeSystemXML);
        FileUtils.deleteDirectory(binCasInputFolder);
        if (dkproHomeFallback != null) {
            FileUtils.deleteDirectory(dkproHomeFallback);
        }
    }

    private void writeTypeSystemToFile(String typeSystem) throws IOException
    {

        FileUtils.writeStringToFile(typeSystemXML, typeSystem, "utf-8");
    }

    private static void startTraining(File casPredictOutput, TypeSystemDescription typeSystem,
            File model, String annotationName, String annotationFieldName)
        throws Exception
    {

        CollectionReaderDescription trainReader = createReaderDescription(BinaryCasReader.class,
                typeSystem, BinaryCasReader.PARAM_LANGUAGE, "de",
                BinaryCasReader.PARAM_SOURCE_LOCATION, casPredictOutput.getAbsolutePath(),
                BinaryCasReader.PARAM_PATTERNS, "*.bin");

        ExperimentBuilder builder = new ExperimentBuilder();
        builder.experiment(ExperimentType.SAVE_MODEL, "Inception")
                .dataReaderTrain(trainReader)
                .featureMode(FeatureMode.UNIT).learningMode(LearningMode.SINGLE_LABEL)
                .outputFolder(model.getAbsolutePath())
                .machineLearningBackend(new MLBackend(new CrfSuiteAdapter(),
                        CrfSuiteAdapter.ALGORITHM_ADAPTIVE_REGULARIZATION_OF_WEIGHT_VECTOR))
                .preprocessing(AnalysisEngineFactory.createEngineDescription(
                        TrainingOutcomeAnnotator.class,
                        TrainingOutcomeAnnotator.PARAM_ANNOTATION_TARGET_NAME, annotationName,
                        TrainingOutcomeAnnotator.PARAM_ANNOTATION_TARGET_FIELD_NAME,
                        annotationFieldName))
                .features(TcFeatureFactory.create(TargetSurfaceFormContextFeature.class,
                        TargetSurfaceFormContextFeature.PARAM_RELATIVE_TARGET_ANNOTATION_INDEX, -2),
                        TcFeatureFactory.create(TargetSurfaceFormContextFeature.class,
                                TargetSurfaceFormContextFeature.PARAM_RELATIVE_TARGET_ANNOTATION_INDEX,
                                -1),
                        TcFeatureFactory.create(TargetSurfaceFormContextFeature.class,
                                TargetSurfaceFormContextFeature.PARAM_RELATIVE_TARGET_ANNOTATION_INDEX,
                                0))
                .run();

    }
}
