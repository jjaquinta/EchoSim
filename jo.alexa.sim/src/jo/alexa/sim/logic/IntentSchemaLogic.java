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
            JSONObject intentSchema;
            try
            {
                intentSchema = (JSONObject)mParser.parse(rdr);
            }
            catch (ClassCastException e)
            {
                throw new IOException("Expected primary JSON entity to be an object.");
            }
            IntentSchemaBean is = new IntentSchemaBean();
            JSONArray intents;
            try
            {
                intents = (JSONArray)intentSchema.get("intents");
            }
            catch (ClassCastException e)
            {
                throw new IOException("Expected intents JSON entity to be an array.");
            }
            if (intents == null)
                throw new IOException("No intents member of intent schema");
            for (Object intent : intents)
            {
                IntentBean i = new IntentBean();
                JSONObject jIntent;
                try
                {
                    jIntent = (JSONObject)intent;
                }
                catch (ClassCastException e)
                {
                    throw new IOException("Expected intent array JSON entity to be an object.");
                }
                try
                {
                    i.setIntent((String)jIntent.get("intent"));
                }
                catch (ClassCastException e)
                {
                    throw new IOException("Expected intent definition to be a string.");
                }
                JSONArray slots;
                try
                {
                    slots = (JSONArray)(jIntent.get("slots"));
                }
                catch (ClassCastException e)
                {
                    throw new IOException("Expected slots JSON entity to be an array.");
                }
                for (Object slot : slots)
                {
                    SlotBean s = new SlotBean();
                    try
                    {
                        s.setName((String)((JSONObject)slot).get("name"));
                    }
                    catch (ClassCastException e)
                    {
                        throw new IOException("Expected name definition to be a string.");
                    }
                    try
                    {
                        s.setType((String)((JSONObject)slot).get("type"));
                    }
                    catch (ClassCastException e)
                    {
                        throw new IOException("Expected type definition to be a string.");
                    }
                    i.getSlots().add(s);
                    if (!s.getType().startsWith("AMAZON."))
                        is.getCustomSlots().add(s.getType());
                }
                is.getIntents().add(i);
            }
            return is;
        }
        catch (ParseException e)
        {
            throw new IOException(e);
        }        
    }
}
