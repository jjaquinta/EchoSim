package jo.alexa.sim.web;

public class RequestData
{
    private String mEndpoint;
    private String mIntents;
    private String mUtterances;
    private String mUserID;
    private String mAppID;
    private String mText;
    
    public String getEndpoint()
    {
        return mEndpoint;
    }
    public void setEndpoint(String endpoint)
    {
        mEndpoint = endpoint;
    }
    public String getIntents()
    {
        return mIntents;
    }
    public void setIntents(String intents)
    {
        mIntents = intents;
    }
    public String getUtterances()
    {
        return mUtterances;
    }
    public void setUtterances(String utterances)
    {
        mUtterances = utterances;
    }
    public String getUserID()
    {
        return mUserID;
    }
    public void setUserID(String userID)
    {
        mUserID = userID;
    }
    public String getAppID()
    {
        return mAppID;
    }
    public void setAppID(String appID)
    {
        mAppID = appID;
    }
    public String getText()
    {
        return mText;
    }
    public void setText(String text)
    {
        mText = text;
    }
}
