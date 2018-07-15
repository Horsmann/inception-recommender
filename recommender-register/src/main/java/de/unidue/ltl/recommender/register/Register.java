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

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Register
{
    private static final Logger logger = LoggerFactory.getLogger(Register.class.getName());
    private static final String TIMESTAMP_SEP = "_";
    File rootFolder;
    Map<String, Entry> registerMap = new HashMap<>();

    public Register(File storeRootDirectory)
    {
        this.rootFolder = storeRootDirectory;
        RegisterUtil.nullCheck(this.rootFolder);
        RegisterUtil.createFileSystemLocation(this.rootFolder);
        logger.info("Create [" + Register.class.getSimpleName() + "] with root folder located at ["
                + this.rootFolder.getAbsolutePath() + "]");
    }

    public void addEntry(Entry entry, File sourceLocation, boolean deleteSource) throws IOException
    {
        registerMap.put(entry.getId(), entry);
        if (deleteSource) {
            FileUtils.moveDirectory(sourceLocation, entry.getModelLocation());
        }
    }

    public void updateEntry(String modelId, long timestamp, File updatedModelExternalLocation,
            boolean deleteSource)
        throws IOException, InterruptedException
    {
        RegisterUtil.nullCheck(updatedModelExternalLocation);

        Entry entry = registerMap.get(modelId);
        if (RegisterUtil.isNull(entry)) {
            logger.debug("No model with id [" + modelId + "] found - will create a new model");
            entry = new Entry(rootFolder, modelId, timestamp);
            File fileSystemPath = entry.getFileSystemPath();
            FileUtils.moveDirectory(updatedModelExternalLocation, fileSystemPath);
            registerMap.put(modelId, entry);
            return;
        }

        logger.debug("Existing model found (id: [" + entry.toString() + "])");
        File pathToOldVersion = entry.getFileSystemPath();

        entry.timestamp = timestamp;
        File pathToInternalLocation = entry.getFileSystemPath();
        FileUtils.moveDirectory(updatedModelExternalLocation, pathToInternalLocation);

        FileUtils.deleteDirectory(pathToOldVersion);
        logger.info("Deleted old version [" + pathToOldVersion.getAbsolutePath() + "]");

        if (deleteSource) {
            logger.info("Deleted source version of new model originally located at ["
                    + updatedModelExternalLocation.getAbsolutePath() + "]");
            FileUtils.deleteDirectory(updatedModelExternalLocation);
        }

    }

    public List<String> getEntryIds()
    {
        return new ArrayList<String>(registerMap.keySet());
    }

    public Entry getEntry(String id)
    {
        Entry model = registerMap.get(id);
        return model;
    }

    void restoreSerializedEntry(Entry entry)
    {
        registerMap.put(entry.getId(), entry);
    }

    public void screenFolderAndLoad()
    {
        registerMap.clear();

        File[] files = rootFolder.listFiles(new FileFilter()
        {

            @Override
            public boolean accept(File pathname)
            {
                return pathname.isDirectory();
            }
        });

        StringBuilder sb = new StringBuilder();
        for (File f : files) {
            sb.append(f.getAbsolutePath() + ",");
        }
        logger.info("Root folder [" + rootFolder.getAbsolutePath() + "] contains [" + sb.toString()
                + "] folders");

        for (File file : files) {
            String name = file.getName();
            int sepIdx = name.lastIndexOf(TIMESTAMP_SEP);
            String id = name.substring(0, sepIdx);
            long timestamp = Long.parseLong(name.substring(sepIdx+1));
            registerMap.put(id, new Entry(rootFolder, id, timestamp));

            logger.info("Loaded item with id: [" + id + "] named [" + file.getName()
                    + "] in root directory [" + rootFolder.getAbsolutePath() + "]");

        }
    }

}
