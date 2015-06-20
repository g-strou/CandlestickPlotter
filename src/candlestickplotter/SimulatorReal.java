package candlestickplotter;

/**
 *
 * @author Georgios Stroumpoulis
 */

import base.Bar;
import candlestick.CandlestickFrame;
import candlestick.dataTypes.HistoricalData;
import candlestick.dataTypes.RealTimeData;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class SimulatorReal extends Thread {
    
    public static enum Mode {HISTORICAL, SIMULATOR, REALDATA};
    
    private HistoricalData historicalData;
    private RealTimeData realTimeData;
    private String strategyFolder;
    private Mode mode;
    
    private JFrame sliderFrame;
    
    private JSlider slider;
    
    //from historical data
    private List<Bar> bars;
    private String dateString = null;

    private int counter;
    
    // if the constructor accepts a folder (strategy folder) mode= simulator, even if the folder string is null
    public SimulatorReal(String filename, String folder) {//SIMULATOR
        historicalData = new HistoricalData(filename);
        bars = historicalData.getBars();
        dateString = historicalData.getDateString();
        mode = Mode.SIMULATOR;
        strategyFolder = folder;
        //System.out.println("strategyFolder: " + strategyFolder + "   dateString: " + dateString);
    }
    
    public SimulatorReal(String filename) {//HISTORICAL
        historicalData = new HistoricalData(filename);
        bars = historicalData.getBars();
        dateString = historicalData.getDateString();
        mode = Mode.HISTORICAL;
        //System.out.println("dateString: " + dateString);
    }
    
    @Override
    public void run() {

        realTimeData = new RealTimeData(historicalData.getDayOpen(), dateString);    
        
        if (mode == Mode.SIMULATOR) {
            counter = 0;
            createFrame();
            sendNextBarToRealTimeData();
        }
        else if (mode == Mode.HISTORICAL) {
            setAllBarsToRTData();
        }
    }

    
    public void setAllBarsToRTData() { //ONESHOT mode
        realTimeData.setSimulationBars(new ArrayList<Bar>(bars));
    }
   
    public void setManyBarsToRTData(int c) {
        
        counter = c;
        slider.setValue(counter);
        ArrayList<Bar> cbars = new ArrayList<Bar>(bars.subList(0, counter));
        realTimeData.setSimulationBars(cbars);
        
    }
    
    public void goBackOneBar() {
        counter--;
        slider.setValue(counter);
        ArrayList<Bar> cbars = new ArrayList<Bar>(bars.subList(0, counter));
        realTimeData.setSimulationBars(cbars);
    }
    
    public void sendNextBarToRealTimeData() {
        realTimeData.setSimulationNewBar(bars.get(counter));
        counter++;
    }

    // GUI functions
    
    private void createFrame() {

        sliderFrame = new JFrame("Simulation Data");
        sliderFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = (JPanel)sliderFrame.getContentPane();
        JPanel buttonsPanel = new JPanel();
        
        panel.setLayout(new BorderLayout());

        JButton nextButton = createButton("Next");
        JButton prevButton = createButton("Prev");

        buttonsPanel.add(prevButton);
        buttonsPanel.add(nextButton);
        
        
        slider = new JSlider(0, bars.size() - 1, 0);
        slider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider slider=(JSlider)e.getSource();
                
                //stopFeedThread();
                int c = slider.getValue();
                setManyBarsToRTData(c);
            }
        });
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        panel.add(slider, BorderLayout.PAGE_END);
  
        sliderFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                CandlestickFrame frame = realTimeData.getCandlestickFrame();
                if (frame!=null) {
                    frame.getFrame().dispose();
                }
            }
        });

        
        sliderFrame.setSize(400, 100);
        sliderFrame.setAlwaysOnTop(true);
        sliderFrame.setVisible(true);
    }
    
    public JPanel getSliderPanel() {
        return (JPanel)sliderFrame.getContentPane();
    }
    
    public JButton createButton(String actionCommand) {

        JButton  button = new JButton(actionCommand);
        
        //button.setToolTipText(actionCommand);
        button.setActionCommand(actionCommand);
        button.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if ("Next".equals(e.getActionCommand())) {
                    sendNextBarToRealTimeData();
                } 
                else if ("Prev".equals(e.getActionCommand())) {
                    goBackOneBar();
                } 
            }
        }); 

        return button;
   }
       

}
