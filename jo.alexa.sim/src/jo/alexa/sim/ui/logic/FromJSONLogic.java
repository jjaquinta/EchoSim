package jo.alexa.sim.ui.logic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.ResponseBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.ui.data.TransactionBean;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FromJSONLogic
{
    public static List<TransactionBean> fromJSON(JSONArray jtranss, ApplicationBean context)
    {
        List<TransactionBean> transs = new ArrayList<TransactionBean>();
        for (Object jtrans : jtranss)
            transs.add(fromJSON(new TransactionBean(), (JSONObject)jtrans, context));
        return transs;
    }
    
    public static TransactionBean fromJSON(TransactionBean trans, JSONObject jtrans, ApplicationBean context)
    {
        trans.setInputText((String)jtrans.get("InputText"));
        trans.setOutputText((String)jtrans.get("OutputText"));
        trans.setTransactionStart(((Number)jtrans.get("TransactionStart")).longValue());
        trans.setTransactionEnd(((Number)jtrans.get("TransactionEnd")).longValue());
        trans.setInputMatch(fromJSON((JSONObject)jtrans.get("InputMatch"), context));
        trans.setOutputData(fromJSON((JSONObject)jtrans.get("OutputData")));
        trans.setError((Throwable)fromJSON((String)jtrans.get("Error")));
        return trans;
    }
    
    private static Serializable fromJSON(String jobj)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            for (StringTokenizer st = new StringTokenizer(jobj, "."); st.hasMoreTokens(); )
                baos.write(Integer.parseInt(st.nextToken(), 16));
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            Serializable obj = (Serializable)ois.readObject();
            return obj;
        }
        catch (Exception e)
        {
            throw new IllegalStateException(e);
        }
    }
    
    private static ResponseBean fromJSON(JSONObject jresponse)
    {
        ResponseBean response = new ResponseBean();
        response.setVersion((String)jresponse.get("Version"));
        response.setOutputSpeechType((String)jresponse.get("OutputSpeechType"));
        response.setOutputSpeechText((String)jresponse.get("OutputSpeechText"));
        response.setCardType((String)jresponse.get("CardType"));
        response.setCardTitle((String)jresponse.get("CardTitle"));
        response.setCardContent((String)jresponse.get("CardContent"));
        response.setRepromptType((String)jresponse.get("RepromptType"));
        response.setRepromptText((String)jresponse.get("RepromptText"));
        response.setShouldEndSession((Boolean)jresponse.get("ShouldEndSession"));
        return response;
    }

    public static MatchBean fromJSON(JSONObject jmatch, ApplicationBean context)
    {
        if (jmatch == null)
            return null;
        MatchBean match = new MatchBean();
        match.setConfidence(((Number)jmatch.get("Confidence")).doubleValue());
        match.setIntent(context.getIntentIndex().get((String)jmatch.get("Intent")));
        match.setValues(fromJSON(new HashMap<SlotBean, String>(), (JSONObject)jmatch.get("Values"), context));
        return match;
    }

    private static Map<SlotBean, String> fromJSON(Map<SlotBean, String> values, JSONObject jvalues, ApplicationBean context)
    {
        for (String key : jvalues.keySet())
        {
            SlotBean slot = context.getSlotIndex().get(key);
            values.put(slot, (String)jvalues.get(key));
        }
        return values;
    }
}
