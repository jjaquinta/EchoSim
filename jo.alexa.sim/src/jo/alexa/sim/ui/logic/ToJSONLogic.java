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
import jo.alexa.sim.ui.data.AppSpecBean;
import jo.alexa.sim.ui.data.ScriptTransactionBean;
import jo.alexa.sim.ui.data.TransactionBean;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ToJSONLogic
{
    @SuppressWarnings("unchecked")
    public static JSONArray toJSONAppSpecs(List<AppSpecBean> specs)
    {
        JSONArray jspecs = new JSONArray();
        for (AppSpecBean spec : specs)
            jspecs.add(toJSON(spec));
        return jspecs;
    }
    
    public static JSONObject toJSON(AppSpecBean spec)
    {
        JSONObject jspec = new JSONObject();
        jspec.put("Name", spec.getName());
        jspec.put("Endpoint", spec.getEndpoint());
        jspec.put("ApplicationID", spec.getApplicationID());
        jspec.put("UserID", spec.getUserID());
        jspec.put("IntentURI", spec.getIntentURI());
        jspec.put("UtteranceURI", spec.getUtteranceURI());
        return jspec;
    }
    
    @SuppressWarnings("unchecked")
    public static JSONArray toJSONScriptTransactions(List<ScriptTransactionBean> transs)
    {
        JSONArray jtranss = new JSONArray();
        for (ScriptTransactionBean trans : transs)
            jtranss.add(toJSONScriptTransaction(trans));
        return jtranss;
    }
    
    public static JSONObject toJSONScriptTransaction(ScriptTransactionBean trans)
    {
        JSONObject jtrans = toJSONTransaction(trans);
        jtrans.put("MatchMode", trans.getMatchMode());
        jtrans.put("ActualResult", toJSONTransaction(trans.getActualResult()));
        return jtrans;
    }
    
    @SuppressWarnings("unchecked")
    public static JSONArray toJSONTransactions(List<TransactionBean> transs)
    {
        JSONArray jtranss = new JSONArray();
        for (TransactionBean trans : transs)
            jtranss.add(toJSONTransaction(trans));
        return jtranss;
    }
    
    public static JSONObject toJSONTransaction(TransactionBean trans)
    {
        if (trans == null)
            return null;
        JSONObject jtrans = new JSONObject();
        jtrans.put("RequestType", trans.getRequestType());
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
        if (match.getIntent() != null)
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
