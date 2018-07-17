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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
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
        String json = FileUtils.readFileToString(new File("src/test/resources/jsonTrainRequest.txt"),
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
    
    @After
    public void cleanUp() throws IOException {
        FileUtils.deleteDirectory(modelLocation);
    }

    @Test
    public void runTrain() throws Exception
    {
        TrainNewModel m = new TrainNewModel();
        m.run(jcasBase64, typesystemBase64, annotationName, annotationFieldName, modelLocation);
        
        assertTrue(modelLocation.exists());
        assertTrue(new File(modelLocation, "classifier.ser").exists());
    }

}
