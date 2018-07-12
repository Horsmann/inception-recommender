package de.unidue.ltl.recommender.core.predict;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.pear.util.FileUtil;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.TypeSystemUtil;
import org.dkpro.tc.ml.uima.TcAnnotator;

import com.google.common.io.Files;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasReader;
import de.unidue.ltl.recommender.core.util.InceptionJsonParser;
import de.unidue.ltl.recommender.core.util.InceptionUtil;

public class Prediction
{
    File typeSystemXML;
    File binCasInputFolder;

    public Prediction() throws Exception
    {
        typeSystemXML = FileUtil.createTempFile("typeSystemTmp", ".txt");
        binCasInputFolder = Files.createTempDir();
    }

    public static void main(String[] args) throws Exception
    {
        Prediction p = new Prediction();
//        p.run("src/main/resources/jsonInput.txt", "src/main/resources/predictionOut", System.getProperty("user.home")+"/Desktop/inception-model");
        
        p.run(args[0], args[1], args[2]);
    }

    public void run(String jsonFile, String predictionOutput, String model) throws Exception
    {

        InceptionJsonParser parser = new InceptionJsonParser(
                FileUtils.readFileToString(new File(jsonFile), "utf-8"));

        writeTypeSystemToFile(parser.getTypesystem());

        JCas jCas = InceptionUtil.deserialize(parser.getCAS(), typeSystemXML);

        TypeSystemDescription typeSystemDesc = TypeSystemUtil
                .typeSystem2TypeSystemDescription(jCas.getTypeSystem());
        InceptionUtil.writeCasBinary(jCas, typeSystemDesc, binCasInputFolder);

        predict(binCasInputFolder, new File(model), parser.getAnnotation(),
                parser.getAnnotationTargetField(), new File(predictionOutput));
        
        cleanUp();
    }

    private void cleanUp() throws IOException
    {
        FileUtils.deleteQuietly(typeSystemXML);
        FileUtils.deleteDirectory(binCasInputFolder);
    }

    private void writeTypeSystemToFile(String typeSystem) throws IOException
    {
        FileUtils.writeStringToFile(typeSystemXML, typeSystem, "utf-8");
    }

    private static void predict(File casPredictOutput, File model, String annotation, String type,
            File serializedCasOut)
        throws Exception
    {
        
        serializedCasOut.mkdirs();
        
        CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
                BinaryCasReader.class, BinaryCasReader.PARAM_MERGE_TYPE_SYSTEM, true,
                BinaryCasReader.PARAM_LANGUAGE, "en", BinaryCasReader.PARAM_SOURCE_LOCATION,
                casPredictOutput.getAbsoluteFile(), BinaryCasReader.PARAM_PATTERNS, "*.bin");

        AnalysisEngineDescription annotator = AnalysisEngineFactory.createEngineDescription(
                TcAnnotator.class, TcAnnotator.PARAM_NAME_SEQUENCE_ANNOTATION,
                Sentence.class.getName(), TcAnnotator.PARAM_NAME_UNIT_ANNOTATION,
                Token.class.getName(), TcAnnotator.PARAM_TC_MODEL_LOCATION, model,
                TcAnnotator.PARAM_RETAIN_TARGETS, false);

        AnalysisEngineDescription out = AnalysisEngineFactory.createEngineDescription(PredictionAnnotator.class,
                PredictionAnnotator.PARAM_ANNOTATION_TARGET, annotation, PredictionAnnotator.PARAM_ANNOTATION_TARGET_TYPE,
                type, PredictionAnnotator.PARAM_OUTPUT_FOLDER, serializedCasOut);

        SimplePipeline.runPipeline(reader, annotator, out);

    }
}
