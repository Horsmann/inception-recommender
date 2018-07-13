package de.unidue.ltl.recommender.server.model;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.unidue.ltl.register.Register;
import de.unidue.ltl.register.RegisterEntry;

@Component
public class RegistryWrapper implements ModelRepository
{
    Register register;
    
    @Value("${repositoryRoot}")
    File repositoryRoot;
    
    public RegistryWrapper() {
        register = new Register(repositoryRoot);
    }

    @Override
    public Model getModel(String id)
    {
        RegisterEntry entry = register.getEntry(id);
        
        return new ModelWrapper(entry.getId(), entry.getTimeStamp(), entry.getModelLocation());
    }

    @Override
    public void addModel(Model m) throws Exception
    {
        if(exists(m.getId())) {
            register.registerEntry(m.getFileSystemLocation(), m.getId(), m.getTimestamp());
            return;
        }
        
        RegisterEntry entry = new RegisterEntry(m.getId(), m.getTimestamp(), m.getFileSystemLocation());
        register.addNewEntry(entry);
    }
    
    private boolean exists(String id)
    {
        return register.getEntryIds().contains(id);
    }

}
