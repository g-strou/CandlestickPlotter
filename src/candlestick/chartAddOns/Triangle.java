package candlestick.chartAddOns;

import candlestick.ChartPanel;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Georgios Stroumpoulis
 */
public class Triangle extends ChartPainting {
    
    
    private static int MAX_VAL = 600;
    private static int counter = 1;
    
    private static int HANDLER_DIM = 20;
    
    private int baseLevel;
    private int startIndex; 
    private int peakIndex;
    private int endIndex;
    private int peakValue; //relative to baseLevel. Can be positive or negative
    private String label;
    
    private Rectangle2D.Double handler;
    
    public Triangle(ChartPanel cp, int level,int a, int b, int c, int v, String lb) {
        
        chart = cp;
        baseLevel = level;
        startIndex = (a > 0 ? a : -1); 
        peakIndex = b;
        endIndex = (c < MAX_VAL ? c : -1);
        peakValue = v;        
        label = lb;
        
        id = Triangle.counter++;
    }
    
    
    public boolean isValid() {
        return (peakIndex>0 && peakValue !=0 && (startIndex>0 || endIndex>0));
    }
    
    public String getLabel() {
        return label;
    }
    
    public Shape getHandler() {
        return handler;
    }
    
    @Override   
    public void paint(Graphics2D g, AffineTransform tr) {
        
        Point2D.Double peakPoint = (Point2D.Double)tr.transform(new Point2D.Double(peakIndex, peakValue + baseLevel), null);
        
        g.setColor(color);
        
        if (startIndex>0) {
            Point2D.Double startPoint = (Point2D.Double)tr.transform(new Point2D.Double(startIndex, baseLevel), null);
            g.drawLine((int)startPoint.getX(), (int)startPoint.getY(), (int)peakPoint.getX(), (int)peakPoint.getY());         
        }

        if(endIndex > 0 ) {
            Point2D.Double endPoint = (Point2D.Double)tr.transform(new Point2D.Double(endIndex, baseLevel), null);
            g.drawLine((int)peakPoint.getX(), (int)peakPoint.getY(), (int)endPoint.getX(), (int)endPoint.getY());     
        }
        
        //System.out.println("peakPoint: " + peakPoint.getX() + " " + peakPoint.getY());
        if (peakValue > baseLevel) {
            handler = new Rectangle2D.Double(peakPoint.getX() - HANDLER_DIM, peakPoint.getY() - 2*HANDLER_DIM - 10, 2*HANDLER_DIM, 2*HANDLER_DIM);
        }
        else {
            handler = new Rectangle2D.Double(peakPoint.getX() - HANDLER_DIM, peakPoint.getY() + 10, 2*HANDLER_DIM, 2*HANDLER_DIM);
        }
        
        String str = label + " (" + peakValue + ")";
        g.drawString(str, (int)handler.getX(), (int)handler.getY() + HANDLER_DIM);
        g.draw(handler);

    }
    
    public int getBaseValue() {return baseLevel;}
    
    public int getStartIndex() {return startIndex;}
    
    public int getPeakIndex() {return peakIndex;}
    
    public int getEndIndex() {return endIndex;}
    
    public int getPeakValue() {return peakValue;}
    
    public void setBaseValue(int v) {baseLevel = v;}
    
    public void setStartIndex(int v) {startIndex= v;}
    
    public void setPeakIndex(int v) {peakIndex= v;}
    
    public void setEndIndex(int v) {endIndex= v;}
    
    public void setPeakValue(int v) {peakValue= v;}
    
    @Override
    public String toString() {
        String s = "";
        s = "Triangle: " + label + ": " + startIndex + " " + peakIndex + " " + endIndex + " " + " baseLevel:" + baseLevel + " peakValue:" + peakValue;
        
        return s;
    }


}
