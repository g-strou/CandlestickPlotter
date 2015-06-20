package base;

/**
 *
 * @author Georgios Stroumpoulis
 */

import java.text.DecimalFormat;

public class Bar {
    
    protected int index;
    protected String timeStamp;
    protected int timeToInteger;
    protected int normOpen;
    protected int normHigh;
    protected int normLow;
    protected int normClose;
    protected double realOpen;
    protected double realHigh;
    protected double realLow;
    protected double realClose;
    protected double realDayOpen = -1;
    protected int type; //1 green 0 red

    public Bar(int i, double[] values, double dopen, String t) {
        
        this(i, values, dopen, t, makeTimeToInteger(t));
    }
    
    public Bar(int i, double[] values, double dopen, String t, int ttn) {
        
        index = i;
        realOpen = roundTwoDecimals(values[0]);
        realHigh = roundTwoDecimals(values[1]);
        realLow = roundTwoDecimals(values[2]);
        realClose = roundTwoDecimals(values[3]);
        realDayOpen = roundTwoDecimals(dopen);
        
        timeStamp = t;
        timeToInteger = ttn;

        normOpen = toInteger(realOpen);
        normHigh = toInteger(realHigh);
        normLow = toInteger(realLow);
        normClose = toInteger(realClose);
        
        findType();
    }
    
    public final void findType() {
         if (normClose>normOpen) 
            type = 1;
        else if (normOpen>normClose)
            type = -1;
        else 
            type = 0;         
    }
    
    public Node[] toNodes() {
        
        Node []nodes = new Node[4];
        
        nodes[0] = new Node(this, Node.PosInBar.OP);
        
        if (type >= 0) {
            nodes[1] = new Node(this, Node.PosInBar.LO);
            nodes[2] = new Node(this, Node.PosInBar.HI);           
        }
        else {
            nodes[1] = new Node(this, Node.PosInBar.HI);
            nodes[2] = new Node(this, Node.PosInBar.LO);
        }
        
        nodes[3] = new Node(this, Node.PosInBar.CL);
        return nodes;
    };
    
    
    public int getType() {return type;}
    
    public int getIndex() {return index;}
    
    public String getTimeStamp() {return timeStamp;}
    
    public int getTimeToInteger() {return timeToInteger;}
    
    public int getNormalizedOpen() {return normOpen; }
    
    public int getNormalizedHigh() {return normHigh;}
    
    public int getNormalizedLow() {return normLow;}
    
    public int getNormalizedClose() {return normClose;}
    
    public double getRealOpen() {return realOpen;}
    
    public double getRealHigh() {return realHigh;}
    
    public double getRealLow() {return realLow;}
    
    public double getRealClose() {return realClose;}
    
    public double getRealDayOpen() {return realDayOpen;}
    
    public static int makeTimeToInteger(String t) {
        return (t.isEmpty() ? 0 : (100 * Integer.valueOf(t.split(":")[0]) + Integer.valueOf(t.split(":")[1])));
    }
    
    public final int toInteger(double x) {
        return (int)Math.round(100*(x - realDayOpen));
    }
    
    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
	return Double.valueOf(twoDForm.format(d).replace(",", "."));
    }
    
    @Override
    public String toString() {
        return "Bar: " + timeStamp + "\tOpen=" + realOpen + "\tHigh=" + realHigh + "\tLow=" + realLow + "\tClose=" + realClose;
    }

}
