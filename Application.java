import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.media.opengl.GL2;

import processing.core.PApplet;
import processing.opengl.PGL;
import processing.opengl.PGraphicsOpenGL;
import oscP5.OscMessage;
import controlP5.ControlEvent;
import sojamo.drop.DropEvent;
import sojamo.drop.SDrop;
import dmxP512.DmxP512;
import codeanticode.syphon.SyphonServer;

public class Application extends PApplet{
	
	// some nice global variables to store our layers
	public Layer layers[];
	public boolean layerEnabled[];
	public int currentLayer = 0; 
	
	// Controllers
	protected List<Controller> controllers = new ArrayList<Controller>();
	protected PresetManager presetManager;
	protected KeyboardController keyboardController;
	protected MouseController mouseController;
	protected SoundController soundController;
	protected MIDIController midiController;
	protected MIDILaunchpadController midiLaunchpadController;
	protected GUIController guiController;
	protected OscController oscController;
	protected GlobalController globalController;
	protected DmxP512 dmxP512;
	protected ThreadsTime threadsTime;
	protected SDrop drop;
	
	//OpenGL stuff
	protected GLTextureWindow previewWindow;
	protected PGraphicsOpenGL pgl;
	protected PGraphicsOpenGL offScreenPgl;
	protected SyphonServer syphonServer;
	protected ShaderRenderer shaderRenderer;
	
	//mouse mover to keep screen saver out !
	Robot robot;
	protected long robotLastMove=0;
	
	//frame recording
	public boolean saveFrame=false;
	
	//get my name.
	protected static final String name=new CurrentClassGetter().getClassName();
	public static class CurrentClassGetter extends SecurityManager {
		public String getClassName() {return getClassContext()[1].getName();}
	}
	private static final long serialVersionUID = 9841879847L;
	
	/**
	 * ran when launched as stand alone
	 * @param args
	 */
	public static void main(String args[]) {
		PApplet.main(new String[] {name});
	}
	
	
	/**
	 * override default init method to handle full screen mode.
	 */
	@Override
	public void init() {
		frame.setTitle("Patterns!");

		//display display information
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] displayDevices = ge.getScreenDevices();
		System.out.println("Displays:");
		for(GraphicsDevice displayDevice : displayDevices){
			GraphicsConfiguration[] gc = displayDevice.getConfigurations();
			Rectangle monitor = gc[0].getBounds();
			System.out.println("\t"+displayDevice.getIDstring()+"(" + monitor.width + "x" + monitor.height+"@" +monitor.x+"x"+monitor.y+")");
		}

		if(Config.PREVIEW || Config.SYPHON){
			Config.OFFSCREEN=true;
		}
		
		//auto full screen based on screens definition
		if(Config.SCREEN>=0 && Config.SCREEN<displayDevices.length){
			Config.CURSOR = false;
			Config.BORDERS = false;
			
			GraphicsDevice displayDevice;
			displayDevice=displayDevices[Config.SCREEN];
			System.out.println("using display "+displayDevice.getIDstring());
			Rectangle monitor = displayDevice.getConfigurations()[0].getBounds();
			Config.X=monitor.x;
			Config.Y=monitor.y;
			Config.HEIGHT=monitor.height;
			Config.WIDTH=monitor.width;			

			// remove window decoration while in full screen mode
			frame.removeNotify();
			frame.setUndecorated(true);
			frame.setResizable(false);
			frame.addNotify();
			frame.setLocation(Config.X, Config.Y);

		}else{

			if(Config.SCREEN>=0){
				System.out.println("screen "+Config.SCREEN+" does not exists. disabling fullscreen");
			}

			if(!Config.BORDERS){
				frame.removeNotify();
				frame.setUndecorated(true);
				frame.setResizable(false);
				frame.addNotify();
				frame.setLocation(Config.X, Config.Y);
			}else{
				frame.setResizable(true);
			}
		}
		
		super.init();
	}

	@Override
	public void setup() {
		//new Exception().printStackTrace();
		
		if (!Config.BORDERS){
			frame.setLocation(Config.X, Config.Y);
		}
		
		// init Layers
		layers = new Layer[15];
		layers[0] = new LayerNineBlockPattern(this);
		//layers[0] = new LayerNoise(this);
		layerEnabled = new boolean[layers.length];
		//layerEnabled[0] = true;
		for (int i = 1; i < layerEnabled.length; i++) {
			layerEnabled[i] = false;
		}
		
		if (!Config.CURSOR){
			this.noCursor();
		}
		
		//OPENGL
		size(Config.WIDTH, Config.HEIGHT, OPENGL);
		
		if(Config.OFFSCREEN){
			//create offscreen renderer
			offScreenPgl = (PGraphicsOpenGL)createGraphics(Config.WIDTH, Config.HEIGHT, OPENGL);  
		}
		
		//if(!Config.SYPHON&&!Config.PREVIEW){
		/*
			if(Config.OPENGL_ANTIALIASING==2)
				hint(ENABLE_OPENGL_2X_SMOOTH);
			else if(Config.OPENGL_ANTIALIASING==4)
				hint(ENABLE_OPENGL_4X_SMOOTH);
			else
				hint(DISABLE_OPENGL_2X_SMOOTH);
		*/ 
		//} 
		
		// controllers setups
		// need to make the controller synchronized when gui is in main window
		if(!Config.GUI_WINDOW){
			if (Config.SOUND && soundController==null){
				soundController = new SoundController(this);
			}
			guiController = new GUIController(this,Config.GUI_WINDOW,Config.GUI_VISIBLE);
		}
		
		lateControllersSetup();
	}
	
	
	private void killScreenSaver(){
		FloatParameter.modulateRatio=0.1f;
		long now = System.currentTimeMillis();
		try{
			if(robot!=null && (now - robotLastMove) > 1000 * 60 * 5){
				int x = MouseInfo.getPointerInfo().getLocation().x;
				int y = MouseInfo.getPointerInfo().getLocation().y;
				//robot.
				robot.mouseMove(x+2, y+2);
				robot.mouseMove(x, y);
				robotLastMove=now;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void windowResizeHandling(){
		if (Config.WIDTH != this.width || Config.HEIGHT != this.height) {
			Config.WIDTH = this.width;
			Config.HEIGHT = this.height;
			if(Config.PREVIEW||Config.SYPHON){
			/*	if(previewWindow!=null){
					previewWindow.setTexture(null);
			//		previewWindow.hide();
				}
				//try{Thread.sleep(100);}catch(Exception e){}
				offScreen = (PGraphicsOpenGL)createGraphics(Config.WIDTH, Config.HEIGHT, OPENGL);  
				//try{Thread.sleep(100);}catch(Exception e){}
				if(previewWindow!=null){
					previewWindow.setTexture(offScreen.getTexture());
				//	previewWindow.getFrame().setSize(Config.WIDTH/4, Config.HEIGHT/4);
				//	previewWindow.show();
				}
			*/	
			}else{
				//resize now correctly handled by processing 1.0.4+
				//gl.glViewport(0, 0, width, height);
			}
			System.out.println("new size : " + Config.WIDTH + "x"+ Config.HEIGHT);
		}
	}
	public void newLayerInCurrentLayer(){
		this.newLayerInLayer(this.currentLayer);
	}
	public void newLayerInLayer(int layer){
		if(guiController!=null && guiController.controlP5.controlWindow!=null){
			layers[currentLayer]=Layer.newLayer(this,guiController.controlP5.controlWindow.getCurrentTab().getName());
			Layer newlayer = layers[layer];
			if(newlayer!=null){
				FloatParameter bgAlpha = newlayer.getFloatParameter("bgAlpha");
				if(bgAlpha!=null){
					if(layer==0)
						bgAlpha.setV(1);
					else
						bgAlpha.setV(0);  
				}
			}
		}
	}
	
	private void drawLayers(){
		
		// paint patterns
		for (int i = 0; i < layers.length; i++) {
			//get the reference to prevent null pointers...
			Layer layer=layers[i];
			
			if (layer != null){
				layer.update();
				//draw main background
				if (i == 0){
					layer.drawBackground(pgl);
				}
			}
	
			if (layerEnabled[i]) {
				//create pattern if needed
				if(layer==null){
					newLayerInCurrentLayer();
				}
				
				if(layer!=null){
					if (i != 0){
						layer.drawBackground(pgl);
					}
				
					layer.draw(pgl);
				}
			}
		}
	}
	
	float fps = 0;
	long lastFrame=0;
	
	@Override
	public void draw() {

		long now = System.currentTimeMillis();
		fps = (1f/(now-lastFrame))*1000;
		lastFrame = now;
		
		Thread.currentThread().setName("drawing thread");
		
		killScreenSaver();
		windowResizeHandling();


		//start openGL
		if(Config.OFFSCREEN){
			offScreenPgl.beginDraw();
			pgl = offScreenPgl;
		}else{
			pgl = (PGraphicsOpenGL) g;
		}
		GL2 gl = PGL.gl.getGL2();
		
		// set vertical sync on
		if(Config.VERTICAL_SYNC){
			gl.setSwapInterval(1);
		}
		
		//clear GL Depth
		gl.glClear(GL2.GL_DEPTH_BUFFER_BIT);
		
		//gl.glEnable(GL.GL_BLEND);
		
		//resize screen
		gl.glViewport(0, 0, width, height);
	    gl.getGL2().glMatrixMode(GL2.GL_PROJECTION);
	    gl.getGL2ES1().glLoadIdentity();
	    gl.getGL2ES1().glOrtho(0.0, width, 0.0, height, -100.0, +100.0);
	    gl.getGL2().glMatrixMode(GL2.GL_MODELVIEW);
	    gl.getGL2ES1().glLoadIdentity();
		
		drawLayers();
		

		
		//end openGL
		if(Config.OFFSCREEN){
			//end draw
			offScreenPgl.endDraw();
			//render shader
			if(shaderRenderer==null){
				shaderRenderer=new ShaderRenderer(
						this, 
						(PGraphicsOpenGL) g,
						"data"+File.separator+"shaders"+File.separator
						+"edges.glsl"
						//+"pixelNoise.glsl"
						);
			}
			if(shaderRenderer!=null){
				shaderRenderer.update();
			}
			//draw texture	
			this.image(offScreenPgl,0,0);
			//preview window
			if(Config.PREVIEW && previewWindow!=null){
				if(previewWindow.init()){
					previewWindow.show();
				}
				previewWindow.setTexture(((PGraphicsOpenGL) g).getTexture());
				previewWindow.render();
			}
			//syphon
			if(Config.SYPHON && syphonServer!=null){
				 syphonServer.sendImage(offScreenPgl);
			}
		}else{
			//nothing to do non offscreen mode.
		}
		//saving frame on request
		if(saveFrame){
			saveFrame=false;
			String filename = new SimpleDateFormat("yyyy-MM-yy_HH-mm-ss").format(new Date())+".png";
			saveFrame(filename);
			System.out.println("saved frame " + filename);
		}
		//controllers
		for(Controller controller : controllers){
			if(controller!=null){
				controller.control();
				controller.update();
			}
		}
	}
	
	/**
	 * make controller creation async to 
	 * get the main window ready as quick as possible
	 */
	private void lateControllersSetup(){
		LateControllersSetupThread async = new LateControllersSetupThread(this);
		async.start();
		//async.runSync();
	}

//controlP5 callback
	public void controlEvent(ControlEvent event) {
		if(guiController!=null)
			guiController.controlEvent(event);
	}

//OscP5 callcack
	void oscEvent(OscMessage theOscMessage) {
		if( oscController!=null)
			oscController.oscEvent(theOscMessage);
	}

//keyboard callback
	public void keyPressed(){
		if(key==27){
			key=0;
		}
		if(keyboardController!=null)
			keyboardController.keyPressed(key,keyCode);
	}
	public void keyReleased(){
		if(keyboardController!=null)
			keyboardController.keyReleased(key,keyCode);
	}


	
//mouse callback
	public void mousePressed(){
		if(mouseController!=null){
			if(guiController!=null &&!Config.GUI_WINDOW && guiController.isVisible()){
				guiController.mousePress(mouseX,mouseY,mouseButton);
			}else{
				mouseController.mousePressed(mouseX,mouseY,mouseButton);	
			}
		}
	}
	public void mouseReleased(){
		if(mouseController!=null){
			if(guiController!=null &&!Config.GUI_WINDOW && guiController.isVisible()){
				guiController.mouseRelease(mouseX,mouseY,mouseButton);
			}else{
				mouseController.mouseReleased(mouseX,mouseY,mouseButton);	
			}
		}
	}
	public void mouseClicked(){
		if(mouseController!=null){
			if(guiController!=null &&!Config.GUI_WINDOW && guiController.isVisible()){
				guiController.mouseClick(mouseX,mouseY,mouseButton);
			}else{
				mouseController.mouseClicked(mouseX,mouseY,mouseButton);	
			}
		}
	}

//sdrop callback
	void dropEvent(DropEvent theDropEvent) {
		if(theDropEvent.isFile() && this.presetManager!=null) {
			File myFile = theDropEvent.file();
			if(myFile.isFile() && !myFile.isDirectory()){
				//if((myFile+"").endsWith(".all.txt")){
					this.presetManager.load(myFile);
					if(guiController!=null){
						guiController.autoSelectTab();
					}
				//}else if((myFile+"").endsWith(".txt")){
					//this.presetManager.loadInCurrentLayer(myFile);
				//}
			}
		}
	}

}
