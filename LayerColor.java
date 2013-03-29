import java.awt.Color;

import controlP5.Group;
import controlP5.Tab;


public abstract class LayerColor extends Layer {

	//colors
	public FloatParameter fgH  = new FloatParameter(this,0,0,1,FloatParameter.WRAP,"fgH");
	public FloatParameter fgS  = new FloatParameter(this,0,0,1,"fgS");
	public FloatParameter fgB  = new FloatParameter(this,0,0,1,"fgB");
	public FloatParameter fgAlpha  = new FloatParameter(this,1,0,1,"fgAlpha");
	public FloatParameter bgH  = new FloatParameter(this,0,0,1,FloatParameter.WRAP,"bgH");
	public FloatParameter bgS  = new FloatParameter(this,0,0,1,"bgS");
	public FloatParameter bgB  = new FloatParameter(this,0,0,1,"bgB");
	public FloatParameter bgAlpha  = new FloatParameter(this,1,0,1,"bgAlpha");

	//moves
	public FloatParameter gradient  = new FloatParameter(this,0f,0f,1f,"gradient");


	//used only for rendering :
	public int foregroundR,foregroundG,foregroundB,backgroundR,backgroundG,backgroundB;
	static public int timings[];
	static{
		int i=0;
		timings = new int[6];
		timings[i] = 60000/Config.BPM/32;
		i++;
		timings[i] = 60000/Config.BPM/16;
		i++;
		timings[i] = 60000/Config.BPM/4;
		i++;
		timings[i] = 60000/Config.BPM/2;
		i++;
		timings[i] = 60000/Config.BPM;
		i++;
		timings[i] = 60000/Config.BPM*2;
		i++;
	}
	
	public static String[] scrollModeNames={"0","1","10","50"};
	public static int[] scrollModes={0,1,10,50};
	public int width,height;

	protected LayerColor(Application parent){
		super(parent);
		this.width=Config.PATTERN_WIDTH;
		this.height=Config.PATTERN_WIDTH;
	}

	public int totalWidth(){return 4*width;}
	public int totalHeight(){return 2*height;}

	public void HSBtoRGB(){
		HSBtoRGB(0,0,0);
	}
	public void HSBtoRGB(float modH,float modS,float modB){
		float h = fgH.v()+modH;
		while(h<0) h+=1;
		while(h>1) h-=1;
		
		float s = fgS.v()+modS;
		while(s>1) s=1;
		
		float b = fgB.v()+modB;
		while(b<0) b=0;
		while(b>1) b=1;
		
		
		Color rgb = new Color(Color.HSBtoRGB(h,s,b));
		foregroundR = rgb.getRed();
		foregroundG = rgb.getGreen();
		foregroundB = rgb.getBlue(); 
		rgb = new Color(Color.HSBtoRGB(bgH.v(),bgS.v(),bgB.v()));
		backgroundR = rgb.getRed();
		backgroundG = rgb.getGreen();
		backgroundB = rgb.getBlue();
	}


	public void changeBackground(float hueModifier, float saturationModifier, float brightnessModifier){
		changeBackground(hueModifier, saturationModifier, brightnessModifier,0 );
	}


	public void changeBackground(float hueModifier, float saturationModifier, float brightnessModifier,float alphaModifier){
		bgH.v += hueModifier; // .58333 
		bgS.v += saturationModifier; // .66667 
		bgB.v += brightnessModifier ; // .6 
		bgAlpha.v += alphaModifier ; // .6 
		if(bgH.v>1)bgH.v-=1;     
		if(bgH.v<0)bgH.v+=1;
		if(bgS.v>1)bgS.v=1;     
		if(bgS.v<0)bgS.v=0;
		if(bgB.v>1)bgB.v=1;     
		if(bgB.v<0)bgB.v=0;
		if(bgAlpha.v>1)bgAlpha.v=1;     
		if(bgAlpha.v<0)bgAlpha.v=0;
	}

	public void randomizeBackground(float hueModifier, float saturationModifier, float brightnessModifier){
		bgH.v+=(Math.random()-0.5)*2*hueModifier;// random(-hueModifier,hueModifier);
		bgS.v+=(Math.random()-0.5)*2*saturationModifier;//random(-saturationModifier,saturationModifier);
		bgB.v+=(Math.random()-0.5)*2*brightnessModifier;//random(-brightnessModifier,brightnessModifier);
		if(bgH.v>1)bgH.v-=1;     
		if(bgH.v<0)bgH.v+=1;
		if(bgS.v>1)bgS.v=1;     
		if(bgS.v<0)bgS.v=0;
		if(bgB.v>1)bgB.v=1;     
		if(bgB.v<0)bgB.v=0;
	}
	public void randomBackground(){
		bgH.randomize();
		bgS.randomize();
		bgB.randomize();
	}
	public void changeColor(float hueModifier, float saturationModifier, float brightnessModifier){
		changeColor(hueModifier, saturationModifier, brightnessModifier,0 );
	}
	public void changeColor(float hueModifier, float saturationModifier, float brightnessModifier,float alphaModifier){
		fgH.v += hueModifier;
		fgS.v += saturationModifier;
		fgB.v += brightnessModifier ;
		fgAlpha.v += alphaModifier ;
		if(fgH.v>1)fgH.v-=1;     
		if(fgH.v<0)fgH.v+=1;
		if(fgS.v>1)fgS.v=1;     
		if(fgS.v<0)fgS.v=0;
		if(fgB.v>1)fgB.v=1;     
		if(fgB.v<0)fgB.v=0;
		if(fgAlpha.v>1)fgAlpha.v=1;     
		if(fgAlpha.v<0)fgAlpha.v=0;
	}
	public void randomizeColor(float hueModifier, float saturationModifier, float brightnessModifier){
		bgH.v+=(Math.random()-0.5)*2*hueModifier;// random(-hueModifier,hueModifier);
		bgS.v+=(Math.random()-0.5)*2*saturationModifier;//random(-saturationModifier,saturationModifier);
		bgB.v+=(Math.random()-0.5)*2*brightnessModifier;//random(-brightnessModifier,brightnessModifier);   if(fgH.v>1)fgH.v-=1;     
		if(fgH.v<0)fgH.v+=1;
		if(fgS.v>1)fgS.v=1;     
		if(fgS.v<0)fgS.v=0;
		if(fgB.v>1)fgB.v=1;     
		if(fgB.v<0)fgB.v=0;
	}
	public void randomColor(){
		fgH.randomize();
		fgS.randomize();
		fgB.randomize();
	}

	public int buildUI(GUIController controller, int x, int y, Tab tab, Group group) {
		String layerType = this.getType();
		//Gradient
		int h=controller.h;
		int hMargin = controller.hMargin;
		//Foreground Color
		y+=controller.addColorGroup(layerType,"Foreground Color","fg",x,y,tab,group);		
		//Background Color
		y+=controller.addColorGroup(layerType,"Background Color","bg",x,y,tab,group);

		y+=h+hMargin;
		controller.addControlFloatMoveParameter(layerType,"gradient",-10,10, x, y, tab,group);  
		y+=h+hMargin;
		
		return(y);

	}


	public void keyEvent(int key, int keyCode, char ch, boolean a, boolean c, boolean s,boolean win,boolean apl,long[] lastPressedTime) {

		switch(keyCode){
		case 89/*'y. gradient'*/: 
			if(!s&&!c&&!a){
				if(System.currentTimeMillis()-lastPressedTime[keyCode] < timings[timings.length-1]){
					gradient.tc=true;
					int idx=0;
					while(idx<timings.length && timings[idx]<System.currentTimeMillis()-lastPressedTime[keyCode])idx++;
					gradient.tcIncPerSec=500.0f/(float)timings[idx];
				}
				else{
					gradient.tc=!gradient.tc;
				}
			}
			if(s&&!c&&!a){
				gradient.tc=false;
				gradient.setV(0);
			}
			if(!s&&c&&!a){
				gradient.tc=true;
				if(gradient.tcIncPerSec>=0)gradient.tcIncPerSec-=gradient.tcIncPerSec*0.25+1;
				if(gradient.tcIncPerSec<0)gradient.tcIncPerSec+=gradient.tcIncPerSec*0.25+1;
			}
			if(!s&&!c&&a){
				gradient.tc=true;
				if(gradient.tcIncPerSec>=0)gradient.tcIncPerSec+=gradient.tcIncPerSec*0.25+1;
				if(gradient.tcIncPerSec<0)gradient.tcIncPerSec-=gradient.tcIncPerSec*0.25+1;
			}         
			if(!s&&c&&a){
				gradient.sc=!gradient.sc;
			}      
			break;
			
		case 81/*'q' hue*/:
			if(!s&&!c&&!a)changeColor(-0.01f,0,0);
			if(s&&!c&&!a)changeBackground(-0.01f,0,0);
			if(!s&&c&&a){fgH.sc=!fgH.sc;}  
			if(s&&c&&a){bgH.sc=!bgH.sc;}  
			break;
		case 83/*'s' hue*/:
			if(!s&&!c&&!a)changeColor(+0.01f,0,0); 
			if(s&&!c&&!a)changeBackground(+0.01f,0,0); 
			if(!s&&c&&a){fgH.sc=!fgH.sc;}  
			if(s&&c&&a){bgH.sc=!bgH.sc;}  
			break;

		case 88/*'x: black*/:
			if(!apl && !s && !c && !a)changeColor(0,0,-1);
			if(!apl &&  s && !c && !a)changeBackground(0,0,-1);
			break;
		case 87/*'w: white */:
			if(!apl && !s && !c  && !a)changeColor(0,-1,+1);
			if(!apl && s  && !c  && !a)changeBackground(0,-1,+1);
			break;

		case 70/*'f' brightness*/:
			if(!s&&!c&&!a)changeColor(0,0,+0.02f); 
			if(s&&!c&&!a)changeBackground(0,0,+0.02f);
			if(!s&&c&&a){fgB.sc=!fgB.sc;}  
			if(s&&c&&a){bgB.sc=!bgB.sc;}  
			break;
		case 86/*'v' brightness*/:
			if(!apl&&!s&&!c&&!a)changeColor(0,0,-0.02f); 
			if(!apl&&s&&!c&&!a)changeBackground(0,0,-0.02f); 
			if(!apl&&!s&&c&&a){fgB.sc=!fgB.sc;}  
			if(!apl&&s&&c&&a){bgB.sc=!bgB.sc;}         
			break;

		case 68/*'d' saturation*/:
			if(!s&&!c&&!a)changeColor(0,+0.02f,0); 
			if(s&&!c&&!a)changeBackground(0,+0.02f,0);
			if(!s&&c&&a){fgS.sc=!fgS.sc;}  
			if(s&&c&&a){bgS.sc=!bgS.sc;}       
			break;
		case 67/*'c' saturation*/:
			if(!apl&&!s&&!c&&!a)changeColor(0,-0.02f,0); 
			if(!apl&&s&&!c&&!a)changeBackground(0,-0.02f,0);
			if(!apl&&!s&&c&&a){fgS.sc=!fgS.sc;}  
			if(!apl&&s&&c&&a){bgS.sc=!bgS.sc;}          
			break;
		case 71://'g':
			if(!s&&!c&&!a)changeColor(0,0,0,0.02f); 
			if(s&&!c&&!a)changeBackground(0,0,0,0.02f);
			if(!s&&c&&a){fgAlpha.sc=!fgAlpha.sc;}  
			if(s&&c&&a){bgAlpha.sc=!bgAlpha.sc;}          
			break;
		case 66://'b':
			if(!s&&!c&&!a)changeColor(0,0,0,-0.02f); 
			if(s&&!c&&!a)changeBackground(0,0,0,-0.02f);
			if(!s&&c&&a){fgAlpha.sc=!fgAlpha.sc;}  
			if(s&&c&&a){bgAlpha.sc=!bgAlpha.sc;}          
			break;

		}

	}


}
