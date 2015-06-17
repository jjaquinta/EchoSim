package jo.alexa.sim.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.logic.RuntimeLogic;

public class DisclaimerPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    private JTextPane   mClient;
    private JButton     mOK;
    private JButton     mCancel;

    public DisclaimerPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mClient = new JTextPane();
        mClient.setContentType("text/html");
        mClient.setText(DISCLAIMER);
        mClient.setEditable(false);
        mOK = new JButton("OK");
        mCancel = new JButton("Cancel");
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", mClient);
        JPanel bottom = new JPanel();
        bottom.setLayout(new GridLayout(1, 2));
        bottom.add("West", mOK);
        bottom.add("East", mCancel);
        add("South", bottom);
    }

    private void initLink()
    {
        mOK.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doOK();
            }
        });
        mCancel.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doCancel();
            }
        });
    }
    
    private void doOK()
    {
        RuntimeLogic.acceptLicense(mRuntime);
    }

    private void doCancel()
    {
        RuntimeLogic.rejectLicense(mRuntime);
    }

    private static final String DISCLAIMER = "<HTML>"
            + "<BODY>"
            + "<H1>TsaTsaTzu Echo Simulator</H1>"
            + "<P>The product is supplied without any warranty or guarantee of behavior or fitness. "
            + "It is not produced by Amazon. "
            + "Echo, Alexa, Amazon and other terms are all trademarked by Amazon. "
            + "They are used here without permission. "
            + "There use here is not intended to produce any challange to the ownership of those terms. "
            + "Yadda yadda yadda. </P>"
            + "<P>If you accept these terms, please press OK below. "
            + "Otherwise press \"Cancel\" and have a nice day.</P>"            
            + "</BODY>"
            + "</HTML>";
}
