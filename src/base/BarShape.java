package base;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Georgios Stroumpoulis
 */
public class BarShape {

    public static final float halfWidth = (float)0.2;

    public Bar bar;
    public int top;
    public int bottom;

    public Shape rectangle;
    public Shape lineUp;
    public Shape lineDown;

    public BarShape(Bar b) {
 
        bar = b;

        if (bar.getType() > 0) {
            top = bar.getNormalizedClose();
            bottom  =  bar.getNormalizedOpen();
        }
        else if (bar.getType() < 0 ) {
            top = bar.getNormalizedOpen();
            bottom  =  bar.getNormalizedClose();
        }
        else {
            top = bar.getNormalizedOpen();
            bottom = top;
        }

        if (top == bottom) 
            rectangle = new Line2D.Float(((float)bar.getIndex()- BarShape.halfWidth), (float)top, ((float)bar.getIndex() + BarShape.halfWidth), (float)top);
        else
            rectangle = new Rectangle2D.Float(((float)bar.getIndex() - BarShape.halfWidth), (float)bottom, 2*BarShape.halfWidth, ((float)top - (float)bottom));

        if (bar.getNormalizedHigh() > top)
            lineUp = new Line2D.Float((float)bar.getIndex(), (float)bar.getNormalizedHigh(), (float)bar.getIndex(), (float)top);
        else
            lineUp = null;

        if (bar.getNormalizedLow() < bottom)
            lineDown = new Line2D.Float((float)bar.getIndex(), (float)bar.getNormalizedLow(), (float)bar.getIndex(), (float)bottom);
        else
            lineDown = null;

    }

    @Override
    public String toString() {
        String ret = "bshape: ";
        ret += this.top;
        ret += " " + this.bottom;
        String tp = "-";
        if (this.rectangle instanceof Rectangle2D.Float) tp = "rectangle";
        else if (this.rectangle instanceof Line2D.Float) tp = "line";
        ret += " " + tp;
        ret += " bar:" + this.bar.toString();
        return ret;
    }
    
    public Bar getBar() { return bar;}

    public Shape getBoundingRectangle() {
        return new Rectangle2D.Float(((float)bar.getIndex() - BarShape.halfWidth), (float)bar.getNormalizedLow(), (float)2*BarShape.halfWidth, (float)(bar.getNormalizedHigh() - bar.getNormalizedLow()));
    }    

    public void draw(Graphics2D g) {
        draw(g, new AffineTransform());
    }

    public void draw(Graphics2D g, AffineTransform tr) {

        Shape rectangleScreen = tr.createTransformedShape(rectangle);

        if (bar.getType() > 0) {
            g.setColor(Color.GREEN);
        }
        else if (bar.getType() < 0 ) {
            g.setColor(Color.RED);
        }
        else {
            g.setColor(Color.BLACK);
        }        

        //Shape rectangleScreen = tr.createTransformedShape(rectangle);
        g.fill(rectangleScreen);


        g.setColor(Color.BLACK);
        
        g.draw(rectangleScreen);
        
        if (lineUp != null) {
            Shape lineUpScreen = tr.createTransformedShape(lineUp);
            g.draw(lineUpScreen);
        }
        if (lineDown != null) {
            Shape lineDownScreen = tr.createTransformedShape(lineDown);
            g.draw(lineDownScreen);
        }
    }
}

