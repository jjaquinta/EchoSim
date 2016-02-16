package jo.alexa.sim.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IntentSchemaBean
{
    private List<IntentBean>    mIntents;
    private Set<String>         mCustomSlots;
    
    public IntentSchemaBean()
    {
        mIntents = new ArrayList<IntentBean>();
        mCustomSlots = new HashSet<>();
    }

    public List<IntentBean> getIntents()
    {
        return mIntents;
    }

    public void setIntents(List<IntentBean> intents)
    {
        mIntents = intents;
    }

    public Set<String> getCustomSlots()
    {
        return mCustomSlots;
    }

    public void setCustomSlots(Set<String> customSlots)
    {
        mCustomSlots = customSlots;
    }
}
