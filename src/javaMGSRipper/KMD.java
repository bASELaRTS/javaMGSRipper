package javaMGSRipper;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

public class KMD {
  /*
   * Header
   * u4b number of visible objects
   * u4b number of objects
   * i4b start bounding box y
   * i4b start bounding box z
   * i4b start bounding box x
   * i4b end bounding box y
   * i4b end bounding box z
   * i4b end bounding box x
   * 
   * Object
   * 1b bitflag
   * 1b bitflag
   * 
   */
  private KMDHeader m_header;
  private Vector<KMDObject> m_objects; 
  
  public KMD() {
    this.m_header = new KMDHeader();
    this.m_objects = new Vector<KMDObject>();
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
    int x,y,z;
    KMDObject object;
    //long pointer;
    
    this.m_objects.clear();
    
    try {
      stream.seek(0);
      
      // parse header
      stream.read(b4);
      this.m_header.setNumberOfVisibleObjects(Helper.bytesToLong(b4));
      
      stream.read(b4);
      this.m_header.setNumberOfObjects(Helper.bytesToLong(b4));

      stream.read(b4);
      y = Helper.bytesToInt(b4);
      stream.read(b4);
      z = Helper.bytesToInt(b4);
      stream.read(b4);
      x = Helper.bytesToInt(b4);      
      this.m_header.getBoundingStart().setCoordinates(x,y,z);

      stream.read(b4);
      y = Helper.bytesToInt(b4);
      stream.read(b4);
      z = Helper.bytesToInt(b4);
      stream.read(b4);
      x = Helper.bytesToInt(b4);      
      this.m_header.getBoundingEnd().setCoordinates(x,y,z);
      
      // read all objects
      for(i=0;i<this.m_header.getNumberOfObjects();i++) {
        object = new KMDObject();
        object.read(stream);
        this.m_objects.add(object);                
      }
      
      // for each object read vertices, orders normals etc.
      //*
      for(i=0;i<this.m_header.getNumberOfObjects();i++) {
        object = this.m_objects.elementAt(i);
        
        stream.seek(object.getVerticesOffset());
        this.readKMDVectors(stream, object.getVerticesCount(), object.getVertices());

        stream.seek(object.getVerticesOrderOffset());
        this.readKMDOrders(stream, object.getNumberOfFaces(), object.getVerticesOrders());

        stream.seek(object.getNormalOffset());
        this.readKMDVectors(stream, object.getNormalCount(), object.getNormals());
      }
      /**/
      
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void readKMDVectors(java.io.RandomAccessFile stream, long count, Vector<KMDVector> list) {
    int i;
    byte[] b2 = new byte[2];
    int x,y,z,w;
   
    list.clear();
    
    try {        
      for(i=0;i<count;i++) {
        stream.read(b2);
        y = Helper.bytesToInt(b2);
        stream.read(b2);
        z = Helper.bytesToInt(b2);
        stream.read(b2);
        x = Helper.bytesToInt(b2);
        stream.read(b2);
        w = Helper.bytesToInt(b2);
        
        KMDVector v = new KMDVector();
        v.getPoint().setCoordinates(x, y, z);
        v.setWeight(w);
        
        list.add(v);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void readKMDOrders(java.io.RandomAccessFile stream, long count, Vector<KMDOrder> list) {
    int i;
    int a,b,c,d;
    
    list.clear();
    
    try {        
      for(i=0;i<count;i++) {
        a = (int)stream.readByte();
        b = (int)stream.readByte();
        c = (int)stream.readByte();
        d = (int)stream.readByte();
        
        KMDOrder o = new KMDOrder();
        o.setOrder(0, a);
        o.setOrder(1, b);
        o.setOrder(2, c);
        o.setOrder(3, d);        
        list.add(o);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void readKMDUVs(java.io.RandomAccessFile stream, long count, Vector<KMDUV> list) {
    int i;
    int u,v;
    
    list.clear();
    
    try {        
      for(i=0;i<count;i++) {
        u = (int)stream.readByte();
        v = (int)stream.readByte();
        
        KMDUV o = new KMDUV();
        o.getPoint().setCoordinates(u, v);
        list.add(o);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void main(String[] args) {
    int i,j;
    String s;
    String filename = "00a.kmd";
    
    int exportObjectId = -1;
    String filenameExportObject = "object.obj";
    
    boolean showVertices = false;
    boolean showVerticesOrder = false;
    boolean showNormals = false;
    boolean showNormalsOrder = false;
    
    for(i=0;i<args.length;i++) {
      s = args[i];
      if (s.equals("-f")) {
        filename = args[++i];
      } else if (s.equals("-v")) {
        showVertices = true;
      } else if (s.equals("-vo")) {
        showVerticesOrder = true;
      } else if (s.equals("-n")) {
        showNormals = true;
      } else if (s.equals("-no")) {
        showNormalsOrder = true;
      } else if (s.equals("-e")) {
        exportObjectId = Integer.parseInt(args[++i]);
      } else if (s.equals("-output")) {
        filenameExportObject = args[++i];
      }
    }
    
    if (filename.length()>0) {
      KMD kmd = new KMD();
      kmd.load(filename);
      
      if (exportObjectId<0) {             
        System.out.println("[Header]");
        System.out.println("Number of Visible Objects : " + kmd.getHeader().getNumberOfVisibleObjects());
        System.out.println("Number of Objects         : " + kmd.getHeader().getNumberOfObjects());
        System.out.println("Bounding box Start        : " + kmd.getHeader().getBoundingStart().toString());
        System.out.println("Bounding box End          : " + kmd.getHeader().getBoundingEnd().toString());
        System.out.println();
        
        System.out.println("[Objects]");
        for(i=0;i<kmd.getHeader().getNumberOfObjects();i++) {
          KMDObject object;
          object = kmd.getObjects().elementAt(i);
          System.out.println("[Object - " + Integer.toString(i) + "]");
          System.out.println("Bitflag1                  : " + object.getBitFlag1());      
          System.out.println("Bitflag2                  : " + object.getBitFlag2());      
          System.out.println("Unknown1                  : " + object.getUnknown1());      
          System.out.println("Faces                     : " + object.getNumberOfFaces());      
          System.out.println("Bounding box Start        : " + object.getBoundingStart().toString());      
          System.out.println("Bounding box End          : " + object.getBoundingEnd().toString());      
          System.out.println("Bone                      : " + object.getBone().toString());      
          System.out.println("Parent bone ID            : " + object.getParentBoneId());      
          System.out.println("Unknown2                  : " + object.getUnknown2());      
          System.out.println("Vertice count             : " + object.getVerticesCount());      
          System.out.println("Vertice offset            : " + object.getVerticesOffset());      
          System.out.println("Vertice order offset      : " + object.getVerticesOrderOffset());      
          System.out.println("Normal count              : " + object.getNormalCount());      
          System.out.println("Normal offset             : " + object.getNormalOffset());      
          System.out.println("Normal order offset       : " + object.getNormalOrderOffset());      
          System.out.println("UV offset                 : " + object.getUVOffset());      
          System.out.println("UV name offset            : " + object.getUVNameOffset());
          
          if (showVertices) {
            System.out.println("[Vertices]");
            for(j=0;j<object.getVerticesCount();j++) {
              KMDVector v = object.getVertices().elementAt(j);
              System.out.println(j + " : " + v.toString());
            }
          }
          
          if (showVerticesOrder) {
            System.out.println("[Vertices Order]");
            for(j=0;j<object.getNumberOfFaces();j++) {
              KMDOrder o = object.getVerticesOrders().elementAt(j);
              System.out.println(j + " : " + o.toString());
            }
          }
          
          if (showNormals) {
            System.out.println("[Normals]");
            for(j=0;j<object.getNormalCount();j++) {
              KMDVector v = object.getNormals().elementAt(j);
              System.out.println(j + " : " + v.toString());
            }
          }
  
          System.out.println();
        }
      } else { // if (exportObjectId<0) {
        KMDObject o = kmd.getObjects().elementAt(exportObjectId);
        o.exportObj(filenameExportObject);
      }
    } else {
      System.out.println("MGS KMD Ripper");
      System.out.println("-f <filename.dar> : opens filename.kmd");
    }
  }
  
  public KMDHeader getHeader() {return this.m_header;}
  public Vector<KMDObject> getObjects(){return this.m_objects;}
  
  public class KMDHeader {
    private long m_numberOfVisibleObjects;
    private long m_numberOfObjects;
    private Vector3 m_boundingStart;
    private Vector3 m_boundingEnd;
    
    public KMDHeader() {
      this.setNumberOfObjects(0);
      this.setNumberOfVisibleObjects(0);
      this.m_boundingStart = new Vector3();
      this.m_boundingEnd = new Vector3();
    }
        
    public void setNumberOfVisibleObjects(long i) {this.m_numberOfVisibleObjects=i;}
    public long getNumberOfVisibleObjects() {return this.m_numberOfVisibleObjects;}
    public void setNumberOfObjects(long i) {this.m_numberOfObjects=i;}
    public long getNumberOfObjects() {return this.m_numberOfObjects;}
    public Vector3 getBoundingStart() {return this.m_boundingStart;}
    public Vector3 getBoundingEnd() {return this.m_boundingEnd;}
  }
  
  public class KMDObject {
    private byte m_bitFlag1;
    private byte m_bitFlag2;
    private int m_unknown1;
    private long m_numberOfFaces;
    private Vector3 m_boundingStart;
    private Vector3 m_boundingEnd;
    private Vector3 m_bone;
    private int m_parentBoneId;
    private long m_unknown2;
    private long m_numberOfVertices;
    private long m_offsetVertices;
    private long m_offsetVerticesOrder;
    private long m_numberOfNormals;
    private long m_offsetNormals;
    private long m_offsetNormalsOrder;
    private long m_offsetUV;
    private long m_textureNameOffset;
    
    private Vector<KMDVector> m_vertices;
    private Vector<KMDOrder> m_verticesOrder;
    private Vector<KMDVector> m_normals;
    private Vector<KMDOrder> m_normalsOrder;
    
    public KMDObject() {
      this.m_boundingStart = new Vector3();
      this.m_boundingEnd = new Vector3();
      this.m_bone = new Vector3();
      
      this.m_vertices = new Vector<KMDVector>();
      this.m_verticesOrder = new Vector<KMDOrder>();
      this.m_normals = new Vector<KMDVector>();
      this.m_normalsOrder = new Vector<KMDOrder>();
    }
    
    public void read(java.io.RandomAccessFile stream) {
      byte b;
      byte[] b2 = new byte[2];
      byte[] b4 = new byte[4];
      int x,y,z;
      
      try {
        b = stream.readByte();
        this.setBitFlag1(b);
        b = stream.readByte();
        this.setBitFlag2(b);
        
        stream.read(b2);
        this.setUnknown1(Helper.bytesToInt(b2));
        
        stream.read(b4);
        this.setNumberOfFaces(Helper.bytesToLong(b4));

        stream.read(b4);
        x = Helper.bytesToInt(b4);
        stream.read(b4);
        y = Helper.bytesToInt(b4);
        stream.read(b4);
        z = Helper.bytesToInt(b4);
        this.getBoundingStart().setCoordinates(x, y, z);
        
        stream.read(b4);
        x = Helper.bytesToInt(b4);
        stream.read(b4);
        y = Helper.bytesToInt(b4);
        stream.read(b4);
        z = Helper.bytesToInt(b4);
        this.getBoundingEnd().setCoordinates(x, y, z);
        
        stream.read(b4);
        x = Helper.bytesToInt(b4);
        stream.read(b4);
        y = Helper.bytesToInt(b4);
        stream.read(b4);
        z = Helper.bytesToInt(b4);
        this.getBone().setCoordinates(x, y, z);
        
        stream.read(b4);
        this.m_parentBoneId = Helper.bytesToInt(b4);
        
        stream.read(b4);
        this.m_unknown2 = Helper.bytesToInt(b4);
        
        stream.read(b4);
        this.m_numberOfVertices = Helper.bytesToLong(b4);
        stream.read(b4);
        this.m_offsetVertices = Helper.bytesToLong(b4);
        stream.read(b4);
        this.m_offsetVerticesOrder = Helper.bytesToLong(b4);
        stream.read(b4);
        this.m_numberOfNormals = Helper.bytesToLong(b4);
        stream.read(b4);
        this.m_offsetNormals = Helper.bytesToLong(b4);
        stream.read(b4);
        this.m_offsetNormalsOrder = Helper.bytesToLong(b4);
        stream.read(b4);
        this.m_offsetUV = Helper.bytesToLong(b4);
        stream.read(b4);
        this.m_textureNameOffset = Helper.bytesToLong(b4);
        
        stream.read(b4);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } 
        
    public void exportObj(String filename) {
      java.io.FileWriter fw;
      java.io.BufferedWriter bw;
      String line;
      int i;
      
      try {
        fw = new FileWriter(filename);
        bw = new BufferedWriter(fw);
        
        for(i=0;i<this.getVerticesCount();i++) {
          KMDVector v = this.getVertices().elementAt(i);
          
          line = "v ";
          line += Double.toString(v.getPoint().x/256.0);
          line += " ";
          line += Double.toString(v.getPoint().y/256.0);
          line += " ";
          line += Double.toString(v.getPoint().z/256.0);
          
          bw.write(line);
          bw.newLine();          
        }
        
        for(i=0;i<this.getNumberOfFaces();i++) {
          KMDOrder o = this.getVerticesOrders().elementAt(i);
          
          line = "f ";
          line += Integer.toString(o.getOrder(0)+1);
          line += " ";
          line += Integer.toString(o.getOrder(1)+1);
          line += " ";
          line += Integer.toString(o.getOrder(2)+1);
          line += " ";
          line += Integer.toString(o.getOrder(3)+1);
          
          bw.write(line);
          bw.newLine();          
        }
        
        bw.flush();
        bw.close();
        fw.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    
    public void setBitFlag1(byte b) {this.m_bitFlag1=b;}
    public byte getBitFlag1() {return this.m_bitFlag1;}
    public void setBitFlag2(byte b) {this.m_bitFlag2=b;}
    public byte getBitFlag2() {return this.m_bitFlag2;}
    public void setUnknown1(int i) {this.m_unknown1=i;}
    public int getUnknown1() {return this.m_unknown1;}
    public void setNumberOfFaces(long i) {this.m_numberOfFaces=i;}
    public long getNumberOfFaces() {return this.m_numberOfFaces;}
    public Vector3 getBoundingStart() {return this.m_boundingStart;}
    public Vector3 getBoundingEnd() {return this.m_boundingEnd;}
    public Vector3 getBone() {return this.m_bone;}
    public void setParentBoneId(int i) {this.m_parentBoneId=i;}
    public int getParentBoneId() {return this.m_parentBoneId;}
    public void setUnknown2(int i) {this.m_unknown2=i;}
    public long getUnknown2() {return this.m_unknown2;}

    public void setVerticesCount(long i) {this.m_numberOfVertices=i;}
    public long getVerticesCount() {return this.m_numberOfVertices;}
    public void setVerticesOffset(long i) {this.m_offsetVertices=i;}
    public long getVerticesOffset() {return this.m_offsetVertices;}
    public void setVerticesOrderOffset(long i) {this.m_offsetVerticesOrder=i;}
    public long getVerticesOrderOffset() {return this.m_offsetVerticesOrder;}

    public void setNormalCount(long i) {this.m_numberOfNormals=i;}
    public long getNormalCount() {return this.m_numberOfNormals;}
    public void setNormalOffset(long i) {this.m_offsetNormals=i;}
    public long getNormalOffset() {return this.m_offsetNormals;}
    public void setNormalOrderOffset(long i) {this.m_offsetNormalsOrder=i;}
    public long getNormalOrderOffset() {return this.m_offsetNormalsOrder;}

    public void setUVOffset(long i) {this.m_offsetUV=i;}
    public long getUVOffset() {return this.m_offsetUV;}

    public void setUVNameOffset(long i) {this.m_textureNameOffset=i;}
    public long getUVNameOffset() {return this.m_textureNameOffset;}
    
    public Vector<KMDVector> getVertices(){return this.m_vertices;}
    public Vector<KMDOrder> getVerticesOrders(){return this.m_verticesOrder;}
    public Vector<KMDVector> getNormals(){return this.m_normals;}
    public Vector<KMDOrder> getNormalsOrders(){return this.m_normalsOrder;}
}
  
  public class KMDVector{
    private Vector3 m_point;
    private int m_weight;
    
    public KMDVector() {
      this.m_point = new Vector3();
      this.setWeight(0);
    }
    
    public String toString() {
      String output = "";
      output += "(";
      output += Double.toString(this.getPoint().x);
      output += ";";
      output += Double.toString(this.getPoint().y);
      output += ";";
      output += Double.toString(this.getPoint().z);
      output += ";";
      output += Integer.toString(this.getWeight());
      output += ")";
      return output;
    }
    
    public void setWeight(int i) {this.m_weight=i;}
    public int getWeight() {return this.m_weight;}
    public Vector3 getPoint() {return this.m_point;}
  }
  
  public class KMDOrder{
    private int[] m_order;
    
    public KMDOrder() {
      this.m_order = new int[4];

      for(int i=0;i<4;i++) {
        this.setOrder(i,0);
      }
    }
    
    public String toString() {
      String output = "(";
      output += Integer.toString(this.getOrder(0));
      output += ";";
      output += Integer.toString(this.getOrder(1));
      output += ";";
      output += Integer.toString(this.getOrder(2));
      output += ";";
      output += Integer.toString(this.getOrder(3));
      output += ")";
      return output;
    }
    
    public void setOrder(int index, int i) {this.m_order[index]=i;}
    public int getOrder(int index) {return this.m_order[index];}
  }
  
  public class KMDUV{
    private Vector2 m_point;
    public KMDUV() {
      this.m_point = new Vector2();
    }
    public Vector2 getPoint() {return this.m_point;}
  }
}

