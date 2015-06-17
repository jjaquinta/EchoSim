package jo.alexa.sim.ui.data;

public class TransactionRenderOpsBean
{
    private boolean mInputText;
    private boolean mIntents;
    private boolean mOutputText;
    private boolean mErrors;
    private boolean mCards;
    private boolean mReprompt;
    private boolean mVerbose;

    public TransactionRenderOpsBean()
    {
        mInputText = true;
        mIntents = false;
        mOutputText = true;
        mErrors = true;
        mCards = false;
        mReprompt = false;
        mVerbose = false;
    }
    
    public boolean isInputText()
    {
        return mInputText;
    }
    public void setInputText(boolean inputText)
    {
        mInputText = inputText;
    }
    public boolean isIntents()
    {
        return mIntents;
    }
    public void setIntents(boolean intents)
    {
        mIntents = intents;
    }
    public boolean isOutputText()
    {
        return mOutputText;
    }
    public void setOutputText(boolean outputText)
    {
        mOutputText = outputText;
    }
    public boolean isErrors()
    {
        return mErrors;
    }
    public void setErrors(boolean errors)
    {
        mErrors = errors;
    }

    public boolean isCards()
    {
        return mCards;
    }

    public void setCards(boolean cards)
    {
        mCards = cards;
    }

    public boolean isReprompt()
    {
        return mReprompt;
    }

    public void setReprompt(boolean reprompt)
    {
        mReprompt = reprompt;
    }

    public boolean isVerbose()
    {
        return mVerbose;
    }

    public void setVerbose(boolean verbose)
    {
        mVerbose = verbose;
    }
}
