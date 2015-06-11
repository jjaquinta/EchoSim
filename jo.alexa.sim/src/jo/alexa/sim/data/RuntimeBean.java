package jo.alexa.sim.data;

import java.util.ArrayList;
import java.util.List;

public class RuntimeBean extends PCSBean
{
    private boolean         mDisclaimerAccepted;
    private ApplicationBean mApp;
    private List<TransactionBean>   mHistory;

    public RuntimeBean()
    {
        mApp = new ApplicationBean();
        mHistory = new ArrayList<TransactionBean>();
    }
    
    public ApplicationBean getApp()
    {
        return mApp;
    }

    public void setApp(ApplicationBean app)
    {
        ApplicationBean _app = mApp;
        mApp = app;
        mPCS.firePropertyChange("app", _app, mApp);
    }

    public boolean isDisclaimerAccepted()
    {
        return mDisclaimerAccepted;
    }

    public void setDisclaimerAccepted(boolean disclaimerAccepted)
    {
        boolean _disclaimerAccepted = mDisclaimerAccepted;
        mDisclaimerAccepted = disclaimerAccepted;
        mPCS.firePropertyChange("disclaimerAccepted", _disclaimerAccepted, mDisclaimerAccepted);
    }

    public List<TransactionBean> getHistory()
    {
        return mHistory;
    }

    public void setHistory(List<TransactionBean> history)
    {
        mHistory = history;
    }
}
