package jo.alexa.sim.ui.logic;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.ResponseBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.logic.ApplicationLogic;
import jo.alexa.sim.logic.MatchLogic;
import jo.alexa.sim.logic.RequestLogic;
import jo.alexa.sim.logic.UtteranceLogic;
import jo.alexa.sim.ui.data.AppSpecBean;
import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TransactionBean;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RuntimeLogic
{
    static final JSONParser mParser = new JSONParser();
    private static Properties   mProps;
    private static ClipboardOwner mClipOwner = new ClipboardOwner() {        
        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents)
        {
        }
    };

    public static RuntimeBean newInstance()
    {
        RuntimeBean runtime = new RuntimeBean();
        runtime.getApp().setEndpoint(getProp("app.endpoint"));
        runtime.getRenderOps().setInputText(Boolean.parseBoolean(getProp("app.ops.inputtext", String.valueOf(runtime.getRenderOps().isInputText()))));
        runtime.getRenderOps().setIntents(Boolean.parseBoolean(getProp("app.ops.intents", String.valueOf(runtime.getRenderOps().isIntents()))));
        runtime.getRenderOps().setOutputText(Boolean.parseBoolean(getProp("app.ops.outputtext", String.valueOf(runtime.getRenderOps().isOutputText()))));
        runtime.getRenderOps().setErrors(Boolean.parseBoolean(getProp("app.ops.errors", String.valueOf(runtime.getRenderOps().isErrors()))));
        runtime.getRenderOps().setCards(Boolean.parseBoolean(getProp("app.ops.cards", String.valueOf(runtime.getRenderOps().isCards()))));
        runtime.getRenderOps().setReprompt(Boolean.parseBoolean(getProp("app.ops.reprompt", String.valueOf(runtime.getRenderOps().isReprompt()))));
        runtime.getRenderOps().setVerbose(Boolean.parseBoolean(getProp("app.ops.verbose", String.valueOf(runtime.getRenderOps().isVerbose()))));
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
        try
        {
            loadHistory(runtime, new File(getProp("app.history")));
        }
        catch (Exception e)
        {
        }
        try
        {
            JSONArray jspecs = (JSONArray)mParser.parse(getProp("app.mrus"));
            runtime.setMRUs(FromJSONLogic.fromJSONAppSpecs(jspecs));
        }
        catch (Exception e)
        {            
        }
        if (runtime.getMRUs() == null)
            runtime.setMRUs(new ArrayList<AppSpecBean>());
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
        runtime.getApp().setEndpoint(endpoint);
        runtime.firePropertyChange("app", null, runtime.getApp());
        setProp("app.endpoint", runtime.getApp().getEndpoint());
    }
    public static void setApplicationID(RuntimeBean runtime, String applicationID)
    {
        runtime.getApp().setApplicationID(applicationID);
        runtime.firePropertyChange("app", null, runtime.getApp());
        setProp("app.applicationID", runtime.getApp().getApplicationID());
    }
    public static void setUserID(RuntimeBean runtime, String UserID)
    {
        runtime.getApp().setUserID(UserID);
        runtime.firePropertyChange("app", null, runtime.getApp());
        setProp("app.endpoint", runtime.getApp().getUserID());
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
    public static TransactionBean send(RuntimeBean runtime, String text)
    {
        TransactionBean trans = new TransactionBean();
        trans.setRequestType(RequestLogic.INTENT_REQUEST);
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
        return trans;
    }
    public static TransactionBean startSession(RuntimeBean runtime)
    {
        TransactionBean trans = new TransactionBean();
        trans.setRequestType(RequestLogic.LAUNCH_REQUEST);
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
        return trans;
    }
    public static TransactionBean endSession(RuntimeBean runtime, String reason)
    {
        TransactionBean trans = new TransactionBean();
        trans.setRequestType(RequestLogic.SESSION_ENDED_REQUEST);
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
        return trans;
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

    public static String getProp(String key, String def)
    {
        loadProps();
        return mProps.getProperty(key, def);
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

    public static void toggleShowInput(RuntimeBean runtime)
    {
        runtime.getRenderOps().setInputText(!runtime.getRenderOps().isInputText());
        runtime.firePropertyChange("renderOps", null, runtime.getRenderOps());
        setProp("app.ops.inputtext", String.valueOf(runtime.getRenderOps().isInputText()));
    }

    public static void toggleShowIntent(RuntimeBean runtime)
    {
        runtime.getRenderOps().setIntents(!runtime.getRenderOps().isIntents());
        runtime.firePropertyChange("renderOps", null, runtime.getRenderOps());
        setProp("app.ops.intents", String.valueOf(runtime.getRenderOps().isIntents()));
    }

    public static void toggleShowOutput(RuntimeBean runtime)
    {
        runtime.getRenderOps().setOutputText(!runtime.getRenderOps().isOutputText());
        runtime.firePropertyChange("renderOps", null, runtime.getRenderOps());
        setProp("app.ops.oOutput", String.valueOf(runtime.getRenderOps().isOutputText()));
    }

    public static void toggleShowError(RuntimeBean runtime)
    {
        runtime.getRenderOps().setErrors(!runtime.getRenderOps().isErrors());
        runtime.firePropertyChange("renderOps", null, runtime.getRenderOps());
        setProp("app.ops.errors", String.valueOf(runtime.getRenderOps().isErrors()));
    }

    public static void toggleShowCards(RuntimeBean runtime)
    {
        runtime.getRenderOps().setCards(!runtime.getRenderOps().isCards());
        runtime.firePropertyChange("renderOps", null, runtime.getRenderOps());
        setProp("app.ops.cards", String.valueOf(runtime.getRenderOps().isCards()));
    }

    public static void toggleShowReprompt(RuntimeBean runtime)
    {
        runtime.getRenderOps().setReprompt(!runtime.getRenderOps().isReprompt());
        runtime.firePropertyChange("renderOps", null, runtime.getRenderOps());
        setProp("app.ops.reprompt", String.valueOf(runtime.getRenderOps().isReprompt()));
    }

    public static void toggleShowVerbose(RuntimeBean runtime)
    {
        runtime.getRenderOps().setVerbose(!runtime.getRenderOps().isVerbose());
        runtime.firePropertyChange("renderOps", null, runtime.getRenderOps());
        setProp("app.ops.verbose", String.valueOf(runtime.getRenderOps().isVerbose()));
    }

    public static void loadHistory(RuntimeBean runtime, File source) throws IOException
    {
        InputStream is = new FileInputStream(source);
        Reader rdr = new InputStreamReader(is, "utf-8");
        JSONArray jtranss;
        try
        {
            jtranss = (JSONArray)(mParser.parse(rdr));
        }
        catch (ParseException e)
        {
            throw new IOException(e);
        }
        rdr.close();
        List<TransactionBean> transs = FromJSONLogic.fromJSONTransactions(jtranss, runtime.getApp());
        runtime.setHistory(transs);
        runtime.firePropertyChange("history", null, runtime.getHistory());
        setProp("app.history", source.toString());
    }

    public static void saveHistory(RuntimeBean runtime, File source) throws IOException
    {
        JSONArray transs = ToJSONLogic.toJSONTransactions(runtime.getHistory());
        OutputStream os = new FileOutputStream(source);
        Writer wtr = new OutputStreamWriter(os, "utf-8");
        wtr.write(transs.toJSONString());
        wtr.close();
        setProp("app.history", source.toString());
    }
    
    private static void saveMRUs(RuntimeBean runtime)
    {
        JSONArray jspecs = ToJSONLogic.toJSONAppSpecs(runtime.getMRUs());
        setProp("app.mrus", jspecs.toJSONString());
    }
    
    public static void addMRU(RuntimeBean runtime, String name)
    {
        AppSpecBean spec = makeMRU(runtime, name);
        runtime.getMRUs().add(spec);
        runtime.firePropertyChange("mrus", null, runtime.getMRUs());
        saveMRUs(runtime);
    }
    
    public static void removeMRU(RuntimeBean runtime, AppSpecBean spec)
    {
        runtime.getMRUs().remove(spec);
        runtime.firePropertyChange("mrus", null, runtime.getMRUs());
        saveMRUs(runtime);
    }
    
    public static void selectMRU(RuntimeBean runtime, AppSpecBean spec) throws IOException, URISyntaxException
    {
        setEndpoint(runtime, spec.getEndpoint());
        setApplicationID(runtime, spec.getApplicationID());
        setUserID(runtime, spec.getUserID());
        readIntents(runtime, new URI(spec.getIntentURI()));
        readUtterances(runtime, new URI(spec.getUtteranceURI()));
    }

    private static AppSpecBean makeMRU(RuntimeBean runtime, String name)
    {
        AppSpecBean spec = new AppSpecBean();
        spec.setName(name);
        spec.setEndpoint(runtime.getApp().getEndpoint());
        spec.setApplicationID(runtime.getApp().getApplicationID());
        spec.setUserID(runtime.getApp().getUserID());
        spec.setIntentURI(getProp("app.intents"));
        spec.setUtteranceURI(getProp("app.utterances"));
        return spec;
    }
    
    public static void copySpec(RuntimeBean runtime)
    {
        AppSpecBean spec = makeMRU(runtime, null);
        JSONObject jspec = ToJSONLogic.toJSON(spec);
        StringSelection ss = new StringSelection(jspec.toJSONString());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(ss, mClipOwner);
    }
    
    public static void pasteSpec(RuntimeBean runtime) throws IOException, URISyntaxException, UnsupportedFlavorException, ParseException
    {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);
        if ((contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor))
        {
            String json = (String)contents.getTransferData(DataFlavor.stringFlavor);
            JSONObject jspec = (JSONObject)mParser.parse(json);
            AppSpecBean spec = FromJSONLogic.fromJSONAppSpec(jspec);
            selectMRU(runtime, spec);
        }
    }
}
