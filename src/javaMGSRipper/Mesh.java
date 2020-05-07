package javaMGSRipper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class Mesh {
  private Vector<Vector3> m_vertices;
  private Vector<Vector3> m_normals;
  private Vector<Vector2> m_uvs;
  private Vector<MeshFace> m_faces;
  
  public Mesh() {
    this.m_vertices = new Vector<Vector3>();
    this.m_normals = new Vector<Vector3>();
    this.m_uvs = new Vector<Vector2>();
    this.m_faces = new Vector<MeshFace>();
  }
  
  public void saveOBJ(String filename) {
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
      
      line = "mtllib material.mtl";
      bw.write(line);
      bw.newLine();         
      
      
      line = "# vertices (" + this.getVertices().size() + ")";
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
      
      line = "# normals (" + this.getNormals().size() + ")";
      bw.write(line);
      bw.newLine();         
      for(i=0;i<this.getNormals().size();i++) {
        v3 = this.getNormals().elementAt(i);
        
        line = "vn ";
        line += Double.toString(v3.x);
        line += " ";
        line += Double.toString(v3.y);
        line += " ";
        line += Double.toString(v3.z);
        
        bw.write(line);
        bw.newLine();          
      }

      line = "# uvs (" + this.getUVs().size() + ")";
      bw.write(line);
      bw.newLine();         
      for(i=0;i<this.getUVs().size();i++) {
        v2 = this.getUVs().elementAt(i);
        
        line = "vt ";
        line += Double.toString(v2.x);
        line += " ";
        line += Double.toString(v2.y);
        
        bw.write(line);
        bw.newLine();                  
      }
      
      line = "usemtl texture";
      bw.write(line);
      bw.newLine();         
      line = "# faces (" + this.getFaces().size() + ")";
      bw.write(line);
      bw.newLine();         
      for(i=0;i<this.getFaces().size();i++) {
        face = this.getFaces().elementAt(i);
        
        line = "f ";
        for(j=0;j<face.getPoints().size();j++) {          
          line += (face.getPoints().elementAt(j)+1);
          if (face.getUVs().size()==face.getPoints().size()) {
            line += "/" + (face.getUVs().elementAt(j)+1);
          }
          
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
  
  public void loadOBJ(String filename) {
    java.io.FileReader freader;
    java.io.BufferedReader reader;
    String line;
    String command;
    String str;
    String strs[];
    String param;
    int index;
    double ds[] = new double[3];
    int i;
    try {
      freader = new FileReader(new java.io.File(filename));
      reader = new BufferedReader(freader);
      
      line = reader.readLine();
      while(line!=null) {
        line = line.trim();
        
        // remove comments
        index = line.indexOf("#");
        if (index>0) {
          line = line.substring(0,index).trim();
        } else if (index==0) {
          line = "";
        }
        
        // parse line
        if (line.length()>0) {
          command = "";
          param = "";
          str = "";
          for(i=0;i<ds.length;i++) ds[i]=0.0;
          
          index = line.indexOf(" ");
          if (index>0) {
            command = line.substring(0,index).trim();
            param = line.substring(index).trim();
          }
          
          if (command.equals("v")) {
            i = 0;
            while(i<3) {
              str = "";
              index = param.indexOf(" ");
              if (index>0) {
                str = param.substring(0, index).trim();
                param = param.substring(index).trim();
              } else {
                str = param;
                param = "";
              }
              
              if (str.length()>0) {
                ds[i]=Double.parseDouble(str);
              }
              
              i++;
            }
            
            if (i==3) {
              this.getVertices().add(new Vector3(ds[0],ds[1],ds[2]));              
            }            
          } else if (command.equals("vt")) {
            i = 0;
            while(i<2) {
              str = "";
              index = param.indexOf(" ");
              if (index>0) {
                str = param.substring(0, index).trim();
                param = param.substring(index).trim();
              } else {
                str = param;
                param = "";
              }
              
              if (str.length()>0) {
                ds[i]=Double.parseDouble(str);
              }
              
              i++;
            }
            
            if (i==2) {
              this.getUVs().add(new Vector2(ds[0],ds[1]));              
            }            
          } else if (command.equals("vn")) {
            i = 0;
            while(i<3) {
              str = "";
              index = param.indexOf(" ");
              if (index>0) {
                str = param.substring(0, index).trim();
                param = param.substring(index).trim();
              } else {
                str = param;
                param = "";
              }
              
              if (str.length()>0) {
                ds[i]=Double.parseDouble(str);
              }
              
              i++;
            }
            
            if (i==3) {
              this.getNormals().add(new Vector3(ds[0],ds[1],ds[2]));              
            }            
          } else if (command.equals("f")) {
            MeshFace face = new MeshFace();
            while(param.length()>0) {
              str = "";
              index = param.indexOf(" ");
              if (index>0) {
                str = param.substring(0, index).trim();
                param = param.substring(index).trim();
              } else {
                str = param;
                param = "";
              }
              
              if (str.length()>0) {
                strs = str.split("/");
                if (strs.length==1) {                  
                  face.getPoints().add(Integer.parseInt(strs[0].trim())-1);
                } else if (strs.length==2) {
                  face.getPoints().add(Integer.parseInt(strs[0].trim())-1);
                  face.getUVs().add(Integer.parseInt(strs[1].trim())-1);
                } else if (strs.length==3) {
                  face.getPoints().add(Integer.parseInt(strs[0].trim())-1);
                  face.getUVs().add(Integer.parseInt(strs[1].trim())-1);
                  face.getNormals().add(Integer.parseInt(strs[2].trim())-1);
                }
              }
            }            
            this.getFaces().add(face);
          }
        }
        
        line = reader.readLine();
      }
      
      reader.close();
      freader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public Vector<Vector3> getVertices(){return this.m_vertices;}
  public Vector<Vector3> getNormals(){return this.m_normals;}
  public Vector<Vector2> getUVs(){return this.m_uvs;}
  public Vector<MeshFace> getFaces(){return this.m_faces;}
  
  
  public static void main(String[] args) {
    int i;
    Mesh mesh;
    String filename = "data/Object_00.OBJ";
    mesh = new Mesh();
    mesh.loadOBJ(filename);
    
    System.out.println("[Vertices ("+ mesh.getVertices().size() +")]");
    for(i=0;i<mesh.getVertices().size();i++) {
      System.out.println(i + " :" + mesh.getVertices().elementAt(i).toString());
    }
    System.out.println("[Normals ("+ mesh.getNormals().size() +")]");
    for(i=0;i<mesh.getNormals().size();i++) {
      System.out.println(i + " :" + mesh.getNormals().elementAt(i).toString());
    }
    System.out.println("[UVs ("+ mesh.getUVs().size() +")]");
    for(i=0;i<mesh.getUVs().size();i++) {
      System.out.println(i + " :" + mesh.getUVs().elementAt(i).toString());
    }
    System.out.println("[Faces ("+ mesh.getFaces().size() +")]");
    for(i=0;i<mesh.getFaces().size();i++) {
      System.out.println(i + " :" + mesh.getFaces().elementAt(i).toString());
    }
  }
}
