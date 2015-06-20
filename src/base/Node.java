package base;

/**
 *
 * @author Georgios Stroumpoulis
 */
public class Node {
    
    public static enum PosInBar {OP, HI, LO, CL, FK};
    
    private int index;
    private Bar bar;
    private int value;
    private int refLevel = 0;
    private PosInBar posInBar;
    private int seq = 1; //pollaplotita
    
    private Node() {}
    
    public Node(Bar b, PosInBar p) {
        bar = b;
        posInBar = p;
        refLevel = 0;
        switch (p) {
            case OP: {
                value = b.getNormalizedOpen();
                break;
            }
            case HI: {
                value = b.getNormalizedHigh();
                break;
            }
            case LO: {
                value = b.getNormalizedLow();
                break;
            }
            case CL: {
                value =b.getNormalizedClose();
                break;
            }
            default: break;
        }
    }
    
    public Node(Node nod) {
        bar = nod.bar;
        posInBar = nod.posInBar;
        refLevel = nod.refLevel;
        value = nod.value;  
        index = nod.index;
    }
    
    public Node(Node nod, int lv) {
        bar = nod.bar;
        posInBar = nod.posInBar;
        refLevel = lv;
        value = nod.value - nod.refLevel;   
        index = nod.index;
    }
       
    public Node makeFakeNodeAtLevel(int lv) {
        Node n = new Node();
        n.bar = this.bar;
        n.posInBar = PosInBar.FK;
        n.refLevel = lv;
        n.value = 0;
        n.index = -1;
        return n;
    }
    
    public Bar getBar() { return bar; }
    
    public int getValue() { return value; }
    
    public int getRawValue() { return refLevel + value;}
    
    public int getRefLevel() { return refLevel; }
    
    public PosInBar getPosInBar() {return posInBar;}
    
    public int getIndex() {return index;}
    
    public void setIndex(int ind) {index = ind;}
    
    public int getSeq() {return seq;}
    
    public void setSeq(int v) {seq = v;}
    
    @Override
    public String toString() {
        String ret;
        ret = "Value: " + value + " RefLevel: " + refLevel + " posInBar: " + posInBar + "   Bar: " + this.getBar().toString(); 
        return ret;
    }
    
    public String toSimpleString() {
        return ("Value: " + value);
    }
}
