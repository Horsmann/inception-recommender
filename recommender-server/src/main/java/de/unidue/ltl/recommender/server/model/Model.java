package de.unidue.ltl.recommender.server.model;

import java.io.File;

public interface Model
{
    String getId();
    
    long getTimestamp();
    
    File getFileSystemLocation(); 

}
