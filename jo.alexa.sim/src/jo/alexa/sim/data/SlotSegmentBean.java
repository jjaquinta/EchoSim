package jo.alexa.sim.data;

public class SlotSegmentBean extends PhraseSegmentBean
{
    private String      mText;
    private SlotBean    mSlot;
    
    @Override
    public String toString()
    {
        if (mText == null)
            return "{"+mSlot.getName()+"}";
        else
            return "{"+mText+"|"+mSlot.getName()+"}";
    }
    
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
