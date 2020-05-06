package javaMGSRipper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class EMD {
  private long m_directoryOffset;
  private long m_directoryCount;
  private Vector<Directory> m_directories;
  private ModelHeader m_modelHeader;
  private Vector<ModelObject> m_modelObjects;
  private Vector<Mesh> m_meshes;

  public EMD() {
    this.m_directories = new Vector<Directory>();
    this.m_modelHeader = new ModelHeader();
    this.m_modelObjects = new Vector<ModelObject>();
    this.m_meshes = new Vector<Mesh>();
  }
  
  public void load(String filename) {
    try {
      this.load(new java.io.RandomAccessFile(filename, "r"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  public void load(java.io.RandomAccessFile stream) {
    byte[] b2 = new byte[2];
    byte[] b4 = new byte[4];
    int i,j;
    int x,y,z;
    long l;
    Directory directory;
    Vector3 v3;
    
    try {
      stream.seek(0);

      stream.read(b4);this.m_directoryOffset=Helper.bytesToLong(b4);
      stream.read(b4);this.m_directoryCount=Helper.bytesToLong(b4); //value==8 then RE2 value>8 then RE3, RE1 has no header 
      
      stream.seek(this.m_directoryOffset);
      for(i=0;i<this.getDirectoryCount();i++) {
        stream.read(b4);
        
        directory = new Directory();
        directory.setOffset(Helper.bytesToLong(b4));
        this.m_directories.add(directory);
      }
      
      // read model header
      if (this.getDirectoryCount()==8) {
        directory = this.m_directories.elementAt(7);
        stream.seek(directory.getOffset());
        this.m_modelHeader.read(stream);
        
        for(j=0;j<(this.m_modelHeader.getObjectCount()/2);j++) {
          ModelObject modelObject;
          modelObject = new ModelObject();
          modelObject.read(stream);
          this.getModelObjects().add(modelObject);
        }
      }
      
      // read meshes
      directory = this.m_directories.elementAt(7);
      for(i=0;i<this.getModelObjects().size();i++) {
        ModelObject modelObject = this.getModelObjects().elementAt(i);
        ModelMeshOffsets offsets;
        Mesh mesh = new Mesh();
        MeshFace face;
        int iv1,iv2,iv3,iv4;
        int in1,in2,in3,in4;

        // vertices
        offsets = modelObject.getTriangle();          
        l = directory.getOffset() + 12 + offsets.vertexOffset;          
        stream.seek(l);
        for(j=0;j<offsets.vertexCount;j++) {            
          stream.read(b2);x = Helper.bytesToInt(b2);
          stream.read(b2);y = Helper.bytesToInt(b2);
          stream.read(b2);z = Helper.bytesToInt(b2);
          stream.read(b2); // always zero
          mesh.getVertices().add(new Vector3(x,y,z));
        }

        // normals
        offsets = modelObject.getTriangle();          
        l = directory.getOffset() + 12 + offsets.normalOffset;          
        stream.seek(l);
        for(j=0;j<offsets.normalCount;j++) {            
          stream.read(b2);x = Helper.bytesToInt(b2);
          stream.read(b2);y = Helper.bytesToInt(b2);
          stream.read(b2);z = Helper.bytesToInt(b2);
          stream.read(b2); // always zero
          mesh.getNormals().add(new Vector3(x,y,z));
        }
        
        // uvs
        offsets = modelObject.getTriangle();          
        l = directory.getOffset() + 12 + offsets.textureOffset;          
        stream.seek(l);
        for(j=0;j<offsets.normalCount;j++) {            
          stream.read(b2);x = Helper.bytesToInt(b2);
          stream.read(b2);y = Helper.bytesToInt(b2);
          stream.read(b2);z = Helper.bytesToInt(b2);
          stream.read(b2); // always zero
          mesh.getUVs().add(new Vector2(x,y));
        }

        /*
        offsets = modelObject.getQuad();          
        l = directory.getOffset() + 12 + offsets.vertexOffset;          
        stream.seek(l);
        for(j=0;j<offsets.vertexCount;j++) {            
          stream.read(b2);x = Helper.bytesToInt(b2);
          stream.read(b2);y = Helper.bytesToInt(b2);
          stream.read(b2);z = Helper.bytesToInt(b2);
          stream.read(b2); // always zero
          mesh.getVertices().add(new Vector3(x,y,z));
        }
        /**/
        
        // faces
        offsets = modelObject.getTriangle();
        if (offsets.faceCount>0) {
          l = directory.getOffset() + 12 + offsets.faceOffset;   
          stream.seek(l);
          for(j=0;j<offsets.faceCount;j++) {
            face = new MeshFace();
            stream.read(b2);in1=Helper.bytesToUInt(b2);
            stream.read(b2);iv1=Helper.bytesToUInt(b2);
            stream.read(b2);in2=Helper.bytesToUInt(b2);
            stream.read(b2);iv2=Helper.bytesToUInt(b2);
            stream.read(b2);in3=Helper.bytesToUInt(b2);
            stream.read(b2);iv3=Helper.bytesToUInt(b2);
            face.getPoints().add(iv3);
            face.getPoints().add(iv2);
            face.getPoints().add(iv1);
            mesh.getFaces().add(face);
          }          
        }
        
        offsets = modelObject.getQuad();
        if (offsets.faceCount>0) {
          l = directory.getOffset() + 12 + offsets.faceOffset;   
          stream.seek(l);
          for(j=0;j<offsets.faceCount;j++) {
            face = new MeshFace();
            stream.read(b2);in1=Helper.bytesToUInt(b2);
            stream.read(b2);iv1=Helper.bytesToUInt(b2);
            stream.read(b2);in2=Helper.bytesToUInt(b2);
            stream.read(b2);iv2=Helper.bytesToUInt(b2);
            stream.read(b2);in3=Helper.bytesToUInt(b2);
            stream.read(b2);iv3=Helper.bytesToUInt(b2);
            stream.read(b2);in4=Helper.bytesToUInt(b2);
            stream.read(b2);iv4=Helper.bytesToUInt(b2);
            face.getPoints().add(iv3);
            face.getPoints().add(iv4);
            face.getPoints().add(iv2);
            face.getPoints().add(iv1);
            mesh.getFaces().add(face);
          }
        }
        this.getMeshes().add(mesh);
      }
      
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  

  
  public long getDirectoryOffset() {return this.m_directoryOffset;}
  public long getDirectoryCount() {return this.m_directoryCount;}
  public Vector<Directory> getDirectories() {return this.m_directories;}
  public ModelHeader getModelHeader() {return this.m_modelHeader;}
  public Vector<ModelObject> getModelObjects(){return this.m_modelObjects;}
  public Vector<Mesh> getMeshes(){return this.m_meshes;}
  
  public class Directory{
    private long m_offset;
    public void setOffset(long l) {this.m_offset=l;}
    public long getOffset() {return this.m_offset;}
  }  
  
  public class ModelHeader {
    private long m_length;
    private long m_unknown;
    private long m_objCount;
    
    public void read(java.io.RandomAccessFile stream) {
      byte[] b4=new byte[4];
      
      try {
        stream.read(b4);this.setLength(Helper.bytesToLong(b4));
        stream.read(b4);this.setUnknown(Helper.bytesToLong(b4));
        stream.read(b4);this.setObjectCount(Helper.bytesToLong(b4));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    public void setLength(long l) {this.m_length=l;}
    public long getLength() {return this.m_length;}
    public void setUnknown(long l) {this.m_unknown=l;}
    public long getUnknown() {return this.m_unknown;}
    public void setObjectCount(long l) {this.m_objCount=l;}
    public long getObjectCount() {return this.m_objCount;}
  }
  
  public class ModelMeshOffsets {
    public long vertexOffset;
    public long vertexCount;
    public long normalOffset;
    public long normalCount;
    public long faceOffset;
    public long faceCount;
    public long textureOffset;
    
    public void read(java.io.RandomAccessFile stream) {
      byte[] b4 = new byte[4];
      
      try {
        stream.read(b4);this.vertexOffset = Helper.bytesToLong(b4);
        stream.read(b4);this.vertexCount = Helper.bytesToLong(b4);
        stream.read(b4);this.normalOffset = Helper.bytesToLong(b4);
        stream.read(b4);this.normalCount = Helper.bytesToLong(b4);
        stream.read(b4);this.faceOffset = Helper.bytesToLong(b4);
        stream.read(b4);this.faceCount = Helper.bytesToLong(b4);
        stream.read(b4);this.textureOffset = Helper.bytesToLong(b4);
        
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public class ModelMeshFaceTriangle {
    public Vector2 uv1;
    public Vector2 uv2;
    public Vector2 uv3;
    public int clutId;
    public int pageId;
    
    public ModelMeshFaceTriangle() {
      this.uv1 = new Vector2();
      this.uv2 = new Vector2();
      this.uv3 = new Vector2();
    }
  }
  
  public class ModelObject {
    private ModelMeshOffsets m_triangle;
    private ModelMeshOffsets m_quad;
    
    public ModelObject() {
      this.m_triangle = new ModelMeshOffsets();
      this.m_quad = new ModelMeshOffsets();
    }
    
    public void read(java.io.RandomAccessFile stream) {
      this.getTriangle().read(stream);
      this.getQuad().read(stream);;        
    }
    
    public ModelMeshOffsets getTriangle() {return this.m_triangle;}
    public ModelMeshOffsets getQuad() {return this.m_quad;}
  }
  
  public static void main(String[] args) {
    int i;
    EMD emd = new EMD();
    emd.load("data\\EM01E.EMD");
    System.out.println("[Header]");
    System.out.println("Directory offset : " + emd.getDirectoryOffset());
    System.out.println("Directory count  : " + emd.getDirectoryCount());

    System.out.println("[Directory]");
    for(i=0;i<emd.getDirectoryCount();i++) {
      System.out.println(i + " : " + emd.getDirectories().elementAt(i).getOffset());
    }

    System.out.println("[ModelHeader]");
    System.out.println("Length           : " + emd.getModelHeader().getLength());
    System.out.println("Unknown          : " + emd.getModelHeader().getUnknown());
    System.out.println("Object count     : " + emd.getModelHeader().getObjectCount());
    
    System.out.println("[ModelObjects]");
    for(i=0;i<emd.getModelObjects().size();i++) {
      ModelMeshOffsets offsets;
      offsets = emd.getModelObjects().elementAt(i).getTriangle();
      System.out.println(i + " : T"
          + " : VCount  : " + offsets.vertexCount
          + " : VOffset : " + offsets.vertexOffset
          + " : NCount : " + offsets.normalCount
          + " : NOffset : " + offsets.normalOffset
          + " : FCount : " + offsets.faceCount
          + " : FOffset : " + offsets.faceOffset
          + " : TOffset : " + offsets.textureOffset
      );
      offsets = emd.getModelObjects().elementAt(i).getQuad();
      System.out.println(i + " : Q"
          + " : VCount  : " + offsets.vertexCount
          + " : VOffset : " + offsets.vertexOffset
          + " : NCount : " + offsets.normalCount
          + " : NOffset : " + offsets.normalOffset
          + " : FCount : " + offsets.faceCount
          + " : FOffset : " + offsets.faceOffset
          + " : TOffset : " + offsets.textureOffset
      );
    }
    
    /*
    for(i=0;i<emd.getMeshes().size();i++) {
      emd.getMeshes().elementAt(i).exportObj("data//EM01E."+i+".obj");
    }
    /**/
    i = 0;emd.getMeshes().elementAt(i).saveOBJ("data//EM01E."+i+".obj");
  }

}
