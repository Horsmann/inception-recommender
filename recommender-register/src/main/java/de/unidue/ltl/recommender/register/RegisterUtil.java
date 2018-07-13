package de.unidue.ltl.recommender.register;

import java.io.File;

public class RegisterUtil
{
    public static  void nullCheck(Object o)
    {
        if (isNull(o)) {
            throw new NullPointerException("Variable is null");
        }
    }

    public static boolean isNull(Object o)
    {
        return o == null;
    }
    
    public static void createFileSystemLocation(File location)
    {
        if (!location.exists()) {
            boolean mkdirs = location.mkdirs();
            if (!mkdirs) {
                throw new InstantiationError("Specified location [" + location.getAbsolutePath()
                        + "] did not exist - failed to create folder");
            }
        }
    }
}
