
import java.awt.Color;

import controlP5.Group;
import controlP5.Tab;

import processing.opengl.PGraphicsOpenGL;

public class LayerDMX extends Layer {

	private Application parent;

	public LayerDMX(Application parent){
		super(parent);
		setParameterMap();
	}

	//DMX
	public FloatParameter dmx1dst    = new FloatParameter(this,1,-0f,512f,"dmx1dst");
	public FloatParameter dmx2dst    = new FloatParameter(this,4,-0f,512f,"dmx2dst");
	public FloatParameter dmx3dst    = new FloatParameter(this,7,-0f,512f,"dmx3dst");
	public FloatParameter dmx4dst    = new FloatParameter(this,10,-0f,512f,"dmx4dst");
	public FloatParameter dmx5dst    = new FloatParameter(this,13,-0f,512f,"dmx5dst");
	public FloatParameter dmx6dst    = new FloatParameter(this,16,-0f,512f,"dmx6dst");

	public FloatParameter dmx1H    = new FloatParameter(this,16,-0f,1f,"dmx1H");
	public FloatParameter dmx1S    = new FloatParameter(this,16,-0f,1f,"dmx1S");
	public FloatParameter dmx1B    = new FloatParameter(this,16,-0f,1f,"dmx1B");


	@Override
	public void draw(PGraphicsOpenGL pgl){
		for(FloatParameter dmxSrc: new FloatParameter[]{dmx1dst,dmx1dst,dmx1dst,dmx1dst,dmx1dst,dmx1dst}){
			ouputColor(
					getFloatParameter(dmxSrc.getName()+"H"),
					getFloatParameter(dmxSrc.getName()+"S"),
					getFloatParameter(dmxSrc.getName()+"B"),
					(int)getFloatParameter(dmxSrc.getName()+"dst").v(),
					true,
					false
					);
		}
	}

	private void ouputColor(FloatParameter h, FloatParameter s, FloatParameter b, int channel, boolean rgbMode, boolean bits16){
		if(parent.dmxP512!=null){
			if(channel!=0){
				if(rgbMode){
					Color rgb = new Color(Color.HSBtoRGB(h.v(),s.v(),b.v()));
					int red=rgb.getRed();
					int green=rgb.getGreen();
					int blue=rgb.getBlue();
					this.parent.dmxP512.set(channel,new int[]{red,green,blue});
				}else{
					this.parent.dmxP512.set(channel,new int[]{(int)(h.v()*255),(int)(s.v()*255),(int)(b.v()*255)});
				}
			}
		}
	}


	@Override
	public void drawBackground(PGraphicsOpenGL pgl){}

	@Override
	public int buildUI(GUIController controller, int x, int y, Tab tab, Group group) {
		String layerType = this.getType();
		controller.addLabel(layerType,"dmx label","Enable DMX",x,y,tab,group);
		int h = controller.h;
		int hMargin = controller.hMargin;
		
		y+=h+hMargin;
		controller.addToggle(layerType,"dmx.v",false ,x,y,10,h,tab,group).setCaptionLabel("");
		y+=h+hMargin*2;
		for (int i=0;i<6;i++){
			controller.addLabel(layerType,"dst "+(i+1)+"","dst "+(i+1)+"",x+i*40,y,tab,group);
			y+=h+hMargin;
			controller.addNumber(layerType,"dmx"+(i+1)+"dst.v"   ,0,x+i*40,y,tab,group).setCaptionLabel("");
			y+=h+hMargin;
			controller.addLabel(layerType,"dmx"+i+"srclbl","src",x+i*40,y,tab,group);
			y+=h+hMargin;
			//addRadio(673,"dmx"+(i+1)+"src.v",x+i*40,y,LayerNineBlockPattern.dmxSourceNames,LayerNineBlockPattern.dmxSources,tab,null);
		}
		return(y);
	}
	
	public void keyEvent(int key, int keyCode, char ch, boolean a, boolean c, boolean s,boolean win,boolean apl,long[] lastPressedTime) {
	}

}

