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
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class OBJViewer extends JFrame implements TreeSelectionListener {
  private static final long serialVersionUID = 1L;

  private OBJ m_object;
  
  private FacePanel m_pnlFace;
  
  private JSplitPane m_splitPane;
  private JScrollPane m_scrollPane;
  private JTree m_tree;
  private DefaultMutableTreeNode m_nodeVertices;
  private DefaultMutableTreeNode m_nodeNormals;
  private DefaultMutableTreeNode m_nodeUVs;
  private DefaultMutableTreeNode m_nodeFaces;
  
  public OBJViewer() {
    super();
        
    this.m_pnlFace = new FacePanel();
    
    DefaultMutableTreeNode root;
    
    root = new DefaultMutableTreeNode("OBJ");    

    this.m_nodeVertices = new DefaultMutableTreeNode("Vertices");
    root.add(this.m_nodeVertices);
    
    this.m_nodeNormals = new DefaultMutableTreeNode("Normals");
    root.add(this.m_nodeNormals);
    
    this.m_nodeUVs = new DefaultMutableTreeNode("UV");
    root.add(this.m_nodeUVs);
    
    this.m_nodeFaces = new DefaultMutableTreeNode("Faces");
    root.add(this.m_nodeFaces);
    
    this.m_tree = new JTree(root);    
    this.m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
    this.m_tree.addTreeSelectionListener(this);
    this.m_scrollPane = new JScrollPane(this.m_tree);
    
    this.m_splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    this.m_splitPane.setLeftComponent(this.m_scrollPane);
    this.m_splitPane.setRightComponent(this.m_pnlFace);
    
    OBJ object = new OBJ();
    object.load("25.obj");
    this.loadObject(object);

    BufferedImage texture = null;
    try {
      texture = ImageIO.read(new File("rif_s2msk.png"));
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.setTexture(texture);
    
    this.setTitle("OBJViewer");
    this.setLayout(new BorderLayout());    
    //this.add(this.m_pnlFace,BorderLayout.CENTER);
    this.add(this.m_splitPane,BorderLayout.CENTER);
    this.pack();
    this.setVisible(true);
  }
  
  private void loadObject(OBJ object) {
    int i;
    Vector2 v2;
    Vector3 v3;
    OBJ.Face face;
    DefaultMutableTreeNode node;
    
    this.m_nodeVertices.removeAllChildren();
    for(i=0;i<object.getVertices().size();i++) {
      v3 = object.getVertices().elementAt(i);
      node = new DefaultMutableTreeNode(new TreeNodeObject(Integer.toString(i) + " " + v3.toString(),i));
      this.m_nodeVertices.add(node);
    }
    
    this.m_nodeNormals.removeAllChildren();
    for(i=0;i<object.getNormals().size();i++) {
      v3 = object.getNormals().elementAt(i);
      node = new DefaultMutableTreeNode(new TreeNodeObject(Integer.toString(i) + " " + v3.toString(),i));
      this.m_nodeNormals.add(node);
    }
    
    this.m_nodeUVs.removeAllChildren();
    for(i=0;i<object.getUVs().size();i++) {
      v2 = object.getUVs().elementAt(i);
      node = new DefaultMutableTreeNode(new TreeNodeObject(Integer.toString(i) + " " + v2.toString(),i));
      this.m_nodeUVs.add(node);
    }

    this.m_nodeFaces.removeAllChildren();
    for(i=0;i<object.getFaces().size();i++) {
      face = object.getFaces().elementAt(i);
      node = new DefaultMutableTreeNode(new TreeNodeObject(Integer.toString(i) + " (" + face.toString() + ")",i));
      this.m_nodeFaces.add(node);
    }  
    
    this.m_pnlFace.setSelectedFaceIndex(-1);
    this.m_pnlFace.setZoom(3.0);
    this.m_pnlFace.setObject(object);
    this.setObject(object);    
  }
  
  public void valueChanged(TreeSelectionEvent arg0) {
    DefaultMutableTreeNode node;
    DefaultMutableTreeNode parent;
    TreeNodeObject tno;
    node = (DefaultMutableTreeNode)this.m_tree.getLastSelectedPathComponent();
    if (node!=null) {      
      parent = (DefaultMutableTreeNode)node.getParent();
      if (node.isLeaf() && (parent!=null) && (parent.getUserObject().equals("Faces"))) {
        tno = (TreeNodeObject)node.getUserObject();
        this.m_pnlFace.setSelectedFaceIndex((int)tno.getObject());
      }
    }
  }
  
  public void setObject(OBJ object) {this.m_object = object;}
  public OBJ getObject() {return this.m_object;}  
  public void setTexture(BufferedImage image) {this.m_pnlFace.setTexture(image);}
  
  public static void main(String[] args) {
    new OBJViewer();
  }
  
  public class TreeNodeObject {
    private String m_caption;
    private Object m_object;
    
    public TreeNodeObject() {
      this.setObject(null);
      this.setCaption("");
    }
    
    public TreeNodeObject(String caption, Object object) {
      this.setCaption(caption);
      this.setObject(object);
    }
    
    public String toString() {return this.getCaption();}
    
    public void setCaption(String s) {this.m_caption=s;}
    public String getCaption() {return this.m_caption;}
    public void setObject(Object o) {this.m_object=o;}
    public Object getObject() {return this.m_object;}
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
          y = (int)((oh - (v2.y*oh)) + oy);          
          g.fillRect(x,y,2,2);
        }
        
        g.setColor(Color.lightGray);
        for(j=0;j<this.getObject().getFaces().size();j++) {
          face = this.getObject().getFaces().elementAt(j);
          for(i=0;i<face.getElements().size();i++) {
            element = face.getElements().elementAt(i);            
            v2 = this.getObject().getUVs().elementAt(element.getUVIndex()-1);
            x = (int)((v2.x*ow) + ox);
            y = (int)((oh - (v2.y*oh)) + oy);     
            
            index = (i+1)%face.getElements().size();
            element = face.getElements().elementAt(index);
            v2 = this.getObject().getUVs().elementAt(element.getUVIndex()-1);
            x2 = (int)((v2.x*ow) + ox);
            y2 = (int)((oh - (v2.y*oh)) + oy);
            
            g.drawLine(x,y,x2,y2);            
          }
        }
        
        if (this.getSelectedFaceIndex()>=0) {
          g.setColor(Color.red);
          face = this.getObject().getFaces().elementAt(this.getSelectedFaceIndex());
          for(i=0;i<face.getElements().size();i++) {
            element = face.getElements().elementAt(i);            
            v2 = this.getObject().getUVs().elementAt(element.getUVIndex()-1);
            x = (int)((v2.x*ow) + ox);
            y = (int)((oh - (v2.y*oh)) + oy);     
            
            index = (i+1)%face.getElements().size();
            element = face.getElements().elementAt(index);
            v2 = this.getObject().getUVs().elementAt(element.getUVIndex()-1);
            x2 = (int)((v2.x*ow) + ox);
            y2 = (int)((oh - (v2.y*oh)) + oy);
            
            g.drawLine(x,y,x2,y2);            
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
