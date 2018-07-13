package de.unidue.ltl.recommender.server.train;

import java.io.File;

import org.apache.commons.io.FileUtils;

import de.unidue.ltl.recommender.core.train.TrainModel;
import de.unidue.ltl.recommender.server.InceptionRequest;
import de.unidue.ltl.recommender.server.model.Model;
import de.unidue.ltl.recommender.server.model.ModelWrapper;

public class InceptionTrainer implements Trainer
{

    @Override
    public Model train(InceptionRequest req) throws Exception
    {
        String cas = req.getCAS();
        String typesystem = req.getTypesystem();
        String layer = req.getLayer();
        String target = req.getTarget();
        File modelLocation = new File(FileUtils.getTempDirectory(), "tc-model" + layer);

        TrainModel model = new TrainModel();
        model.run(cas, typesystem, layer, target, modelLocation);
        
        return new ModelWrapper("ABC", System.currentTimeMillis(), modelLocation);
    }

}
