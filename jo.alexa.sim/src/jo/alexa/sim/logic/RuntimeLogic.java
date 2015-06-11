package jo.alexa.sim.logic;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jo.alexa.sim.data.RuntimeBean;

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
}
