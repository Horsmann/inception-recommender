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

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import de.unidue.ltl.recommender.register.Register;
import de.unidue.ltl.recommender.register.RegisterJsonDeSerializer;

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
        storage.registerEntry(modelDummyOne.getRoot(), "ABC", 123);
        storage.registerEntry(modelDummyTwo.getRoot(), "DFS-ABC", 324123);
        storage.dumpStorageToJson(serializedFile);
        
        String json = FileUtils.readFileToString(serializedFile, "utf-8");
        assertTrue(!json.isEmpty());

        Register deserialize = RegisterJsonDeSerializer.deserialize(serializedFile);
        assertEquals(storage.getEntryIds().size(), deserialize.getEntryIds().size());
        
        for(String key : deserialize.getEntryIds()) {
            assertEquals(storage.getEntry(key).toString(), deserialize.getEntry(key).toString());
        }
        
        
        FileUtils.deleteQuietly(serializedFile);
    }

 
}
