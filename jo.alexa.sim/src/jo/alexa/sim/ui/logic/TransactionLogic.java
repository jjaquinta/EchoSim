package jo.alexa.sim.ui.logic;

import java.util.Date;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import jo.alexa.sim.logic.RequestLogic;
import jo.alexa.sim.ui.data.TransactionBean;
import jo.alexa.sim.ui.data.TransactionRenderOpsBean;
import jo.util.utils.xml.XMLUtils;

public class TransactionLogic
{
    public static String renderAsHTML(TransactionBean trans, TransactionRenderOpsBean ops)
    {
        StringBuffer html = new StringBuffer();
        html.append("<tr>");
        if (ops.isCards())
            html.append("<td valign=\"top\" width=\"50%\">");
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
            html.append(renderTextAsHTML(trans.getOutputText()));
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
            if (trans.getOutputData() != null)
            {
                if (trans.getOutputData().getOutputSpeechType() != null)
                    html.append("<small>output type="+trans.getOutputData().getOutputSpeechType()+"</small> ");
                if (trans.getOutputData().getRepromptType() != null)
                    html.append("<small>reprompt type="+trans.getOutputData().getRepromptType()+"</small> ");
            }
        }
        html.append("</td>");
        if (ops.isCards())
        {
            html.append("<td valign=\"top\" width=\"50%\">");
            html.append("<table width=\"100%\" border=\"1\"><tr><td>");
            if (trans.getOutputData() != null)
            {
                if (trans.getOutputData().getCardTitle() != null)
                    html.append("<b>"+trans.getOutputData().getCardTitle()+"</b><br/>");
                if (trans.getOutputData().getCardContent() != null)
                    html.append("<i>"+trans.getOutputData().getCardContent()+"</i><br/>");
                if (ops.isVerbose() && (trans.getOutputData().getCardType() != null))
                    html.append("<small>type="+trans.getOutputData().getCardType()+"</small><br/>");
            }
            html.append("</td></tr></table>");
            html.append("</td>");
        }
        html.append("</tr>");
        return html.toString();
    }
    
    public static String renderTextAsHTML(String outputText)
    {
        if (!outputText.startsWith("<speak>"))
                return outputText;
        Document doc = XMLUtils.readString(outputText);
        StringBuffer html = new StringBuffer();
        parseSSML(doc.getFirstChild(), html);
        return html.toString();
    }

    // Shuffling cards.  <audio src=\"https://s3.amazonaws.com/tsatsatzu-alexa/sound/blackjack/shuffle-cards-4.mp3\"/> I deal you  <audio src=\"https://s3.amazonaws.com/tsatsatzu-alexa/sound/blackjack/fwip.mp3\"/> Eight of Clubs,  and Three of Hearts. I deal me  <audio src=\"https://s3.amazonaws.com/tsatsatzu-alexa/sound/blackjack/fwip.mp3\"/> King of Diamonds,  and Three of Clubs. 
    private static void parseSSML(Node n, StringBuffer html)
    {
        if ("speak".equals(n.getNodeName()))
        {
            for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling())
                parseSSML(child, html);
        }
        else if ("#text".equals(n.getNodeName()))
            html.append(n.getNodeValue());
        else if ("audio".equals(n.getNodeName()))
        {
            String src = XMLUtils.getAttribute(n, "src");
            html.append("(<a href=\""+src+"\">sound</a>)");
        }
        else if ("break".equals(n.getNodeName()))
        {
            //String strength = XMLUtils.getAttribute(n, "strength");
            //String time = XMLUtils.getAttribute(n, "time");
            html.append("(pause)");
        }
        else if ("p".equals(n.getNodeName()))
        {
            html.append("<p>");
            for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling())
                parseSSML(child, html);
            html.append("</p>");
        }
        else if ("phoneme".equals(n.getNodeName()))
        {
            //String alphabet = XMLUtils.getAttribute(n, "alphabet");
            String ph = XMLUtils.getAttribute(n, "ph");
            if (ph != null)
                html.append("/"+ph+"/");
            html.append("(");
            for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling())
                parseSSML(child, html);
            html.append(")");
        }
        else if ("s".equals(n.getNodeName()))
        {
            for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling())
                parseSSML(child, html);
        }
        else if ("say-as".equals(n.getNodeName()))
        {
            String interpretas = XMLUtils.getAttribute(n, "interpret-as");
            String format = XMLUtils.getAttribute(n, "format");
            StringBuffer txt = new StringBuffer();
            for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling())
                parseSSML(child, txt);
            handleSayAs(html, interpretas, format, txt.toString());
        }
        else if ("w".equals(n.getNodeName()))
        {
            for (Node child = n.getFirstChild(); child != null; child = child.getNextSibling())
                parseSSML(child, html);
        }
        else
            System.err.println("Unhandled SSML node: "+n.getNodeName());
    }

    private static void handleSayAs(StringBuffer html, String interpretas,
            String format, String txt)
    {
        if ("characters".equals(interpretas) || "spell-out".equals(interpretas))
            for (char c : txt.toString().trim().toCharArray())
                html.append(" "+c);
        else if ("digits".equals(interpretas))
            for (char c : txt.toString().trim().toCharArray())
                switch (c)
                {
                    case '0':
                        html.append(" zero");
                        break;
                    case '1':
                        html.append(" one");
                        break;
                    case '2':
                        html.append(" two");
                        break;
                    case '3':
                        html.append(" three");
                        break;
                    case '4':
                        html.append(" four");
                        break;
                    case '5':
                        html.append(" five");
                        break;
                    case '6':
                        html.append(" six");
                        break;
                    case '7':
                        html.append(" seven");
                        break;
                    case '8':
                        html.append(" eight");
                        break;
                    case '9':
                        html.append(" nine");
                        break;
                    default:
                        html.append(" "+c);
                        break;
                }
        else
        {
            System.err.println("Unhandled SSML, interpretas="+interpretas+", format="+format+", txt="+txt);
            html.append(txt);
        }
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
