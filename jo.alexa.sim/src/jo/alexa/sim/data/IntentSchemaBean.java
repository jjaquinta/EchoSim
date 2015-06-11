package jo.alexa.sim.data;

import java.util.ArrayList;
import java.util.List;

public class IntentSchemaBean
{
    private List<IntentBean>    mIntents;
    
    public IntentSchemaBean()
    {
        mIntents = new ArrayList<IntentBean>();
    }

    public List<IntentBean> getIntents()
    {
        return mIntents;
    }

    public void setIntents(List<IntentBean> intents)
    {
        mIntents = intents;
    }
}
