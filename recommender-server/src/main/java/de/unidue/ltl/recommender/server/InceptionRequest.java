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

package de.unidue.ltl.recommender.server;

public class InceptionRequest
{

    public String getCAS()
    {
        return cas;
    }


    public void setCAS(String CAS)
    {
        this.cas = CAS;
    }


    public String getTypesystem()
    {
        return typesystem;
    }


    public void setTypesystem(String Typesystem)
    {
        this.typesystem = Typesystem;
    }


    public String getLayer()
    {
        return layer;
    }


    public void setLayer(String Layer)
    {
        this.layer = Layer;
    }


    public String getTarget()
    {
        return target;
    }


    public void setTarget(String Target)
    {
        this.target = Target;
    }

    String cas;
    String typesystem;
    String layer;
    String target;

    public InceptionRequest() {};
  
}
