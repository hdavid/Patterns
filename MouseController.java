public class MouseController{

	private Application app;
	
	private int mouseLastPressedButton=0;
	private long mouseLastPressMillis=0;

	private int mouseLastX=0;
	private int mouseLastY=0;

	private float mouseResolutionSlide	= 30f;
	private float mouseResolutionRotate	= 0.8f;
	private float mouseResolutionZoom	= 0.8f;

	public MouseController(Application app){
		this.app=app;
	}
	
	public void mousePressed(int mouseX,int mouseY,int mouseButton) {
		if(app.layers[app.currentLayer]!=null){
			mouseLastPressedButton=mouseButton;
			mouseLastX=mouseX;
			mouseLastY=mouseY;
			mouseLastPressMillis=System.currentTimeMillis();
		}
	}

	public void mouseClicked(int mouseX,int mouseY,int mouseButton) {
		
	}

	public void mouseReleased(int mouseX,int mouseY,int mouseButton) {
		
		LayerNineBlockPattern p = null;
		if(app.layers[app.currentLayer] instanceof LayerNineBlockPattern){
			p = ((LayerNineBlockPattern)app.layers[app.currentLayer]);
		}
		
		
		if(p!=null){
			int x=mouseX-mouseLastX;
			int y=mouseY-mouseLastY;
			long time=System.currentTimeMillis()-mouseLastPressMillis;
			if(x*x>25 || y*y>25){
				switch(mouseLastPressedButton){
				case 37:
					p.scrollX.tcIncPerSec=
						(float) ((Math.cos(p.angle.v())*(float)x
								+Math.sin(p.angle.v())*(float)y)
								*mouseResolutionSlide/(float)time);
					p.scrollY.tcIncPerSec=
						(float) ((-Math.sin(p.angle.v())*(float)(x)
								+Math.cos(p.angle.v())*(float)(y))
								* mouseResolutionSlide/(float)(time));
					p.scrollX.tc=true;
					p.scrollY.tc=true;
					break;
				case 39:
					int way=1;//(mouseY>height/2)?-1:1;
					p.angle.tcIncPerSec=way*(float)(x)*mouseResolutionRotate/(float)(time);
					p.angle.tc=true;
					break;
				case 3:
					p.zoom.tcIncPerSec=(float)(y)*mouseResolutionZoom/(float)(time);
					p.zoom.tc=true;
					break;
				}
			}else{
				switch(mouseLastPressedButton){
				case 37:
					p.scrollX.tc=!p.scrollX.tc;
					p.scrollY.tc=!p.scrollY.tc;
					break;
				case 39:
					p.angle.tc=!p.angle.tc;
					break;
				case 3:
					p.zoom.tc=!p.zoom.tc;
					break;
				}   
			}
			mouseLastPressedButton=0;
		}
	}
	
}

