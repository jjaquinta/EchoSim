package jo.alexa.sim.logic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.ResponseBean;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RequestLogic
{
    private static final JSONParser mParser = new JSONParser();
    
    private static String makeRequest(ApplicationBean app, String request) throws IOException
    {
        byte[] body = request.getBytes("utf-8");
        URL serviceURL = new URL(app.getEndpoint());
        HttpsURLConnection con = (HttpsURLConnection)serviceURL.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Accept-Charset", "utf-8");
        con.setRequestProperty("Content-Length", String.valueOf(body.length));
        OutputStream os = con.getOutputStream();
        os.write(body);
        os.close();
        InputStream is = con.getInputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (;;)
        {
            int ch = is.read();
            if (ch == -1)
                break;
            baos.write(ch);
        }
        is.close();
        byte[] data = baos.toByteArray();
        return new String(data, "utf-8");
    }
    
    private static ResponseBean makeRequest(ApplicationBean app, JSONObject requestObject) throws IOException
    {
        String requestString = makeRequestBody(app, requestObject).toJSONString();
        String responseString = makeRequest(app, requestString);
        JSONObject responseObject;
        try
        {
            responseObject = (JSONObject)mParser.parse(responseString);
        }
        catch (ParseException e)
        {
            throw new IOException("Error parsing JSON response", e);
        }
        ResponseBean response = new ResponseBean();
        response.setVersion((String)responseObject.get("version"));
        JSONObject attributes = (JSONObject)responseObject.get("sessionAttributes");
        if (attributes != null)
            for (String key : attributes.keySet())
                app.getAttributes().put(key, attributes.get(key));
        JSONObject resp = (JSONObject)responseObject.get("response");
        JSONObject outputSpeech = (JSONObject)resp.get("outputSpeech");
        if (outputSpeech != null)
        {
            response.setOutputSpeechType((String)outputSpeech.get("type"));
            response.setOutputSpeechText((String)outputSpeech.get("text"));
        }
        JSONObject card = (JSONObject)resp.get("card");
        if (card != null)
        {
            response.setCardType((String)card.get("type"));
            response.setCardTitle((String)card.get("title"));
            response.setCardContent((String)card.get("content"));
        }
        JSONObject reprompt = (JSONObject)resp.get("reprompt");
        if (reprompt != null)
        {
            reprompt = (JSONObject)reprompt.get("outputSpeech");
            if (reprompt != null)
            {
                response.setRepromptType((String)reprompt.get("type"));
                response.setRepromptText((String)reprompt.get("text"));
            }
        }
        response.setShouldEndSession(Boolean.parseBoolean(resp.get("shouldEndSession").toString()));
        return response;
    }
    
    private static JSONObject makeSession(ApplicationBean app)
    {
        JSONObject session = new JSONObject();
        JSONObject attributes = new JSONObject();
        if (app.getSessionID() == null)
        {
            app.setSessionID("id"+System.currentTimeMillis());
            session.put("new", Boolean.TRUE);
            attributes.put("new", Boolean.TRUE);
        }
        else
        {
            session.put("new", Boolean.FALSE);            
        }
        session.put("sessionId", app.getSessionID());
        for (Object key : app.getAttributes().keySet())
            attributes.put((String)key, app.getAttributes().get(key));
        session.put("attributes", attributes);
        JSONObject application = new JSONObject();
        application.put("applicationId", app.getApplicationID());
        session.put("application", application);
        
        JSONObject user = new JSONObject();
        user.put("userId", app.getUserID());
        session.put("user", user);
        return session;
    }
    
    private static JSONObject makeRequestBody(ApplicationBean app, JSONObject request)
    {
        JSONObject requestBody = new JSONObject();
        requestBody.put("version", "1.0");
        requestBody.put("session", makeSession(app));
        requestBody.put("request", request);
        return requestBody;
    }

    private static JSONObject makeBaseRequest(String type)
    {
        JSONObject request = new JSONObject();
        request.put("type", type);
        request.put("requestId", "req"+System.currentTimeMillis());
        request.put("timestamp", (new Date()).toString());
        return request;
    }
    
    private static JSONObject makeLaunchRequest(ApplicationBean app)
    {
        JSONObject launchRequest = makeBaseRequest("LaunchRequest");
        return launchRequest;
    }
    
    private static JSONObject makeIntentRequest(ApplicationBean app, String intentName, Properties slotValues)
    {
        JSONObject intentRequest = makeBaseRequest("IntentRequest");
        JSONObject intent = new JSONObject();
        intent.put("name", intentName);
        JSONObject slots = new JSONObject();
        for (Object slotName : slotValues.keySet())
        {
            JSONObject slotValue = new JSONObject();
            slotValue.put("name", slotName);
            if (slotValues.get(slotName) != null)
                slotValue.put("value", slotValues.get(slotName));
            slots.put((String)slotName, slotValue);
        }
        intent.put("slots", slots);
        intentRequest.put("intent", intent);
        return intentRequest;
    }
    
    private static JSONObject makeSessionEndedRequest(ApplicationBean app, String reason)
    {
        JSONObject sessionEndedRequest = makeBaseRequest("SessionEndedRequest");
        sessionEndedRequest.put("reason", reason);
        return sessionEndedRequest;
    }
    
    public static ResponseBean performLaunchRequest(ApplicationBean app) throws IOException
    {
        return makeRequest(app, makeLaunchRequest(app));
    }
    
    public static ResponseBean performIntentRequest(ApplicationBean app, String intentName, Properties slotValues) throws IOException
    {
        return makeRequest(app, makeIntentRequest(app, intentName, slotValues));
    }
    
    public static ResponseBean performSessionEndedRequest(ApplicationBean app, String reason) throws IOException
    {
        return makeRequest(app, makeSessionEndedRequest(app, reason));
    }
}
