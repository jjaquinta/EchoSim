package jo.alexa.sim.test;

import java.io.IOException;
import java.io.StringReader;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.IntentBean;
import jo.alexa.sim.logic.ApplicationLogic;
import jo.alexa.sim.logic.UtteranceLogic;

public class TestParsing
{

    public static void main(String[] args)
    {
        try
        {
            ApplicationBean app = new ApplicationBean();
            StringReader rdr = new StringReader(INTENT_SCHEMA);
            ApplicationLogic.readIntents(app, rdr);
            rdr.close();
            System.out.println("Schema contains "+app.getSchema().getIntents().size()+" intents");
            for (IntentBean intent : app.getSchema().getIntents())
                System.out.println("  Intent "+intent.getIntent()+" contains "+intent.getSlots().size()+" slots");
            rdr = new StringReader(UTTERANCES);
            UtteranceLogic.read(app, rdr);
            rdr.close();
            System.out.println("Application contains "+app.getUtterances().size()+" utterances");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static final String INTENT_SCHEMA = "{"+
      "  \"intents\": ["+
      "    {"+
      "      \"intent\": \"GetHoroscope\","+
      "      \"slots\": ["+
      "        {"+
      "          \"name\": \"Sign\","+
      "          \"type\": \"LITERAL\""+
      "        },"+
      "        {"+
      "          \"name\": \"Date\","+
      "          \"type\": \"DATE\""+
      "        }"+
      "      ]"+
      "    },"+
      "    {"+
      "      \"intent\": \"GetLuckyNumbers\","+
      "      \"slots\": []"+
      "    }"+
      "  ]"+
      "}";
    
    private static final String UTTERANCES = 
    "GetHoroscope\twhat is the horoscope for {pisces|Sign}\n"+
    "GetHoroscope\twhat will the horoscope for {leo|Sign} be {next tuesday|Date}\n"+
    "GetHoroscope\tget me my horoscope\n"+
    "GetLuckyNumbers\twhat are my lucky numbers\n"+
    "GetLuckyNumbers\ttell me my lucky numbers\n";
}
