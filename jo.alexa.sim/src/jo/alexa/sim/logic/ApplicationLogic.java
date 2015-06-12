package jo.alexa.sim.logic;

import java.io.IOException;
import java.io.Reader;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.IntentBean;
import jo.alexa.sim.data.SlotBean;

public class ApplicationLogic
{    
    public static void readIntents(ApplicationBean app, Reader rdr) throws IOException
    {
        app.getSlotIndex().clear();
        app.getIntentIndex().clear();
        app.setSchema(IntentSchemaLogic.read(rdr));
        app.getUtterances().clear(); // these are now invalidated
        for (IntentBean intent : app.getSchema().getIntents())
        {
            app.getIntentIndex().put(intent.getIntent(), intent);
            for (SlotBean slot : intent.getSlots())
                app.getSlotIndex().put(slot.getName(), slot);
        }
    }    
}
