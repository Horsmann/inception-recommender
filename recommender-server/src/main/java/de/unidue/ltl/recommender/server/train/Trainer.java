package de.unidue.ltl.recommender.server.train;

import de.unidue.ltl.recommender.server.InceptionRequest;
import de.unidue.ltl.recommender.server.model.Model;

public interface Trainer
{
    
    Model train(InceptionRequest req) throws Exception;

}
