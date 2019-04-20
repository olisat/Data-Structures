/* John Okechukwu  cs610 9256 prp */

import java.io.*;
import java.util.*;

public class Henc_9256 {
	
	
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
                String string64 = toBase64(input);
                System.out.println("Compressing...");
                
                int[] freq = new int[256];
                for (int i = 0; i < string64.length(); i++)
                {
                    freq[string64.charAt(i)] += 1;
                }
                
                BinMin_9256 heap = createMinHeap(freq);
                
                Node_9256 minFreq1 = null, minFreq2 = null;
                BinMax_9256 huffman = huf(heap, minFreq1,minFreq2);
                
                String[] prefix = new String[256];
                heap.createPre(heap.getNode(0),prefix);
                
                StringBuilder encoded = encodeString(string64, huffman);
   
                writeFile(input, freq, encoded);
                System.out.println("Encoding Has been Complete!");
            }
            catch (FileNotFoundException e){
                System.out.println("File with the given name was not found!");
            }
        }
    }
	
    @SuppressWarnings("resource")
	public static String toBase64(File x) throws IOException {
		
        FileInputStream inStream = new FileInputStream(x);
        byte[] inFileChars = new byte[(int) x.length()];
        
			inStream.read(inFileChars);
			return Base64.getEncoder().encodeToString(inFileChars);
    }
	
	public static void writeFile(File x, int[] y, StringBuilder z) throws IOException {
		String inputFileName = x.getName();
        BufferedWriter writer =  new BufferedWriter(new FileWriter(inputFileName + ".huf"));
        writer.write(Arrays.toString(y));
        writer.write(z.toString());
        writer.flush();
        writer.close();
	}
	
	public static StringBuilder encodeString(String str, BinMax_9256 max) {
		StringBuilder enc = new StringBuilder();
		for(int i = 0 ; i < str.length(); i++){
            for(int j = 0; j < max.getHeapSize(); j++){
               if(max.get(j).getLeft().getLeft() == null){
                   if(str.charAt(i) == max.get(j).getLeft().getChar()){
                       enc.append(max.get(j).getLeft().getPrefix());
                   }
               }
               if(max.get(j).getRight().getRight() == null){
                   if(str.charAt(i) == max.get(j).getRight().getChar()){
                       enc.append(max.get(j).getRight().getPrefix());
                   }
               }
           }
       }
		return enc;
	}
	
	@SuppressWarnings("null")
	public static BinMax_9256 huf(BinMin_9256 min,Node_9256 minFreq1,Node_9256 minFreq2) {
		int sum;
		int size = 0;
		BinMax_9256 huffman = new BinMax_9256();
        if(min.getHeapSize() % 2 != 0){
            size = 1;
        }
        while(min.getHeapSize() > size){
            minFreq1 = min.extract();
            minFreq2 = min.extract();
            sum = minFreq1.getFrequency() + minFreq2.getFrequency();
            min.insert(new Node_9256(sum, '*', minFreq1, minFreq2, ""));
            huffman.insert(new Node_9256(sum, '*', minFreq1, minFreq2, ""));
        }
		return huffman;
	}

	public static BinMin_9256 createMinHeap(int[] freq) {
		BinMin_9256 minHeap = new BinMin_9256();
		for(int i = 0; i < freq.length; i++){
            if(freq[i] != 0){
                minHeap.insert(new Node_9256(freq[i], (char)i, null, null, ""));
            }
        }
		return minHeap;
	}
}

class BinMax_9256 {

    private ArrayList<Node_9256> minHeap = new ArrayList<Node_9256>();
    private int heapSize = 0;
    public void insert(Node_9256 newNode){
        minHeap.add(newNode);
        heapSize++;
        heapifyUp();
    }
    public void heapifyUp(){
        int i = heapSize - 1;
        while(hasParent(i) && minHeap.get(getParent(i)).getFrequency()  < minHeap.get(i).getFrequency() ){
            swap(getParent(i), i);
            i = getParent(i);
        }
    }


    public void swap(int x, int y){
        Node_9256 temp = minHeap.get(x);
        minHeap.set(x, minHeap.get(y));
        minHeap.set(y,temp);
    }
    public boolean hasParent(int i){
        return getParent(i) >= 0;
    }
    public int getParent(int i){
        return (int)Math.floor((i - 1) / 2);
    }
    public int getHeapSize(){
        return heapSize;
    }

    public Node_9256 get(int i){
        return minHeap.get(i);
    }
}
