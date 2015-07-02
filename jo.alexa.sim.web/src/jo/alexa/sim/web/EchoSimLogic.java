package jo.alexa.sim.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.ResponseBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.logic.ApplicationLogic;
import jo.alexa.sim.logic.MatchLogic;
import jo.alexa.sim.logic.RequestLogic;
import jo.alexa.sim.logic.UtteranceLogic;
import jo.alexa.sim.ui.data.TransactionBean;

public class EchoSimLogic
{
    private static final Map<String, ApplicationBean> mApplications = new HashMap<String, ApplicationBean>();
    
    public static TransactionBean invoke(RequestData request)
    {
        TransactionBean trans = null;
        ApplicationBean app;
        try
        {
            app = getApplication(request);
            if ((request.getText() != null) && (request.getText().trim().length() > 0))
                trans = converse(app, request.getText());
            else
                trans = startSession(app);
        }
        catch (IOException e)
        {
            trans = new TransactionBean();
            trans.setRequestType(RequestLogic.INTENT_REQUEST);
            trans.setInputText(request.getText());
            trans.setError(e);
        }
        return trans;
    }

    private static TransactionBean startSession(ApplicationBean app)
    {
        TransactionBean trans = new TransactionBean();
        trans.setRequestType(RequestLogic.LAUNCH_REQUEST);
        try
        {
            trans.setTransactionStart(System.currentTimeMillis());
            ResponseBean response = RequestLogic.performLaunchRequest(app);
            trans.setTransactionEnd(System.currentTimeMillis());
            trans.setOutputData(response);
            trans.setOutputText(response.getOutputSpeechText());
        }
        catch (IOException e)
        {
            trans.setError(e);
        }
        return trans;
    }

    private static TransactionBean converse(ApplicationBean app, String text)
    {
        TransactionBean trans = new TransactionBean();
        trans.setRequestType(RequestLogic.INTENT_REQUEST);
        trans.setInputText(text);
        List<MatchBean> matches =  MatchLogic.parseInput(app, text);
        if (matches.size() == 0)
        {
            trans.setError(new IllegalArgumentException("No suitable parse for input: '"+text+"'"));
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
                ResponseBean response = RequestLogic.performIntentRequest(app, intentName, slotValues);
                trans.setTransactionEnd(System.currentTimeMillis());
                trans.setOutputData(response);
                trans.setOutputText(response.getOutputSpeechText());
            }
            catch (IOException e)
            {
                trans.setError(e);
            }
        }
        return trans;
    }
    
    private static ApplicationBean getApplication(RequestData request) throws IOException
    {
        String key = request.getEndpoint().toLowerCase()+"$"+request.getIntents().toLowerCase()+"$"+request.getUtterances().toLowerCase();
        ApplicationBean app = mApplications.get(key);
        if (app == null)
        {
            app = new ApplicationBean();
            app.setEndpoint(request.getEndpoint());
            InputStream intentsStream = (new URL(request.getIntents())).openStream();
            InputStreamReader intentsReader = new InputStreamReader(intentsStream);
            ApplicationLogic.readIntents(app, intentsReader);
            intentsReader.close();
            InputStream utterancesStream = (new URL(request.getUtterances())).openStream();
            InputStreamReader utterancesReader = new InputStreamReader(utterancesStream);
            UtteranceLogic.read(app, utterancesReader);
            utterancesReader.close();
            mApplications.put(key, app);
        }
        if (request.getAppID() != null)
            app.setApplicationID(request.getAppID());
        if (request.getUserID() != null)
            app.setUserID(request.getUserID());
        return app;
    }
}
