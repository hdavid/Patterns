

import javax.media.opengl.GL2;

import controlP5.Group;
import controlP5.Tab;

public class LayerBars extends LayerMovementIncremental {


	public ListParameter  bar1  = new ListParameter(this,0,ListParameter.onOff,ListParameter.onOffNames,"bar1");
	public ListParameter  bar2  = new ListParameter(this,0,ListParameter.onOff,ListParameter.onOffNames,"bar2");
	public FloatParameter number = new FloatParameter(this,-15f,-15f,50f,FloatParameter.LIMIT,"number");
	
	public LayerBars(Application parent){
		super(parent);
		setParameterMap();
	}
	
	@Override
	public void draw(GL2 gl){
		if(bar1.v()==1 || bar2.v()==1){
			//transform
			gl.glPushMatrix();
			gl.glTranslatef(Config.WIDTH/2,Config.HEIGHT/2,0);
			//PGL.gl2x.glScalef(zoom.v()/4f,zoom.v()/4f,0);
			//PGL.gl2x.glRotatef(angle.v()/(2f*(float)Math.PI)*360f,0,0,1); 
			//gl.glTranslatef(scrollX.v()*4f,scrollY.v()*4f,0);//sync scroll with patterns
			//PGL.gl2x.glTranslatef(scrollX.v(),scrollY.v(),0);
			fillAll(gl);
			gl.glPopMatrix();
		}
	}


	private void fillAll(GL2 gl){
		//makeList();
		//PGL.gl2x.glColor4f( foregroundR/255f, foregroundG/255f, foregroundB/255f,fgAlpha.v()/255);

		int range = 2+(int)(
				(Math.min(width*2,height)*zoom.v()+Math.sqrt((float)Config.WIDTH*Config.WIDTH+Config.HEIGHT*Config.HEIGHT))
				/
				(Math.min(width*2,height)*zoom.v())
				/ 2
		);
		range*=8;
		
		if(number.v()>-10){
			if(number.v()>0){
				range=(int)number.v();
			}else{
				range=0;
			}
		}
		
		if(range>0){
			if(bar1.v()==1){
				for(int x=-range;x<=range;x++){
					gl.glPushMatrix();

					gl.glScalef(zoom.v(izoom,x,range)/4f,zoom.v(izoom,x,range)/4f,0);
					gl.glRotatef(angle.v(iangle,x,range)/(2f*(float)Math.PI)*360f,0,0,1); 
					//gl.glTranslatef(scrollX.v()*4f,scrollY.v()*4f,0);//sync scroll with patterns
					gl.glTranslatef(scrollX.v(iscrollX,x,range),scrollY.v(iscrollY,x,range),0);
					
					int xOffset=(int)(width*(x)/-2f);
					paint(xOffset,x,range,gl);
					//PGL.gl2x.glRotatef(-1*iangle.v()*x/(2f*(float)Math.PI)*360f,0,0,1);
					gl.glPopMatrix();
				}
			}
			
			gl.glTranslatef(-scrollX.v(),-scrollY.v(),0);
			gl.glRotatef(rotate.v()/(2f*(float)Math.PI)*360f+90f,0,0,1);
			gl.glTranslatef(scrollY.v(),scrollX.v(),0);
			
			if(bar2.v()==1){
				for(int x=-range;x<=range;x++){
					gl.glPushMatrix();

					gl.glScalef(zoom.v(izoom,x,range)/4f,zoom.v(izoom,x,range)/4f,0);
					gl.glRotatef(angle.v(iangle,x,range)/(2f*(float)Math.PI)*360f,0,0,1); 
					//gl.glTranslatef(scrollX.v()*4f,scrollY.v()*4f,0);//sync scroll with patterns
					gl.glTranslatef(scrollY.v(iscrollY,x,range),scrollX.v(iscrollX,x,range),0);
					
					int xOffset=(int)(width*(x)/-2f);
					paint(xOffset,x,range,gl);
					//PGL.gl2x.glRotatef(-1*iangle.v()*x/(2f*(float)Math.PI)*360f,0,0,1);
					gl.glPopMatrix();
					
				}
			}
		}
		
	}

	float alpha = 0;
	
	private void paint(float x,int i,int range,GL2 gl){
		HSBtoRGB(i, range);
		alpha=fgAlpha.v(ifgAlpha,i+1,range);
		
		//gl.glPushMatrix();
		gl.glTranslatef(x,0,0);
		//PGL.gl2x.glCallList(patternList); 
		shape(new float[][]{
				{-0.25f*size.v(isize,i+1,range)/4f,-1f*200},
				{0.25f*size.v(isize,i+1,range)/4f,-1f*200},
				{0.25f*size.v(isize,i+1,range)/4f,1f*200},
				{-0.25f*size.v(isize,i+1,range)/4f,1f*200}
		}, gl);
		gl.glTranslatef(-1*x,0,0);
		//gl.glPopMatrix();
	}

	/**
	 * due to opengl limitations, only works (well) for convex shapes
	 * @param points
	 */
	private void shape(float[][] points, GL2 gl){
		float ratio=0; 
		gl.glBegin(GL2.GL_POLYGON);
		for(int i=0;i<points.length;i++){
			ratio=(float)i/(float)points.length*gradient.v() ;
			gl.glColor4f(
					(1-ratio)*foregroundR/255f +(ratio)*backgroundR/255f 
					,    (1-ratio)*foregroundG/255f +(ratio)*backgroundG/255f,
					(1-ratio)*foregroundB/255f +(ratio)*backgroundB/255f
					,alpha
			);
			gl.glVertex2f(points[i][0]*width,points[i][1]*height);	
		}
		gl.glEnd();
	}

	@Override
	public void drawBackground(GL2 gl){
		HSBtoRGB();
		
		//GL2 gl = gl;
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
		
		super.buildUI(controller, x, y, tab, group);
		String layerType = this.getType();
		//Bars		
		x=750;
		y+=controller.h+controller.hMargin;
		controller.addLabel(layerType,"bars","bars",x-72,y,tab,null);
		controller.addLabel(layerType,"scroll","scroll",x,y,tab,null);
		y+=controller.h+controller.hMargin;
		controller.addToggle(layerType,"bar1.sc",false        ,x-50*2,y,tab,null).setCaptionLabel("");
		controller.addToggle(layerType,"bar2.sc",false        ,x-50+5,y,tab,null).setCaptionLabel("");
		controller.addToggle(layerType,"scrollMode.sc",false        ,x,y,tab,null).setCaptionLabel("");
		
		controller.addNumber(layerType,"bar1.scAccumulatorInc"      ,0,x-50*2+15,y,tab,null).setCaptionLabel("");
		controller.addNumber(layerType,"bar2.scAccumulatorInc"      ,0,x-50+15,y,tab,null).setCaptionLabel("");
		controller.addNumber(layerType,"scrollMode.scAccumulatorInc"  ,0,x+15,y,tab,null).setCaptionLabel("");
		y+=controller.h+controller.hMargin;
		
		controller.addRadio(layerType,"bar1.v",ListParameter.onOffNames,x-50*2,y,tab,null);
		controller.addRadio(layerType,"bar2.v",ListParameter.onOffNames,x-50,y,tab,null);
		controller.addRadio(layerType,"scrollMode.v",LayerNineBlockPattern.scrollModeNames,x,y,tab,null);
		
		return(y);
		
	}
	
	public void keyEvent(int key, int keyCode, char ch, boolean a, boolean c, boolean s,boolean win,boolean apl,long[] lastPressedTime) {

		super.keyEvent(key, keyCode, ch, a, c, s, win, apl, lastPressedTime);
		
		switch(keyCode){


		case 79/*'o.centerpattern'*/: 
			if(!s&&!c&&!a){
					bar2.v=bar2.v()==1?0:1;
			}
           
			break;

		case 73/*'i.sidepattern'*/: 
			if(!s&&!c&&!a){
				bar1.v=bar1.v()==1?0:1;
			}     
			break;	
		}

	}

	@Override
	public void update() {
		super.update();
	}

}






