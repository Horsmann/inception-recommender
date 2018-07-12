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
