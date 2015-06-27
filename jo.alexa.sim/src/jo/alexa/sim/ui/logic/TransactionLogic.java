package jo.alexa.sim.ui.logic;

import java.util.Date;
import java.util.List;

import jo.alexa.sim.logic.RequestLogic;
import jo.alexa.sim.ui.data.TransactionBean;
import jo.alexa.sim.ui.data.TransactionRenderOpsBean;

public class TransactionLogic
{
    public static String renderAsHTML(TransactionBean trans, TransactionRenderOpsBean ops)
    {
        StringBuffer html = new StringBuffer();
        html.append("<tr>");
        if (ops.isCards())
            html.append("<td valign=\"top\" width=\"75%\">");
        else
            html.append("<td>");
        if (ops.isInputText() && trans.getInputText() != null)
        {
            html.append("<span style=\"color: blue\">");
            if ((trans.getRequestType() != null) && trans.getRequestType().equals(RequestLogic.LAUNCH_REQUEST))
                html.append("&lt;launch session&gt;");
            else if ((trans.getRequestType() != null) && trans.getRequestType().equals(RequestLogic.SESSION_ENDED_REQUEST))
                html.append("&lt;session end&gt;");
            else
                html.append(trans.getInputText());
            html.append("</span>");
            html.append("<br/>");
        }
        if (ops.isIntents() && trans.getInputMatch() != null)
        {
            html.append("<span style=\"color: blue\">");
            html.append(trans.getInputMatch().toString());
            html.append("</span>");
            html.append("<br/>");
        }
        if (ops.isOutputText() && trans.getOutputText() != null)
        {
            html.append("<span style=\"color: green\">");
            html.append(trans.getOutputText());
            html.append("</span>");
            html.append("<br/>");
        }
        if (ops.isReprompt() && trans.getOutputData() != null && trans.getOutputData().getRepromptText() != null)
        {
            html.append("<span style=\"color: darkgreen\">");
            html.append(trans.getOutputData().getRepromptText());
            html.append("</span>");
            html.append("<br/>");
        }
        if (ops.isErrors() && trans.getError() != null)
        {
            html.append("<span style=\"color: red\">");           
            html.append("<b>"+trans.getError().getLocalizedMessage()+"</b><br/>");
            for (StackTraceElement elem : trans.getError().getStackTrace())
                html.append("&nbsp;&nbsp;"+elem.toString()+"<br/>");
            html.append("</span>");
            html.append("<br/>");
        }
        if (ops.isVerbose())
        {
            Date d = new Date(trans.getTransactionEnd());
            long e = trans.getTransactionEnd() - trans.getTransactionStart();
            html.append("<small>"+d+", "+e+"ms</small> ");
            if (trans.getOutputData().getOutputSpeechType() != null)
                html.append("<small>output type="+trans.getOutputData().getOutputSpeechType()+"</small> ");
            if (trans.getOutputData().getRepromptType() != null)
                html.append("<small>reprompt type="+trans.getOutputData().getRepromptType()+"</small> ");
        }
        html.append("</td>");
        if (ops.isCards())
        {
            html.append("<td valign=\"top\" width=\"25%\">");
            if (trans.getOutputData() != null)
            {
                if (trans.getOutputData().getCardTitle() != null)
                    html.append("<b>"+trans.getOutputData().getCardTitle()+"</b><br/>");
                if (trans.getOutputData().getCardContent() != null)
                    html.append("<i>"+trans.getOutputData().getCardContent()+"</i><br/>");
                if (ops.isVerbose() && (trans.getOutputData().getCardType() != null))
                    html.append("<small>type="+trans.getOutputData().getCardType()+"</small><br/>");
            }
            html.append("</td>");
        }
        html.append("</tr>");
        return html.toString();
    }
    
    public static String renderAsHTML(List<TransactionBean> transs, TransactionRenderOpsBean ops)
    {
        StringBuffer html = new StringBuffer();
        html.append("<table width=\"100%\">");
        for (TransactionBean trans : transs)
            html.append(renderAsHTML(trans, ops));
        html.append("</table>");
        return html.toString();
    }
}
