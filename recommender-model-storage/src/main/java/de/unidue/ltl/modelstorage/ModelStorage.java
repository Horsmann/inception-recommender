package de.unidue.ltl.modelstorage;

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

public class ModelStorage
{
    static final Logger logger = LoggerFactory.getLogger(ModelStorage.class.getName());
    File storeRootDirectory;
    Map<String, Model> modelMap = new HashMap<>();

    public ModelStorage(File storeRootDirectory)
    {
        this.storeRootDirectory = storeRootDirectory;
        ModelUtil.nullCheck(this.storeRootDirectory);
        ModelUtil.createFileSystemLocation(this.storeRootDirectory);
        logger.info(
                "Create [" + ModelStorage.class.getSimpleName() + "] with root folder located at ["
                        + this.storeRootDirectory.getAbsolutePath() + "]");
    }

    public void addModelToStore(File sourceLocation, String modelId, long timestamp)
        throws IOException, InterruptedException
    {
        ModelUtil.nullCheck(sourceLocation);
        ModelUtil.nullCheck(modelId);

        Model model = modelMap.get(modelId);
        if (ModelUtil.isNull(model)) {
            logger.debug("No model with id [" + modelId + "] found - will create a new model");
            File inStorageLocation = getDestinationInStorage(modelId, timestamp);
            FileUtils.moveDirectory(sourceLocation, inStorageLocation);

            model = new Model(modelId, timestamp, inStorageLocation);
            modelMap.put(modelId, model);
            return;
        }

        logger.debug("Existing model found with id [" + modelId + "] which will be updated");
        File inStorageLocation = getDestinationInStorage(modelId, timestamp);

        ensureStorageLocationDoesNotExist(inStorageLocation);
        FileUtils.moveDirectory(sourceLocation, inStorageLocation);
        model.setModel(inStorageLocation, timestamp);

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

    public List<String> getModelIds()
    {
        return new ArrayList<String>(modelMap.keySet());
    }

    public Model getModel(String id)
    {
        Model model = modelMap.get(id);
        return model;
    }

    public void dumpStorageToJson(File serializedModelStorage) throws IOException
    {
        ModelStorageJsonDeSerializer.serialize(this, serializedModelStorage);
    }

    public void loadStorageFromJson(File serializedModelStorage) throws JSONException, IOException
    {
        ModelStorageJsonDeSerializer.deserialize(serializedModelStorage);
    }

    void addModel(Model model)
    {
        Model m = modelMap.get(model.getId());
        if (!ModelUtil.isNull(m)) {
            throw new IllegalStateException(
                    "The model with id [" + model.getId() + "] exists already");
        }

        modelMap.put(model.getId(), model);
    }
}
