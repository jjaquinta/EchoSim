package jo.alexa.sim.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.PhraseSegmentBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.data.SlotSegmentBean;
import jo.alexa.sim.data.TextSegmentBean;
import jo.alexa.sim.data.UtteranceBean;

public class MatchLogic
{
    public static List<MatchBean> parseInput(ApplicationBean app, String inbuf)
    {
        inbuf = inbuf.trim().toLowerCase();
        List<MatchBean> matches = new ArrayList<MatchBean>();
        findLiteralMatch(app, matches, inbuf);
        if (matches.size() == 0)
        {
            findSlotMatch(app, matches, inbuf);
            if (matches.size() == 0)
            {
                for (UtteranceBean u : app.getUtterances())
                {
                    MatchBean match = match(u, inbuf);
                    if (match != null)
                        matches.add(match);
                }
            }
        }
        Collections.sort(matches, new Comparator<MatchBean>() {
            @Override
            public int compare(MatchBean o1, MatchBean o2)
            {
                return (int)Math.signum(o2.getConfidence() - o1.getConfidence());
            }
        });
        // remove duplicates
        for (int i = 0; i < matches.size(); i++)
        {
            MatchBean m1 = matches.get(i);
            while (matches.size() > i + 1)
            {
                MatchBean m2 = matches.get(i+1);
                if (m1.equals(m2))
                    matches.remove(i + 1);
                else
                    break;
            }
        }
        return matches;
    }

    private static void findLiteralMatch(ApplicationBean app, List<MatchBean> matches, String inbuf)
    {
        for (UtteranceBean u : app.getUtterances())
        {
            if (u.getPhrase().size() != 1)
                continue;
            if (!(u.getPhrase().get(0) instanceof TextSegmentBean))
                continue;
            TextSegmentBean text = (TextSegmentBean)u.getPhrase().get(0);
            if (!text.getText().trim().toLowerCase().equals(inbuf.trim().toLowerCase()))
                continue;
            MatchBean match = new MatchBean();
            match.setIntent(u.getIntent());
            match.setConfidence(1.0);
            matches.add(match);
            break;
        }
    }
    
    private static void findSlotMatch(ApplicationBean app, List<MatchBean> matches, String inbuf)
    {
        for (UtteranceBean u : app.getUtterances())
        {
            addSlotMatches(u, 0, new HashMap<SlotBean,String>(), matches, inbuf);
        }
    }
    
    private static void addSlotMatches(UtteranceBean u, int idx, Map<SlotBean,String> values, List<MatchBean> matches, String text)
    {
        if (idx >= u.getPhrase().size())
        {
            if (text.length() == 0)
            {
                Map<SlotBean,String> valuesCopy = new HashMap<SlotBean, String>();
                for (SlotBean slot : values.keySet())
                    valuesCopy.put(slot, values.get(slot));
                MatchBean match = new MatchBean();
                match.setIntent(u.getIntent());
                match.setConfidence(1.0);
                match.setValues(valuesCopy);
                matches.add(match);
            }
            return;
        }
        if (u.getPhrase().get(idx) instanceof TextSegmentBean)
        {
            TextSegmentBean textSeg = (TextSegmentBean)u.getPhrase().get(idx);
            if (text.equals(textSeg.getText()) || text.startsWith(textSeg.getText()+" "))
                addSlotMatches(u, idx + 1, values, matches, text.substring(textSeg.getText().length()).trim());
        }
        else if (u.getPhrase().get(idx) instanceof SlotSegmentBean)
        {
            SlotSegmentBean slotSeg = (SlotSegmentBean)u.getPhrase().get(idx);
            for (String slotVal : slotSeg.getSlot().getValues())
                if (text.equals(slotVal) || text.startsWith(slotVal+" "))
                {
                    values.put(slotSeg.getSlot(), slotVal);
                    addSlotMatches(u, idx + 1, values, matches, text.substring(slotVal.length()).trim());
                    values.remove(slotSeg.getSlot());
                }
        }
        else
            throw new IllegalArgumentException("Unknown phrase "+u.getPhrase().getClass().getName());
    }
    
    private static MatchBean match(UtteranceBean u, String inbuf)
    {
        int[][] segments = new int[u.getPhrase().size()][2];
        int lastMatch = 0;
        for (int i = 0; i < u.getPhrase().size(); i++)
        {
            PhraseSegmentBean phrase = u.getPhrase().get(i);
            if (phrase instanceof TextSegmentBean)
            {
                segments[i] = indexOf(inbuf, lastMatch, ((TextSegmentBean)phrase).getText());
                if (segments[i] == null)
                    return null;
                if (i == 0)
                {
                    String prefix = inbuf.substring(0, segments[i][0]).trim();
                    if (prefix.length() > 0)
                        return null; // no cruft at the start
                }
                lastMatch = segments[i][1];
            }
            else if (phrase instanceof SlotSegmentBean)
            {
                SlotSegmentBean seg = (SlotSegmentBean)phrase;
                if (seg.getText() != null)
                {
                    segments[i] = indexOf(inbuf, lastMatch, ((SlotSegmentBean)phrase).getText());
                    if (segments[i] != null)
                        lastMatch = segments[i][1];
                }
                else
                {
                    for (String txt : seg.getSlot().getValues())
                    {
                        segments[i] = indexOf(inbuf, lastMatch, txt);
                        if (segments[i] != null)
                        {
                            lastMatch = segments[i][1];
                            break;
                        }
                    }
                }
            }
            else
                throw new IllegalArgumentException("Unknown phrase "+phrase.getClass().getName());
        }
        double confidence = 0;
        int numSlots = 0;
        MatchBean match = new MatchBean();
        match.setIntent(u.getIntent());
        for (int i = 0; i < u.getPhrase().size(); i++)
        {
            PhraseSegmentBean phrase = u.getPhrase().get(i);
            if (phrase instanceof SlotSegmentBean)
            {
                SlotSegmentBean slotSeg = (SlotSegmentBean)phrase;
                numSlots++;
                if (segments[i] != null)
                    confidence += 1.0;
                else
                {
                    confidence += .5;
                    int start = 0;
                    int end = inbuf.length();
                    if ((i > 0) && (segments[i-1] != null))
                        start = segments[i-1][1];
                    while ((start < end) && Character.isWhitespace(inbuf.charAt(start)))
                        start++;
                    if (i + 1 < segments.length)
                        if (segments[i+1] != null)
                            end = segments[i+1][0];
                        else
                        {
                            int sp = inbuf.indexOf(' ', start);
                            if (sp > start)
                                end = sp;
                        }
                    segments[i] = new int[] { start, end };
                }
                match.getValues().put(slotSeg.getSlot(), inbuf.substring(segments[i][0], segments[i][1]).trim());
            }
        }
        if (numSlots > 0)
            match.setConfidence(confidence/numSlots);
        else
            match.setConfidence(1.0);
        return match;
    }
    
    private static int[] indexOf(String target, int start, String pattern)
    {
        int first = -1;
        int last = -1;
        int at = start;
        for (StringTokenizer st = new StringTokenizer(pattern, " "); st.hasMoreTokens(); )
        {
            String pat = st.nextToken();
            int o = target.indexOf(pat, at);
            if (o < 0)
                return null;
            last = o + pat.length();
            if (last < target.length())
                if (!Character.isWhitespace(target.charAt(last)))
                    return null; // not a word break
            if (first < 0)
                first = o;
            String mid = target.substring(at, o);
            if (mid.trim().length() > 0)
                return null; // something in the middle
            at = o + pat.length();
        }
        if ((first < 0) || (last < 0))
            return null;
        return new int[] { first, last };
    }
}
