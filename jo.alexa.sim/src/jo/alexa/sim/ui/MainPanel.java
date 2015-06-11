package jo.alexa.sim.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import jo.alexa.sim.data.RuntimeBean;

public class MainPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
 
    // tabs
    private ApplicationPanel    mApp;
    private ManualScriptPanel   mManScript;
    
    public MainPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mApp = new ApplicationPanel(mRuntime);
        mManScript = new ManualScriptPanel(mRuntime);
    }

    private void initLayout()
    {
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("App", mApp);
        tabs.add("Manual", mManScript);
        setLayout(new BorderLayout());
        add("Center", tabs);
    }

    private void initLink()
    {
    }
}
