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

package de.unidue.ltl.recommender.register;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.unidue.ltl.recommender.register.Register;
import de.unidue.ltl.recommender.register.RegisterEntry;

public class RegisterTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    public TemporaryFolder modelDummyOne = new TemporaryFolder();
    public TemporaryFolder modelDummyTwo = new TemporaryFolder();

    Register storage;

    @Before
    public void setup() throws IOException
    {
        folder.create();
        modelDummyOne.create();
        modelDummyTwo.create();

        storage = new Register(folder.getRoot());
    }

    @Test
    public void storagePlainEntryTest() throws IOException, InterruptedException
    {
        storage.registerEntry(modelDummyOne.getRoot(), "ABC", 123456);
        List<String> modelIds = storage.getEntryIds();
        assertEquals(1, modelIds.size());

        RegisterEntry m = storage.getEntry("ABC");
        assertEquals("ABC", m.getId());
        assertEquals(123456, m.getTimeStamp());
    }

    @Test
    public void storageOverrideModelEntryTest() throws IOException, InterruptedException
    {
        storage.registerEntry(modelDummyOne.getRoot(), "ABC", 123456);
        RegisterEntry m = storage.getEntry("ABC");
        File modelOneLoc = m.getModelLocation();
        storage.registerEntry(modelDummyTwo.getRoot(), "ABC", 3333);
        File modelTwoLoc = m.getModelLocation();

        // location should have changed
        assertTrue(!modelOneLoc.getAbsolutePath().equals(modelTwoLoc.getAbsolutePath()));
    }

    @Test
    public void modelAccessTest() throws IOException, InterruptedException
    {

        storage.registerEntry(modelDummyOne.getRoot(), "ABC", 123456);
        RegisterEntry model = storage.getEntry("ABC");

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

        storage.registerEntry(modelDummyOne.getRoot(), "ABC", 123456);
        RegisterEntry model = storage.getEntry("ABC");
        File modelLocationBefore = model.getModelLocation();
        model.beginReadAccess();

        startLockReleasingThread(model);
        storage.registerEntry(modelDummyTwo.getRoot(), "ABC", 3333);

        File modelLocationAfter = model.getModelLocation();

        assertTrue(
                !modelLocationBefore.getAbsolutePath().equals(modelLocationAfter.getAbsolutePath()));

    }
    
    private void startLockReleasingThread(RegisterEntry model) throws InterruptedException
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
