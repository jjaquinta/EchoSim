package jo.alexa.sim.ui.natural;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import jo.alexa.sim.ui.TableLayout;
import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TransactionBean;
import jo.alexa.sim.ui.logic.RuntimeLogic;

public class NaturalPanel extends JPanel implements PropertyChangeListener
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
    private JList<TransactionBean> mTranscript;
    
    public NaturalPanel(RuntimeBean runtime)
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
        mTranscript = new JList<TransactionBean>(new HistoryModel(mRuntime));
        mTranscript.setCellRenderer(new HistoryCellRenderer(mRuntime));
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(mTranscript,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER));
        JPanel inputBar = new JPanel();
        add("South", inputBar);
        inputBar.setLayout(new TableLayout());
        inputBar.add("1,1", new JLabel("Say:"));
        inputBar.add("+,. fill=h", mInput);
        inputBar.add("+,.", mSend);
        inputBar.add("+,.", mStartSession);
        inputBar.add("+,.", mEndSession);
        inputBar.add("+,.", mClear);
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
        mInput.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSend();
            }
        });
        mTranscript.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent ev)
            {
                if (ev.getClickCount() == 2)
                {
                    TransactionBean trans = mTranscript.getSelectedValue();
                    if ((trans != null) && (trans.getInputText() != null))
                        mInput.setText(trans.getInputText());
                }
            }
        });
        mRuntime.addPropertyChangeListener(this);
        mRuntime.getApp().addPropertyChangeListener(this);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent ev)
            {
                mTranscript.setFixedCellWidth(NaturalPanel.this.getWidth());
                mTranscript.setFixedCellHeight(-1);
            }
        });
    }

    private void doSend()
    {
        RuntimeLogic.send(mRuntime, mInput.getText());
        mInput.selectAll();
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
    }
    
    private void doNewHistory()
    {
        //String html = "<html><body>" + TransactionLogic.renderAsHTML(mRuntime.getHistory(), mRuntime.getRenderOps()) + "</body></html>";
        //mTranscript.setText(html);
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if ("sessionID".equals(evt.getPropertyName()))
            doNewSessionID();
        else if ("history".equals(evt.getPropertyName()))
            doNewHistory();
        else if ("renderOps".equals(evt.getPropertyName()))
        {
            doNewHistory();
            mTranscript.repaint();
        }
    }
}
