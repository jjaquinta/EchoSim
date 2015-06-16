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
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        for (SlotBean slot : mValues.keySet())
        {
            if (sb.length() > 0)
                sb.append(", ");
            sb.append(mValues.get(slot));
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
