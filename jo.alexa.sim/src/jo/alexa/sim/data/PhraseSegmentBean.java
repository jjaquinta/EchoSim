package jo.alexa.sim.data;

public class PhraseSegmentBean implements Comparable<PhraseSegmentBean>
{
    @Override
    public int compareTo(PhraseSegmentBean seg2)
    {
        if ((this instanceof TextSegmentBean) && (seg2 instanceof SlotSegmentBean))
            return -1;
        if ((this instanceof SlotSegmentBean) && (seg2 instanceof TextSegmentBean))
            return 1;
        if (this instanceof SlotSegmentBean) // both are slot
            return ((SlotSegmentBean)this).getSlot().getName().compareTo(((SlotSegmentBean)seg2).getSlot().getName());
        return ((TextSegmentBean)this).getText().compareTo(((TextSegmentBean)seg2).getText());
    }
}
