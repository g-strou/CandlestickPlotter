package candlestick;

import base.Bar;
import base.BarShape;
import candlestick.dataTypes.RealTimeData;
import java.awt.BorderLayout;
import java.awt.Container;
import java.util.List;
import javax.swing.JFrame;
import static candlestickplotter.Settings.CANDLESTICK_FRAME_HEIGHT;
import static candlestickplotter.Settings.CANDLESTICK_FRAME_WIDTH;

/**
 *
 * @author Georgios Stroumpoulis
 */
public class CandlestickFrame extends JFrame{
    
    private final ChartPanel chartPanel;
    private final ToolBarPanel toolBarPanel;
    private final RealTimeData realTimeData;

    public CandlestickFrame(RealTimeData d, String dateString) {

        super("Chart " + dateString);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        chartPanel = new ChartPanel(dateString);
        toolBarPanel = new ToolBarPanel(this);
        chartPanel.setOutputTextArea(toolBarPanel.getTextArea());

        Container pane = this.getContentPane();
        pane.setLayout(new BorderLayout());

        pane.add(toolBarPanel,BorderLayout.NORTH);
        pane.add(chartPanel, BorderLayout.CENTER);

        this.setSize(CANDLESTICK_FRAME_WIDTH  ,CANDLESTICK_FRAME_HEIGHT);
        this.setVisible(true);
       
        realTimeData = d;
        chartPanel.setRealTimeData(realTimeData);

    }
    
    public void update(List<Bar> realDataBars, List<BarShape> realDataBarShapes) {
        chartPanel.updateRealTimeData(realDataBars, realDataBarShapes);
    }
    
    public JFrame getFrame() {return this;}
    
    public ChartPanel getChartPanel() {return chartPanel;}

}