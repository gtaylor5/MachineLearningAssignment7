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
            for(int i = 0; i < 1; i++){
                    
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
            writer.println("The average number of iterations for Value Iteration on " + track.split("\\.")[0] + ": "
                    + calculateAverages(numberOfIterations));
            writer.println("The average number of moves from start to finish on " + track.split("\\.")[0] + ": "
                    + calculateAverages(simulationResults));
            writer.close();
        }
    }
    
    public double calculateAverages(ArrayList<Integer> arr){
        double sum = 0;
        for(Integer val : arr){
            sum += (double) val;
        }
        return sum / (double) arr.size();
    }
    
    public static void main(String[] args) {
		String[] tracks = {"L-track.txt", "O-track.txt", "R-track.txt"};
		@SuppressWarnings("unused")
		Main m = new Main(tracks);
	}
    
}
