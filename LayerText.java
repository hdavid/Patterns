import processing.core.PFont;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;

import javax.media.opengl.GL2;

import controlP5.Group;
import controlP5.Tab;

public class LayerText extends LayerMovement {

	//OPENGL
	//private PGraphicsOpenGL pgl=null;
	private static PFont font = null;
	private static int size=0;
	private String text=null;
	
	public LayerText(Application parent) {
		super(parent);
		setParameterMap();
	}
	
	public void draw(PGraphicsOpenGL pgl) {
		if(text!=null&&fgAlpha.v()>0){

			parent.pushMatrix();
			parent.translate(Config.WIDTH/2,Config.HEIGHT/2);
			parent.scale(zoom.v());
			parent.rotate(angle.v());
			parent.translate(-Config.WIDTH,-Config.HEIGHT);
			parent.translate(scrollX.v()*2,scrollY.v()*2);

			if(LayerText.font==null||LayerText.size!=Config.WIDTH *Config.HEIGHT){
				LayerText.font=parent.loadFont(Config.FONT);
				//resizing the window break the font :/
				LayerText.size=Config.WIDTH *Config.HEIGHT;
			}
			if(LayerText.font!=null){
				int fontsize=30;
				parent.textFont(LayerText.font,fontsize);
				PGL.gl.getGL2().glClear(GL2.GL_DEPTH_BUFFER_BIT);
				parent.fill( foregroundR, foregroundG, foregroundB,fgAlpha.v()*255);
				parent.text(text,Config.WIDTH-parent.textWidth(text)/2,Config.HEIGHT+fontsize/2);
			}
			parent.popMatrix();
			/*parent.translate(-scrollX.v()*2,-scrollY.v()*2);
			parent.translate(Config.WIDTH,Config.HEIGHT);
			parent.rotate(-angle.v());
			parent.scale(1/zoom.v());
			parent.translate(-Config.WIDTH,-Config.HEIGHT);*/
		}
	}


	@Override
	public int buildUI(GUIController controller,int x, int y, Tab tab, Group group) {
		return(0);
	}

	@Override
	public void drawBackground(PGraphicsOpenGL pgl) {
		
	}

	@Override
	public void update() {
		super.update();
	}

}






