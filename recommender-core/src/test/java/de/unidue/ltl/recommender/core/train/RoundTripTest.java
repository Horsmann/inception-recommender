package de.unidue.ltl.recommender.core.train;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import de.unidue.ltl.recommender.core.predict.PredictionWithModel;

public class RoundTripTest
{
    String jcasBase64;
    String typesystemBase64;
    String annotationName;
    String annotationFieldName;

    @Rule
    public TemporaryFolder resultFolder = new TemporaryFolder();
    @Rule
    public TemporaryFolder modelLocation = new TemporaryFolder();
    
    @Before
    public void setup() throws Exception
    {
        modelLocation.create();
        resultFolder.create();
    }

    @After
    public void cleanUp() throws IOException
    {
        modelLocation.delete();
        resultFolder.delete();
    }

    @Test
    public void roundTrip() throws Exception
    {
        train();
        
        predict();
    }

    private void predict() throws Exception
    {
        initPredict();
        PredictionWithModel pwm = new PredictionWithModel(resultFolder.getRoot());
        pwm.run(jcasBase64, typesystemBase64, annotationName, annotationFieldName, modelLocation.getRoot());
        
        List<File> files = getFiles(resultFolder.getRoot());
        assertEquals(1, files.size());
        
        String content = FileUtils.readFileToString(files.get(0), "utf-8");
        assertTrue(content.startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?><xmi:XMI xmlns:xmi=\"http://www.omg.org/XMI\""));
    }

    private List<File> getFiles(File resultFolder)
    {
        File [] f = resultFolder.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.getName().endsWith(".txt");
            }
        });
        
        return new ArrayList<>(Arrays.asList(f));
    }

    private void initPredict() throws IOException
    {
        String json = FileUtils
                .readFileToString(new File("src/test/resources/jsonPredictionRequest.txt"), "utf-8");
        

        JsonElement parse = new JsonParser().parse(json);
        jcasBase64 = new String(parse.getAsJsonObject().get("cas").toString().getBytes());
        jcasBase64 = jcasBase64.substring(1, jcasBase64.length() - 1);

        typesystemBase64 = new String(
                parse.getAsJsonObject().get("typesystem").toString().getBytes());
        typesystemBase64 = typesystemBase64.substring(1, typesystemBase64.length() - 1);

        annotationName = new String(parse.getAsJsonObject().get("layer").toString().getBytes());
        annotationName = annotationName.substring(1, annotationName.length() - 1);

        annotationFieldName = new String(
                parse.getAsJsonObject().get("target").toString().getBytes());
        annotationFieldName = annotationFieldName.substring(1, annotationFieldName.length() - 1);        
    }

    private void train() throws Exception
    {
        initTrain();
        // Train Model
        TrainNewModel m = new TrainNewModel();
        m.run(jcasBase64, typesystemBase64, annotationName, annotationFieldName, modelLocation.getRoot());
        assertTrue(modelLocation.getRoot().exists());
        File theModel = new File(modelLocation.getRoot(), "classifier.ser");
        assertTrue(theModel.exists());        
    }

    private void initTrain() throws IOException
    {
        String json = FileUtils
                .readFileToString(new File("src/test/resources/jsonTrainRequest.txt"), "utf-8");
        

        JsonElement parse = new JsonParser().parse(json);
        jcasBase64 = new String(parse.getAsJsonObject().get("cas").toString().getBytes());
        jcasBase64 = jcasBase64.substring(1, jcasBase64.length() - 1);

        typesystemBase64 = new String(
                parse.getAsJsonObject().get("typesystem").toString().getBytes());
        typesystemBase64 = typesystemBase64.substring(1, typesystemBase64.length() - 1);

        annotationName = new String(parse.getAsJsonObject().get("layer").toString().getBytes());
        annotationName = annotationName.substring(1, annotationName.length() - 1);

        annotationFieldName = new String(
                parse.getAsJsonObject().get("target").toString().getBytes());
        annotationFieldName = annotationFieldName.substring(1, annotationFieldName.length() - 1);        
    }
}
