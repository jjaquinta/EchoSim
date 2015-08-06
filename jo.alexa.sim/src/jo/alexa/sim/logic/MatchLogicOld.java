package jo.alexa.sim.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.PhraseSegmentBean;
import jo.alexa.sim.data.SlotSegmentBean;
import jo.alexa.sim.data.TextSegmentBean;
import jo.alexa.sim.data.UtteranceBean;

public class MatchLogicOld
{
    public static List<MatchBean> parseInput(ApplicationBean app, String inbuf)
    {
        inbuf = inbuf.trim().toLowerCase();
        List<MatchBean> matches = new ArrayList<MatchBean>();
        for (UtteranceBean u : app.getUtterances())
        {
            MatchBean match = match(u, inbuf);
            if (match != null)
                matches.add(match);
        }
        Collections.sort(matches, new Comparator<MatchBean>() {
            @Override
            public int compare(MatchBean o1, MatchBean o2)
            {
                return (int)Math.signum(o2.getConfidence() - o1.getConfidence());
            }
        });
        return matches;
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
                segments[i] = indexOf(inbuf, lastMatch, ((SlotSegmentBean)phrase).getText());
                if (segments[i] != null)
                    lastMatch = segments[i][1];
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
