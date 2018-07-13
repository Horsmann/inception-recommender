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

package de.unidue.ltl.recommender.server.train;

import java.io.File;

import org.apache.commons.io.FileUtils;

import de.unidue.ltl.recommender.core.train.TrainModel;
import de.unidue.ltl.recommender.server.InceptionRequest;
import de.unidue.ltl.recommender.server.model.Model;
import de.unidue.ltl.recommender.server.model.ModelWrapper;

public class InceptionTrainer
    implements Trainer
{

    @Override
    public Model train(InceptionRequest req) throws Exception
    {
        String cas = req.getCAS();
        String typesystem = req.getTypesystem();
        String layer = req.getLayer();
        String target = req.getTarget();

        long timestamps = System.currentTimeMillis();
        File modelLocation = new File(FileUtils.getTempDirectory(), layer + "_" + timestamps);

        TrainModel model = new TrainModel();
        model.run(cas, typesystem, layer, target, modelLocation);

        return new ModelWrapper(layer, timestamps, modelLocation);
    }

}
