/* John Okechukwu  cs610 9256 prp */

import java.io.*;
import java.util.*;
public class hdec9256 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
       
        try{
            int [] freq = new int[256];
            int endArr = 0;
            int count = 0;            
            String indexVal = "" ;
            
            File rdFile = new File(args[0]);
            FileInputStream inStream = new FileInputStream(rdFile);
            byte[] fileChars = new byte[(int) rdFile.length()];
            inStream.read(fileChars);


                    //break down file
            for(int i = 0; i < fileChars.length; i++){
                //identify breaking points in file
                    if((char)fileChars[i] == ']'){  
                        endArr = i;
                        break;
                    }
                    //identify false breaks
                    else if((char)fileChars[i] == '[' ||(char)fileChars[i] == '\n' || (char)fileChars[i] == ' '){
                        continue;
                    }else if((char)fileChars[i] == ','){
                        freq[count] = Integer.valueOf(indexVal);
                        indexVal = "";
                        count++;
                    }else{
                        indexVal += (char)fileChars[i];
                    }
            }
            //access references
            decRefs9256 refs = new decRefs9256();
            minHeap9256 minHeap = refs.createminHeap(freq);
            maxHeap9256 huffman = refs.createhuffTree(minHeap);     
            String[] prefix = new String[256];
            minHeap.createPre(minHeap.getNode(0),prefix);
            StringBuilder decResult = refs.decHuff(endArr, fileChars, huffman);
            refs.wrtFile(decResult, rdFile);
        }
        catch(FileNotFoundException e){System.out.println("File not found.");}
    }                  
}

class decRefs9256 {
	public static int [] calculateFreq(byte[] fileChars, int endArr) {
	int [] freq = new int[256]; //create frequency array
        int count = 0;
        String indexVal = "" ;
            for(int i = 0; i < fileChars.length; i++){
                if((char)fileChars[i] == ']'){ //breaking point of array
                    endArr = i;
                    break;
                }
                else if((char)fileChars[i] == '[' || (char)fileChars[i] == ' ' || (char)fileChars[i] == '\n'){
                        continue;
                }else if((char)fileChars[i] == ','){
                        freq[count] = Integer.valueOf(indexVal);
                        indexVal = "";
                        count++;
                }else{
                       indexVal += (char)fileChars[i];
                }
            }return freq;		
	}
	public static minHeap9256 createminHeap(int [] freq) {
	minHeap9256 minHeap = new minHeap9256();
            for(int i = 0; i < freq.length; i++){
                if(freq[i] != 0){
                    minHeap.ins(new node9256("", freq[i], null, null, (char)i));
                }
            }return minHeap;	
	}
	public static maxHeap9256 createhuffTree(minHeap9256 minHeap) {		
        maxHeap9256 huffFin = new maxHeap9256();
        node9256 minFreq1, minFreq2;
        int len = 0;
        if(minHeap.getHeapSz() % 2 != 0){
            len = 1;
        }      
        int total;
        while(minHeap.getHeapSz() > len){
            minFreq1 = minHeap.ext();
            minFreq2 = minHeap.ext();
            total = minFreq1.getFreq() + minFreq2.getFreq();
            minHeap.ins(new node9256("", total, minFreq1, minFreq2,'*' ));
            huffFin.ins(new node9256("", total, minFreq1, minFreq2,'*' ));
            if(minHeap.getHeapSz() % 2 != 0){
                len = 1;
            }else{
                len = 1;
            }
        }return huffFin;     
	}	
	public static StringBuilder decHuff(int arrayLen, byte[] chars, maxHeap9256 huffFin) {
	String emptStr = "";
        StringBuilder finalStr = new StringBuilder();
        for(int i = arrayLen+1 ; i < chars.length; i++){
            emptStr += Character.toString((char)chars[i]);
            for(int j = 0; j < huffFin.getHeapSz(); j++){
                if(huffFin.getNode(j).getLeft().getLeft() == null){
                    if(huffFin.getNode(j).getLeft().getPref().equals(emptStr)){
                        finalStr.append(huffFin.getNode(j).getLeft().getCha());
                        emptStr = "";
                    }
                }
                if(huffFin.getNode(j).getRight().getRight() == null){
                    if(huffFin.getNode(j).getRight().getPref().equals(emptStr)){
                        finalStr.append(huffFin.getNode(j).getRight().getCha());
                        emptStr = "";
                    }
                }
            }
        }return finalStr;	
	}
    public static void wrtFile(StringBuilder decStr, File inputFile) {
    	byte[] decByte = Base64.getDecoder().decode(decStr.toString().getBytes());
        String nameFile = inputFile.getName();
        int indexFin = nameFile.lastIndexOf(".");
        nameFile = nameFile.substring(0,indexFin);
        File output = new File(nameFile);
        try {
            output.createNewFile();
            FileOutputStream wFile = new FileOutputStream(output, false);
            wFile.write(decByte);
            wFile.flush();
            wFile.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }
    }	
}
class node9256 {
    String prefix;
    int freq;
    node9256 left, right;
    char cha;
    
    public node9256(String pref, int fre, node9256 l, node9256 r, char cha){
        prefix = pref; freq = fre; left = l; right = r;
    }
//getters and setters
    public void setFreq(int f){
        freq = f;
    }
    public int getFreq(){
        return freq;
    }
    public void setPref(String p){
        prefix = p;
    }
    public String getPref(){
        return prefix;
    }
    public node9256 getLeft(){
        return left;
    }
    public void setLeft(node9256 l){
        left = left;
    }
    public node9256 getRight(){
        return right;
    }
    public void setRight(node9256 r){
        right = r;
    }
    public char getCha(){
        return cha;
    }
}
class minHeap9256 {
    ArrayList<node9256> minHeap = new ArrayList<node9256>();
    int len = 0;
    void ins(node9256 node){
        minHeap.add(node);
        len++;
        heapifyUp();
    }
    
    public void heapifyUp(){
        int i = len - 1;
        while(hasPar(i) && minHeap.get(getPar(i)).getFreq() > minHeap.get(i).getFreq()){
            swp(getPar(i), i);
            i = getPar(i);
        }
    }
    public node9256 ext(){
        node9256 root = minHeap.get(0);
        minHeap.set(0, minHeap.get(len-1));
        minHeap.remove(len-1);
        len--;
        heapifyDwn();
        return root;
    }
        public void swp(int x, int y){
        node9256 temp = minHeap.get(x);
        minHeap.set(x, minHeap.get(y));
        minHeap.set(y,temp);
    }
        public node9256 getNode(int i){
            return minHeap.get(i);
        }
        
    public int getPar(int i){
        int parent = (int)Math.floor((i-1)/2);
        return parent;
        
    }
    public boolean hasL(int i){
        return getL(i) < len;
    }    
    public boolean hasR(int i){
        return getR(i) < len;
    }
    public boolean hasPar(int i){
        return getPar(i) < len;
    }
    public int getR(int i){ //get right of node 2*i+2
        return 2*i+2;
    }
    public int getL(int i){ //get left of node 2*i+1
        return 2*i+1;
    }
    public int getHeapSz(){
        return len;
    }
    public void createPre(node9256 root, String[] pre){
        if(root.getLeft() != null){
        String leftPre = (root.getPref() + 0);
        root.getLeft().setPref(leftPre);
        createPre(root.getLeft(), pre);
        
        String rightPre = (root.getPref() + 1);
        root.getRight().setPref(rightPre);
        createPre(root.getRight(), pre);       
        }
    }    
    public void heapifyDwn(){ 
        int min;
        int root = 0;
        
       while(hasL(root)){
            min = root; 
            if(hasL(root) && minHeap.get(getL(root)).getFreq() < minHeap.get(min).getFreq() ){
                min = getL(root);
            }
            if(hasR(root) && minHeap.get(getR(root)).getFreq()  < minHeap.get(min).getFreq()){
                min = getR(root); 
            }else{
                break;
            }           
            if(min != root){
                swp(root, min);
            }root = min;           
        }
    }         
}
