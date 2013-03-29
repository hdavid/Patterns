
import java.util.HashMap;
import java.util.Map;



import rwmidi.*;

public class MIDILaunchpadController implements Controller {

	Map<Integer,Boolean> onBeat= new HashMap<Integer,Boolean>();
	protected boolean enabled = true;
	protected Application app;
	private MidiOutput output;
	protected boolean debug=false;
	protected Mode mode=Mode.Pattern;
	protected int x=0;
	protected int y=0;
	public static enum Mode{Pattern,Color,Incremental,Moves};
	private long lastUpdate=System.currentTimeMillis();
	private long lastFullUpdate=System.currentTimeMillis();

	private int[][] matrix = new int[8][8];
	private int[][] matrixBackBuffer = null;

	protected int SCENE1=8, SCENE2=24, SCENE3=40, SCENE4=56, SCENE5=72, SCENE6=88, SCENE7=104, SCENE8=120;
	private int[] scenesCC = new int[]{SCENE1, SCENE2,SCENE3,SCENE4,SCENE5,SCENE6,SCENE7,SCENE8};
	private int[] scenes = new int[8];
	private int[] scenesBackBuffer=null;

	protected int UP=0x68, DOWN=0x69, LEFT=0x6A, RIGHT=0x6B, SESSION=0x6C, USER1=0x6D, USER2=0x6E, MIXER=0x6F;
	private int[] topButtonsCC = new int[]{UP,DOWN,LEFT,RIGHT,SESSION,USER1,USER2,MIXER};
	private int[] topButtons = new int[8];
	private int[] topButtonsBackBuffer=null;

	public MIDILaunchpadController(Application app) {
		this.app = app;
		if(Config.RWMIDI){
			System.out.println("Midi Launchpad Controller : RWMidi.");
			for(MidiInputDevice in : RWMidi.getInputDevices()){
				if(in.getName().matches(".*Launchpad.*")){
					System.out.println("  opening  midi in : "+in.getName());
					in.createInput(this);
				}else{
					//System.out.println("  ignoring midi in : "+in.getName());
				}
			}
			for(MidiOutputDevice out : RWMidi.getOutputDevices()){
				if(out.getName().matches(".*Launchpad.*")){
					System.out.println("  opening  midi output : "+out.getName());
					output = out.createOutput();
				}else{
					//System.out.println("  ignoring midi output : "+out.getName());
				}
			}
		}
	}


	protected void updateMatrix(){

		Layer layer = this.app.layers[this.app.currentLayer];

		//clear matrix
		for(int i=0;i<8;i++){
			for(int j=0;j<8;j++){
				this.matrix[j][i]=this.color(0,0,false);
			}
		}

		//global Layer select and enable
		for(int i=0;i<8;i++){
			if(this.app.layers[i]==null){
				this.matrix[7][i]=this.color(0,0,false);
			}else if(this.app.layerEnabled[i]){
				this.matrix[7][i]=this.color(3,3,false);
			}else{
				this.matrix[7][i]=this.color(1,1,false);
			}
		}

		if(layer!=null){
			if(mode==Mode.Pattern){
				if(layer instanceof LayerNineBlockPattern){
					LayerNineBlockPattern p = (LayerNineBlockPattern)layer;
					for(int i=0;i<8;i++){
						this.matrix[0][i]=this.color(p.shapeCorner.v==LayerNineBlockPattern.shapesNumbers[i]?3:1,0,false);
						this.matrix[1][i]=this.color(p.shapeCorner.v==LayerNineBlockPattern.shapesNumbers[i+8]?3:1,0,false);
						this.matrix[3][i]=this.color(p.shapeSide.v==LayerNineBlockPattern.shapesNumbers[i]?3:1,0,false);
						this.matrix[4][i]=this.color(p.shapeSide.v==LayerNineBlockPattern.shapesNumbers[i+8]?3:1,0,false);
					}
					for(int i=0;i<4;i++){
						this.matrix[2][i]=this.color(p.shapeCornerAngle.v/90==i?3:1,0,false);
						this.matrix[5][i]=this.color(p.shapeSideAngle.v/90==i?3:1,0,false);
						this.matrix[6][i]=this.color(p.shapeCenter.v==LayerNineBlockPattern.centerShapesNumbers[i]?3:1,0,false);
						this.matrix[6][i+4]=this.color(p.shapeCenterAngle.v/90==i?3:1,0,false);
					}

					this.matrix[2][6]=this.color(0,p.shapeCornerAngle.sc?3:1,false);
					this.matrix[2][7]=this.color(0,p.shapeCorner.sc?3:1,false);

					this.matrix[5][4]=this.color(0,p.shapeCenterAngle.sc?3:1,false);
					this.matrix[5][5]=this.color(0,p.shapeCenter.sc?3:1,false);

					this.matrix[5][6]=this.color(0,p.shapeSideAngle.sc?3:1,false);
					this.matrix[5][7]=this.color(0,p.shapeSide.sc?3:1,false);

				}
				if(layer instanceof LayerBars){
					LayerBars p = (LayerBars)layer;
					this.matrix[2][4]=this.color(p.bar1.v==1?3:1,p.bar1.v==1?3:1,false);
					this.matrix[2][5]=this.color(p.bar2.v==1?3:1,p.bar2.v==1?3:1,false);
				}


			}else if(mode==Mode.Color){
				if(layer instanceof LayerColor){
					LayerColor p = (LayerColor)layer;

					FloatParameter parameters[] = new FloatParameter[]{p.fgH,p.fgS,p.fgB,p.fgAlpha};
					for(int i=0;i<parameters.length;i++){
						if(parameters[i]!=null){
							for(int j=0;j<8;j++){
								this.matrix[i][j]=this.color(parameters[i].v>(8-j-2)/7f?3:1,0,false);
							}
						}
					}

					parameters = new FloatParameter[]{p.bgH,p.bgS,p.bgB,p.bgAlpha,p.fgH,p.fgS,p.fgB,p.fgAlpha};
					for(int i=0;i<parameters.length;i++){
						if(parameters[i]!=null){
							this.matrix[4][i]=this.color(0,parameters[i].sc?3:1,false);
							this.matrix[5][i]=this.color(0,parameters[i].bump?3:1,false);
							this.matrix[6][i]=this.color(0,parameters[i].modulate?3:1,false);	
						}
					}
				}

			}else if(mode==Mode.Incremental){

				if(layer instanceof LayerMovementIncremental){
					LayerMovementIncremental p = (LayerMovementIncremental)layer;
					FloatParameter parameters[] = new FloatParameter[]{p.ifgH,p.ifgS,p.ifgB,p.ifgAlpha,null,null,null,null};
					for(int i=0;i<parameters.length;i++){
						if(parameters[i]!=null){
							if(i>4){
								this.matrix[3][i]=this.color(parameters[i].v!=0?3:1,parameters[i].v!=0?3:1,false);
							}
							this.matrix[4][i]=this.color(0,parameters[i].sc?3:1,false);
							this.matrix[5][i]=this.color(0,parameters[i].bump?3:1,false);
							this.matrix[6][i]=this.color(0,parameters[i].modulate?3:1,false);
						}
					}

					parameters = new FloatParameter[]{p.iscrollX,p.iscrollY,p.iangle,p.irotate,p.izoom,p.isize};
					for(int i=0;i<parameters.length;i++){
						if(parameters[i]!=null){
							this.matrix[0][i]=this.color(0,parameters[i].sc?3:1,false);
							this.matrix[1][i]=this.color(0,parameters[i].bump?3:1,false);
							this.matrix[2][i]=this.color(0,parameters[i].modulate?3:1,false);
						}
					}
				}
			}else if(mode==Mode.Moves){
				if(layer instanceof LayerMovement){
					LayerMovement p = (LayerMovement)layer;
					FloatParameter parameters[] = new FloatParameter[]{p.scrollX,p.scrollY,p.angle,p.rotate,p.zoom,p.size};//,p.number};
					for(int i=0;i<parameters.length;i++){
						if(parameters[i]!=null){
							if(i!=5)
								this.matrix[0][i]=this.color(0,parameters[i].tc?3:1,false);
							this.matrix[1][i]=this.color(0,parameters[i].sc?3:1,false);
							this.matrix[2][i]=this.color(0,parameters[i].bump?3:1,false);
							this.matrix[3][i]=this.color(0,parameters[i].modulate?3:1,false);
							if(i!=5)
								this.matrix[4][i]=this.color(0,parameters[i].scAlter?3:1,false);	
						}
					}
				}

			}
		}
	}


	protected void updateTopButtons(){
		for(int i=0;i<4;i++){
			if(mode.ordinal()==i){
				this.topButtons[i+4]=this.color(3,3,false);
			}else{
				this.topButtons[i+4]=this.color(1,1,false);
			}
		}
		for(int i=0;i<6;i++){
			if(this.onBeat.get(i)!=null && this.onBeat.get(i)){
				this.onBeat.put(i,false);
				if(i==4||i==5){
					if(mode.ordinal()==i-4){
						this.topButtons[i]=this.color(3,2,false);
					}else{
						this.topButtons[i]=this.color(3,1,false);
					}	
				}else{
					this.topButtons[i]=this.color(1,0,false);
				}
			}else{
				if(i==4||i==5){
					if(mode.ordinal()==i-4){
						this.topButtons[i]=this.color(2,2,false);
					}else{
						this.topButtons[i]=this.color(1,1,false);
					}	
				}else{
					this.topButtons[i]=this.color(0,0,false);
				}
			}
		}
	}


	protected void updateSceneButtons(){
		for(int i=0;i<8;i++){
			if(this.app.currentLayer==i){
				if(this.app.layers[i]==null){
					this.scenes[i]=this.color(2,0,this.app.currentLayer==i);
				}else{
					this.scenes[i]=this.color(0,3,this.app.currentLayer==i);
				}
			}else{
				if(this.app.layers[i]==null){
					this.scenes[i]=this.color(0,0,this.app.currentLayer==i);
				}else{
					this.scenes[i]=this.color(0,1,this.app.currentLayer==i);
				}
			}
		}
	}


	protected void topButtonValue(int x,int value){
		if(value!=0){
			if(x==UP){
				if(this.y<1){
					if(this.app.soundController !=null)
						this.app.soundController.setOnBeat(0);
				}
			}else if(x==DOWN){
				if(this.app.soundController !=null)
					this.app.soundController.setOnBeat(1);
			}else if(x==LEFT){
				if(this.app.soundController !=null)
					this.app.soundController.setOnBeat(2);
			}else if(x==RIGHT){
				this.app.guiController.switchTab();
				//if(this.app.soundController !=null)
				//	this.app.soundController.setOnBeat(3);
			}else if(x==SESSION){
				this.mode=Mode.Pattern;
			}else if(x==USER1){
				this.mode=Mode.Color;
			}else if(x==USER2){
				this.mode=Mode.Incremental;
			}else if(x==MIXER){
				this.mode=Mode.Moves;
			}
		}
	}

	protected void matrixValue(int x,int y,int value){
		//enable / create pattern
		if(x==8){
			if(this.app.layers[y-1]==null){
				app.newLayerInLayer(y-1);
			}else{
				this.app.layerEnabled[y-1]=!this.app.layerEnabled[y-1];
			}
		}


		Layer layer = this.app.layers[this.app.currentLayer];


		if(layer!=null){
			if(mode==Mode.Pattern){
				if(layer instanceof LayerNineBlockPattern){
					LayerNineBlockPattern p = (LayerNineBlockPattern)layer;
					if(x==1) p.shapeCorner.v=LayerNineBlockPattern.shapesNumbers[y-1];
					if(x==2) p.shapeCorner.v=LayerNineBlockPattern.shapesNumbers[y+7];
					if(x==3 && y<=4) p.shapeCornerAngle.v=(y-1)*90;
					if(x==3 && y==7) p.shapeCornerAngle.sc=!p.shapeCornerAngle.sc;
					if(x==3 && y==8) p.shapeCorner.sc=!p.shapeCorner.sc;

					if(x==4) p.shapeSide.v=LayerNineBlockPattern.shapesNumbers[y-1];
					if(x==5) p.shapeSide.v=LayerNineBlockPattern.shapesNumbers[y+7];
					if(x==6 && y<=4) p.shapeSideAngle.v=(y-1)*90;
					if(x==6 && y==7) p.shapeSideAngle.sc=!p.shapeSideAngle.sc;
					if(x==6 && y==8) p.shapeSide.sc=!p.shapeSide.sc;

					if(x==7 && y<=4) p.shapeCenter.v=LayerNineBlockPattern.centerShapesNumbers[y-1];
					if(x==7 && y>=4) p.shapeCenterAngle.v=(y-1-4)*90;
					if(x==6 && y==5) p.shapeCenterAngle.sc=!p.shapeCenterAngle.sc;
					if(x==6 && y==6) p.shapeCenter.sc=!p.shapeCenter.sc;
				}
					if(layer instanceof LayerBars){
						LayerBars p = (LayerBars)layer;

						if(x==3){
						if(y==5) p.bar1.v=p.bar1.v==0?1:0;
						if(y==6) p.bar2.v=p.bar2.v==0?1:0;
					}
				}

			}else if(mode==Mode.Color){
				if(layer instanceof LayerColor){
					LayerColor p = (LayerColor)layer;
					FloatParameter parameters[] = new FloatParameter[]{p.fgH,p.fgS,p.fgB,p.fgAlpha};
					if((x-1)<parameters.length && parameters[x-1]!=null){
						parameters[x-1].v=(8-y)/7f;
					}

					parameters = new FloatParameter[]{p.bgH,p.bgS,p.bgB,p.bgAlpha,p.fgH,p.fgS,p.fgB,p.fgAlpha};
					if(parameters[y-1]!=null){
						if(x==5) parameters[y-1].sc=!parameters[y-1].sc;
						if(x==6) parameters[y-1].bump=!parameters[y-1].bump;
						if(x==7) parameters[y-1].modulate=!parameters[y-1].modulate;
					}
				}

			}else if(mode==Mode.Incremental){
				if(layer instanceof LayerMovementIncremental){
					LayerMovementIncremental p = (LayerMovementIncremental)layer;
					FloatParameter parameters[] = new FloatParameter[]{p.ifgH,p.ifgS,p.ifgB,p.ifgAlpha,null,null,null,null};
					if(parameters[y-1]!=null){

						if(x==4 && y>5)parameters[y-1].v=parameters[y-1].v>0.5?0:1;
						if(x==5)parameters[y-1].sc=!parameters[y-1].sc;
						if(x==6)parameters[y-1].bump=!parameters[y-1].bump;
						if(x==7)parameters[y-1].modulate=!parameters[y-1].modulate;
					}

					parameters = new FloatParameter[]{p.iscrollX,p.iscrollY,p.iangle,p.irotate,p.izoom,p.isize,null,null,null,null};
					if(parameters[y-1]!=null){
						if(x==1)parameters[y-1].sc=!parameters[y-1].sc;
						if(x==2)parameters[y-1].bump=!parameters[y-1].bump;
						if(x==3)parameters[y-1].modulate=!parameters[y-1].modulate;
					}
				}

			}else if(mode==Mode.Moves){
				if(layer instanceof LayerMovement){
					LayerMovement p = (LayerMovement)layer;
					FloatParameter parameters[] = new FloatParameter[]{p.scrollX,p.scrollY,p.angle,p.rotate,p.zoom,p.size};
					if(parameters[y-1]!=null){
						if(y-1!=5)
							if(x==1)parameters[y-1].tc=!parameters[y-1].tc;
						if(x==2)parameters[y-1].sc=!parameters[y-1].sc;
						if(x==3)parameters[y-1].bump=!parameters[y-1].bump;
						if(x==4)parameters[y-1].modulate=!parameters[y-1].modulate;
						if(y-1!=5)
							if(x==5)parameters[y-1].scAlter=!parameters[y-1].scAlter;
					}
				}
			}
		}
	}


	protected void sceneValue(int x,int value) {
		this.app.currentLayer=x-1;
		this.app.guiController.currentLayerChanged();
	}


	protected int color(int red,int green,boolean flashing){
		boolean buffering=false;
		int color = 16 * green + red;
		int flags = 12;
		if(flashing) flags=8;
		if(buffering) flags=0;
		return(color + flags);
	}

	protected void updateLaunchPad(){
		updateLaunchPad(false);
	}

	protected void updateLaunchPad(boolean force){
		if(output!=null){
			this.lastUpdate=System.currentTimeMillis();
			this.updateTopButtons();
			this.updateSceneButtons();
			this.updateMatrix();
			if(this.lastUpdate-this.lastFullUpdate>60 * 1000){
				this.lastFullUpdate=this.lastUpdate;
				force=true;
			}
			if(matrixBackBuffer==null || matrixBackBuffer==null || topButtonsBackBuffer==null){
				matrixBackBuffer = new int[8][8];
				scenesBackBuffer = new int[scenes.length];
				topButtonsBackBuffer = new int[topButtons.length];
				force=true;
			}
			for(int i=0;i<8;i++){
				try{
					if(topButtonsBackBuffer[i]!=topButtons[i] || force){
						topButtonsBackBuffer[i]=topButtons[i];
						output.sendController(0, topButtonsCC[i],topButtons[i]);
					}
					if(scenesBackBuffer[i]!=scenes[i] || force){
						scenesBackBuffer[i]=scenes[i];
						output.sendNoteOn(0, scenesCC[i],scenes[i]);
					}
					for(int j=0;j<8;j++){
						if(matrix[i][j]!=matrixBackBuffer[i][j] || force){
							matrixBackBuffer[i][j]=matrix[i][j];
							output.sendNoteOn(0, i+16*(j),matrix[i][j]);
						}
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void update(){
		if(enabled){
			if(System.currentTimeMillis()-lastUpdate>50){
				updateLaunchPad();
			}
		}
	}

	public void control(){}

	public void noteOnReceived(rwmidi.Note note){
		if(enabled){
			int pitch=note.getPitch();
			if(note.getVelocity()!=0){
				if(debug)System.out.println("midi note ON + channel:"+note.getChannel()+" pitch:"+note.getPitch()+" vel:"+note.getVelocity());
				if(pitch==SCENE1||pitch==SCENE2||pitch==SCENE3||pitch==SCENE4||pitch==SCENE5||pitch==SCENE6||pitch==SCENE7||pitch==SCENE8){
					sceneValue((pitch+8)/16,note.getVelocity());
				}else{
					matrixValue(1+pitch%16, 1+pitch/16, note.getVelocity());
				}
			}else{
				if(debug)System.out.println("midi note ON (with velocity 0) channel:"+note.getChannel()+" pitch:"+note.getPitch()+" vel:"+note.getVelocity());
			}
		}


	}


	public void noteOffReceived(rwmidi.Note note){
		if(enabled){
			if(debug)System.out.println("midi note OFF  channel:"+note.getChannel()+" pitch:"+note.getPitch()+" vel:"+note.getVelocity());
		}
	}


	public void controllerChangeReceived(rwmidi.Controller controller){
		if(enabled){
			topButtonValue(controller.getCC(),controller.getValue());
			if(debug)System.out.println("midi cc channel:"+controller.getChannel()+" cc:"+controller.getCC()+" value:"+controller.getValue());
		}
	}


	public void programChangeReceived(rwmidi.ProgramChange programChange){
		if(enabled){
		
		}
	}

	public void sysexReceived(rwmidi.SysexMessage sysexMessage){
		if(enabled){
			
		}
	}


}

