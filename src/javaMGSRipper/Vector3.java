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
  
  public void setCoordinates(double dx, double dy, double dz) {
    this.x = dx;
    this.y = dy;
    this.z = dz;
  }
  
  public void setVector(Vector3 v) {
    this.setCoordinates(v.x, v.y, v.z);
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
}
