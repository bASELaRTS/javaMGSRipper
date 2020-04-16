package javaMGSRipper;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class OBJ {

  private Vector<Vector3> m_vertices;
  private Vector<Vector3> m_normals;
  private Vector<Vector2> m_uvs;
  private Vector<Face> m_faces;
  
  public OBJ() {
    this.m_vertices = new Vector<Vector3>();
    this.m_normals = new Vector<Vector3>();
    this.m_uvs = new Vector<Vector2>();
    this.m_faces = new Vector<Face>();
  }
  
  public void load(String filename) {
    java.io.File file = new File(filename);
    java.io.FileReader reader;
    try {
      reader = new FileReader(file);
      this.load(new java.io.BufferedReader (reader));
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void load(java.io.BufferedReader stream) {
    String line;
    String strs[];
    String strs2[];
    Face face;
    FaceElement element;
    int index;
    int i;
    double x,y,z;
    
    try {
      line = stream.readLine();
      while (line!=null) {
        line = line.trim();
        index = line.indexOf('#');
        if (index>0) {
          line = line.substring(index);
        } else if (index==0) {
          line = "";
        }
        
        if (line.length()>0) {
          strs = line.split(" ");
          if (strs.length>1) {
            if (strs[0].equals("v")) {
              x = Double.parseDouble(strs[1]);
              y = Double.parseDouble(strs[2]);
              z = Double.parseDouble(strs[3]);
              this.getVertices().add(new Vector3(x,y,z));
            } else if (strs[0].equals("vn")) {
              x = Double.parseDouble(strs[1]);
              y = Double.parseDouble(strs[2]);
              z = Double.parseDouble(strs[3]);
              this.getNormals().add(new Vector3(x,y,z));
            } else if (strs[0].equals("vt")) {
              x = Double.parseDouble(strs[1]);
              y = Double.parseDouble(strs[2]);
              this.getUVs().add(new Vector2(x,y));              
            } else if (strs[0].equals("f")) {
              face = new Face();
              for(i=1;i<strs.length;i++) {
                element = new FaceElement();                
                strs2 = strs[i].split("/");
                if (strs2.length==1) {
                  element.setVertexIndex(Integer.parseInt(strs2[0]));
                } else if (strs2.length==2) {
                  element.setVertexIndex(Integer.parseInt(strs2[0]));
                  element.setUVIndex(Integer.parseInt(strs2[1]));
                } else if (strs2.length==3) {
                  element.setVertexIndex(Integer.parseInt(strs2[0]));
                  element.setUVIndex(Integer.parseInt(strs2[1]));
                  element.setNormalIndex(Integer.parseInt(strs2[2]));                  
                }                
                face.getElements().add(element);
              }
              this.getFaces().add(face);
            }
          }
        }
        
        line = stream.readLine();        
      }
      
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
    
  public static void main(String[] args) {
    String filename = "";
    boolean showVertices = false;
    boolean showNormals = false;
    boolean showUVs = false;
    boolean showFaces = false;
    int i;
    OBJ obj;
    Vector3 v3;
    Vector2 v2;
    Face face;
    
    for(i=0;i<args.length;i++) {
      if (args[i].equals("-f")) {
        filename = args[++i];
      } else if (args[i].equals("-v")) {
        showVertices = true;
      } else if (args[i].equals("-vn")) {
        showNormals = true;
      } else if (args[i].equals("-vt")) {
        showUVs = true;
      } else if (args[i].equals("-faces")) {
        showFaces = true;
      } 
    }
    
    if (filename.length()>0) {
      obj = new OBJ();
      obj.load(filename);
      
      System.out.println("[Header]");
      System.out.println("Vertice count             : " + Integer.toString(obj.getVertices().size()));
      System.out.println("Normals count             : " + Integer.toString(obj.getNormals().size()));
      System.out.println("UV count                  : " + Integer.toString(obj.getUVs().size()));
      System.out.println("Faces                     : " + Integer.toString(obj.getFaces().size()));
      
      if (showVertices) {
        System.out.println("[Vertices]");
        for(i=0;i<obj.getVertices().size();i++) {
          v3 = obj.getVertices().elementAt(i);
          System.out.println(Integer.toString(i) + " : " + v3.toString());
        }
      }

      if (showNormals) {
        System.out.println("[Normals]");
        for(i=0;i<obj.getNormals().size();i++) {
          v3 = obj.getNormals().elementAt(i);
          System.out.println(Integer.toString(i) + " : " + v3.toString());
        }
      }

      if (showUVs) {
        System.out.println("[UVs]");
        for(i=0;i<obj.getUVs().size();i++) {
          v2 = obj.getUVs().elementAt(i);
          System.out.println(Integer.toString(i) + " : " + v2.toString());
        }
      }

      if (showFaces) {
        System.out.println("[Faces]");
        for(i=0;i<obj.getFaces().size();i++) {
          face = obj.getFaces().elementAt(i);
          System.out.println(Integer.toString(i) + " : " + face.toString());
        }
      }
    } else {
      System.out.println("OBJ viewer");
      System.out.println("bASELaRTS 2020");
      System.out.println();
      System.out.println("-f <filename.obj> : opens filename.obj");
      System.out.println("-v                : shows vertices");
      System.out.println("-vn               : shows normals");
      System.out.println("-vt               : shows uv coordinates");
      System.out.println("-faces            : shows uv coordinates");
      System.out.println();
    }
  }
  
  public Vector<Vector3> getVertices(){return this.m_vertices;}
  public Vector<Vector3> getNormals(){return this.m_normals;}
  public Vector<Vector2> getUVs(){return this.m_uvs;}
  public Vector<Face> getFaces(){return this.m_faces;}
  
  public class Face {
    private Vector<FaceElement> m_elements;
    
    public Face() {
      this.m_elements = new Vector<FaceElement>();
    }
    
    public String toString() {
      String output = "";
      int i;
      FaceElement element;

      for(i=0;i<this.getElements().size();i++) {
        element = this.getElements().elementAt(i);
        output+=Integer.toString(element.getVertexIndex());
        if (element.getUVIndex()>=0) {
          output += "/" + Integer.toString(element.getUVIndex());
        }
        if (element.getNormalIndex()>=0) {
          if (element.getUVIndex()<0) {
            output += "/";
          }
          output += "/" + Integer.toString(element.getNormalIndex());
        }
        output +=" ";
      }
      
      return output;
    }
    
    public Vector<FaceElement> getElements(){return this.m_elements;}
  }
  
  public class FaceElement {
    private int m_vertexIndex;
    private int m_normalIndex;
    private int m_uvIndex;
    
    public FaceElement() {
      this.setVertexIndex(-1);
      this.setNormalIndex(-1);
      this.setUVIndex(-1);
    }
    
    public void setVertexIndex(int i) {this.m_vertexIndex=i;}
    public int getVertexIndex() {return this.m_vertexIndex;}
    
    public void setNormalIndex(int i) {this.m_normalIndex=i;}
    public int getNormalIndex() {return this.m_normalIndex;}
    
    public void setUVIndex(int i) {this.m_uvIndex=i;}
    public int getUVIndex() {return this.m_uvIndex;}
  }

}

