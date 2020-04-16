package javaMGSRipper;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class OBJViewer extends JFrame {
  private static final long serialVersionUID = 1L;

  private OBJ m_object;
  
  private FacePanel m_pnlFace;
  
  public OBJViewer() {
    super();
        
    this.m_pnlFace = new FacePanel();
    
    OBJ object = new OBJ();
    object.load("25.obj");

    BufferedImage texture = null;
    try {
      texture = ImageIO.read(new File("rif_s2msk.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.m_pnlFace.setSelectedFaceIndex(6);
    this.m_pnlFace.setZoom(3.0);
    this.setTexture(texture);
    this.setObject(object);
    
    this.setTitle("OBJViewer");
    this.setLayout(new BorderLayout());    
    this.add(this.m_pnlFace,BorderLayout.CENTER);        
    this.pack();
    this.setVisible(true);
  }
    
  public void setObject(OBJ object) {
    this.m_object = object;
    this.m_pnlFace.setObject(this.m_object);
  }
  public OBJ getObject() {return this.m_object;}
  
  public void setTexture(BufferedImage image) {this.m_pnlFace.setTexture(image);}
  
  public static void main(String[] args) {
    new OBJViewer();
  }
  
  public class FacePanel extends JPanel implements KeyListener {
    private static final long serialVersionUID = 1L;

    private BufferedImage m_texture;
    private OBJ m_object;
    private int m_selectedFaceIndex;
    private double m_zoom;
    
    public FacePanel() {
      super();      
      
      this.setFocusable(true);
      this.addKeyListener(this);
      
      this.setObject(null);
      this.setSelectedFaceIndex(-1);
      this.setZoom(1.0);
      this.setPreferredSize(new Dimension(640,480));
    }
    
    public void paint(Graphics g) {
      super.paint(g);
      
      int i,j;
      int x,y;
      int w,h;
      int x2,y2;
      int ox,oy;
      int ow,oh;
      int index;
      Vector2 v2;
      OBJ.Face face;
      OBJ.FaceElement element;
      
      x = 0;
      y = 0;
      w = this.getWidth();
      h = this.getHeight();
      ow = 256;
      oh = 256;
      ox = (int)((w-ow)*0.5);
      oy = (int)((h-oh)*0.5);
      
      g.setColor(Color.black);
      g.fillRect(x, y, w, h);
      
      if (this.getTexture()!=null) {
        ow = (int)(this.getTexture().getWidth()*this.getZoom());
        oh = (int)(this.getTexture().getHeight()*this.getZoom());
        ox = (int)((w-ow)*0.5);
        oy = (int)((h-oh)*0.5);
        g.drawImage(this.getTexture(), ox, oy, ow, oh, null);
      }
      
      if (this.getObject()!=null) {
        g.setColor(Color.white);
        for(i=0;i<this.getObject().getUVs().size();i++) {
          v2 = this.getObject().getUVs().elementAt(i);
          x = (int)((v2.x*ow) + ox);
          y = (int)((v2.y*oh) + oy);          
          g.fillRect(x, y, 2, 2);
        }
        
        g.setColor(Color.lightGray);
        for(j=0;j<this.getObject().getFaces().size();j++) {
          face = this.getObject().getFaces().elementAt(j);
          for(i=0;i<face.getElements().size();i++) {
            element = face.getElements().elementAt(i);            
            v2 = this.getObject().getUVs().elementAt(element.getUVIndex()-1);
            x = (int)((v2.x*ow) + ox);
            y = (int)((v2.y*oh) + oy);     
            
            index = (i+1)%face.getElements().size();
            element = face.getElements().elementAt(index);
            v2 = this.getObject().getUVs().elementAt(element.getUVIndex()-1);
            x2 = (int)((v2.x*ow) + ox);
            y2 = (int)((v2.y*oh) + oy);
            
            g.drawLine(x, y, x2, y2);            
          }
        }
        
        if (this.getSelectedFaceIndex()>=0) {
          g.setColor(Color.red);
          face = this.getObject().getFaces().elementAt(this.getSelectedFaceIndex());
          for(i=0;i<face.getElements().size();i++) {
            element = face.getElements().elementAt(i);            
            v2 = this.getObject().getUVs().elementAt(element.getUVIndex()-1);
            x = (int)((v2.x*ow) + ox);
            y = (int)((v2.y*oh) + oy);     
            
            index = (i+1)%face.getElements().size();
            element = face.getElements().elementAt(index);
            v2 = this.getObject().getUVs().elementAt(element.getUVIndex()-1);
            x2 = (int)((v2.x*ow) + ox);
            y2 = (int)((v2.y*oh) + oy);
            
            g.drawLine(x, y, x2, y2);            
          }        
        }
      }
    }
    
    public void keyTyped(KeyEvent e) {
    }
    public void keyPressed(KeyEvent e) {
      if (e.getKeyCode()==KeyEvent.VK_F) {
        int index = this.getSelectedFaceIndex();
        index = (index+1)%this.m_object.getFaces().size();
        this.setSelectedFaceIndex(index);
      }
    }
    public void keyReleased(KeyEvent e) {
    }

    public void setObject(OBJ object) {
      this.m_object = object;
      this.repaint();
    }
    public OBJ getObject() {return this.m_object;}
    
    public void setTexture(BufferedImage image) {
      this.m_texture = image;
      this.repaint();
    }
    public BufferedImage getTexture() {return this.m_texture;}
    
    public void setSelectedFaceIndex(int i) {
      this.m_selectedFaceIndex = i;
      this.repaint();
    }
    public int getSelectedFaceIndex() {return this.m_selectedFaceIndex;}
    public void setZoom(double d) {
      this.m_zoom = d;
      this.repaint();
    }
    public double getZoom() {return this.m_zoom;}
  }
}
