package de.unidue.ltl.modelstorage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ModelStorageTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public TemporaryFolder modelDummyOne = new TemporaryFolder();
    public TemporaryFolder modelDummyTwo = new TemporaryFolder();

    ModelStorage storage;

    @Before
    public void setup() throws IOException
    {
        folder.create();
        modelDummyOne.create();
        modelDummyTwo.create();

        storage = new ModelStorage(folder.getRoot());
    }

    @Test
    public void storagePlainEntryTest() throws IOException, InterruptedException
    {
        storage.addModelToStore(modelDummyOne.getRoot(), "ABC", 123456);
        List<String> modelIds = storage.getModelIds();
        assertEquals(1, modelIds.size());

        Model m = storage.getModel("ABC");
        assertEquals("ABC", m.getId());
        assertEquals(123456, m.getTimeStamp());
    }

    @Test
    public void storageOverrideModelEntryTest() throws IOException, InterruptedException
    {
        storage.addModelToStore(modelDummyOne.getRoot(), "ABC", 123456);
        Model m = storage.getModel("ABC");
        File modelOneLoc = m.getModelLocation();
        storage.addModelToStore(modelDummyTwo.getRoot(), "ABC", 3333);
        File modelTwoLoc = m.getModelLocation();

        // location should have changed
        assertTrue(!modelOneLoc.getAbsolutePath().equals(modelTwoLoc.getAbsolutePath()));
    }

    @Test
    public void modelAccessTest() throws IOException, InterruptedException
    {

        storage.addModelToStore(modelDummyOne.getRoot(), "ABC", 123456);
        Model model = storage.getModel("ABC");

        assertEquals(0, model.getNumberOfModelAccesses().intValue());
        model.beginReadAccess();
        assertEquals(1, model.getNumberOfModelAccesses().intValue());
        model.beginReadAccess();
        assertEquals(2, model.getNumberOfModelAccesses().intValue());
        model.endReadAccess();
        model.endReadAccess();
        assertEquals(0, model.getNumberOfModelAccesses().intValue());
    }

    @Test
    public void updateModelWhileBeingAccessed() throws IOException, InterruptedException
    {

        storage.addModelToStore(modelDummyOne.getRoot(), "ABC", 123456);
        Model model = storage.getModel("ABC");
        File modelLocationBefore = model.getModelLocation();
        model.beginReadAccess();

        startLockReleasingThread(model);
        storage.addModelToStore(modelDummyTwo.getRoot(), "ABC", 3333);

        File modelLocationAfter = model.getModelLocation();

        assertTrue(
                !modelLocationBefore.getAbsolutePath().equals(modelLocationAfter.getAbsolutePath()));

    }
    
    private void startLockReleasingThread(Model model) throws InterruptedException
    {
        Runnable r = new Runnable()
        {

            @Override
            public void run()
            {
                try {
                    Thread.sleep(100);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                model.endReadAccess();
            }
        };

        Thread t = new Thread(r);
        t.start();
    }
}
