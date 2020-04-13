package javaMGSRipper;

import java.io.IOException;

public class DAREntry {
  private String m_filename;
  private int m_position;
  private int m_size;
  private byte[] m_data;      
  
  public DAREntry() {
    this.setFilename("");
    this.setPosition(0);
    this.setSize(0);
    this.setData(null);
  }
  
  public void read(java.io.RandomAccessFile stream) {
    byte b;
    byte[] b4 = new byte[4];
    byte[] data;
    
    String s;
    
    try {
      s = "";
      b = stream.readByte();
      while(b!=0) {
        s+=(char)b;
        b = stream.readByte();
      }          
      this.setFilename(s);
      
      int padding = (int)(stream.getFilePointer()%4);
      padding = (4-padding)%4;
      stream.skipBytes(padding);
      
      //stream.read(b4);
      //this.setPosition(Helper.bytesToInt(b4));
      
      stream.read(b4);
      this.setSize(Helper.bytesToInt(b4));
      
      data = new byte[this.getSize()];
      stream.read(data);
      this.setData(data);
      
      stream.readByte();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void setFilename(String s) {this.m_filename=s;}
  public String getFilename() {return this.m_filename;}      
  public void setPosition(int i) {this.m_position=i;}
  public int getPosition() {return this.m_position;}
  public void setSize(int i) {this.m_size=i;}
  public int getSize() {return this.m_size;}      
  public void setData(byte[] data) {this.m_data=data;}
  public byte[] getData() {return this.m_data;}
}