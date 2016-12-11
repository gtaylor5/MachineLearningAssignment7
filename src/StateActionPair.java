
public class StateActionPair extends State{

    Action a;
    String asString = "";
    double qValue = 0;
    
    
    public StateActionPair(int r, int c, int vr, int vc, Action a){
        super(r, c, vr, vc);
        this.a = a;
        stringify();
    }
    
    public StateActionPair(State s, Action a){
        super(s);
        this.a = a;
        stringify();
    }
    
    public void printSAP(){
        this.printState();
        this.a.printAction();
        System.out.println();
    }
    
    public void stringify(){
        this.asString = this.stateAsString;
        this.asString += this.a.actionAsString;
    }
    
}
