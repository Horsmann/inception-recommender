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
package de.unidue.ltl.recommender.core;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.jcas.JCas;
import org.apache.uima.pear.util.FileUtil;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.apache.uima.util.TypeSystemUtil;
import org.codehaus.plexus.util.Base64;

import com.google.common.io.Files;

import de.unidue.ltl.recommender.core.util.CoreUtil;

public abstract class DKProTcSkeleton
{
    protected File typeSystemXML;
    protected File binCasInputFolder;
    protected File dkproHomeFallback;
    
    public abstract void run(String casBase64, String typeSystemBase64, String annotationName,
            String annotationFieldName, File targetFolder) throws Exception;

    public DKProTcSkeleton() throws Exception
    {
        typeSystemXML = FileUtil.createTempFile("typeSystemTmp", ".txt");
        binCasInputFolder = Files.createTempDir();
    }
    
    protected void dkproHome()
    {
        String property = System.getProperty("DKPRO_HOME");
        if (property == null || property.isEmpty()) {
            dkproHomeFallback = Files.createTempDir();
            System.setProperty("DKPRO_HOME", dkproHomeFallback.getAbsolutePath());
        }
    }

    protected TypeSystemDescription prepare(String casBase64, String typeSystemBase64)
        throws Exception
    {
        writeTypeSystemToFile(decodeBase64(typeSystemBase64));
        JCas jCas = CoreUtil.deserialize(decodeBase64(casBase64), typeSystemXML);

        TypeSystemDescription typeSystemDesc = TypeSystemUtil
                .typeSystem2TypeSystemDescription(jCas.getTypeSystem());
        CoreUtil.writeCasBinary(jCas, typeSystemDesc, binCasInputFolder);

        return typeSystemDesc;
    }

    protected String decodeBase64(String v)
    {
        return new String(Base64.decodeBase64(v.getBytes()));
    }

    protected void cleanUp() throws IOException
    {
        FileUtils.deleteQuietly(typeSystemXML);
        FileUtils.deleteDirectory(binCasInputFolder);
        if (dkproHomeFallback != null) {
            FileUtils.deleteDirectory(dkproHomeFallback);
        }
    }

    protected void writeTypeSystemToFile(String typeSystem) throws IOException
    {

        FileUtils.writeStringToFile(typeSystemXML, typeSystem, "utf-8");
    }
}
