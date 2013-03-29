import java.awt.Color;

import controlP5.Accordion;
import controlP5.Group;
import controlP5.Tab;




public abstract class LayerMovementIncremental extends LayerMovement {

	//incremental
	public FloatParameter iangle    = new FloatParameter(this,0f,-2f*(float)Math.PI,2f*(float)Math.PI,FloatParameter.FREE,"iangle");
	public FloatParameter irotate    = new FloatParameter(this,0f,-2f*(float)Math.PI,2f*(float)Math.PI,FloatParameter.FREE,"irotate");
	
	public FloatParameter izoom    = new FloatParameter(this,0f,-50,50,FloatParameter.FREE,"izoom");
	public FloatParameter iscrollX    = new FloatParameter(this,0f,-500,500,FloatParameter.FREE,"iscrollX");
	public FloatParameter iscrollY    = new FloatParameter(this,0f,-500,500,FloatParameter.FREE,"iscrollY");
	public FloatParameter isize    = new FloatParameter(this,0f,-20f,20f,FloatParameter.FREE,"isize");
	public FloatParameter ifgH    = new FloatParameter(this,0f,-1f,1f,FloatParameter.FREE,"ifgH");
	public FloatParameter ifgS    = new FloatParameter(this,0f,-1f,1f,FloatParameter.FREE,"ifgS");
	public FloatParameter ifgB    = new FloatParameter(this,0f,-1f,1f,FloatParameter.FREE,"ifgB");
	public FloatParameter ifgAlpha    = new FloatParameter(this,0f,-1f,1f,FloatParameter.FREE,"ifgAlpha");
	
	
	protected LayerMovementIncremental(Application parent){
		super(parent);	
		this.iangle.ghost=false;
		this.izoom.ghost=false;
		this.iscrollX.ghost=false;
		this.iscrollY.ghost=false;
		this.isize.ghost=false;
		this.ifgB.ghost=false;
		this.ifgH.ghost=false;
		this.ifgS.ghost=false;
		this.irotate.ghost=false;
		this.ifgAlpha.ghost=false;
	}
	
	public void HSBtoRGB(){
		HSBtoRGB(0,1);
	}


	public void HSBtoRGB(int x,int range){
		Color rgb = new Color(Color.HSBtoRGB(fgH.v(ifgH,x,range),fgS.v(ifgS,x,range),fgB.v(ifgB,x,range)));
		foregroundR = rgb.getRed();
		foregroundG = rgb.getGreen();
		foregroundB = rgb.getBlue(); 
		if(x==0){
			rgb = new Color(Color.HSBtoRGB(bgH.v(),bgS.v(),bgB.v()));
			backgroundR = rgb.getRed();
			backgroundG = rgb.getGreen();
			backgroundB = rgb.getBlue();

		}
	}

	public int buildUI(GUIController controller, int x, int y, Tab tab, Group group) {
		String layerType = this.getType();
		//Colors and Moves		
		//Foreground Color
		int h = controller.h;
		int hMargin = controller.hMargin;

		Accordion acc = controller.addAccordion(layerType, "acc", x, y, 600, h*300, tab, group);
		
		Group moveGroup=controller.addGroup(layerType, "moveGroup", 0, y, 600, h, null, null, acc);
		moveGroup.setTitle("move");
		moveGroup.setBackgroundColor(20);
		moveGroup.setBackgroundHeight(365);
		
		Group incGroup=controller.addGroup(layerType, "incGroup", 0, y, 600, h, null, null, acc);
		incGroup.setTitle("inc");
		incGroup.setBackgroundColor(20);
		incGroup.setBackgroundHeight(230);
		
		acc.open(0);

		x=5;
		y=5;
		super.buildUI(controller, x, y, null, moveGroup);
		
		
		y+=controller.addColorGroup(layerType,"inc Foreground Color","ifg",x, y, null,incGroup);
		
		y+=h+hMargin;
		y+=hMargin;
		//Moves
		controller.addControlNamesMoves(layerType,"move",x,y,null,incGroup);
		y+=h+hMargin;
		controller.addControlFloatParameter(layerType,"iscrollX",-500,500,x,y,null,incGroup);  
		y+=h+hMargin;
		controller.addControlFloatParameter(layerType,"iscrollY",-500,500, x,y,null,incGroup);
		y+=h+hMargin*2;
		controller.addControlFloatParameter(layerType,"iangle",-6,6,x,y,null,incGroup);    
		y+=h+hMargin*2;
		controller.addControlFloatParameter(layerType,"irotate",-6,6,x,y,null,incGroup);  
		y+=h+hMargin*2;
		controller.addControlFloatParameter(layerType,"izoom",-50,50,x,y,null,incGroup);    
		y+=h+hMargin*2;
		controller.addControlFloatParameter(layerType,"isize",-20,20,x,y,null,incGroup); 
		y+=h+hMargin;
		//controller.addControlFloatParameter(layerType,"number",0,50, x, y,null, incGroup); 
		//y+=h+hMargin*2;
		y+=hMargin;
	
		return(y);

	}
	


}
