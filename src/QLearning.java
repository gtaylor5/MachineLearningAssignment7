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
            //r.printTrack();
            QLearning q = new QLearning();
            q.r = r;
            q.initializeStates();
            q.initializeSAPs();
            q.executeQLearningAlgo();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
    }
    
    /***********************************
     * 
     *    Attempt at q learning algo.
     * 
     **********************************/
    
    public void executeQLearningAlgo(){
        for(String loc : r.startLocations){ // episodes
            String[] pt = loc.split(",");
            int[] start = {Integer.parseInt(pt[0]), Integer.parseInt(pt[1])}; // start location
            State starts = new State(start[0],start[1],0,0); // random start state.
            StateActionPair racer = new StateActionPair(starts,new Action(0,0)); // racers start state
            racer.printState(1);
            System.out.println();
            int i = 0;// episode counter.
            double max = 0;
            do{
                
                //get all state-action pairs at racers location
                ArrayList<StateActionPair> list = new ArrayList<StateActionPair>();
                for(StateActionPair sa : sap){
                    if(sa.stateAsString.equalsIgnoreCase(racer.stateAsString)){
                        list.add(sa);
                    }
                }
                
                // find the maximum action based on q
                
                double maxVal = Double.NEGATIVE_INFINITY;
                StateActionPair temp = new StateActionPair();
                for(StateActionPair sap : list){
                    if(sap.qValue > maxVal){
                        temp = new StateActionPair(sap);
                    }
                }
                
                list.clear();
                
                //find maximum find the max q in the future state.
                
                temp.updateSpeedAndLocation(temp.a);
                double maxVal2 = Double.NEGATIVE_INFINITY;
                for(StateActionPair sa : sap){
                    if(sa.stateAsString.equalsIgnoreCase(temp.stateAsString)){
                        if(sa.qValue > maxVal2){
                            maxVal2 = sa.qValue;
                        }
                    }
                }
                
                //update racer
                
                temp.qValue += eta*(-1 + gamma*(maxVal - maxVal2));
                racer = new StateActionPair(temp);
                i++;
            }while(!r.endLocations.contains(racer.position)); // stopping criteria
        }
    }
    
    /***********************************
     * 
     *    print q values
     * 
     **********************************/
    
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
    
    /***********************************
     * 
     *    cost function
     * 
     **********************************/
    
    public int getCost(State s){
        if(r.endLocations.contains(Integer.toString(s.stateAsArray[0]) + Integer.toString(s.stateAsArray[1]))){
            return 0;
        }
        return -1;
    }
    
    /***********************************
     * 
     *    initialize states
     * 
     **********************************/
    
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

    /***********************************
     * 
     *    initialize state action pairs
     * 
     **********************************/
    
    public void initializeSAPs(){
        for(State state : states){
            for(String key : r.actions.keySet()){
                StateActionPair sap = new StateActionPair(state, r.actions.get(key));
                this.sap.add(sap);
            }
        }
    }
    
}
