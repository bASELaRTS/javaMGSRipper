package javaMGSRipper;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TIM {
  private int m_tag;
  private int m_version;
  private int m_bpp;
  private boolean m_hasCLUT;
  private long m_imageOffset;
  private int m_paletteOrgX;
  private int m_paletteOrgY;
  private int m_paletteColors;
  private int m_numberOfPalettes;
  private int[] m_palette;
  
  private long m_imageSize;
  private int m_imageOrgX;
  private int m_imageOrgY;
  private int m_imageWidth;
  private int m_imageHeight;  
  
  private int m_data[];
  
  public TIM() {
  }
  
  public void load(String filename) {
    try {
      this.load(new java.io.RandomAccessFile(filename, "r"));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  public void load(java.io.RandomAccessFile stream) {
    int i,j,c;
    long l;
    byte[] b2 = new byte[2];
    byte[] b4 = new byte[4];
    
    try {
      stream.seek(0);

      this.m_tag = stream.readByte()&0xff;
      this.m_version = stream.readByte()&0xff;
      
      stream.readByte();
      stream.readByte();
      
      stream.read(b4);
      l = Helper.bytesToLong(b4);
      this.m_bpp = (int)(l&0x03); //00(0)=4bpp 01(1)=8bpp 10(2)=16bpp 11(3)=24bpp
      this.m_hasCLUT = ((l&8)==8);
      
      stream.read(b4);this.m_imageOffset = Helper.bytesToLong(b4);
      
      stream.read(b2);this.m_paletteOrgX = Helper.bytesToInt(b2);
      stream.read(b2);this.m_paletteOrgY = Helper.bytesToInt(b2);
      stream.read(b2);this.m_paletteColors = Helper.bytesToInt(b2);
      stream.read(b2);this.m_numberOfPalettes = Helper.bytesToInt(b2);
      
      if (this.m_hasCLUT) {
        this.m_palette = new int[this.m_paletteColors*this.m_numberOfPalettes];
        for(j=0;j<this.getNumberOfPalettes();j++) {
          for(i=0;i<this.getNumberOfColors();i++) {
            stream.read(b2);          
            this.m_palette[j*this.m_numberOfPalettes+i] = Helper.bytesToInt(b2); 
          }
        }
      }
      
      stream.read(b4);this.m_imageSize = Helper.bytesToLong(b4);
      stream.read(b2);this.m_imageOrgX = Helper.bytesToInt(b2);
      stream.read(b2);this.m_imageOrgY = Helper.bytesToInt(b2);
      
      stream.read(b2);this.m_imageWidth = Helper.bytesToInt(b2)*2;
      stream.read(b2);this.m_imageHeight = Helper.bytesToInt(b2);
      
      this.m_data = new int[this.getWidth()*this.getHeight()];
      for(j=0;j<this.getHeight();j++) {
        for(i=0;i<this.getWidth();i++) {
          c = stream.readByte()&0xff;
          this.m_data[j*this.getWidth()+i]=c;
        }
      }
      
      System.out.println(stream.getFilePointer());
      
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }  
  
  public void save(String filename) {
    int i,j;
    int c;
    BufferedImage image = new BufferedImage(this.getWidth(),this.getHeight(),BufferedImage.TYPE_INT_ARGB);
    for(j=0;j<this.getHeight();j++) {
      for(i=0;i<this.getWidth();i++) {
        c = this.getColor(2,this.m_data[j*this.getWidth()+i]);
        image.setRGB(i, j, c);
      }
    }
    try {
      ImageIO.write(image, "png", new File(filename));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public int getColor1(int paletteIndex, int colorIndex) {
    int color = this.m_palette[paletteIndex*this.getNumberOfPalettes()+colorIndex];
    int a,r,g,b,c;
    // color = A1B5G5R5;
    a = ((color&0x8000)>>15)*255;
    b = ((color&0x7c00)>>10)<<3;
    g = ((color&0x03e0)>>5)<<3;
    r = (color&0x1f)<<3;
    c = (a<<24)|(r<<16)|(g<<8)|b;
    return c;
  }
  
  public int getColor2(int paletteIndex, int colorIndex) {
    int color = this.m_palette[paletteIndex*this.getNumberOfPalettes()+colorIndex];
    int a,r,g,b,c;
    // color = A1B5G5R5;
    a = (color&0x01)*255;
    b = ((color>>1)&0x1f)<<3;
    g = ((color>>6)&0x1f)<<3;
    r = ((color>>11)&0x1f)<<3;
    c = (a<<24)|(r<<16)|(g<<8)|b;
    return c;
  }
  
  public int getColor(int paletteIndex, int colorIndex) {
    int color = this.m_palette[paletteIndex*this.getNumberOfPalettes()+colorIndex];
    int a,r,g,b,c;
    // color = A1B5G5R5;
    a = ((color>>15)&0x01)*255;
    b = ((color>>10)&0x1f)<<3;
    g = ((color>>5)&0x1f)<<3;
    r = ((color)&0x1f)<<3;
    c = (a<<24)|(r<<16)|(g<<8)|b;
    return c;
  }

  public int getTag() {return this.m_tag;}
  public int getVersion() {return this.m_version;}
  public int getBPP() {return this.m_bpp;}
  public boolean getHasPalette() {return this.m_hasCLUT;}
  public long getImageOffset() {return this.m_imageOffset;}
  public int getPaletteOrgX() {return this.m_paletteOrgX;}
  public int getPaletteOrgY() {return this.m_paletteOrgY;}
  public int getNumberOfColors() {return this.m_paletteColors;}
  public int getNumberOfPalettes() {return this.m_numberOfPalettes;}
  public long getImageSize() {return this.m_imageSize;}
  public int getImageOrgX() {return this.m_imageOrgX;}
  public int getImageOrgY() {return this.m_imageOrgY;}
  public int getWidth() {return this.m_imageWidth;}
  public int getHeight() {return this.m_imageHeight;}
      
  public static void main(String[] args) {
    TIM tim = new TIM();
    tim.load("G:\\Pl0\\emd0\\EM049.TIM");
    tim.save("data\\EM049.png");
    System.out.println("[Header]");
    System.out.println("Tag              : " + tim.getTag());
    System.out.println("Version          : " + tim.getVersion());
    System.out.println("BPPCode          : " + tim.getBPP());
    System.out.println("HasPalette       : " + tim.getHasPalette());    
    System.out.println("ImageOffset      : " + tim.getImageOffset());
    System.out.println("PaletteOrgX      : " + tim.getPaletteOrgX());
    System.out.println("PaletteOrgY      : " + tim.getPaletteOrgY());
    System.out.println("NumberOfColors   : " + tim.getNumberOfColors());
    System.out.println("NumberOfPalettes : " + tim.getNumberOfPalettes());    
    System.out.println("ImageSize        : " + tim.getImageSize());    
    System.out.println("ImageOrgX        : " + tim.getImageOrgX());    
    System.out.println("ImageOrgY        : " + tim.getImageOrgY());    
    System.out.println("Width            : " + tim.getWidth());    
    System.out.println("Height           : " + tim.getHeight());  
    
    int i,j,c;
    if (tim.getHasPalette()) {
      for(j=0;j<tim.getNumberOfPalettes();j++) {
        System.out.println("[Palette" + j + "]");
        for(i=0;i<tim.getNumberOfColors();i++) {
          c = tim.getColor(j, i);
          System.out.println(i + " : " + c);
        }
      }
    }
  }
}
