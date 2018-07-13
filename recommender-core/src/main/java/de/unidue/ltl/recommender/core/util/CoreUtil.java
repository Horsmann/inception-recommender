package de.unidue.ltl.recommender.core.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;

import de.tudarmstadt.ukp.dkpro.core.io.bincas.BinaryCasWriter;

public class CoreUtil
{
    
    public static void serialize(JCas jCas, File name) throws Exception  {
        
        FileOutputStream fos = new FileOutputStream(name);
        XmiCasSerializer.serialize(jCas.getCas(), jCas.getTypeSystem(), fos);
        fos.close();
    }

    public static JCas deserialize(String casString, File typeSystemXML) throws Exception
    {
        JCas jcas = JCasFactory.createJCasFromPath(typeSystemXML.getAbsolutePath());
        InputStream bais = new ByteArrayInputStream(casString.getBytes());
        XmiCasDeserializer.deserialize(bais, jcas.getCas());
        bais.close();
        
        return jcas;
    }
    
    public static void writeCasBinary(JCas jcas, TypeSystemDescription typeSystemDesc, File casFolder) throws ResourceInitializationException, AnalysisEngineProcessException
    {
        
        AnalysisEngine xmiWriter = AnalysisEngineFactory.createEngine(
                BinaryCasWriter.class,
                typeSystemDesc,
                BinaryCasWriter.PARAM_TARGET_LOCATION,
                casFolder.toString(),
                BinaryCasWriter.PARAM_FORMAT, "6+", 
                BinaryCasWriter.PARAM_OVERWRITE, true);
        
        xmiWriter.process(jcas);
        xmiWriter.collectionProcessComplete();
    }

}
