
import java.io.*;
import java.util.HashSet;
import java.util.*;

public class RaceTrack {
    
    HashSet<String> startLocations = new HashSet<String>(); // start line
    HashSet<String> endLocations = new HashSet<String>(); // finish line
    HashSet<String> wallLocation = new HashSet<String>();
    HashMap<String, Action> actions = new HashMap<String, Action>();
    
    String[][] trackMakeUp; // Wall or Regular Road
    
    int trackHeight = 0; //y-direction
    int trackWidth = 0; //x-direction
    
    
    /**************************************
     * 
     * Global Method to initialize the track
     * 
     * @param path
     * @return
     * @throws FileNotFoundException
     **************************************/
    
    public static RaceTrack initializeTrack(String path) throws FileNotFoundException{
        Scanner fileScanner = new Scanner(new File(path));
        RaceTrack r = new RaceTrack();
        if(fileScanner.hasNextLine()){
            String[] line = fileScanner.nextLine().split(","); // get dimensions of track
            r.trackHeight = Integer.parseInt(line[0]);
            r.trackWidth = Integer.parseInt(line[1]);
            r.trackMakeUp = new String[r.trackHeight][r.trackWidth];
        }
        //fill track
        int i = 0;
        while(fileScanner.hasNextLine()){
            String[] line = fileScanner.nextLine().split("");
            for(int j = 0; j < line.length; j++){
                if(line[j].equalsIgnoreCase("#")){
                    r.trackMakeUp[i][j] = "W"; // Wall
                    String loc = Integer.toString(j) +","+ Integer.toString(i);
                    r.wallLocation.add(loc);
                }else if(line[j].equalsIgnoreCase("\\.")){
                    r.trackMakeUp[i][j] = "R"; // Road
                }else if(line[j].equalsIgnoreCase("F")){
                    r.trackMakeUp[i][j] = "F";
                    String loc = Integer.toString(j) +","+ Integer.toString(i);
                    r.endLocations.add(loc);
                }else if(line[j].equalsIgnoreCase("S")){
                    r.trackMakeUp[i][j] = "S";
                    String loc = Integer.toString(j) +","+ Integer.toString(i);
                    r.startLocations.add(loc);
                }else{
                    r.trackMakeUp[i][j] = "R";
                }
            }
            i++;
        }
        fileScanner.close();
        return r;
    }   
    
    /***********************************
     * 
     *    Fills hashmap of all possible
     *    actions
     * 
     **********************************/
    
    public void setActions(){
        for (int x= -1; x <= 1; x++){
            for (int y = -1; y <= 1; y++){
                Action a = new Action(x, y);
                actions.put(a.actionAsString,a);
            }
        }
    }
    
    /***********************************
     * 
     *    Checks if an input state is
     *    located beyond the boundaries
     *    of the map or is in a wall.
     * 
     **********************************/

    public boolean isWall(State s){
        if (s.stateAsArray[0] >= trackWidth || s.stateAsArray[0] < 0 || s.stateAsArray[1] >= trackHeight || s.stateAsArray[1] < 0){
            return true;
        }else if(this.wallLocation.contains(Integer.toString(s.stateAsArray[0])+","+Integer.toString(s.stateAsArray[1]))){
            return true;
        }
        return false;
    }
    
    /***********************************
     * 
     *    Print race track
     * 
     **********************************/
    
    public void printTrack(){
        for(int i = 0; i < trackMakeUp.length; i++){
            for(int j = 0; j < trackMakeUp[i].length; j++){
                if(startLocations.contains(new Point(i,j))){
                    System.out.print("S ");
                }else if(endLocations.contains(new Point(i,j))){
                    System.out.print("F ");
                }else{
                    System.out.print(trackMakeUp[i][j] + " ");
                }
            }
            System.out.println();
        }
    }
    
    /***********************************
     * 
     *    pick random starting point
     * 
     **********************************/
   
    public int[] randomStart(){
        int[] xy = new int[2];
        int randomItem = new Random().nextInt(startLocations.size());
        int i = 0;
        for(String loc : startLocations){
            if(i == randomItem){
                String[] temp = loc.split(",");
                xy[0] = Integer.parseInt(temp[0]);
                xy[1] = Integer.parseInt(temp[1]);
            }
            i++;
        }
        return xy;
    }
    
    /***********************************
     * 
     *    pick random action
     * 
     **********************************/
    
    public Action randomActon(){
        int randomItem = new Random().nextInt(actions.size());
        int i = 0;
        for(String str : actions.keySet()){
            if(i == randomItem){
                return actions.get(str);
            }
            i++;
        }
        return null;
    }
    
    /***********************************
     * 
     *    pick random action
     * 
     **********************************/
    
    public String[] randomEnd(){
        int randomItem = new Random().nextInt(endLocations.size());
        int i = 0;
        for(String str : endLocations){
            if(i == randomItem){
                return str.split(",");
            }
            i++;
        }
        return null;
    }
    
    
}
