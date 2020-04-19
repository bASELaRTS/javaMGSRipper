package javaMGSRipper;

public class Vector3 {
  public double x;
  public double y;
  public double z;
  
  public Vector3() {
    this.setCoordinates(0, 0, 0);
  }
  
  public Vector3(double dx, double dy, double dz) {
    this.setCoordinates(dx, dy, dz);
  }
  
  public Vector3(Vector3 v) {
    this.setCoordinates(v.x, v.y, v.z);
  }
  
  public void setCoordinates(double dx, double dy, double dz) {
    this.x = dx;
    this.y = dy;
    this.z = dz;
  }
  
  public void setVector(Vector3 v) {
    this.setCoordinates(v.x, v.y, v.z);
  }
  
  public double length() {return Vector3.length(this);}
  public void normalize() {
    Vector3 v = new Vector3();
    Vector3.normalize(this, v);   
    this.setVector(v);
  }
  
  public String toString() {
    return "(" 
        + Double.toString(this.x) 
        + ";" 
        + Double.toString(this.y) 
        + ";"
        + Double.toString(this.z) 
        + ")"
        ;
  }
  
  public static void normalize(Vector3 v1, Vector3 v2) {
    double d = Vector3.length(v1);
    v2.x = v1.x/d;
    v2.y = v1.y/d;
    v2.z = v1.z/d;
  }
  
  public static double length(Vector3 v) {
    double d = Math.sqrt(v.x*v.x + v.y*v.y + v.z* v.z);
    return d;
  }
  
  public static void add(Vector3 v1, Vector3 v2, Vector3 v3) {
    v3.x = v1.x + v2.x;
    v3.y = v1.y + v2.y;
    v3.z = v1.z + v2.z;
  }

  public static void subtract(Vector3 v1, Vector3 v2, Vector3 v3) {
    v3.x = v1.x - v2.x;
    v3.y = v1.y - v2.y;
    v3.z = v1.z - v2.z;
  }
  
  public static void multiply(Vector3 v1, Vector3 v2, Vector3 v3) {
    v3.x = v1.x * v2.x;
    v3.y = v1.y * v2.y;
    v3.z = v1.z * v2.z;
  }

  public static void scale(Vector3 v1, double scalar, Vector3 v3) {
    v3.x = v1.x * scalar;
    v3.y = v1.y * scalar;
    v3.z = v1.z * scalar;
  }
  
}
