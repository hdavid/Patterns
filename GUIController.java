import java.awt.Color;

import java.util.HashMap;
import processing.core.PApplet;
import controlP5.*;

public class GUIController extends ControlP5Util  {

	private int dspW = 600;
	private int bandWidth = 0;

	public HashMap<Integer,Boolean> onBeat = new HashMap<Integer,Boolean>();
	public HashMap<Integer,Float> groupScores = new HashMap<Integer,Float>();
	public HashMap<Integer,Float> groupBeatSenses = new HashMap<Integer,Float>();
	private String currentController=null;

	public GUIController(Application app,boolean window,boolean visible){
		super(app, window, visible);
	}

	private void computeBanWidth(){
		this.bandWidth = (dspW-app.soundController.zoneEnabled().length+1)/app.soundController.zoneEnabled().length;
	}

	public void drawCustomUI(PApplet theApplet) {
		if(this.isVisible()){
			
			//draw sound groups
			for(Integer group : onBeat.keySet()){
				if(onBeat.get(group)!=null){
					int numGroup=onBeat.keySet().size();
					if(onBeat.get(group)){
						if(0==group){
							theApplet.fill(255,0,255);			
						}else{
							theApplet.fill(255,0,0);
						}
						theApplet.rect(dspW-5,theApplet.height-28-numGroup*(h+hMargin)+(group+2)*(h+hMargin),h,h);
						onBeat.put(group,false);
					}
				}
			}
			
			//paint DSP data
			if(app.soundController!=null){
				app.soundController.draw(theApplet);
			}
		}
	}

	/**
	 * Create all the controls !!
	 */
	public void setup(){

		/*for(Integer group : this.onBeat.keySet()){
			this.onBeat.put(group, false);
		}*/
		
		
		//CREATE TABS
		Tab globalTab=controlP5.getTab("global");
		
		addSlider(null,"layer0bgAlpha.v"     ,0,0,1,5,18,80,h,globalTab,null).setCaptionLabel("bgAlpha 1");
		
		//Top Buttons
		int y=3;  
		int x=300;
		addButton(null,"Save",x,y,30,h,globalTab,null);
		x+=30+wMargin;
		addButton(null,"Save All",x,y,40,h,globalTab,null);
		x+=40+wMargin;
		x+=wMargin;
		if(Config.OSC){
			addButton(null,"Send",x,y,45,h,globalTab,null);
			x+=45+wMargin;
			addButton(null,"SendAll",x,y,45,h,globalTab,null);
			x+=45+wMargin;
			x+=wMargin;
		}
		addButton(null,"Frame",x,y,30,h,globalTab,null);
		x+=30+wMargin;
		addLabel(null,"currentLayer","",x,y,globalTab,null);
		x+=12;
		addTextfield(null,"layerType","",x,y-2,80,(int)(h*1.5),globalTab,null).setCaptionLabel("");
		x+=80+wMargin; 
		addTextfield(null,"layerName","",x,y-2,80,(int)(h*1.5),globalTab,null).setCaptionLabel("");
		x+=80+wMargin; 
		addButton(null,"relocate",x,y,45,h,globalTab,null);
		x+=45+wMargin;
		addSlider(null,"frames"     ,0,0,80,x,y,60,h,globalTab,null).setCaptionLabel("FPS");
		x+=60+30+wMargin;
		addSlider(null,"refresh"     ,10,0,500,x,y,60,h,globalTab,null).setCaptionLabel("Gui(ms)");
		x+=90+wMargin; 
		addToggle(null,"LP",true,10,300,h,h,globalTab,null);
		
		
		//Layers
		x=wMargin;
		y=hMargin+30;
		addLabel(null,"Layers label","Layers",x,y,globalTab,null);
		y+=h+hMargin;
		x=5;
		for(int i=0;i<app.layers.length;i++){
			addToggle(null,"Layer Enabled "+i,((i==0)?true:false),x,y+i*(h+hMargin),h,h,globalTab,null).setCaptionLabel("");
			addButton(null,"layer"+(i)+"fgDsp", x+wMargin*4+h+3,y+i*(h+hMargin),h*2,h,globalTab,null).setCaptionLabel((1+i)+"");
		}    
		x=h+2*wMargin+1;  
		//layers
		String[] layerNames = new String[app.layers.length];
		int[] layerNumbers = new int[app.layers.length];
		for(int i=0;i<app.layers.length;i++){
			layerNames[i]="";
			layerNumbers[i]=(i);
		}
		addRadio(null,"Layer Selected",layerNames,x,y,null,null,globalTab,null).setLabel("");
		y=34;x=60;
		addRadio(null,"ghostLayer.v",new String[]{"","","","","","","","","","","","","","","",""},x,y,null,null,globalTab,null); 


		//Sound and FPS
		x=dspW+h+hMargin-5;
		y=windowH-(app.soundController.getGroups().size())*(h+hMargin)-50;
		addToggle(null,"autoBeatSense",true,x,y,h,h,globalTab,null).setCaptionLabel("");
		x+=h+wMargin;
		addLabel(null,"autoBeatSenseL","Auto Sense",x,y,globalTab,null);
		addSlider(null,"beatSenseSense"  ,1.1f,0.5f,5,x+15,y,120,null,globalTab,null).setCaptionLabel("");

		
		//SC groups
		x=dspW+h+hMargin-5;
		y=windowH-(app.soundController.getGroups().size())*(h+hMargin)-20;
		for(int i=0;i<app.soundController.getGroups().size();i++){
			addToggle(null,"bang-"+i,false ,x,y+(i)*(h+hMargin),h,h,globalTab,null).setCaptionLabel("");
			controlP5.getController("bang-"+i).setColorActive(app.color(255,0,255));
			addSlider(null,"score-"+i     ,0,0,20,x+50,y+(i)*(h+hMargin),w,null,globalTab,null).setCaptionLabel(""); 
			addSlider(null,"beatsense-"+i     ,0,0,20,x+50+h+w,y+(i)*(h+hMargin),w/3,null,globalTab,null).setCaptionLabel("");

		}
		x+=h+wMargin;
		addRadio(null,"scGroup",new String[]{"g","1","2","3","4","5"},x,y,null,null,globalTab,null);
		x+=h+wMargin;
		x=dspW+h+hMargin-5;
		y=windowH-(app.soundController.getGroups().size()+1)*(h+hMargin);
		addLabel(null,"SC Group : ","current : ",x,y-20,globalTab,null);
		x+=38+wMargin;
		addLabel(null,"currentController","",x,y-20,globalTab,null);
		y+=h+hMargin;

		
		//global Buttons
		x=dspW+h+hMargin-5;
		y=windowH-(app.soundController.getGroups().size())*(h+hMargin)-80;
		addButton(null,"bump+",x,y+h+hMargin,35,null,globalTab,null) ;     
		x+=40;
		addButton(null,"bump-",x,y+h+hMargin,35,null,globalTab,null)  ;
		x+=40+wMargin;
		addButton(null,"speed+",x,y+h+hMargin,35,null,globalTab,null) ;
		x+=40;
		addButton(null,"speed-",x,y+h+hMargin,35,null,globalTab,null)  ;
		y+=h+hMargin;


		//DSP Band Buttons
		if(app.soundController!=null && Config.DSP){
			if(bandWidth==0){
				computeBanWidth();
			}
			for(int i=0;i<app.soundController.zoneEnabled().length;i++){
				addToggle(null,"band"+i,true,10+1+(bandWidth+1)*i,windowH-13-20,bandWidth,7,globalTab,null).setCaptionLabel("");
			}
		}


		//LAYER TABS
		int tabX=80;
		int tabY=30;
		boolean first=false;
		for(String layerType : Config.LAYER_TYPES.split("[, ]+")){
			String tabName = first?"default":layerType;
			first=false;
			Tab tab = controlP5.getTab(tabName);
			tab.setTitle(layerType.replace("Layer", "").replace("NineBlockPattern","Pattern"));
			Layer layer;
			try {
				layer = Layer.newLayer(app, layerType);
				layer.buildUI(this, tabX, tabY,tab,null);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}

	}



	private void layerColors(){
		for(int i=0;i<app.layers.length;i++){
			Layer layer = app.layers[i];
			if(layer!=null){
				if(i==app.currentLayer){
					setColorControlColor(layer,"fg");
					setColorControlColor(layer,"bg");
					setColorControlColor(layer,"c1");
					setColorControlColor(layer,"c2");
					setColorControlColor(layer,"c3");
					setColorControlColor(layer,"c4");
				}
			}
			setLayerColorPreviewColor(layer,"fg","layer"+i+"fg");
		}
	}

	protected void setLayerColorPreviewColor(Layer layer, String name, String controlName){
		float h=0;
		float b=0;
		float s=0;
		if(layer!=null){
			FloatParameter H = layer.getFloatParameter(name+"H");
			if(H!=null){
				h=H.v();
			}
			FloatParameter S = layer.getFloatParameter(name+"S");
			if(S!=null){
				s=S.v();
			}
			FloatParameter B = layer.getFloatParameter(name+"B");
			if(B!=null){
				b=B.v();
			}
		}
		Color rgb = new Color(Color.HSBtoRGB(h,s,b));
		controlP5.Controller<?> c;
		if((c=getController(null,controlName+"Dsp"))!=null){
			int col = app.color(rgb.getRed(),rgb.getGreen(),rgb.getBlue());
			c.setColorForeground( col);
			c.setColorActive(col );
			c.setColorBackground(col);	
		}
	}

	protected void setColorControlColor(Layer layer, String name){
		float h=0;
		FloatParameter H = layer.getFloatParameter(name+"H");
		if(H!=null){
			h=H.v;
		}
		float s=0;
		FloatParameter S = layer.getFloatParameter(name+"S");
		if(S!=null){
			s=S.v;
		}
		float b=0;
		FloatParameter B = layer.getFloatParameter(name+"B");
		if(B!=null){
			b=B.v;
		}

		Color rgb = new Color(Color.HSBtoRGB(h,s,b));
		controlP5.Controller<?> c;
		if((c=getController(layer.getType(),name+"Dsp"))!=null){
			int col = app.color(rgb.getRed(),rgb.getGreen(),rgb.getBlue());
			c.setColorForeground(col);
			c.setColorActive(col);
			c.setColorBackground(col);	
		}
		rgb = new Color(Color.HSBtoRGB(h,1.0f,1.0f));
		if((c= getController(layer.getType(),name+"H.v"))!=null){
			int col = app.color(rgb.getRed(),rgb.getGreen(),rgb.getBlue() );
			c.setColorForeground(col);
			c.setColorActive(col);
		}
		rgb = new Color(Color.HSBtoRGB(0,0,b));
		if((c= getController(layer.getType(),name+"B.v"))!=null){
			int col = app.color(rgb.getRed(),rgb.getGreen(),rgb.getBlue()  );
			c.setColorForeground(col);
			c.setColorActive(col);
		}
	}


	public void controlEvent(ControlEvent theEvent) {
		try{
			if(ready){
				//check if interface setup is complete and parameters updated before treating messages
				if(theEvent.isController()){
					controlEvent(theEvent.getController().getName(),theEvent.getController().getValue());
				}else if(theEvent.isGroup()){
					controlEvent(theEvent.getName(),theEvent.getValue());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void controlEvent(String name,float value) {
		Layer layer=app.layers[app.currentLayer];
		if(layer!=null){
			String layerType = layer.getType();
			if(name.startsWith(layerType+".")){
				name = name.substring(layerType.length()+1);
				if(name.indexOf(":")!=-1){
					name=name.substring(0,name.indexOf(":"));
				}
				Parameter parameter;
				if(name.indexOf(".")!=-1){
					parameter = layer.getParameter(name.substring(0,name.indexOf(".")));
				}else{
					parameter = layer.getParameter(name);
				}
				if(parameter!=null){
					controlParam(parameter,layerType , name, value);
				}
			}
		} 

		if(name.equals("layer0bgAlpha.v")){
			layer = app.layers[0];
			if(layer!=null){
				FloatParameter bgAlpha= layer.getFloatParameter("bgAlpha");
				if(bgAlpha!=null){
					bgAlpha.v=value;
				}
			}
		}
		
		//layers
		if(name.equals("Layer Selected") ) {
			app.currentLayer =(int)(value);
			currentLayerChanged();
		}
		if(name.startsWith("bang") ) {
			if(controlP5.getController(name).getValue()==1)
			{
				if(app.soundController!=null){
					app.soundController.setOnBeat(Integer.parseInt(name.split("g\\-")[1]));
				}
				controlP5.getController(name).setValue(0);
			}
		}
		if(name.startsWith("Layer Enabled ") ) {
			int i = Integer.parseInt(name.split("Enabled ")[1]);
			app.layerEnabled[i] = (value!=0);
		}

		//sound
		if(app.soundController!=null){
			//if(name.equals("beatSense") ) app.soundController.setBeatSense(value);
			if(name.equals("beatSenseSense") ) app.soundController.setBeatSenseSense(value);
			//if(name.equals("drawSoundControl") )app.soundController.draw=value==1;      
			if(name.equals("autoBeatSense") )   app.soundController.setAutoBeatSense(value==1);  
			if(name.startsWith("band") ) {
				int i = Integer.parseInt(name.replaceAll("band",""));
				app.soundController.zoneEnabled()[i]=value==1;  
			}
			if(name.equals("scGroup")) {
				if(layer!=null){
					Parameter param =layer.getParameter(currentController.substring(layer.getType().length()+1));
					if(param!=null){
						param.setScGroup((int)value);
						int a = param.getScGroup();
						app.soundController.setCurrentGroup(a);
					}
				}
			}
		}
		if(name.equals("refresh")  ) refresh=(int)value;
		//various buttons
		//if(name.equals("SendAll") &&app.oscController!=null ) app.oscController.broadcastLoadAllPatterns();
		if(name.equals("Frame")){
			this.app.saveFrame=true;
		}
		if(app.midiLaunchpadController!=null){
			if(name.equals("LP")  ) app.midiLaunchpadController.enabled=!app.midiLaunchpadController.enabled;
		}
		
		//if(name.equals("Send") && pat!=null&&app.oscController!=null) app.oscController.broadcastLoadPatternInLayer(pat,app.currentLayer);
		if(name.equals("Save")) app.presetManager.asyncFileOperation("save");
		if(name.equals("Save All")) app.presetManager.asyncFileOperation("SaveAll");
		if(name.equals("relocate")) {
			otherPosition=!otherPosition;
			if(otherPosition){
				app.frame.setLocation(x, y);
			}else{
				app.frame.setLocation(Config.X, Config.Y);
			}/*if(app.previewWindow!=null){
				app.previewWindow.setLocation(50, 50);
				app.previewWindow.getFrame().setSize(Config.WIDTH/4,Config.HEIGHT/4+20);
			}*/
		}
		if(app.globalController!=null){
			if(name.equals("speed+"))app.globalController.incSpeed();
			if(name.equals("speed-"))app.globalController.decSpeed();
			if(name.equals("bump+"))app.globalController.incBump();
			if(name.equals("bump-"))app.globalController.decBump();
		}
	}
	int x=0;
	int y=0;
	boolean otherPosition=false;
	

	public void currentLayerChanged(){
		Layer layer = app.layers[app.currentLayer];
		if(layer!=null){
			for(Parameter param : layer.getParameters()){
				if(app.guiController.currentController!=null &&param.getName().equals(app.guiController.currentController)){
					updateController(null,"scGroup",param.getScGroup());
					if(app.soundController!=null){
						app.soundController.setCurrentGroup(param.getScGroup());					
					}
				}
			}
			this.autoSelectTab();

		}else{
			controlP5.controlWindow.activateTab("default");
			if(app.soundController!=null)
				app.soundController.setCurrentGroup(0);
		}
	}



	public void update(){

		if(!Config.GUI_WINDOW){
			this.drawCustomUI(this.app);
		}
		
		if(app.millis() - lastGuiUpdate > refresh && this.ready){
			lastGuiUpdate = app.millis();

			if(controlP5!=null){
				
				Layer layer = app.layers[app.currentLayer];
				if(layer!=null){
					String layerType = layer.getType();
					for(Parameter param : layer.getParameters()){
						updateParam(layerType,param);
					}
					updateController(null,"layerType",layer.getType().replaceAll("^Layer",""));
					updateController(null,"layerName",layer.getName());
					if(currentController!=null){
						updateController(null,"currentController",currentController);
					}else{
						updateController(null,"currentController","");
						updateController(null,"scGroup",0);
					}
				}else{
					//no layer
					updateController(null,"layerType","");
					updateController(null,"LayerName","");
					updateController(null,"currentController","");
					updateController(null,"scGroup",0);
				}
				
				//update some colour indicator
				layerColors();

				//current layer
				updateController(null,"currentLayer",(app.currentLayer+1)+"");
				updateController(null,"Layer Selected",app.currentLayer);
				//layers
				for(int i=0;i<10;i++){
					updateController(null,"Layer Enabled "+i,app.layerEnabled[i]?1:0);
				}
				
				layer = app.layers[0];
				if(layer!=null){
					FloatParameter bgAlpha= layer.getFloatParameter("bgAlpha");
					if(bgAlpha!=null){
						updateController(null,"layer0bgAlpha.v",bgAlpha.v);
					}
				}
				
				
				//sound
				if(app.soundController!=null&&Config.DSP){
		
					updateController(null,"beatSenseSense",app.soundController.getGroups().get(0).beatSenseSense);

					//bands
					for(int i=0;i<app.soundController.zoneEnabled().length;i++){
						updateController(null,"band"+i,(app.soundController.zoneEnabled()[i])?1:0);	
					}
					for(int i =0;i<app.soundController.getGroups().size();i++){
						controlP5.Controller<?> c;
						if((c=controlP5.getController("score-"+i))!=null && groupScores.get(i)!=null &&groupBeatSenses.get(i)!=null){
							if(groupScores.get(i)<groupBeatSenses.get(i)){
								c.setColorActive(app.color(6,153,196) );
								c.setColorForeground(app.color(0,105,140) );
							}else{
								c.setColorActive(app.color(196,153,6) );
								c.setColorForeground(app.color(196,153,6) );
							}
						}
						if(groupScores.get(i)!=null){
							updateController(null,"score-"+i,groupScores.get(i));
						}
						if(groupBeatSenses.get(i)!=null){
							updateController(null,"beatsense-"+i,groupBeatSenses.get(i));
						}
					}
				}else{
					//no sound
					//updateController("beatSense",0); 
					updateController(null,"drawSoundControl",0); 
					//updateController(null,"autoBeatSense",0);
					updateController(null,"currentController","");
				}
				if(app.midiLaunchpadController!=null){
					updateController(null,"LP",app.midiLaunchpadController.enabled?1:0);
				}
	
				//FPS
				updateController(null,"frames", app.frameRate);
				updateController(null,"refresh", refresh);
				//computer FPS colors
				controlP5.Controller<?> cc=controlP5.getController("frames");
				if(cc!=null){
					float r =  0;
					float g =  0;
					float b =  0;
					if(app.frameRate>55){
						g =  255;
					} else if(app.frameRate>25){
						float v = (app.frameRate-25)/30f*255f;
						r =  255-v;
						g =  v;
					} else {
						r =  255;
					}
					cc.setColorBackground(app.color(r,g,b));
				}
				
			}

		}
	
	}



	private void controlParam(Parameter param,String layerType,String name,float value){
		if(param !=null){
			if(param instanceof FloatParameter){
				controlParam((FloatParameter)param,layerType,name,value);
			}
			if(param instanceof ListParameter){
				controlParam((ListParameter)param,layerType,name,value);
			}

			//handle global / local SC
			currentController = layerType+"."+name.split("\\.")[0];
			updateController(null,"currentController",currentController);
			
			if(app.soundController!=null){
				int scGroup = param.getScGroup();
				updateController(null,"scGroup",scGroup);
				app.soundController.setCurrentGroup(scGroup);
			}else{
				updateController(null,"scGroup",0);
			}
		}
	}
	
	void controlParam(FloatParameter param,String layerType,String name,float value){
		if(app.layers[app.currentLayer]!=null){
			if(name.endsWith(".v")) param.setV(value);
			if(name.endsWith(".sc"))  param.sc =(value==1.0f);
			if(name.endsWith(".scChange"))  param.scChange =value/100f;
			if(name.endsWith(".scAccumulatorInc"))  param.scAccumulatorInc =accumulateFromGui(value);
			if(name.endsWith(".tc"))  param.tc =(value==1.0f);
			if(name.endsWith(".tcIncPerSec"))  param.tcIncPerSec =value;
			if(name.endsWith(".bump"))  param.bump =(value==1.0f);
			if(name.endsWith(".bumpAccumulatorInc"))  param.bumpAccumulatorInc =accumulateFromGui(value);
			if(name.endsWith(".bumpChange"))  param.bumpChange =value/100f;
			if(name.endsWith(".bumpAttack"))  param.bumpAttack =value;
			if(name.endsWith(".bumpFade"))  param.bumpFade =value;
			if(name.endsWith(".modulate"))  param.modulate =(value==1.0f);
			if(name.endsWith(".modulatePct"))  param.modulatePct =value/100f;
			if(name.endsWith(".scAlter"))  param.scAlter =(value==1.0f);
			if(name.endsWith(".scAlterAccumulatorInc"))  param.scAlterAccumulatorInc =accumulateFromGui(value);
			if(name.endsWith(".max"))  param.max =value;
			if(name.endsWith(".min"))  param.min =value;
			if(name.endsWith(".ghost"))  param.ghost = (value==1.0f);
			if(name.endsWith(".ghostFull"))  param.ghostFull = (value==1.0f);
			if(name.endsWith(".ghostMode"))  param.ghostValue = (value==1.0f?-1:1);

			//colors
			if(name.endsWith(".tcIncPerSec")){  
				if(!name.startsWith("fg")&&!name.startsWith("bg")){
					controlP5.Controller<?> c = controlP5.getController(layerType+"."+name);
					if(value>=0){
						c.setColorActive(app.color(6,153,196) );
						c.setColorForeground(app.color(0,105,140) );
					}else{
						c.setColorActive(app.color(196,6,153) );
						c.setColorForeground(app.color(150,5,105) );
					}
				}
			}
			if(name.endsWith(".v")){  
				if(name.startsWith("fgAlpha")||name.startsWith("bgAlpha")){
					controlP5.Controller<?> c = controlP5.getController(layerType+"."+name);
					c.setColorActive(app.color(6*value,153*value,196*value) );
					c.setColorForeground(app.color(0*value,105*value,140*value) );
				}
			}
		}
	}
	void controlParam(ListParameter param,String layerType,String name,float value){
		if(app.layers[app.currentLayer]!=null){
			if(name.endsWith(".v"))  param.setIndex((int)value);
			if(name.endsWith(".sc"))  param.sc =(value==1.0f);
			if(name.endsWith(".scAccumulatorInc"))  param.scAccumulatorInc =accumulateFromGui(value);
			if(name.endsWith(".ghost"))  param.ghost = (value==1.0f);
		}
	}
	private float accumulateFromGui(float value){
		return(value==0?0:10f/value);
	}
	private float accumulateToGui(float value){
		return((value==0?0:10f/value));
	}


	private void updateParam(String layerType,Parameter param){
		if(param !=null){
			if(param instanceof FloatParameter){
				updateParam(layerType,(FloatParameter)param);
			}
			if(param instanceof ListParameter){
				updateParam(layerType,(ListParameter)param);
			}
		}
	}

	private void updateParam(String layerType, FloatParameter param){
		String name = param.getName();
		updateController(layerType,name+".v", param.v);
		updateController(layerType,name+".sc", param.sc?1:0 );
		updateController(layerType,name+".scChange", param.scChange*100f);
		updateController(layerType,name+".scAccumulatorInc", accumulateToGui(param.scAccumulatorInc));
		updateController(layerType,name+".tc", param.tc?1:0);
		updateController(layerType,name+".tcIncPerSec",  param.tcIncPerSec);
		updateController(layerType,name+".bump",param.bump?1:0);
		updateController(layerType,name+".bumpAccumulatorInc",accumulateToGui(param.bumpAccumulatorInc));
		updateController(layerType,name+".bumpChange", param.bumpChange*100f);
		updateController(layerType,name+".bumpAttack",param.bumpAttack);
		updateController(layerType,name+".bumpFade",param.bumpFade);
		updateController(layerType,name+".scAlter", param.scAlter?1:0 );
		updateController(layerType,name+".scAlterAccumulatorInc", accumulateToGui(param.scAlterAccumulatorInc));
		updateController(layerType,name+".modulate",param.modulate?1:0);
		updateController(layerType,name+".modulatePct",  param.modulatePct*100f);
		updateController(layerType,name+".max", param.max);
		updateController(layerType,name+".min",  param.min);
		updateController(layerType,name+".ghost",  param.ghost?1:0);
		updateController(layerType,name+".ghostFull",  param.ghostFull?1:0);
		updateController(layerType,name+".ghostMode",  param.ghostValue==-1?1:0);
	}

	
	private void updateParam(String layerType,ListParameter param){
		String name = param.getName();
		updateController(layerType,name+".v",  param.getIndex());
		updateController(layerType,name+".sc",  param.sc?1:0 );
		updateController(layerType,name+".scAccumulatorInc",accumulateToGui(param.scAccumulatorInc));
		updateController(layerType,name+".tc",  param.tc?1:0);
		updateController(layerType,name+".tcIncPerSec",  param.tcIncPerSec);
		updateController(layerType,name+".ghost",  param.ghost?1:0);
	}



	public int addColorGroup(String layerType,String title, String name,int x, int y,Tab tab,Group group){
		int initY=y;;
		addControlNames(layerType,name,x,y,tab,group);
		addLabel(layerType, name, title, x, y, tab, group);
		Toggle t = addToggle(layerType,name+"Dsp",true,x+100,y,50,h,tab,group);
		t.setColorActive(0);
		t.setColorBackground(0);
		t.setColorForeground(0);
		y+=h+hMargin;
		addControlFloatParameter(layerType,name+"H",0,1,x, y, tab,group);
		y+=h+hMargin;
		addControlFloatParameter(layerType,name+"S",0,1, x, y, tab,group);
		y+=h+hMargin;
		addControlFloatParameter(layerType,name+"B",0,1, x, y, tab,group);    
		y+=h+hMargin;
		y+=hMargin;
		addControlFloatParameter(layerType,name+"Alpha",0,1, x, y, tab,group);    
		y+=h+hMargin;
		y+=hMargin;
		return(y-initY);
	}

	public int wNum = 23;
	public void addControlNames(String layerType, String name,int x, int y,Tab tab,Group group){
		int xx=0;
		xx+=w*2+wMargin+hMargin*3+8+h+wMargin;
		int i=0;
		addLabel(layerType, name+i++, "  sc", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "acc", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "chg", xx, y,tab, group);xx+=wNum+wMargin;xx+=hMargin*3;
		addLabel(layerType, name+i++, "bmp", xx, y,tab, group);xx+=h+wMargin;
		addLabel(layerType, name+i++, "acc", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "chg", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "acc", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "att", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "fade", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, " modulat", xx, y,tab, group);
		addLabel(layerType, name+i++, "   ghost", xx, y,tab, group);
	}


	public void addControlNamesMoves(String layerType, String name,int x, int y,Tab tab,Group group){
		addLabel(layerType, name, "Moves", x, y,tab, group);
		int xx=w*2+wMargin+hMargin*3+8;
		int i =0;
		addLabel(layerType, name+i++, "   tc", xx, y,tab, group);xx+=h+wMargin;
		addLabel(layerType, name+i++, " value", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "   sc", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, " chg", xx, y,tab, group);		xx+=wNum+wMargin;xx+=hMargin*3;
		addLabel(layerType, name+i++, "   bmp", xx, y,tab, group);xx+=h+wMargin;
		addLabel(layerType, name+i++, " %", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "   chg", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "   att", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, " fade", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "   modulat", xx, y,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, " ghost", xx, y,tab, group);		xx+=h+wMargin;xx+=wNum+wMargin;xx+=wMargin*3;
		addLabel(layerType, name+i++, "   alter", xx, y,tab, group);		xx+=wNum+wMargin;
		xx+=05;
		addLabel(layerType, name+i++, "   min", x-2*(wNum+wMargin),y+70,tab, group);xx+=wNum+wMargin;
		addLabel(layerType, name+i++, "   max", x-2*(wNum+wMargin),y+70, tab, group);xx+=wNum+wMargin;

	}

	public void addControlFloatParameter(String layerType, String name,float min,float max,int x, int y,Tab tab,Group group){
		int xx=0;
		Slider s = addSlider(layerType,name+".v"              ,min,min,max,x,y,w*2,h,tab,group);
		s.setCaptionLabel(name);
		if(name.endsWith("H")) s.setCaptionLabel("hue");
		if(name.endsWith("S")) s.setCaptionLabel("sat");
		if(name.endsWith("B")) s.setCaptionLabel("lum");
		if(name.endsWith("Alpha")) s.setCaptionLabel("alpha");


		xx+=w*2+wMargin+hMargin*3+15+h+wMargin+hMargin;    
		addToggle(layerType,name+".sc",false        ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=h+wMargin;
		Numberbox c = addNumber(layerType,name+".scAccumulatorInc"   ,0,x+xx,y,tab,group);
		c.setCaptionLabel("");
		xx+=wNum+wMargin;
		addNumber(layerType,name+".scChange"       ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		xx+=hMargin*2;
		addToggle(layerType,name+".bump",false      ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=h+wMargin;
		addNumber(layerType,name+".bumpAccumulatorInc"   ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		addNumber(layerType,name+".bumpChange"    ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		addNumber(layerType,name+".bumpAttack"      ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		addNumber(layerType,name+".bumpFade"      ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		xx+=hMargin*2;
		addToggle(layerType,name+".modulate",false      ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=h+wMargin;
		addNumber(layerType,name+".modulatePct"      ,0,x+xx,y,tab,group).setCaptionLabel("");

		xx+=h*2+wMargin*2;
		addToggle(layerType,name+".ghost",false      ,x+xx,y,tab,group).setCaptionLabel("");
		//addDropDown(234,name+".ghost" , x+xx, y, new String[]{"off","1","-1","total"},new int[]{0,1,-1,2}, tab, group);
		xx+=h+wMargin;
		addToggle(layerType,name+".ghostFull",false      ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=h+wMargin;
		addToggle(layerType,name+".ghostMode",false      ,x+xx,y,tab,group).setCaptionLabel("");



		if( "size".equals(name)){
			xx+=hMargin*2;
			xx+=wNum+wMargin;
			xx+=wNum;
			addNumber(layerType,name+".min"       ,0,x-2*(wNum+wMargin),y,tab,group).setCaptionLabel("");
			xx+=wNum+wMargin;
			addNumber(layerType,name+".max"       ,0,x-(wNum+wMargin),y,tab,group).setCaptionLabel("");
			xx+=wNum+wMargin;
		}



	}

	public void addControlFloatMoveParameter(String layerType, String nameOriginal,float max,float min,int x, int y,Tab tab,Group group){
		int xx=0;
		Slider s;
		String name=nameOriginal;
		if(layerType!=null){
			//name=layerType+"."+nameOriginal;
		}
		if(nameOriginal.equals("zoom")||nameOriginal.equals("number")){
			s=addSlider(layerType,name+".tcIncPerSec"   ,0,-2,2,x,y,w*2,null,tab,group);
			xx+=w*2+wMargin;
			xx+=hMargin*3+15;
			addToggle(layerType,name+".tc",false        ,x+xx,y,tab,group).setCaptionLabel("");
			xx+=h+wMargin;
			addNumber(layerType,name+".v"   ,0,2,x+xx,y,tab,group).setCaptionLabel("");
			xx+=wNum+wMargin;
			xx+=hMargin;    
		}
		else if(nameOriginal.equals("angle")||nameOriginal.equals("rotate")||nameOriginal.equals("subRotate")){
			s=addSlider(layerType,name+".tcIncPerSec"        ,0,-2f,2f,x,y,w*2,null,tab,group);
			xx+=w*2+wMargin;
			xx+=hMargin*3+15;
			addToggle(layerType,name+".tc",false        ,x+xx,y,tab,group).setCaptionLabel("");
			xx+=h+wMargin;
			addNumber(layerType,name+".v"   ,0,2,x+xx,y,tab,group).setCaptionLabel("");
			xx+=wNum+wMargin;
			xx+=hMargin;       
		}
		else if(nameOriginal.equals("gradient")){
			s=addSlider(layerType,name+".v"              ,0,0,1,x+xx,y,w/2,null,tab,group);
			xx+=w/2+wMargin;
			xx+=hMargin*3+30;
			addToggle(layerType,name+".tc",false        ,x+xx,y,10,null,tab,group).setCaptionLabel("");
			xx+=h+wMargin;
			addSlider(layerType,name+".tcIncPerSec" ,0,-5,5,x+xx,y,w+w/2,null,tab,group).setCaptionLabel("");
			xx+=w+w/2+wMargin;
			xx+=hMargin;     
			xx=0;
			xx+=w*2+wMargin;
			xx+=hMargin*3+15;
			xx+=h+wMargin;
			xx+=wNum+wMargin;
			xx+=hMargin;   
		}else{
			s=addSlider(layerType,name+".tcIncPerSec"              ,0,-75,75,x,y,w*2,h,tab,group);
			xx+=w*2+wMargin;
			xx+=hMargin*3+15;
			addToggle(layerType,name+".tc",false        ,x+xx,y,null,null,tab,group).setCaptionLabel("");
			xx+=h+wMargin;
			addNumber(layerType,name+".v"   ,0,2,x+xx,y,tab,group).setCaptionLabel("");
			xx+=wNum+wMargin;
			xx+=hMargin;   
		}
		addToggle(layerType,name+".sc",false        ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=h+wMargin;
		addNumber(layerType,name+".scChange"       ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		xx+=hMargin*2;
		addToggle(layerType,name+".bump",false      ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=h+wMargin;
		addNumber(layerType,name+".bumpAccumulatorInc"    ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		addNumber(layerType,name+".bumpChange"    ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		addNumber(layerType,name+".bumpAttack"      ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		addNumber(layerType,name+".bumpFade"      ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;
		xx+=hMargin*2;
		addToggle(layerType,name+".modulate",false      ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=h+wMargin;
		addNumber(layerType,name+".modulatePct"      ,0,x+xx,y,tab,group).setCaptionLabel("");
		xx+=wNum+wMargin;

		//xx+=h*2+wMargin*2;
		addToggle(layerType,name+".ghost",false      ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=h+wMargin;
		addToggle(layerType,name+".ghostFull",false      ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=h+wMargin;	
		addToggle(layerType,name+".ghostValue",false      ,x+xx,y,tab,group).setCaptionLabel("");


		xx+=h+hMargin*2;

		addToggle(layerType,name+".scAlter",false        ,x+xx,y,tab,group).setCaptionLabel("");
		xx+=15;
		addNumber(layerType,name+".scAlterAccumulatorInc"      ,0,x+xx,y,tab,group).setCaptionLabel("");

		if( "zoom".equals(nameOriginal)||"number".equals(nameOriginal)){
			xx+=wNum;//+wMargin;
			//xx+=hMargin*2;
			addNumber(layerType,name+".min"       ,0,x-2*(wNum+wMargin),y,tab,group).setCaptionLabel("");
			xx+=wNum+wMargin;
			addNumber(layerType,name+".max"       ,0,x-(wNum+wMargin),y,tab,group).setCaptionLabel("");
			xx+=wNum+wMargin;
		}

		s.setCaptionLabel(name);


		if("scrollX".equalsIgnoreCase(nameOriginal)) s.setCaptionLabel("x");
		if("scrollY".equalsIgnoreCase(nameOriginal)) s.setCaptionLabel("y");


	}




	//mouse
	int lastMouseX=0;
	int lastMouseY=0;
	public void mousePress(int x,int y, int buttons){
		lastMouseX=x;
		lastMouseY=y;
	}
	public void mouseClick(int x,int y, int buttons){
		if(y>this.windowH-110 && y<this.windowH-15 && app.soundController!=null){
			//in the zone..
			//use click to enable zones faster than with the buttons:)
			if(bandWidth==0){
				computeBanWidth();
			}
			int band = (x - 10)/(bandWidth+1);
			if(app.soundController!=null && band<app.soundController.zoneEnabled().length & band>=0)
				if(buttons==1 || buttons==37){
					app.soundController.zoneEnabled()[band]=!app.soundController.zoneEnabled()[band];
				}else if(buttons==2||buttons==39){
					for(int i=0;i<app.soundController.zoneEnabled().length;i++){
						app.soundController.zoneEnabled()[i]=true;
					}
				}else if(buttons==3){
					for(int i=0;i<app.soundController.zoneEnabled().length;i++){
						app.soundController.zoneEnabled()[i]=!app.soundController.zoneEnabled()[i];
					}
				}
		}
	}
	public void mouseRelease(int x,int y, int buttons){
		if(y>this.windowH-110 && y<this.windowH-15 
				&&lastMouseY>this.windowH-100 && lastMouseY<this.windowH-15
				&& app.soundController!=null){	
			if(java.lang.Math.abs(lastMouseX-x)>10){
				//in the zone..
				//use click to enable zones faster than with the buttons:)
				if(bandWidth==0){
					computeBanWidth();
				}
				int band = (java.lang.Math.min(lastMouseX,x) - 10)/(bandWidth+1);
				int band2 = (java.lang.Math.max(lastMouseX,x) - 10)/(bandWidth+1);
				if(buttons==1|| buttons==37){
					for(int i=0;i<app.soundController.zoneEnabled().length;i++){
						app.soundController.zoneEnabled()[i]=false;
					}
				}
				for(int i=band;i<band2+1;i++){
					if(app.soundController!=null && i<app.soundController.zoneEnabled().length && i>=0)
						if(buttons==1||buttons==37){
							//app.soundController.zoneEnabled[i]=!app.soundController.zoneEnabled[i];
							app.soundController.zoneEnabled()[i]=true;
						}else if(buttons==2||buttons==39){
							app.soundController.zoneEnabled()[i]=false;
						}else if(buttons==3){
							app.soundController.zoneEnabled()[i]=true;
						}
				}
			}
		}
		lastMouseX=x;
		lastMouseY=y;
	}


}
