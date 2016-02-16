package jo.util.utils.xml;

public class EntityUtils
{
    // LATIN-1, A0-FF
    private static String[] LATIN1 = {
    "&nbsp;",
    "&iexcl;",
    "&cent;",
    "&pound;",
    "&curren;",
    "&yen;",
    "&brvbar;",
    "&sect;",
    "&uml;",
    "&copy;",
    "&ordf;",
    "&laquo;",
    "&not;",
    "&shy;",
    "&reg;",
    "&macr;",
    "&deg;",
    "&plusmn;",
    "&sup2;",
    "&sup3;",
    "&acute;",
    "&micro;",
    "&para;",
    "&middot;",
    "&cedil;",
    "&sup1;",
    "&ordm;",
    "&raquo;",
    "&frac14;",
    "&frac12;",
    "&frac34;",
    "&iquest;",
    "&Agrave;",
    "&Aacute;",
    "&Acirc;",
    "&Atilde;",
    "&Auml;",
    "&Aring;",
    "&AElig;",
    "&Ccedil;",
    "&Egrave;",
    "&Eacute;",
    "&Ecirc;",
    "&Euml;",
    "&Igrave;",
    "&Iacute;",
    "&Icirc;",
    "&Iuml;",
    "&ETH;",
    "&Ntilde;",
    "&Ograve;",
    "&Oacute;",
    "&Ocirc;",
    "&Otilde;",
    "&Ouml;",
    "&times;",
    "&Oslash;",
    "&Ugrave;",
    "&Uacute;",
    "&Ucirc;",
    "&Uuml;",
    "&Yacute;",
    "&THORN;",
    "&szlig;",
    "&agrave;",
    "&aacute;",
    "&acirc;",
    "&atilde;",
    "&auml;",
    "&aring;",
    "&aelig;",
    "&ccedil;",
    "&egrave;",
    "&eacute;",
    "&ecirc;",
    "&euml;",
    "&igrave;",
    "&iacute;",
    "&icirc;",
    "&iuml;",
    "&eth;",
    "&ntilde;",
    "&ograve;",
    "&oacute;",
    "&ocirc;",
    "&otilde;",
    "&ouml;",
    "&divide;",
    "&oslash;",
    "&ugrave;",
    "&uacute;",
    "&ucirc;",
    "&uuml;",
    "&yacute;",
    "&thorn;",
    "&yuml;",
    };

    public static String insertEntities(String txt, boolean latin1)
    {
        if (txt == null)
            return null;
        StringBuffer ret = new StringBuffer();
        char[] c = txt.toCharArray();
        for (int i = 0; i < c.length; i++)
            if (c[i] == '&')
                ret.append("&amp;");
            else if (c[i] == '<')
                ret.append("&lt;");
            else if (c[i] == '>')
                ret.append("&gt;");
            else if (c[i] == '\"')
                ret.append("&quot;");
            else if ((c[i] >= 0x20) && (c[i] < 0x80))
                ret.append(c[i]);
            else if (Character.isWhitespace(c[i]))
                ret.append(c[i]);
            else
            {
                if (latin1 && ((c[i] >= 0xa0) && (c[i] <= 0xff)))
                    ret.append(LATIN1[c[i] - 0xa0]);
                else
                {
                    ret.append("&#x");
                    ret.append(Integer.toHexString(c[i]));
                    ret.append(";");
                }
            }
        return ret.toString();
    }
    
    public static String removeEntities(String txt, boolean latin1)
    {
        if (txt == null)
            return null;
        txt = txt.replace("&amp;", "&");
        txt = txt.replace("&lt;", "<");
        txt = txt.replace("&gt;", ">");
        txt = txt.replace("&quot;", "\"");
        for (;;)
        {
            int o = txt.indexOf("&#");
            if (o < 0)
                break;
            int o2 = txt.indexOf(';', o);
            if (o2 < 0)
                break;
            String num = txt.substring(o + 2, o2);
            char ch;
            if (num.startsWith("x"))
                ch = (char)Integer.parseInt(num.substring(1), 16);
            else
                ch = (char)Integer.parseInt(num);
            txt = txt.substring(0, o) + String.valueOf(ch) + txt.substring(o2 + 1);
        }
        if (latin1)
        {
            for (int i = 0; i < LATIN1.length; i++)
            {
                for (;;)
                {
                    int o = txt.indexOf(LATIN1[i]);
                    if (o < 0)
                        break;
                    txt = txt.substring(0, o) + String.valueOf((char)(i + 0xa0)) + txt.substring(o + LATIN1[i].length());
                }
            }
        }
        return txt;
    }
}
