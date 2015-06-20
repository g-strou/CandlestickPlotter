package candlestick.dataTypes;

import base.Bar;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Georgios Stroumpoulis
 */
public class HistoricalData {

    private int counter;
    private double dayOpen;
    private List<Bar> bars;
    private String filename;
    private String dateString = null;
    
    public HistoricalData(String filename) {
        dateString = filename.substring(filename.lastIndexOf("\\") + 1, filename.indexOf(".txt"));
        readCSVFormat(filename);
    }
    
    public String getDateString() {
        return dateString;
    }

    public List<Bar> getBars() { return bars; }

    public float getDayOpen()  { return (float)dayOpen; }

    public final void readCSVFormat(String fn) {
        
        filename = fn;
        dayOpen = -1;
        bars = new ArrayList<Bar>();
       
        try {

            File file = new File(filename);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            
            String[] fields;
            String line;
            counter = 0;
            
            while ((line = reader.readLine()) != null) {
                
                fields = line.split(",");
                String timeStamp = fields[0];
                String[] timeSplit = timeStamp.split(":");
                timeStamp = timeSplit[0] + ":" + timeSplit[1];
                
                int timeToInteger = 100 * Integer.valueOf(timeSplit[0]) + Integer.valueOf(timeSplit[1]);
                
                if (timeToInteger > 900 && timeToInteger < 1831) {
                
                    float f1, f2, f3, f4;

                    f1 = Float.valueOf(fields[1]);
                    f2 = Float.valueOf(fields[2]);
                    f3 = Float.valueOf(fields[3]);
                    f4 = Float.valueOf(fields[4]);
                    
                    if (dayOpen<0) dayOpen = f1; //runs only once

                    double [] values = {f1, f2, f3, f4};

                    Bar bar = new Bar(counter++, values, dayOpen, timeStamp);

                    bars.add(bar);
                }
            }
            
           System.out.println("numberOfBars=" + counter);
            
           reader.close();
            
        }
        catch(Exception ex) {
            System.out.println(ex.getMessage());
        }        
    }
}
