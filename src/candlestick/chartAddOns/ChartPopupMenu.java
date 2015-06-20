package candlestick.chartAddOns;

import candlestick.ChartPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Georgios Stroumpoulis
 */
public abstract class ChartPopupMenu extends JPopupMenu {
    
    public static class UserLineMenu extends ChartPopupMenu {
        private UserLine userLine;
        private ChartPanel chart;
        public UserLineMenu(ChartPanel cp, UserLine line) {
            chart = cp;
            userLine = line;
            JMenuItem item = new JMenuItem("Delete");
            item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        chart.removeUserLine(userLine.getId());
                    } 
                });
            add(item);
            
            JMenuItem item2 = new JMenuItem("Add Fake Node");
            item2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //
                        chart.repaint();
                    } 
                });
            add(item2);
            
        } 
    }
    
}
