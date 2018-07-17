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

package de.unidue.ltl.recommender.server.modelrep;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.unidue.ltl.recommender.register.Register;
import de.unidue.ltl.recommender.register.Entry;
import de.unidue.ltl.recommender.server.tc.train.TcModel;
import de.unidue.ltl.recommender.server.train.InceptionRecommenderModel;

@Component
public class ModelRepositoryImpl
    implements ModelRepository
{
    private static final Logger logger = LoggerFactory
            .getLogger(ModelRepositoryImpl.class.getName());

    Register register;

    @Value("${repositoryRoot}")
    File repositoryRoot;

    private void init()
    {
        if (register == null) {
            logger.info("Initializing with root directory located at [" + repositoryRoot + "]");
            register = new Register(repositoryRoot);
            register.screenFolderAndLoad();
        }
    }

    @Override
    public InceptionRecommenderModel getModel(String id)
    {
        init();
        logger.debug("Retrieve model with id [" + id + "]");
        Entry entry = register.getEntry(id);

        return new TcModel(entry.getId(), entry.getTimeStamp(),
                register.getFileSystemLocationOfEntry(entry.getId()));
    }

    @Override
    public void checkInModel(String id, long timestamp, File sourceLocation,
            boolean deleteSourceLocation)
        throws Exception
    {
        init();

        if (exists(id)) {
            logger.info("The model with [" + id + "] already exists - will update existing entry");
            register.updateEntry(id, timestamp, sourceLocation, deleteSourceLocation);
            return;
        }

        Entry entry = new Entry(id, timestamp);
        register.addEntry(entry, sourceLocation, deleteSourceLocation);
    }

    @Override
    public void checkInModel(InceptionRecommenderModel irm, boolean deleteSourceLocation)
        throws Exception
    {
        init();

        checkInModel(irm.getId(), irm.getTimestamp(), irm.getFileSystemLocation(),
                deleteSourceLocation);
    }

    private boolean exists(String id)
    {
        init();
        return register.getEntryIds().contains(id);
    }

}
