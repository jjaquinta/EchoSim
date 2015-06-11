package jo.alexa.sim.logic;

import java.io.IOException;
import java.io.Reader;

import jo.alexa.sim.data.IntentBean;
import jo.alexa.sim.data.IntentSchemaBean;
import jo.alexa.sim.data.SlotBean;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class IntentSchemaLogic
{
    private static final JSONParser mParser = new JSONParser();
    
    public static IntentSchemaBean read(Reader rdr) throws IOException
    {
        try
        {
            JSONObject intentSchema = (JSONObject)mParser.parse(rdr);
            IntentSchemaBean is = new IntentSchemaBean();
            JSONArray intents = (JSONArray)intentSchema.get("intents");
            if (intents == null)
                throw new IOException("No intents member of intent schema");
            for (Object intent : intents)
            {
                IntentBean i = new IntentBean();
                i.setIntent((String)((JSONObject)intent).get("intent"));
                JSONArray slots = (JSONArray)(((JSONObject)intent).get("slots"));
                for (Object slot : slots)
                {
                    SlotBean s = new SlotBean();
                    s.setName((String)((JSONObject)slot).get("name"));
                    s.setType((String)((JSONObject)slot).get("type"));
                    i.getSlots().add(s);
                }
                is.getIntents().add(i);
            }
            return is;
        }
        catch (ClassCastException e)
        {
            throw new IOException("Bad json format", e);            
        }
        catch (ParseException e)
        {
            throw new IOException(e);
        }        
    }
}
