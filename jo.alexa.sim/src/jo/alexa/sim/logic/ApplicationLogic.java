package jo.alexa.sim.logic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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

    public static void readCustomSlot(ApplicationBean app, String slotName,
            FileReader frdr) throws IOException
    {
        List<String> slotValues = new ArrayList<>();
        BufferedReader rdr = new BufferedReader(frdr);
        for (;;)
        {
            String inbuf = rdr.readLine();
            if (inbuf == null)
                break;
            slotValues.add(inbuf.toLowerCase());
        }
        app.getCustomSlots().put(slotName, slotValues);
        for (SlotBean slot : app.getSlotIndex().values())
            if (slot.getType().equals(slotName))
                slot.getValues().addAll(slotValues);
    }    
}
