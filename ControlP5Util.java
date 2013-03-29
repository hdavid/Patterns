import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controlP5.Accordion;
import controlP5.Button;
import controlP5.ControlP5;
import controlP5.ControllerGroup;
import controlP5.Group;
import controlP5.Numberbox;
import controlP5.RadioButton;
import controlP5.Slider;
import controlP5.Tab;
import controlP5.Textfield;
import controlP5.Textlabel;
import controlP5.Toggle;


public abstract class ControlP5Util  {

	protected ControlP5 controlP5;
	public int wMargin = 4;
	public int hMargin = wMargin;
	protected int toggleActiveColor=1;
	protected int selectorActiveColor=1;
	private List<Textfield> textFields = new ArrayList<Textfield>();
	
	public int w = 100;
	public int h = 10;
	
	public abstract void update();
	public abstract void setup();
	
	
	public boolean textFieldIsFocus(){
		for(Textfield textField : textFields){
			if(textField.isFocus()){
				return(true);
			}
		}
		return(false);
	}
	
	public  Textfield addTextfield(String layerType, String name, String value, int x, int y, Integer w, Integer h, Tab tab, Group group){
		Textfield c = this.controlP5.addTextfield(getName(layerType, name));
		c.setWidth(getW(w));
		c.setHeight(getH(h));
		moveTo(c, tab, group, x, y);
		textFields.add(c);
		return(c);
	}
	
	public  Slider addSlider(String layerType, String name, float def,float min, float max, int x, int y, Integer w, Integer h, Tab tab, Group group){
		Slider c = this.controlP5.addSlider(getName(layerType, name));
		c.setDefaultValue(def);
		c.setMax(max);
		c.setMin(min);
		c.setWidth(getW(w));
		c.setHeight(getH(h));
		moveTo(c, tab, group, x, y);
		return(c);
	}

	public  Toggle addToggle(String layerType, String name, boolean def, int x, int y, Tab tab, Group group){
		return(addToggle(layerType, name, def, x, y, null, null, tab, group));
	}
	public  Toggle addToggle(String layerType, String name, boolean def, int x, int y, Integer w, Integer h, Tab tab, Group group){
		Toggle c = this.controlP5.addToggle(getName(layerType, name));
		c.setDefaultValue(def?1:0);
		c.setWidth(w!=null?w:this.h);
		c.setHeight(getH(h));
		moveTo(c, tab, group, x, y);
		c.setColorActive(toggleActiveColor);
		return(c);
	}

	public  Button addButton(String layerType, String name, int x, int y, Integer w, Integer h, Tab tab, Group group){
		Button c = this.controlP5.addButton(getName(layerType, name));
		c.setWidth(w!=null?w:this.h);
		c.setHeight(getH(h));
		moveTo(c, tab, group, x, y);
		c.setColorActive(toggleActiveColor);
		return(c);
	}

	public  Numberbox addNumber(String layerType, String name, float def, int x, int y, Tab tab, Group group){
		return addNumber(layerType, name, def, null, null, null, x, y, tab, group);
	}
	public  Numberbox addNumber(String layerType, String name, float def, Integer precision, int x, int y, Tab tab, Group group){
		return addNumber(layerType, name, def, null, null, precision, x, y, tab, group);
	}
	public  Numberbox addNumber(String layerType, String name, float def,Float min, Float max, Integer precision, int x, int y, Tab tab, Group group){
		Numberbox c = this.controlP5.addNumberbox(getName(layerType, name));
		c.setDefaultValue(def);
		c.setWidth(w/4);
		c.setHeight(h);
		//nb.setId(id);
		if(max!=null){
			c.setMax(max);
		}
		if(min!=null){
			c.setMin(min);
		}
		if(precision!=null){
			c.setDecimalPrecision(precision);
		}else{
			c.setDecimalPrecision(0);
		}
		//c.setWidth(getW(w));
		//c.setHeight(getH(h));
		moveTo(c, tab, group, x, y);
		return(c);
	}

	public  RadioButton addRadio(String layerType, String name, String[] names, int x, int y, Tab tab, Group group){
		return(addRadio(layerType, name, names, x, y, null, null, tab, group));
	}
	public  RadioButton addRadio(String layerType, String name, String[] names, int x, int y, Integer w, Integer h, Tab tab, Group group){
		RadioButton c = this.controlP5.addRadio(getName(layerType, name));
		c.setWidth(getW(w));
		c.setHeight(getH(h));
		int hh=this.h;
		if(h!=null){
			hh=h;
		}
		c.setSize(hh,hh+1)
		.setColorActive(selectorActiveColor)
		.setItemsPerRow(1)
		.setSpacingColumn(50)
		.setNoneSelectedAllowed(false)
		.setSpacingRow(this.hMargin-1);

		for(int i=0;i<names.length;i++){
			c.addItem(name+"_"+i+"_"+names[i], i);
			c.getItem(i).getCaptionLabel().set(names[i]);
			//c.getItem(i).setInternalValue(values[i]);
		}

		moveTo(c, tab, group, x, y);
		return(c);
	}

	public  Textlabel addLabel(String layerType, String name,String value,int x, int y, Tab tab, Group group){
		Textlabel c = this.controlP5.addTextlabel(getName(layerType, name));
		c.setText(value);
		//c.setWidth(getW(w));
		//c.setHeight(getH(h));
		moveTo(c, tab, group, x, y);
		return(c);
	}


	public  Group addGroup(String layerType, String name, int x, int y, int w, int h, Tab tab, Group group, Accordion accordion){
		Group c = this.controlP5.addGroup(getName(layerType, name));
		c.setWidth(w);
		c.setHeight(h);
		moveTo(c, tab, group, accordion, x, y);
		return(c);
	}

	public  Accordion addAccordion(String layerType, String name, int x, int y, Integer w, Integer h, Tab tab, Group group){
		Accordion c = this.controlP5.addAccordion(getName(layerType, name));
		if(w!=null){
			c.setWidth(w);
		}
		if(h!=null){
			c.setHeight(h);
		}
		moveTo(c, tab, group, x, y);
		return(c);
	}

	
	private  String getName(String layerType, String name){
		if(layerType!=null){
			return(layerType+"."+name);
		}
		return(name);
	}


	/*private  void moveTo(controlP5.Controller<?> c, Tab tab, Group group, Accordion accordion){
		moveTo(c, tab, group, accordion,null,null);
	}*/

	private  void moveTo(controlP5.Controller<?> c, Tab tab, Group group, Integer x, Integer y){
		if(x!=null && y !=null){
			c.setPosition(x, y);
		}
		if(tab!=null){
			c.moveTo(tab);
		}
		if(group!=null){
			c.moveTo(group);
		}
	}

	private  void moveTo(RadioButton c, Tab tab, Group group, Integer x, Integer y){
		if(x!=null && y !=null){
			c.setPosition(x, y);
		}
		if(tab!=null){
			c.moveTo(tab);
		}
		if(group!=null){
			c.moveTo(group);
		}
	}
	private  void moveTo(Accordion c, Tab tab, Group group, Integer x, Integer y){
		if(x!=null && y !=null){
			c.setPosition(x, y);
		}
		if(tab!=null){
			c.moveTo(tab);
		}
		if(group!=null){
			c.moveTo(group);
		}
	}
	
	private  void moveTo(Group c, Tab tab, Group group, Accordion accordion, Integer x, Integer y){
		if(x!=null && y !=null){
			c.setPosition(x, y);
		}
		if(tab!=null){
			c.moveTo(tab);
		}
		if(group!=null){
			c.moveTo(group);
		}
		if(accordion!=null){
			accordion.addItem(c);
		}
	}


	private  int getW(Integer w){
		if(w!=null){
			return(w);
		}
		return(this.w);
	}

	private  int getH(Integer h){
		if(h!=null){
			return(h);
		}
		return(this.h);
	}

	
	Map<String,controlP5.Controller<?>> controllers = new HashMap<String,controlP5.Controller<?>>();
	
	protected controlP5.Controller<?> getController(String layerType, String name){
		name = getName(layerType,name);
		controlP5.Controller<?> c = controllers.get(name);
		if(c==null){
			c=controlP5.getController(name);
			controllers.put(name,c);
		}
		return(c);
	
	}

	
	protected ControllerGroup<?> getControllerGroup(String layerType, String name){
		if(layerType==null){
			return(controlP5.getGroup(name));
		}else{
			return(controlP5.getGroup(getName(layerType,name)));
		}
	}

	
	
	protected  void updateController(String layerType,String name, float value){
		controlP5.Controller<?> c = getController(layerType,name);
		if(c!=null){
			updateController(getController(layerType,name),value);
		}else{
			ControllerGroup<?> cg = getControllerGroup(layerType,name);
			if(cg instanceof RadioButton){
				updateController((RadioButton)cg,value);
			}
		}
	}
	protected  void updateController(controlP5.Controller<?> c, float value){
		if(c!=null && c.getValue()!=value){
			c.setBroadcast(false);
			c.setValue(value);
			c.setBroadcast(true);
		}
	}
	
	protected void updateController(RadioButton r, float value){
		updateControllerRadioHack(r,value);
		/*
		if(r!=null && !r.getState((int)value)){
			for (Toggle item : r.getItems()) {
				item.setBroadcast(false);
			}
			r.deactivateAll();
			//r.activate((int)value);
			r.getItem((int)value).setValue(true);
			System.out.println(r);
			for (Toggle item : r.getItems()) {
				item.setBroadcast(true);
			}
		}*/
	}
	Field f =null;
	Field f2 =null;
	public void updateControllerRadioHack(RadioButton r, float value){
		try{
			if(f==null){
				Field fieldlist[] = Toggle.class.getDeclaredFields();
				for(Field fieldd : fieldlist){
					//System.out.println(fieldd.getName());
					if(fieldd.getName().equals("isOn")){
						if("boolean".equals(fieldd.getType().getName())){
							f = fieldd;
							f.setAccessible( true);
						}
					}
				}		
			}
			if(f2==null){
				Field fieldlist[] = Controller.class.getDeclaredFields();
				for(Field fieldd : fieldlist){
					if(fieldd.getName().equals("_myValue")){
						if("float".equals(fieldd.getType().getName())){
							f2 = fieldd;
							f2.setAccessible(true);
						}
					}
				}		
			}

			int n = r.getItems().size();
			//if (value < n) {
			for (int i = 0; i < n; i++) {
				r.getItem(i).setBroadcast(false);
			}

			for (int i = 0; i < n; i++) {
				Toggle t = r.getItem(i);
				//t.setState(true);
				try{
					//r.getItem(i).setValue(0);
					f.setBoolean(t, false);
					if(f2!=null){
						f2.setFloat(t, 0);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			for (int i = 0; i < n; i++) {
				Toggle t = r.getItem(i);
				//t.setState(true);
				try{
					if(r.getItem(i).internalValue()==value && !r.getItem(i).getState()){
						f.setBoolean(t, true);
						if(f2!=null){
							f2.setFloat(t, 1);
						}
					}else{ //if( r.getItem(i).getValue()!=0){
						//r.getItem(i).setValue(0);
						//f.setBoolean(t, false);
						//f2.setFloat(t, 0);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			//r.update();
			for (int i = 0; i < n; i++) {
				r.getItem(i).setBroadcast(true);
			}
			//}
		}catch(Exception e){
			System.out.println(r+" "+value);
			e.printStackTrace();
		}
	}



	protected  void updateController(String layerType,String name, String value){
		updateController(getController(layerType,name),value);
	}
	protected  void updateController(controlP5.Controller<?> c, String value){
		if(c instanceof controlP5.Textfield){
			Textfield textField = ((controlP5.Textfield)c);
			if(!textField.isFocus() && !textField.getText().equals(value)){
				textField.setBroadcast(false);
				textField.setText(value);
				textField.setBroadcast(true);
			}
		}else if(c instanceof controlP5.Textlabel){
			String val = ((controlP5.Textlabel)c).getValueLabel().getText();
			if(!val.equals(value)){
				c.setBroadcast(false);
				((controlP5.Textlabel)c).setValue(value);
				c.setBroadcast(true);
			}
		}else{
			if(c!=null){
				System.err.println("unspported controller type"+c.getClass());
			}
		}
	}


}
