package javaMGSRipper;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class ImageViewer extends JFrame {
  private static final long serialVersionUID = 1L;
  private ImageViewerPanel m_panel;
  
  public ImageViewer() {
    super();
    
    this.m_panel = new ImageViewerPanel(); 
    
    this.setTitle("ImageViewer");
    this.setDefaultCloseOperation(EXIT_ON_CLOSE);
    this.setLayout(new BorderLayout());
    this.add(this.m_panel,BorderLayout.CENTER);
    this.pack();
    this.setLocationRelativeTo(null);
    this.setVisible(true);
  }
  
  public void setImage(BufferedImage image) {
    this.m_panel.setImage(image);
  }
  
  public class ImageViewerPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private BufferedImage m_image;
    
    public ImageViewerPanel() {
      super();
      this.setPreferredSize(new Dimension(640,480));      
    }
    
    public void paint(Graphics g) {
      super.paint(g);
      BufferedImage image = this.getImage();
      if (image!=null) {
        g.drawImage(image, 0,0, image.getWidth(),image.getHeight(),null);
      }
    }
    
    public void setImage(BufferedImage image) {      
      this.m_image = image;
      this.setPreferredSize(new Dimension(this.getImage().getWidth(),this.getImage().getHeight()));
      this.repaint();
    }
    public BufferedImage getImage() {return this.m_image;}
  }
}
