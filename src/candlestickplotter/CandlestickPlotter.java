package candlestickplotter;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author Georgios Stroumpoulis
 */

public class CandlestickPlotter extends JFrame {

    private SimulatorReal simulatorReal =null;
    
    private File selectedFile =null;
  
    public CandlestickPlotter() {
        initComponents();
    }
    
    private void initComponents() {
        
        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("TWSConnection");
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("HISTORICAL", historicalPanel());
        tabbedPane.addTab("SIMULATOR", simulatorPanel());
        
        
        this.setPreferredSize(new Dimension(Settings.TWS_FRAME_WIDTH, Settings.TWS_FRAME_HEIGHT));
        this.add(tabbedPane);
        
        pack();
    }
 
    private JPanel simulatorPanel() {
        
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JButton btnOpenFile = new JButton("Open Files(s)");
        
        JButton clearButton = new JButton("Demo");
        clearButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                simulatorReal = new SimulatorReal("demoData.txt", null);
                simulatorReal.start();   
            } 
        });
        
        JPanel buttonsPanel = new JPanel(new GridLayout(1,2));
        buttonsPanel.add(btnOpenFile);
        buttonsPanel.add(clearButton);
        
        btnOpenFile.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();

                fc.showOpenDialog(panel);
                selectedFile = fc.getSelectedFile();
                
                if (selectedFile != null) {
                    simulatorReal = new SimulatorReal(selectedFile.getAbsolutePath(), null);
                    simulatorReal.start();                        
                }
            }
        });
        
        panel.add(buttonsPanel);
        return panel;
    }
    
    private JPanel historicalPanel() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        JButton btnOpenFiles = new JButton("Open Files(s)");
        
        JButton clearButton = new JButton("Demo");
        clearButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                simulatorReal = new SimulatorReal("demoData.txt");
                simulatorReal.start();   
            } 
        });
        

        JPanel buttonsPanel = new JPanel(new GridLayout(1,2));
        buttonsPanel.add(btnOpenFiles);
        buttonsPanel.add(clearButton);

        btnOpenFiles.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();
                
                fc.setMultiSelectionEnabled(true);

                fc.showOpenDialog(panel);

                File [] selectedFiles = fc.getSelectedFiles();
                for (File f : selectedFiles) {
                    
                    simulatorReal = new SimulatorReal(f.getAbsolutePath());
                    simulatorReal.start();          
                }   
            }
        
        });
        
        panel.add(buttonsPanel);
        
        return panel;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame connectionFrame = new CandlestickPlotter();
                connectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                connectionFrame.setVisible(true);
            }
        });
    }
    
}
