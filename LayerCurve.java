
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import processing.core.PApplet;
import toxi.geom.Spline2D;
import toxi.geom.Vec2D;
import controlP5.Group;
import controlP5.Tab;


public class LayerCurve extends LayerColor {

	public FloatParameter angle     = new FloatParameter(this,0f,0f,2f*(float)Math.PI,FloatParameter.WRAP,"angle");
	public FloatParameter scrollX   = new FloatParameter(this,0f,0f,this.width,FloatParameter.WRAP,"scrollX");
	public FloatParameter scrollY   = new FloatParameter(this,0f,0f,this.height,FloatParameter.WRAP,"scrollY");

	public FloatParameter zoom      = new FloatParameter(this,1f,1f,50f,"zoom");

	public FloatParameter iangle    = new FloatParameter(this,0f,-2f*(float)Math.PI/10,2f/10*(float)Math.PI,FloatParameter.FREE,"iangle");
	public FloatParameter number = new FloatParameter(this,1f,0f,20f,FloatParameter.LIMIT,"number");
	public FloatParameter izoom    = new FloatParameter(this,0f,-1f,1f,FloatParameter.FREE,"izoom");


	public FloatParameter ratio = new FloatParameter(this,0.3f,0f,1f,FloatParameter.LIMIT,"ratio");
	public FloatParameter slowRatio = new FloatParameter(this,35f,1f,50f,FloatParameter.LIMIT,"slowRatio");
	public FloatParameter fix = new FloatParameter(this,0.2f,0f,1f,FloatParameter.LIMIT,"fix");
	public FloatParameter move = new FloatParameter(this,0.1f,0f,1f,FloatParameter.LIMIT,"move");
	public ListParameter moveNeg = new ListParameter(this, 0, ListParameter.onOff, ListParameter.onOffNames, "moveNeg");
	public FloatParameter fftPrecision = new FloatParameter(this,1f,1f,50f,FloatParameter.LIMIT,"fftPrecision");
	public FloatParameter splineRes = new FloatParameter(this,15f,1f,50f,FloatParameter.LIMIT,"splineRes");

	public FloatParameter extraRadius = new FloatParameter(this,1f,0.5f,20f,FloatParameter.LIMIT,"extraRadius");

	public ListParameter negative = new ListParameter(this, 0, ListParameter.onOff, ListParameter.onOffNames, "negative");
	public ListParameter carrier = new ListParameter(this, 0, new int[]{0,1}, new String[]{"circle","line"}, "carrier");
	public ListParameter neutralAvg = new ListParameter(this, 0,new int[]{0,1,2}, new String[]{"off","global","local"}, "neutralAvg");
	public ListParameter source = new ListParameter(this, 0, new int[]{0,1}, new String[]{"enregy","score"}, "source");


	float[] energy;
	float[] energySmooth1;
	float[] energySmooth2;
	float[] energySmooth3;
	float[] score;
	float[] scoreSmooth1;
	float[] scoreSmooth2;
	float[] scoreSmooth3;
	int cpt=0;
	float highLow = 1;

	public LayerCurve(Application parent){
		super(parent);
		setParameterMap();
	}


	@Override
	public void draw(GL2 gl){

		//GL2 glGL2 gl = PJOGL.gl;

		int number = (int)this.number.v();

		energy=parent.soundController.getZoneEnergy();
		score=parent.soundController.getGroups().get(0).getPerBandScores();


		if(energySmooth1==null){
			energySmooth1 = new float[energy.length];
		}
		if(energySmooth2==null){
			energySmooth2 = new float[energy.length];
		}
		if(energySmooth3==null){
			energySmooth3 = new float[energy.length];
		}
		if(scoreSmooth1==null){
			scoreSmooth1 = new float[energy.length];
		}
		if(scoreSmooth2==null){
			scoreSmooth2 = new float[energy.length];
		}
		if(scoreSmooth3==null){
			scoreSmooth3 = new float[energy.length];
		}



		for(int iteration=0;iteration<number;iteration++){

			//parameters
			float neutralAvg = this.neutralAvg.v();
			float r = ratio.v()*ratio.v();
			float lowE = 0;
			float highE = 0;
			float sum=0;
			float fixSize = fix.v()*fix.v();
			float moveSize = move.v()*move.v()*((moveNeg.v()==1)?-1:1);
			float firstX=0;
			float firstY=0;
			float slowRatio=0.1f/this.slowRatio.v()/this.slowRatio.v();
			//System.out.println(neutralAvg);
			boolean first=true;

			boolean negative = this.negative.v()==1;
			float radius = PApplet.sqrt(Config.WIDTH*Config.WIDTH/4+Config.HEIGHT*Config.HEIGHT/4);
			Spline2D s=new Spline2D();
			int carrier = this.carrier.v();
			float t = (int)fftPrecision.v();
			List<Float> sums = new ArrayList<Float>();

			for(int i=0;i<energy.length;i++){
				//accumulate high/low ratio
				if(i<energy.length/4){
					lowE+=energy[i];
				}else{
					highE+=energy[i];
				}
				//compute energy avgs
				energySmooth1[i] = (1-r) * energySmooth1[i] + r * energy[i];
				energySmooth2[i] = (1-r/10) * energySmooth2[i] + (r/10) * energy[i];
				energySmooth3[i] = (1-slowRatio) * energySmooth3[i] + slowRatio * energy[i];
				scoreSmooth1[i] = (1-r) * scoreSmooth1[i] + r * score[i];
				scoreSmooth2[i] = (1-r/10) * scoreSmooth2[i] + (r/10) * score[i];
				scoreSmooth3[i] = (1-slowRatio) * scoreSmooth3[i] + slowRatio * score[i];
				//sum for this sector
				if(source.v()==0)
					sum += ((energySmooth1[i]-(neutralAvg==2?energySmooth3[i]:0))*energySmooth3[i])/energySmooth3[i]*1;
				else
					sum += ((scoreSmooth1[i]-(neutralAvg==2?scoreSmooth3[i]:0))*scoreSmooth3[i])/scoreSmooth3[i]*1;

				//sector end
				if(i%t==0){
					sum = sum<0f?(-1*PApplet.sqrt(-1*sum/t)):sum==0?0:PApplet.sqrt(sum/t);
					sums.add(sum);
					sum=0;
				}
			}
			int j=0;

			float avg = Utils.average(sums.toArray(new Float[]{}));
			for(Float sum2 : sums){
				sum = sum2/t;
				if(neutralAvg==1){
					sum -=avg;
				}
				sum = sum<0f?(-1*PApplet.sqrt(-1*sum)):sum==0?0:PApplet.sqrt(sum);

				float x = 0;
				float y = 0;
				float mult = (radius*fixSize + radius*sum*moveSize);
				if(mult<=0.001f){
					mult=0.001f;
				}

				if(carrier==0f){
					x=PApplet.sin( PApplet.PI*2*(float)j/sums.size() ) * mult;
					y=-1*PApplet.cos( PApplet.PI*2*(float)j/sums.size()) * mult;
				}else{
					x=(-0.5f+(float)j/sums.size())*Config.WIDTH;
					y=mult;
				}


				s.add(x,y);
				if(first){
					firstX=x;
					firstY=y;
					first=false;
				}
				j++;
			}
			if(carrier==0){
				s.add(firstX,firstY);
			}
			//finalise high/low ratio
			highLow=highE/lowE * r + highLow * (1-r);

			HSBtoRGB();
			gl.glPushMatrix();
			gl.glTranslatef(Config.WIDTH/2,Config.HEIGHT/2,0);
			gl.glScalef(zoom.v()+izoom.v()*iteration,zoom.v()+izoom.v()*iteration,0);
			if(carrier==1){
				gl.glRotatef((angle.v()+iangle.v()*iteration)/(2*(float)Math.PI)*360,0,0,1);
			}

			gl.glTranslatef(scrollX.v(),scrollY.v(),0);
			if(carrier==0){
				gl.glRotatef((angle.v()+iangle.v()*iteration)/(2*(float)Math.PI)*360,0,0,1);
			}

			int res = (int)splineRes.v();


			if(s.pointList.size()>2){

				if(negative){
					gl.glBegin(GL2.GL_TRIANGLE_STRIP);
				}else{
					if(carrier==0){
						gl.glBegin(GL2.GL_TRIANGLE_FAN);
					}else{
						gl.glBegin(GL2.GL_TRIANGLE_STRIP);
					}
				}

				float g = gradient.v();
				float ag = 1-g;
				int c=0;
				if(!negative){		
					gl.glColor4f(foregroundR/255f,foregroundG/255f,foregroundB/255f,fgAlpha.v());
					if(carrier==0){
						gl.glVertex2f(0,0);
						gl.glColor4f(
								backgroundR/255f*g + foregroundR/255f*ag
								,backgroundG/255f*g + foregroundG/255f*ag
								,backgroundB/255f*g + foregroundB/255f*ag
								,fgAlpha.v()
								);
					}else{

					}
				}else{
					if(carrier==0){
						gl.glColor4f(
								backgroundR/255f*g + foregroundR/255f*ag
								,backgroundG/255f*g + foregroundG/255f*ag
								,backgroundB/255f*g + foregroundB/255f*ag
								,fgAlpha.v()
								);
					}else{

					}
				}

				float extraRadius = this.extraRadius.v();
				for(java.util.Iterator<?> i=s.computeVertices(res).iterator(); i.hasNext();) {

					Vec2D p=(Vec2D)i.next();
					if(c==0){
						firstX=p.x;
						firstY=p.y;
					}

					if(negative){
						if(carrier==0){
							gl.glColor4f(
									backgroundR/255f*g + foregroundR/255f*ag
									,backgroundG/255f*g + foregroundG/255f*ag
									,backgroundB/255f*g + foregroundB/255f*ag
									,fgAlpha.v()
									);
							gl.glVertex2f(p.x,p.y);
							Vec2D p2 = p.normalizeTo(radius*extraRadius);
							gl.glColor4f(foregroundR/255f,foregroundG/255f,foregroundB/255f,fgAlpha.v());
							gl.glVertex2f(p2.x,p2.y);
						}else{
							gl.glColor4f(
									backgroundR/255f*g + foregroundR/255f*ag
									,backgroundG/255f*g + foregroundG/255f*ag
									,backgroundB/255f*g + foregroundB/255f*ag
									,fgAlpha.v()
									);
							gl.glVertex2f(p.x,p.y);
							gl.glColor4f(foregroundR/255f,foregroundG/255f,foregroundB/255f,fgAlpha.v());
							gl.glVertex2f(p.x,radius*extraRadius);
						}
					}else{
						if(carrier==0){
							gl.glVertex2f(p.x,p.y);
						}else{
							gl.glColor4f(
									backgroundR/255f*g + foregroundR/255f*ag
									,backgroundG/255f*g + foregroundG/255f*ag
									,backgroundB/255f*g + foregroundB/255f*ag
									,fgAlpha.v()
									);
							gl.glVertex2f(p.x,p.y);
							gl.glColor4f(foregroundR/255f,foregroundG/255f,foregroundB/255f,fgAlpha.v());
							gl.glVertex2f(p.x,0);
						}
					}
					c++;	
				}

				//Last triangle
				if(carrier==0){
					gl.glVertex2f(firstX,firstY);
				}else{

				}

				gl.glEnd();
			}
			gl.glPopMatrix();
		}
	}

	@Override
	public void drawBackground(GL2 gl){
		//GL2 gl = PJOGL.gl;
		gl.glColor4f( backgroundR/255f, backgroundG/255f, backgroundB/255f, bgAlpha.v());
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(0f,0f);
		gl.glVertex2f(Config.WIDTH,0f);
		gl.glVertex2f(Config.WIDTH,Config.HEIGHT);
		gl.glVertex2f(0f,Config.HEIGHT);
		gl.glEnd();
	}

	@Override
	public int buildUI(GUIController controller, int x, int y, Tab tab, Group group) {
		String layerType = this.getType();

		controller.addToggle(layerType, "negative.v", false, x+650, y, tab, group).setCaptionLabel("negative");
		controller.addToggle(layerType, "moveNeg.v", false, x+650, y+30, tab, group).setCaptionLabel("moveNeg");

		controller.addRadio(layerType, "neutralAvg.v",neutralAvg.names, x+650, y+60, tab, group);

		controller.addRadio(layerType,"carrier.v",carrier.names,x+650,y+120,tab,group);

		controller.addNumber(layerType,"extraRadius.v",1f,x+650,y+180,tab,group);

		controller.addRadio(layerType,"source.v",source.names,x+650,y+230,tab,group);

		y+=super.buildUI(controller, x, y, tab, group);
		y-=20;

		controller.addControlNamesMoves(layerType,"names",x,y,tab,group);
		y+=controller.h+controller.hMargin;
		controller.addControlFloatMoveParameter(layerType,"scrollX",-20,20, x, y, tab,group);    
		y+=controller.h+controller.hMargin;
		controller.addControlFloatMoveParameter(layerType,"scrollY",-20,20, x, y, tab,group);
		y+=controller.h+controller.hMargin*2;
		controller.addControlFloatMoveParameter(layerType,"zoom",-500,500, x, y, tab,group);
		y+=controller.h+controller.hMargin*2;
		controller.addControlFloatMoveParameter(layerType,"angle",-10,10, x, y, tab,group); 
		y+=controller.h+controller.hMargin*2;

		controller.addControlFloatParameter(layerType,"iangle",iangle.min,iangle.max,x,y,tab,group);    
		y+=controller.h+controller.hMargin*2;
		controller.addControlFloatParameter(layerType,"izoom",izoom.min,izoom.max,x,y,tab,group);    
		y+=controller.h+controller.hMargin*2;

		controller.addControlFloatParameter(layerType,"number",number.min,number.max, x, y, tab,null);
		y+=controller.h+controller.hMargin*2;

		
		controller.addControlFloatParameter(layerType,"fix",fix.min,fix.max, x, y, tab,null);
		y+=controller.h+controller.hMargin;
		controller.addControlFloatParameter(layerType,"move",move.min,move.max, x, y, tab,null);
		y+=controller.h+controller.hMargin*2;

		controller.addControlFloatParameter(layerType,"ratio",ratio.min,ratio.max, x, y, tab,null);
		y+=controller.h+controller.hMargin;
		controller.addControlFloatParameter(layerType,"slowRatio",slowRatio.min,slowRatio.max, x, y, tab,null);
		y+=controller.h+controller.hMargin*2;

		controller.addControlFloatParameter(layerType,"fftPrecision",fftPrecision.min,fftPrecision.max, x, y, tab,null);
		y+=controller.h+controller.hMargin;
		controller.addControlFloatParameter(layerType,"splineRes",splineRes.min,splineRes.max, x, y, tab,null);
		y+=controller.h+controller.hMargin;


		return(y);

	}

	@Override
	public void update() {
		super.update();
	}

}
