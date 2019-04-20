/* John Okechukwu  cs610 9256 prp */

import java.util.*;
import java.io.*;
import java.time.Duration;
import java.time.Instant;

public class Hdec_9256 {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException
    {
        if(args.length < 1){
        	System.out.println("Please provide with a file name!! Use below samples as example.");
            System.out.println("To decrypt: java hdec_9256 <filename.huf>");
            System.out.println("To ecrypt: java henc_9256 <filename.huf>");
        }else if(args.length  > 1){
            System.out.println("Make sure no extra space or word is after file name!! Use below samples as example.");
            System.out.println("To decrypt: java hdec_9256 <filename.huf>");
            System.out.println("To ecrypt: java henc_9256 <filename.huf>");
        }else
        
        {
            try{
            	
                File input = new File(args[0]);
                FileInputStream inStream = new FileInputStream(input);
                byte[] inFileChars = new byte[(int) input.length()];
                inStream.read(inFileChars);

                System.out.println("Decoding...");

                
                int [] frequency = new int[256];
                int count = 0;
                int arrayEnds = 0;
                String indexValue = "" ;

                for(int j = 0; j < inFileChars.length; j++){
                        if((char)inFileChars[j] == ']'){  
                            arrayEnds = j;
                            break;
                        }
                        else if((char)inFileChars[j] == '[' || (char)inFileChars[j] == ' ' || (char)inFileChars[j] == '\n'){
                            continue;
                        }else if((char)inFileChars[j] == ','){
                            frequency[count] = Integer.valueOf(indexValue);
                            indexValue = "";
                            count++;
                        }else{
                            indexValue += (char)inFileChars[j];
                        }
                }

                BinMin_9256 minHeap = BuildMinHeap(frequency);

                BinMax_9256 huffman = createHuffmanTree(minHeap);
                
                String[] prefix = new String[256];
                minHeap.createPre(minHeap.getNode(0),prefix);

                StringBuilder decoded = decHuffman(arrayEnds, inFileChars, huffman);


                write(decoded, input);
                System.out.println("Decoding Has been Complete!");
            }
            

            catch (FileNotFoundException e){
                System.out.println("File not found! Please provide a valid file name.");
            }
        }
    }

	public static int [] calculateFreq(byte[] inFileChars, int arrayEnds) {
		int [] freq = new int[256];
        int count = 0;
        String indexValue = "" ;

        for(int j = 0; j < inFileChars.length; j++){
                if((char)inFileChars[j] == ']'){ 
                    arrayEnds = j;
                    break;
                }
                else if((char)inFileChars[j] == '[' || (char)inFileChars[j] == ' ' || (char)inFileChars[j] == '\n'){
                    continue;
                }else if((char)inFileChars[j] == ','){
                    freq[count] = Integer.valueOf(indexValue);
                    indexValue = "";
                    count++;
                }else{
                    indexValue += (char)inFileChars[j];
                }
        }
		return freq;
	}
	public static BinMin_9256 BuildMinHeap(int [] freq) {
		BinMin_9256 minHeap = new BinMin_9256();
        for(int i = 0; i < freq.length; i++){
            if(freq[i] != 0){
                minHeap.insert(new Node_9256(freq[i], (char)i, null, null, ""));
            }
        }
		return minHeap;
	}
	public static BinMax_9256 createHuffmanTree(BinMin_9256 minHeap) {
		
		BinMax_9256 huffman = new BinMax_9256();
		Node_9256 minFreq1, minFreq2;
        int size = 0;
        if(minHeap.getHeapSize() % 2 != 0){
            size = 1;
        }

        
        int sum;
        while(minHeap.getHeapSize() > size){
            minFreq1 = minHeap.extract();
            minFreq2 = minHeap.extract();
            sum = minFreq1.getFrequency() + minFreq2.getFrequency();
            minHeap.insert(new Node_9256(sum, '*', minFreq1, minFreq2, ""));
            huffman.insert(new Node_9256(sum, '*', minFreq1, minFreq2, ""));
            if(minHeap.getHeapSize() % 2 != 0){
                size = 1;
            }else{
                size = 1;
            }
        }
		return huffman;
        
	}
	
	public static StringBuilder decHuffman(int lengthOfArray, byte[] chars, BinMax_9256 huffman) {
		String t = "";
        StringBuilder d = new StringBuilder();

        for(int i = lengthOfArray+1 ; i < chars.length; i++){
            t += Character.toString((char)chars[i]);
            for(int j = 0; j < huffman.getHeapSize(); j++){
                if(huffman.get(j).getLeft().getLeft() == null){
                    if(huffman.get(j).getLeft().getPrefix().equals(t)){
                        d.append(huffman.get(j).getLeft().getChar());
                        t = "";
                    }
                }

                if(huffman.get(j).getRight().getRight() == null){
                    if(huffman.get(j).getRight().getPrefix().equals(t)){
                        d.append(huffman.get(j).getRight().getChar());
                        t = "";
                    }
                }
            }

        }
		return d;
	}
    public static void write(StringBuilder dec, File in) {
    	byte[] decByte = Base64.getDecoder().decode(dec.toString().getBytes());
        String inFileName = in.getName();
        int extIndex = inFileName.lastIndexOf(".");
        inFileName = inFileName.substring(0,extIndex);

        File output = new File(inFileName);
        try {
			output.createNewFile();
			FileOutputStream writer = new FileOutputStream(output, false);
	        writer.write(decByte);
	        writer.flush();
	        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
}


class BinMin_9256 {

    private ArrayList<Node_9256> min_heap = new ArrayList<Node_9256>();
    private int length = 0;
    
    
    public BinMin_9256(){
        this.length = 0;
    }
    
    public void insert(Node_9256 newNode){
        min_heap.add(newNode);
        length++;
        heapifyUp();
        
    }
    public void heapifyUp(){
        int i = length - 1;
        while(hasParent(i) && min_heap.get(getParent(i)).getFrequency()  > min_heap.get(i).getFrequency() ){
            swap(getParent(i), i);
            i = getParent(i);
        }
    }
    
    public Node_9256 extract(){
        Node_9256 root = min_heap.get(0);
        min_heap.set(0, min_heap.get(length-1));
        min_heap.remove(length-1);
        length--;
        heapifyDown();
        return root;
    }

    public void heapifyDown(){
        int root = 0;
        int min;
       
        while(hasLeft(root)){
            min = root; 
            if(hasLeft(root) && min_heap.get(getLeft(root)).getFrequency() < min_heap.get(min).getFrequency() ){
                min = getLeft(root);
            }
            if(hasRight(root) && min_heap.get(getRight(root)).getFrequency()  < min_heap.get(min).getFrequency() ){
                min = getRight(root); 
            }else{
                break;
            }
            
            if(min != root){
                swap(root, min);
            }
            root = min;
        }
        
    }
    
    public void swap(int x, int y){
        Node_9256 temp = min_heap.get(x);
        min_heap.set(x, min_heap.get(y));
        min_heap.set(y,temp);
    }
    
    public boolean hasLeft(int index){
        return getLeft(index) < length;
    }
    public boolean hasRight(int index){
        return getRight(index) < length;
    }
    public boolean hasParent(int index){
        return getParent(index) >= 0;
    }
    public int getParent(int index){
        return (int)Math.floor((index - 1) / 2);
    }
    public int getLeft(int index){
        return 2 * index + 1;
    }
    public int getRight(int index){
        return 2 * index + 2;
    }
    public int getHeapSize(){
        return length;
    }
    public Node_9256 getNode(int i){
        return min_heap.get(i);
    }
    public void createPre(Node_9256 root, String[] prefix){
        if(root.getLeft() != null){
            root.getLeft().setPrefix(root.getPrefix() + 0);
            createPre(root.getLeft(), prefix);

            root.getRight().setPrefix(root.getPrefix() + 1);
            createPre(root.getRight(), prefix);
        }
    }
}
class Node_9256 {
	private int frequency;
    private char ch;
    private Node_9256 left, right;
    private String prefix;
    
    public Node_9256 (int frequency, char ch, Node_9256 left, Node_9256 right, String prefix){
        this.frequency = frequency;
        this.ch = ch;
        this.left = left;
        this.right = right;
        this.prefix = prefix;

    }
    public void setPrefix(String prefix){
        this.prefix = prefix;
    }
    public int getFrequency(){
        return frequency;
    }
    public char getChar(){
        return ch;
    }
    public Node_9256 getLeft(){
        return left;
    }
    public Node_9256 getRight(){
        return right;
    }
    public String getPrefix(){
        return prefix;
    }
}