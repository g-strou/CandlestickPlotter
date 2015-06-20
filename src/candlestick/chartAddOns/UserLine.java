package candlestick.chartAddOns;

/**
 *
 * @author Georgios Stroumpoulis
 */

import candlestick.ChartPanel;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class UserLine extends ChartPainting {
        
        protected static int counter = 1;
        
        public int handlerWidth = 20;
        public int handlerHeight = 20;
        protected Rectangle2D.Float handler;
        
        protected int value;

        public UserLine(ChartPanel cp) {
            this(cp, Color.black);
        }
        
        public UserLine(ChartPanel cp, Color c) {
            chart = cp;
            color = c;
            id = UserLine.counter++;
        }
        
        public void setValue(int v) {
            value = v;
        }
        public int getValue() {
            return value;
        }
        
        public Shape getHandler() {
            return handler;
        }
        
        @Override
        public void paint(Graphics2D g, AffineTransform tr) {

            Point2D.Double p = (Point2D.Double)tr.transform(new Point2D.Double(0, value), null);
            int y = (int)p.getY();

            g.setColor(color);
            g.drawLine(0, y, chart.getWidth(), y);


            handler = new Rectangle2D.Float(chart.getWidth() - handlerWidth, y - handlerHeight, handlerWidth, handlerHeight);
            g.fill(handler);

            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(value), (int)handler.getX(), (int)handler.getY()-5);

            g.setColor(color);

        }
        
         @Override
        public String toString() {
             String ret = "line: " + id + " " + value;
             return ret;
        }
    }

