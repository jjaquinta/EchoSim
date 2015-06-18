package jo.alexa.sim.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import jo.alexa.sim.logic.ManualScriptLogic;
import jo.alexa.sim.ui.data.RuntimeBean;

public class ManualScriptPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    
    private JButton     mGenerate;
    private JCheckBox   mExhaustive;
    private JSpinner    mNumberOfTests;
    private JCheckBox   mRandomize;
    private JCheckBox   mUseHistory;
    private JTextPane   mScript;
    
    public ManualScriptPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
        doNewApplication();
    }

    private void initInstantiate()
    {
        mGenerate = new JButton("Generate Script");
        mExhaustive = new JCheckBox("All Possible Test Cases");
        mExhaustive.setSelected(true);
        mNumberOfTests = new JSpinner();
        mNumberOfTests.setEnabled(false);
        mRandomize = new JCheckBox("Randomize Order");
        mUseHistory = new JCheckBox("Use History");
        mUseHistory.setToolTipText("Use currently logged history to generate 'expected' results");
        mScript = new JTextPane();
        mScript.setContentType("text/html");
        mScript.setEditable(false);
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,1 5x1 anchor=nw weighty=1", new JLabel("Options:"));
        add("1,+  weighty=1", mExhaustive);
        add("+,.", new JLabel("Select Test Cases:"));
        add("+,. ", mNumberOfTests);
        add("+,. ", mRandomize);
        add("+,. ", mUseHistory);
        add("1,+ 5x1 weighty=1", mGenerate);
        add("1,+ 5x1 fill=hv weighty=20", new JScrollPane(mScript));
    }

    private void initLink()
    {
        mExhaustive.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doExhaustiveToggled();
            }
        });
        mGenerate.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doGenerate();
            }
        });
        mRuntime.addPropertyChangeListener("app", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewApplication();
            }
        });
    }

    private void doExhaustiveToggled()
    {
        mNumberOfTests.setEnabled(!mExhaustive.isSelected());
    }
    
    private void doGenerate()
    {
        String html = ManualScriptLogic.generateScript(mRuntime.getApp(), 
                mExhaustive.isSelected() ? -1 : ((Number)mNumberOfTests.getValue()).intValue(), 
                mRandomize.isSelected(),
                mUseHistory.isSelected() ? mRuntime.getHistory() : null);
        mScript.setText("<html><body>"+html+"</body></html>");
    }
    
    private void doNewApplication()
    {
        if (mRuntime.getApp().getUtterances().size() > 0)
        {
            int numIntents = mRuntime.getApp().getUtterances().size();
            SpinnerModel sm = new SpinnerNumberModel(numIntents/2, 1, numIntents, Math.max(numIntents/10, 1));
            mNumberOfTests.setModel(sm);
        }
    }
}
