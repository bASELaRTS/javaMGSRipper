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
    byte[] b2 = new byte[2];
    byte[] b4 = new byte[4];
    int i,j;
    int x,y,z;
    KMDObject object;
    //long pointer;
    
    this.m_objects.clear();
    
    try {
      stream.seek(0);
      
      // parse header
      stream.read(b4);
      this.m_header.setNumberOfVisibleObjects(Helper.bytesToUInt(b4));
      
      stream.read(b4);
      this.m_header.setNumberOfObjects(Helper.bytesToUInt(b4));

      stream.read(b4);
      x = Helper.bytesToInt(b4);      
      stream.read(b4);
      y = Helper.bytesToInt(b4);
      stream.read(b4);
      z = Helper.bytesToInt(b4);
      this.m_header.getBoundingStart().setCoordinates(x,y,z);

      stream.read(b4);
      x = Helper.bytesToInt(b4);      
      stream.read(b4);
      y = Helper.bytesToInt(b4);
      stream.read(b4);
      z = Helper.bytesToInt(b4);
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

        stream.seek(object.getUVOffset());
        this.readKMDUVs(stream, object.getNumberOfFaces()*4, object.getUVs());
        
        object.getTextureIndices().clear();
        stream.seek(object.getUVNameOffset());
        for(j=0;j<object.getNumberOfFaces();j++) {
          stream.read(b2);
          x = Helper.bytesToInt(b2);
          object.getTextureIndices().add(x);
        }
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
        x = Helper.bytesToInt(b2);
        stream.read(b2);
        y = Helper.bytesToInt(b2);
        stream.read(b2);
        z = Helper.bytesToInt(b2);
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
        a = stream.readByte()&0xff;
        b = stream.readByte()&0xff;
        c = stream.readByte()&0xff;
        d = stream.readByte()&0xff;
        
        KMDOrder o = new KMDOrder();
        // anti clockwise
        o.setOrder(0, d);
        o.setOrder(1, c);
        o.setOrder(2, b);
        o.setOrder(3, a);        
        list.add(o);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void readKMDUVs(java.io.RandomAccessFile stream, long count, Vector<Vector2> list) {
    int i;
    int u,v;
    byte b1,b2;
    Vector2 v2;
    
    list.clear();
    
    try {        
      for(i=0;i<count;i++) {
        b1 = stream.readByte();
        b2 = stream.readByte();
        u = (b1&0xff);
        v = (b2&0xff);
        
        v2 = new Vector2();        
        v2.setCoordinates(u, v);
        
        list.add(v2);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static void objectInfo(KMDObject object, int index, boolean showVertices, boolean showFaceIndices, boolean showNormals, boolean showUVs, boolean showTextureIndices) {
    int i,j;
    
    System.out.println("[Object]");
    System.out.println("Id                        : " + Integer.toString(index));
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
    
    if (showFaceIndices) {
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

    if (showUVs) {
      System.out.println("[UVs]");
      for(j=0;j<object.getUVs().size();j++) {
        Vector2 uv = object.getUVs().elementAt(j);
        System.out.println(j + " : " + uv.toString());
      }
    }
    
    if (showTextureIndices) {
      System.out.println("[TextureIndices]");
      for(j=0;j<object.getTextureIndices().size();j++) {
        i = object.getTextureIndices().elementAt(j);
        System.out.println(j + " : " + Integer.toString(i));
      }
    }
  }
  
  public static void main(String[] args) {
    int i;
    String s;
    String filename = "";
    KMDObject object;
    
    int objectId = -1;
    boolean export = false;
    String filenameExportObject = "";
    
    boolean showVertices = false;
    boolean showVerticesOrder = false;
    boolean showNormals = false;
    boolean showUVs = false;
    boolean showTextureIndices = false;
    
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
      } else if (s.equals("-uv")) {
        showUVs = true;
      } else if (s.equals("-ti")) {
        showTextureIndices = true;
      } else if (s.equals("-id")) {
        objectId = Integer.parseInt(args[++i]);
      } else if (s.equals("-e")) {
        export = true;
      } else if (s.equals("-output")) {
        filenameExportObject = args[++i];
      }
    }
    
    if (filename.length()>0) {
      KMD kmd = new KMD();
      kmd.load(filename);
      
      if (!export) {             
        System.out.println("[Header]");
        System.out.println("Number of Visible Objects : " + kmd.getHeader().getNumberOfVisibleObjects());
        System.out.println("Number of Objects         : " + kmd.getHeader().getNumberOfObjects());
        System.out.println("Bounding box Start        : " + kmd.getHeader().getBoundingStart().toString());
        System.out.println("Bounding box End          : " + kmd.getHeader().getBoundingEnd().toString());
        System.out.println();
        
        System.out.println("[Objects]");
        if (objectId>=0) {
          object = kmd.getObjects().elementAt(objectId);
          objectInfo(object, objectId, showVertices, showVerticesOrder, showNormals, showUVs, showTextureIndices);
          System.out.println();
        } else {
          for(i=0;i<kmd.getHeader().getNumberOfObjects();i++) {
            object = kmd.getObjects().elementAt(i);
            objectInfo(object, i, showVertices, showVerticesOrder, showNormals, showUVs, showTextureIndices);
            System.out.println();
          }          
        }
        System.out.println();
      } else if (objectId>=0) { // if (exportObjectId<0) {
        object = kmd.getObjects().elementAt(objectId);
        if (filenameExportObject.length()>0) {
          object.exportObj(filenameExportObject);
        } else {
          object.exportObj(Integer.toString(objectId) + ".obj");
        }      
      } else {
        for(i=0;i<kmd.getObjects().size();i++) {
          object = kmd.getObjects().elementAt(i);
          object.exportObj(Integer.toString(i) + ".obj");
       }
      }
    } else {
      System.out.println("MGS KMD Ripper");
      System.out.println("bASELaRTS 2020");
      System.out.println();
      System.out.println("-f <filename.kmd> : opens filename.kmd");
      System.out.println("-id               : select specific id");
      System.out.println("-e                : exports object index");
      System.out.println("-output <abc.obj> : exports -e and sets output filename");
      System.out.println("-v                : shows vertices");
      System.out.println("-n                : shows normals");
      System.out.println("-uv               : shows uv coordinates");
      System.out.println("-ti               : shows texture indices");
      System.out.println();
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
    private Vector<Vector2> m_uvs;
    private Vector<Integer> m_textureIndices;
    
    public KMDObject() {
      this.m_boundingStart = new Vector3();
      this.m_boundingEnd = new Vector3();
      this.m_bone = new Vector3();
      
      this.m_vertices = new Vector<KMDVector>();
      this.m_verticesOrder = new Vector<KMDOrder>();
      this.m_normals = new Vector<KMDVector>();
      this.m_normalsOrder = new Vector<KMDOrder>();      
      this.m_uvs = new Vector<Vector2>();
      this.m_textureIndices = new Vector<Integer>();
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
        this.setUnknown1(Helper.bytesToUInt(b2));
        
        stream.read(b4);
        this.setNumberOfFaces(Helper.bytesToUInt(b4));

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
        this.m_unknown2 = Helper.bytesToUInt(b4);
        
        stream.read(b4);
        this.m_numberOfVertices = Helper.bytesToUInt(b4);
        stream.read(b4);
        this.m_offsetVertices = Helper.bytesToUInt(b4);
        stream.read(b4);
        this.m_offsetVerticesOrder = Helper.bytesToUInt(b4);
        stream.read(b4);
        this.m_numberOfNormals = Helper.bytesToUInt(b4);
        stream.read(b4);
        this.m_offsetNormals = Helper.bytesToUInt(b4);
        stream.read(b4);
        this.m_offsetNormalsOrder = Helper.bytesToLong(b4);
        stream.read(b4);
        this.m_offsetUV = Helper.bytesToUInt(b4);
        stream.read(b4);
        this.m_textureNameOffset = Helper.bytesToUInt(b4);
        
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
      Vector2 v2;
      
      try {
        fw = new FileWriter(filename);
        bw = new BufferedWriter(fw);
        
        line = "# MGS Ripper";
        bw.write(line);
        bw.newLine();
        
        line = "#mtllib " + filename + ".mtl";
        bw.write(line);
        bw.newLine();
        
        line = "# vertices";
        bw.write(line);
        bw.newLine();          
        for(i=0;i<this.getVerticesCount();i++) {
          KMDVector v = this.getVertices().elementAt(i);
          
          line = "v ";
          line += Double.toString(v.getPoint().x/(double)v.getWeight());
          line += " ";
          line += Double.toString((-v.getPoint().y)/(double)v.getWeight());
          line += " ";
          line += Double.toString(v.getPoint().z/(double)v.getWeight());
          
          bw.write(line);
          bw.newLine();          
        }
        
        line = "# uvs";
        bw.write(line);
        bw.newLine();          
        for(i=0;i<this.getUVs().size();i++) {
          v2 = this.getUVs().elementAt(i);
          
          line = "vt ";
          line += Double.toString(v2.x/256.0);
          line += " ";
          line += Double.toString(v2.y/256.0);
          
          bw.write(line);
          bw.newLine();          
        }

        line = "# normals";
        bw.write(line);
        bw.newLine();          
        for(i=0;i<this.getNormalCount();i++) {
          KMDVector vn = this.getNormals().elementAt(i);
          
          line = "vn ";
          line += Double.toString(vn.getPoint().x/(double)vn.getWeight());
          line += " ";
          line += Double.toString(vn.getPoint().y/(double)vn.getWeight());
          line += " ";
          line += Double.toString(vn.getPoint().z/(double)vn.getWeight());
          
          bw.write(line);
          bw.newLine();          
        }

        line = "# faces";        
        bw.write(line);
        bw.newLine();          
        
        line = "#usemtl texture";        
        bw.write(line);
        bw.newLine();          

        for(i=0;i<this.getNumberOfFaces();i++) {
          KMDOrder o = this.getVerticesOrders().elementAt(i);
          
          /*
          line = "f ";
          line += Integer.toString(o.getOrder(0)+1);
          line += " ";
          line += Integer.toString(o.getOrder(1)+1);
          line += " ";
          line += Integer.toString(o.getOrder(2)+1);
          line += " ";
          line += Integer.toString(o.getOrder(3)+1);
          /**/

          //*
          line = "f ";
          line += Integer.toString(o.getOrder(0)+1);
          line += "/" + Integer.toString(i*4+1);
          line += " ";
          line += Integer.toString(o.getOrder(1)+1);
          line += "/" + Integer.toString(i*4+2);
          line += " ";
          line += Integer.toString(o.getOrder(2)+1);
          line += "/" + Integer.toString(i*4+3);
          line += " ";
          line += Integer.toString(o.getOrder(3)+1);
          line += "/" + Integer.toString(i*4+4);
          /**/
          
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
    public Vector<Vector2> getUVs(){return this.m_uvs;}
    public Vector<Integer> getTextureIndices(){return this.m_textureIndices;}
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

