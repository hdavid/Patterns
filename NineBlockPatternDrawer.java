
import javax.media.opengl.GL2;

import processing.core.PShape;

public class NineBlockPatternDrawer{

	//OPENGL
	private int patternList=-1; 
	private PShape shape;
	public boolean useGlList=false;
    public boolean useShape=false;
	private LayerNineBlockPattern layer;
    private float[] rotations = new float[9];
    public int positionsX[];
    public int positionsY[];
    public float gradients[];
    
	  public void rotations(){
	  		float ratio = layer.subRotate.v();
		    //center
		    rotations[0]= 0;
		    rotations[1]= ratio* 2f*(float) Math.PI/2f;//xy X
		    rotations[2]= ratio* 0f*(float) Math.PI/2f;//y
		    rotations[3]= ratio* 1f*(float) Math.PI/2;//xy X
		    rotations[4]= ratio*-1f*(float) Math.PI/2;//x
		    rotations[5]= ratio* 0f*(float) Math.PI/2f;//xy X
		    rotations[6]= ratio* 2f*(float) Math.PI/2;//y
		    rotations[7]= ratio*-1f*(float) Math.PI/2;//xy X
		    rotations[8]= ratio* 1f*(float) Math.PI/2;//x
	  }
	  
	  /**
	   * fill rotation and positions arrays
	   */
	   public void computePositions(){
	     positionsX = new int[9];
	     positionsX[0]=3*this.layer.width/6;
	     positionsX[1]=1*this.layer.width/6;
	     positionsX[2]=3*this.layer.width/6;
	     positionsX[3]=5*this.layer.width/6;
	     positionsX[4]=5*this.layer.width/6;
	     positionsX[5]=5*this.layer.width/6;
	     positionsX[6]=3*this.layer.width/6;
	     positionsX[7]=1*this.layer.width/6;
	     positionsX[8]=1*this.layer.width/6;

	     positionsY = new int[9];
	     positionsY[0]=3*this.layer.width/6;
	     positionsY[1]=5*this.layer.width/6;
	     positionsY[2]=5*this.layer.width/6;
	     positionsY[3]=5*this.layer.width/6;
	     positionsY[4]=3*this.layer.width/6;
	     positionsY[5]=1*this.layer.width/6;
	     positionsY[6]=1*this.layer.width/6;
	     positionsY[7]=1*this.layer.width/6;
	     positionsY[8]=3*this.layer.width/6;
	     
	     gradients = new float[4];
	     gradients[0]=0.0f;
	     gradients[1]=1.f;
	     gradients[2]=1.0f;
	     gradients[3]=1f;

	   }
	
	public void set(LayerNineBlockPattern layer,GL2 gl){
		this.layer=layer;
		computePositions();
		
	}
	
	
	public void draw(GL2 gl){
		//gl = PJOGL.gl.getGL2();
		if(useShape){
			
		}else if(useGlList && patternList==-1){
			this.patternList = gl.glGenLists(1);
		}
		//compute rotations
		 rotations();
		//transform
		gl.glPushMatrix();
		gl.glTranslatef(Config.WIDTH/2,Config.HEIGHT/2,0);
		gl.glScalef(layer.zoom.v(),layer.zoom.v(),0);
		gl.glRotatef(layer.angle.v()/(2*(float)Math.PI)*360,0,0,1);
		// gl.glTranslatef(-width/2,-Config.HEIGHT/2,0);  
		gl.glTranslatef(layer.scrollX.v(),layer.scrollY.v(),0);
		
		fillAll(gl);
		gl.glPopMatrix();

	}

	private void fillAll(GL2 gl){
		if(useShape){
			makeShape(gl);
		}else if(useGlList){
			makeList(gl);
		}
		gl.glColor4f( layer.foregroundR/255f, layer.foregroundG/255f, layer.foregroundB/255f,1);

		int range = 2+(int)(
				(	Math.min(layer.width,layer.height)*layer.zoom.v()
					+
					Math.sqrt(Config.WIDTH*Config.WIDTH+Config.HEIGHT*Config.HEIGHT))
				/
				(Math.min(layer.width,layer.height)*layer.zoom.v())
				/ 2
		);
		int incRange=range*4;
		//System.out.println(range);
		//if(layer.number.v>-10)
		//	range=(int)layer.number.v();
		for(int x=0;x<=range;x++){
			layer.xOffset=layer.width*x;
			for(int y=0;y<=range;y++){        
				layer.yOffset=layer.height*y;

				paint(layer.xOffset,layer.yOffset,+range+x + range+y,incRange, gl);
				if(x!=0){
					paint(-layer.xOffset,layer.yOffset,range-x + range+y,incRange,gl);
				}
				if(y!=0){
					paint(layer.xOffset,-layer.yOffset,range+x + range-y,incRange,gl);
				}
				if(x!=0&&y!=0){
					paint(-layer.xOffset,-layer.yOffset,range-x + range-y,incRange,gl);
				}
			}
		}
	}


	public void paintBackground(GL2 gl){
		//gl = PJOGL.gl.getGL2();
		gl.glColor4f( layer.backgroundR/255f, layer.backgroundG/255f, layer.backgroundB/255f,layer.bgAlpha.v());
		gl.glBegin(GL2.GL_QUADS);
		gl.glVertex2f(0f,0f);
		gl.glVertex2f(Config.WIDTH,0f);
		gl.glVertex2f(Config.WIDTH,Config.HEIGHT);
		gl.glVertex2f(0f,Config.HEIGHT);
		gl.glEnd();
	}


	private void paint(float x,float y,int inc,int range,GL2 gl){
		gl.glPushMatrix();
		gl.glTranslatef(x+layer.iscrollX.v()*inc,y+layer.iscrollY.v()*inc,0);
		if(useShape){
			if(shape!=null){
				layer.parent.shape(shape);
			}
		}else if(useGlList){
			gl.getGL2().glCallList(patternList);
		}else{
			drawOnePattern(inc,range,gl);
		}
		gl.glPopMatrix();
	}

	private void drawOnePattern(int inc,int range,GL2 gl){ 
		if(inc!=0){
			layer.HSBtoRGB(inc, range);
		}
		//paint center
		paintShape(layer.shapeCenter.v(),0,inc,range,gl);
		//paint other shapes
		paintShape(layer.shapeCorner.v(),1,inc,range,gl);
		paintShape(layer.shapeSide.v(),2,inc,range,gl);
		paintShape(layer.shapeCorner.v(),3,inc,range,gl);
		paintShape(layer.shapeSide.v(),4,inc,range,gl);
		paintShape(layer.shapeCorner.v(),5,inc,range,gl);
		paintShape(layer.shapeSide.v(),6,inc,range,gl);
		paintShape(layer.shapeCorner.v(),7,inc,range,gl);
		paintShape(layer.shapeSide.v(),8,inc,range,gl);
	}
	
	private void makeList(GL2 gl){ 
		//layer.parent.beginRecord(pgl);
		gl.glNewList(patternList,GL2.GL_COMPILE);
		drawOnePattern(0,0,gl);
		gl.glEndList();
		//layer.parent.endRecord();
	}

	private void makeShape(GL2 gl){ 	
		shape = layer.parent.createShape();
		drawOnePattern(0,0,gl);
		//shape.endShape();
	}

	private void paintShape(int shapeNr,int position,int inc,int range,GL2 gl){
		
		float r1 = rotations[position]/(2*(float)Math.PI)*360;
		float r2 = layer.rotate.v(layer.irotate,inc,range)/(2*(float)Math.PI)*360;
		float r3=0;	
		if(position==0){
			r3=layer.shapeCenterAngle.v();
		}else if(position%2==0){
			r3=layer.shapeSideAngle.v();
		}else{
			r3=layer.shapeCornerAngle.v();
		}
		float r = (r1+r2+r3)%360;
	
		float size = layer.size.v(layer.isize,inc,range);
		
		boolean skipTransformations = r==0 && size==1;
		
		if(!skipTransformations){
			gl.glPushMatrix();
			gl.glTranslatef(positionsX[position],positionsY[position],0);
			if(r!=0){
				gl.glRotatef(r,0,0,1);
			}
			if(size!=1){
				gl.glScalef(size,size,0);
			}
			gl.glTranslatef(-(positionsX[position]),-(positionsY[position]),0);
		}
		
		switch(shapeNr){
		case 1: 
			paintShape1(position,inc,range,gl); 
			break;
		case 2: 
			paintShape2(position,inc,range,gl); 
			break;
		case 3: 
			paintShape3(position,inc,range,gl); 
			break;
		case 4: 
			paintShape4(position,inc,range,gl); 
			break;
		case 5: 
			paintShape5(position,inc,range,gl); 
			break;
		case 6: 
			paintShape6(position,inc,range,gl); 
			break;
		case 7: 
			paintShape7(position,inc,range,gl); 
			break;
		case 8: 
			paintShape8(position,inc,range,gl); 
			break;
		case 9: 
			paintShape9(position,inc,range,gl); 
			break;
		case 10: 
			paintShape10(position,inc,range,gl); 
			break;
		case 11: 
			paintShape11(position,inc,range,gl); 
			break;
		case 12: 
			paintShape12(position,inc,range,gl); 
			break;
		case 13: 
			paintShape13(position,inc,range,gl); 
			break;
		case 14: 
			paintShape14(position,inc,range,gl); 
			break;
		case 15: 
			paintShape15(position,inc,range,gl); 
			break;
		case 16: 
			paintShape16(position,inc,range,gl); 
			break;
		}

		if(!skipTransformations){
			gl.glPopMatrix();
		}

	}


	private void paintShape1(int position,int inc,int range,GL2 gl){
		quadri(inc,range,position,
				-layer.width/6,-layer.width/6,
				layer.width/6,-layer.width/6,
				layer.width/6,layer.width/6,
				-layer.width/6,layer.width/6,gl);
	}
	private void paintShape2(int position,int inc,int range,GL2 gl){
		tri(inc,range,position,
				-layer.width/6,-layer.width/6,
				layer.width/6,-layer.width/6,
				-layer.width/6,layer.width/6,gl);
	}
	private void paintShape3(int position,int inc,int range,GL2 gl){
		tri(inc,range,position,
				-layer.width/6,layer.width/6,
				0,-layer.width/6,
				layer.width/6,layer.width/6,gl);
	}
	private void paintShape4(int position,int inc,int range,GL2 gl){
		quadri(inc,range,position,
				-layer.width/6,-layer.width/6,
				0,-layer.width/6,
				0,layer.width/6,
				-layer.width/6,layer.width/6,gl);
	}
	private void paintShape5(int position,int inc,int range,GL2 gl){
		quadri(inc,range,position,
				0,0 -layer.width/6,
				layer.width/6,0+ 0,
				0, layer.height/6,
				-layer.width/6,0+ 0,gl);
	}
	private void paintShape6(int position,int inc,int range,GL2 gl){
		quadri(inc,range,position,
				-layer.width/6, -layer.width/6,
				layer.width/6, 0,
				layer.width/6,layer.width/6,
				0, layer.width/6,gl);
	}
	private void paintShape7(int position,int inc,int range,GL2 gl){
		tri(inc,range,position,
				0, -layer.width/6,
				layer.width/12, 0,
				-layer.width/12, 0,gl);
		tri(inc,range,position,
				layer.width/12, 0,
				layer.width/6, layer.width/6,
				0, layer.width/6,gl);
		tri(inc,range,position,
				-layer.width/12, 0,
				0, layer.width/6,
				-layer.width/6, layer.width/6,gl);
	}
	private void paintShape8(int position,int inc,int range,GL2 gl){
		tri(inc,range,position,
				-layer.width/6, -layer.width/6,
				layer.width/6, 0,
				0, layer.width/6,gl);
	}
	private void paintShape9(int position,int inc,int range,GL2 gl){
		quadri(inc,range,position,
				-layer.width/12, -layer.width/12,
				layer.width/12,-layer.width/12,
				layer.width/12, layer.width/12,
				-layer.width/12, layer.width/12,gl);
	}
	private void paintShape10(int position,int inc,int range,GL2 gl){
		tri(inc,range,position,
				0,0 -layer.width/6,
				layer.width/6, -layer.width/6,
				0, 0,gl);
		tri(inc,range,position,
				-layer.width/6, 0,
				0, 0,
				-layer.width/6, layer.width/6,gl);
	}
	private void paintShape11(int position,int inc,int range,GL2 gl){
		quadri(inc,range,position,
				-layer.width/6, -layer.width/6,
				-layer.width/6, 0,
				0,0,
				0,-layer.width/6,gl);
	} 
	private void paintShape12(int position,int inc,int range,GL2 gl){
		tri(inc,range,position,
				-layer.width/6, 0,
				layer.width/6, 0,
				0, -layer.width/6,gl);
	}
	private void paintShape13(int position,int inc,int range,GL2 gl){
		tri(inc,range,position,
				0,0,
				layer.width/6, layer.width/6,
				-layer.width/6,layer.width/6,gl);
	}
	private void paintShape14(int position,int inc,int range,GL2 gl){
		tri(inc,range,position,
				0,-layer.width/6,
				0,0,
				-layer.width/6,0,gl);
	}
	private void paintShape15(int position,int inc,int range,GL2 gl){
		tri(inc,range,position,
				0,-layer.width/6,
				-layer.width/6,0,
				-layer.width/6,-layer.width/6,gl);
	}
	private void paintShape16(int position,int inc,int range,GL2 gl){}

	/*
	private void tri(int position, int x1,int y1,int x2,int y2,int x3,int y3){
		float ratio=1; 

		aplayer.noStroke();
		aplayer.beginShape(aplayer.TRIANGLE);
		ratio=gradients[0]*layer.gradient.v();
		aplayer.color(
				(1-ratio)*layer.foregroundR/255f +(ratio)*layer.backgroundR/255f 
				,    (1-ratio)*layer.foregroundG/255f +(ratio)*layer.backgroundG/255f,
				(1-ratio)*layer.foregroundB/255f +(ratio)*layer.backgroundB/255f
				,layer.fgAlpha.v()
		);
		aplayer.vertex(positionsX[position]+x1,positionsY[position]+y1);

		ratio=gradients[1]*layer.gradient.v();
		aplayer.color(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,layer.fgAlpha.v()
		);       
		aplayer.vertex(positionsX[position]+x2,positionsY[position]+y2);

		ratio=gradients[2]*layer.gradient.v(); 
		aplayer.color(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,layer.fgAlpha.v()
		);       
		aplayer.vertex(positionsX[position]+x3,positionsY[position]+y3);

		aplayer.endShape();
	}

	private void quadri(int position,int x1, int y1,int x2,int y2,int x3,int y3,int x4,int y4){
		float ratio=1; 
		aplayer.noStroke();
		aplayer.beginShape(aplayer.QUAD);
		ratio=gradients[0]*layer.gradient.v(); 
		aplayer.color(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,layer.fgAlpha.v()
		);
		aplayer.vertex(positionsX[position]+x1,positionsY[position]+y1);

		ratio=gradients[1]*layer.gradient.v(); 
		aplayer.color(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,layer.fgAlpha.v()
		);       
		aplayer.vertex(positionsX[position]+x2,positionsY[position]+y2);

		ratio=gradients[2]*layer.gradient.v(); 
		aplayer.color(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,layer.fgAlpha.v()
		);       
		aplayer.vertex(positionsX[position]+x3,positionsY[position]+y3);

		ratio=gradients[3]*layer.gradient.v(); 
		aplayer.color(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,layer.fgAlpha.v()
		);       
		aplayer.vertex(positionsX[position]+x4,positionsY[position]+y4);
		aplayer.endShape(); 
	}

	
	
*/
	private void tri(int inc, int range,int position, int x1,int y1,int x2,int y2,int x3,int y3,GL2 gl){
		float ratio=1; 
		gl.glBegin(GL2.GL_TRIANGLES);
		ratio=gradients[0]*layer.gradient.v();
		float alpha=layer.fgAlpha.v(layer.ifgAlpha,inc+1,range);
		gl.glColor4f(
				(1-ratio)*layer.foregroundR/255f +(ratio)*layer.backgroundR/255f 
				,    (1-ratio)*layer.foregroundG/255f +(ratio)*layer.backgroundG/255f,
				(1-ratio)*layer.foregroundB/255f +(ratio)*layer.backgroundB/255f
				,alpha
		);
		gl.glVertex2f(positionsX[position]+x1,positionsY[position]+y1);

		ratio=gradients[1]*layer.gradient.v();
		gl.glColor4f(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,alpha
		);       
		gl.glVertex2f(positionsX[position]+x2,positionsY[position]+y2);

		ratio=gradients[2]*layer.gradient.v(); 
		gl.glColor4f(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,alpha
		);       
		gl.glVertex2f(positionsX[position]+x3,positionsY[position]+y3);

		gl.glEnd();
	}

	private void quadri(int inc, int range,int position,int x1, int y1,int x2,int y2,int x3,int y3,int x4,int y4,GL2 gl){
		float ratio=1; 
		gl.glBegin(GL2.GL_QUADS);
		ratio=gradients[0]*layer.gradient.v(); 
		float alpha=layer.fgAlpha.v(layer.ifgAlpha,inc+1,range);
		
		gl.glColor4f(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,alpha
		);
		gl.getGL2().glVertex2f(positionsX[position]+x1,positionsY[position]+y1);

		ratio=gradients[1]*layer.gradient.v(); 
		gl.glColor4f(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,alpha
		);       
		gl.getGL2().glVertex2f(positionsX[position]+x2,positionsY[position]+y2);

		ratio=gradients[2]*layer.gradient.v(); 
		gl.glColor4f(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,alpha
		);       
		gl.getGL2().glVertex2f(positionsX[position]+x3,positionsY[position]+y3);

		ratio=gradients[3]*layer.gradient.v(); 
		gl.glColor4f(
				(1-ratio)*(layer.foregroundR)/255.0f +(ratio)*(layer.backgroundR)/255.0f 
				,    (1-ratio)*(layer.foregroundG)/255.0f +(ratio)*(layer.backgroundG)/255.0f ,
				(1-ratio)*(layer.foregroundB)/255.0f +(ratio)*(layer.backgroundB)/255.0f
				,alpha
		);       
		gl.glVertex2f(positionsX[position]+x4,positionsY[position]+y4);
		gl.glEnd(); 
	}

}





