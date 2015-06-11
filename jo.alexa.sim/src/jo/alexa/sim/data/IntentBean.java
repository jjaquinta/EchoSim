package jo.alexa.sim.data;

import java.util.ArrayList;
import java.util.List;

public class IntentBean
{
    private String          mIntent;
    private List<SlotBean>  mSlots;
    
    public IntentBean()
    {
        mSlots = new ArrayList<SlotBean>();
    }
    
    @Override
    public String toString()
    {
        return mIntent;
    }
    
    public String getIntent()
    {
        return mIntent;
    }
    public void setIntent(String intent)
    {
        mIntent = intent;
    }
    public List<SlotBean> getSlots()
    {
        return mSlots;
    }
    public void setSlots(List<SlotBean> slots)
    {
        mSlots = slots;
    }
}
