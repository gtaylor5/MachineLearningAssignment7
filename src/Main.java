import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;


public class Main {

    public Main(String[] tracks){
        try {
			executeValueIteration(tracks);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    static PrintWriter writer;
    
    /***********************************
     * 
     *    Value Iteration runner
     * 
     **********************************/
    
    public void executeValueIteration(String[] tracks) throws IOException{
        for(String track : tracks){
        	writer = new PrintWriter(new FileWriter(track.split("\\.")[0]+"Results.txt",true));
            ArrayList<Integer> numberOfIterations = new ArrayList<Integer>();
            ArrayList<Integer> simulationResults = new ArrayList<Integer>();
            writer.println(track + " : ");
            writer.println();
            RaceTrack r = RaceTrack.initializeTrack(track);
            r.setActions();
            r.printTrack();
            writer.println();
            for(int i = 0; i < 10; i++){
                    
                ValueIteration vi = new ValueIteration();
                vi.r = r;
                vi.initializeStates();
                if(track.equalsIgnoreCase("O-track.txt")){
                    vi.ep = 0.000000001;
                    vi.gamma = .5;
                }else if(track.equalsIgnoreCase("L-track.txt")){
                    vi.ep = 0.000000001;
                    vi.gamma = .4;
                }
                numberOfIterations.add(vi.valueIteration());
                for(int j = 0; j < 10; j++){
                    simulationResults.add(vi.simulate());
                }

            }
            
            writer.println("Statistics: ");
            
            writer.println("The average number of iterations for Value Iteration on " + track.split("\\.")[0] + ": "
                    + mean(numberOfIterations));
            writer.println("Variance of Number of Iterations: " + variance(numberOfIterations));
            writer.println("Standard Deviation of Number of Iterations: "+Math.sqrt(variance(numberOfIterations)));
            double standardError = Math.sqrt(variance(numberOfIterations))/Math.sqrt(numberOfIterations.size());
            double margin = standardError/2.0;
            writer.println("Confidence Interval: " + (mean(numberOfIterations)-margin) + " to " + (mean(numberOfIterations)+margin));
            writer.println();
            
            
            writer.println("The average number of moves from start to finish on " + track.split("\\.")[0] + ": "
                    + mean(simulationResults));
            writer.println("Variance of Number of Iterations: " + variance(simulationResults));
            writer.println("Standard Deviation of Number of Iterations: "+Math.sqrt(variance(simulationResults)));
            standardError = Math.sqrt(variance(simulationResults))/Math.sqrt(simulationResults.size());
            margin = standardError/2.0;
            writer.println("Confidence Interval: " + (mean(simulationResults)-margin) + " to " + (mean(simulationResults)+margin));
            writer.println();

            writer.close();
        }
    }
    
    /***********************************
     * 
     *    Calculate variance
     * 
     **********************************/
   
    
    double variance(ArrayList<Integer> arr){
        double sum = 0;
        for(Integer val: arr){
            sum+=Math.pow(val-mean(arr),2);
        }
        return (double)sum/((double) arr.size());
    }
    
    /***********************************
     * 
     *    Calculate mean
     * 
     **********************************/
    
    double mean(ArrayList<Integer> arr){
        double sum = 0;
        for(Integer val : arr){
            sum+=val;
        }
        return (double)sum/((double)arr.size());
    }
    
    public static void main(String[] args) {
		String[] tracks = {"L-track.txt", "O-track.txt", "R-track.txt"};
		@SuppressWarnings("unused")
		Main m = new Main(tracks);
	}
    
}
