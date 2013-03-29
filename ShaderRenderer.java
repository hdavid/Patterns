

import processing.opengl.PGraphicsOpenGL;
import processing.opengl.PShader;


public class ShaderRenderer {

	String filename;
	
	/*
	 
	 y+=h+hMargin;
		addControlFloatParameter(this,"edgeX",0,1, x, y, incrementalTab,null);
		y+=h+hMargin;
		addControlFloatParameter(this,"edgeY",0,1, x, y, incrementalTab,null);
		y+=h+hMargin;
		addControlFloatParameter(this,"edgeRatio",0,1, x, y, incrementalTab,null);
		y+=h+hMargin;
		y+=hMargin+3*(h+hMargin);
		
	 */
	
	//public FloatParameter edgeX  = new FloatParameter(this,0,0,1,"edgeX");
	//public FloatParameter edgeY  = new FloatParameter(this,0,0,1,"edgeY");
	//public FloatParameter edgeRatio  = new FloatParameter(this,1,0,1,"edgeRatio");

	ShaderRenderer(Application app,PGraphicsOpenGL pgl,String filename){
		this.app=app;
		this.pgl=pgl;
		this.filename=filename;

		try{
			/*if(new File(filename).exists()){
				filename = "file://"+new File(filename).getCanonicalPath();
			}else if(new File("Contents/Resources/Java/"+filename).exists()){
				filename = "file://"+new File("Contents/Resources/Java/"+filename).getPath();
			}else{
				filename= "file://"+filename;
			}*/
			shader = pgl.loadShader(filename);

			/*String content = "";
			BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
			String str;
			while ((str = in.readLine()) != null) {
				content += str + "\n";
			}
			in.close();

			shader.vertexShaderSource = content;
			 */	
			//pgl.setShader(shader, shaderType);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	PShader shader;
	Application app;
	PGraphicsOpenGL pgl;

	public void update(){

		//if(app.frameCount%100==0)shader = pgl.loadShader(filename, shaderType);

		//float x=0;
		//float y=0;
		//float ratio=0;

		//Layer layer = app.layers[0];
		/*if(layer instanceof LayerNineBlockPattern){
			ShaderRenderer p = ((ShaderRenderer)layer);
			if(p!=null){
				ratio=p.edgeRatio.v();
				x=p.edgeX.v();
				y=p.edgeY.v();

			}
			shader.set("x",x);
			shader.set("y", y);
			shader.set("ratio", ratio);

			pgl.shader(shader);
		}*/
	}

}
