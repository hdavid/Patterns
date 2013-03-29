
import controlP5.Group;
import controlP5.Tab;


public abstract class LayerMovement extends LayerColor {


	public FloatParameter angle     = new FloatParameter(this,0f,0f,2f*(float)Math.PI,FloatParameter.WRAP,"angle");
	public FloatParameter rotate    = new FloatParameter(this,0f,0f,2f*(float)Math.PI,FloatParameter.WRAP,"rotate");
	public FloatParameter zoom      = new FloatParameter(this,4f,1f,50f,"zoom");
	public FloatParameter scrollX   = new FloatParameter(this,0f,0f,this.width,FloatParameter.WRAP,"scrollX");
	public FloatParameter scrollY   = new FloatParameter(this,0f,0f,this.height,FloatParameter.WRAP,"scrollY");
	public ListParameter scrollMode = new ListParameter(this,1,LayerNineBlockPattern.scrollModes,LayerNineBlockPattern.scrollModeNames,"scrollMode");
	public FloatParameter size      = new FloatParameter(this,1f,0f,4f,"size");

	//used only for rendering :
	//public int foregroundR,foregroundG,foregroundB,backgroundR,backgroundG,backgroundB;
	
	public static String[] scrollModeNames={"0","1","10","50"};
	public static int[] scrollModes={0,1,10,50};
	public int width,height;

	protected LayerMovement(Application parent){
		super(parent);
		this.width=Config.PATTERN_WIDTH;
		this.height=Config.PATTERN_WIDTH;
		angle.tcIncPerSec=0;
		rotate.tcIncPerSec=0;
		zoom.bumpChange=0.02f;
		zoom.tcIncPerSec=0;    
		scrollX.scChange=0.1f;
		scrollX.tcIncPerSec=0;    
		scrollY.scChange=0.1f;
		scrollY.tcIncPerSec=0;           
		scrollX.bumpChange=0.1f;
		scrollY.bumpChange=0.1f;
	}

	public int totalWidth(){return 4*width;}
	public int totalHeight(){return 2*height;}

	

	public int buildUI(GUIController controller, int x, int y, Tab tab, Group group) {
		String layerType = this.getType();
		y+=super.buildUI(controller,x,y,tab,group);
		//Gradient
		int h=controller.h;
		int hMargin = controller.hMargin;
		//Moves
		controller.addControlNamesMoves(layerType,"names",x,y,tab,group);
		y+=h+hMargin;
		controller.addControlFloatMoveParameter(layerType,"scrollX",-500,500, x, y, tab,group);    
		y+=h+hMargin;
		controller.addControlFloatMoveParameter(layerType,"scrollY",-500,500, x, y, tab,group);
		y+=h+hMargin*2;
		controller.addControlFloatMoveParameter(layerType,"angle",-10,10, x, y, tab,group);    
		y+=h+hMargin*2;
		controller.addControlFloatMoveParameter(layerType,"rotate",2,2, x, y, tab,group);  
		y+=h+hMargin*2;
		controller.addControlFloatMoveParameter(layerType,"zoom",-3,3, x, y, tab,group);    
		y+=h+hMargin;
		y+=h+hMargin;
		controller.addControlFloatParameter(layerType,"size",0,4, x, y, tab,group); 
		y+=h+hMargin*2;
		controller.addControlFloatMoveParameter(layerType,"number",0,50, x, y, tab,group); 
		y+=h+hMargin;
		y+=h+hMargin;

		return(y);

	}


	public void keyEvent(int key, int keyCode, char ch, boolean a, boolean c, boolean s,boolean win,boolean apl,long[] lastPressedTime) {
		super.keyEvent(key,keyCode,ch,a,c,s,win,apl,lastPressedTime);
		switch(keyCode){
		case 37/*'<-'*/:
			if(s){
				scrollX.tc=!scrollX.tc;
			} 
			break;
		case 39/*'->'*/:
			if(!s&&!c&&!a){
				//aplayers[apcurrentLayer].scrollX.tcIncPerSec+=parent.layers[parent.currentLayer].scrollY.tcIncPerSec*0.25+0.05;
				//parent.layers[parent.currentLayer].scrollX.tc=true;
				if(parent.globalController!=null)parent.globalController.incSpeed();
			}
			if(s){
				scrollX.tc=!scrollY.tc;
			} 
			break;    
		case 38/*'up'*/:
			if(!s&&!c&&!a){
				//parent.layers[parent.currentLayer].scrollY.tcIncPerSec-=parent.layers[parent.currentLayer].scrollY.tcIncPerSec*0.25+0.05;
				//parent.layers[parent.currentLayer].scrollY.tc=true;
				if(parent.globalController!=null)parent.globalController.incBump();
			}
			if(s){
				scrollY.tc=!scrollY.tc;
			} 
			break;
		case 40/*'down'*/:
			if(!s&&!c&&!a){
				//parent.layers[parent.currentLayer].scrollY.tcIncPerSec+=parent.layers[parent.currentLayer].scrollY.tcIncPerSec*0.25+0.05;
				//parent.layers[parent.currentLayer].scrollY.tc=true;
				if(parent.globalController!=null)parent.globalController.decBump();
			}
			if(s){
				scrollY.tc=!scrollY.tc;
			} 
			break; 

		case 69/*'e'*/: 
			if(!s&&!c&&!a){
				scrollX.tc=!scrollX.tc;
				scrollY.tc=!scrollY.tc;
			}
			if(s&&!c&&!a){
				scrollX.tc=false;
				scrollY.tc=false;
			}
			if(!s&&c&&!a){
				scrollX.tcIncPerSec+=scrollX.tcIncPerSec*0.25+0.01;
				scrollY.tcIncPerSec+=scrollY.tcIncPerSec*0.25+0.01;
			}
			if(!s&&!c&&a){
				scrollX.tcIncPerSec-=scrollX.tcIncPerSec*0.25+0.01;
				scrollY.tcIncPerSec-=scrollY.tcIncPerSec*0.25+0.01;
			}
			if(!s&&c&&a){
				scrollX.sc=!scrollX.sc;
				scrollY.sc=!scrollY.sc;
			}
			break;

		case 61/*'=' movment sound control*/: 
		case 47/*MAC '=' movment sound control*/: 
			if(!s&&!c&&!a){
				scrollX.sc=true;
				scrollY.sc=true;
				angle.sc=true;
				zoom.sc=true;

				System.out.println("Move Sound control ON for layer "+(parent.currentLayer+1));
			}
			if(s&&!c&&!a){
				scrollX.sc=false;
				scrollY.sc=false;
				angle.sc=false;
				zoom.sc=false;

				System.out.println("Move Sound control OFF for layer "+(parent.currentLayer+1));
			}
			break;

		case 82/*'R.otate'*/: 
			if(!s&&!c&&!a){
				angle.tc=!angle.tc;
			}
			if(s&&!c&&!a){
				angle.tc=false;
			}
			if(!s&&c&&!a){
				angle.tc=true;
				angle.tcIncPerSec-=angle.tcIncPerSec*0.25+0.001;
			}
			if(!s&&!c&&a){
				angle.tc=true;
				angle.tcIncPerSec+=angle.tcIncPerSec*0.25+0.001;
			}     
			if(!s&&c&&a){
				angle.sc=!angle.sc;
			}       
			break;

		case 90/*'Z.oom'*/:             
			if(!s&&!c&&!a){
				zoom.tc=!zoom.tc;
			}
			if(s&&!c&&!a){
				zoom.tc=false;
			}
			if(!s&&c&&!a){
				zoom.tc=true;
				if(zoom.tcIncPerSec>=0)zoom.tcIncPerSec-=zoom.tcIncPerSec*0.25+0.001;
				if(zoom.tcIncPerSec<0)zoom.tcIncPerSec+=zoom.tcIncPerSec*0.25+0.001;
			}
			if(!s&&!c&&a){
				zoom.tc=true;
				if(zoom.tcIncPerSec>=0)zoom.tcIncPerSec+=zoom.tcIncPerSec*0.25+0.001;
				if(zoom.tcIncPerSec<0)zoom.tcIncPerSec-=zoom.tcIncPerSec*0.25+0.001;
			}
			if(!s&&c&&a){
				zoom.sc=!zoom.sc;
			} 
			break;
		}

	}


}
