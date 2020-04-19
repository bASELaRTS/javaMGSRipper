package javaMGSRipper;

public class Vector2 {
  public double x;
  public double y;
  
  public Vector2() {
    this.setCoordinates(0, 0);
  }
  
  public Vector2(Vector2 v) {
    this.setCoordinates(v.x, v.y);
  }
  
  public Vector2(double dx, double dy) {
    this.setCoordinates(dx, dy);
  }
  
  public void setCoordinates(double dx, double dy) {
    this.x = dx;
    this.y = dy;
  }
  
  public void setVector(Vector2 v) {
    this.setCoordinates(v.x, v.y);
  }
  
  public double length() {return Vector2.length(this);}
  
  public void normalize() {
    Vector2 v = new Vector2();
    Vector2.normalize(this, v);
    this.setVector(v);
  }
  
  public String toString() {
    return "(" 
        + Double.toString(this.x) 
        + ";" 
        + Double.toString(this.y)
        + ")"
        ;
  }  
  
  public static double length(Vector2 v) {
    double d = Math.sqrt(v.x*v.x + v.y*v.y);
    return d;
  }
  
  public static void normalize(Vector2 v1, Vector2 v2) {
    double d = Vector2.length(v1);
    v2.x = v1.x/d;
    v2.y = v1.y/d;
  }
  
  public static void add(Vector2 v1, Vector2 v2, Vector2 v3) {
    v3.x = v1.x + v2.x;
    v3.y = v1.y + v2.y;
  }
  public static void subtract(Vector2 v1, Vector2 v2, Vector2 v3) {
    v3.x = v1.x - v2.x;
    v3.y = v1.y - v2.y;
  }
  public static void multiply(Vector2 v1, Vector2 v2, Vector2 v3) {
    v3.x = v1.x * v2.x;
    v3.y = v1.y * v2.y;
  }
  public static void scale(Vector2 v1, double scalar, Vector2 v3) {
    v3.x = v1.x * scalar;
    v3.y = v1.y * scalar;
  }
}
