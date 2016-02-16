package jo.alexa.sim.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.IntentBean;
import jo.alexa.sim.data.PhraseSegmentBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.data.SlotSegmentBean;
import jo.alexa.sim.data.TextSegmentBean;
import jo.alexa.sim.data.UtteranceBean;

public class UtteranceLogic
{
    private static final String[] BUILT_IN_UTTERANCES = {
        "AMAZON.CancelIntent\tcancel",
        "AMAZON.CancelIntent\tnever mind",
        "AMAZON.CancelIntent\tforget it",
        "AMAZON.HelpIntent\thelp",
        "AMAZON.HelpIntent\thelp me",
        "AMAZON.HelpIntent\tcan you help me",
        "AMAZON.NoIntent\tno",
        "AMAZON.NoIntent\tno thanks",
        "AMAZON.RepeatIntent\trepeat",
        "AMAZON.RepeatIntent\tsay that again",
        "AMAZON.RepeatIntent\trepeat that",
        "AMAZON.StartOverIntent\tstart over",
        "AMAZON.StartOverIntent\trestart",
        "AMAZON.StartOverIntent\tstart again",
        "AMAZON.StopIntent\tstop",
        "AMAZON.StopIntent\toff",
        "AMAZON.StopIntent\tshut up",
        "AMAZON.YesIntent\tyes",
        "AMAZON.YesIntent\tyes please",
        "AMAZON.YesIntent\tsure",
    };
    
    public static void read(ApplicationBean app, Reader r) throws IOException
    {
        app.getUtterances().clear();
        for (IntentBean intent : app.getIntentIndex().values())
            for (SlotBean slot : intent.getSlots())
                slot.getValues().clear();
        BufferedReader rdr = new BufferedReader(r);
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            inbuf = inbuf.trim();
            if (inbuf.length() > 0)
                parseUtterance(app, inbuf, true);
        }
        rdr.close();
        // simulate built in utterances
        for (String inbuf : BUILT_IN_UTTERANCES)
            parseUtterance(app, inbuf, false);
    }

    private static void parseUtterance(ApplicationBean app, String inbuf, boolean strict)
    {
        int o = inbuf.indexOf('\t');
        if (o < 0)
        {
            o = inbuf.indexOf(' ');
            if (o < 0)
                throw new IllegalArgumentException("Badly formed utterance line '"+inbuf+"'");
        }
        UtteranceBean utterance = new UtteranceBean();
        utterance.setIntent(app.getIntentIndex().get(inbuf.substring(0, o)));
        if ((utterance.getIntent() == null) && strict)
            throw new IllegalArgumentException("Unknown intent '"+inbuf.substring(0, o)+"'");
        inbuf = inbuf.substring(o + 1).trim();
        while (inbuf.length() > 0)
            if (inbuf.charAt(0) == '{')
            {
                int end = inbuf.indexOf('}');
                if (end < 0)
                    throw new IllegalArgumentException("Can't find end of slot '"+inbuf+"'");
                String slotPhrase = inbuf.substring(1, end);
                inbuf = inbuf.substring(end + 1).trim();
                SlotSegmentBean slotSeg = new SlotSegmentBean();
                int mid = slotPhrase.indexOf('|');
                if (mid < 0)
                {
                    slotSeg.setText(null);
                    slotSeg.setSlot(app.getSlotIndex().get(slotPhrase));
                }
                else
                {
                    slotSeg.setText(slotPhrase.substring(0, mid).toLowerCase());
                    slotSeg.setSlot(app.getSlotIndex().get(slotPhrase.substring(mid + 1)));
                }
                if (slotSeg.getSlot() == null)
                    throw new IllegalArgumentException("Unknown slot '"+slotPhrase.substring(mid + 1)+"'");
                utterance.getPhrase().add(slotSeg);
                if (slotSeg.getText() != null)
                    slotSeg.getSlot().getValues().add(slotSeg.getText());
            }
            else
            {
                int end = inbuf.indexOf('{');
                if (end < 0)
                    end = inbuf.length();
                TextSegmentBean textSeg = new TextSegmentBean();
                textSeg.setText(inbuf.substring(0, end).trim().toLowerCase());
                inbuf = inbuf.substring(end).trim();
                utterance.getPhrase().add(textSeg);
            }
        app.getUtterances().add(utterance);
    }
    
    public static String renderAsHTML(List<UtteranceBean> utterances)
    {
        StringBuffer html = new StringBuffer();
        for (UtteranceBean u : utterances)
        {
            if (html.length() > 0)
                html.append("<br/>");
            html.append(renderAsHTML(u));
        }
        return html.toString();
    }
    
    public static String renderAsHTML(UtteranceBean utterance)
    {
        StringBuffer html = new StringBuffer();
        html.append("<b>");
        html.append(utterance.getIntent().getIntent());
        html.append("</b>");
        html.append(" ");
        for (PhraseSegmentBean p : utterance.getPhrase())
            if (p instanceof TextSegmentBean)
            {
                html.append(((TextSegmentBean)p).getText());
                html.append(" ");
            }
            else if (p instanceof SlotSegmentBean)
            {
                html.append("<span title=\""+((SlotSegmentBean)p).getSlot().getName()+" ("+((SlotSegmentBean)p).getSlot().getName()+")\">");
                html.append(((SlotSegmentBean)p).getText());
                html.append("</span>");
                html.append(" ");
            }
            else
                throw new IllegalArgumentException("Unknown PhraseSegment: "+p.getClass().getName());
        return html.toString();
    }
}
