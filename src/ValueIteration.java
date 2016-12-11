import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;

public class ValueIteration {
    
    HashSet<State> states = new HashSet<State>();
    HashSet<String> statesAsString = new HashSet<String>();
    HashMap<String, State> currentValue = new HashMap<String, State>();
    HashMap<String, State> futureValue = new HashMap<String, State>();
    HashMap<String, Action> policy = new HashMap<String, Action>();
    
    double ep = .000000001;
    double gamma = .2;
    RaceTrack r;
    
    boolean badCrash = false;
    
    public static void main(String[] args) {
        String[] tracks = {"R-track.txt"/*, "O-track.txt", "R-track.txt"*/};
        for(String track : tracks){
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
                vi.valueIteration();
                vi.simulate();
                
            } catch (FileNotFoundException e) {
                System.out.println("Track could not be found");
            }
        }
    }
    
    /***********************************
     * 
     *    Simulator for testing
     * 
     **********************************/
    
    public int simulate(){
        
        int i = 0; // counter
        int[] startLocation = r.randomStart(); // random start location for agent
        String[][] trackCopy = deepCopy(r.trackMakeUp); // copy map for printing
        State s = new State(startLocation[0], startLocation[1],0,0); // set initial state with speed equal to zero
        System.out.print(i + " Starting State: ");
        s.printState(); // print state
        trackCopy[startLocation[1]][startLocation[0]] = Integer.toString(i); // fill map with current step.
        i++; // incremement.
        
        State temp = new State(); // initialize empty state for future use.
        
        while(!r.endLocations.contains(s.position) && i < 10000){ // while we havent crossed the finishline, and the number of steps  < 100 simulate
            String[] arr = r.randomEnd();
            if(Math.abs(s.stateAsArray[1]-Integer.parseInt(arr[1])) == 1 && Math.abs(s.stateAsArray[0]-Integer.parseInt(arr[0])) < 3){
                System.out.println("SUCCESSFULLY NAVIGATED");
                return i;
            }
            // copy most recent valid position
            temp.stateAsArray[0] = s.stateAsArray[0]; 
            temp.stateAsArray[1] = s.stateAsArray[1];
            
            //get action based on current state.
            
            Action a = policy.get(s.stateAsString);
            System.out.print(i + " Action: ");
            a.printAction();
            
            //move agent to new state.
            
            s.move(a);
            System.out.print(" -> ");
            s.printState();
            System.out.println();
            
            //if new state is a wall execute soft crash
            if(r.isWall(s)){
                if(temp != null){
                    int[] newStart = r.randomStart();
                    System.out.print("CRASH! RESTARTING AT: ");
                    if(!badCrash){
                        s.stateAsArray[0] = temp.stateAsArray[0]; // newStart[0] for hard crash
                        s.stateAsArray[1] = temp.stateAsArray[1]; // newStart[1] for hard crash
                    }else{
                        s.stateAsArray[0] = newStart[0];
                        s.stateAsArray[1] = newStart[1];
                    }
                    s.setVelocity(0, 0);
                    s.setPosition();
                    s.reStringifyState();
                    s.printState();
                    continue;
                }
            }else{
                
                trackCopy[s.stateAsArray[1]][s.stateAsArray[0]] = Integer.toString(i); // update value in map
                i++; //increment counter.
            }
        }
        System.out.println();
        printArray(trackCopy, 5); // print track after loop completes.
        System.out.println();
        return i;
    }
    
    /***********************************
     * 
     *    Value Iteration Algo
     * 
     **********************************/
    
    public int valueIteration(){
        int count = 0;
        double max = 0; // max difference
        do{
            count++;
            max = Double.NEGATIVE_INFINITY; // initialize to small number
            
            //copy future values into current values.
            
            for(String s : futureValue.keySet()){
                State temp = new State(futureValue.get(s));
                currentValue.put(temp.stateAsString, temp);
            }
            
            //iterate over all states
            
            for(State s : states){
                
                //initializations.
                
                Action best = new Action(0,0); //best action
                double cost = getCost(s); // set cost based on location of current state (0 if end location, -1 otherwise)
                s.setPosition();
                
                
                //find the best action for this state
                
                double maxValueFromAction = Double.NEGATIVE_INFINITY; //used to get max action
                for(String temp : r.actions.keySet()){
                    
                    Action a = r.actions.get(temp); //pull valid actions
                    
                    double sum = 0;
                    
                    HashMap<String, State> transitions = getTransitionFunctionResults(s, a); // results from various transition possibilites.
                    
                    for(String key : transitions.keySet()){
                        sum += transitions.get(key).value * currentValue.get(key).value; //calculate total potential gain for action
                    }
                    
                    //update best action
                    
                    if(sum > maxValueFromAction){
                        maxValueFromAction = sum;
                        best = a;
                        continue;
                    }
                }
                
                //add the best action to the policy table.
                
                best.reStringify();
                
              //update policy
                
                if(policy.containsKey(s.stateAsString)){
                    policy.replace(s.stateAsString, best); 
                }else{
                    policy.put(s.stateAsString, best);
                }
                
                //recalculate the s value.
                State sCopy = new State(s);
                sCopy.value = (cost + (gamma*maxValueFromAction));
                
                futureValue.replace(sCopy.stateAsString, sCopy);
                
                //calculate delta between t and t-1
                double delta = Math.abs(futureValue.get(s.stateAsString).value - currentValue.get(s.stateAsString).value);
                if(delta > max){
                    max = delta;
                }
                
            }
        }while(max > ep); // stopping condition
        return count;
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
     *    Print policy set.
     * 
     **********************************/
    
    public void printPolicy(){
        for(String s : policy.keySet()){
            System.out.print(s);
            policy.get(s).printAction();
        }
    }
    
    /***********************************
     * 
     *    Cost function
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
     *    Initialize all possible states
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
     *    deep copy used for copying
     *    map
     * 
     **********************************/
    
    public String[][] deepCopy(String[][] r){
        String[][] copy = new String[r.length][r[0].length];
        for(int i = 0; i < r.length; i++){
            for(int j = 0; j < r[i].length; j++){
                copy[i][j] = r[i][j];
            }
        }
        return copy;
    }
    
    /***********************************
     * 
     *    Print map after simulation
     * 
     **********************************/
    
    public void printArray(String[][] s, int longest){
        for(int i = 0; i < s.length; i++){
            for(int j = 0; j < s[i].length; j++){
                int len = s[i][j].length();
                String buffer = "";
                for(int k = 0; k < longest-len; k++){ 
                    buffer += " ";
                }
                if(s[i][j].equalsIgnoreCase("R")){
                    System.out.print("-"+buffer);
                }else if(s[i][j].equalsIgnoreCase("W")){
                    System.out.print("W"+buffer);
                }else if(s[i][j].equalsIgnoreCase("S")){
                    System.out.print("S"+buffer);
                }else{
                    System.out.print(s[i][j] + buffer);
                }
            }
            System.out.println();
        }
    }
    
}
