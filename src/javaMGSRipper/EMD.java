package javaMGSRipper;

import java.io.FileNotFoundException;
import java.io.IOException;

public class EMD {
  private Directory m_directory;

  public EMD() {
    
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
    
    try {
      stream.seek(0);
      
      this.m_directory = new Directory();
      this.m_directory.read(stream);
      
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }  
  
  public class Directory {
    private long m_offset;
    private long m_count;
    
    public Directory() {
      this.setOffset(0);
      this.setCount(0);
    }
    
    public void read(java.io.RandomAccessFile stream) {
      byte[] b4 = new byte[4];
      try {
        stream.read(b4);
        this.setOffset(Helper.bytesToLong(b4));
        stream.read(b4);
        this.setCount(Helper.bytesToLong(b4));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    
    public void setOffset(long l) {this.m_offset=l;}
    public long getOffset() {return this.m_offset;}
    public void setCount(long l) {this.m_count=l;}
    public long getCount() {return this.m_count;}
  }
  
  public static void main(String[] args) {
    EMD emd = new EMD();
    emd.load("G:\\Pl0\\emd0\\EM01E.EMD");
  }

}
