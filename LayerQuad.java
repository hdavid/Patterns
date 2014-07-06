
import javax.media.opengl.GL2;

import controlP5.Group;
import controlP5.Tab;


public class LayerQuad extends LayerColor {

	public FloatParameter speed = new FloatParameter(this,50f,1f,1000f,FloatParameter.LIMIT,"speed");


	 float tlX = 0;
	 float tlY = 0;
	 float trX = 1;
	 float trY = 0;
	 float brX = 1;
	 float brY = 1;
	 float blX = 0;
	 float blY = 1;

	 int ctlX = 0;
	 int ctlY = 0;
	 int ctrX = 0;
	 int ctrY = 0;
	 int cbrX = 0;
	 int cbrY = 0;
	 int cblX = 0;
	 int cblY = 0;
	
	long lastFrame = 0;


	public LayerQuad(Application parent){
		super(parent);
		setParameterMap();
	}



	@Override
	public void draw(GL2 gl){

		//SoundControllerGroup group = parent.soundController.getGroups().get(0);
		HSBtoRGB();
		gl.glPushMatrix();
		//PJOGL.gl.glTranslatef(Config.WIDTH/2,Config.HEIGHT/2,0);

		gl.glBegin(GL2.GL_QUADS);
		float g = gradient.v();
		float ag = 1-g;
		
		gl.glColor4f(
				foregroundR/255f
				,foregroundG/255f
				,foregroundB/255f
				,fgAlpha.v()
				);


		gl.glVertex2f(Config.WIDTH*tlX, Config.HEIGHT*tlY);
		gl.glColor4f(
				backgroundR/255f*g + foregroundR/255f*ag
				,backgroundG/255f*g + foregroundG/255f*ag
				,backgroundB/255f*g + foregroundB/255f*ag
				,fgAlpha.v()
				);
		gl.glVertex2f(Config.WIDTH*trX, Config.HEIGHT*trY);
		gl.glColor4f(
				foregroundR/255f
				,foregroundG/255f
				,foregroundB/255f
				,fgAlpha.v()
				);

		gl.glVertex2f(Config.WIDTH*brX, Config.HEIGHT*brY);
		gl.glColor4f(
				backgroundR/255f*g + foregroundR/255f*ag
				,backgroundG/255f*g + foregroundG/255f*ag
				,backgroundB/255f*g + foregroundB/255f*ag
				,fgAlpha.v()
				);
		gl.glVertex2f(Config.WIDTH*blX, Config.HEIGHT*blY);



		gl.glEnd();
		gl.glPopMatrix();
	}






	@Override
	public void drawBackground(GL2 gl){
		//GL2 gl = PJOGL.gl.getGL2();
		gl.glColor4f( backgroundR/255f, backgroundG/255f, backgroundB/255f, bgAlpha.v());
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(0f,0f);
		gl.glVertex2f(Config.WIDTH,0f);
		gl.glVertex2f(Config.WIDTH,Config.HEIGHT);
		gl.glVertex2f(0f,Config.HEIGHT);
		gl.glEnd();
	}

	@Override
	public int buildUI(GUIController controller, int x, int y, Tab tab, Group group) {

		y+=super.buildUI(controller, x, y, tab, group);
		String layerType = this.getType();
		//Bars		


		controller.addControlFloatParameter(layerType,"speed",speed.min,speed.max, x, y, tab,null);
		y+=controller.h+controller.hMargin;

		return(y);

	}

	public void soundControl(SoundController soundController){
		super.soundControl(soundController);

		//SoundControllerGroup group0 = soundController.getGroups().get(0);
		SoundControllerGroup group1 = soundController.getGroups().get(1);
		SoundControllerGroup group2 = soundController.getGroups().get(2);
		SoundControllerGroup group3 = soundController.getGroups().get(3);
		SoundControllerGroup group4 = soundController.getGroups().get(4);


		if(group1.isOnBeat()){
			if(ctlX==0){
				if(tlX==0){
					ctlX=1;
				}else if(tlX==1){
					ctlX=-1;
				}
			}
		}
		if(group2.isOnBeat()){
			if(ctrX==0){
				if(trX==0){
					ctrX=1;
				}else if(trX==1){
					ctrX=-1;
				}
			}
		}
		if(group3.isOnBeat()){
			if(cblX==0){
				if(blX==0){
					cblX=1;
				}else if(blX==1){
					cblX=-1;
				}
			}
		}
		if(group4.isOnBeat()){
			if(cbrX==0){
				if(brX==0){
					cbrX=1;
				}else if(brX==1){
					ctrX=-1;
				}
			}

		}
		if(group1.isOnBeat()){
			if(ctlY==0){
				if(tlY==0){
					ctlY=1;
				}else if(tlY==1){
					ctlY=-1;
				}
			}
		}
		if(group2.isOnBeat()){
			if(ctrY==0){
				if(trY==0){
					ctrY=1;
				}else if(trY==1){
					ctrY=-1;
				}
			}
		}
		if(group3.isOnBeat()){
			if(cblY==0){
				if(blY==0){
					cblY=1;
				}else if(blY==1){
					cblY=-1;
				}
			}
		}
		if(group4.isOnBeat()){
			if(cbrY==0){
				if(brY==0){
					cbrY=1;
				}else if(brY==1){
					ctrY=-1;
				}
			}

		}

	}

	
	@Override
	public void update() {
		super.update();
		long now =  System.currentTimeMillis();
		long timeDiff = now - lastFrame;

		float speed = this.speed.v();
		if(ctlX!=0){
			tlX +=  timeDiff/speed*ctlX;
			if(tlX<0){
				ctlX=0;
				tlX=0;
			}
			if(tlX>1){
				ctlX=0;
				tlX=1;
			}
		}
		if(ctrX!=0){
			trX +=  timeDiff/speed*ctrX;
			if(trX<0){
				ctrX=0;
				trX=0;
			}
			if(trX>1){
				ctrX=0;
				trX=1;
			}
		}
		if(cblX!=0){
			blX +=  timeDiff/speed*cblX;
			if(blX<0){
				cblX=0;
				blX=0;
			}
			if(blX>1){
				cblX=0;
				blX=1;
			}
		}
		if(cbrX!=0){
			brX +=  timeDiff/speed*cbrX;
			if(brX<0){
				cbrX=0;
				brX=0;
			}
			if(trX>1){
				ctrX=0;
				trX=1;
			}
		}
		if(ctlY!=0){
			tlY +=  timeDiff/speed*ctlY;
			if(tlY<0){
				ctlY=0;
				tlY=0;
			}
			if(tlY>1){
				ctlY=0;
				tlY=1;
			}
		}
		if(ctrY!=0){
			trY +=  timeDiff/speed*ctrY;
			if(trY<0){
				ctrY=0;
				trY=0;
			}
			if(trY>1){
				ctrY=0;
				trY=1;
			}
		}
		if(cblY!=0){
			blY +=  timeDiff/speed*cblY;
			if(blY<0){
				cblY=0;
				blY=0;
			}
			if(blY>1){
				cblY=0;
				blY=1;
			}
		}
		if(cbrY!=0){
			brY +=  timeDiff/speed*cbrY;
			if(brY<0){
				cbrY=0;
				brY=0;
			}
			if(trY>1){
				ctrY=0;
				trY=1;
			}
		}
		lastFrame=now;
	}

}
