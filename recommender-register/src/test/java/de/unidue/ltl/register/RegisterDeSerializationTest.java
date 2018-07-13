package de.unidue.ltl.register;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.unidue.ltl.register.Register;
import de.unidue.ltl.register.RegisterJsonDeSerializer;

public class RegisterDeSerializationTest
{
    @Rule
    public TemporaryFolder root = new TemporaryFolder();
    public TemporaryFolder modelDummyOne = new TemporaryFolder();
    public TemporaryFolder modelDummyTwo = new TemporaryFolder();
    File serializedFile; 
            
    Register storage;

    @Before
    public void setup() throws IOException
    {
        root.create();
        modelDummyOne.create();
        modelDummyTwo.create();

        storage = new Register(root.getRoot());
    }

    @Test
    public void deSerialization() throws IOException, InterruptedException
    {
        serializedFile = File.createTempFile("serializedStorage", ".json");
        Register storage = new Register(root.getRoot());
        storage.addModelToStore(modelDummyOne.getRoot(), "ABC", 123);
        storage.addModelToStore(modelDummyTwo.getRoot(), "DFS-ABC", 324123);
        storage.dumpStorageToJson(serializedFile);
        
        String json = FileUtils.readFileToString(serializedFile, "utf-8");
        assertTrue(!json.isEmpty());

        Register deserialize = RegisterJsonDeSerializer.deserialize(serializedFile);
        assertEquals(storage.getModelIds().size(), deserialize.getModelIds().size());
        
        for(String key : deserialize.getModelIds()) {
            assertEquals(storage.getModel(key).toString(), deserialize.getModel(key).toString());
        }
        
        
        FileUtils.deleteQuietly(serializedFile);
    }

 
}
