package javaMGSRipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

public class DAR {
  private Vector<DAREntry> m_entries;
  
  // .DAR format
  //Header
  // 4byte Number of files
  
  // Entry
  // xbyte Filename (0 ending)
  // xbyte null padding (4byte alignment)
  // 4byte File length
  // xbyte File data
  // 1byte 0 terminator

  
  public DAR() {
    this.m_entries = new Vector<DAREntry>();
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
    int files = 0;
    int i;
    DAREntry entry;
    
    this.m_entries.clear();
    
    try {
      stream.seek(0);
      
      stream.read(b4);
      files = Helper.bytesToInt(b4);
      
      for(i=0;i<files;i++) {
        entry = new DAREntry();
        entry.read(stream);
        this.m_entries.add(entry);
      }
      
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public void extract(DAREntry entry) {
    if (entry!=null) {
      this.extract(entry, entry.getFilename());
    }
  }
  
  public void extract(DAREntry entry, String filename) {
    if (entry!=null) {
      java.io.FileOutputStream stream;
      try {
        stream = new FileOutputStream(new File(filename));
        stream.write(entry.getData());
        stream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
  
  public DAREntry find(String filename) {
    int i;
    for(i=0;i<this.getEntries().size();i++) {
      DAREntry entry = this.getEntries().elementAt(i);
      if (entry.getFilename().equals(filename)) {
        return entry;
      }
    }
    return null;
  }
  
  public Vector<DAREntry> getEntries(){return this.m_entries;}
  
  public static void main(String[] args) {
    int i;
    int extractOption = 0;
    String s;
    String filename;
    String filenameToExtract;
    String outputPath;
    
    //filename = "C:\\Downloads\\MetalGearSolid\\stage\\s00a\\stg_tex2.dar";
    filename = "";
    outputPath = ".";
    filenameToExtract = "";

    for(i=0;i<args.length;i++) {
      s = args[i];
      if (s.equals("-f")) {
        filename = args[++i];
      } else if (s.equals("-e")) {
        extractOption = 1;
        filenameToExtract = args[++i];
      } else if (s.equals("-E")) {
        extractOption = 2;
      } else if (s.equals("-outputpath")) {
        outputPath = args[++i];
      }
    }
    
    if (filename.length()>0) {
      DAR dar = new DAR();
      dar.load(filename);
      
      if (extractOption==1) {
        if (filenameToExtract.length()>0) {
          DAREntry entry = dar.find(filenameToExtract);
          if (entry!=null) {
            dar.extract(entry,outputPath + "\\" + entry.getFilename());
          }
        } else {
          System.out.println("Invalid argument(s)");
        }
      } else if (extractOption==2) {
        for(i=0;i<dar.getEntries().size();i++) {
          DAREntry entry = dar.getEntries().elementAt(i);
          dar.extract(entry,outputPath + "\\" + entry.getFilename());
        }
      } else {
        for(i=0;i<dar.getEntries().size();i++) {
          DAREntry entry = dar.getEntries().elementAt(i);
          System.out.println(entry.getFilename() + " (" + entry.getSize() + ")");
        }
        System.out.println(dar.getEntries().size() + " file(s)");
      }
    } else {
      System.out.println("MGS DAR Ripper");
      System.out.println("bASELaRTS 2020");
      System.out.println();
      System.out.println("-f <filename.dar> : opens filename.dar");
      System.out.println("-e <filename.pcx> : extracts filename.pcx from DAR");
      System.out.println("-E                : extracts all files");
      System.out.println();
    }
  }  
}