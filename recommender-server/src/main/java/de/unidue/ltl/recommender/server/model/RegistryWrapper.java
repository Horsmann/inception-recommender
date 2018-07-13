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

package de.unidue.ltl.recommender.server.model;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.unidue.ltl.recommender.register.Register;
import de.unidue.ltl.recommender.register.RegisterEntry;

@Component
public class RegistryWrapper
    implements ModelRepository
{
    private static final Logger logger = LoggerFactory.getLogger(RegistryWrapper.class.getName());

    Register register;

    @Value("${repositoryRoot}")
    File repositoryRoot;

    private void init()
    {
        if (register == null) {
            logger.info("Initializing with root directory located at [" + repositoryRoot + "]");
            register = new Register(repositoryRoot);
        }
    }

    @Override
    public Model getModel(String id)
    {
        init();
        RegisterEntry entry = register.getEntry(id);

        return new ModelWrapper(entry.getId(), entry.getTimeStamp(), entry.getModelLocation());
    }

    @Override
    public void addModel(Model m) throws Exception
    {
        init();
        
        if (exists(m.getId())) {
            register.registerEntry(m.getFileSystemLocation(), m.getId(), m.getTimestamp());
            return;
        }

        RegisterEntry entry = new RegisterEntry(m.getId(), m.getTimestamp(),
                m.getFileSystemLocation());
        register.addNewEntry(entry);
    }

    private boolean exists(String id)
    {
        init();
        return register.getEntryIds().contains(id);
    }

}
