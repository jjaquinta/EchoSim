package jo.alexa.sim.ui.data;

public class ScriptTransactionBean extends TransactionBean
{
    private boolean mExpectedResult;
    private TransactionBean mActualResult;
    
    public ScriptTransactionBean()
    {
        mExpectedResult = true;
    }

    public ScriptTransactionBean(TransactionBean trans)
    {
        super(trans);
        mExpectedResult = true;
    }
    
    public boolean isExpectedResult()
    {
        return mExpectedResult;
    }
    public void setExpectedResult(boolean expectedResult)
    {
        mExpectedResult = expectedResult;
    }
    public TransactionBean getActualResult()
    {
        return mActualResult;
    }
    public void setActualResult(TransactionBean actualResult)
    {
        mActualResult = actualResult;
    }
}
