package candlestick.dataTypes;

import base.Bar;
import base.BarShape;
import candlestick.CandlestickFrame;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Georgios Stroumpoulis
 */
public class RealTimeData {
 
    private CandlestickFrame candlestickFrame = null;
    private List<Bar> completeBars;
    private List<BarShape> completeBarShapes;
    private Bar activeBar;
    private String dateString;
    private int barIndex;
    private double dayOpen;
    
    public RealTimeData(double openValue, String ds) {

        dateString = ds;
        completeBars = new ArrayList<Bar>();
        completeBarShapes = new ArrayList<BarShape>();
        barIndex = 0;
        activeBar = null;
        dayOpen = openValue;

    }

    public CandlestickFrame getCandlestickFrame() {
        return candlestickFrame;
    }
      
    public Bar getActiveBar() {
        return activeBar;
    }

    public List<Bar> getBars() { 
        
        ArrayList<Bar> bars = ((completeBars.size()>0) ? new ArrayList<Bar>(completeBars) : new ArrayList<Bar>());

        if (activeBar != null) {
            bars.add(activeBar);
        }
        
        return bars;
    }
    
    public List<BarShape> getBarShapes() {
        
        ArrayList<BarShape> shapes = ((completeBarShapes.size()>0) ? new ArrayList<BarShape>(completeBarShapes) : new ArrayList<BarShape>());
        
        if (activeBar != null) {
            shapes.add(new BarShape(activeBar));
        }
        
        return shapes;
    }
    
    private void createChartIfNull() {
        if (candlestickFrame == null) {
            candlestickFrame = new CandlestickFrame(this, dateString);
        }        
    }
    
    public void setSimulationNewBar(Bar bar) {
        
        completeBars.add(bar);
        completeBarShapes.add(new BarShape(bar));
        
        createChartIfNull();
        
        candlestickFrame.update(completeBars, completeBarShapes);        

    }
    
    public void setSimulationBars(ArrayList<Bar> bars) {

        completeBars = bars;
        completeBarShapes = new ArrayList<BarShape>();
        for (Bar bar : bars) {
            completeBarShapes.add(new BarShape(bar));
        }
        
        createChartIfNull();
       
        candlestickFrame.update(completeBars, completeBarShapes);
    }

    public int[] getinitialLimits() {
        
        int[] limits = {0, 400, -50, 50};
        
        return limits;
    }
    
    public String getDateString()  {
        return dateString;
    }
    
    public double getDayOpen() {
        return dayOpen;
    }
    
}
