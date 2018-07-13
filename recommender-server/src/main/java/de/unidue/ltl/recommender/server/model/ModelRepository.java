package de.unidue.ltl.recommender.server.model;

public interface ModelRepository
{
    Model getModel(String id);
    
    void addModel(Model m) throws Exception;

}
