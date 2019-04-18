/* John Okechukwu  cs610 9256 prp */
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 *
 * @author okechukwu
 */
 public class pgrk9256 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        // input arguements
        //String ini = args[0]; 
        String filename = args[2]; 
        int initVal = Integer.parseInt(args[1]);
        int iter = Integer.parseInt(args[0]);        
        
        // object for reference methods
        //read file, and form arrays
        ArrayList<int[][]> filearray = references_9256.readfile(filename);
        
        
        /* Page Rank */
        pageRank_9256 pageRan = new pageRank_9256();
        pageRan.pageRank(iter, initVal, filearray);

        }  
}
class references_9256 {
    public static ArrayList<int[][]> readfile(String path){
        ArrayList<int[][]> Matrices = new ArrayList<int[][]>();
        int[][] adjMatrix = null; 
        int[][] adjMatrixInv = null;
        try{
            FileReader reader = new FileReader(path);
            
            BufferedReader buffReader = new BufferedReader(reader);
            Scanner run = new Scanner(buffReader);
            int n = run.nextInt();
            int m = run.nextInt();
            adjMatrix = new int[m][n];            
            adjMatrixInv = new int[m][n];
            for(int i = 0; i < n; i++){
                for(int j = 0; j < m; j++){//initialize matrix
                    adjMatrix[i][j] = 0;
                    adjMatrixInv[i][j] = 0;
                }        
            }
            while(run.hasNextLine()){// build adjacency matrix for hub calc. All nodes that point to a vertex set to 1.
                int src = run.nextInt();
                int dest = run.nextInt();
                adjMatrix[src][dest] = 1;
                adjMatrixInv[dest][src] = 1;
            }
            run.close();
            Matrices.add(adjMatrix);
            Matrices.add(adjMatrixInv);
            
        }
        catch(IOException e){
            System.out.println("Cannot open file" + path);
        }return Matrices;
    }
}
class pageRank_9256 {
    boolean val = true;
    double[][] pageR = null;
    int[] countOut = null;
   
                            //MAIN FUNTION
    public void pageRank(int iter, int iniValue, ArrayList<int[][]> Matrices){         
        int length = Matrices.get(0).length;
        pageR = new double[length][length];//current pagrR and old pageR
        countOut = new int[length];
        if(length > 10){iter = 0; iniValue = -1;}
        initPage(iniValue, length ); //initialize hub and auth scores to 1.  
        int[][] adjMatrix = Matrices.get(0); //for= outdegree matrix
        int[][] adjMatrixInv = Matrices.get(1);//indegree matrix
        counOut(adjMatrix);
        printBase();
        if(iter <= 0){// use errorate or set iterations
            do{
                val = true;//reset boolean
                int len = adjMatrixInv.length;   
                for(int i = 0; i < len; i++){ // transverse through update page rank of each vertex
                    double rankSum = sumIndeg(adjMatrixInv[i], i);
                    pageR[i][1] = pageR[i][0];// old pageR = new PageR
                    pageR[i][0] = rankSum; //set new pageR                    
                }printR();
           }while(iscalcConv(iter) == false);               
        }else{// iterations greater than 1 and number of vertices less than 10
            for(int it = 0; it < iter; it++ ){
            int len = adjMatrixInv.length;   
                for(int i = 0; i < len; i++){ // transverse through update page rank of each vertex
                double rankSum = sumIndeg(adjMatrixInv[i], i);  
                pageR[i][1] = pageR[i][0];// old pageR = new PageR
                pageR[i][0] = rankSum ; //set new pageR
                }printR();
            }
        }
    }
 /*                         Auxillary Functions  */ 
    public double sumIndeg(int[] vertex, int vertIndex){// pass in vertex
        double sumIned = 0;
        double fin = 0;
        for(int i = 0; i < vertex.length; i++){//add all the hubscores of vertexes incident from 
            if(vertex[i] == 1){
                sumIned += pageR[i][0]/countOut[i];       
            }fin = .85*sumIned+(1-.85)/4;
        }return fin;
    }    
    public void initPage(int initValue, int len){
        double initVal = 0;
        for(int i = 0; i < len; i++){
            double lengInv = (len);
            lengInv = 1/lengInv;
            pageR[i][0] = lengInv;
            pageR[i][1] = 0;
            countOut[i] = 0;
        }
    }
    public void counOut(int[][] adjMat){// get count of out degree for each vertex
        int out = 0;
        for(int i = 0; i < adjMat.length; i++){
            out = 0;
            for(int j = 0; j < adjMat[0].length; j++){
                if(adjMat[i][j] == 1){
                    out += 1;       
                }
            }countOut[i] = out;
        }
    }
   
    public boolean iscalcConv(int iter){//determine err based on iter value input
        double err = 0;
        if(iter == 0)err = 0.00001;
        else err = Math.pow(10, iter);       
        for(int i = 0; i < pageR.length; i++){
            double pageDiff = Math.abs(pageR[i][0] - pageR[i][1]);
            if(pageDiff > err){
               val = false;
            }                
        }return val;
    }
    public void printBase(){
        System.out.print("Base:");
        for(int i = 0; i<pageR.length;i++){      
            System.out.printf("P[%d]=%.6f ",i,pageR[i][0]);            
        }
            System.out.print("\n");          
    }
    public void printR(){
        for(int i = 0; i<pageR.length;i++){
            System.out.printf("P[%d]=%.6f ",i,pageR[i][0]);           
        }
            System.out.print("\n");                       
    }    
       
}
