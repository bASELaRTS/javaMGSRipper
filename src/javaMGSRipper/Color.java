package javaMGSRipper;

public class Color {
  public int a;
  public int r;
  public int g;
  public int b;
  
  public Color() {
    this.setARGB(255,0,0,0);
  }
  public Color(int a, int r, int g, int b) {
    this.setARGB(a, r, g, b);
  }
  public Color(int c) {
    this.setARGB(c);
  }
  
  public void setARGB(int a, int r, int g, int b){
    this.a = a;
    this.r = r;
    this.g = g;
    this.b = b;
  }
  
  public void setARGB(int c) {
    this.a = (c>>24)&0xff;
    this.r = (c>>16)&0xff;
    this.g = (c>> 8)&0xff;
    this.b = (c    )&0xff;
  }
  
  public void setA1B5G5R5(int c) {    
    int a = ((c>>15)&0x01)&0xff;
    if (a==1) {
      this.a = 0;
    } else {
      this.a = 255;
    }      
    this.b = (((c>>10)&0x1f)<<3)&0xff;
    this.g = (((c>> 5)&0x1f)<<3)&0xff;
    this.r = (((c>> 0)&0x1f)<<3)&0xff;
  }
  
  public int getARGB8888() {
    int c = 0;
    c |= ((this.a&0xff)<<24);
    c |= ((this.r&0xff)<<16);
    c |= ((this.g&0xff)<< 8);
    c |=  (this.b&0xff);
    return c;
  }
  
  public String toString() {
    return "(" + this.a + ";" + this.r + ";" + this.g + ";" + this.b + ")";
  }
  
  public static Color fromARGB(int a, int r, int g, int b) {      
    return new Color(a,r,g,b);
  }
  public static Color fromARGB(int c) {
    return new Color(c);
  }
  public static Color fromA1B5G5R5(int c) {
    Color color = new Color();
    color.setA1B5G5R5(c);
    return color;
  }
}