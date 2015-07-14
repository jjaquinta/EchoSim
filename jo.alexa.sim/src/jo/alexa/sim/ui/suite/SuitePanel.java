package jo.alexa.sim.ui.suite;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TestCaseBean;
import jo.alexa.sim.ui.logic.RuntimeLogic;
import jo.alexa.sim.ui.logic.SuiteLogic;

public class SuitePanel extends JPanel implements PropertyChangeListener
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    // status
    private JLabel      mName;
    private JLabel      mStatus;
    // commands
    private JButton     mRun;
    private JButton     mInsertCase;
    private JButton     mLoadDisk;
    private JButton     mSaveDisk;
    private JButton     mClearCases;
    // edits
    private JButton     mDelete;
    private JButton     mMoveUp;
    private JButton     mMoveDown;
    private JButton     mRename;
    private JButton     mClearResults;
    private JList<TestCaseBean> mSuite;
    
    public SuitePanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        // status
        mName = new JLabel();
        mName.setFont(mName.getFont().deriveFont(Font.BOLD));
        mStatus = new JLabel();
        // commands
        mRun = new JButton("Run");
        mRun.setToolTipText("Run current suite");
        mInsertCase = new JButton("Insert Case");
        mInsertCase.setToolTipText("Insert current test case into seleciton point of suite");
        mLoadDisk = new JButton("Load...");
        mLoadDisk.setToolTipText("Read a suite from the disk");
        mSaveDisk = new JButton("Save...");
        mSaveDisk.setToolTipText("Save suite to disk");
        mClearCases = new JButton("Clear Suite");
        mClearCases.setToolTipText("Delete all lines from suite");
        // edits
        mDelete = new JButton("Del");
        mDelete.setToolTipText("Delete selected case from suite");
        mMoveUp = new JButton("\u2191");
        mMoveUp.setToolTipText("Move selected cases up");
        mMoveDown = new JButton("\u2193");
        mMoveDown.setToolTipText("Move selected cases down");
        mRename = new JButton("Rename");
        mRename.setToolTipText("Rename suite");
        mClearResults = new JButton("Clear Results");
        mClearResults.setToolTipText("Clear results");
        mSuite = new JList<TestCaseBean>(new TestCaseModel(mRuntime));
        mSuite.setCellRenderer(new TestCaseCellRenderer());
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        add("Center", new JScrollPane(mSuite));
        JPanel topBar = new JPanel();
        topBar.setLayout(new GridLayout(2, 1));
        JPanel statusLine = new JPanel();
        topBar.add(statusLine);
        statusLine.setLayout(new BorderLayout());
        statusLine.add("Center", mStatus);
        statusLine.add("West", mName);
        JPanel commandBar = new JPanel();
        topBar.add(commandBar);
        add("North", topBar);
        commandBar.setLayout(new FlowLayout());
        commandBar.add(mRun);
        commandBar.add(mInsertCase);
        commandBar.add(mLoadDisk);
        commandBar.add(mSaveDisk);
        commandBar.add(mClearCases);
        JPanel editBar = new JPanel();
        add("South", editBar);
        editBar.setLayout(new FlowLayout());
        editBar.add(mDelete);
        editBar.add(mMoveUp);
        editBar.add(mMoveDown);
        editBar.add(mRename);
        commandBar.add(mClearResults);
    }

    private void initLink()
    {
        mRun.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doRun();
            }
        });
        mClearCases.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doClearCases();
            }
        });
        mClearResults.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doClearResults();
            }
        });
        mInsertCase.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doInsertCase();
            }
        });
        mLoadDisk.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doLoadDisk();
            }
        });
        mSaveDisk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doSaveDisk();
            }
        });
        mDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doDelete();
            }
        });
        mRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doRename();
            }
        });
        mMoveUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doMoveUp();
            }
        });
        mMoveDown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doMoveDown();
            }
        });
        mRuntime.addPropertyChangeListener(this);
        mRuntime.getApp().addPropertyChangeListener(this);
    }

    private void doRun()
    {
        SuiteLogic.runSuite(mRuntime);
    }

    private void doClearCases()
    {
        SuiteLogic.clearSuite(mRuntime);
    }

    private void doClearResults()
    {
        SuiteLogic.clearResults(mRuntime);
    }
    
    private void doInsertCase()
    {
        int idx = mSuite.getSelectedIndex();
        if (idx < 0)
            idx = 0;
        SuiteLogic.insertCurrentCase(mRuntime, idx);
    }
    
    private void doLoadDisk()
    {
        FileDialog fd = new FileDialog(getFrame(), "Load Test Suite", FileDialog.LOAD);
        fd.setDirectory(RuntimeLogic.getProp("suite.file.dir"));
        fd.setFile(RuntimeLogic.getProp("suite.file.file"));
        fd.setVisible(true);
        if (fd.getDirectory() == null)
            return;
        String historyFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((historyFile == null) || (historyFile.length() == 0))
            return;
        try
        {
            SuiteLogic.load(mRuntime, new File(historyFile));
            RuntimeLogic.setProp("suite.file.dir", fd.getDirectory());
            RuntimeLogic.setProp("suite.file.file", fd.getFile());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+historyFile, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doSaveDisk()
    {
        FileDialog fd = new FileDialog(getFrame(), "Save Suite", FileDialog.SAVE);
        fd.setDirectory(RuntimeLogic.getProp("suite.file.dir"));
        fd.setFile(RuntimeLogic.getProp("suite.file.file"));
        fd.setVisible(true);
        if (fd.getDirectory() == null)
            return;
        String historyFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((historyFile == null) || (historyFile.length() == 0))
            return;
        try
        {
            SuiteLogic.save(mRuntime, new File(historyFile));
            RuntimeLogic.setProp("suite.file.dir", fd.getDirectory());
            RuntimeLogic.setProp("suite.file.file", fd.getFile());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+historyFile, JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doDelete()
    {
        List<TestCaseBean> sel = mSuite.getSelectedValuesList();
        SuiteLogic.deleteCases(mRuntime, sel);
    }
    
    private void doRename()
    {
        String newValue = (String)JOptionPane.showInputDialog(this, "Enter in new suite name", "Suite Name", 
                JOptionPane.QUESTION_MESSAGE, null, null, mRuntime.getSuite().getName());
        if (newValue != null)
            SuiteLogic.setName(mRuntime, newValue);
    }
    
    private void doMoveUp()
    {
        SuiteLogic.moveUp(mRuntime, mSuite.getSelectedIndices());
    }
    
    private void doMoveDown()
    {
        SuiteLogic.moveDown(mRuntime, mSuite.getSelectedIndices());
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (mRuntime.isSuiteRunning())
            mStatus.setText("Suite running...");
        else
        {
            Boolean result = SuiteLogic.pass(mRuntime.getSuite());
            if (result == null)
                mStatus.setText("Indeterminate result");
            else if (result)
                mStatus.setText("Suite passed");
            else
                mStatus.setText("Suite failed");
        }
        mName.setText(mRuntime.getSuite().getName());
    }
    
    private Frame getFrame()
    {
        for (Container c = getParent(); c != null; c = c.getParent())
            if (c instanceof Frame)
                return (Frame)c;
        return null;
    }
}
