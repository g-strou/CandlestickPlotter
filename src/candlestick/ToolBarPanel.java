package candlestick;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 *
 * @author Georgios Stroumpoulis
 */
public class ToolBarPanel extends JPanel {
    
    private Color color;
    private JPanel standardPanel;
    private CandlestickFrame candlestickFrame;
    private ChartPanel chartPanel;
    private JTextArea txtArea;

    
    public ToolBarPanel(CandlestickFrame cf){
        
        super(new GridLayout(1, 0));
        
        candlestickFrame = cf;
        chartPanel = cf.getChartPanel();
        
        txtArea = new JTextArea();
        txtArea.setEditable(false);
        JScrollPane txtAreaScrollPane = new JScrollPane(txtArea);
        txtAreaScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.add(txtAreaScrollPane);
        
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 0));
        addButtons(buttonsPanel); //creates buttonsPanel
        
        standardPanel = new JPanel();
        standardPanel.add(buttonsPanel);
        standardPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        this.add(standardPanel);           

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }
    
    public JTextArea getTextArea() {
        return txtArea;
    }
    
    public final JComboBox makeComboBox(Object [] objects, int selIndex, boolean addListener){
        JComboBox cmb= new JComboBox(objects);
        cmb.setSelectedIndex(selIndex);
        cmb.setSize(new Dimension(60, 40));

        return cmb;       
    }
    
    public final void addButtons(JPanel panel) {

        JButton button;

        //buttons
        button = createButton(createImageIcon("images/reset.gif"), "Reset");
        panel.add(button);
        
        button = createButton(createImageIcon("images/zoom_out.gif"), "Zoom Out");
        panel.add(button);

        button = createButton(createImageIcon("images/zoom_in.gif"), "Zoom In");
        panel.add(button);
        
        button = createButton(createImageIcon("images/save.gif"), "Save Image");
        panel.add(button);

        button = createButton(createImageIcon("images/colors.gif"), "Set Color");
        panel.add(button);

        button = createButton(createImageIcon("images/line.gif"), "Add Line");
        panel.add(button);
        
        button = createButton(createImageIcon("images/triangle.gif"), "Add Triangle Line");
        //panel.add(button);        
    }
    
    public JButton createButton( ImageIcon icon, String actionCommand) {

        JButton  button = new JButton();

        button.setIcon(icon);
        button.setToolTipText(actionCommand);
        button.setActionCommand(actionCommand);
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if ("Reset".equals(e.getActionCommand())) {
                    chartPanel.resetChart();
                }
                if ("Zoom Out".equals(e.getActionCommand())) {
                    chartPanel.zoom(-1);
                }
                else if ("Zoom In".equals(e.getActionCommand())){
                    chartPanel.zoom(1);
                }
                else if ("Add Line".equals(e.getActionCommand())){
                    chartPanel.addUserLine(color, 0);
                }                
                else if ("Set Color".equals(e.getActionCommand())){
                    color = getColor();
                    chartPanel.setColor(color);
                }  
                else if ("Save Image".equals(e.getActionCommand())) {
                    chartPanel.saveAsImage();  
                }
                else if ("Add Triangle Line".equals(e.getActionCommand())) {
                    // add triangle line
                }
            }
        }); 

        return button;
   }

    public Color  getColor(){
        Color c = null;
        c = JColorChooser.showDialog(new JToolBar(), "pick your color", c);
        return c;
    }
    
    public static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = CandlestickFrame.class.getResource(path);

        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.out.println("Couldn't find file: " + path);
            return null;
        }
    }
    
}

   
    