
public class State {
    
    String stateAsString = ""; // string encoding
    int[] stateAsArray = new int[4]; // x, y, xdot, ydot
    double value = 0; // expected reward
    double probability = 0; 
    String position = ""; // x y position
    
    /***********************************
     * 
     *    Constructor
     * 
     **********************************/
    
    public State(int horizLocation, int verticalLocation, int xSpeed, int ySpeed){
        stateAsArray[0] = horizLocation;
        stateAsArray[1] = verticalLocation;
        stateAsArray[2] = xSpeed;
        stateAsArray[3] = ySpeed;
        String temp = "";
        for(int i = 0; i < stateAsArray.length; i++){
            if(i == stateAsArray.length -1){
                temp += stateAsArray[i];
                break;
            }
            temp += Integer.toString(stateAsArray[i]) + ",";
        }
        stateAsString = temp;
    }
    
    /***********************************
     * 
     *    used for copying the state
     * 
     **********************************/
    
    public State(State s){
        this.stateAsArray = s.stateAsArray;
        this.stateAsString = s.stateAsString;
        this.value = s.value;
        this.probability = s.probability;
        this.position = s.position;
    }
    
    /***********************************
     * 
     *    empty constructor
     * 
     **********************************/
    
    public State(){
        
    }
    
    /***********************************
     * 
     *    print the state in nice 
     *    format
     * 
     **********************************/
    
    void printState(){
        Main.writer.print("(");
        for(int i = 0; i < stateAsArray.length; i++){
            if(i == stateAsArray.length-1){
                Main.writer.print(stateAsArray[i]+") ");
                return;
            }
            Main.writer.print(stateAsArray[i] + ",");
        }
    }
    
    /***********************************
     * 
     *    re-encode string
     * 
     **********************************/
    
    void reStringifyState(){
        this.stateAsString = "";
        for(int i = 0; i < stateAsArray.length; i++){
            if(i == stateAsArray.length -1){
                this.stateAsString += stateAsArray[i];
                break;
            }
            this.stateAsString += Integer.toString(stateAsArray[i]) + ",";
        }
    }
    
    /***********************************
     * 
     *    sets position variable above
     * 
     **********************************/
    
    void setPosition(){
        position = Integer.toString(stateAsArray[0]) +"," +Integer.toString(stateAsArray[1]);
    }
    
    /***********************************
     * 
     *    sets position given a string
     * 
     **********************************/
    
    void setPosition(String s){
        String[] temp = s.split(",");
        stateAsArray[0] = Integer.parseInt(temp[0]);
        stateAsArray[1] = Integer.parseInt(temp[1]);
    }
    
    /***********************************
     * 
     *    set velocity
     * 
     **********************************/
    
    void setVelocity(int vx, int vy){
        stateAsArray[2] = vx;
        stateAsArray[3] = vy;
    }
    
    /***********************************
     * 
     *   if position is null
     *   set position
     *   otherwise return position
     * 
     **********************************/
    
    String getPosition(){
        if(position.isEmpty()){
            setPosition();
            return position;
        }
        return position;
    }
    
    
    /***********************************
     * 
     *    Move agent based on action
     * 
     **********************************/
    
    
    public void move(Action a){
        stateAsArray[2] += a.actionAsArray[0];
        stateAsArray[3] += a.actionAsArray[1];
        
        if(stateAsArray[2] < -5){
            stateAsArray[2] = -5;
        }
        if(stateAsArray[2] > 5){
            stateAsArray[2] = 5;
        }
        
        if(stateAsArray[3] < -5){
            stateAsArray[3] = -5;
        }
        if(stateAsArray[3] > 5){
            stateAsArray[3] = 5;
        }
        stateAsArray[0] += stateAsArray[2];
        stateAsArray[1] += stateAsArray[3];
        reStringifyState();
        setPosition();
    }
    
    /***********************************
     * 
     *    return a future state based
     *    on an action.
     * 
     **********************************/
    
    public State updateSpeedAndLocation(Action a){
        State s = new State(stateAsArray[0], stateAsArray[1], stateAsArray[2], stateAsArray[3]);
        s.value = value;
        s.probability = probability;
        s.stateAsArray[2] += a.actionAsArray[0];
        s.stateAsArray[3] += a.actionAsArray[1];
        
        if(s.stateAsArray[2] < -5){
            s.stateAsArray[2] = -5;
        }
        if(s.stateAsArray[2] > 5){
            s.stateAsArray[2] = 5;
        }
        
        if(s.stateAsArray[3] < -5){
            s.stateAsArray[3] = -5;
        }
        if(s.stateAsArray[3] > 5){
            s.stateAsArray[3] = 5;
        }
        s.stateAsArray[0] += s.stateAsArray[2];
        s.stateAsArray[1] += s.stateAsArray[3];
        s.reStringifyState();
        s.setPosition();
        return s;
    }
    
    
    
}
