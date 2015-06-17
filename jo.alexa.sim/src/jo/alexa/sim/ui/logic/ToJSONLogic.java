package jo.alexa.sim.ui.logic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.ResponseBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.ui.data.TransactionBean;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ToJSONLogic
{
    @SuppressWarnings("unchecked")
    public static JSONArray toJSON(List<TransactionBean> transs)
    {
        JSONArray jtranss = new JSONArray();
        for (TransactionBean trans : transs)
            jtranss.add(toJSON(trans));
        return jtranss;
    }
    
    public static JSONObject toJSON(TransactionBean trans)
    {
        JSONObject jtrans = new JSONObject();
        jtrans.put("InputText", trans.getInputText());
        jtrans.put("OutputText", trans.getOutputText());
        jtrans.put("TransactionStart", trans.getTransactionStart());
        jtrans.put("TransactionEnd", trans.getTransactionEnd());
        jtrans.put("InputMatch", toJSON(trans.getInputMatch()));
        jtrans.put("OutputData", toJSON(trans.getOutputData()));
        jtrans.put("Error", toJSON(trans.getError()));
        return jtrans;
    }
    
    private static String toJSON(Serializable obj)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();
            StringBuffer sb = new StringBuffer();
            for (byte b : baos.toByteArray())
            {
                if (sb.length() > 0)
                    sb.append(".");
                sb.append(Integer.toHexString(b&0xff));
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    private static JSONObject toJSON(ResponseBean response)
    {
        JSONObject jresponse = new JSONObject();
        jresponse.put("Version", response.getVersion());
        jresponse.put("OutputSpeechType", response.getOutputSpeechType());
        jresponse.put("OutputSpeechText", response.getOutputSpeechText());
        jresponse.put("CardType", response.getCardType());
        jresponse.put("CardTitle", response.getCardTitle());
        jresponse.put("CardContent", response.getCardContent());
        jresponse.put("RepromptType", response.getRepromptType());
        jresponse.put("RepromptText", response.getRepromptText());
        jresponse.put("ShouldEndSession", response.isShouldEndSession());
        return jresponse;
    }

    public static JSONObject toJSON(MatchBean match)
    {
        if (match == null)
            return null;
        JSONObject jmatch = new JSONObject();
        jmatch.put("Confidence", match.getConfidence());
        jmatch.put("Intent", match.getIntent().getIntent());
        jmatch.put("Values", toJSON(match.getValues()));
        return jmatch;
    }

    private static JSONObject toJSON(Map<SlotBean, String> values)
    {
        JSONObject jvalues = new JSONObject();
        for (SlotBean slot : values.keySet())
            jvalues.put(slot.getName(), values.get(slot));
        return jvalues;
    }
}
