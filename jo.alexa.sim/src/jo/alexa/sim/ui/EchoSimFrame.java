package jo.alexa.sim.ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import jo.alexa.sim.data.RuntimeBean;
import jo.alexa.sim.logic.RuntimeLogic;

public class EchoSimFrame extends JFrame
{
    /**
     * 
     */
    private static final long serialVersionUID = 4297397520186885053L;
    
    private RuntimeBean mRuntime;
    
    private EchoSimPanel    mClient;

    public EchoSimFrame()
    {
        super("Tsatsatzu - Echo Simulator");
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mRuntime = RuntimeLogic.newInstance();
        mClient = new EchoSimPanel(mRuntime);
    }

    private void initLayout()
    {
        getContentPane().add("Center", mClient);
        
    }

    private void initLink()
    {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e)
            {
                super.windowClosed(e);
                System.exit(0);
            }
        });
    }
}
