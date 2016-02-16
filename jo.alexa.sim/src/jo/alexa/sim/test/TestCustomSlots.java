package jo.alexa.sim.test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.IntentBean;
import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.PhraseSegmentBean;
import jo.alexa.sim.data.SlotSegmentBean;
import jo.alexa.sim.data.TextSegmentBean;
import jo.alexa.sim.data.UtteranceBean;
import jo.alexa.sim.logic.ApplicationLogic;
import jo.alexa.sim.logic.MatchLogic;
import jo.alexa.sim.logic.UtteranceLogic;

public class TestCustomSlots
{
    static public File INTENT_FILE = new File("C:\\Users\\IBM_ADMIN\\git\\EchoTsaTsaTzu\\jo.echo.tweet.poll\\src\\jo\\starlanes\\lambda\\slu\\StarlanesTest.json");
    static public File UTTERANCE_FILE = new File("C:\\Users\\IBM_ADMIN\\git\\EchoTsaTsaTzu\\jo.echo.tweet.poll\\src\\jo\\starlanes\\lambda\\slu\\StarlanesTest.baf");
    static public Map<String, File> CUSTOM_SLOT_FILES = new HashMap<>();
    static
    {
        CUSTOM_SLOT_FILES.put("CALLSIGN", new File("C:\\Users\\IBM_ADMIN\\git\\EchoTsaTsaTzu\\jo.echo.tweet.poll\\src\\jo\\starlanes\\lambda\\slu\\TYPE_CALLSIGN.txt"));
        CUSTOM_SLOT_FILES.put("CONFIG", new File("C:\\Users\\IBM_ADMIN\\git\\EchoTsaTsaTzu\\jo.echo.tweet.poll\\src\\jo\\starlanes\\lambda\\slu\\TYPE_CONFIG.txt"));
        CUSTOM_SLOT_FILES.put("DRONE", new File("C:\\Users\\IBM_ADMIN\\git\\EchoTsaTsaTzu\\jo.echo.tweet.poll\\src\\jo\\starlanes\\lambda\\slu\\TYPE_DRONE.txt"));
        CUSTOM_SLOT_FILES.put("LOCATIONS", new File("C:\\Users\\IBM_ADMIN\\git\\EchoTsaTsaTzu\\jo.echo.tweet.poll\\src\\jo\\starlanes\\lambda\\slu\\TYPE_LOCATIONS.txt"));
        CUSTOM_SLOT_FILES.put("SIDE", new File("C:\\Users\\IBM_ADMIN\\git\\EchoTsaTsaTzu\\jo.echo.tweet.poll\\src\\jo\\starlanes\\lambda\\slu\\TYPE_SIDE.txt"));
        CUSTOM_SLOT_FILES.put("TOPIC", new File("C:\\Users\\IBM_ADMIN\\git\\EchoTsaTsaTzu\\jo.echo.tweet.poll\\src\\jo\\starlanes\\lambda\\slu\\TYPE_TOPIC.txt"));
    }   
    
    public static void main(String[] args)
    {
        try
        {
            ApplicationBean app = new ApplicationBean();
            FileReader rdr = new FileReader(INTENT_FILE);
            ApplicationLogic.readIntents(app, rdr);
            rdr.close();
            rdr = new FileReader(UTTERANCE_FILE);
            UtteranceLogic.read(app, rdr);
            rdr.close();
            for (String key : CUSTOM_SLOT_FILES.keySet())
            {
                rdr = new FileReader(CUSTOM_SLOT_FILES.get(key));
                ApplicationLogic.readCustomSlot(app, key, rdr);
                rdr.close();
            }
            System.out.println("Schema contains "+app.getSchema().getIntents().size()+" intents");
            for (IntentBean intent : app.getSchema().getIntents())
                System.out.println("  Intent "+intent.getIntent()+" contains "+intent.getSlots().size()+" slots");
            System.out.println("Application contains "+app.getUtterances().size()+" utterances");
            List<String> variants = new ArrayList<>();
            for (UtteranceBean utt : app.getUtterances())
                assembleVariants(variants, new StringBuffer(), utt.getPhrase(), 0);
            System.out.println("Application contains "+variants.size()+" variants");
            for (int i = 0; i < 10; i++)
                System.out.println("  "+variants.get(i));
            for (String variant : variants)
            {
                List<MatchBean> matches = MatchLogic.parseInput(app, variant);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void assembleVariants(List<String> variants,
            StringBuffer line, List<PhraseSegmentBean> phrases, int off)
    {
        if (off == phrases.size())
        {
            variants.add(line.toString());
            return;
        }
        PhraseSegmentBean phrase = phrases.get(off);
        if (phrase instanceof TextSegmentBean)
        {
            TextSegmentBean txt = (TextSegmentBean)phrase;
            line.append(" "+txt.getText()+" ");
            assembleVariants(variants, line, phrases, off + 1);
        }
        else if (phrase instanceof SlotSegmentBean)
        {
            SlotSegmentBean seg = (SlotSegmentBean)phrase;
            if (seg.getText() != null)
            {
                line.append(" "+seg.getText()+" ");
                assembleVariants(variants, line, phrases, off + 1);
            }
            else
            {
                int l = line.length();
                for (String txt : seg.getSlot().getValues())
                {
                    if (txt == null)
                        System.err.println("Why is there a null in "+seg.getSlot().getName()+"?");
                    line.append(" "+txt+" ");
                    assembleVariants(variants, line, phrases, off + 1);
                    line.setLength(l);
                }
            }
        }
    }
}
