package jo.alexa.sim.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.data.RuntimeBean;
import jo.alexa.sim.logic.MatchLogic;
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
    private JButton     mClear;
    private JTextField  mInput;
    private JTextPane   mTranscript;
    private JTextField  mIntent;
    
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
        mSend.setToolTipText("Send an IntentReqeust to the app");
        mClear = new JButton("Clear");
        mClear.setToolTipText("Clear history");
        mStartSession = new JButton("\u25b6");
        mStartSession.setToolTipText("Send a LaunchReqeust to the app");
        mEndSession = new JButton("\u25a0");
        mEndSession.setToolTipText("Send a SessionEndedReqeust to the app");
        mInput = new JTextField(40);
        mIntent = new JTextField(40);
        mIntent.setEditable(false);
        mIntent.setToolTipText("This is the intent your text will be translated into");
        mTranscript = new JTextPane();
        mTranscript.setContentType("text/html");
        mTranscript.setEditable(false);
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(mTranscript));
        JPanel inputBar = new JPanel();
        add("South", inputBar);
        inputBar.setLayout(new TableLayout());
        inputBar.add("1,1", new JLabel("Say:"));
        inputBar.add("+,. fill=h", mInput);
        inputBar.add("+,.", mSend);
        inputBar.add("+,.", mStartSession);
        inputBar.add("+,.", mEndSession);
        inputBar.add("+,.", mClear);
        inputBar.add("1,+", new JLabel(""));
        inputBar.add("+,. fill=h", mIntent);
        inputBar.add("+,. 4x1", new JLabel(""));
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
        mClear.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doClear();
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
        mInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent arg0)
            {
                doInputUpdate();
            }
        });
        mInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSend();
            }
        });
        mRuntime.addPropertyChangeListener(this);
        mRuntime.getApp().addPropertyChangeListener(this);
    }

    private void doSend()
    {
        RuntimeLogic.send(mRuntime, mInput.getText());
        mInput.setText("");
        mInput.requestFocus();
    }

    private void doClear()
    {
        RuntimeLogic.clearHistory(mRuntime);
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
            mEndSession.setEnabled(false);
        }
        else
        {
            mStartSession.setEnabled(false);
            mEndSession.setEnabled(true);
        }
        doInputUpdate();
    }
    
    private void doInputUpdate()
    {
        String txt = mInput.getText();
        List<MatchBean> matches = MatchLogic.parseInput(mRuntime.getApp(), txt);
        if (matches.size() == 0)
            mIntent.setText("");
        else
            mIntent.setText(matches.get(0).toString());
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
