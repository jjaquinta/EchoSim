package jo.alexa.sim.data;

import java.util.HashMap;
import java.util.Map;

public class MatchBean
{
    private double                  mConfidence;
    private IntentBean              mIntent;
    private Map<SlotBean, String>   mValues;
    
    public MatchBean()
    {
        mValues = new HashMap<SlotBean, String>();
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof MatchBean)
        {
            MatchBean m2 = (MatchBean)obj;
            if (mIntent != m2.getIntent())
                return false;
            if (mConfidence != m2.getConfidence())
                return false;
            if (mValues.size() != m2.getValues().size())
                return false;
            for (SlotBean s1 : mValues.keySet())
            {
                if (!m2.getValues().containsKey(s1))
                    return false;
                String v2 = m2.getValues().get(s1);
                if (!mValues.get(s1).equals(v2))
                    return false;
            }
            return true;
        }
        return super.equals(obj);
    }
    
    @Override
    public String toString()
    {
        if (mIntent == null)
            return "";
        StringBuffer sb = new StringBuffer();
        for (SlotBean slot : mValues.keySet())
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(slot.getName()+"="+mValues.get(slot));
        }
        if (sb.length() > 0)
            sb.insert(0, " - ");
        sb.insert(0, mIntent.getIntent());
        return sb.toString();
    }
    
    public double getConfidence()
    {
        return mConfidence;
    }
    public void setConfidence(double confidence)
    {
        mConfidence = confidence;
    }
    public IntentBean getIntent()
    {
        return mIntent;
    }
    public void setIntent(IntentBean intent)
    {
        mIntent = intent;
    }
    public Map<SlotBean, String> getValues()
    {
        return mValues;
    }
    public void setValues(Map<SlotBean, String> values)
    {
        mValues = values;
    }
}
