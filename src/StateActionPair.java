
/***********************************
     * 
     *    Used in Q Learning and Sarsa
     * 
     **********************************/

public class StateActionPair extends State{

    Action a;
    String asString = "";
    double qValue = 0;
    
    /***********************************
     * 
     *    Constructor
     * 
     **********************************/
    
    public StateActionPair(int r, int c, int vr, int vc, Action a){
        super(r, c, vr, vc);
        this.a = a;
        stringify();
    }
    
    /***********************************
     * 
     *    constructor
     * 
     **********************************/
    
    public StateActionPair(State s, Action a){
        super(s);
        this.a = a;
        stringify();
    }
    
    /***********************************
     * 
     *   default constructor
     * 
     **********************************/
    
    public StateActionPair(){
        
    }
    
    /***********************************
     * 
     *    constructor
     * 
     **********************************/
    
    public StateActionPair(StateActionPair sap){
        super(sap);
        this.a = sap.a; 
    }
    
    /***********************************
     * 
     *    print sap
     * 
     **********************************/
    
    public void printSAP(){
        this.printState();
        this.a.printAction();
        System.out.println();
    }
    
    /***********************************
     * 
     *    writer the string
     * 
     **********************************/
    
    public void stringify(){
        this.asString = this.stateAsString;
        this.asString += this.a.actionAsString;
    }
    
}
