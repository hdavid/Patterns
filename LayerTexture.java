import javax.media.opengl.GL2;

import controlP5.Group;
import controlP5.Tab;

import processing.core.PApplet;
import processing.core.PImage;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;

public class LayerTexture extends LayerMovement {

	private PImage texture;
	public float gradients[];
	
	public LayerTexture(Application parent){
		super(parent);
	     gradients = new float[4];
	     gradients[0]=0.0f;
	     gradients[1]=1.f;
	     gradients[2]=1.0f;
	     gradients[3]=1f;
	     setParameterMap();
	}

	public void set(PGraphicsOpenGL pgl){
		texture = parent.loadImage("VK.gif");
		texture.mask(parent.loadImage("VK.gif"));
	}


	@Override
	public void draw(PGraphicsOpenGL pgl) {
		//transform
		PGL.gl.getGL2().glPushMatrix();
		PGL.gl.getGL2().glTranslatef(Config.WIDTH/2,Config.HEIGHT/2,0);
		PGL.gl.getGL2().glScalef(zoom.v(),zoom.v(),0);
		PGL.gl.getGL2().glRotatef(angle.v()/(2*(float)Math.PI)*360,0,0,1);
		//PGL.gl.getGL2().glTranslatef(-width/2,-Config.HEIGHT/2,0);  
		PGL.gl.getGL2().glTranslatef(scrollX.v(),scrollY.v(),0);

		fillAll();
		PGL.gl.getGL2().glPopMatrix();

	}



	private void fillAll(){
		parent.color(foregroundR/255f, foregroundG/255f, foregroundB/255f);
		int range = 2+(int)(
				(Math.min(width,height)*zoom.v()+Math.sqrt((float)Config.WIDTH*Config.WIDTH+Config.HEIGHT*Config.HEIGHT))
				/
				(Math.min(width,height)*zoom.v())
				/ 2
				);

		for(int x=0;x<=range;x++){
			int xOffset=width*x;
			for(int y=0;y<=range;y++){        
				int yOffset=height*y;

				paint(xOffset,yOffset);
				if(x!=0){
					paint(-xOffset,yOffset);
				}
				if(y!=0){
					paint(xOffset,-yOffset);
				}
				if(x!=0&&y!=0){
					paint(-xOffset,-yOffset);
				}
			}
		}
	}


	public void paintBackground(){
	}

	private void paint(float x,float y){
		//PGL.gl.getGL2().glPushMatrix();
		parent.translate(x,y);  
		if(texture!=null ) {
			parent.beginShape(PApplet.QUADS);
			parent.texture(texture);
			parent.vertex(x, y,0f,0f);
			parent.vertex(x+width, y,texture.width,0f);
			parent.vertex(x+width,y+height,texture.width,texture.height);
			parent.vertex(x,y+height,0f, texture.height);
			parent.endShape(); 
		}else{
			System.out.println("pattern texture is null");
		}
		parent.translate(-x,-y);

	}


	float ratio=0.5f;;


	@SuppressWarnings("unused")
	private void tri( int x1,int y1,int x2,int y2,int x3,int y3){
		float ratio=1; 
		PGL.gl.getGL2().getGL2().glBegin(GL2.GL_TRIANGLES);
		ratio=gradients[0]*gradient.v();
		PGL.gl.getGL2().glColor4f(
				(1-ratio)*foregroundR/255f +(ratio)*backgroundR/255f 
				,    (1-ratio)*foregroundG/255f +(ratio)*backgroundG/255f,
				(1-ratio)*foregroundB/255f +(ratio)*backgroundB/255f
				,fgAlpha.v()
				);
		PGL.gl.getGL2().getGL2().glVertex2f(x1,y1);

		ratio=gradients[1]*gradient.v();
		PGL.gl.getGL2().glColor4f(
				(1-ratio)*(foregroundR)/255.0f +(ratio)*(backgroundR)/255.0f 
				,    (1-ratio)*(foregroundG)/255.0f +(ratio)*(backgroundG)/255.0f ,
				(1-ratio)*(foregroundB)/255.0f +(ratio)*(backgroundB)/255.0f
				,fgAlpha.v()
				);       
		PGL.gl.getGL2().getGL2().glVertex2f(x2,y2);

		ratio=gradients[2]*gradient.v(); 
		PGL.gl.getGL2().glColor4f(
				(1-ratio)*(foregroundR)/255.0f +(ratio)*(backgroundR)/255.0f 
				,    (1-ratio)*(foregroundG)/255.0f +(ratio)*(backgroundG)/255.0f ,
				(1-ratio)*(foregroundB)/255.0f +(ratio)*(backgroundB)/255.0f
				,fgAlpha.v()
				);       
		PGL.gl.getGL2().getGL2().glVertex2f(x3,y3);

		PGL.gl.getGL2().getGL2().glEnd();
	}

	@SuppressWarnings("unused")
	private void quadri(float x1, float y1,float x2,float y2,float x3,float y3,float x4,float y4){
		float ratio=1;

		PGL.gl.getGL2().getGL2().glBegin(GL2.GL_QUADS);
		//parent.texture(parent.textures[1]);

		ratio=gradients[0]*gradient.v(); 
		PGL.gl.getGL2().glColor4f(
				(1-ratio)*(foregroundR)/255.0f +(ratio)*(backgroundR)/255.0f 
				,    (1-ratio)*(foregroundG)/255.0f +(ratio)*(backgroundG)/255.0f ,
				(1-ratio)*(foregroundB)/255.0f +(ratio)*(backgroundB)/255.0f
				,fgAlpha.v()
				);
		PGL.gl.getGL2().getGL2().glVertex2f(x1,y1);

		ratio=gradients[1]*gradient.v(); 
		PGL.gl.getGL2().glColor4f(
				(1-ratio)*(foregroundR)/255.0f +(ratio)*(backgroundR)/255.0f 
				,    (1-ratio)*(foregroundG)/255.0f +(ratio)*(backgroundG)/255.0f ,
				(1-ratio)*(foregroundB)/255.0f +(ratio)*(backgroundB)/255.0f
				,fgAlpha.v()
				);       
		PGL.gl.getGL2().getGL2().glVertex2f(x2,y2);

		ratio=gradients[2]*gradient.v(); 
		PGL.gl.getGL2().glColor4f(
				(1-ratio)*(foregroundR)/255.0f +(ratio)*(backgroundR)/255.0f 
				,    (1-ratio)*(foregroundG)/255.0f +(ratio)*(backgroundG)/255.0f ,
				(1-ratio)*(foregroundB)/255.0f +(ratio)*(backgroundB)/255.0f
				,fgAlpha.v()
				);       
		PGL.gl.getGL2().getGL2().glVertex2f(x3,y3);

		ratio=gradients[3]*gradient.v(); 
		PGL.gl.getGL2().glColor4f(
				(1-ratio)*(foregroundR)/255.0f +(ratio)*(backgroundR)/255.0f 
				,    (1-ratio)*(foregroundG)/255.0f +(ratio)*(backgroundG)/255.0f ,
				(1-ratio)*(foregroundB)/255.0f +(ratio)*(backgroundB)/255.0f
				,fgAlpha.v()
				);       
		PGL.gl.getGL2().getGL2().glVertex2f(x4,y4);
		PGL.gl.getGL2().getGL2().glEnd(); 
	}

	@Override
	public int buildUI(GUIController controller,int x, int y , Tab tab, Group group) {
		// TODO Auto-generated method stub
		return(0);

	}



	@Override
	public void drawBackground(PGraphicsOpenGL pgl) {

	}

}