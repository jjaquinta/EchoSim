package jo.alexa.sim.data;

public class SlotSegmentBean extends PhraseSegmentBean
{
    private String      mText;
    private SlotBean    mSlot;
    
    public String getText()
    {
        return mText;
    }
    public void setText(String text)
    {
        mText = text;
    }
    public SlotBean getSlot()
    {
        return mSlot;
    }
    public void setSlot(SlotBean slot)
    {
        mSlot = slot;
    }
}
