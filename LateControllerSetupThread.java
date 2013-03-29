import java.awt.Robot;
import java.lang.reflect.Field;


import codeanticode.syphon.SyphonServer;

import sojamo.drop.SDrop;
import dmxP512.DmxP512;


class LateControllersSetupThread extends Thread {

	Application parent;

	public LateControllersSetupThread(Application parent) {
		super();
		this.parent=parent;
	}
	public void run() {
		//delay the setup so that all the rest can start up before the controllers.
		try{
			while(this.parent.frameCount<40){	
				sleep(50);
			}
		}catch(Exception e){e.printStackTrace();}
		runSync();
	}
	public void runSync() {


		System.out.println("Setting up controllers...");
		//create robot to disable screen saver by moving the mouse.
		try{
			this.parent.robot = new Robot();
		}catch(Exception e){e.printStackTrace();}

		if(Config.PREVIEW && this.parent.previewWindow==null){
			this.parent.previewWindow = new GLTextureWindow(this.parent, "Preview for Patterns!",50, 50, Config.WIDTH/4, Config.HEIGHT/4,true,true,true);
			
		}
		if(Config.SYPHON && this.parent.syphonServer==null)
			this.parent.syphonServer = new SyphonServer(this.parent, "Processing Syphon");
		
		if(this.parent.presetManager==null)
			this.parent.presetManager = new PresetManager(this.parent);
		
		if(this.parent.drop==null)
			this.parent.drop = new SDrop(this.parent);
		
		if (Config.KEYBOARD && this.parent.keyboardController==null)
			this.parent.keyboardController = new KeyboardController(this.parent);
		

		if (Config.MOUSE && this.parent.mouseController==null)
			this.parent.mouseController = new MouseController(this.parent);
		
		if (Config.SOUND && this.parent.soundController==null)
			this.parent.soundController = new SoundController(this.parent);
		
		if (Config.GUI && this.parent.guiController==null && Config.GUI_WINDOW)
			this.parent.guiController = new GUIController(this.parent,Config.GUI_WINDOW,Config.GUI_VISIBLE);
		
		if (this.parent.midiController==null){
			this.parent.midiController = new MIDIController(this.parent);
			this.parent.midiLaunchpadController = new MIDILaunchpadController(this.parent);
		}
		
		if (Config.OSC && this.parent.oscController==null)
			this.parent.oscController = new OscController(this.parent);
		
		if (this.parent.globalController==null)
			this.parent.globalController = new GlobalController(this.parent);
		
		if(Config.PRINT_THREAD_TIME && this.parent.threadsTime==null)
			this.parent.threadsTime =new ThreadsTime(1000);
		
		if(Config.DMX_DMXPRO||Config.DMX_LANBOX){
			if(this.parent.dmxP512==null){
				this.parent.dmxP512=new DmxP512(this.parent,Config.DMX_UNIV_SIZE);
				if(Config.DMX_LANBOX){
					this.parent.dmxP512.setupLanbox(Config.DMX_LANBOX_IP,Config.DMX_LANBOX_PORT);
				}
				if(Config.DMX_DMXPRO){
					this.parent.dmxP512.setupDmxPro(Config.DMX_DMXPRO_PORT,Config.DMX_DMXPRO_BAUDRATE);
				}
			}
		}
		
		try {  	  
			Class<?> cls = this.parent.getClass();
			Field fieldlist[] = cls.getDeclaredFields();
			for (Field fld : fieldlist) {
				//System.out.println(fld.getName());
				if(!fld.getName().equals("serialVersionUID")){
					if(fld.get(this.parent) instanceof Controller){
						this.parent.controllers.add((Controller)fld.get(this.parent));
					}
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			System.err.println(e);
		}
		
		System.out.println("Setting up controllers done.");
	}
}
