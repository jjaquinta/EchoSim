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
    private String              mSessionID;
    private String              mApplicationID;
    private String              mUserID;
    private Map<String,Object>  mAttributes;
    
    public ApplicationBean()
    {
        mSlotIndex = new HashMap<String, SlotBean>();
        mIntentIndex = new HashMap<String, IntentBean>();
        mUtterances = new ArrayList<UtteranceBean>();
        mSessionID = null;
        mApplicationID = "yourFunkyApplication";
        mUserID = System.getProperty("user.name");
        mAttributes = new HashMap<String,Object>();
        mAttributes.put("simulation", Boolean.TRUE);
        mAttributes.put("simulator", "TsaTsaTzu EchoSim");
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

    public String getSessionID()
    {
        return mSessionID;
    }

    public void setSessionID(String sessionID)
    {
        mSessionID = sessionID;
    }

    public String getApplicationID()
    {
        return mApplicationID;
    }

    public void setApplicationID(String applicationID)
    {
        mApplicationID = applicationID;
    }

    public String getUserID()
    {
        return mUserID;
    }

    public void setUserID(String userID)
    {
        mUserID = userID;
    }

    public Map<String, Object> getAttributes()
    {
        return mAttributes;
    }

    public void setAttributes(Map<String, Object> attributes)
    {
        mAttributes = attributes;
    }
}
