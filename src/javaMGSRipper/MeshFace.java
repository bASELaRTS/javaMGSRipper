package javaMGSRipper;

import java.util.Vector;

public class MeshFace {
  private Vector<Integer> m_points;
  private Vector<Integer> m_uvs;
  
  public MeshFace() {
    this.m_points = new Vector<Integer>();
    this.m_uvs = new Vector<Integer>();
  }
  
  public Vector<Integer> getPoints(){return this.m_points;};
  public Vector<Integer> getUVs(){return this.m_uvs;}
}