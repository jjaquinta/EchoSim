package jo.alexa.sim.data;

public class SlotBean
{
    private String  mName;
    private String  mType;
    
    @Override
    public String toString()
    {
        return mName+" ("+mType+")";
    }
    
    public String getName()
    {
        return mName;
    }
    public void setName(String name)
    {
        mName = name;
    }
    public String getType()
    {
        return mType;
    }
    public void setType(String type)
    {
        mType = type;
    }
}
