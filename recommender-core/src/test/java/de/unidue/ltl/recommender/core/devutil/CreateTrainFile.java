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
