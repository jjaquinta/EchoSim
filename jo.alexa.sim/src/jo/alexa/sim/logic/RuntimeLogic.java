package jo.alexa.sim.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.ResponseBean;
import jo.alexa.sim.data.RuntimeBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.data.TransactionBean;

public class RuntimeLogic
{
    public static void acceptLicense(RuntimeBean runtime)
    {
        runtime.setDisclaimerAccepted(true);
    }
    public static void rejectLicense(RuntimeBean runtime)
    {
        System.exit(0);
    }
    public static void setEndpoint(RuntimeBean runtime, String endpoint)
    {
        runtime.getApp().setEndpoint(endpoint);
        runtime.firePropertyChange("app", null, runtime.getApp());
    }
    public static void readIntents(RuntimeBean runtime, InputStream is) throws IOException
    {
        InputStreamReader rdr = new InputStreamReader(is);
        ApplicationLogic.readIntents(runtime.getApp(), rdr);
        rdr.close();
        runtime.firePropertyChange("app", null, runtime.getApp());
    }
    public static void readUtterances(RuntimeBean runtime, InputStream is) throws IOException
    {
        InputStreamReader rdr = new InputStreamReader(is);
        UtteranceLogic.read(runtime.getApp(), rdr);
        rdr.close();
        runtime.firePropertyChange("app", null, runtime.getApp());
    }
    public static void send(RuntimeBean runtime, String text)
    {
        TransactionBean trans = new TransactionBean();
        trans.setInputText(text);
        List<MatchBean> matches =  MatchLogic.parseInput(runtime.getApp(), text);
        if (matches.size() == 0)
        {
            trans.setError(new IllegalArgumentException("No suitable parse for input"));
        }
        else
        {
            trans.setInputMatch(matches.get(0));
            String intentName = trans.getInputMatch().getIntent().getIntent();
            Properties slotValues = new Properties();
            for (SlotBean slot : trans.getInputMatch().getValues().keySet())
                slotValues.put(slot.getName(), trans.getInputMatch().getValues().get(slot));
            try
            {
                trans.setTransactionStart(System.currentTimeMillis());
                ResponseBean response = RequestLogic.performIntentRequest(runtime.getApp(), intentName, slotValues);
                trans.setTransactionEnd(System.currentTimeMillis());
                trans.setOutputData(response);
                trans.setOutputText(response.getOutputSpeechText());
            }
            catch (IOException e)
            {
                trans.setError(e);
            }
        }
        runtime.getHistory().add(trans);
        runtime.firePropertyChange("history", null, runtime.getHistory());
    }
    public static void startSession(RuntimeBean runtime)
    {
        TransactionBean trans = new TransactionBean();
        try
        {
            trans.setTransactionStart(System.currentTimeMillis());
            ResponseBean response = RequestLogic.performLaunchRequest(runtime.getApp());
            trans.setTransactionEnd(System.currentTimeMillis());
            trans.setOutputData(response);
            trans.setOutputText(response.getOutputSpeechText());
        }
        catch (IOException e)
        {
            trans.setError(e);
        }
        runtime.getHistory().add(trans);
        runtime.firePropertyChange("history", null, runtime.getHistory());
    }
    public static void endSession(RuntimeBean runtime, String reason)
    {
        TransactionBean trans = new TransactionBean();
        try
        {
            trans.setTransactionStart(System.currentTimeMillis());
            ResponseBean response = RequestLogic.performSessionEndedRequest(runtime.getApp(), reason);
            trans.setTransactionEnd(System.currentTimeMillis());
            trans.setOutputData(response);
            trans.setOutputText(response.getOutputSpeechText());
        }
        catch (IOException e)
        {
            trans.setError(e);
        }
        runtime.getHistory().add(trans);
        runtime.firePropertyChange("history", null, runtime.getHistory());
    }
}
