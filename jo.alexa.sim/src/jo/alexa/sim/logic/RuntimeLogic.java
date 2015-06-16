package jo.alexa.sim.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.Properties;

import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.ResponseBean;
import jo.alexa.sim.data.RuntimeBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.data.TransactionBean;

public class RuntimeLogic
{
    private static Properties   mProps;

    public static RuntimeBean newInstance()
    {
        RuntimeBean runtime = new RuntimeBean();
        runtime.getApp().setEndpoint(getProp("app.endpoint"));
        try
        {
            readIntents(runtime, new URI(getProp("app.intents")));
        }
        catch (Exception e)
        {
        }
        try
        {
            readUtterances(runtime, new URI(getProp("app.utterances")));
        }
        catch (Exception e)
        {
        }
        RequestLogic.disableCertificateValidation();
        return runtime;
    }
    
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
        System.out.println("Start new endpoint");
        runtime.getApp().setEndpoint(endpoint);
        runtime.firePropertyChange("app", null, runtime.getApp());
        setProp("app.endpoint", runtime.getApp().getEndpoint());
        System.out.println("End new endpoint");
    }
    public static void readIntents(RuntimeBean runtime, URI source) throws IOException
    {
        InputStream is = source.toURL().openStream();
        InputStreamReader rdr = new InputStreamReader(is);
        ApplicationLogic.readIntents(runtime.getApp(), rdr);
        rdr.close();
        runtime.firePropertyChange("app", null, runtime.getApp());
        setProp("app.intents", source.toString());
    }
    public static void readUtterances(RuntimeBean runtime, URI source) throws IOException
    {
        InputStream is = source.toURL().openStream();
        InputStreamReader rdr = new InputStreamReader(is);
        UtteranceLogic.read(runtime.getApp(), rdr);
        rdr.close();
        runtime.firePropertyChange("app", null, runtime.getApp());
        setProp("app.utterances", source.toString());
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
            runtime.getApp().setSessionID(null);
        }
        catch (IOException e)
        {
            trans.setError(e);
        }
        runtime.getHistory().add(trans);
        runtime.firePropertyChange("history", null, runtime.getHistory());
    }

    public static void setProp(String key, String value)
    {
        loadProps();
        mProps.setProperty(key, value);
        saveProps();
    }

    public static String getProp(String key)
    {
        loadProps();
        return mProps.getProperty(key);
    }
    
    private static void loadProps()
    {
        if (mProps != null)
            return;
        mProps = new Properties();
        File propFile = new File(System.getProperty("user.home"), ".echosim.properties");
        if (!propFile.exists())
            return;
        try
        {
            FileInputStream fis = new FileInputStream(propFile);
            mProps.load(fis);
            fis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void saveProps()
    {
        if (mProps == null)
            return;
        File propFile = new File(System.getProperty("user.home"), ".echosim.properties");
        try
        {
            FileOutputStream fos = new FileOutputStream(propFile);
            mProps.store(fos, "EchoSim Properties");
            fos.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void clearHistory(RuntimeBean runtime)
    {
        runtime.getHistory().clear();
        runtime.firePropertyChange("history", null, runtime.getHistory());
    }
}
