package jo.alexa.sim.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationBean
{
    // primary values
    private String              mEndpoint;
    private IntentSchemaBean    mSchema;
    private List<UtteranceBean> mUtterances;
    // derived values
    private Map<String, IntentBean> mIntentIndex;
    private Map<String, SlotBean>   mSlotIndex;
    
    public ApplicationBean()
    {
        mSlotIndex = new HashMap<String, SlotBean>();
        mIntentIndex = new HashMap<String, IntentBean>();
        mUtterances = new ArrayList<UtteranceBean>();
    }
    
    public String getEndpoint()
    {
        return mEndpoint;
    }
    public void setEndpoint(String endpoint)
    {
        mEndpoint = endpoint;
    }
    public IntentSchemaBean getSchema()
    {
        return mSchema;
    }
    public void setSchema(IntentSchemaBean schema)
    {
        mSchema = schema;
    }
    public List<UtteranceBean> getUtterances()
    {
        return mUtterances;
    }
    public void setUtterances(List<UtteranceBean> utterances)
    {
        mUtterances = utterances;
    }
    public Map<String, SlotBean> getSlotIndex()
    {
        return mSlotIndex;
    }
    public void setSlotIndex(Map<String, SlotBean> slotIndex)
    {
        mSlotIndex = slotIndex;
    }

    public Map<String, IntentBean> getIntentIndex()
    {
        return mIntentIndex;
    }

    public void setIntentIndex(Map<String, IntentBean> intentIndex)
    {
        mIntentIndex = intentIndex;
    }
    
}
