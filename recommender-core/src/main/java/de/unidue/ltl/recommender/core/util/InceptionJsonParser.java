package de.unidue.ltl.recommender.core.util;

import org.codehaus.plexus.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class InceptionJsonParser
{
    JsonElement parse;

    public InceptionJsonParser(String json) throws Exception
    {
        parse = new JsonParser().parse(json);
    }

    public String getCAS()
    {
        return new String(
                Base64.decodeBase64(parse.getAsJsonObject().get("CAS").toString().getBytes()));

    }

    public String getTypesystem()
    {
        return new String(Base64
                .decodeBase64(parse.getAsJsonObject().get("Typesystem").toString().getBytes()));
    }

    public String getAnnotation()
    {
        String annotation = parse.getAsJsonObject().get("Layer").toString();
        annotation = annotation.substring(1, annotation.length() - 1);
        return annotation;
    }

    public String getAnnotationTargetField()
    {
        String featureType = new String(
                parse.getAsJsonObject().get("Target").toString().getBytes());
        featureType = featureType.substring(1, featureType.length() - 1);
        return featureType;
    }

}
