package de.unidue.ltl.recommender.server.model;

import java.io.File;

public class ModelWrapper
    implements Model
{
    private String id;
    private long timestamp;
    private File modelLocation;

    public ModelWrapper(String id, long timestamp, File modelLocation)
    {
        this.id = id;
        this.timestamp = timestamp;
        this.modelLocation = modelLocation;
    }

    @Override
    public String getId()
    {
        return id;
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public File getFileSystemLocation()
    {
        return modelLocation;
    }

}
