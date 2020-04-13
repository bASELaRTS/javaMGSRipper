package javaMGSRipper;

import java.io.FileNotFoundException;
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
    
    this.m_objects.clear();
    
    try {
      stream.seek(0);
      
      stream.read(b4);
      this.m_header.setNumberOfVisibleObjects(Helper.bytesToInt(b4));
      
      stream.read(b4);
      this.m_header.setNumberOfObjects(Helper.bytesToInt(b4));

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
      for(i=0;i<this.m_header.getNumberOfObjects();i++) {
        object = this.m_objects.elementAt(i);
        
        stream.seek(object.getVerticesOffset());
        this.readKMDVectors(stream, object.getVerticesCount(), object.getVertices());
      }
      
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void readKMDVectors(java.io.RandomAccessFile stream, int count, Vector<KMDVector> list) {
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
  public void readKMDOrders(java.io.RandomAccessFile stream, int count, Vector<KMDOrder> list) {
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
        o.setOrder(0, b);
        o.setOrder(0, c);
        o.setOrder(0, d);        
        list.add(o);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void readKMDUVs(java.io.RandomAccessFile stream, int count, Vector<KMDUV> list) {
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
    String filename;
    
    filename = "mouse.kmd";

    for(i=0;i<args.length;i++) {
      s = args[i];
      if (s.equals("-f")) {
        filename = args[++i];
      }
    }
    
    if (filename.length()>0) {
      KMD kmd = new KMD();
      kmd.load(filename);
      
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
        
        System.out.println("[Vertices]");
        for(j=0;j<object.getVerticesCount();j++) {
          KMDVector v = object.getVertices().elementAt(j);
          System.out.println(j + " : " + v.toString());
        }
      }
    } else {
      System.out.println("MGS KMD Ripper");
      System.out.println("-f <filename.dar> : opens filename.kmd");
    }
  }
  
  public KMDHeader getHeader() {return this.m_header;}
  public Vector<KMDObject> getObjects(){return this.m_objects;}
  
  public class KMDHeader {
    private int m_numberOfVisibleObjects;
    private int m_numberOfObjects;
    private Vector3 m_boundingStart;
    private Vector3 m_boundingEnd;
    
    public KMDHeader() {
      this.setNumberOfObjects(0);
      this.setNumberOfVisibleObjects(0);
      this.m_boundingStart = new Vector3();
      this.m_boundingEnd = new Vector3();
    }
        
    public void setNumberOfVisibleObjects(int i) {this.m_numberOfVisibleObjects=i;}
    public int getNumberOfVisibleObjects() {return this.m_numberOfVisibleObjects;}
    public void setNumberOfObjects(int i) {this.m_numberOfObjects=i;}
    public int getNumberOfObjects() {return this.m_numberOfObjects;}
    public Vector3 getBoundingStart() {return this.m_boundingStart;}
    public Vector3 getBoundingEnd() {return this.m_boundingEnd;}
  }
  
  public class KMDObject {
    private byte m_bitFlag1;
    private byte m_bitFlag2;
    private int m_unknown1;
    private int m_numberOfFaces;
    private Vector3 m_boundingStart;
    private Vector3 m_boundingEnd;
    private Vector3 m_bone;
    private int m_parentBoneId;
    private int m_unknown2;
    private int m_numberOfVertices;
    private int m_offsetVertices;
    private int m_offsetVerticesOrder;
    private int m_numberOfNormals;
    private int m_offsetNormals;
    private int m_offsetNormalsOrder;
    private int m_offsetUV;
    private int m_textureNameOffset;
    
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
        this.setNumberOfFaces(Helper.bytesToInt(b4));

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
        this.m_numberOfVertices = Helper.bytesToInt(b4);
        stream.read(b4);
        this.m_offsetVertices = Helper.bytesToInt(b4);
        stream.read(b4);
        this.m_offsetVerticesOrder = Helper.bytesToInt(b4);
        stream.read(b4);
        this.m_numberOfNormals = Helper.bytesToInt(b4);
        stream.read(b4);
        this.m_offsetNormals = Helper.bytesToInt(b4);
        stream.read(b4);
        this.m_offsetNormalsOrder = Helper.bytesToInt(b4);
        stream.read(b4);
        this.m_offsetUV = Helper.bytesToInt(b4);
        stream.read(b4);
        this.m_textureNameOffset = Helper.bytesToInt(b4);
        
      } catch (IOException e) {
        e.printStackTrace();
      }
    } 
        
    public void setBitFlag1(byte b) {this.m_bitFlag1=b;}
    public byte getBitFlag1() {return this.m_bitFlag1;}
    public void setBitFlag2(byte b) {this.m_bitFlag2=b;}
    public byte getBitFlag2() {return this.m_bitFlag2;}
    public void setUnknown1(int i) {this.m_unknown1=i;}
    public int getUnknown1() {return this.m_unknown1;}
    public void setNumberOfFaces(int i) {this.m_numberOfFaces=i;}
    public int getNumberOfFaces() {return this.m_numberOfFaces;}
    public Vector3 getBoundingStart() {return this.m_boundingStart;}
    public Vector3 getBoundingEnd() {return this.m_boundingEnd;}
    public Vector3 getBone() {return this.m_bone;}
    public void setParentBoneId(int i) {this.m_parentBoneId=i;}
    public int getParentBoneId() {return this.m_parentBoneId;}
    public void setUnknown2(int i) {this.m_unknown2=i;}
    public int getUnknown2() {return this.m_unknown2;}

    public void setVerticesCount(int i) {this.m_numberOfVertices=i;}
    public int getVerticesCount() {return this.m_numberOfVertices;}
    public void setVerticesOffset(int i) {this.m_offsetVertices=i;}
    public int getVerticesOffset() {return this.m_offsetVertices;}
    public void setVerticesOrderOffset(int i) {this.m_offsetVerticesOrder=i;}
    public int getVerticesOrderOffset() {return this.m_offsetVerticesOrder;}

    public void setNormalCount(int i) {this.m_numberOfNormals=i;}
    public int getNormalCount() {return this.m_numberOfNormals;}
    public void setNormalOffset(int i) {this.m_offsetNormals=i;}
    public int getNormalOffset() {return this.m_offsetNormals;}
    public void setNormalOrderOffset(int i) {this.m_offsetNormalsOrder=i;}
    public int getNormalOrderOffset() {return this.m_offsetNormalsOrder;}

    public void setUVOffset(int i) {this.m_offsetUV=i;}
    public int getUVOffset() {return this.m_offsetUV;}

    public void setUVNameOffset(int i) {this.m_textureNameOffset=i;}
    public int getUVNameOffset() {return this.m_textureNameOffset;}
    
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

