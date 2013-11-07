import java.io.BufferedReader;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controlP5.Group;
import controlP5.Tab;
import processing.opengl.PGraphicsOpenGL;


/**
 * abstract class representing an object to drop in a layer and to draw.
 * @author mots
 */
public abstract class Layer {
	
	
	abstract public int buildUI(GUIController controller,int x, int y, Tab tab, Group group);
	abstract public void draw(PGraphicsOpenGL pgl);
	abstract public void drawBackground(PGraphicsOpenGL pgl);
	abstract public void keyEvent(int key, int keyCode, char ch, boolean alt, boolean ctrl, boolean shift,boolean win,boolean apple,long[] lastPressedTimes);
	
	final protected Application parent;
	private String name="untitled";
	protected Map<String,Parameter> parameters = new HashMap<String, Parameter>();
	
	protected ListParameter ghostLayer=new ListParameter(this,0,new int[]{0,1,2,3,4,5,6,7,8,9,10},new String[]{"0","1","2","3","4","5","6","7","8","9","10"},"ghostLayer");
	protected long lastDraw=0;
	
	Layer(Application parent){
		this.parent=parent;
	}
	
	/** must be called after the object is build to set the parameter map using reflexivity 
	 * */
	public void setParameterMap(){
		List<Field>fieldlist = Utils.getAllFields(this.getClass());
		for (Field fld : fieldlist) {
			fld.setAccessible(true);
			try{
				if(fld.get(this) instanceof Parameter){
					Parameter parameter = ((Parameter)fld.get(this));
					this.setParameter(parameter);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public Parameter getParameter(String name){
		return(parameters.get(name));
	}
	
	public void setParameter(Parameter parameter){
		parameters.put(parameter.getName(), parameter);
	}
	
	public ListParameter getListParameter(String name){
		if(parameters.get(name) instanceof ListParameter){
			return((ListParameter)parameters.get(name));
		}else{
			return(null);
		}
	}
	
	public FloatParameter getFloatParameter(String name){
		if(parameters.get(name) instanceof FloatParameter){
			return((FloatParameter)parameters.get(name));
		}else{
			return(null);
		}
	}
	
	public Collection<Parameter> getParameters(){
		return(parameters.values());
	}
	
	public String getName(){
		return(name);
	}
	
	public void setName(String name){
		this.name=name;
	}
	
	public String getType(){
		return(this.getClass().getSimpleName());
	}
	
	public Layer getGhost(){
		int v = ghostLayer.v(false);
		if(v>0){
			if(parent.layers[v-1]==this){
				//don't return myself !
				return(null);
			}else{
				Layer layer = parent.layers[v-1];
				return(layer);

			}
		}else{
			return(null);
		}
	}
	
	/**
	 * update layers variable before drawing.
	 */
	public void update(){
		if(lastDraw!=0){
			for (Parameter parameter : parameters.values()){
				parameter.doTc();
			}
		}
		//lastdraw = now
		lastDraw = System.currentTimeMillis();
	}
	
	/**
	 * perform sound control
	 * @param soundController
	 */
	public void soundControl(SoundController soundController){
		for(Parameter parameter : getParameters()){
			int groupId = parameter.getScGroup();
			if(groupId>=0 && groupId<soundController.getGroups().size()){
				SoundControllerGroup group = soundController.getGroups().get(groupId);
				parameter.doBump(group.isOnBeat());
				parameter.doSc(group.isOnBeat());
				parameter.doScAlter(group.isOnBeat());
				parameter.doModulation(group.getModulation());
			}
		}
	}
	
	/**
	 * clone the Layer by creating a new layer and cloning each Parameter.
	 * other variables must be copied by over loading this function.
	 */
	public Layer clone(){
		Layer layer = Layer.newLayer(parent, this.getType());
		try {  	 
			List<Field> fields = Utils.getAllFields(this.getClass());
			for (Field fld : fields) {
				fld.setAccessible(true);//just in case
				if(fld.get(this) instanceof Parameter){
					Parameter param = ((Parameter)fld.get(this)).clone(layer);
					fld.set(layer, param);
				}else{
					//blindly copy other data for now...
					//fld.set(layer, fld.get(this));
				}
			}
		}
		catch (Throwable e) {
			System.err.println(e);
		}
		layer.setParameterMap();
		return(layer);
	}
	
	public String toString(){
		StringBuilder s = new StringBuilder();
		s.append("layerType:"+this.getClass().getSimpleName()+"\n");
		s.append("name:"+this.getName()+"\n");
		for (Parameter parameter : parameters.values()) {
			s.append(parameter.toString());
		}
		return (s.toString());
	}
	
	
	public static Layer newLayer(Application parent,String layerType){
		try {
			Constructor<?> constructor = Class.forName(layerType).getConstructor(new Class[]{Application.class});
			if(constructor==null){
				System.err.println("class "+layerType+" misses contructor "+layerType+"(Application parent)");
			}else{
				Layer layer = (Layer) constructor.newInstance(parent);
				return(layer);
			}
		} catch (Exception e) {
			System.err.println("error creating layer "+layerType+e.getMessage());
		}
		return(null);
	}
	
	public static Layer loadLayer(Application parent,String data){

		Layer layer = newLayer(parent, "LayerNineBlockPattern");
		
		//detect old layer types.
		if(data.contains("bar1:1;")||data.contains("bar2:1;")){
			layer = newLayer(parent, "LayerBars");
		}else if(data.contains("c1B:1.0;")){
			layer = newLayer(parent, "LayerNoShape");
		}else if(data.contains("qsdfqsdf")){
			layer = newLayer(parent, "LayerBars");
		}
		try {
			BufferedReader in = new BufferedReader(new StringReader(data));
			String line;
			StringBuilder missingParameters = new StringBuilder();
			while ((line = in.readLine()) != null) {
				int index = line.indexOf(":");
				if(index!=-1){
					String key = line.substring(0, index);
					String value = line.substring(index+1);
					Parameter parameter = layer.getParameter(key);
					if(key.equals("layerType")){
						//create layer, usually this line comes first in the preset file
						layer = newLayer(parent, value);
					}else if(key.equals("name")){
						layer.setName(value);
					}else if(parameter==null){
						missingParameters.append(key+" ");
					}else{
						parameter.load(value);
					}
				}else{
					System.err.println("could not parse parameter: "+line);
				}
			}
			if(missingParameters.length()>0){
				System.err.println(layer.getType()+" does not support "+missingParameters.toString());
			}
			in.close();
			layer.setParameterMap();
			return(layer);
		} catch (Exception e) {
			System.out.println("error while loading decoding String in load()");
			e.printStackTrace();
			layer = null;
		}
		return(null);
	}


}
