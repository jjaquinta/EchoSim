package jo.alexa.sim.ui.data;

import java.util.ArrayList;
import java.util.List;

import jo.alexa.sim.data.ApplicationBean;
import jo.alexa.sim.data.PCSBean;

public class RuntimeBean extends PCSBean
{
    private boolean         mDisclaimerAccepted;
    private ApplicationBean mApp;
    private List<TransactionBean>   mHistory;
    private TransactionRenderOpsBean    mRenderOps;

    public RuntimeBean()
    {
        mApp = new ApplicationBean();
        mHistory = new ArrayList<TransactionBean>();
        mRenderOps = new TransactionRenderOpsBean();
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

    public TransactionRenderOpsBean getRenderOps()
    {
        return mRenderOps;
    }

    public void setRenderOps(TransactionRenderOpsBean renderOps)
    {
        mRenderOps = renderOps;
    }
}
