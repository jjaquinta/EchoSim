package jo.alexa.sim.data;

public class ResponseBean
{
    private String  mVersion;
    private String  mOutputSpeechType;
    private String  mOutputSpeechText;
    private String  mCardType;
    private String  mCardTitle;
    private String  mCardContent;
    private String  mRepromptType;
    private String  mRepromptText;
    private boolean mShouldEndSession;
    
    public String getVersion()
    {
        return mVersion;
    }
    public void setVersion(String version)
    {
        mVersion = version;
    }
    public String getOutputSpeechType()
    {
        return mOutputSpeechType;
    }
    public void setOutputSpeechType(String outputSpeechType)
    {
        mOutputSpeechType = outputSpeechType;
    }
    public String getOutputSpeechText()
    {
        return mOutputSpeechText;
    }
    public void setOutputSpeechText(String outputSpeechText)
    {
        mOutputSpeechText = outputSpeechText;
    }
    public String getCardType()
    {
        return mCardType;
    }
    public void setCardType(String cardType)
    {
        mCardType = cardType;
    }
    public String getCardTitle()
    {
        return mCardTitle;
    }
    public void setCardTitle(String cardTitle)
    {
        mCardTitle = cardTitle;
    }
    public String getCardContent()
    {
        return mCardContent;
    }
    public void setCardContent(String cardContent)
    {
        mCardContent = cardContent;
    }
    public String getRepromptType()
    {
        return mRepromptType;
    }
    public void setRepromptType(String repromptType)
    {
        mRepromptType = repromptType;
    }
    public String getRepromptText()
    {
        return mRepromptText;
    }
    public void setRepromptText(String repromptText)
    {
        mRepromptText = repromptText;
    }
    public boolean isShouldEndSession()
    {
        return mShouldEndSession;
    }
    public void setShouldEndSession(boolean shouldEndSession)
    {
        mShouldEndSession = shouldEndSession;
    }
}
