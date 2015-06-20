package base;

import java.util.*;

/**
 *
 * @author Georgios Stroumpoulis
 */
public class NodeManager {

    protected int counter = 0;
    protected ArrayList<Node> rawNodes;
    protected ArrayList<Node> compressedNodes;
    

    public NodeManager(ArrayList<Bar> bars) {
        rawNodes = new ArrayList<Node>();
        compressedNodes = null;
        counter = 0;
        for (Bar bar: bars) {
            Node []barNodes = bar.toNodes();
            for(int i=0; i<4; i++) {
                Node node = barNodes[i];
                node.setIndex(counter++);
                rawNodes.add(node);
            }
        }
    }
    
    public ArrayList<Node> getCompressedNodes() {
        if (compressedNodes != null) return compressedNodes;
        compressedNodes = new ArrayList<Node>();     
        int value = -1000;
        int seq = 1; 
        for (Node node : rawNodes) {   
            if (node.getValue() == value) seq++;
            else {
                Node compNode = new Node(node);               
                value = compNode.getValue();
                if (seq > 1) { compressedNodes.get(compressedNodes.size() - 1).setSeq(seq); }
                seq = 1;
                compressedNodes.add(compNode); 
            }
        }
        if (seq > 1) { compressedNodes.get(compressedNodes.size() - 1).setSeq(seq); }
        return compressedNodes;        
    }
    
    public ArrayList<Node> getNormalizedAtLevel(int level) {
        ArrayList<Node> compList = this.getCompressedNodes();
        ArrayList<Node> normList = new ArrayList<Node>();
        int value = 0;
        for (Node compNode: compList) {
            Node normNode = new Node(compNode, level);
            if (normNode.getValue() * value < 0) { //add zeros when sign changes
                normList.add(normNode.makeFakeNodeAtLevel(level));
            }
            normList.add(normNode);
            value = normNode.getValue();
        }
        
        return normList;
    }
    
    public LinkedList<Node> getTrianglesAtLevel(int level) {
        
        ArrayList<Node> normList = this.getNormalizedAtLevel(level); 
        LinkedList<Node> triangleList = new LinkedList<Node>();
        
        Node startNode;
        Iterator<Node> normIter = normList.listIterator();
        do {
            startNode = normIter.next();
        } while (startNode.getValue() != 0 && normIter.hasNext());
        
        if (startNode.getValue() != 0) return null;
        
        Node peakNode = startNode;
        triangleList.add(startNode);
        
        while (normIter.hasNext()) {
            Node node = normIter.next();
            if (node.getValue() == 0) {
                triangleList.add(peakNode);
                triangleList.add(node);
                peakNode = node;
            }
            else {
                if (Math.abs(node.getValue()) > Math.abs(peakNode.getValue())) {
                    peakNode = node;
                }
            }
        }
        triangleList.add(peakNode); //adds last triangle - very important
        return triangleList;   
    }
    
    public static LinkedList<Node> simplifyTriangleList(LinkedList<Node> triangleList) {
        LinkedList<Node> simpList = new LinkedList<Node>();
        Iterator<Node> iter = triangleList.listIterator();
        while (iter.hasNext()) {
            Node node = iter.next();
            if (node.getValue() != 0 ) {
                simpList.add(node);
            }
        } 
        return simpList;
    }
    
    public static HashMap<String, Node> makeTable(LinkedList<Node> triangleList) {
        
        //System.out.println("makeTable");
        
        HashMap<String, Node> map = new HashMap();
        Node node;
        int oddCounter, i, index, maxIndex, minIndex, peakIndex, prevPeakIndex, sgn,minValue, maxValue, peakValue;
        String peakName;
        
        Node firstNode = triangleList.get(0);
        LinkedList<Node> enhancedTriangleList = NodeManager.simplifyTriangleList(triangleList);
        enhancedTriangleList.addFirst(firstNode);
        
        //printLinkedListToScreen("enhanced" ,enhancedTriangleList);
        
        ArrayList<Node> simpList = NodeManager.convertLinkedListToArrayList(enhancedTriangleList);     
       
        maxIndex = minIndex = -1;
        
        i = 0;
        peakIndex = 0;
        minValue = 5000;
        maxValue = -5000;
        //findMaxMin
        while (i < simpList.size()) {
            node = simpList.get(i);
            if (node.getValue() > maxValue) { maxIndex = i; maxValue = simpList.get(i).getValue();}
            if (node.getValue() < minValue) {minIndex = i; minValue = simpList.get(i).getValue();}
            i++;
        }
        
        if (maxIndex > minIndex){// || (simpList.size() == 1 && simpList.get(0).getValue() > 0)) {
            sgn = 1;
            oddCounter = 5;
            peakIndex = maxIndex;
            //peakValue = maxValue;
        } 
        else {
            sgn = -1;
            oddCounter = 7;
            peakIndex = minIndex;
            //peakValue = minValue;
        }

        peakName = (sgn > 0 ? "h" : "d") + oddCounter;
        map.put(peakName, simpList.get(peakIndex));
        
        prevPeakIndex = simpList.size();

        findBetweens(sgn, peakIndex, prevPeakIndex, oddCounter, simpList, map);
        
        //System.out.println("makeTable 1");
        
        while(peakIndex > 0) {
            
            //System.out.println();
            //System.out.println();
            
            //find previous peak
            sgn = -sgn;
            oddCounter -= 2;
            prevPeakIndex = peakIndex;
           
            
            //System.out.println(peakName + "  peakIndex=" + peakIndex + " sgn=" + sgn);
            
            peakValue = 0;
            boolean found = false;
            index = peakIndex;
            i = 0;
            while (i < index) {
                node = simpList.get(i);
                //System.out.println(" : " + i + " - " + (sgn * node.getValue()) + " - " + (sgn * peakValue) +  "        peakIndex= " + peakIndex);
                if (sgn * node.getValue() > sgn * peakValue) {
                    peakIndex = i;
                    peakValue = node.getValue();
                    found = true;
                }     
                i++;
            }
            if (!found) peakIndex = 0;
            
            peakName = (sgn > 0 ? "h" : "d") + oddCounter;
            map.put(peakName, simpList.get(peakIndex));

            findBetweens(sgn, peakIndex, prevPeakIndex, oddCounter, simpList, map);

        } 
        
        //System.out.println("makeTable 2");
        
        return map;
    }
    

    
    
    public static void findBetweens(int sgn, int peakIndex, int prevPeakIndex, int oddCounter, ArrayList<Node> simpList, HashMap<String, Node> map) {
        
        int j = peakIndex + 1;
        int middleSign = -sgn; 
        int middleIndex = -1,  midValue = 0, middleIndex2,  midValue2;
        Node node;
        String peakName;
        
        while (j < prevPeakIndex) {
            node = simpList.get(j);
            if (middleSign * node.getValue() > middleSign * midValue ) { 
                middleIndex = j; midValue = node.getValue();
            }  
            j++;
        }
        
        if (middleIndex > 0) {
            peakName = (middleSign > 0 ? "h" : "d") + (oddCounter +1);
            map.put(peakName, simpList.get(middleIndex));
            int k = middleIndex + 1;
            middleSign = sgn;
            middleIndex2 = -1;
            midValue2 = 0;
            while (k < prevPeakIndex) {
                node = simpList.get(k); 
                if (middleSign * node.getValue() > middleSign * midValue2) { middleIndex2 = k; midValue2 = node.getValue();}
                k++;
            } 
            if (middleIndex2 > 0) {
                peakName = (middleSign > 0 ? "h" : "d") + (oddCounter +1);
                map.put(peakName, simpList.get(middleIndex2));
            }
        }
        
        //addition for d4bef, h6bef
        if (middleIndex >0) {
            j = peakIndex + 1;
            int befSign = sgn;
            int befIndex = -1;
            int befValue = 0;
            while(j < middleIndex) {
                node = simpList.get(j);
                if (befSign * node.getValue() > befSign * befValue ) { 
                    befIndex = j; befValue = node.getValue();
                }  
                j++;
            }
            if (befIndex>0) {
                peakName = (befSign >0 ? "h" : "d") + (oddCounter +1) + "bef";
                map.put(peakName, simpList.get(befIndex));
            }
        }
    }
    
    public static void printMakeTableToScreen(HashMap<String, Node> map) {
        
        HashMap<String, Integer> valuesMap = new HashMap<String, Integer>();
        for (Map.Entry<String, Node> entry : map.entrySet()) {
            valuesMap.put(entry.getKey(), entry.getValue().getValue());
        }
        
        ArrayList<String> stringList = new ArrayList<String>(Arrays.asList("h-7", "d-5", "h-3", "d-1", "h1", "d2", "h2", "d3", "h4", "d4", "h5", "d6", "h6", "d7", "h8", "d8", "d4bef", "h6bef"));
        ArrayList<String> nameList = new ArrayList<String>(Arrays.asList("hb", "db", "h0", "d0", "h1", "d2", "h2", "d3", "h4", "d4", "h5", "d6", "h6", "d7", "h8", "d8", "d4bef", "h6bef"));
        System.out.println();
        for (int i=0; i < stringList.size(); i++) {
            String s = stringList.get(i);
            int value = (valuesMap.containsKey(s) ? valuesMap.get(s) : 0); 
            String name = (i < nameList.size() ? nameList.get(i) : s);
            if (value!=0) System.out.println(name + "=\t" + value + "\t at bar: " + map.get(s).getBar().getIndex());
        }
       
    }
    
    
    public static ArrayList<Integer> getMapValuesForLabels(HashMap<String, Node> map,ArrayList<String> stringList) {
        
        HashMap<String, Integer> valuesMap = new HashMap<String, Integer>();
        for (Map.Entry<String, Node> entry : map.entrySet()) {
            valuesMap.put(entry.getKey(), entry.getValue().getValue());
        }
        ArrayList<Integer> values = new ArrayList<Integer>();
        
        for (int i=0; i < stringList.size(); i++) {
            String s = stringList.get(i);
            int value = (valuesMap.containsKey(s) ? valuesMap.get(s) : 0); 
            values.add(new Integer(value));
        }
        
        return values;
    }
    
    public static LinkedList<Node> getZigZagMaxToStart(LinkedList<Node> triangleList) {
        LinkedList<Node> zzList = new LinkedList<Node>();
        Node node;
        LinkedList<Node> simpList = NodeManager.simplifyTriangleList(triangleList);    
        ListIterator<Node> iter = simpList.listIterator();
       
        int peakIndex = 0;
        int sgn = 1;
        Node peakNode = iter.next();
        //findMax
        while (iter.hasNext()) {
            node = iter.next();
            if (node.getValue() > peakNode.getValue()) {
                peakIndex = iter.previousIndex(); 
                peakNode = node;
            }
        }
        zzList.add(peakNode);
        
        ListIterator<Node> peakIter = simpList.listIterator(peakIndex);
        
        while(peakIter.hasPrevious()) {
            sgn *= -1;
            iter = simpList.listIterator(peakIndex);
            //System.out.println(peakIter.nextIndex());
            while (iter.hasPrevious()) {
                node = iter.previous();
                if (sgn * node.getValue() > sgn * peakNode.getValue() ) {
                    peakIndex = iter.nextIndex();
                    peakNode = node;
                }   
                //System.out.print("pit:" + peakIter.nextIndex() + " it:" + iter.nextIndex() + " = " + node.getValue() + "     ");           
            }
            zzList.add(peakNode);
            peakIter = simpList.listIterator(peakIndex);
        } 
        
        return zzList;
    }
    
    public ArrayList<Node> getRawNodes() {return rawNodes;}
    
    public static LinkedList<Node> convertArrayListToLinkdedList(ArrayList<Node> arList) {
        LinkedList<Node> lnList = new LinkedList<Node>();
        
        for (Node n: arList)         
            lnList.add(n);
            
        return lnList;
    }
    
    public static ArrayList<Node> convertLinkedListToArrayList(LinkedList<Node> lnList) {
        ArrayList<Node> arList = new ArrayList<Node>();
        
        for (Node n: lnList)         
            arList.add(n);
            
        return arList;
    }
    
    public static void printLinkedListToScreen(String listName, LinkedList<Node> list) {
        int nodesPerLine = 24;
 
        System.out.println(listName);

        for (ListIterator<Node> it = list.listIterator() ; it.hasNext() ;) {

            if (it.nextIndex() % nodesPerLine == 0) {
                System.out.println();
            }
            else {
                System.out.print("   ");
            }

            System.out.format("%5s" , it.next().getValue());
        }
        System.out.println();
    }
}
