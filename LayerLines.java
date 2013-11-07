

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;

import javax.media.opengl.GL2;

import controlP5.Group;
import controlP5.Tab;
import processing.opengl.PJOGL;
import processing.opengl.PGraphicsOpenGL;

public class LayerLines extends LayerColor {

	class Line{
		float h;
		float s;
		float b;
		float alpha;
		float width;
		float space;
		float angle;
	}

	public FloatParameter alphaMult      = new FloatParameter(this,1f,0f,1f,"alphaMult");

	public FloatParameter size      = new FloatParameter(this,0f,0f,100f,"size");
	public FloatParameter space      = new FloatParameter(this,1f,0f,100f,"space");
	public FloatParameter add      = new FloatParameter(this,1f,0f,10f,"add");
	public FloatParameter insertPoint      = new FloatParameter(this,0f,0f,1f,"insertPoint");
	public FloatParameter deletePoint      = new FloatParameter(this,1f,0f,1f,"deletePoint");

	public ListParameter  onBeatAdd  = new ListParameter(this,0,ListParameter.onOff,ListParameter.onOffNames,"onBeatAdd");
	public ListParameter  deleteLeft  = new ListParameter(this,0,ListParameter.onOff,ListParameter.onOffNames,"deleteLeft");
	public ListParameter  deleteMobile  = new ListParameter(this,1,ListParameter.onOff,ListParameter.onOffNames,"deleteMobile");
	public ListParameter  deleteRight  = new ListParameter(this,0,ListParameter.onOff,ListParameter.onOffNames,"deleteRight");

	
	boolean addNow=false;
	List<Line> lines=new LinkedList<Line>();
	int deleteIdx=0;
	
	
	public LayerLines(Application parent){
		super(parent);
		setParameterMap();
	}

	@Override
	public void draw(PGraphicsOpenGL pgl){

		//transform
		GL2 gl = PJOGL.gl.getGL2();
		gl.glPushMatrix();
		gl.glTranslatef(Config.WIDTH/2,Config.HEIGHT/2,0);


		float x=0;
		float alphaMult = this.alphaMult.v();
		for(Line line : lines){
			if(line.width>0){
				Color rgb = new Color(Color.HSBtoRGB(line.h,line.s,line.b));

				float r = rgb.getRed()/255f;
				float g = rgb.getGreen()/255f;
				float b = rgb.getBlue()/255f;
				gl.glBegin(GL2.GL_QUADS);
				gl.glColor4f(r, g, b, line.alpha*alphaMult);
				gl.glVertex2f(x-Config.WIDTH/2, -1*Config.HEIGHT);
				gl.glVertex2f(x-Config.WIDTH/2+line.width, -1*Config.HEIGHT);
				//gl.glColor4f(b, g, b, line.alpha);
				gl.glVertex2f(x-Config.WIDTH/2+line.width, Config.HEIGHT);
				gl.glVertex2f(x-Config.WIDTH/2, Config.HEIGHT);
				gl.glEnd();
			}
			x+=line.space;

		}
		PJOGL.gl.getGL2().glPopMatrix();
		//System.out.println(x);
	}

	private float linesLength(List<Line> lines){
		float l = 0;
		for(Line line : lines){
			l= l + line.space;
		}
		return(l);
	}
	
	
	private void processLines(){

		//clean old lines
		
		float insertPoint = this.insertPoint.v();
		boolean inserted=false;

		//delete
		float deletePoint = this.deletePoint.v();
		boolean deleteLeft = this.deleteLeft.v()==1;
		boolean deleteMobile = this.deleteMobile.v()==1;
		boolean deleteRight = this.deleteRight.v()==1;
		//System.out.println(linesLength(lines)+ " "+lines.size());

		LinkedList<Line> newLines = new LinkedList<LayerLines.Line>();
		
		float len = linesLength(lines);
		
		int j=0;
		while(j<lines.size() && len - lines.get(j).space>Config.WIDTH && (deleteLeft || deleteRight)){
			
			if(deleteIdx%3==0 && deleteLeft){
				len -= lines.get(0).space;
				lines.remove(0);
				deleteIdx++;
			}else if(deleteIdx%3==1 && deleteRight){
				len -= lines.get(lines.size()-1).space;
				lines.remove(lines.size()-1);
				deleteIdx++;
			}else if(j>0){
				deleteIdx++;
			}
			j++;
		}
		
		
		float x = 0;
		//boolean deleted=true;
		
		for(int i=0;i<lines.size();i++){
			Line line = lines.get(i);
			x+=line.space;
			
			//add
			if(onBeatAdd.v()==0 || addNow){
				if(x>=insertPoint*Config.WIDTH && !inserted){
					addLine(newLines);
					inserted=true;
					addNow=false;
				}
			}
			
			if(deleteMobile && len-line.space > Config.WIDTH && x>=Config.WIDTH*deletePoint){
				//delete
				len-=line.space;
				deleteIdx++;
			}else{
				//keep
				newLines.add(line);
			}
		}
		
		//add if not added
		if(onBeatAdd.v()==0 || addNow){
			if(len < Config.WIDTH && !inserted){
				addLine(newLines);
				addNow=false;
			}
		}
		
		j=0;
		len = linesLength(newLines);
		while(j<newLines.size() && len - newLines.get(j).space>Config.WIDTH && (deleteLeft || deleteRight)){	
			if(deleteIdx%3==0 && deleteLeft){
				len -= newLines.get(0).space;
				newLines.remove(0);
				deleteIdx++;
			}else if(deleteIdx%3==1 && deleteRight){
				len -= newLines.get(newLines.size()-1).space;
				newLines.remove(newLines.size()-1);
				deleteIdx++;
			}else if(j>0){
				deleteIdx++;
			}
			j++;
		}
		
		//publish
		lines = newLines;

	}

	private void addLine(LinkedList<Line> lines){
		float add=this.add.v();
		for(int j=0;j<add;j++){
			Line newLine = new Line();
			newLine.h=this.fgH.v();
			newLine.s=this.fgS.v();
			newLine.b=this.fgB.v();
			newLine.alpha=this.fgAlpha.v();
			newLine.width=this.size.v();
			newLine.space=this.space.v();
			if(! (newLine.space==0 && newLine.width==0)){
				lines.add(newLine);
			}
			
		}	
	}
	
	@Override
	public void drawBackground(PGraphicsOpenGL pgl){
		HSBtoRGB();
		GL2 gl = PJOGL.gl.getGL2().getGL2();
		gl.glColor4f( backgroundR/255f, backgroundG/255f, backgroundB/255f, bgAlpha.v());
		gl.getGL2().glBegin(GL2.GL_QUADS);
		gl.getGL2().glVertex2f(0f,0f);
		gl.getGL2().glVertex2f(Config.WIDTH,0f);
		gl.getGL2().glVertex2f(Config.WIDTH,Config.HEIGHT);
		gl.getGL2().glVertex2f(0f,Config.HEIGHT);
		gl.getGL2().glEnd();
	}

	@Override
	public int buildUI(GUIController controller, int x, int y, Tab tab, Group group) {

		y+=super.buildUI(controller, x, y, tab, group);
		String layerType = this.getType();
		//Bars		
		//x=750;

		controller.addControlFloatParameter(layerType,"alphaMult",alphaMult.min,alphaMult.max, x, y, tab,group);
		y+=controller.h+controller.hMargin;
		
		controller.addControlFloatParameter(layerType,"add",add.min,add.max, x, y, tab,group);  
		controller.addToggle(layerType, "onBeatAdd.v", false, x+650, y+30, tab, group).setCaptionLabel("onBeatAdd");

		int y2 = y;
		y2+=controller.h+controller.hMargin;
		y2+=controller.h+controller.hMargin;
		controller.addToggle(layerType, "deleteLeft.v", false, x+650, y2+30, tab, group).setCaptionLabel("del Left");
		y2+=controller.h+controller.hMargin;
		y2+=controller.h+controller.hMargin;
		controller.addToggle(layerType, "deleteMobile.v", false, x+650, y2+30, tab, group).setCaptionLabel("del Mobile");
		y2+=controller.h+controller.hMargin;
		y2+=controller.h+controller.hMargin;
		controller.addToggle(layerType, "deleteRight.v", false, x+650, y2+30, tab, group).setCaptionLabel("del Right");

		y+=controller.h+controller.hMargin;

		controller.addControlFloatParameter(layerType,"size",size.min,size.max, x, y, tab,group);  
		y+=controller.h+controller.hMargin;
		controller.addControlFloatParameter(layerType,"space",space.min,space.max, x, y, tab,group);  
		y+=controller.h+controller.hMargin;
		controller.addControlFloatParameter(layerType,"insertPoint",insertPoint.min,insertPoint.max, x, y, tab,group);  
		y+=controller.h+controller.hMargin;
		controller.addControlFloatParameter(layerType,"deletePoint",deletePoint.min,deletePoint.max, x, y, tab,group);  
		y+=controller.h+controller.hMargin;

		return(y);

	}

	@Override
	public void keyEvent(int key, int keyCode, char ch, boolean a, boolean c, boolean s,boolean win,boolean apl,long[] lastPressedTime) {
		super.keyEvent(key, keyCode, ch, a, c, s, win, apl, lastPressedTime);
	}

	public void soundControl(SoundController soundController){
		super.soundControl(soundController);
		SoundControllerGroup group0 = soundController.getGroups().get(0);
		if(group0.isOnBeat()){
			if(onBeatAdd.v()==1){
				addNow=true;
			}
		}
	}
	@Override
	public void update() {
		this.processLines();

		super.update();
	}

}






