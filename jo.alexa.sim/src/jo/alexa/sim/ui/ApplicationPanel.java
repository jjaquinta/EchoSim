package jo.alexa.sim.ui;

import java.awt.Container;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import jo.alexa.sim.data.IntentBean;
import jo.alexa.sim.data.RuntimeBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.logic.RuntimeLogic;
import jo.alexa.sim.logic.UtteranceLogic;

public class ApplicationPanel extends JPanel
{
    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean mRuntime;
    private DefaultMutableTreeNode  mRoot;
    
    private JTextField  mEndpoint;
    private JButton     mIntentLoadFile;
    private JButton     mIntentLoadURL;
    private JTree       mIntents;
    private JButton     mUtteranceLoadFile;
    private JButton     mUtteranceLoadURL;
    private JTextPane   mUtterances;
    
    public ApplicationPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
        doNewApplication();
    }

    private void initInstantiate()
    {
        mEndpoint = new JTextField(40);
        mIntentLoadFile = new JButton("Load File...");
        mIntentLoadURL = new JButton("Load URL...");
        mRoot = new DefaultMutableTreeNode("Intents");
        mIntents = new JTree(mRoot);
        mUtteranceLoadFile = new JButton("Load File...");
        mUtteranceLoadURL = new JButton("Load URL...");
        mUtterances = new JTextPane();
        mUtterances.setContentType("text/html");
        mUtterances.setEditable(false);
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,1", new JLabel("Endpoint:"));
        add("+,. 2x1 fill=h", mEndpoint);
        add("1,+ anchor=nw", new JLabel("Intent Schema:"));
        add("+,.", mIntentLoadFile);
        add("+,.", mIntentLoadURL);
        add("1,+ 3x1 fill=hv weighty=2", new JScrollPane(mIntents));
        add("1,+ anchor=nw", new JLabel("Utterances:"));
        add("+,.", mUtteranceLoadFile);
        add("+,.", mUtteranceLoadURL);
        add("1,+ 3x1 fill=hv weighty=2", new JScrollPane(mUtterances));
    }

    private void initLink()
    {
        mEndpoint.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e)
            {
                doDeclareNewEndpoint();
            }
        });
        mIntentLoadFile.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doIntentLoadFile();
            }
        });
        mIntentLoadURL.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doIntentLoadURL();
            }
        });
        mUtteranceLoadFile.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doUtteranceLoadFile();
            }
        });
        mUtteranceLoadURL.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doUtteranceLoadURL();
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
    
    private Frame getFrame()
    {
        for (Container c = getParent(); c != null; c = c.getParent())
            if (c instanceof Frame)
                return (Frame)c;
        return null;
    }
    
    protected void doIntentLoadFile()
    {
        FileDialog fd = new FileDialog(getFrame(), "Load Intent File", FileDialog.LOAD);
        //fd.setDirectory(lastFile);
        //fd.setFile(lastFile);
        fd.setVisible(true);
        String intentFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((intentFile == null) || (intentFile.length() == 0))
            return;
        try
        {
            RuntimeLogic.readIntents(mRuntime, (new File(intentFile)).toURI());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+intentFile, JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void doIntentLoadURL()
    {
        String intentURL = JOptionPane.showInputDialog(this, "Enter in URL to Intent File", "Load Intent File", JOptionPane.QUESTION_MESSAGE);
        if ((intentURL == null) || (intentURL.length() == 0))
            return;
        try
        {
            RuntimeLogic.readIntents(mRuntime, new URI(intentURL));
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+intentURL, JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void doUtteranceLoadFile()
    {
        FileDialog fd = new FileDialog(getFrame(), "Load Utterance File", FileDialog.LOAD);
        //fd.setDirectory(lastFile);
        //fd.setFile(lastFile);
        fd.setVisible(true);
        String intentFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((intentFile == null) || (intentFile.length() == 0))
            return;
        try
        {
            RuntimeLogic.readUtterances(mRuntime, (new File(intentFile)).toURI());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+intentFile, JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void doUtteranceLoadURL()
    {
        String intentURL = JOptionPane.showInputDialog(this, "Enter in URL to Utterance File", "Load Utterance File", JOptionPane.QUESTION_MESSAGE);
        if ((intentURL == null) || (intentURL.length() == 0))
            return;
        try
        {
            RuntimeLogic.readUtterances(mRuntime, new URI(intentURL));
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+intentURL, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDeclareNewEndpoint()
    {
        RuntimeLogic.setEndpoint(mRuntime, mEndpoint.getText());
    }
    
    private void doNewApplication()
    {
        if (mRuntime.getApp().getEndpoint() == null)
            mEndpoint.setText("");
        else
            if (!mRuntime.getApp().getEndpoint().equals(mEndpoint.getText()))
                mEndpoint.setText(mRuntime.getApp().getEndpoint());
        mRoot.removeAllChildren();
        if (mRuntime.getApp().getSchema() != null)
            for (IntentBean intent : mRuntime.getApp().getSchema().getIntents())
            {
                DefaultMutableTreeNode i = new DefaultMutableTreeNode(intent);
                mRoot.add(i);
                for (SlotBean slot : intent.getSlots())
                {
                    DefaultMutableTreeNode s = new DefaultMutableTreeNode(slot);
                    i.add(s);
                }
            }
        DefaultTreeModel m = (DefaultTreeModel)mIntents.getModel();
        m.reload();
        mIntents.expandRow(0);
        mUtterances.setText("<html><body>"+UtteranceLogic.renderAsHTML(mRuntime.getApp().getUtterances())+"</body></html>");
    }
}
