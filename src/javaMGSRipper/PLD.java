package javaMGSRipper;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFrame;


public class PLD {
  private long m_directoryOffset;
  private long m_directoryCount;
  private Vector<Directory> m_directories;

  public PLD() {
    this.m_directories = new Vector<Directory>();
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
                 
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public long getDirectoryOffset() {return this.m_directoryOffset;}
  public long getDirectoryCount() {return this.m_directoryCount;}
  public Vector<Directory> getDirectories() {return this.m_directories;}
  
  public class Directory{
    private long m_offset;
    public void setOffset(long l) {this.m_offset=l;}
    public long getOffset() {return this.m_offset;}
  }  

  public static void main(String[] args) {
    int i;
    PLD pld = new PLD();
    String filename;
    filename = "G:\\Pl0\\PLD\\PL00.PLD";
    
    pld.load(filename);
    System.out.println("[Header]");
    System.out.println("Directory offset : " + pld.getDirectoryOffset());
    System.out.println("Directory count  : " + pld.getDirectoryCount());

    System.out.println("[Directory]");
    for(i=0;i<pld.getDirectoryCount();i++) {
      System.out.println(i + " : " + pld.getDirectories().elementAt(i).getOffset());
    }
    
    // extract TIM out of PLD
    TIM tim = new TIM();
    try {
      tim.load(new java.io.RandomAccessFile(filename, "r"), pld.getDirectories().elementAt(3).getOffset());
      
      for(i=0;i<tim.getNumberOfPalettes();i++) {
        ImageViewer viewer = new ImageViewer();
        BufferedImage image = tim.getImage(i);
        viewer.setTitle("PaletteIndex " + i + "/" + tim.getNumberOfPalettes());
        viewer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        viewer.setImage(image);
      }
      
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
