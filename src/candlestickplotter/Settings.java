package candlestickplotter;

/**
 *
 * @author Georgios Stroumpoulis
 */
public class Settings {

    public static final int TWS_FRAME_WIDTH = 600;
    public static final int TWS_FRAME_HEIGHT = 200;   
    
    public static final int CANDLESTICK_FRAME_WIDTH = 1500;
    public static final int CANDLESTICK_FRAME_HEIGHT = 900;

    //public static final int DOT_SIZE = 10;
    public static final double BAR_WHITE_SPACE = 0.4; //percentage of 1 world space unit that remains empty when drawing a bar 
    
    public static final int X_MARGIN = 60; //distance of Y_AXIS from left edge of the panel
    public static final int Y_MARGIN = 70; //   >>       X_AXIS  >>  bottom     >>
    
    //scale limits for both axes
    public static final int MAX_SCALE_X = 30;
    public static final int MIN_SCALE_X = 2;
    
    public static final int MAX_SCALE_Y = 40;
    public static final int MIN_SCALE_Y = 5;
    
    // scale = dScreen/ SCALE_DENOMINATOR
    public static final int X_SCALE_DENOMINATOR = 5;
    public static final int Y_SCALE_DENOMINATOR = 5;
    
    //short tick length
    public static final int TICK_LINE_LENGTH = 20;
    
    public static final int MAX_BAR_NUMBER = 600;
    

    
}

