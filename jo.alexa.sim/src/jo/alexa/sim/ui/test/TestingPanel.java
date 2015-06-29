package jo.alexa.sim.ui.test;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import jo.alexa.sim.data.MatchBean;
import jo.alexa.sim.logic.MatchLogic;
import jo.alexa.sim.ui.TableLayout;
import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.logic.RuntimeLogic;
import jo.alexa.sim.ui.logic.TransactionLogic;

public class TestingPanel extends JPanel implements PropertyChangeListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    private JCheckBox   mShowInput;
    private JCheckBox   mShowIntent;
    private JCheckBox   mShowOutput;
    private JCheckBox   mShowError;
    private JCheckBox   mShowCards;
    private JCheckBox   mShowReprompt;
    private JCheckBox   mShowVerbose;
    private JButton     mSend;
    private JButton     mStartSession;
    private JButton     mEndSession;
    private JButton     mClear;
    private JButton     mSave;
    private JButton     mLoad;
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
        doNewOpts();
    }

    private void initInstantiate()
    {
        mShowInput = new JCheckBox("Inputs");
        mShowIntent = new JCheckBox("Intents");
        mShowOutput = new JCheckBox("Outputs");
        mShowError = new JCheckBox("Errors");
        mShowCards = new JCheckBox("Cards");
        mShowReprompt = new JCheckBox("Reprompt");
        mShowVerbose = new JCheckBox("Misc");
        mSend = new JButton("Send");
        mSend.setToolTipText("Send an IntentReqeust to the app");
        mClear = new JButton("Clear");
        mClear.setToolTipText("Clear history");
        mStartSession = new JButton("\u25b6");
        mStartSession.setToolTipText("Send a LaunchReqeust to the app");
        mEndSession = new JButton("\u25a0");
        mEndSession.setToolTipText("Send a SessionEndedReqeust to the app");
        mSave = new JButton("Save");
        mSave.setToolTipText("Save history");
        mLoad = new JButton("Load");
        mLoad.setToolTipText("Load history");
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
        inputBar.add("+,.", new JLabel(""));
        inputBar.add("+,.", mSave);
        inputBar.add("+,.", mLoad);
        inputBar.add("+,.", new JLabel(""));
        JPanel optionBar = new JPanel();
        add("North", optionBar);
        optionBar.setLayout(new FlowLayout());
        optionBar.add(mShowInput);
        optionBar.add(mShowIntent);
        optionBar.add(mShowOutput);
        optionBar.add(mShowError);
        optionBar.add(mShowCards);
        optionBar.add(mShowReprompt);
        optionBar.add(mShowVerbose);
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
        mShowInput.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RuntimeLogic.toggleShowInput(mRuntime);
            }
        });
        mShowIntent.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RuntimeLogic.toggleShowIntent(mRuntime);
            }
        });
        mShowOutput.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RuntimeLogic.toggleShowOutput(mRuntime);
            }
        });
        mShowError.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RuntimeLogic.toggleShowError(mRuntime);
            }
        });
        mShowCards.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RuntimeLogic.toggleShowCards(mRuntime);
            }
        });
        mShowReprompt.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RuntimeLogic.toggleShowReprompt(mRuntime);
            }
        });
        mShowVerbose.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                RuntimeLogic.toggleShowVerbose(mRuntime);
            }
        });
        mSave.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSave();
            }
        });
        mLoad.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doLoad();
            }
        });
        mRuntime.addPropertyChangeListener(this);
        mRuntime.getApp().addPropertyChangeListener(this);
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

    private void doSave()
    {
        FileDialog fd = new FileDialog(getFrame(), "Save History File", FileDialog.SAVE);
        fd.setDirectory(RuntimeLogic.getProp("history.file.dir"));
        fd.setFile(RuntimeLogic.getProp("history.file.file"));
        fd.setVisible(true);
        if (fd.getDirectory() == null)
            return;
        String historyFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((historyFile == null) || (historyFile.length() == 0))
            return;
        try
        {
            RuntimeLogic.saveHistory(mRuntime, new File(historyFile));
            RuntimeLogic.setProp("history.file.dir", fd.getDirectory());
            RuntimeLogic.setProp("history.file.file", fd.getFile());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+historyFile, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doLoad()
    {
        FileDialog fd = new FileDialog(getFrame(), "Load History File", FileDialog.LOAD);
        fd.setDirectory(RuntimeLogic.getProp("history.file.dir"));
        fd.setFile(RuntimeLogic.getProp("history.file.file"));
        fd.setVisible(true);
        if (fd.getDirectory() == null)
            return;
        String historyFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((historyFile == null) || (historyFile.length() == 0))
            return;
        try
        {
            RuntimeLogic.loadHistory(mRuntime, new File(historyFile));
            RuntimeLogic.setProp("history.file.dir", fd.getDirectory());
            RuntimeLogic.setProp("history.file.file", fd.getFile());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+historyFile, JOptionPane.ERROR_MESSAGE);
        }
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
    
    private void doNewOpts()
    {
        mShowInput.setSelected(mRuntime.getRenderOps().isInputText());
        mShowIntent.setSelected(mRuntime.getRenderOps().isIntents());
        mShowOutput.setSelected(mRuntime.getRenderOps().isOutputText());
        mShowError.setSelected(mRuntime.getRenderOps().isErrors());
        mShowCards.setSelected(mRuntime.getRenderOps().isCards());
        mShowReprompt.setSelected(mRuntime.getRenderOps().isReprompt());
        mShowVerbose.setSelected(mRuntime.getRenderOps().isVerbose());
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
        String html = "<html><body>" + TransactionLogic.renderAsHTML(mRuntime.getHistory(), mRuntime.getRenderOps()) + "</body></html>";
        mTranscript.setText(html);
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
            doNewOpts();
            doNewHistory();
        }
    }
    
    private Frame getFrame()
    {
        for (Container c = getParent(); c != null; c = c.getParent())
            if (c instanceof Frame)
                return (Frame)c;
        return null;
    }
}
