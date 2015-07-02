package jo.alexa.sim.web;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.logic.RequestLogic;
import jo.alexa.sim.ui.data.TransactionBean;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Servlet implementation class EchoSimServlet
 */
@WebServlet("/EchoSimServlet")
public class EchoSimServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static JSONParser mParser = new JSONParser();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EchoSimServlet() 
    {
        super();
        RequestLogic.disableCertificateValidation();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
	    JSONObject obj = null;
		if ("applicaiton/json".equals(request.getContentType()))
		{
		    try
            {
                obj = (JSONObject)mParser.parse(new InputStreamReader(request.getInputStream(), "utf-8"));
            }
            catch (ParseException e)
            {
            }
		}
        RequestData data = getRequestData(request, obj);
		TransactionBean trans = EchoSimLogic.invoke(data);
		String accept = request.getHeader("Accept");
		if ((accept == null) || (accept.indexOf("json") >= 0))
		    respondJSON(trans, response);
		else
		    respondText(trans, response);
	}

	private void respondJSON(TransactionBean trans, HttpServletResponse response) throws IOException
    {
        response.setContentType("application/json");
        JSONObject json = new JSONObject();
        if (trans.getError() != null)
            json.put("error", toString(trans.getError()));
        else
            json.put("error", "none");
        if (trans.getInputText() != null)
            json.put("inputText", trans.getInputText());
        if (trans.getInputMatch() != null)
        {
            JSONObject inputMatch = new JSONObject();
            json.put("inputMatch", inputMatch);
            inputMatch.put("intent", trans.getInputMatch().getIntent().getIntent());
            JSONObject slots = new JSONObject();
            inputMatch.put("slots", slots);
            for (SlotBean slot : trans.getInputMatch().getValues().keySet())
                slots.put(slot.getName(), trans.getInputMatch().getValues().get(slot));
        }
        if (trans.getOutputText() != null)
            json.put("outputText", trans.getOutputText());
        if (trans.getOutputData() != null)
        {
            if (trans.getOutputData().getCardContent() != null)
                json.put("cardContent", trans.getOutputData().getCardContent());
            if (trans.getOutputData().getCardTitle() != null)
                json.put("cardTitle", trans.getOutputData().getCardTitle());
            if (trans.getOutputData().getRepromptText() != null)
                json.put("reprompt", trans.getOutputData().getRepromptText());
        }
        byte[] outBytes = json.toJSONString().getBytes("utf-8");
        response.setContentLength(outBytes.length);
        response.getOutputStream().write(outBytes);
    }

    private void respondText(TransactionBean trans, HttpServletResponse response) throws IOException
    {
        response.setContentType("text/plain");
        StringBuffer out = new StringBuffer();
        if (trans.getError() != null)
            out.append("ERROR: "+toString(trans.getError()));
        else
            out.append(trans.getOutputText());
        byte[] outBytes = out.toString().getBytes("utf-8");
        response.setContentLength(outBytes.length);
        response.getOutputStream().write(outBytes);
    }

    private Object toString(Throwable error)
    {
        StringBuffer out = new StringBuffer();
        for (Throwable t = error; t != null; t = t.getCause())
        {
            out.append(t.toString());
            out.append("\n");
            for (StackTraceElement ele : t.getStackTrace())
                out.append("  "+ele.toString()+"\n");
        }                
        return out.toString();
    }

    private RequestData getRequestData(HttpServletRequest request, JSONObject obj)
	{
	    RequestData data = new RequestData();
	    data.setEndpoint(getDatum(request, obj, "endpoint", "http://echodevtestenv-65qiix3m3d.elasticbeanstalk.com/jose"));
        data.setIntents(getDatum(request, obj, "intents", "http://echodevtestenv-65qiix3m3d.elasticbeanstalk.com/jose?fetch=intents"));
        data.setUtterances(getDatum(request, obj, "utterances", "http://echodevtestenv-65qiix3m3d.elasticbeanstalk.com/jose?fetch=utterances"));
        data.setAppID(getDatum(request, obj, "appid", null));
        data.setUserID(getDatum(request, obj, "userid", request.getSession().getId()));
        data.setText(getDatum(request, obj, "text", null));
	    return data;
	}

    private String getDatum(HttpServletRequest request, JSONObject obj,
            String key, String def)
    {
        if ((obj != null) && obj.containsKey(key))
            return (String)obj.get(key);
        if (request.getParameterMap().containsKey(key))
            return request.getParameter(key);
        return def;
    }
}
