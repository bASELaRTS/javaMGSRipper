package javaMGSRipper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

public class EMD {
  private long m_directoryOffset;
  private long m_directoryCount;
  private Vector<Directory> m_directories;
  private ModelHeader m_modelHeader;

  public EMD() {
    this.m_directories = new Vector<Directory>();
    this.m_modelHeader = new ModelHeader();
  }
  
  public void load(String filename) {
    try {
      this.load(new java.io.RandomAccessFile(filename, "r"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  public void load(java.io.RandomAccessFile stream) {
    byte[] b4 = new byte[4];
    int i;
    Directory directory;
    
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
  }
  
  public class ModelObject {
    private ModelMeshOffsets m_triangle;
    private ModelMeshOffsets m_quad;
    
    public ModelObject() {
      this.m_triangle = new ModelMeshOffsets();
      this.m_quad = new ModelMeshOffsets();
    }
    
    public ModelMeshOffsets getTriangle() {return this.m_triangle;}
    public ModelMeshOffsets getQuad() {return this.m_quad;}
  }
  
  public static void main(String[] args) {
    int i;
    EMD emd = new EMD();
    emd.load("G:\\Pl0\\emd0\\EM01E.EMD");
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
    
  }

}
