package javaMGSRipper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Vector;

public class WAD {
  private long m_directoryCount;
  private long m_directoryOffset;
  private Vector<Directory> m_directories;
  private RandomAccessFile m_stream;
  
  public WAD() {
    this.m_directories = new Vector<Directory>();
  }
  
  public void close() {
    
  }
  
  public void load(String filename) {
    try {
      this.m_stream = new RandomAccessFile(new java.io.File(filename),"r");
      this.load(this.m_stream);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }    
  }
  
  public void load(java.io.RandomAccessFile stream) {
    int i,j;
    boolean isWAD = false;
    byte b4[] = new byte[4];
    byte b8[] = new byte[8];
    Directory directory;
    
    try {
      stream.read(b4);
      if ((b4[1]=='W')&&(b4[2]=='A')&&(b4[3]=='D')) {
        if (b4[0]=='P') {
          isWAD = true;
        } else if (b4[0]=='I') {
          isWAD = true;
        }
      }
      
      if (isWAD) {
        stream.read(b4);this.m_directoryCount=Helper.bytesToUInt(b4);
        stream.read(b4);this.m_directoryOffset=Helper.bytesToUInt(b4);
        
        stream.seek(this.m_directoryOffset);
        for(i=0;i<this.m_directoryCount;i++) {
          directory = new Directory();
          stream.read(b4);directory.offset=Helper.bytesToUInt(b4);
          stream.read(b4);directory.size=Helper.bytesToUInt(b4);
          
          stream.read(b8);
          directory.name="";
          j=0;
          while(j<8) {
            if (b8[j]!=0) {
              directory.name += (char)b8[j];
            } else {
              j=8;
            }
            j++;
          }
          
          this.m_directories.add(directory);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public Directory find(String name) {
    int i;
    Directory directory;
    for(i=0;i<this.getDirectories().size();i++) {
      directory = this.getDirectories().elementAt(i);
      if (directory.name.equals(name)) {
        return directory;
      }
    }
    return null;
  }
  
  public byte[] extract(Directory directory) {
    byte bin[];
    RandomAccessFile stream = this.m_stream;
    if (stream!=null) {
      try {
        stream.seek(directory.offset);
        bin = new byte[(int)directory.size];
        stream.read(bin);
        return bin;
      } catch (IOException e) {
        e.printStackTrace();
      }      
    }
    return null;
  }
  
  public void extract(Directory directory, String filename) {
    byte b[] = this.extract(directory);
    try {
      java.io.FileOutputStream stream = new FileOutputStream(new File(filename));
      stream.write(b);
      stream.close();      
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public BufferedImage flatToImage(Directory directory, byte[] palette) {
    int i,j,argb;    
    byte flat[] = this.extract(directory);
    BufferedImage image = new BufferedImage(64,64,BufferedImage.TYPE_INT_ARGB);
    for(j=0;j<64;j++) {
      for(i=0;i<64;i++) {
        argb  = 255<<24;
        argb |= (palette[(flat[j*64+i]&0xff)*3+0]&0xff)<<16;
        argb |= (palette[(flat[j*64+i]&0xff)*3+1]&0xff)<<8;
        argb |= (palette[(flat[j*64+i]&0xff)*3+2]&0xff);
        image.setRGB(i, j, argb);
      }
    }        
    return image;
  }
  
  public BufferedImage spriteToImage(Directory directory, byte[] palette) {
    RandomAccessFile stream = this.m_stream;
    byte b2[] = new byte[2];
    byte b4[] = new byte[4];
    int i,j,argb;
    int w,h;    
    int colx[];
    int rowStart;
    int pixelCount;
    int pixel;
    BufferedImage image;
    
    image = null;
    
    try {
      stream.seek(directory.offset);
      stream.read(b2);w=Helper.bytesToUInt(b2);
      stream.read(b2);h=Helper.bytesToUInt(b2);
      stream.read(b2);//left
      stream.read(b2);//top
      
      image = new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
      
      colx = new int[w];
      for(i=0;i<w;i++) {
        stream.read(b4);colx[i]=Helper.bytesToUInt(b4);
      }
      
      for(i=0;i<w;i++) {
        stream.seek(directory.offset+colx[i]);
        
        rowStart = 0;
        while(rowStart!=255) {
          rowStart = stream.readByte()&0xff;
          if (rowStart!=255) {
            pixelCount = stream.readByte()&0xff;
            stream.readByte();
            for(j=0;j<pixelCount;j++) {
              pixel = stream.readByte()&0xff;
              argb  = 255<<24;
              argb |= (palette[pixel*3+0]&0xff)<<16;
              argb |= (palette[pixel*3+1]&0xff)<<8;
              argb |= (palette[pixel*3+2]&0xff);
              image.setRGB(i, j+rowStart, argb);
            }          
            stream.readByte();
          }
        }
        
        rowStart = stream.readByte()&0xff;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
        
    return image;
  }
    
  public long getDirectoryCount() {return this.m_directoryCount;}
  public long getDirectoryOffset() {return this.m_directoryOffset;}
  public Vector<Directory> getDirectories(){return this.m_directories;}
  
  public class Directory {
    public long offset;
    public long size;
    public String name;
  }
  
  public static void main(String[] args) {
    int i;
    String filename = "data/doom/doom.wad";
    WAD wad;
    Directory directory;
    
    wad = new WAD();
    wad.load(filename);
    
    System.out.println("Directory Count  : " + wad.getDirectoryCount());
    System.out.println("Directory Offset : " + wad.getDirectoryOffset());
    for(i=0;i<wad.getDirectoryCount();i++) {
      directory = wad.getDirectories().elementAt(i);
      System.out.println( i + " : " + directory.name + " (" + directory.size + ", " + directory.offset + ")");
    }
    
    //*
    directory = wad.find("PLAYPAL");
    if (directory!=null) {
      byte playpal[] = wad.extract(directory);
      byte palette[] = new byte[768];
      for(i=0;i<palette.length;i++) {
        palette[i]=playpal[i];
      }
      
      /*
      directory = wad.find("FLAT8");
      if (directory!=null) {
        ImageViewer imageViewer = new ImageViewer();
        imageViewer.setImage(wad.flatToImage(directory, palette));
      }
      /**/
      
      directory = wad.find("CHGGA0");
      if (directory!=null) {
        ImageViewer imageViewer = new ImageViewer();
        imageViewer.setImage(wad.spriteToImage(directory, palette));
      }
    }
    /**/
    
    wad.close();
  }
}
