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
