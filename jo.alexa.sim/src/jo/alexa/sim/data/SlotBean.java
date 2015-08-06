package jo.alexa.sim.data;

import java.util.HashSet;
import java.util.Set;

public class SlotBean
{
    private String  mName;
    private String  mType;
    private Set<String> mValues;
    
    public SlotBean()
    {
        mValues = new HashSet<String>();
    }
    
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

    public Set<String> getValues()
    {
        return mValues;
    }

    public void setValues(Set<String> values)
    {
        mValues = values;
    }
}
