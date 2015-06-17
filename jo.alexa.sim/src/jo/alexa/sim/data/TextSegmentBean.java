package jo.alexa.sim.data;

public class TextSegmentBean extends PhraseSegmentBean
{
    private String  mText;
    
    @Override
    public String toString()
    {
        return mText;
    }

    public String getText()
    {
        return mText;
    }

    public void setText(String text)
    {
        mText = text;
    }
}
