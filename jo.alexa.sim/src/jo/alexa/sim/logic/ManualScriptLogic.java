package jo.alexa.sim.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.IntentBean;
import jo.alexa.sim.data.PhraseSegmentBean;
import jo.alexa.sim.data.SlotSegmentBean;
import jo.alexa.sim.data.TextSegmentBean;
import jo.alexa.sim.data.UtteranceBean;
import jo.alexa.sim.ui.data.TransactionBean;

public class ManualScriptLogic
{
    private static final Random RND = new Random();
    
    public static String generateScript(ApplicationBean app, int numTestCases, boolean randomizeOrder,
            List<TransactionBean> history)
    {
        // validate args
        if (numTestCases < 1)
            numTestCases = app.getUtterances().size();
        if (numTestCases > app.getUtterances().size())
            numTestCases = app.getUtterances().size();
        // collect test cases
        Set<UtteranceBean> cases = new HashSet<UtteranceBean>();
        if (numTestCases == app.getUtterances().size())
            cases.addAll(app.getUtterances());
        else
            while (cases.size() < numTestCases)
                cases.add(app.getUtterances().get(RND.nextInt(app.getUtterances().size())));
        List<UtteranceBean> sorted;
        if (randomizeOrder)
            sorted = randomize(cases);
        else
            sorted = sort(cases);
        // print cases
        StringBuffer html = new StringBuffer();
        IntentBean lastIntent = null;
        int major = 0;
        int minor = 0;
        for (UtteranceBean utterance : sorted)
        {
            if (!randomizeOrder)
            {
                if (lastIntent != utterance.getIntent())
                {
                    major++;
                    minor = 1;
                    lastIntent = utterance.getIntent();
                    html.append("<h1>"+lastIntent.getIntent()+"</h1>");
                }
                else
                    minor++;
                html.append("<h2>Test case "+major+"."+minor+"</h2>");
            }
            else
            {
                minor++;
                html.append("<h2>Test case "+minor+"</h2>");
            }
            html.append("<ul>");
            html.append("<li>");
            html.append("Say:");
            for (PhraseSegmentBean seg : utterance.getPhrase())
                if (seg instanceof TextSegmentBean)
                    html.append(" "+((TextSegmentBean)seg).getText());
                else if (seg instanceof SlotSegmentBean)
                    html.append(" "+((SlotSegmentBean)seg).getText());
                else
                    throw new IllegalArgumentException("Unknown phrase "+seg.getClass().getName());
            html.append("</li>");
            html.append("<li>");
            html.append("Expect:");
            TransactionBean expect = findExpected(utterance, history);
            if (expect != null)
                html.append(expect.getOutputText());
            html.append("</li>");
            html.append("</ul>");
        }
        return html.toString();
    }

    private static TransactionBean findExpected(UtteranceBean utterance,
            List<TransactionBean> history)
    {
        if (history == null)
            return null;
        TransactionBean best = null;
        for (TransactionBean hist : history)
        {
            if (hist.getInputMatch() == null)
                continue; // open session
            if (!hist.getInputMatch().getIntent().getIntent().equals(utterance.getIntent().getIntent()))
                continue;
            boolean match = true;
            boolean perfectMatch = true;
            for (PhraseSegmentBean seg : utterance.getPhrase())
                if (seg instanceof SlotSegmentBean)
                {
                    SlotSegmentBean slotSeg = (SlotSegmentBean)seg;
                    String histText = hist.getInputMatch().getValues().get(slotSeg.getSlot());
                    if (histText == null)
                    {
                        match = false;
                        break;
                    }
                    String slotText = slotSeg.getText();
                    if (!histText.equals(slotText))
                        perfectMatch = false;
                }
            if (perfectMatch)
                return hist;
            if (match)
                best = hist;
        }
        return best;
    }

    private static List<UtteranceBean> randomize(Set<UtteranceBean> cases)
    {
        List<UtteranceBean> sorted = new ArrayList<UtteranceBean>();
        for (Iterator<UtteranceBean> i = cases.iterator(); i.hasNext(); )
            sorted.add(RND.nextInt(sorted.size() + 1), i.next());
        return sorted;
    }

    private static List<UtteranceBean> sort(Set<UtteranceBean> cases)
    {
        List<UtteranceBean> sorted = new ArrayList<UtteranceBean>();
        sorted.addAll(cases);
        Collections.sort(sorted, new Comparator<UtteranceBean>() {
            @Override
            public int compare(UtteranceBean o1, UtteranceBean o2)
            {
                return o1.compareTo(o2);
            }
        });
        return sorted;
    }
}
