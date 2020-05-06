package javaMGSRipper;

import java.util.Vector;

public class MeshFace {
  private Vector<Integer> m_points;
  private Vector<Integer> m_normals;
  private Vector<Integer> m_uvs;
  
  public MeshFace() {
    this.m_points = new Vector<Integer>();
    this.m_normals = new Vector<Integer>();
    this.m_uvs = new Vector<Integer>();
  }
  
  public String toString() {
    int i;
    String str;
    str = "";
    for(i=0;i<this.getPoints().size();i++) {
      str+= (this.getPoints().elementAt(i)+1); 
      if (this.getUVs().size()==this.getUVs().size()) {
        str+="/" + (this.getUVs().elementAt(i)+1);
      }
      if (this.getNormals().size()==this.getPoints().size()) {
        str+="/" + (this.getNormals().elementAt(i)+1);
      }
      str+=" ";
    }
    return str;
  }
  
  public Vector<Integer> getPoints(){return this.m_points;};
  public Vector<Integer> getNormals(){return this.m_normals;}
  public Vector<Integer> getUVs(){return this.m_uvs;}
}
