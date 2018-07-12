package de.unidue.ltl.recommender.core.train;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class TrainModelTest
{

    String jcasBase64;
    String typesystemBase64;
    String annotationName;
    String annotationFieldName;

    File modelLocation = Files.createTempDir();

    @Before
    public void setup() throws Exception
    {
        String json = FileUtils.readFileToString(new File("src/test/resources/jsonRequest.txt"),
                "utf-8");

        JsonElement parse = new JsonParser().parse(json);
        jcasBase64 = new String(parse.getAsJsonObject().get("cas").toString().getBytes());
        jcasBase64 = jcasBase64.substring(1, jcasBase64.length()-1);
        
        typesystemBase64 = new String(
                parse.getAsJsonObject().get("typesystem").toString().getBytes());
        typesystemBase64 = typesystemBase64.substring(1, typesystemBase64.length()-1);
        
        annotationName = new String(parse.getAsJsonObject().get("layer").toString().getBytes());
        annotationName = annotationName.substring(1, annotationName.length()-1);
        
        annotationFieldName = new String(
                parse.getAsJsonObject().get("target").toString().getBytes());
        annotationFieldName = annotationFieldName.substring(1, annotationFieldName.length()-1);

    }

    @Test
    public void runTrain() throws Exception
    {
        TrainModel m = new TrainModel();
        m.run(jcasBase64, typesystemBase64, annotationName, annotationFieldName, modelLocation);
        
        assertTrue(modelLocation.exists());
        assertTrue(new File(modelLocation, "classifier.ser").exists());
    }

}
