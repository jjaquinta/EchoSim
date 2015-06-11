package jo.alexa.sim.data;

import java.util.ArrayList;
import java.util.List;

public class UtteranceBean implements Comparable<UtteranceBean>
{
    private IntentBean              mIntent;
    private List<PhraseSegmentBean> mPhrase;
    
    public UtteranceBean()
    {
        mPhrase = new ArrayList<PhraseSegmentBean>();
    }
    
    public int compareTo(UtteranceBean o2) 
    {
        String i1 = getIntent().getIntent();
        String i2 = o2.getIntent().getIntent();
        int cmp = i1.compareTo(i2);
        if (cmp != 0)
            return cmp;
        int ph1 = getPhrase().size();
        int ph2 = o2.getPhrase().size();
        if (ph1 != ph2)
            return ph1 - ph2;
        PhraseSegmentBean seg1 = getPhrase().get(0);
        PhraseSegmentBean seg2 = o2.getPhrase().get(0);
        return seg1.compareTo(seg2);
    }
    
    public IntentBean getIntent()
    {
        return mIntent;
    }
    public void setIntent(IntentBean intent)
    {
        mIntent = intent;
    }
    public List<PhraseSegmentBean> getPhrase()
    {
        return mPhrase;
    }
    public void setPhrase(List<PhraseSegmentBean> phrase)
    {
        mPhrase = phrase;
    }
}
