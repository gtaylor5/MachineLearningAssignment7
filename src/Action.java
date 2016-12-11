

public class Action{

    String actionAsString = ""; // used as the key in a hashmap.
    int[] actionAsArray = new int[2];//actual integer values of the action
    
    /***********************************
     * 
     *    Constructor
     *    
     **********************************/
    
    public Action(int horizAccel, int verticalAccel){
        actionAsArray[0] = horizAccel;
        actionAsArray[1] = verticalAccel;
        String temp = "";
        for(int val : actionAsArray){
            temp += Integer.toString(val);
        }
        actionAsString = temp;
    }
    
    /***********************************
     * 
     *    re-encodes action 
     * 
     **********************************/
    
    void reStringify(){
        actionAsString = "";
        for(int i = 0; i < actionAsArray.length; i++){
            actionAsString += Integer.toString(actionAsArray[i]);
        }
    }
    
    /***********************************
     * 
     *    print action
     * 
     **********************************/
    
    public void printAction(){
        System.out.print("(");
        System.out.print(actionAsArray[0]+",");
        System.out.print(actionAsArray[1]+")");
    }
    
}