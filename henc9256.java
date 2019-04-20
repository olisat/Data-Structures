/* John Okechukwu  cs610 9256 prp */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jokechukwu
 */
import java.io.*;
import java.util.*;
public class henc9256 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
            encRefs9256 refs = new encRefs9256();           
            File rdFile = new File(args[0]);
            String string64 = refs.toBase64(rdFile);
            int[] freq = new int[256];
            for (int i = 0; i < string64.length(); i++){
                freq[string64.charAt(i)] += 1;
            }           
            
            minHeap9256 heap = refs.createMinHeap(freq);               
            node9256 minFreq1 = null, minFreq2 = null;
            maxHeap9256 huffman = refs.huf(heap, minFreq1,minFreq2);

            String[] pref = new String[256];
            heap.createPre(heap.getNode(0),pref);
            StringBuilder encResult = refs.strEnc(string64, huffman);
            refs.wrtFile(rdFile, freq, encResult);
            
    }
}

class encRefs9256 {
	public static String toBase64(File i) throws IOException {
            FileInputStream inStr = new FileInputStream(i);
            byte[] chars = new byte[(int)i.length()];       
            inStr.read(chars);
            return Base64.getEncoder().encodeToString(chars);
        }	
	public static void wrtFile(File x, int[] y, StringBuilder z) throws IOException {
            String fileName = x.getName();
            BufferedWriter wFile =  new BufferedWriter(new FileWriter(fileName + ".huf"));
            wFile.write(Arrays.toString(y));
            wFile.write(z.toString());
            wFile.flush();
            wFile.close();
	}	
	public static StringBuilder strEnc(String str, maxHeap9256 maxHeap) {
            StringBuilder encondedStr = new StringBuilder();
            for(int i = 0 ; i < str.length(); i++){
                for(int j = 0; j < maxHeap.getHeapSz(); j++){
                   if(maxHeap.getNode(j).getLeft().getLeft() == null){
                       if(str.charAt(i) == maxHeap.getNode(j).getLeft().getCha()){
                           encondedStr.append(maxHeap.getNode(j).getLeft().getPref());
                       }
                   }
                   if(maxHeap.getNode(j).getRight().getRight() == null){
                       if(str.charAt(i) == maxHeap.getNode(j).getRight().getCha()){
                           encondedStr.append(maxHeap.getNode(j).getRight().getPref());
                       }
                   }
                }
            }return encondedStr;
	}	
	public static maxHeap9256 huf(minHeap9256 min,node9256 minFreq1,node9256 minFreq2) {
            int total;
            int len = 0;
            maxHeap9256 huffFin = new maxHeap9256();
            if(min.getHeapSz() % 2 != 0){
                len = 1;
            }
            while(min.getHeapSz() > len){
                minFreq1 = min.ext();
                minFreq2 = min.ext();
                total = minFreq1.getFreq() + minFreq2.getFreq();
                min.ins(new node9256("",total ,minFreq1, minFreq2,'*'));
                huffFin.ins(new node9256("",total ,minFreq1, minFreq2,'*'));
            }return huffFin;	
	}
	public static minHeap9256 createMinHeap(int[] freq) {
            minHeap9256 minHeap = new minHeap9256();
            for(int i = 0; i < freq.length; i++){
                if(freq[i] != 0){
                    minHeap.ins(new node9256( "", freq[i],null, null,(char)i));
                }
            }return minHeap;		
	}
}

class maxHeap9256 {
    int len = 0;
    ArrayList<node9256> minHeap = new ArrayList<node9256>(); 
    void ins(node9256 node){
        minHeap.add(node);
        len++;
        heapifyUp();
    }
    void heapifyUp(){
        int i = len-1;
        while(hasPar(i) && minHeap.get(getPar(i)).getFreq() < minHeap.get(i).getFreq()) {
            swp(getPar(i), i);
            i = getPar(i);            
        }
    }
    public boolean hasPar(int i){
        return getPar(i) < len;
    }    
    public void swp(int x, int y){//swap nodes in heap
    node9256 temp = minHeap.get(x);
    minHeap.set(x, minHeap.get(y));
    minHeap.set(y,temp);
    }
    public node9256 getNode(int i){// retrieve node
        return minHeap.get(i);
    }    
    public int getPar(int i){
        int parent = (int)Math.floor((i-1)/2);
        return parent;  
    }
    public int getHeapSz(){
        return len;
    }    
}
