package jo.alexa.sim.logic;

import java.util.List;

import jo.alexa.sim.data.TransactionBean;

public class TransactionLogic
{
    public static String renderAsHTML(TransactionBean trans)
    {
        StringBuffer html = new StringBuffer();
        html.append("<p>");
        html.append("<span style=\"color: blue\">");
        html.append(trans.getInputText());
        html.append("</span>");
        html.append("<br/>");
        html.append("<span style=\"color: green\">");
        html.append(trans.getOutputText());
        html.append("</span>");
        html.append("</p>");
        return html.toString();
    }
    
    public static String renderAsHTML(List<TransactionBean> transs)
    {
        StringBuffer html = new StringBuffer();
        for (TransactionBean trans : transs)
            html.append(renderAsHTML(trans));
        return html.toString();
    }
}
