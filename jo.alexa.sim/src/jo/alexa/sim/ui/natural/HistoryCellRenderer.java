package jo.alexa.sim.ui.natural;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.MatteBorder;

import jo.alexa.sim.ui.data.RuntimeBean;
import jo.alexa.sim.ui.data.TransactionBean;
import jo.alexa.sim.ui.data.TransactionRenderOpsBean;
import jo.alexa.sim.ui.logic.TransactionLogic;

public class HistoryCellRenderer implements
        ListCellRenderer<TransactionBean>
{
    public static final Color DESELECTED = Color.WHITE;
    public static final Color SELECTED = Color.LIGHT_GRAY;

    private RuntimeBean mRuntime;
    
    public HistoryCellRenderer(RuntimeBean runtime)
    {
        mRuntime = runtime;
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends TransactionBean> list,
            TransactionBean value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        JTextPane client = new JTextPane();
        client.setEditable(false);
        client.setContentType("text/html");
        TransactionRenderOpsBean ops = mRuntime.getRenderOps();
        String html = TransactionLogic.renderAsHTML(value, ops);
        int w = list.getFixedCellWidth();
        if (w < 0)
            w = 750;
        else
            w = w*3/4;
        html = "<html><body><table width=\""+w+"px\">"+html+"</table></body></html>";
        client.setText(html);
        if (cellHasFocus)
            client.setBorder(new MatteBorder(1, 5, 1, 1, Color.DARK_GRAY));
        client.setBackground(isSelected ? SELECTED : DESELECTED);
        return client;
    }

}
