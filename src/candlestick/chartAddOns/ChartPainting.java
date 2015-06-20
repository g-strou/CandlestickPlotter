package candlestick.chartAddOns;

import candlestick.ChartPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Georgios Stroumpoulis
 */
public abstract class ChartPainting {
    
    protected ChartPanel chart;
    protected int id;
    protected Color color;
    
    public int getId() {return id;}

    public void setColor(Color c) {
        color = c;
    }
    
    public Color getColor() {return color;}
    
    public abstract void paint(Graphics2D g, AffineTransform tr);
}
