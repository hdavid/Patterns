import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.media.opengl.GL2;

import controlP5.Group;
import controlP5.Tab;

import processing.opengl.PJOGL;
import processing.opengl.PGraphicsOpenGL;

public class LayerNoise extends LayerColor {

	FloatBuffer vbuffer;
	FloatBuffer cbuffer;
	
	int maxNumPoint=100000;

	public FloatParameter noiseH      = new FloatParameter(this,0f,0f,1f,"noiseH");
	public FloatParameter noiseS      = new FloatParameter(this,0f,0f,1f,"noiseS");
	public FloatParameter noiseB      = new FloatParameter(this,0f,0f,1f,"noiseB");

	public FloatParameter sortX      = new FloatParameter(this,0f,0f,1f,"sortX");
	public FloatParameter sortY      = new FloatParameter(this,0f,0f,1f,"sortY");
	public FloatParameter noiseX      = new FloatParameter(this,1f,0f,1f,"noiseX");
	public FloatParameter noiseY     = new FloatParameter(this,1f,0f,1f,"noiseY");

	public FloatParameter updateColor  = new FloatParameter(this,1,0,1,"updateColor");
	public FloatParameter updatePosition  = new FloatParameter(this,1,0,1,"updatePosition");

	public FloatParameter size      = new FloatParameter(this,1f,0f,200f,FloatParameter.FREE,"size");
	public FloatParameter number      = new FloatParameter(this,0f,0f,maxNumPoint,"number");
	
	public FloatParameter number2      = new FloatParameter(this,1f,1f,100,"number2");
	public ListParameter shape      = new ListParameter(this,1,new int[]{1,2,3,4},new String[]{"pt","line","tri","quad"},"shape");

	public ListParameter  incNoiseX  = new ListParameter(this,0,ListParameter.onOff,ListParameter.onOffNames,"incNoiseX");
	public ListParameter  incNoiseY  = new ListParameter(this,0,ListParameter.onOff,ListParameter.onOffNames,"incNoiseY");
	public ListParameter  incNoiseC  = new ListParameter(this,0,ListParameter.onOff,ListParameter.onOffNames,"incNoiseC");
	
	public ListParameter continous  = new ListParameter(this,0,ListParameter.onOff,ListParameter.onOffNames,"continous");

	long lastCol=0;
	long lastPos=0;
	float[]x=null;
	float[]y=null;

	public LayerNoise(Application parent) {
		super(parent);
		setParameterMap();
	}

	@Override
	public void draw(PGraphicsOpenGL pgl){

		int numPoints = (int)number.v();

		//allocate buffer
		if(vbuffer==null){
			int vSize = (maxNumPoint * 2);
			int cSize = (maxNumPoint * 4);
			vSize = vSize << 2;
			cSize = cSize << 2;
			vbuffer = ByteBuffer.allocateDirect(vSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
			cbuffer = ByteBuffer.allocateDirect(cSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
			x = new float[maxNumPoint];
			y = new float[maxNumPoint];
		}
		
		//update post and colours
		long now = System.currentTimeMillis();
		boolean pos = false;
		if(now-lastPos> (1-updatePosition.v())*1000 && updatePosition.v()>0f){
			pos=true;
			lastPos=now;
		}
		boolean col=false;
		if(now-lastCol> (1-updateColor.v())*1000 && updateColor.v()>0f){
			col=true;
			lastCol=now;
		}
		
		//parameter values
		float noiseX = this.noiseX.v();
		float noiseY = this.noiseY.v();
		boolean incNoiseX = this.incNoiseX.v()==1;
		boolean incNoiseY = this.incNoiseY.v()==1;
		float g = this.gradient.v();
		float ag = 1-g;
		boolean continous = this.continous.v()==1;
		
		int adaptedNumber = (int)number.v();
		if((int)adaptedNumber>number.max){
			adaptedNumber=(int)number.max;
		}
		adaptedNumber /=this.number2.v();
		
		float shape = this.shape.v();
		adaptedNumber = (shape==2)?adaptedNumber/10: (shape==3)?adaptedNumber/500: (shape==4)?adaptedNumber/=2000: adaptedNumber-2;
		
		
		for (int i = 0; i < adaptedNumber; i++) {
			// random x,y
			if(pos){
				x[i]= (incNoiseX&&i>0?x[i-1]:Config.WIDTH/2)
						+ parent.random(-1f,1f) * Config.WIDTH *noiseX * 
						(incNoiseX&&i>0?1f/numPoints*1000:1f);
				y[i]= (incNoiseY&&i>0?y[i-1]:Config.HEIGHT/2) + 
						parent.random(-1f,1f) *Config.HEIGHT * noiseY * 
						(incNoiseY&&i>0?1f/numPoints*1000:1f);
			}
			// random r,g,b
			if(col){
				if(shape==2 && continous && i>0){
					for(int j=0;j<2;j++){
						if(i%2==0){
							HSBtoRGB(noiseH.v(),noiseS.v(),noiseB.v(),fgH.v(),fgS.v(),fgB.v());
							cbuffer.put(foregroundR/255f);
							cbuffer.put(foregroundG/255f);
							cbuffer.put(foregroundB/255f);
							cbuffer.put(fgAlpha.v());
						}else{
							HSBtoRGB();
							HSBtoRGB(noiseH.v(),noiseS.v(),noiseB.v(),fgH.v(),fgS.v(),fgB.v());
							cbuffer.put(foregroundR/255f*ag+backgroundR*g);
							cbuffer.put(foregroundG/255f*ag+backgroundG*g);
							cbuffer.put(foregroundB/255f*ag+backgroundB*g);
							cbuffer.put(fgAlpha.v());
						}
					}
				}else{
					if(i%2==0){
						HSBtoRGB(noiseH.v(),noiseS.v(),noiseB.v(),fgH.v(),fgS.v(),fgB.v());
						cbuffer.put(foregroundR/255f);
						cbuffer.put(foregroundG/255f);
						cbuffer.put(foregroundB/255f);
						cbuffer.put(fgAlpha.v());
					}else{
						HSBtoRGB();
						HSBtoRGB(noiseH.v(),noiseS.v(),noiseB.v(),fgH.v(),fgS.v(),fgB.v());
						cbuffer.put(foregroundR/255f*ag+backgroundR*g);
						cbuffer.put(foregroundG/255f*ag+backgroundG*g);
						cbuffer.put(foregroundB/255f*ag+backgroundB*g);
						cbuffer.put(fgAlpha.v());
					}
				}
			}

		}
		for(int j=0;j<2;j++){
				HSBtoRGB(noiseH.v(),noiseS.v(),noiseB.v(),fgH.v(),fgS.v(),fgB.v());
				cbuffer.put(foregroundR/255f);
				cbuffer.put(foregroundG/255f);
				cbuffer.put(foregroundB/255f);
				cbuffer.put(fgAlpha.v());
		}

		
		//sorting
		if(pos){
			int a=0;
			int block=(int)(adaptedNumber*sortX.v()*sortX.v()*sortX.v()*sortX.v());
			if(block>0){
				while(a+block<adaptedNumber){
					Arrays.sort(x,a,a+block);
					a+=block;
				}
				Arrays.sort(x,a,adaptedNumber);


			}
			block=(int)(adaptedNumber*sortY.v()*sortY.v()*sortY.v()*sortY.v());
			a=0;
			if(block>0){
				while(a+block<adaptedNumber){
					Arrays.sort(y,a,a+block);
					a+=block;
				}
				Arrays.sort(y,a,adaptedNumber);
			}
		}

		//put sorted data in buffer
		for (int i = 0; i < adaptedNumber; i++) {
			if(pos){
				if(shape==2 && continous && i>0){
					vbuffer.put(x[i]);
					vbuffer.put(y[i]);
				}
				vbuffer.put(x[i]);
				vbuffer.put(y[i]);
			}
		}
		//closing the loop for continous lines
		if(pos){
			if(shape==2 && continous && adaptedNumber>0){
				vbuffer.put(x[0]);
				vbuffer.put(y[0]);
			}
		}
		vbuffer.rewind();
		cbuffer.rewind();
		
		GL2 gl2 = PJOGL.gl.getGL2();
		
		//draw
		float size = this.size.v();
		if(size>=1f){
			//use buffers
			gl2.glEnableClientState(GL2.GL_VERTEX_ARRAY);
			gl2.glVertexPointer(2, GL2.GL_FLOAT, 0, vbuffer);
			gl2.glEnableClientState(GL2.GL_COLOR_ARRAY);
			gl2.glColorPointer(4, GL2.GL_FLOAT, 0, cbuffer);
			//size
			gl2.glPointSize(size);
			gl2.glLineWidth(size);
			//shapes
			if(shape==1){
				gl2.glDrawArrays(GL2.GL_POINTS, 0, adaptedNumber);			
			}
			if(shape==2){
				gl2.glDrawArrays(GL2.GL_LINES, 0, (continous && adaptedNumber>0)?adaptedNumber*2+1:adaptedNumber);			
			}
			if(shape==3){
				gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, adaptedNumber);			
			}	
			if(shape==4){
				gl2.glDrawArrays(GL2.GL_QUADS, 0, adaptedNumber);			
			}	
		}
		//gl2.glPopMatrix();
		gl2.glDisableClientState(GL2.GL_VERTEX_ARRAY);
		gl2.glDisableClientState(GL2.GL_COLOR_ARRAY);

	}

	public void HSBtoRGB(float nH,float nS, float nB,float h,float s, float b){
		h = parent.random(nH)+h;
		s = parent.random(nS)+s;
		b = parent.random(nB)+b;
		if(h>1)h=h-1;
		if(h<1)h=h+1;
		if(s>1)s=1;
		if(b>1)b=1;
		Color rgb = new Color(Color.HSBtoRGB(h,s,b));
		foregroundR = rgb.getRed();
		foregroundG = rgb.getGreen();
		foregroundB = rgb.getBlue(); 
	}

	public void HSBtoRGB(){
		Color rgb = new Color(Color.HSBtoRGB(fgH.v(),fgS.v(),fgB.v()));
		foregroundR = rgb.getRed();
		foregroundG = rgb.getGreen();
		foregroundB = rgb.getBlue(); 
		rgb = new Color(Color.HSBtoRGB(bgH.v(),bgS.v(),bgB.v()));
		backgroundR = rgb.getRed();
		backgroundG = rgb.getGreen();
		backgroundB = rgb.getBlue();
	}


	@Override
	public void drawBackground(PGraphicsOpenGL pgl) {
		HSBtoRGB();
		HSBtoRGB();
		GL2 gl = PJOGL.gl.getGL2().getGL2();
		gl.glColor4f( backgroundR/255f, backgroundG/255f, backgroundB/255f,bgAlpha.v());
		gl.getGL2().glBegin(GL2.GL_QUADS);
		gl.getGL2().glVertex2f(0f,0f);
		gl.getGL2().glVertex2f(Config.WIDTH,0f);
		gl.getGL2().glVertex2f(Config.WIDTH,Config.HEIGHT);
		gl.getGL2().glVertex2f(0f,Config.HEIGHT);
		gl.getGL2().glEnd();
	}

	@Override
	public int buildUI(GUIController controller, int x, int y, Tab tab, Group group) {
		int h=controller.h;
		int hMargin = controller.hMargin;
		String layerType = this.getType();
		int startY=y;
		y=+super.buildUI(controller,x,y,tab,group);

		y+=controller.addColorGroup(layerType,"Colour Noise","noise",x,y,tab,null);
		y+=h;
		controller.addControlFloatParameter(layerType,"size",0,40, x, y, tab,null); 

		y+=h+hMargin;
		controller.addControlFloatParameter(layerType,"number",0,10000, x, y, tab,null); 
		y+=h+hMargin;
		controller.addControlFloatParameter(layerType,"number2",1,100, x, y, tab,null); 

		y+=h+hMargin;
		controller.addControlFloatParameter(layerType,"updatePosition",0,1, x, y, tab,null); 
		y+=h+hMargin;
		controller.addControlFloatParameter(layerType,"updateColor",0,1, x, y, tab,null); 
		y+=h+hMargin+hMargin;
		controller.addControlFloatParameter(layerType,"sortX",0,1, x, y, tab,null); 
		y+=h+hMargin;
		controller.addControlFloatParameter(layerType,"sortY",0,1, x, y, tab,null); 
		y+=h+hMargin+hMargin;
		controller.addControlFloatParameter(layerType,"noiseX",0,1, x, y, tab,null); 
		y+=h+hMargin;
		controller.addControlFloatParameter(layerType,"noiseY",0,1, x, y, tab,null); 

		x=670;
		y=startY;
		controller.addRadio(layerType, "shape.v",shape.names,x, y, tab, null);
		y+=5*(h+hMargin);
		controller.addToggle(layerType, "incNoiseX.v", false, x, y, tab, group).setCaptionLabel("inc noise x");
		y+=h+hMargin;
		controller.addToggle(layerType, "incNoiseY.v", false, x, y, tab, group).setCaptionLabel("inc noise y");
//		y+=h+hMargin;
//		controller.addToggle(layerType, "incNoiseC.v", false, x, y, tab, group);
		y+=2*(h+hMargin);
		controller.addToggle(layerType, "continous.v",false,x, y, tab, null).setCaptionLabel("continuous lines");
		
		return(y);
	}


	public void keyEvent(int key, int keyCode, char ch, boolean a, boolean c, boolean s,boolean win,boolean apl,long[] lastPressedTime) {

		super.keyEvent(key, keyCode, ch, a, c, s, win, apl, lastPressedTime);

	}

}






