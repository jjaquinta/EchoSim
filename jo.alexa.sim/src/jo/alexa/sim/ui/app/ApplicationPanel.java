package jo.alexa.sim.ui.app;

import java.awt.Container;
import java.awt.FileDialog;
import java.awt.FlowLayout;
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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import jo.alexa.sim.data.IntentBean;
import jo.alexa.sim.data.SlotBean;
import jo.alexa.sim.data.UtteranceBean;
import jo.alexa.sim.ui.TableLayout;
import jo.alexa.sim.ui.data.AppSpecBean;
import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.logic.RuntimeLogic;

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
    private JList<String> mUtterances;
    private JComboBox<AppSpecBean>  mMRUs;
    private JButton                 mCopy;
    private JButton                 mPaste;
    
    public ApplicationPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
        doNewApplication();
        doNewMRUs();
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
        mUtterances = new JList<String>();
        mMRUs = new JComboBox<AppSpecBean>();
        mCopy = new JButton("Copy");
        mPaste = new JButton("Paste");
    }

    private void initLayout()
    {
        setLayout(new TableLayout());
        add("1,1", new JLabel("Applications:"));
        JPanel topBar = new JPanel();
        add(".,+ 2x1 fill=h", topBar);
        topBar.setLayout(new FlowLayout());
        topBar.add(mMRUs);
        topBar.add(mCopy);
        topBar.add(mPaste);
        add("1,+", new JLabel("Endpoint:"));
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
        mCopy.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doCopy();
            }
        });
        mPaste.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doPaste();
            }
        });
        mMRUs.addActionListener(new ActionListener() {            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                doMRU();
            }
        });
        mRuntime.addPropertyChangeListener("app", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewApplication();
            }
        });
        mRuntime.addPropertyChangeListener("mrus", new PropertyChangeListener() {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                doNewMRUs();
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
        fd.setDirectory(RuntimeLogic.getProp("intent.file.dir"));
        fd.setFile(RuntimeLogic.getProp("intent.file.file"));
        fd.setVisible(true);
        if (fd.getDirectory() == null)
            return;
        String intentFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((intentFile == null) || (intentFile.length() == 0))
            return;
        try
        {
            RuntimeLogic.readIntents(mRuntime, (new File(intentFile)).toURI());
            RuntimeLogic.setProp("intent.file.dir", fd.getDirectory());
            RuntimeLogic.setProp("intent.file.file", fd.getFile());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+intentFile, JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void doIntentLoadURL()
    {
        String intentURL = JOptionPane.showInputDialog(this, "Enter in URL to Intent File", RuntimeLogic.getProp("intent.url"));
        if ((intentURL == null) || (intentURL.length() == 0))
            return;
        try
        {
            RuntimeLogic.readIntents(mRuntime, new URI(intentURL));
            RuntimeLogic.setProp("intent.url", intentURL);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+intentURL, JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void doUtteranceLoadFile()
    {
        FileDialog fd = new FileDialog(getFrame(), "Load Utterance File", FileDialog.LOAD);
        fd.setDirectory(RuntimeLogic.getProp("utterance.file.dir"));
        fd.setFile(RuntimeLogic.getProp("utterance.file.file"));
        fd.setVisible(true);
        if (fd.getDirectory() == null)
            return;
        String intentFile = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
        if ((intentFile == null) || (intentFile.length() == 0))
            return;
        try
        {
            RuntimeLogic.readUtterances(mRuntime, (new File(intentFile)).toURI());
            RuntimeLogic.setProp("utterance.file.dir", fd.getDirectory());
            RuntimeLogic.setProp("utterance.file.file", fd.getFile());
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+intentFile, JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void doUtteranceLoadURL()
    {
        String intentURL = JOptionPane.showInputDialog(this, "Enter in URL to Utterance File", RuntimeLogic.getProp("utterance.url"));
        if ((intentURL == null) || (intentURL.length() == 0))
            return;
        try
        {
            RuntimeLogic.readUtterances(mRuntime, new URI(intentURL));
            RuntimeLogic.setProp("utterance.url", intentURL);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error reading "+intentURL, JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doCopy()
    {
        RuntimeLogic.copySpec(mRuntime);
    }

    private void doPaste()
    {
        try
        {
            RuntimeLogic.pasteSpec(mRuntime);
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error during paste", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void doDeclareNewEndpoint()
    {
        RuntimeLogic.setEndpoint(mRuntime, mEndpoint.getText());
    }

    private void doMRU()
    {
        AppSpecBean spec = (AppSpecBean)mMRUs.getSelectedItem();
        if ("$choose".equals(spec.getEndpoint()))
            return;
        if ("$add".equals(spec.getEndpoint()))
        {
            String name = JOptionPane.showInputDialog(this, "What do you want to name the preset?", "");
            if ((name != null) && (name.length() > 0))
                RuntimeLogic.addMRU(mRuntime, name);
        }
        else if ("$del".equals(spec.getEndpoint()))
        {
            AppSpecBean s = (AppSpecBean)JOptionPane.showInputDialog(this, "What preset to delete?", "Delete Preset", JOptionPane.QUESTION_MESSAGE, null,
                   mRuntime.getMRUs().toArray(), null);
            if (s != null)
                RuntimeLogic.removeMRU(mRuntime, s);
        }
        else
        {
            try
            {
                RuntimeLogic.selectMRU(mRuntime, spec);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(this, e.getLocalizedMessage(), "Error during loading", JOptionPane.ERROR_MESSAGE);
            }
            mMRUs.setSelectedIndex(0);
        }
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
        List<String> data = new ArrayList<String>();
        for (UtteranceBean u : mRuntime.getApp().getUtterances())
            data.add(u.toString());
        mUtterances.setListData(data.toArray(new String[0]));
        //mUtterances.setText("<html><body>"+UtteranceLogic.renderAsHTML(mRuntime.getApp().getUtterances())+"</body></html>");
    }
    
    private void doNewMRUs()
    {
        List<AppSpecBean> mrus = new ArrayList<AppSpecBean>();
        AppSpecBean choose = new AppSpecBean();
        choose.setName("Choose preset applications");
        choose.setEndpoint("$choose");
        mrus.add(choose);
        mrus.addAll(mRuntime.getMRUs());
        AppSpecBean add = new AppSpecBean();
        add.setName("Add current as preset");
        add.setEndpoint("$add");
        mrus.add(add);
        if (mRuntime.getMRUs().size() > 0)
        {
            AppSpecBean del = new AppSpecBean();
            del.setName("Delete a preset");
            del.setEndpoint("$del");
            mrus.add(del);
        }
        ComboBoxModel<AppSpecBean> aModel = new DefaultComboBoxModel<AppSpecBean>(mrus.toArray(new AppSpecBean[0]));         
        mMRUs.setModel(aModel);
        mMRUs.setSelectedIndex(0);
    }
}
