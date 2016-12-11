import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Main {

    public Main(String[] tracks){
        
    }
    
    
    public void executeValueIteration(String[] tracks){
        for(String track : tracks){
            ArrayList<Integer> numberOfIterations = new ArrayList<Integer>();
            ArrayList<Integer> simulationResults = new ArrayList<Integer>();
            for(int i = 0; i < 10; i++){
                try {
                    System.out.println(track + " : ");
                    System.out.println();
                    RaceTrack r = RaceTrack.initializeTrack(track);
                    r.setActions();
                    r.printTrack();
                    System.out.println();
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
                    
                } catch (FileNotFoundException e) {
                    System.out.println("Track could not be found");
                }
            }
            System.out.println("The average number of iterations for Value Iteration on " + track.split("\\.")[0] + ": "
                    + calculateAverages(numberOfIterations));
            System.out.println("The average number of moves from start to finish on " + track.split("\\.")[0] + ": "
                    + calculateAverages(simulationResults));
        }
    }
    
    public double calculateAverages(ArrayList<Integer> arr){
        double sum = 0;
        for(Integer val : arr){
            sum += (double) val;
        }
        return sum / (double) arr.size();
    }
    
}
