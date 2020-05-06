package javaMGSRipper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class EMD2 {
  private long m_directoryOffset;
  private long m_directoryCount;
  private Vector<Directory> m_directories;
  private Model m_model;
  private ModelSkeleton m_skeleton;
  
  public EMD2() {
    this.m_directories = new Vector<Directory>();
    this.m_skeleton = new ModelSkeleton();
  }
  
  public void load(String filename) {
    try {
      this.load(new java.io.RandomAccessFile(filename, "r"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  public void load(java.io.RandomAccessFile stream) {
    int i;
    double x,y,z;
    byte b2[] = new byte[2];
    byte b4[] = new byte[4];
    Directory directory;
    
    try {
      // header
      stream.read(b4);this.m_directoryOffset=Helper.bytesToLong(b4);
      stream.read(b4);this.m_directoryCount=Helper.bytesToLong(b4);
      
      // read directories
      stream.seek(this.m_directoryOffset);
      for(i=0;i<this.m_directoryCount;i++) {
        directory = new Directory();
        stream.read(b4);directory.offset=Helper.bytesToLong(b4);
        this.getDirectories().add(directory);
      }
      
      // read skeleton directory index 2
      directory = this.getDirectories().elementAt(2);
      stream.seek(directory.offset);
      stream.read(b2);this.m_skeleton.offset=Helper.bytesToUInt(b2);
      stream.read(b2);this.m_skeleton.length=Helper.bytesToUInt(b2);
      stream.read(b2);this.m_skeleton.count=Helper.bytesToUInt(b2);
      stream.read(b2);this.m_skeleton.size=Helper.bytesToUInt(b2);
      if (this.m_skeleton.offset>8) {
        for(i=0;i<this.m_skeleton.count;i++) {
          stream.read(b2);x=Helper.bytesToInt(b2);
          stream.read(b2);y=Helper.bytesToInt(b2);
          stream.read(b2);z=Helper.bytesToInt(b2);
          this.m_skeleton.getPositions().add(new Vector3(x,y,z));
        }
      }
      
      // read mesh directory index 7
      if (this.getDirectories().size()==8) {
        directory = this.getDirectories().elementAt(7);
        stream.seek(directory.offset);

        this.m_model = new Model();
        this.m_model.read(stream);
      }
      
      directory = this.getDirectories().elementAt(7);
      for(i=0;i<this.m_model.getObjects().size();i++) {
        ModelObject object = this.m_model.getObjects().elementAt(i);
        object.getTriangles().readDataTriangle(stream, directory.offset);
        object.getQuad().readDataQuad(stream, directory.offset);
      }
      
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public Vector<Directory> getDirectories(){return this.m_directories;}
  public Model getModel() {return this.m_model;}
  public ModelSkeleton getSkeleton() {return this.m_skeleton;} 
  

  public class Directory {
    public long offset;
  }

  public class Model {
    public long length;
    public long unknown;
    public long count;
    
    private Vector<ModelObject> m_objects;
    
    public Model() {
      this.m_objects = new Vector<ModelObject>();
    }
    
    public void read(java.io.RandomAccessFile stream) {
      int i;
      byte b4[] = new byte[4];
      ModelObject object;
      
      try {
        stream.read(b4);this.length=Helper.bytesToLong(b4);
        stream.read(b4);this.unknown=Helper.bytesToLong(b4);
        stream.read(b4);this.count=Helper.bytesToLong(b4);
        
        // read objects sequentially
        for(i=0;i<(this.count/2);i++) {
          object = new ModelObject();
          object.read(stream);
          this.getObjects().add(object);
        }        
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    public Vector<ModelObject> getObjects(){return this.m_objects;}
  }
  
  public class ModelObject {
    private ModelGeometry m_triangles;
    private ModelGeometry m_quads;
    
    public ModelObject() {
      this.m_triangles = new ModelGeometry();
      this.m_quads = new ModelGeometry();
    }
    
    public void read(java.io.RandomAccessFile stream) {
      this.getTriangles().read(stream);
      this.getQuad().read(stream);
    }
    
    public Mesh getMesh() {
      int i;
      int triCount;
      double scale = 50.0;
      Vector4 v4;
      Mesh mesh;
      mesh = new Mesh();
      
      
      if (this.getTriangles().faceCount>0) {
        // triangles
        for(i=0;i<this.getTriangles().getVertices().size();i++) {
          v4 = this.getTriangles().getVertices().elementAt(i);
          mesh.getVertices().add(new Vector3((-v4.z)/scale,(-v4.y)/scale,(-v4.x)/scale));          
        }
        
        // faces
        for(i=0;i<this.getTriangles().getFaces().size();i++) {
          ModelFace modelFace = this.getTriangles().getFaces().elementAt(i);
          MeshFace meshFace = new MeshFace();
          meshFace.getPoints().add(modelFace.v2);
          meshFace.getPoints().add(modelFace.v1);
          meshFace.getPoints().add(modelFace.v0);
          mesh.getFaces().add(meshFace);
        }   
      }
      
      if (this.getQuad().faceCount>0) {
        triCount=0;

        // if no triangles vertices
        if (mesh.getVertices().size()==0) {
          for(i=0;i<this.getQuad().getVertices().size();i++) {
            v4 = this.getQuad().getVertices().elementAt(i);
            mesh.getVertices().add(new Vector3((-v4.z)/scale,(-v4.y)/scale,(-v4.x)/scale));          
          }            
        }        
 
        // triangle vertices != quad vertices
        if ((this.getTriangles().vertexCount!=this.getQuad().vertexCount)||(this.getTriangles().vertexOffset!=this.getQuad().vertexOffset)) {
          triCount = this.getTriangles().getVertices().size();
          for(i=0;i<this.getQuad().getVertices().size();i++) {
            v4 = this.getQuad().getVertices().elementAt(i);
            mesh.getVertices().add(new Vector3((-v4.z)/scale,(-v4.y)/scale,(-v4.x)/scale));          
          }            
        }
        
        // faces
        for(i=0;i<this.getQuad().getFaces().size();i++) {
          ModelFace modelFace = this.getQuad().getFaces().elementAt(i);
          MeshFace meshFace = new MeshFace();
          meshFace.getPoints().add(modelFace.v2 + triCount);
          meshFace.getPoints().add(modelFace.v3 + triCount);
          meshFace.getPoints().add(modelFace.v1 + triCount);
          meshFace.getPoints().add(modelFace.v0 + triCount);
          mesh.getFaces().add(meshFace);
        }
      }      
      
      return mesh;
    }
    
    public ModelGeometry getTriangles() {return this.m_triangles;}
    public ModelGeometry getQuad() {return this.m_quads;}
  }
  
  public class ModelGeometry {
    public long vertexOffset;
    public long vertexCount;
    public long normalOffset;
    public long normalCount;
    public long faceOffset;
    public long faceCount;
    public long textureOffset;
    
    private Vector<Vector4> m_vertices;
    private Vector<Vector4> m_normals;
    private Vector<ModelFace> m_faces;
    private Vector<ModelTexture> m_textures;    
    
    public ModelGeometry() {
      this.m_vertices = new Vector<Vector4>();
      this.m_normals = new Vector<Vector4>();
      this.m_faces = new Vector<ModelFace>();
      this.m_textures = new Vector<ModelTexture>();
    }
    
    public void read(java.io.RandomAccessFile stream) {
      byte b4[] = new byte[4];
      
      try {
        stream.read(b4);this.vertexOffset=Helper.bytesToLong(b4);
        stream.read(b4);this.vertexCount=Helper.bytesToLong(b4);
        stream.read(b4);this.normalOffset=Helper.bytesToLong(b4);
        stream.read(b4);this.normalCount=Helper.bytesToLong(b4);
        stream.read(b4);this.faceOffset=Helper.bytesToLong(b4);
        stream.read(b4);this.faceCount=Helper.bytesToLong(b4);
        stream.read(b4);this.textureOffset=Helper.bytesToLong(b4);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    public void readDataTriangle(java.io.RandomAccessFile stream, long offset) {
      byte b2[] = new byte[2];
      int i;
      double x,y,z,w;
      
      try {
        stream.seek(offset + 12 + this.vertexOffset);
        for(i=0;i<this.vertexCount;i++) {
          stream.read(b2);x=Helper.bytesToInt(b2);
          stream.read(b2);y=Helper.bytesToInt(b2);
          stream.read(b2);z=Helper.bytesToInt(b2);
          stream.read(b2);w=Helper.bytesToInt(b2);          
          this.getVertices().add(new Vector4(x,y,z,w));
        }
        stream.seek(offset + 12 + this.normalOffset);
        for(i=0;i<this.normalCount;i++) {
          stream.read(b2);x=Helper.bytesToInt(b2);
          stream.read(b2);y=Helper.bytesToInt(b2);
          stream.read(b2);z=Helper.bytesToInt(b2);
          stream.read(b2);w=Helper.bytesToInt(b2);          
          this.getNormals().add(new Vector4(x,y,z,w));
        }

        stream.seek(offset + 12 + this.faceOffset);
        for(i=0;i<this.faceCount;i++) {
          ModelFace face;
          face = new ModelFace();
          
          stream.read(b2);face.n0=Helper.bytesToUInt(b2);
          stream.read(b2);face.v0=Helper.bytesToUInt(b2);
          stream.read(b2);face.n1=Helper.bytesToUInt(b2);
          stream.read(b2);face.v1=Helper.bytesToUInt(b2);
          stream.read(b2);face.n2=Helper.bytesToUInt(b2);
          stream.read(b2);face.v2=Helper.bytesToUInt(b2);
          
          this.getFaces().add(face);
        }        

        // read textures
        stream.seek(offset + 12 + this.textureOffset);
        for(i=0;i<this.faceCount;i++) {
          ModelTexture texture;
          texture = new ModelTexture();
          
          texture.u0=stream.readByte()&0xff;
          texture.v0=stream.readByte()&0xff;
          stream.read(b2);texture.clutId=Helper.bytesToUInt(b2);
          texture.u1=stream.readByte()&0xff;
          texture.v1=stream.readByte()&0xff;
          stream.read(b2);texture.pageId=Helper.bytesToUInt(b2);
          texture.u2=stream.readByte()&0xff;
          texture.v2=stream.readByte()&0xff;
          stream.read(b2);texture.zero1=Helper.bytesToUInt(b2);
          
          this.getTextures().add(texture);
        } 
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    public void readDataQuad(java.io.RandomAccessFile stream, long offset) {
      byte b2[] = new byte[2];
      int i;
      double x,y,z,w;
      
      try {
        stream.seek(offset + 12 + this.vertexOffset);
        for(i=0;i<this.vertexCount;i++) {
          stream.read(b2);x=Helper.bytesToInt(b2);
          stream.read(b2);y=Helper.bytesToInt(b2);
          stream.read(b2);z=Helper.bytesToInt(b2);
          stream.read(b2);w=Helper.bytesToInt(b2);          
          this.getVertices().add(new Vector4(x,y,z,w));
        }
        stream.seek(offset + 12 + this.normalOffset);
        for(i=0;i<this.normalCount;i++) {
          stream.read(b2);x=Helper.bytesToInt(b2);
          stream.read(b2);y=Helper.bytesToInt(b2);
          stream.read(b2);z=Helper.bytesToInt(b2);
          stream.read(b2);w=Helper.bytesToInt(b2);          
          this.getNormals().add(new Vector4(x,y,z,w));
        }

        stream.seek(offset + 12 + this.faceOffset);
        for(i=0;i<this.faceCount;i++) {
          ModelFace face;
          face = new ModelFace();
          
          stream.read(b2);face.n0=Helper.bytesToUInt(b2);
          stream.read(b2);face.v0=Helper.bytesToUInt(b2);
          stream.read(b2);face.n1=Helper.bytesToUInt(b2);
          stream.read(b2);face.v1=Helper.bytesToUInt(b2);
          stream.read(b2);face.n2=Helper.bytesToUInt(b2);
          stream.read(b2);face.v2=Helper.bytesToUInt(b2);
          stream.read(b2);face.n3=Helper.bytesToUInt(b2);
          stream.read(b2);face.v3=Helper.bytesToUInt(b2);
          
          this.getFaces().add(face);
        }        

        // read textures
        stream.seek(offset + 12 + this.textureOffset);
        for(i=0;i<this.faceCount;i++) {
          ModelTexture texture;
          texture = new ModelTexture();
          
          texture.u0=stream.readByte()&0xff;
          texture.v0=stream.readByte()&0xff;
          stream.read(b2);texture.clutId=Helper.bytesToUInt(b2);
          texture.u1=stream.readByte()&0xff;
          texture.v1=stream.readByte()&0xff;
          stream.read(b2);texture.pageId=Helper.bytesToUInt(b2);
          texture.u2=stream.readByte()&0xff;
          texture.v2=stream.readByte()&0xff;
          stream.read(b2);texture.zero1=Helper.bytesToUInt(b2);
          texture.u3=stream.readByte()&0xff;
          texture.v3=stream.readByte()&0xff;
          stream.read(b2);texture.zero2=Helper.bytesToUInt(b2);
          
          this.getTextures().add(texture);
        } 
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    public Vector<Vector4> getVertices(){return this.m_vertices;}
    public Vector<Vector4> getNormals(){return this.m_normals;}
    public Vector<ModelFace> getFaces(){return this.m_faces;}
    public Vector<ModelTexture> getTextures(){return this.m_textures;}
  }
  
  public class ModelFace {
    public int n0;
    public int v0;
    public int n1;
    public int v1;
    public int n2;
    public int v2;
    public int n3;
    public int v3;
  }  
  
  public class ModelTexture {
    public int u0;
    public int v0;
    public int clutId;
    public int u1;
    public int v1;
    public int pageId;
    public int u2; 
    public int v2;
    public int zero1;
    public int u3; 
    public int v3;
    public int zero2;
  }
  
  public class ModelSkeleton {
    public int offset;
    public int length;
    public int count;
    public int size;  
    
    private Vector<Vector3> m_positions;
    
    public ModelSkeleton() {
      this.m_positions = new Vector<Vector3>();
    }
    
    public Vector<Vector3> getPositions(){return this.m_positions;}
  }
  
  public static void main(String[] args) {
    int i;
    String s;
    String filename = "data/em04f.emd";
    EMD2 emd;
    EMD2.ModelObject object;
    EMD2.ModelGeometry geometry;
    
    emd = new EMD2();
    emd.load(filename);
    
    System.out.println("[Directories]");
    for(i=0;i<emd.getDirectories().size();i++) {
      EMD2.Directory directory = emd.getDirectories().elementAt(i);
      System.out.println(i + " : " + directory.offset);
    }
    
    System.out.println("[Model]");
    System.out.println("Length : " + emd.getModel().length);
    System.out.println("Count : " + emd.getModel().count);
    for(i=0;i<emd.getModel().getObjects().size();i++) {
      object = emd.getModel().getObjects().elementAt(i);
      geometry = object.getTriangles();
      s = i + " :T:";
      s+=" VC=" + geometry.vertexCount;
      s+=" VO=" + geometry.vertexOffset;
      s+=" NC=" + geometry.normalCount;
      s+=" NO=" + geometry.normalOffset;
      s+=" FC=" + geometry.faceCount;
      s+=" FO=" + geometry.faceOffset;
      s+=" TO=" + geometry.textureOffset;      
      System.out.println(s);
      
      geometry = object.getQuad();
      s = i + " :Q:";
      s+=" VC=" + geometry.vertexCount;
      s+=" VO=" + geometry.vertexOffset;
      s+=" NC=" + geometry.normalCount;
      s+=" NO=" + geometry.normalOffset;
      s+=" FC=" + geometry.faceCount;
      s+=" FO=" + geometry.faceOffset;
      s+=" TO=" + geometry.textureOffset;      
      System.out.println(s);
    }

    ModelSkeleton skeleton = emd.getSkeleton();
    for(i=0;i<skeleton.getPositions().size();i++) {
      System.out.println(skeleton.getPositions().elementAt(i).toString());
    }
    
    for(i=0;i<emd.getModel().getObjects().size();i++) {
      object = emd.getModel().getObjects().elementAt(i);        
      Mesh mesh = object.getMesh();
      mesh.saveOBJ(filename + "." + i + ".obj");
    }
  }
}
