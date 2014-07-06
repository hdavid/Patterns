import javax.media.opengl.GL2;

import controlP5.Group;
import controlP5.Tab;

public class LayerNineBlockPattern extends LayerMovementIncremental {

	private NineBlockPatternDrawer drawer;

	public int width=Config.PATTERN_WIDTH;
	public int height=Config.PATTERN_WIDTH;
	public int xOffset=0;
	public int yOffset=0;

	static int[] shapeAnglesNumbers=new int[]{0,90,180,270};
	static String[] shapeAnglesNames=new String[] {"0","90","180","270"};
	static String[] centerShapesNames=new String[] {"none", "full","<>","[]","none"};
	static int[] centerShapesNumbers=new int[]{0,1,5,9,16};
	static int[] shapesNumbers=new int[]{0,1,5,9,2,3,4,6,7,8,10,11,12,13,14,15,16};
	static String[] shapesNames=new String[] {
		"none"     ,"full"/*1*/ ,
		"<>"/*5*/  ,"[]"/*9*/,
		"|\\"/*2*/ ,"/\\"/*3*/,
		"||"/*4*/  ,"Z"/*6*/,
		"TF"/*7*/  ,"/|"/*8*/,
		"^^"/*10*/ ,"[]"/*11*/,
		"|/"/*12*/,"/\\"/*13*/,
		"/\\"/*14*/ ,"/\\"/*15*/,
		"none"
	};




	public ListParameter shapeCenter       = new ListParameter(this,0,centerShapesNumbers ,centerShapesNames ,"shapeCenter");
	public ListParameter shapeSide         = new ListParameter(this,0,shapesNumbers,shapesNames ,"shapeSide");
	public ListParameter shapeCorner       = new ListParameter(this,0,shapesNumbers,shapesNames ,"shapeCorner");
	public ListParameter shapeCenterAngle  = new ListParameter(this,0, shapeAnglesNumbers,shapeAnglesNames ,"shapeCenterAngle");
	public ListParameter shapeSideAngle    = new ListParameter(this,0, shapeAnglesNumbers ,shapeAnglesNames ,"shapeSideAngle");
	public ListParameter shapeCornerAngle  = new ListParameter(this,0, shapeAnglesNumbers,shapeAnglesNames ,"shapeCornerAngle");

	public ListParameter subRotate  = new ListParameter(this,1, new int[] { 0,1,2,3},new String[] {"0","1","2","3"} ,"subRotate");

	public FloatParameter number    = new FloatParameter(this,-15f,-15f,50f,"number");

	public LayerNineBlockPattern(Application parent){
		super(parent);
		drawer=new NineBlockPatternDrawer();
		subRotate.tcIncPerSec=0;
		gradient.tcIncPerSec=0;
		randomize();
		shapeCenter.v=0;
		shapeCorner.v=0;
		shapeSide.v=0;
		setParameterMap();
	}

	@Override
	public int  buildUI(GUIController controller, int x, int y, Tab tab, Group group) {
		String layerType = this.getType();

		y+=super.buildUI(controller, x, y,tab,group);

		int h = controller.h;
		y=controller.hMargin;
		x=670;
		controller.addLabel(layerType,"Pattern","   P a t t e r n",x,y,tab,group);
		y+=h+controller.hMargin;
		controller.addLabel(layerType,"corner label","C (u)",x,y,tab,group);
		y+=h+controller.hMargin;
		controller.addToggle(layerType,"shapeCornerAngle.sc",false        ,x,y,tab,group).setCaptionLabel("sc");
		x+=15;
		controller.addNumber(layerType,"shapeCornerAngle.scAccumulatorInc"      ,0,x,y,tab,group).setCaptionLabel("");
		x-=15;
		y+=h+controller.hMargin;
		controller.addRadio(layerType,"shapeCornerAngle.v",shapeCornerAngle.names,x,y,tab,group);    
		y+=h*5+controller.hMargin*3;
		controller.addToggle(layerType,"shapeCorner.sc",false        ,x,y,tab,group).setCaptionLabel("sc");
		x+=15;
		controller.addNumber(layerType,"shapeCorner.scAccumulatorInc"      ,0,x,y,tab,group).setCaptionLabel("");
		x-=15;
		y+=h+controller.hMargin;
		controller.addRadio(layerType,"shapeCorner.v",shapeCorner.names,x,y,tab,group);

		x+=50;
		y=h+controller.hMargin+controller.hMargin;
		controller.addLabel(layerType,"side label","S (i)",x,y,tab,group);
		y+=h+controller.hMargin;
		controller.addToggle(layerType,"shapeSideAngle.sc",false        ,x,y,tab,group).setCaptionLabel("sc");
		x+=15;
		controller.addNumber(layerType,"shapeSideAngle.scAccumulatorInc"      ,0,x,y,tab,group).setCaptionLabel("");
		x-=15;
		y+=h+controller.hMargin;    
		controller.addRadio(layerType,"shapeSideAngle.v",shapeSideAngle.names,x,y,tab,group);    
		y+=h*5+controller.hMargin*3;
		controller.addToggle(layerType,"shapeSide.sc",false        ,x,y,tab,group).setCaptionLabel("sc");
		x+=15;
		controller.addNumber(layerType,"shapeSide.scAccumulatorInc"      ,0,x,y,tab,group).setCaptionLabel("");
		x-=15;
		y+=h+controller.hMargin;
		controller.addRadio(layerType,"shapeSide.v",shapeSide.names,x,y,tab,group);


		x+=50;
		y=h+controller.hMargin+controller.hMargin;;
		controller.addLabel(layerType,"center label","C (o)",x,y,tab,group);
		y+=h+controller.hMargin;
		controller.addToggle(layerType,"shapeCenterAngle.sc",false        ,x,y,tab,group).setCaptionLabel("sc");
		x+=15;
		controller.addNumber(layerType,"shapeCenterAngle.scAccumulatorInc"      ,0,x,y,tab,group).setCaptionLabel("");
		x-=15;
		y+=h+controller.hMargin;    
		controller.addRadio(layerType,"shapeCenterAngle.v",shapeCenterAngle.names,x,y,tab,group);    
		y+=h*5+controller.hMargin*3;
		controller.addToggle(layerType,"shapeCenter.sc",false        ,x,y,tab,group).setCaptionLabel("sc");
		x+=15;
		controller.addNumber(layerType,"shapeCenter.scAccumulatorInc"      ,0,x,y,tab,group).setCaptionLabel("");
		x-=15;
		y+=h+controller.hMargin;
		controller.addRadio(layerType,"shapeCenter.v",shapeCenter.names,x,y,tab,group);
		y+=h*7+controller.hMargin;

		//sub rotates
		controller.addLabel(layerType,"subrotate","subrotate",x,y,tab,group);
		y+=h+controller.hMargin;
		controller.addToggle(layerType,"subRotate.sc",false        ,x,y,tab,group).setCaptionLabel("sc");
		x+=15;
		controller.addNumber(layerType,"subRotate.scAccumulatorInc"      ,0,x,y,tab,group).setCaptionLabel("");
		x-=15;
		y+=h+controller.hMargin;
		controller.addRadio(layerType,"subRotate.v",new String[]{"0","1","2","3"},x,y,tab,group);    
		y+=h*6+controller.hMargin;

		return(y);


	}

	@Override
	public void draw(GL2 gl) {
		//convert color from HSB into RGB color space for rendering.
		HSBtoRGB();

		drawer.set(this, gl);

		drawer.draw(gl);

	}

	@Override
	public void drawBackground(GL2 gl) {
		HSBtoRGB();
		drawer.set(this, gl);
		drawer.paintBackground(gl);
	}

	public void randomizePattern(){
		randomizeShapeCenter();
		randomizeShapeSide();
		randomizeShapeCorner();
	}
	public void randomizeShapeCenter(){
		shapeCenter.randomize();
		shapeCenterAngle.randomize();
	}
	public void randomizeShapeCorner(){
		shapeCorner.randomize();
		shapeCornerAngle.randomize();
	}
	public void randomizeShapeSide(){
		shapeSide.randomize();
		shapeSideAngle.randomize();
	}

	public void randomize(){
		randomizeShapeCenter();
		randomizeShapeSide();
		randomizeShapeCorner();
		randomColor();
	} 


	public void keyEvent(int key, int keyCode, char ch, boolean a, boolean c, boolean s,boolean win,boolean apl,long[] lastPressedTime) {

		super.keyEvent(key, keyCode, ch, a, c, s, win, apl, lastPressedTime);
		
		switch(keyCode){


		case 44/*',' shape sound control*/: 
			//case 44/* ';' MAC*/
			if(!s&&!c&&!a){
				shapeCenter.sc=true;
				shapeSide.sc=true;
				shapeCorner.sc=true;
				System.out.println("Shape Sound control ON for layer "+(parent.currentLayer+1));
			}
			if(s&&!c&&!a){
				shapeCenter.sc=false;
				shapeSide.sc=false;
				shapeCorner.sc=false;
				System.out.println("Shape Sound control OFF for layer "+(parent.currentLayer+1));
			}
			break;

		case 79/*'o.centerpattern'*/: 
			if(!s&&!c&&!a){
				if(System.currentTimeMillis()-lastPressedTime[keyCode] < timings[timings.length-1]){
					shapeCenter.tc=true;
					shapeCenter.randomize();
					shapeCenterAngle.randomize();
					int idx=0;
					while(idx<timings.length && timings[idx]<System.currentTimeMillis()-lastPressedTime[keyCode])idx++;
					shapeCenter.tcIncPerSec=timings[idx];

				}
				else{
					if(!shapeCenter.tc){
						shapeCenter.randomize();
						shapeCenterAngle.randomize();
					}
					shapeCenter.tc=false;
				}
			}
			if(s&&!c&&!a){
				shapeCenter.tc=false;
				shapeCenter.v=16;
			}
			if(!s&&c&&!a){
				shapeCenter.tc=true;
				shapeCenter.tcIncPerSec+=shapeCenter.tcIncPerSec*0.25+0.001;
			}
			if(!s&&!c&&a){
				shapeCenter.tc=true;
				shapeCenter.tcIncPerSec-=shapeCenter.tcIncPerSec*0.25+0.001;
			}      
			if(!s&&c&&a){
				shapeCenter.sc=!shapeCenter.sc;
			}           
			break;


		case 73/*'i.sidepattern'*/: 
			if(!s&&!c&&!a){
				if(System.currentTimeMillis()-lastPressedTime[keyCode] < timings[timings.length-1]){
					shapeSide.tc=true;
					shapeSide.randomize();
					shapeSideAngle.randomize();
					int idx=0;
					while(idx<timings.length && timings[idx]<System.currentTimeMillis()-lastPressedTime[keyCode])idx++;
					shapeSide.tcIncPerSec=timings[idx];

				}
				else{
					if(!shapeSide.tc){
						shapeSide.randomize();
						shapeSideAngle.randomize();
					}
					shapeSide.tc=false;
				}
			}
			if(s&&!c&&!a){
				shapeSide.tc=false;
				shapeSide.v=16;
			}
			if(!s&&c&&!a){
				shapeSide.tc=true;
				shapeSide.tcIncPerSec+=shapeSide.tcIncPerSec*0.25+0.001;
			}
			if(!s&&!c&&a){
				shapeSide.tc=true;
				shapeSide.tcIncPerSec-=shapeSide.tcIncPerSec*0.25+0.001;
			}      
			if(!s&&c&&a){
				shapeSide.sc=!shapeSide.sc;
			}           
			break;

		case 85/*'o.cornerpattern'*/: 
			if(!s&&!c&&!a){
				if(System.currentTimeMillis()-lastPressedTime[keyCode] < timings[timings.length-1]){
					shapeCorner.tc=true;
					shapeCorner.randomize();
					shapeCornerAngle.randomize();
					int idx=0;
					while(idx<timings.length && timings[idx]<System.currentTimeMillis()-lastPressedTime[keyCode])idx++;
					shapeCorner.tcIncPerSec=timings[idx];

				}
				else{
					if(!shapeCorner.tc){
						shapeCorner.randomize();
						shapeCornerAngle.randomize();
					}
					shapeCorner.tc=false;
				}
			}
			if(s&&!c&&!a){
				shapeCorner.tc=false;
				shapeCorner.v=16;
			}
			if(!s&&c&&!a){
				shapeCorner.tc=true;
				shapeCorner.tcIncPerSec+=shapeCorner.tcIncPerSec*0.25+0.001;
			}
			if(!s&&!c&&a){
				shapeCorner.tc=true;
				shapeCorner.tcIncPerSec-=shapeCorner.tcIncPerSec*0.25+0.001;
			}      
			if(!s&&c&&a){
				shapeCorner.sc=!shapeCorner.sc;
			}             
			break;

		case 74://'j':
			if(!s&&!c&&!a){
				if(System.currentTimeMillis()-lastPressedTime[keyCode] < timings[timings.length-1]){
					shapeCornerAngle.tc=true;
					shapeCornerAngle.randomize();
					int idx=0;
					while(idx<timings.length && timings[idx]<System.currentTimeMillis()-lastPressedTime[keyCode])idx++;
					shapeCornerAngle.tcIncPerSec=timings[idx];

				}
				else{
					if(!shapeCornerAngle.tc){
						shapeCornerAngle.randomize();
					}
					shapeCornerAngle.tc=false;
				}
			}
			if(s&&!c&&!a){
				shapeCornerAngle.tc=false;

			}
			if(!s&&c&&!a){
				shapeCornerAngle.tc=true;
				shapeCornerAngle.tcIncPerSec+=shapeCornerAngle.tcIncPerSec*0.25+0.001;
			}
			if(!s&&!c&&a){
				shapeCornerAngle.tc=true;
				shapeCornerAngle.tcIncPerSec-=shapeCornerAngle.tcIncPerSec*0.25+0.001;
			}      
			if(!s&&c&&a){
				shapeCornerAngle.sc=!shapeCornerAngle.sc;
			}      
			break;
			
		case 75://'k':
			if(!s&&!c&&!a){
				if(System.currentTimeMillis()-lastPressedTime[keyCode] < timings[timings.length-1]){
					shapeSideAngle.tc=true;
					shapeSideAngle.randomize();
					int idx=0;
					while(idx<timings.length && timings[idx]<System.currentTimeMillis()-lastPressedTime[keyCode])idx++;
					shapeSideAngle.tcIncPerSec=timings[idx];

				}
				else{
					if(!shapeSideAngle.tc){
						shapeSideAngle.randomize();
					}
					shapeSideAngle.tc=false;
				}
			}
			if(s&&!c&&!a){
				shapeSideAngle.tc=false;

			}
			if(!s&&c&&!a){
				shapeSideAngle.tc=true;
				shapeSideAngle.tcIncPerSec+=shapeSideAngle.tcIncPerSec*0.25+0.001;
			}
			if(!s&&!c&&a){
				shapeSideAngle.tc=true;
				shapeSideAngle.tcIncPerSec-=shapeSideAngle.tcIncPerSec*0.25+0.001;
			}      
			if(!s&&c&&a){
				shapeSideAngle.sc=!shapeSideAngle.sc;
			}      
			break;
			
		case 76://'l':
			if(!s&&!c&&!a){
				if(System.currentTimeMillis()-lastPressedTime[keyCode] < timings[timings.length-1]){
					shapeCenterAngle.tc=true;
					shapeCenterAngle.randomize();
					int idx=0;
					while(idx<timings.length && timings[idx]<System.currentTimeMillis()-lastPressedTime[keyCode])idx++;
					shapeCenterAngle.tcIncPerSec=timings[idx];

				}
				else{
					if(!shapeCenterAngle.tc){
						shapeCenterAngle.randomize();
					}
					shapeCenterAngle.tc=false;
				}
			}
			if(s&&!c&&!a){
				shapeCenterAngle.tc=false;

			}
			if(!s&&c&&!a){
				shapeCenterAngle.tc=true;
				shapeCenterAngle.tcIncPerSec+=shapeCenterAngle.tcIncPerSec*0.25+0.001;
			}
			if(!s&&!c&&a){
				shapeCenterAngle.tc=true;
				shapeCenterAngle.tcIncPerSec-=shapeCenterAngle.tcIncPerSec*0.25+0.001;
			}      
			if(!s&&c&&a){
				shapeCenterAngle.sc=!shapeCenterAngle.sc;
			}             
			break;

		}

	}


}
