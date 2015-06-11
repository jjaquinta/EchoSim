package jo.alexa.sim.data;

public class RuntimeBean extends PCSBean
{
    private boolean         mDisclaimerAccepted;
    private ApplicationBean mApp;

    public RuntimeBean()
    {
        mApp = new ApplicationBean();
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
}
