package candlestick;

/**
 *
 * @author Georgios Stroumpoulis
 */

import base.Bar;
import base.BarShape;
import candlestick.chartAddOns.*;
import candlestick.dataTypes.RealTimeData;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.MouseInputListener;
import static candlestickplotter.Settings.*;


public class ChartPanel extends JPanel  {

    private enum SpaceOnChart { EMPTY, ORIGIN, SCALE_X, SCALE_Y, ON_BAR, ON_USER_LINE}
    private enum ActionMode {NONE, TRANSLATION, SCALE_X, SCALE_Y, BAR_CLICKED, MOVE_USER_LINE}
    
    private RealTimeData realTimeData = null;

    private JTextArea txtArea = null;
    
    private List<Bar> bars;
    private List<BarShape> barShapes;
    private float dayOpen;
    private int shiftSpace;
    private String dateString;
    
    private int[] xlimits = new int[2]; //xlimits in world coords on the screen
    private int[] ylimits = new int[2]; //ylimits         >>   >>  >>
    
    private Point2D initialScale;
    private Point2D initialTranslation;
    private Point2D scale;
    private Point2D translation;
    private AffineTransform transformWorldToScreen = new AffineTransform();
    private AffineTransform transformScreenToWorld;
    private int panelHeight;
    private int panelWidth;
    private Bar selectedBar = null;
    
    private List<UserLine> userLines;
    private List<Triangle> triangles;
    private TriangleLine triangleLine;
    
    private UserLine selectedLine = null;
    private ActionMode mode = ActionMode.NONE;
    private boolean hasData;
    private Color selectedColor;

    //general constructor
     public ChartPanel(String ds) {
        
        MouseHandler mouseHandler = new MouseHandler();
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        
        userLines = new ArrayList<UserLine>();
        triangles = new ArrayList<Triangle>();

        hasData = false; 
        dateString = ds;

    }
     
    public void setOutputTextArea(JTextArea txta) {
        txtArea = txta;
    }
    
    public Color getColor() { //the color of toolbar panel
        return selectedColor;
    }

    public void setColor(Color c) {
        selectedColor = c;
    }

    //transform functions
    
    public void makeTransforms() {
        
        AffineTransform transformWorldToScreenUninverted = new AffineTransform(scale.getX(), 0, 0, scale.getY(), translation.getX() + X_MARGIN, translation.getY()+ Y_MARGIN);

        transformWorldToScreen.setTransform(1, 0, 0, -1, 0, getHeight());
        //transformWorldToScreenUninverted.setTransform
        transformWorldToScreen.concatenate(transformWorldToScreenUninverted);
        try {
            transformScreenToWorld = transformWorldToScreen.createInverse();
       }
       catch(Exception ex) {
           System.out.println("Could not create inverse transformation matrix");
           ex.getMessage();
       }
    }
    
    public void zoom(int mode) {       
        int ds = 40;
        if (mode>0) {
            zoomToLimitsX(xlimits[0] + ds, xlimits[1] - ds);  
        }
        else { 
            zoomToLimitsX(xlimits[0] - ds, xlimits[1] + ds);     
        }
    }
        
    private void zoomToLimitsX(int x1, int x2) {

        if (x2<x1) return;
        
        double EPS = 0.01;

        double scaleX = ((double)getWidth() - X_MARGIN)/(x2-x1);
        
        if (scaleX < MIN_SCALE_X) scaleX = MIN_SCALE_X;
        else if (scaleX > MAX_SCALE_X) scaleX = MAX_SCALE_X;
        
        scale.setLocation(scaleX, scale.getY());
       
        if (Math.abs(scaleX-MIN_SCALE_X)<EPS || Math.abs(scaleX-MAX_SCALE_X)<EPS) return;

        double transX = -x1 * scaleX;
        
        translation.setLocation(transX, translation.getY());
        
        makeTransforms();
        repaint();
    }
    
    public void saveAsImage() {
        
        String imageName = dateString + ".png";
        System.out.println("SAVE IMAGE " + imageName); 
        
        BufferedImage image = new BufferedImage(getWidth() , getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        this.paint(g2);
        try {ImageIO.write(image, "png", new File(imageName));}
        catch(Exception e)  {System.out.println(e.getMessage());}
    }
    
    //draw functions
    
        @Override
    public void paintComponent(Graphics gr) {

       Graphics2D g = (Graphics2D) gr;
       
       g.setColor(Color.white);
       g.fillRect(0, 0, getWidth(), getHeight());
      
       if (hasData) paintData(g);
    }
        
    private void paintData(Graphics2D g) {
        
        panelHeight = getHeight();
        panelWidth = getWidth();

        Point2D.Double zeroCoordsWorld  = (Point2D.Double)transformScreenToWorld.transform(new Point2D.Double(X_MARGIN, panelHeight - Y_MARGIN), null); 
        Point2D.Double topCoordsWorld  = (Point2D.Double)transformScreenToWorld.transform(new Point2D.Double(panelWidth, 0), null); 
        
        xlimits[0] = (int)Math.round(zeroCoordsWorld.getX());
        xlimits[1] = (int)Math.round(topCoordsWorld.getX());
        ylimits[0] = (int)Math.round(zeroCoordsWorld.getY());
        ylimits[1] = (int)Math.round(topCoordsWorld.getY());  
        
        paintBars(g);
        paintTriangles(g);
        paintUserLines(g);
        paintTicks(g);

    }
    
    public void paintBars(Graphics2D g) {
         
       if (selectedBar != null) { //yellow line for selected bar
            Line2D selectionLine = new Line2D.Double(selectedBar.getIndex(), ylimits[0], selectedBar.getIndex(), ylimits[1]);
            Shape convertedLine = transformWorldToScreen.createTransformedShape(selectionLine);
            g.setColor(Color.ORANGE);
            g.draw(convertedLine);
        }
        for(int x = xlimits[0]; x <= xlimits[1]; x++) {       
            if (x>=0 && x < barShapes.size()) {
                barShapes.get(x).draw(g, transformWorldToScreen);    
            }                
        }      
    }
    
    public void paintTicks(Graphics2D g) {
        
        g.drawString(dateString, X_MARGIN + 10, 20);
        
        int dy = ylimits[1] - ylimits[0];
        int dx = xlimits[1] - xlimits[0];
        
        int yGap, xGap;
        if (dy < 30) yGap = 2;
        else if(dy < 60) yGap = 5;
        else if(dy < 100) yGap = 10;
        else if (dy <200) yGap = 20;
        else yGap = 50;
        
        if (dx < 30) xGap = 5;
        else if (dx<60) xGap = 10;
        else if (dx<120) xGap = 15;
        else if (dx<480) xGap = 30;
        else xGap = 60;
        
        g.clearRect(0, 0, X_MARGIN, panelHeight);
        g.clearRect(0, panelHeight - Y_MARGIN, panelWidth, Y_MARGIN);
        
        g.setColor(Color.black);
        
        Point vecWorld = new Point();
        Point2D pScreen;
        for(int x = xlimits[0]; x <= xlimits[1]; x++) { 
            vecWorld.setLocation(x, 0);
            pScreen = transformWorldToScreen.transform(vecWorld, null);
            int xValue = (int)pScreen.getX();
            
            g.drawLine(xValue, panelHeight - Y_MARGIN, xValue, panelHeight - (Y_MARGIN - TICK_LINE_LENGTH));
            
            if (x>=0 && x< bars.size() && xValue > X_MARGIN && (x==0 || (x+1) % xGap == 0)) {
                String s = bars.get(x).getTimeStamp();
                g.drawString(s, xValue, panelHeight - 35);
                g.drawLine(xValue, panelHeight - Y_MARGIN, xValue, panelHeight);
            }
            else
                g.drawLine(xValue, panelHeight - Y_MARGIN, xValue, panelHeight - (Y_MARGIN - TICK_LINE_LENGTH));  
        }
        
        for(int y = ylimits[0]; y <= ylimits[1]; y++) {
            vecWorld.setLocation(0, y);
            pScreen = transformWorldToScreen.transform(vecWorld, null);
            int yValue = (int)pScreen.getY();
            g.drawLine(X_MARGIN, yValue, X_MARGIN - TICK_LINE_LENGTH, yValue);
            
            if (yValue < panelHeight - Y_MARGIN && ((y + shiftSpace) % yGap) == 0) {

                g.drawString(String.format("%.2f",worldToRealPrice(y)), 5, yValue - 2);
                
                Stroke st = g.getStroke();
                Color ct = g.getColor();
                Stroke st2 = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] {4.0f,4.0f}, 0.0f);
                g.setColor(new Color((float)0.5, (float)0.5, (float)0.5, (float)0.5));
                g.setStroke(st2);
                g.drawLine(0, yValue, panelWidth, yValue);
                g.setStroke(st);
                g.setColor(ct);
                
            }
        }        
        
        g.clearRect(0, panelHeight - Y_MARGIN, X_MARGIN, Y_MARGIN);
        
        g.drawLine(X_MARGIN, 0, X_MARGIN, panelHeight);
        g.drawLine(0, panelHeight - Y_MARGIN, panelWidth, panelHeight - Y_MARGIN);

    }
    
    public void paintUserLines(Graphics2D g) {
        for (UserLine line:userLines) {
            line.paint(g, transformWorldToScreen);
        }
    }
    
    public void paintTriangles(Graphics2D g) {
        for (Triangle triangle:triangles) {
            triangle.paint(g, transformWorldToScreen);
        }
    }
   

    public void setRealTimeData(RealTimeData data) {

        realTimeData = data;

        dateString = realTimeData.getDateString();
        
        int [] limits = realTimeData.getinitialLimits();
        dayOpen = (float)realTimeData.getDayOpen();
        shiftSpace = Math.round((float)dayOpen * 100) % 10;
        System.out.println("dayOpen=" + dayOpen + " shift=" + shiftSpace);
        
        initialTranslation = new Point2D.Double(0,CANDLESTICK_FRAME_HEIGHT/2);
        initialScale = new Point2D.Double((double)(CANDLESTICK_FRAME_WIDTH - X_MARGIN)/(limits[1] - limits[0]), (double)((CANDLESTICK_FRAME_HEIGHT-Y_MARGIN)/(limits[3] - limits[2])));
        
        scale = copyDoublePoint2D(initialScale);
        translation = copyDoublePoint2D(initialTranslation);
        makeTransforms();

        hasData = true;

        updateRealTimeData(realTimeData.getBars(), realTimeData.getBarShapes());
        
    }
    
    public void updateRealTimeData(List<Bar> realDataBars, List<BarShape> realDataBarShapes) {

        bars = realDataBars;
        barShapes = realDataBarShapes;

        if (bars == null || barShapes==null) {
            System.out.println("chartpanel: null received");
            System.out.println("bars:" +bars.toString());
            System.out.println("barShapes:" + barShapes.toString());
            return;
        }

        repaint();
    }
    
    private Point2D.Double copyDoublePoint2D(Point2D p) {
        return new Point2D.Double((double)p.getX(), (double)p.getY());
    }
    
    private float worldToRealPrice(int y) {
        return Math.round(100*dayOpen + (float)y)/((float)100);
    }

    public void resetChart() {
        translation = copyDoublePoint2D(initialTranslation);
        scale = copyDoublePoint2D(initialScale);
        makeTransforms();
        repaint();
    }
    
    public void removeUserLine(int lineId) {
      
        if (userLines.isEmpty() || lineId < 0) return;
        
        int index = -1;
        
        for (int i=0; i<userLines.size(); i++) {
            if (userLines.get(i).getId() == lineId) {
                index = i;
                break;
            }
        }
        if (index>=0) userLines.remove(index);
        repaint();
    }

    
    public void getTrianglesAtLevel(int yWorld) {
            
//        HashMap<String, Integer> map = parser.makeTableAtRandomLevelFromStart(yWorld ,1,true);
//
//        ChartPanel.this.clearTextArea();
//
//        if (map == null) return;
//        
//        parser.outputToTextArea(map, txtArea);
//
//        System.out.println("yWorld = " + yWorld);
//        List<Triangle> mapTriangles = parser.getTrianglesFromMap(map, false); 
//        for(Triangle t:mapTriangles) {
//            System.out.println(t);
//
//        } 
//        System.out.println("-------------");
//        clearTriangles();
//        addTriangleList(mapTriangles);           
    }

    public void clearTriangles() {
        triangles.clear();
        repaint();
    }
    
    public void addTriangle(Triangle t) {
        triangles.add(t);
        repaint();
    }
    
    public void addTriangleList(List<Triangle> triangleList) {
        for (Triangle t:triangleList) {
            triangles.add(t);
        }
        repaint();
    }

    public int addUserLine(Color c, int v) {
        UserLine line = new UserLine(this, c);
        line.setValue(v);
        userLines.add(line);
        repaint();
        return line.getId();
    }
       
    public void clearUserLines() {
        userLines.clear();
        repaint();
    }
    
    class MouseHandler implements MouseInputListener {
       
        private int x0Screen;
        private int y0Screen;

        @Override
        public void mousePressed(MouseEvent e) {
            
            x0Screen = e.getX();
            y0Screen = e.getY();
            
            panelHeight = getHeight();
            panelWidth = getWidth();
            
            SpaceOnChart onSpace = findSpaceOnChart();
            
            int btnType = e.getButton();
            
            switch (btnType) {
                case 1:    //left button
                    switch (onSpace) {   

                        case ON_BAR :
                            mode = ActionMode.BAR_CLICKED;      
                            break;
                        case ON_USER_LINE :
                            mode = ActionMode.MOVE_USER_LINE;
                            ChartPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                            break;
                        case EMPTY:
                            mode = ActionMode.TRANSLATION;
                            ChartPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                            break;
                        case SCALE_X:
                            mode = ActionMode.SCALE_X;
                            ChartPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                            break;
                        case SCALE_Y:
                            mode = ActionMode.SCALE_Y;
                            ChartPanel.this.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
                            break;
                        case ORIGIN:
                            resetChart();
                            break;
                        default:
                            mode = ActionMode.NONE;
                    }
                    break;
                    
                case 3:     //right button
                    
                    ChartPopupMenu menu;
                    
                    switch (onSpace) {   
                        case ON_USER_LINE :
                            menu = new ChartPopupMenu.UserLineMenu(ChartPanel.this, selectedLine);
                            menu.show(ChartPanel.this, x0Screen, y0Screen);
                            break;
                        default:
                            mode = ActionMode.NONE;                    
                    }
                    break;
                    
                default:break;
            }
        }
        
        public SpaceOnChart findSpaceOnChart() {
            
            //----------checks which part of the chart the mouse was pressed on
            SpaceOnChart onSpace = SpaceOnChart.EMPTY;
            
            if (x0Screen < X_MARGIN) {
                if (y0Screen > panelHeight - Y_MARGIN) {  //bottom-left corner resets chart
                    onSpace = SpaceOnChart.ORIGIN;
                }
                else
                    onSpace = SpaceOnChart.SCALE_Y;   
            }
            else if (y0Screen > panelHeight - Y_MARGIN)
                onSpace = SpaceOnChart.SCALE_X;
            else {
                // checks if the user selects a bar
                for(int x=xlimits[0]; x<=xlimits[1]; x++) {

                    if (x>=0 && x< barShapes.size()) {

                        Shape barRect = transformWorldToScreen.createTransformedShape(barShapes.get(x).getBoundingRectangle());

                        if (barRect.contains(x0Screen, y0Screen)) {
                            onSpace = SpaceOnChart.ON_BAR;
                            selectedBar = bars.get(x);
                            txtArea.setText(selectedBar.toString());
                            repaint();
                            break;
                        }
                    }
                }
                
                for (UserLine line : userLines) {
                    Shape lineRect = line.getHandler();
                    if (lineRect.contains(x0Screen, y0Screen)) {
                        onSpace = SpaceOnChart.ON_USER_LINE;
                        selectedLine = line;
                    }
                }
            }
            return onSpace;
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {

            if (mode == ActionMode.NONE) return;
            
            int xScreen = e.getX();
            int yScreen = e.getY();

            if (mode == ActionMode.TRANSLATION) {

                translation.setLocation(translation.getX() + xScreen - x0Screen, translation.getY() - (yScreen - y0Screen));

            }
            else if (mode == ActionMode.SCALE_X) {                
               
                int dxScreen = xScreen - x0Screen;
  
                zoomToLimitsX(xlimits[0] + dxScreen/2, xlimits[1]-dxScreen/2);
  
            }
            else if (mode == ActionMode.SCALE_Y) {
                
                int dyScreen = -(yScreen - y0Screen);
                
                //boolean trChange = false;
                
                double scaleY = scale.getY();
                double difY = (double)dyScreen/Y_SCALE_DENOMINATOR;
                scaleY += difY;
                
                if (scaleY < MIN_SCALE_Y) scaleY = MIN_SCALE_Y;
                else if (scaleY > MAX_SCALE_Y) scaleY = MAX_SCALE_Y;
                //else trChange = true;
               
                scale.setLocation(scale.getX(), scaleY);      
                
                //if (trChange) translation.setLocation(translation.getX(), translation.getY() - difY/2);
            }
            else if (mode == ActionMode.MOVE_USER_LINE && selectedLine != null) {
                int y = e.getY();
                Point2D.Double worldVec = (Point2D.Double)transformScreenToWorld.transform(new Point2D.Double(0, y), null);
                int yWorld = (int) Math.round(worldVec.getY()); //Math.round(getHeight() - worldVec.getY());
                selectedLine.setValue(yWorld);
                repaint();
            }
            
            x0Screen =  xScreen;
            y0Screen =  yScreen;
            
            makeTransforms();
            repaint();

        }
        
        @Override
        public void mouseReleased(MouseEvent e) { 
            mode = ActionMode.NONE; 
            ChartPanel.this.setCursor(Cursor.getDefaultCursor());
            repaint();
        }

        @Override
        public void mouseClicked(MouseEvent e) {}
        @Override
        public void mouseExited(MouseEvent e) {} 
        @Override
        public void mouseMoved(MouseEvent e) {}
        @Override
        public void mouseEntered(MouseEvent e) {}

    }
    
    
}

