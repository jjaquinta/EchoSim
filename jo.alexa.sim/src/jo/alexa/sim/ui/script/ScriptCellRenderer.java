package jo.alexa.sim.ui.script;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import jo.alexa.sim.ui.data.ScriptTransactionBean;

public class ScriptCellRenderer implements
        ListCellRenderer<ScriptTransactionBean>
{

    @Override
    public Component getListCellRendererComponent(
            JList<? extends ScriptTransactionBean> list,
            ScriptTransactionBean value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        ScriptTransactionPanel panel = new ScriptTransactionPanel();
        panel.setTrans(value);
        panel.setIndex(index);
        panel.setSelected(isSelected);
        panel.setFocus(cellHasFocus);
        return panel;
    }

}
