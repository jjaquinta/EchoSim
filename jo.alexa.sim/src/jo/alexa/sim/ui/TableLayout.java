package jo.alexa.sim.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class TableLayout extends GridBagLayout
{
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 507791756710474413L;

    static final String[] defaults = {
        "gridx", "gridy", "gridwidth", "gridheight", "weightx", "weighty",
    };
    
    GridBagConstraints defaultGBC = new GridBagConstraints();
    int currentX;
    int currentY;
    
    public TableLayout()
    {
        super();
        defaultGBC.weightx = 1;
        defaultGBC.weighty = 1;
        currentX = 0;
        currentY = 0;
    }
    
    public TableLayout(String defaultConstraints)
    {
        super();
        java.util.StringTokenizer st = new java.util.StringTokenizer(defaultConstraints, " x,-");
        defaultGBC.weightx = 1;
        defaultGBC.weighty = 1;
        while (st.hasMoreTokens())
        {
            String tok = st.nextToken().toLowerCase();
//            int o;
//            String key;
//            String val;
            parseToken(defaultGBC, "", tok);
        }       
    }
    
    public void addLayoutComponent(Component comp, Object constraints)
    {
        if (!(constraints instanceof String))
        {
            super.addLayoutComponent(comp, constraints);
            return;
        }
        java.util.StringTokenizer st = new java.util.StringTokenizer((String)constraints, " x,-");
        GridBagConstraints gbc = new GridBagConstraints();
        copy(gbc, defaultGBC);
        gbc.weightx = 1;
        gbc.weighty = 1;
        for (int i = 0; st.hasMoreTokens(); i++)
        {
            String tok = st.nextToken().toLowerCase();
//            int o;
//            String key;
//            String val;
            parseToken(gbc, defaults[i], tok);
        }
        currentX = gbc.gridx;
        currentY = gbc.gridy;
        super.addLayoutComponent(comp, gbc);
    }

    void parseToken(GridBagConstraints gbc, String theDefault, String tok)
    {
        int o = tok.indexOf("=");
        String key;
        String val;
        if (o > 0)
        {
            key = tok.substring(0, o);
            val = tok.substring(o + 1);
        }
        else
        {
            key = theDefault;
            val = tok;
        }
        parseSetting(gbc, key, val);
    }
    
    private void parseSetting(GridBagConstraints gbc, String key, String val)
    {
        int v;
        try
        {
            v = Integer.parseInt(val);
        }
        catch (NumberFormatException e)
        {
            v = 0;
        }
        if (key.equals("x") || key.equals("gridx"))
        {
            if (val.equals("."))
                gbc.gridx = currentX;
            else if (val.equals("+"))
                gbc.gridx = ++currentX;
            else
                gbc.gridx = v; 
        } 
        else if (key.equals("y") || key.equals("gridy"))
        {
            if (val.equals("."))
                gbc.gridy = currentY;
            else if (val.equals("+"))
                gbc.gridy = ++currentY;
            else
               gbc.gridy = v; 
        } 
        else if (key.equals("gridwidth") || key.equals("width"))
            gbc.gridwidth = v;
        else if (key.equals("gridheight") || key.equals("height"))
            gbc.gridheight = v;
        else if (key.equals("weightx"))
            gbc.weightx = v;
        else if (key.equals("weighty"))
            gbc.weighty = v;
        else if (key.equals("ipadx"))
            gbc.ipadx = v;
        else if (key.equals("ipady"))
            gbc.ipady = v;
        else if (key.equals("fill"))
        {
            if (val.equals("none"))
                gbc.fill = GridBagConstraints.NONE;
            else if (val.equals("horizontal") || val.equals("h"))
                gbc.fill = GridBagConstraints.HORIZONTAL;
            else if (val.equals("vertical") || val.equals("v"))
                gbc.fill = GridBagConstraints.VERTICAL;
            else if (val.equals("both") || val.equals("hv"))
                gbc.fill = GridBagConstraints.BOTH;
        }
        else if (key.equals("anchor"))
        {
            if (val.equals("center"))
                gbc.anchor = GridBagConstraints.CENTER;
            else if (val.equals("north") || val.equals("n"))
                gbc.anchor = GridBagConstraints.NORTH;
            else if (val.equals("northeast") || val.equals("ne"))
                gbc.anchor = GridBagConstraints.NORTHEAST;
            else if (val.equals("east") || val.equals("e"))
                gbc.anchor = GridBagConstraints.EAST;
            else if (val.equals("southeast") || val.equals("se"))
                gbc.anchor = GridBagConstraints.SOUTHEAST;
            else if (val.equals("south") || val.equals("s"))
                gbc.anchor = GridBagConstraints.SOUTH;
            else if (val.equals("southwest") || val.equals("sw"))
                gbc.anchor = GridBagConstraints.SOUTHWEST;
            else if (val.equals("west") || val.equals("w"))
                gbc.anchor = GridBagConstraints.WEST;
            else if (val.equals("northwest") || val.equals("nw"))
                gbc.anchor = GridBagConstraints.NORTHWEST;
        }
    }
    
    void copy(GridBagConstraints lvalue, GridBagConstraints rvalue)
    {
        lvalue.anchor = rvalue.anchor;
        lvalue.fill = rvalue.fill;
        lvalue.gridheight = rvalue.gridheight;
        lvalue.gridwidth = rvalue.gridwidth;
        lvalue.gridx = rvalue.gridx;
        lvalue.gridy = rvalue.gridy;
        lvalue.insets = rvalue.insets;
        lvalue.ipadx = rvalue.ipadx;
        lvalue.ipady = rvalue.ipady;
        lvalue.weightx = rvalue.weightx;
        lvalue.weighty = rvalue.weighty;
    }
}
