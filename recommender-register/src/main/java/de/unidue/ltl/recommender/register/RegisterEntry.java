package de.unidue.ltl.recommender.register;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterEntry
{
    static final Logger logger = LoggerFactory.getLogger(RegisterEntry.class.getName());

    private long timestamp;
    private AtomicInteger modelAccesses = new AtomicInteger(0);
    private Semaphore modelUpdateOperation = new Semaphore(1, true);
    private String id;
    private File inStorageLocation;

    public RegisterEntry(String id, long timestamp, File inStorageLocation)
    {
        this.id = id;
        this.timestamp = timestamp;
        this.inStorageLocation = inStorageLocation;
    }

    public synchronized void updateModel(File newModel, long timestamp)
        throws InterruptedException, IOException
    {
        RegisterUtil.nullCheck(newModel);

        logger.debug("Update on model with id [" + this.id + "] current read accesses ["
                + modelAccesses.get() + "]");
        modelUpdateOperation.acquire();

        logger.debug("Replacing model [" + inStorageLocation.getAbsolutePath()
                + "] with new model [" + newModel.getAbsolutePath() + "]");
        this.inStorageLocation = newModel;
        this.timestamp = timestamp;

        modelUpdateOperation.release();
    }

    public synchronized void beginReadAccess() throws InterruptedException
    {

        logger.debug("Model update is taking place ["
                + (modelUpdateOperation.availablePermits() == 0 ? "true" : "false") + "]");
        if (modelUpdateOperation.tryAcquire()) {
            modelAccesses.incrementAndGet();
            logger.debug("Read access on model [" + id + "]- new access count ["
                    + modelAccesses.get() + "]");
            modelUpdateOperation.release();
        }
        else {
            logger.debug("Update of model [" + id + "] in progress");
            wait();
            beginReadAccess();
        }

    }

    public synchronized void endReadAccess()
    {
        modelAccesses.decrementAndGet();
        logger.debug("Decrease model access counter to [" + modelAccesses.get() + "]");
        notify();
    }

    public long getTimeStamp()
    {
        return timestamp;
    }

    public String getId()
    {
        return id;
    }

    public File getModelLocation()
    {
        return inStorageLocation;
    }

    Integer getNumberOfModelAccesses()
    {
        return modelAccesses.get();
    }

    public String toString()
    {
        return "[" + id + "] / [" + timestamp + "] / ["
                + inStorageLocation.getAbsolutePath() + "]";
    }
}
