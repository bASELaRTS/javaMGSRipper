package javaMGSRipper;

public class Vector2 {
  public double x;
  public double y;
  
  public Vector2() {
    this.setCoordinates(0, 0);
  }
  
  public void setCoordinates(double dx, double dy) {
    this.x = dx;
    this.y = dy;
  }
  
  public void setVector(Vector2 v) {
    this.setCoordinates(v.x, v.y);
  }
  
  public String toString() {
    return "(" 
        + Double.toString(this.x) 
        + ";" 
        + Double.toString(this.y)
        ;
  }  
}
