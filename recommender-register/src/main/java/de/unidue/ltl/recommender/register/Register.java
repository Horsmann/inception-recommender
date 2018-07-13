package de.unidue.ltl.recommender.register;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Register
{
    private static final Logger logger = LoggerFactory.getLogger(Register.class.getName());
    File storeRootDirectory;
    Map<String, RegisterEntry> registerMap = new HashMap<>();

    public Register(File storeRootDirectory)
    {
        this.storeRootDirectory = storeRootDirectory;
        RegisterUtil.nullCheck(this.storeRootDirectory);
        RegisterUtil.createFileSystemLocation(this.storeRootDirectory);
        logger.info("Create [" + Register.class.getSimpleName() + "] with root folder located at ["
                + this.storeRootDirectory.getAbsolutePath() + "]");
    }

    public void registerEntry(File sourceLocation, String modelId, long timestamp)
        throws IOException, InterruptedException
    {
        RegisterUtil.nullCheck(sourceLocation);
        RegisterUtil.nullCheck(modelId);

        RegisterEntry model = registerMap.get(modelId);
        if (RegisterUtil.isNull(model)) {
            logger.debug("No model with id [" + modelId + "] found - will create a new model");
            File inStorageLocation = getDestinationInStorage(modelId, timestamp);
            FileUtils.moveDirectory(sourceLocation, inStorageLocation);

            model = new RegisterEntry(modelId, timestamp, inStorageLocation);
            registerMap.put(modelId, model);
            return;
        }

        logger.debug("Existing model found with id [" + modelId + "] which will be updated");
        File inStorageLocation = getDestinationInStorage(modelId, timestamp);

        ensureStorageLocationDoesNotExist(inStorageLocation);
        FileUtils.moveDirectory(sourceLocation, inStorageLocation);
        model.updateModel(inStorageLocation, timestamp);

    }

    private void ensureStorageLocationDoesNotExist(File inStorageLocation)
    {
        if (inStorageLocation.exists()) {
            throw new IllegalStateException("A folder at location ["
                    + inStorageLocation.getAbsolutePath() + "] already exists");
        }
    }

    private File getDestinationInStorage(String id, long timeStamp)
    {
        return new File(storeRootDirectory, id + "_" + timeStamp);
    }

    public List<String> getEntryIds()
    {
        return new ArrayList<String>(registerMap.keySet());
    }

    public RegisterEntry getEntry(String id)
    {
        RegisterEntry model = registerMap.get(id);
        return model;
    }

    public void dumpStorageToJson(File serializationTarget) throws IOException
    {
        RegisterJsonDeSerializer.serialize(this, serializationTarget);
    }

    public void loadStorageFromJson(File deserializsationSource) throws JSONException, IOException
    {
        RegisterJsonDeSerializer.deserialize(deserializsationSource);
    }

    public void addNewEntry(RegisterEntry entry)
    {
        logger.info("Register new entry with id/timestemps [" + entry.getId() + "/"
                + entry.getTimeStamp() + "]");
        RegisterEntry e = registerMap.get(entry.getId());
        if (!RegisterUtil.isNull(e)) {
            throw new IllegalStateException(
                    "The entry with id [" + entry.getId() + "] exists already");
        }

        registerMap.put(entry.getId(), entry);
    }
}
