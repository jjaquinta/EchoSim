package jo.alexa.sim.logic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.ResponseBean;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RequestLogic
{
    public static final String LAUNCH_REQUEST = "LaunchRequest";
    public static final String INTENT_REQUEST = "IntentRequest";
    public static final String SESSION_ENDED_REQUEST = "SessionEndedRequest";

    private static final JSONParser mParser = new JSONParser();
    private static final DateFormat mISO8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
    static
    {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        mISO8601.setTimeZone(tz);
    }
    
    private static String makeRequest(ApplicationBean app, String request) throws IOException
    {
        byte[] body = request.getBytes("utf-8");
        URL serviceURL = new URL(app.getEndpoint());
        HttpURLConnection con = (HttpURLConnection)serviceURL.openConnection();
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
        System.out.println("Request: "+requestString);
        String responseString = null;
        try
        {
            responseString = makeRequest(app, requestString);
        }
        catch (IOException e)
        {
            app.setSessionID(null);
            e.printStackTrace();
            throw e;
        }
        System.out.println("Response: "+responseString);
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
        if (resp != null)
        {
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
            Boolean shouldEndSession = (Boolean)resp.get("shouldEndSession");
            if (shouldEndSession != null)
                response.setShouldEndSession(shouldEndSession);
            else
                response.setShouldEndSession(false);
            if (response.isShouldEndSession())
                app.setSessionID(null);
        }
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
        request.put("timestamp", getNow());
        return request;
    }
    
    private static JSONObject makeLaunchRequest(ApplicationBean app)
    {
        JSONObject launchRequest = makeBaseRequest(LAUNCH_REQUEST);
        return launchRequest;
    }
    
    private static JSONObject makeIntentRequest(ApplicationBean app, String intentName, Properties slotValues)
    {
        JSONObject intentRequest = makeBaseRequest(INTENT_REQUEST);
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
        JSONObject sessionEndedRequest = makeBaseRequest(SESSION_ENDED_REQUEST);
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
    
    private static String getNow()
    {
        String nowAsISO = mISO8601.format(new Date());
        return nowAsISO;
    }

    public static void disableCertificateValidation()
    {
     // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { 
            new X509TrustManager() {     
                public java.security.cert.X509Certificate[] getAcceptedIssuers() { 
                    return new X509Certificate[0];
                } 
                public void checkClientTrusted( 
                    java.security.cert.X509Certificate[] certs, String authType) {
                    } 
                public void checkServerTrusted( 
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            } 
        }; 

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL"); 
            sc.init(null, trustAllCerts, new java.security.SecureRandom()); 
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (GeneralSecurityException e) {
        } 
        // Now you can access an https URL without having the certificate in the truststore
    }
}
