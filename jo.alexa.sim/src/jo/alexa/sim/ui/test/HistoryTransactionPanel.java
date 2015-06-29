package jo.alexa.sim.ui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.MatteBorder;

import jo.alexa.sim.logic.RequestLogic;
import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TransactionBean;
import jo.alexa.sim.ui.data.TransactionRenderOpsBean;

public class HistoryTransactionPanel extends JPanel implements PropertyChangeListener
{
    private static final Color DESELECTED = Color.WHITE;
    private static final Color SELECTED = Color.LIGHT_GRAY;

    /**
     * 
     */
    private static final long serialVersionUID = 2553292711822715257L;
 
    private RuntimeBean       mRuntime;
    private TransactionBean   mTrans;
    private int                     mIndex;
    private boolean                 mFocus;
    private boolean                 mSelected;
    
    private JLabel  mInputText;
    private JLabel  mInputIntents;
    private JLabel  mOutputText;
    private JLabel  mRepromptText;
    private JLabel  mErrorText;
    private JLabel  mMiscText;
    private JLabel  mCardTitle;
    private JLabel  mCardText;
    private JLabel  mCardType;
    private JPanel  mCardPanel;
    private JPanel  mTextPanel;
    
    public HistoryTransactionPanel(RuntimeBean runtime)
    {
        mRuntime = runtime;
        initInstantiate();
        initLayout();
        initLink();
    }

    private void initInstantiate()
    {
        mTextPanel = new JPanel();
        mCardPanel = new JPanel();
        mInputText = new JLabel();
        mInputText.setForeground(Color.blue);
        mInputIntents = new JLabel();
        mInputText.setForeground(Color.blue);
        mOutputText = new JLabel();
        mRepromptText = new JLabel();
        mErrorText = new JLabel();
        mMiscText = new JLabel();
        mCardTitle = new JLabel();
        mCardText = new JLabel();
        mCardType = new JLabel();
    }

    private void initLayout()
    {
        setLayout(new BorderLayout());
        mCardPanel.setLayout(new BorderLayout());
    }

    private void initLink()
    {
        mRuntime.addPropertyChangeListener("renderOps", this);
    }

    public TransactionBean getTrans()
    {
        return mTrans;
    }

    public void setTrans(TransactionBean trans)
    {
        mTrans = trans;
        updateData();
    }
    
    private void updateData()
    {        
        TransactionRenderOpsBean ops = mRuntime.getRenderOps();
        removeAll();
        mTextPanel.removeAll();
        mCardPanel.removeAll();
        int rows = 0;
        if (ops.isInputText() && mTrans.getInputText() != null)
        {
            mTextPanel.add(mInputText);
            if ((mTrans.getRequestType() != null) && mTrans.getRequestType().equals(RequestLogic.LAUNCH_REQUEST))
                mInputText.setText("<launch session>");
            else if ((mTrans.getRequestType() != null) && mTrans.getRequestType().equals(RequestLogic.SESSION_ENDED_REQUEST))
                mInputText.setText("<session end>");
            else
                mInputText.setText(mTrans.getInputText());
            rows++;
        }
        if (ops.isIntents() && mTrans.getInputMatch() != null)
        {
            mTextPanel.add(mInputIntents);
            mInputIntents.setText(mTrans.getInputMatch().toString());
            rows++;
        }
        if (ops.isOutputText() && mTrans.getOutputText() != null)
        {
            mTextPanel.add(mOutputText);
            mOutputText.setText(mTrans.getOutputText());
            rows++;
        }
        if (ops.isReprompt() && mTrans.getOutputData() != null && mTrans.getOutputData().getRepromptText() != null)
        {
            mTextPanel.add(mRepromptText);
            mRepromptText.setText(mTrans.getOutputData().getRepromptText());
            rows++;
        }
        if (ops.isErrors() && mTrans.getError() != null)
        {
            mTextPanel.add(mErrorText);
            mErrorText.setText(mTrans.getError().getLocalizedMessage());
            StringBuffer toolTip = new StringBuffer();
            for (StackTraceElement elem : mTrans.getError().getStackTrace())
                toolTip.append(elem.toString()+"\n");
            mErrorText.setToolTipText(toolTip.toString());
            rows++;
        }
        if (ops.isVerbose())
        {
            mTextPanel.add(mMiscText);
            StringBuffer txt = new StringBuffer();
            Date d = new Date(mTrans.getTransactionEnd());
            long e = mTrans.getTransactionEnd() - mTrans.getTransactionStart();
            txt.append(d+", "+e+"ms ");
            if (mTrans.getOutputData().getOutputSpeechType() != null)
                txt.append("output type="+mTrans.getOutputData().getOutputSpeechType()+" ");
            if (mTrans.getOutputData().getRepromptType() != null)
                txt.append("reprompt type="+mTrans.getOutputData().getRepromptType()+" ");
            mMiscText.setText(txt.toString());
            rows++;
        }
        if (rows > 0)
        {
            add("Center", mTextPanel);
            mTextPanel.setLayout(new GridLayout(rows, 1));            
        }
        if (ops.isCards() && (mTrans.getOutputData() != null))
        {
            add("East", mCardPanel);
            if (mTrans.getOutputData().getCardTitle() != null)
            {
                mCardPanel.add("North",  mCardTitle);
                mCardTitle.setText(mTrans.getOutputData().getCardTitle());
            }
            if (mTrans.getOutputData().getCardContent() != null)
            {
                mCardPanel.add("Center", mCardText);
                mCardText.setText(mTrans.getOutputData().getCardContent());
            }
            if (ops.isVerbose() && (mTrans.getOutputData().getCardType() != null))
            {
                mCardPanel.add("South",  mCardType);
                mCardType.setText("type="+mTrans.getOutputData().getCardType());
            }
        }
    }

    public int getIndex()
    {
        return mIndex;
    }

    public void setIndex(int index)
    {
        mIndex = index;
    }

    public boolean isFocus()
    {
        return mFocus;
    }

    public void setFocus(boolean focus)
    {
        mFocus = focus;
        setBorder(mFocus ? new MatteBorder(1, 5, 1, 1, Color.DARK_GRAY) : null);
    }

    public boolean isSelected()
    {
        return mSelected;
    }

    public void setSelected(boolean selected)
    {
        mSelected = selected;
        setBackground(mSelected ? SELECTED : DESELECTED);
        mCardPanel.setBackground(mSelected ? SELECTED : DESELECTED);
        mTextPanel.setBackground(mSelected ? SELECTED : DESELECTED);        
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if (getParent() == null)
        {
            mRuntime.removePropertyChangeListener("renderOps", this);
            return;
        }
        updateData();
    }
}
