/* John Okechukwu  cs610 9256 prp */
import java.util.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
/**
 *
 * @author okechukwu
 */
public class hits9256 {

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
       
        /*HITS*/
        hits_9256 hit = new hits_9256();
        hit.hits(iter, initVal, filearray);//iter file initial value
        
        
        /* Page Rank */
        //pageRank pageRan = new pageRank();
        //pageRan.pageRank(iter, filearray, initVal);

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
class hits_9256 {
    boolean val = true;
    double authFac = 0;
    double hubFac = 0;
    double[][] scores = null;// arrays for auth, hub, and old hub          

                            //MAIN FUNTION
    public void hits(int iter, int iniValue, ArrayList<int[][]> Matrices){         
        int length = Matrices.get(0).length;
        scores = new double[length][4];//first col:auth second:oldauth third:hub fourth: old hub
        if(length > 10){iter = 0; iniValue = -1;}
        initScores(iniValue, length ); //initialize hub and auth scores to 1.  
        printBase();
        if(iter <= 0){// use errorate or set iterations
            do{
                val = true;//reset boolean
                int[][] adjMatrix = Matrices.get(0); //for calc hub
                int[][] adjMatrixInv = Matrices.get(1);//for calc auth
                int len = adjMatrixInv.length;   
                for(int i = 0; i < len; i++){ // transverse through all vertices and calculate auth score
                    double hubSum = calcAuth(adjMatrixInv[i]);  
                    scores[i][1] = scores[i][0]; //set old auth score
                    scores[i][0] = hubSum; //update auth score                         
                }
                for(int i = 0; i < len; i++){// transverse through all vertices and calculate hub scores
                    double authSum = calcHub(adjMatrix[i]);  
                    scores[i][3] = scores[i][2]; //set old hub score to current hub score
                    scores[i][2] = authSum;//set new hub score to calculated hub score
                }
                scaleScores();// scale auth and hub scores of all vertices
                printScores();    
                }while(iscalcConv(iter) == false);               
        }else{// iterations greater than 1 and number of vertices less than 10
            for(int it = 0; it < iter; it++ ){
                int[][] adjMatrix = Matrices.get(0); //for calc hub
                int[][] adjMatrixInv = Matrices.get(1);//for calc auth
                int len = adjMatrixInv.length;   
                for(int i = 0; i < len; i++){ // transverse through all vertices and calculate auth score
                    double hubSum = calcAuth(adjMatrixInv[i]);  
                    scores[i][1] = scores[i][0]; //set old auth score
                    scores[i][0] = hubSum; //update auth score                         
                }
                for(int i = 0; i < len; i++){// transverse through all vertices and calculate hub scores
                    double authSum = calcHub(adjMatrix[i]);  
                    scores[i][3] = scores[i][2]; //set old hub score to current hub score
                    scores[i][2] = authSum;//set new hub score to calculated hub score
                }
                scaleScores();// scale auth and hub scores of all vertices
                printScores();
                }
        
        };
    }
 /*                         Auxillary Functions  */ 
    public double calcAuth(int[] vertex){// pass in vertex
        double auth = 0;
        for(int i = 0; i < vertex.length; i++){//add all the hubscores of vertexes incident from 
            if(vertex[i] == 1){
                auth += scores[i][2];       
            }
        }return auth;
    }    
    public void initScores(int initValue, int len){
        double initVal = 0;
        switch(initValue){
            case 0: 
                initVal = 0;
                break;
            case 1: 
                initVal = 1;
                break;
            case -1: 
                initVal = len;
                initVal = 1/initVal;
                break;
            case -2: 
                initVal = 1/Math.sqrt(len);
                break; 
            case -3: 
                initVal = 1/Math.cbrt(len);
                break;				
        }
        if(len > 10)initVal = 1/len;
        for(int i = 0; i < len; i++ ){
            scores[i][2] = initVal;
            scores[i][0] = initVal;
        }    

    }
    public double calcHub(int[] vertex){// sum of auth scores of vertices that the index points to
        double hub = 0;
        for(int i = 0; i < vertex.length; i++){//add all the hubscores of vertexes incident from 
            if(vertex[i] == 1){
                hub += scores[i][0];       
            }
        }return hub;       
    }
    public void scaleScores(){
        int n = scores.length;
        double scaleAuth = 0;
        double scaleHub = 0;
        for(int i = 0; i < n; i++){
            scaleAuth += Math.pow(scores[i][0], 2);
            scaleHub += Math.pow(scores[i][2], 2);                     
        }
        scaleAuth = (1/scaleAuth);
        scaleHub = (1/scaleHub); 
        for(int i = 0; i < n; i++){// scale the auth scores for each vertex
            double currAuth = scores[i][0];
            double currHub = scores[i][2];
            scores[i][0] = currAuth*Math.sqrt(scaleAuth); 
            scores[i][2] = currHub*Math.sqrt(scaleHub);
        }
    }
   
    public boolean iscalcConv(int iter){//determine err based on iter value input
        double err = 0;
        if(iter == 0)err = 0.00001;
        else err = Math.pow(10, iter);       
        for(int i = 0; i < scores.length; i++){
            double authDiff = Math.abs(scores[i][0] - scores[i][1]);
            double hubDiff = Math.abs(scores[i][2] - scores[i][3]); 
            if(authDiff > err || hubDiff > err){
                val = false;
            }                 
        }return val;
    }
    public void printBase(){
        System.out.print("Base:");
        for(int i = 0; i<scores.length;i++){      
            System.out.printf("A/H[%d]=%.6f/%.6f ",i,scores[i][0],scores[i][2]);            
        }
            System.out.print("\n");          
    }
    public void printScores(){
        for(int i = 0; i<scores.length;i++){
            System.out.printf("A/H[%d]=%.6f/%.6f ",i,scores[i][0],scores[i][2]);
            
        }
            System.out.print("\n");                       
    }

}
