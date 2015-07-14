package jo.alexa.sim.ui.data;

public class ScriptTransactionBean extends TransactionBean
{
    public static final int MODE_MUST_MATCH = 0;
    public static final int MODE_CANT_MATCH = 1;
    public static final int MODE_DONT_CARE = 2;
    public static final int MODE_MUST_REGEX = 3;
    public static final int MODE_CANT_REGEX = 4;
    
    private int     mMatchMode;
    private TransactionBean mActualResult;
    
    public ScriptTransactionBean()
    {
        setMatchMode(MODE_MUST_MATCH);
    }

    public ScriptTransactionBean(TransactionBean trans)
    {
        super(trans);
        setMatchMode(MODE_MUST_MATCH);
    }
    public TransactionBean getActualResult()
    {
        return mActualResult;
    }
    public void setActualResult(TransactionBean actualResult)
    {
        mActualResult = actualResult;
    }

    public int getMatchMode()
    {
        return mMatchMode;
    }

    public void setMatchMode(int matchMode)
    {
        mMatchMode = matchMode;
    }
}
