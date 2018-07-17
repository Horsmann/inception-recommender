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
package de.unidue.ltl.recommender.server.tc.prediction;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.google.common.io.Files;

import de.unidue.ltl.recommender.core.predict.PredictionWithModel;
import de.unidue.ltl.recommender.server.InceptionRequest;

public class TcInceptionRecommenderPredictor
    implements Predictor
{
    PredictionWithModel pwm;
    File resultOut;

    public TcInceptionRecommenderPredictor() throws Exception
    {

        resultOut = Files.createTempDir();

        pwm = new PredictionWithModel(resultOut);
    }

    @Override
    public void predict(InceptionRequest req, File model) throws Exception
    {
        pwm.run(req.getCAS(), req.getTypesystem(), req.getLayer(), req.getTarget(), model);
    }

    @Override
    public List<String> getResults() throws Exception
    {
        File [] files = resultOut.listFiles(new FileFilter()
        {
            @Override
            public boolean accept(File pathname)
            {
                return pathname.getName().endsWith(".txt");
            }
        });
        
        List<String> casAsString = new ArrayList<>();
        
        for(File f : files) {
            casAsString.add(FileUtils.readFileToString(f, "utf8"));
        }
        
        return casAsString;
    }

}
