package jo.alexa.sim.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import jo.alexa.sim.data.RuntimeBean;
import jo.alexa.sim.logic.RuntimeLogic;
import jo.alexa.sim.logic.TransactionLogic;

public class TestingPanel extends JPanel implements PropertyChangeListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    private JButton     mSend;
    private JButton     mStartSession;
    private JButton     mEndSession;
    private JTextField  mInput;
    private JTextPane   mTranscript;
    
    public TestingPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
        doNewSessionID();
        doNewHistory();
    }

    private void initInstantiate()
    {
        mSend = new JButton("Send");
        mStartSession = new JButton("|>");
        mEndSession = new JButton("[]");
        mInput = new JTextField(40);
        mTranscript = new JTextPane();
        mTranscript.setContentType("text/html");
        mTranscript.setEditable(false);
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(mTranscript));
        JPanel bottom = new JPanel();
        add("South", bottom);
        bottom.setLayout(new BorderLayout());
        bottom.add("West", new JLabel("Say:"));
        bottom.add("Center", mInput);
        JPanel right = new JPanel();
        bottom.add("East", right);
        right.setLayout(new GridLayout(1, 3));
        right.add(mSend);
        right.add(mStartSession);
        right.add(mEndSession);
    }

    private void initLink()
    {
        mSend.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSend();
            }
        });
        mStartSession.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doStart();
            }
        });
        mEndSession.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doEnd();
            }
        });
        mRuntime.addPropertyChangeListener(this);
        mRuntime.getApp().addPropertyChangeListener(this);
    }

    private void doSend()
    {
        RuntimeLogic.send(mRuntime, mInput.getText());
    }
    
    private void doStart()
    {
        RuntimeLogic.startSession(mRuntime);
    }
    
    private void doEnd()
    {
        // TODO: update reason from UI
        RuntimeLogic.endSession(mRuntime, "USER_INITIATED");
    }
    
    private void doNewSessionID()
    {
        if (mRuntime.getApp().getSessionID() == null)
        {
            mStartSession.setEnabled(true);
            mSend.setEnabled(false);
            mEndSession.setEnabled(false);
        }
        else
        {
            mStartSession.setEnabled(false);
            mSend.setEnabled(true);
            mEndSession.setEnabled(true);
        }
    }
    
    private void doNewHistory()
    {
        String html = "<html><body>" + TransactionLogic.renderAsHTML(mRuntime.getHistory()) + "</body></html>";
        mTranscript.setText(html);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if ("sessionID".equals(evt.getPropertyName()))
            doNewSessionID();
        else if ("history".equals(evt.getPropertyName()))
            doNewHistory();
    }
}
