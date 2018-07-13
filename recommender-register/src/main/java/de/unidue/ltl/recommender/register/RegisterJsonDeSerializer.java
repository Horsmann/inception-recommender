package de.unidue.ltl.recommender.register;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterJsonDeSerializer
{
    private static final Logger logger = LoggerFactory
            .getLogger(RegisterJsonDeSerializer.class.getName());
    private static final String ROOT_DIR = "rootDirectory";
    private static final String MODEL_ID = "modelId";
    private static final String MODEL_TIMESTAMP = "modelTimestamp";
    private static final String MODEL_SET = "setRegisteredModels";
    private static final String MODEL_LOCATION = "modelLocation";

    public static String createJsonFromStorage(Register modelStorage)
    {

        List<String> keys = new ArrayList<String>(modelStorage.registerMap.keySet());
        Collections.sort(keys);

        JSONObject dump = new JSONObject();
        dump.put(ROOT_DIR, modelStorage.storeRootDirectory.getAbsolutePath());
        JSONArray array = new JSONArray();
        for (String k : keys) {
            JSONObject item = new JSONObject();
            RegisterEntry model = modelStorage.registerMap.get(k);
            item.put(MODEL_ID, model.getId());
            item.put(MODEL_TIMESTAMP, model.getTimeStamp() + "");
            item.put(MODEL_LOCATION, model.getModelLocation().getAbsolutePath());
            array.put(item);
        }
        dump.put(MODEL_SET, array);
        return dump.toString();
    }

    public static void serialize(Register modelStorage, File dumpLocation) throws IOException
    {
        logger.info("Serializing instance of [" + Register.class.getName() + "] to JSON ["
                + dumpLocation.getAbsolutePath() + "]");
        FileUtils.writeStringToFile(dumpLocation, createJsonFromStorage(modelStorage), "utf-8");
    }

    public static Register deserialize(File serializedModelStorage)
        throws JSONException, IOException
    {
        logger.info("DeSerializing instance of [" + Register.class.getName() + "] from JSON file ["
                + serializedModelStorage.getAbsolutePath() + "]");
        JSONObject dump = new JSONObject(
                FileUtils.readFileToString(serializedModelStorage, "utf-8"));

        String rootDirectory = (String) dump.get(ROOT_DIR);
        Register storage = new Register(new File(rootDirectory));

        JSONArray jsonArray = dump.getJSONArray(MODEL_SET);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject modelEntry = jsonArray.getJSONObject(i);
            String id = (String) modelEntry.get(MODEL_ID);
            Long ts = Long.parseLong((String) modelEntry.get(MODEL_TIMESTAMP));
            File location = new File((String) modelEntry.get(MODEL_LOCATION));

            RegisterEntry model = new RegisterEntry(id, ts, location);

            storage.addNewEntry(model);
        }

        return storage;
    }

}
