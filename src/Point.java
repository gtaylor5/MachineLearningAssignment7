
public class Point {
    
    int x;
    int y; 
    
    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    public Point(){
        
    }
    
    public void printPoint(){
        System.out.printf("<%d, %d>", x, y);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        
        Point temp = (Point) obj;
        if(!(x == temp.x)){
            return false;
        }
        if (!(y == temp.y)){
            return false;
        }
        return true;
    }
    
}
