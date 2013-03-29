

import java.awt.Color;

import javax.media.opengl.GL2;

import controlP5.Group;
import controlP5.Tab;

import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;

public class LayerNoShape extends Layer {

	//used only for rendering :
	public int cc1R,cc1G,cc1B,cc2R,cc2G,cc2B,cc3R,cc3G,cc3B,cc4R,cc4G,cc4B;

	//noShape
	public FloatParameter c1H  = new FloatParameter(this,0,0,1,FloatParameter.WRAP,"c1H");
	public FloatParameter c1S  = new FloatParameter(this,0,0,1,"c1S");
	public FloatParameter c1B  = new FloatParameter(this,0,0,1,"c1B");
	public FloatParameter c1Alpha  = new FloatParameter(this,0,0,1,"c1Alpha");
	public FloatParameter c2H  = new FloatParameter(this,0,0,1,FloatParameter.WRAP,"c2H");
	public FloatParameter c2S  = new FloatParameter(this,0,0,1,"c2S");
	public FloatParameter c2B  = new FloatParameter(this,0,0,1,"c2B");
	public FloatParameter c2Alpha  = new FloatParameter(this,0,0,1,"c2Alpha");
	public FloatParameter c3H  = new FloatParameter(this,0,0,1,FloatParameter.WRAP,"c3H");
	public FloatParameter c3S  = new FloatParameter(this,0,0,1,"c3S");
	public FloatParameter c3B  = new FloatParameter(this,0,0,1,"c3B");
	public FloatParameter c3Alpha  = new FloatParameter(this,0,0,1,"c3Alpha");
	public FloatParameter c4H  = new FloatParameter(this,0,0,1,FloatParameter.WRAP,"c4H");
	public FloatParameter c4S  = new FloatParameter(this,0,0,1,"c4S");
	public FloatParameter c4B  = new FloatParameter(this,0,0,1,"c4B");
	public FloatParameter c4Alpha  = new FloatParameter(this,0,0,1,"c4Alpha");


	public LayerNoShape(Application parent) {
		super(parent);
		setParameterMap();
	}

	public void HSBtoRGB(){



		Color rgb = new Color(Color.HSBtoRGB(c1H.v(),c1S.v(),c1B.v()));
		cc1R = rgb.getRed();
		cc1G = rgb.getGreen();
		cc1B = rgb.getBlue();

		rgb = new Color(Color.HSBtoRGB(c2H.v(),c2S.v(),c2B.v()));
		cc2R = rgb.getRed();
		cc2G = rgb.getGreen();
		cc2B = rgb.getBlue();

		rgb = new Color(Color.HSBtoRGB(c3H.v(),c3S.v(),c3B.v()));
		cc3R = rgb.getRed();
		cc3G = rgb.getGreen();
		cc3B = rgb.getBlue();


		rgb = new Color(Color.HSBtoRGB(c4H.v(),c4S.v(),c4B.v()));
		cc4R = rgb.getRed();
		cc4G = rgb.getGreen();
		cc4B = rgb.getBlue();
	}

	@Override
	public void draw(PGraphicsOpenGL pgl){
		HSBtoRGB();
		PGL.gl.getGL2().getGL2().glBegin(GL2.GL_QUADS);

		PGL.gl.getGL2().glColor4f( cc1R/255f, cc1G/255f, cc1B/255f,c1Alpha.v());
		PGL.gl.getGL2().getGL2().glVertex2f(0,0);

		PGL.gl.getGL2().glColor4f( cc2R/255f, cc2G/255f, cc2B/255f,c2Alpha.v());
		PGL.gl.getGL2().getGL2().glVertex2f(Config.WIDTH,0);

		PGL.gl.getGL2().glColor4f( cc3R/255f, cc3G/255f, cc3B/255f,c3Alpha.v());
		PGL.gl.getGL2().getGL2().glVertex2f(Config.WIDTH,Config.HEIGHT);

		PGL.gl.getGL2().glColor4f( cc4R/255f, cc4G/255f, cc4B/255f,c4Alpha.v());
		PGL.gl.getGL2().getGL2().glVertex2f(0,Config.HEIGHT);

		PGL.gl.getGL2().getGL2().glEnd();
	}



	@Override
	public void drawBackground(PGraphicsOpenGL pgl){}

	@Override
	public int buildUI(GUIController controller, int x, int y, Tab tab, Group group) {
		String layerType=this.getType();
		y+=controller.addColorGroup(layerType,"Color 1","c1",x,y,tab,null);		
		y+=controller.addColorGroup(layerType,"Color 2","c2",x,y,tab,null);
		y+=controller.addColorGroup(layerType,"Color 3","c3",x,y,tab,null);
		y+=controller.addColorGroup(layerType,"Color 4","c4",x,y,tab,null);
		return(y);
	}

	public void keyEvent(int key, int keyCode, char ch, boolean a, boolean c, boolean s,boolean win,boolean apl,long[] lastPressedTime) {

		//super.keyEvent(key, keyCode, ch, a, c, s, win, apl, lastPressedTime);

	}

}






