

import processing.core.PApplet;
import processing.opengl.PJOGL;
import processing.opengl.PGraphicsOpenGL;
import processing.opengl.Texture;

import java.awt.Frame;
import java.awt.GraphicsDevice;


import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilitiesChooser;
import javax.media.opengl.GLCapabilitiesImmutable;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;

/**
 * An undecorated lightweight AWT window to show just a single texture created
 * in the main OpenGL context of the parent PApplet. The renderer in parent must
 * be of type OPENGL2.
 */
public class GLTextureWindow {
  protected PApplet parent;
  protected String name;
  protected boolean hasBorder;
  protected boolean resizable;
  protected MyGLRenderer renderer;
  protected GLCanvas canvas;
  protected GLContext context;
  protected GLCapabilitiesImmutable caps;
  protected Frame frame;
  protected Texture outTex;
  protected PGraphicsOpenGL pgl;  
  
  protected boolean visible;
  protected int x0, y0;
  protected int width, height;
  protected boolean override;
  protected boolean initialized;
  
  
  /**
   * Creates a visible instance of GLTextureWindow with the specified size (w, h) and
   * position (x, y), which will show texture tex in it.
   * 
   * @param parent
   *          PApplet
   * @param tex
   *          PTexture
   * @param x
   *          int
   * @param y
   *          int
   * @param w
   *          int
   * @param h
   *          int
   */  
  public GLTextureWindow(PApplet parent, int x, int y, int w, int h) {
    this(parent, x, y, w, h, true);
  }
  
  public GLTextureWindow(PApplet parent, int x, int y, int w, int h, boolean v) {
    this(parent, "texture window", x, y, w, h, v, false, false);
  }

  public GLTextureWindow(PApplet parent, int x, int y, int w, int h, boolean v, boolean b) {
    this(parent, "texture window", x, y, w, h, v, b, false);
  }
  
  /**
   * Creates an instance of GLTextureWindow with the specified size (w, h) and
   * position (x, y), which will show texture tex in it.
   * 
   * @param parent
   *          PApplet
   * @param tex
   *          PTexture
   * @param name
   *          String          
   * @param x
   *          int
   * @param y
   *          int
   * @param w
   *          int
   * @param h
   *          int
   * @param v
   *          boolean
   * @param d
   *          boolean    
   * @param r
   *          boolean          
   */
  public GLTextureWindow(PApplet parent, String windowTitle, int x, int y, int w, int h, 
                         boolean v, boolean b, boolean r) {
    this.parent = parent;
    pgl = (PGraphicsOpenGL) parent.g;

    name = windowTitle;
    x0 = x;
    y0 = y;
    width = w;
    height = h;
    visible = v;
    hasBorder = b;
    resizable = r;
    initialized = false;
    override = false;
    outTex = null;   
    //pgl.pgl.addWindow(this);
  }
  
  public boolean init() {
    if (!initialized) {
      // The GL canvas of the AWT window must have exactly the same GL capabilities
      // as the main renderer for sharing with the main context to be possible.      
      context = PJOGL.gl.getGL2().getContext(); 
      //caps = pgl.pgl.gl.getGL().
      
      initImpl(x0, y0, width, height);   
      //Image image = Toolkit.getDefaultToolkit().createImage(PApplet.ICON_IMAGE);
      //frame.setIconImage(image);
      
      frame.setVisible(visible);
      frame.addKeyListener(this.parent);
      initialized = true; 
      return(true);
      
    } else if (context != PJOGL.gl.getContext()) {
      // The window has been initialized, but the main context in the      
      // renderer has changed. The frame, canvas, and renderer are
      // recreated.
      
      // Getting current context and capabilities of the main renderer.
      context = PJOGL.gl.getContext();  
      //caps = pgl.pgl.gl.capabilities;
      
      // Getting current parameters of the frame.
      boolean v = frame.isVisible();
      int x = frame.getX();
      int y = frame.getY();
      int w = frame.getWidth();
      int h = frame.getHeight();

      frame.setVisible(false);
      initImpl(x, y, w, h);
      frame.setVisible(v);
      
      return(false);
    }
    
    return(false);
  }

  
  protected void initImpl(int x, int y, int w, int h) {
    frame = new Frame(name);
    frame.setSize(w, h);
    frame.setLocation(x, y);
    if (!hasBorder) {
      frame.setUndecorated(true);        
    } else {
      if (!resizable) {
        frame.setResizable(false);
      }
    }      
    canvas = new GLCanvas(null,(GLCapabilitiesChooser) null, context,(GraphicsDevice) null);      
    renderer = new MyGLRenderer();
    canvas.addGLEventListener(renderer);
    frame.add(canvas);
  }
 
  /**
   * Sets the texture reference
   * @param tex
   */
  public void setTexture(Texture tex) {
    outTex = tex;
  }

  /**
   * Sets the override property to the desired value.
   * @param override
   *          boolean
   */
  public void setOverride(boolean override) {
    this.override = override;
  }

  /**
   * Shows the window.
   */
  public void show() {
    frame.setVisible(true);
  }
  
  /**
   * Hides the window.
   */
  public void hide() {
    frame.setVisible(false);
  }

  /**
   * Returns whether the window is visible or not.
   * @return boolean
   */
  public boolean isVisible() {
    return frame.isVisible();
  }

  /**
   * Sets the texture tint color.
   * 
   * @param int color
   */
  public void tint(int color) {
    int ir, ig, ib, ia;

    ia = (color >> 24) & 0xff;
    ir = (color >> 16) & 0xff;
    ig = (color >> 8) & 0xff;
    ib = color & 0xff;

    renderer.a = ia / 255.0f;
    renderer.r = ir / 255.0f;
    renderer.g = ig / 255.0f;
    renderer.b = ib / 255.0f;
  }

  /**
   * Returns true or false depending on whether or not the internal renderer has
   * been initialized.
   * @return boolean
   */
  public boolean ready() {
    return initialized && frame != null && renderer != null && renderer.initalized;
  }

  /**
   * Draws the window, if the renderer has been initialized.
   */
  public void render() {
    if (ready()) {
      renderer.started = true;
      canvas.display();
    }
  }

  /**
   * Sets the window location.
   * 
   * @param x
   *          int
   * @param y
   *          int          
   */  
  public void setLocation(int x, int y) {
    frame.setLocation(x, y);
  }
  
  protected class MyGLRenderer implements GLEventListener {
    public MyGLRenderer() {
      super();
      initalized = false;
      started = false;
      r = g = b = a = 1.0f;
    }

    public void init(GLAutoDrawable drawable) {
      gl = drawable.getGL().getGL2();
      context = drawable.getContext();

      gl.glClearColor(0, 0, 0, 0);
      initalized = true;
    }

    public void display(GLAutoDrawable drawable) {
      if (!initalized || !started || (outTex == null))
        return;

      int w = drawable.getWidth();
      int h = drawable.getHeight();

      gl = drawable.getGL().getGL2();
      context = drawable.getContext();
      detainContext();

      // Setting orthographics view to display the texture.
      gl.glViewport(0, 0, w, h);
      gl.getGL2().glMatrixMode(GL2.GL_PROJECTION);
      gl.getGL2ES1().glLoadIdentity();
      gl.getGL2ES1().glOrtho(0.0, w, 0.0, h, -100.0, +100.0);
      gl.getGL2().glMatrixMode(GL2.GL_MODELVIEW);
      gl.getGL2ES1().glLoadIdentity();

      float uscale = outTex.maxTexcoordU();
      float vscale = outTex.maxTexcoordV();

      float cx = 0.0f;
      float sx = +1.0f;
      if (outTex.invertedX()) {
        cx = 1.0f;
        sx = -1.0f;
      }

      float cy = 0.0f;
      float sy = +1.0f;
      if (outTex.invertedY()) {
        cy = 1.0f;
        sy = -1.0f;
      }

      gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
      gl.glEnable(outTex.glTarget);
      gl.glActiveTexture(GL2.GL_TEXTURE0);
      gl.glBindTexture(outTex.glTarget, outTex.glName);
      gl.getGL2ES1().glColor4f(r, g, b, a);
      gl.getGL2().glBegin(GL2.GL_QUADS);
      gl.getGL2().glTexCoord2f((cx + sx * 0.0f) * uscale, (cy + sy * 1.0f) * vscale);
      gl.getGL2().glVertex2f(0.0f, 0.0f);

      gl.getGL2().glTexCoord2f((cx + sx * 1.0f) * uscale, (cy + sy * 1.0f) * vscale);
      gl.getGL2().glVertex2f(w, 0.0f);

      gl.getGL2().glTexCoord2f((cx + sx * 1.0f) * uscale, (cy + sy * 0.0f) * vscale);
      gl.getGL2().glVertex2f(w, h);

      gl.getGL2().glTexCoord2f((cx + sx * 0.0f) * uscale, (cy + sy * 0.0f) * vscale);
      gl.getGL2().glVertex2f(0.0f, h);
      gl.getGL2().glEnd();
      gl.glBindTexture(outTex.glTarget, 0);

      gl.glFlush();

      releaseContext();
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width,
        int height) {
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
        boolean deviceChanged) {
    }

    protected void detainContext() {
      try {
        while (context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
          Thread.sleep(10);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    protected void releaseContext() {
      context.release();
    }

    float r, g, b, a;
    GL2 gl;
    GLContext context;
    boolean initalized;
    boolean started;
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
  }
  
  /**
  * Returns the value of the override variable, used to skip automatic
  * rendering in the GLGraphics renderer.
  * 
  * @return boolean
  */
 public boolean getOverride() {
   return override;
 }
 
 /**
  * Returns the value of the initialized variable.
  * 
  * @return boolean
  */  
 public boolean isInitialized() {
   return initialized;
 }
 
}
