package javaMGSRipper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class Mesh {
  private Vector<Vector3> m_vertices;
  private Vector<Vector2> m_uvs;
  private Vector<MeshFace> m_faces;
  
  public Mesh() {
    this.m_vertices = new Vector<Vector3>();
    this.m_uvs = new Vector<Vector2>();
    this.m_faces = new Vector<MeshFace>();
  }
  
  public void exportObj(String filename) {
    java.io.FileWriter fw;
    java.io.BufferedWriter bw;
    String line;
    int i,j;
    Vector2 v2;
    Vector3 v3;
    MeshFace face;
    
    try {
      fw = new FileWriter(filename);
      bw = new BufferedWriter(fw);
      
      line = "# vertices";
      bw.write(line);
      bw.newLine();         

      for(i=0;i<this.getVertices().size();i++) {
        v3 = this.getVertices().elementAt(i);
        
        line = "v ";
        line += Double.toString(v3.x);
        line += " ";
        line += Double.toString(v3.y);
        line += " ";
        line += Double.toString(v3.z);
        
        bw.write(line);
        bw.newLine();          
      }
      
      for(i=0;i<this.getUVs().size();i++) {
        v2 = this.getUVs().elementAt(i);
        
        line = "vt ";
        line += Double.toString(v2.x);
        line += " ";
        line += Double.toString(v2.y);
        
        bw.write(line);
        bw.newLine();                  
      }
      
      for(i=0;i<this.getFaces().size();i++) {
        face = this.getFaces().elementAt(i);
        
        line = "f ";
        for(j=0;j<face.getPoints().size();j++) {          
          line += face.getPoints().elementAt(j)+1;
          line += " ";
        }
        
        bw.write(line);
        bw.newLine();
      }
      
      bw.close();
      fw.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public Vector<Vector3> getVertices(){return this.m_vertices;}
  public Vector<Vector2> getUVs(){return this.m_uvs;}
  public Vector<MeshFace> getFaces(){return this.m_faces;}
}
