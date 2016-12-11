import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class QLearning {
    
    HashSet<State> states = new HashSet<State>();
    HashSet<String> statesAsString = new HashSet<String>();
    HashMap<String, State> currentValue = new HashMap<String, State>();
    HashMap<String, State> futureValue = new HashMap<String, State>();
    HashMap<String, Action> policy = new HashMap<String, Action>();
    ArrayList<StateActionPair> sap = new ArrayList<StateActionPair>(); //all state action pairs.
    
    double gamma = .5;
    double eta = .25;
    boolean badCrash = false;
    
    RaceTrack r;
    
    public static void main(String[] args) {
        RaceTrack r;
        try {
            r = RaceTrack.initializeTrack("L-track.txt");
            r.setActions();
            r.printTrack();
            QLearning q = new QLearning();
            q.r = r;
            q.initializeStates();
            q.initializeSAPs();
            q.executeQLearningAlgo();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }
    
    public void executeQLearningAlgo(){
        
        //int[] start = r.randomStart(); // random start location
        
        int i = 0;
        Date date = new Date();
        double max = 0;
        do{
            System.out.println(i);
            long start = date.getTime();
            int j = 0;
            for(State s : states){
                j++;
                ArrayList<StateActionPair> SAPs = new ArrayList<StateActionPair>();
                for(StateActionPair sap : this.sap){
                    if(sap.stateAsString.equalsIgnoreCase(s.stateAsString)){
                        SAPs.add(sap);
                    }
                }
                double maxDif = Double.NEGATIVE_INFINITY;
                for(StateActionPair sap : SAPs){
                    State temp = new State(sap.updateSpeedAndLocation(sap.a));
                    ArrayList<StateActionPair> futureSAPs = new ArrayList<StateActionPair>();
                    
                    for(StateActionPair sap1 : this.sap){
                        if(sap1.stateAsString.equalsIgnoreCase(temp.stateAsString)){
                            futureSAPs.add(sap1);
                        }
                    }
                    
                    
                    for(StateActionPair sap1 : futureSAPs){
                        if(sap1.qValue > max){
                            max = sap1.qValue;
                        }
                    }
                    double current = sap.qValue;
                    double newVal = 0;
                    if(r.isWall(sap)){
                        sap.qValue = sap.qValue + eta*(-100 + gamma*(max - sap.qValue)); // bigger cost for hitting wall
                        newVal = sap.qValue;
                    }else {
                        sap.qValue = sap.qValue + eta*(getCost(sap) + gamma*(max - sap.qValue));
                    }
                    if(Math.abs(current-newVal) > max){
                        max = Math.abs(current-newVal);
                    }
                }
                System.out.println(j);
            }
            long end = date.getTime();
            long diff = end-start;
            long diffSeconds = diff / 1000 % 60;  
            long diffMinutes = diff / (60 * 1000) % 60; 
            System.out.println(diffMinutes +":"+diffSeconds);
            i++;
        }while(max > .00001);
    }
    
    void printQVals(){
        for(StateActionPair s: sap){
            System.out.println(s.qValue);
        }
    }
    
    /***********************************
     * 
     *    Get all possible transition
     *    gains
     * 
     **********************************/
    
    public HashMap<String, State> getTransitionFunctionResults(State s, Action a){
        
        HashMap<String, State> map = new HashMap<String, State>(); // mapping
        // if wall assume hard crash and return mapping
        if(r.isWall(s)){
            for(String str : r.startLocations){
                String[] arr = str.split(",");
                State temp = new State(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]),0,0);
                temp.value = 1.0/r.startLocations.size();
                map.put(temp.stateAsString, currentValue.get(temp.stateAsString));
            }
            return map;
        }else{
            
            State accelerationSuccessful = s.updateSpeedAndLocation(a); // update speed and location based on action
            State accelerationFailed = s.updateSpeedAndLocation(new Action(0,0)); // acceleration failed so default action is (0,0)
            
            //return values from possible crashes from both situations
            
            HashMap<String, State> success = assessCrash(s, accelerationSuccessful);
            HashMap<String, State> failed = assessCrash(s, accelerationFailed);
            
            //calculate and store results
            
            for(String key : success.keySet()){
                success.get(key).value = success.get(key).probability*.8;
                map.put(key, success.get(key));
            }
            
            for(String key : failed.keySet()){
                failed.get(key).value = failed.get(key).probability*.2;
                map.put(key, failed.get(key));
            }
            
            //return map
            
            return map;
        }
    }
    
    /***********************************
     * 
     *    Assess all crash situations
     *    and return values accordingly.
     * 
     **********************************/
    
    HashMap<String, State> assessCrash(State current, State future){
        
        HashMap<String, State> map = new HashMap<String, State>();
        double val = Math.max(Math.abs(future.stateAsArray[2]), Math.abs(future.stateAsArray[3]));
        
        //already in wall and no change has been made.
        
        if(current.stateAsString.equalsIgnoreCase(future.stateAsString)){
            if(r.isWall(future)){
                for(String str : r.startLocations){
                    String[] arr = str.split(",");
                    State temp = new State(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]),0,0);
                    temp.probability = 1.0/r.startLocations.size(); // probability of picking particular start location
                    map.put(temp.stateAsString, temp);
                    break;
                }
                return map;
            }else{ // return future
                future.probability = current.value; 
                map.put(future.stateAsString, future);
                return map;
            }
        }
        
        //check all squares in between the destination and start for walls
        
        String position = "";
        
        for(int i = 0; i <= val; i++){
            double multiplier = (double) i/val;
            State s = new State(current.stateAsArray[0],current.stateAsArray[1],current.stateAsArray[2],current.stateAsArray[3]);
            s.stateAsArray[0] = (int)Math.round(s.stateAsArray[0] + multiplier*future.stateAsArray[2]);
            s.stateAsArray[1] = (int)Math.round(s.stateAsArray[1] + multiplier*future.stateAsArray[3]);
            s.reStringifyState();
            s.setPosition();
            if(r.isWall(s)){
                break;
            }else{
                position = s.position;
            }
        }
        
        //hit wall
        
        if(position.equalsIgnoreCase("")){
           
            for(String str : r.startLocations){
                String[] arr = str.split(",");
                State temp = new State(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]),0,0);
                temp.probability = 1.0/r.startLocations.size(); // probability of picking particular start location
                map.put(temp.stateAsString, temp);
            }
            
            return map;
        }
        
        //valid move
        
        if(position.equals(future.getPosition())){
            future.probability = currentValue.get(future.stateAsString).value; //probability of success
            map.put(future.stateAsString, future);
            return map;
        }
        
        //bad crash case
        
        if(badCrash){
            for(String str : r.startLocations){
                String[] arr = str.split(",");
                State temp = new State(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]),0,0);
                temp.probability = 1.0/r.startLocations.size(); // probability of picking particular start location
                map.put(temp.stateAsString, temp);
            }
            return map;
        }else{// soft crash
            State s = new State(current.stateAsArray[0], current.stateAsArray[1], current.stateAsArray[2], current.stateAsArray[3]);
            s.value = current.value;
            s.probability = current.probability;
            s.setPosition(position);
            s.setVelocity(0, 0);
            s.probability = 1.0;
            s.reStringifyState();
            map.put(s.stateAsString, s);
        }
        return map;
    } 
    
    public int getCost(State s){
        if(r.endLocations.contains(Integer.toString(s.stateAsArray[0]) + Integer.toString(s.stateAsArray[1]))){
            return 0;
        }
        return -1;
    }
    
    public void initializeStates(){
        int height = r.trackHeight;
        int width = r.trackWidth;
        
        for(int i = 0; i < height; i++){ //rows
            for(int j = 0; j < width; j++){ //columns
                for(int k = -5; k < 6; k++){ // v_x
                    for(int l = -5; l < 6; l++){ //v_y
                        State s = new State(j,i,k,l); // create state\
                        
                        //skip duplicate states
                        if(statesAsString.contains(s.stateAsString) || r.endLocations.contains(s.stateAsString)){
                            continue;
                        }
                        //add states in various places
                        statesAsString.add(s.stateAsString);
                        states.add(s);
                        currentValue.put(s.stateAsString, s);
                        futureValue.put(s.stateAsString, s);
                    }
                }
            }
        }
    }

    public void initializeSAPs(){
        for(State state : states){
            for(String key : r.actions.keySet()){
                StateActionPair sap = new StateActionPair(state, r.actions.get(key));
                this.sap.add(sap);
            }
        }
    }
    
}
