package de.unidue.ltl.recommender.core.devutil;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.codehaus.plexus.util.Base64;

public class CreateTrainFile
{

    public static void main(String[] args) throws Exception
    {
        String s = FileUtils.readFileToString(
                new File("src/test/resources/buildTrainRequestJson/casplain.txt"), "utf-8");

        byte[] casBase64 = Base64.encodeBase64(s.getBytes());
        byte[] typsystemBase64 = Base64.encodeBase64(FileUtils.readFileToString(
                new File("src/test/resources/buildTrainRequestJson/typesystem.xml"), "utf-8")
                .getBytes());

        String json = "{" + "\"cas\":\"" + new String(casBase64) + "\",\"typesystem\":\""
                + new String(typsystemBase64)
                + "\", \"layer\":\"webanno.custom.EDAktivitt\",\"target\":\"Typ\"}";

        FileUtils.writeStringToFile(new File("src/test/resources/jsonTrainRequest.txt"), json,
                "utf-8");

    }

}
