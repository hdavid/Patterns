import java.io.BufferedWriter;


public class FloatParameter implements Parameter {
	
	public static float modulateRatio=0.5f;

	public static final int LIMIT=0;
	public static final int WRAP=1;
	public static final int FREE=2;
	
	public float v;
	public float max, min;
	public boolean sc = false;
	public float scChange = 0.5f;
	public float scAccumulator= 0f;	
	public float scAccumulatorInc= 1.0f;
	public boolean tc = false;
	public float tcIncPerSec = 500;
	private long ms = System.currentTimeMillis();
	public boolean bump = false;
	public float bumpAccumulator = 0f;
	public float bumpAccumulatorInc = 1.0f;
	public long bumpTimeAttack = 0;
	public long bumpTimeFade = 0;
	public float bumpChange = .5f;
	public float bumpAttack = 50.0f;
	public float bumpFade = 200;
	public float bumpValue = 0;
	public float incValue = 0;
	public int mode = FloatParameter.LIMIT;
	public boolean scAlter = false;
	public float scAlterAccumulator = 0f;
	public float scAlterAccumulatorInc = 0.25f;
	public boolean modulate = false;
	public float modulateValue = 0;
	public float modulatePct = 0.15f;
	public int incMode = 1;
	private final String name;
	public final Layer parent;
	public int scGroup = 0;
	public boolean ghost = true;
	public boolean ghostFull = true;
	public int ghostValue = 1;
	
	
	
	public FloatParameter(Layer parent,float v, float min, float max, String name) {
		this(parent,v,min,max,FloatParameter.LIMIT,name);
	}
	public FloatParameter(Layer parent,float v, float min, float max, int mode, String name) {
		this.v = v;
		this.min = min;
		this.max = max;
		this.mode = mode;
		this.name = name;
		this.parent = parent;
	}
	public FloatParameter clone(Layer parent) {
		FloatParameter param = new FloatParameter(parent,this.v, this.min, this.max,this.mode, this.name);
		param.sc = this.sc;
		param.scChange = this.scChange;
		param.scAccumulatorInc = this.scAccumulatorInc;
		param.tc = this.tc;
		param.tcIncPerSec = this.tcIncPerSec;
		param.bump = this.bump;
		param.bumpAccumulatorInc = this.bumpAccumulatorInc;
		param.bumpTimeAttack = this.bumpTimeAttack;
		param.bumpTimeFade = this.bumpTimeFade;
		param.bumpChange = this.bumpChange;
		param.bumpAttack = this.bumpAttack;
		param.bumpFade = this.bumpFade;
		param.bumpValue = this.bumpValue;
		param.scAlter = this.scAlter;
		param.scAlterAccumulatorInc = this.scAlterAccumulatorInc;
		param.modulate = this.modulate;
		param.modulateValue = this.modulateValue;
		param.modulatePct = this.modulatePct;
		param.scGroup=this.scGroup;
		param.ghost=this.ghost;
		param.ghostFull=this.ghostFull;
		param.ghostValue=this.ghostValue;
		param.ms = System.currentTimeMillis();
		return (param);
	}
	
	public String getName() {
		return (name);
	}

	public void setV(float v){
		this.v=v;
	}
	public float vReal() {
		return (v);
	}
	public float v() {
		return(v(true));
	}
	public float v(boolean useGhost) {
		Layer ghost=parent.getGhost();
		if(useGhost &&ghost!=null && this.ghost){
			return (adjustValue((this.ghostFull?0:1)*this.ghostValue*(v + bumpValue + modulateValue + ghostValueFix()) + ghost.getFloatParameter(this.name).v(false)));	
		}else{
			return (adjustValue(v + bumpValue + modulateValue));
		}
	}
	
	private float ghostValueFix(){
		if(this.name.equals("zoom")){
			return(-4);
		}else if(this.name.equals("size")){
			return(-1);
		}else{
			return(0);
		}
	}
	
	public float v(FloatParameter inc,int x,int range){
		if(inc.incMode==0){
			incValue= inc.v()*(float)x/(float)range;
		}else{
			incValue= inc.v()*(float)(x+range)/(float)range;
		}
		Layer ghost=parent.getGhost();
		if(ghost!=null && this.ghost){
			return(adjustValue((this.ghostFull?0:1)*this.ghostValue*(v + bumpValue + modulateValue+ incValue + ghostValueFix()) 
					+ ghost.getFloatParameter(this.name).v(ghost.getFloatParameter("i"+this.name),x,range)));
		}else{
			return(adjustValue(v + bumpValue + modulateValue + incValue));	
		}
	}
	
	private float adjustValue(float value){
		if(!this.name.startsWith("scroll")){
		if(this.mode==FloatParameter.WRAP){
			float range= Math.abs(this.max-this.min);
			if(value>this.max && range!=0){
				value=value%range;
			}
			if(value<this.min && range!=0){
				value=value%range;
			}
		}else if(this.mode==FloatParameter.LIMIT){
			if(value>this.max){
				value=this.max;
			}
			if(value<this.min){
				value=this.min;
			}
		}
		}
		return(value);
	}



	public void load(String data) {
		String values[] = data.split(";");
		v = Float.parseFloat(values[0]);
		sc = values[1].equals("true");
		scChange = Float.parseFloat(values[2]);
		tc = values[3].equals("true");
		tcIncPerSec = Float.parseFloat(values[4]);
		bump = values[5].equals("true");
		bumpChange = Float.parseFloat(values[6]);
		bumpAttack = Float.parseFloat(values[7]);
		bumpFade = Float.parseFloat(values[8]);
		bumpValue = Float.parseFloat(values[9]);
		int i=10;
		if (values.length > i)
			scAlter = values[i].equals("true");
		i++;
		if (values.length > i)
			modulate = values[i].equals("true");
		i++;
		if (values.length > i)
			modulatePct = Float.parseFloat(values[i]);
		i++;
		if (values.length > i)
			min = Float.parseFloat(values[i]);
		i++;
		if (values.length > i)
			max = Float.parseFloat(values[i]);
		i++;
		if (values.length >i)
			scAlterAccumulatorInc = Float.parseFloat(values[i]);
		i++;
		if (values.length > i)
			scAccumulatorInc = Float.parseFloat(values[i]);
		i++;
		if (values.length > i)
			bumpAccumulatorInc = Float.parseFloat(values[i]);
		i++;
		if (values.length > i)
			scGroup = Integer.parseInt(values[i]);
		i++;
		if (values.length > i)
			ghost = values[i].equals("true");
		i++;
		if (values.length > i)
			ghostValue = Integer.parseInt(values[i]);
		i++;
		if (values.length > i)
			ghostFull = values[i].equals("true");		
		ms = System.currentTimeMillis();
		bumpTimeAttack = 0;
		bumpTimeFade = 0;
	}

	public void save(BufferedWriter out) {
		try {
			out.write(this.toString());
		} catch (Exception e) {
		}
	}

	public String toString() {
		return name + ":" + v + ";" + (sc ? "true" : "false") + ";" + scChange
		+ ";" + (tc ? "true" : "false") + ";" + tcIncPerSec + ";"
		+ (bump ? "true" : "false") + ";" + bumpChange + ";"
		+ bumpAttack + ";" + bumpFade + ";" + bumpValue + ";" + scAlter
		+ ";" + (modulate ? "true" : "false") + ";" + modulatePct
		+ ";" + min + ";" + max+";"+scAlterAccumulatorInc+";"+scAccumulatorInc+";"+bumpAccumulatorInc
		+ ";" +  scGroup + ";" + (ghost? "true" : "false")+";"+ghostValue+";"+(ghostFull? "true" : "false")
		+ "\n";
	}

	public void randomize() {
		this.v = random(this.min, this.max);
	}

	public void change(float change) {
		float v = this.v;
		v += random(-1 * (this.max - this.min) * change / 10,
				(this.max - this.min) * change / 10);
		if (this.mode==FloatParameter.WRAP) {
			if (v > this.max)
				v = v - (this.max - this.min);
			if (v < this.min)
				v = v + (this.max - this.min);
		} else if (this.mode==FloatParameter.LIMIT) {
			if (v > this.max)
				v = this.max;
			if (v < this.min)
				v = this.min;
		}
		this.v = v;
	}

	// change direction of movement on beat
	public void doScAlter(boolean onBeat) {
		if(this.scAlterAccumulator<0)this.scAlterAccumulator=0;
		if (onBeat && this.scAlter) {
			if(this.scAlterAccumulatorInc>0)
				this.scAlterAccumulator +=this.scAlterAccumulatorInc;
			else
				this.scAlterAccumulator =1;
			if(this.scAlterAccumulator>=1.0f){
				this.scAlterAccumulator=0;
				if(this.tc){
					float rand = (float) Math.random() * 3f;
					if (rand < 2.5f) {
						this.tcIncPerSec = -this.tcIncPerSec;
					} else if (rand < 3.0f) {
						this.tc = false;
					}
				}else{
					this.tc = true;
				}
			}
		}
	}

	// bump on beat
	public void doBump(boolean onBeat) {
		float bumpValue = this.bumpValue;
		long now = System.currentTimeMillis();
		float attack = 0;
		float fade = 0;

		if (bump) {
			if(this.bumpAccumulator<0){
				this.bumpAccumulator=0;
			}
			if(this.bumpAccumulatorInc>0){
				this.bumpAccumulator +=this.bumpAccumulatorInc;
			}else{
				this.bumpAccumulator =1;
			}
			if(this.bumpAccumulator>=1.0f){
				this.bumpAccumulator=0;
				
				if (bumpFade < 0)
					bumpFade = 0;
				if (bumpAttack < 0)
					bumpAttack = 0;
	
				if (onBeat) {
					// if(now>bumpTimeAttack+bumpAttack/2){
					bumpTimeAttack = now;
					// }
				}
	
				if (bumpAttack > 0) {
					if ((now - bumpTimeAttack) < (long)bumpAttack) {
						// in attack
						attack = bumpChange * (float)(now - bumpTimeAttack) / bumpAttack;
					} else {
						// now in release
						if (bumpTimeAttack != 0){
							bumpTimeFade = bumpTimeAttack + (long)bumpAttack;
						}
					}
				} else if (bumpAttack == 0) {
					// now in release
					if (bumpTimeAttack != 0)
						bumpTimeFade = bumpTimeAttack + (long)bumpAttack;
				}
	
				if ((now - bumpTimeFade) < (long)bumpFade) {
					fade = bumpChange
					* (0f - (float)(now - bumpTimeFade - bumpFade) / bumpFade);
				}
				
				if(fade!=0||attack!=0){
					if (Math.abs(fade) > Math.abs(attack)) {
						bumpValue = fade;
					} else {
						bumpValue = attack;
					}
				}else{
					bumpValue=0;
				}
				
				//post process the value for specific parameters.
				if (name != null && name.startsWith("scroll") && parent instanceof LayerMovement) {
					if(bumpValue!=0.0f)
					bumpValue = bumpValue * ((LayerMovement)parent).width;
				}else if (  name != null && name.equals("zoom")){
						bumpValue = bumpValue * v();
				}else if(name.equals("number")){
						bumpValue *= 100 ;
				} else if (name != null && (name.equals("angle")||name.equals("rotate"))) {
					//bumpValue = bumpValue * 2 * (float) Math.PI / 100;
				        bumpValue = bumpValue * (float) Math.PI;
				}
				
			}
			
		} else {
			bumpValue = 0;
		}

		this.bumpValue = bumpValue;
	}

	public void doSc(boolean onBeat) {
		if (this.sc && onBeat) {

			if(this.scAccumulator<0)this.scAccumulator=0;
			if(this.scAccumulatorInc>0)
				this.scAccumulator +=this.scAccumulatorInc;
			else
				this.scAccumulator =1;
			if(this.scAccumulator>=1.0f){
				this.scAccumulator=0;
				
				if (name != null && name.startsWith("scrollX") && parent instanceof LayerMovement) {
					LayerMovement parent2 = ((LayerMovement)parent);
					v += scChange * parent2.width;
					if(parent2.scrollMode.v()>0){
						//if(parent2.mask.v()==1){
						//	v = v % (parent2.totalWidth() * ( parent2.scrollMode.v()));
						//}else{
							v = v % (parent2.width * /*(parent2.drawText ? 4 : */parent2.scrollMode.v());//);
						//}
					}
	
				} else if (name != null && name.startsWith("scrollY") && parent instanceof LayerMovement) {
					LayerMovement parent2 = ((LayerMovement)parent);
					v += scChange * parent2.height;
					if(parent2.scrollMode.v()>0){
						//if(parent2.mask.v()==1){
						//	v = v % (parent2.totalHeight() * ( parent2.scrollMode.v()));
						//}else{
							v = v % (parent2.height * /*(parent2.drawText ? 4 : */parent2.scrollMode.v());//);
						//}
					}
				} else if (name != null && (name.equals("angle")||name.equals("rotate"))) {
					v += scChange * 2 * (float) Math.PI / 100;
				} else if (name != null && (name.equals("zoom")||name.equals("number"))) {
					if (name.equals("zoom")){
						v += scChange * v / 10;
					}else if(name.equals("number")){
						v += scChange ;
					}
					if (v <= min) {
						scChange = -scChange;
					}
					if (v >= max) {
						scChange = -scChange;
					}
				} else {
					this.change(scChange);
				}
			}
		}
	}

	public void doModulation(float value) {

		if (modulate) {
			float modulateValue =((this.name.contains("size")||this.name.contains("scroll"))?modulateRatio:1f) *value * modulatePct * (this.max - this.min);

			// adjust bump value for specific parameters
			/*if (name == null 
					|| name.endsWith("H") || name.endsWith("S") ||  name.endsWith("B")
					|| name.endsWith("Alpha") 
					|| name.startsWith("gradient")||name.startsWith("size")){
				if (wrap) {
					if (modulateValue + v > max)
						modulateValue = modulateValue - max;
					if (modulateValue + v < min)
						modulateValue = modulateValue + max;
				} else {
					if (modulateValue + v > max)
						modulateValue = max - v;
					if (modulateValue + v < min)
						modulateValue = min - v;
				}
			}*/

			if (name != null && name.startsWith("scroll")&& parent instanceof LayerMovement) {
				modulateValue = modulateValue * ((LayerMovement)parent).width;
			} else if (name != null && (name.equals("zoom"))) {
					modulateValue = modulateValue * v() / 100f;
			} else if (name != null && (name.equals("angle")||name.equals("rotate"))) {
				modulateValue = modulateValue /* * 2 * (float) Math.PI*/ / 100f;
			}
			this.modulateValue = modulateValue;
		} else {
			this.modulateValue = 0;
		}

	}

	public void doTc() {

		float v = this.v;
		if (name != null && name.startsWith("scroll")) {
			if (tc)
				v += tcIncPerSec * tcIncPerSec * tcIncPerSec * (System.currentTimeMillis() - ms) / 1000f /1000f;
	         //v = v % (pattern.width);
			if(parent.getListParameter("scrollMode")!=null && parent.getListParameter("scrollMode").v()>0 && parent instanceof LayerMovement) {
	           v = v % (((LayerMovement)parent).width * /*(parent.drawText ? 4 : */parent.getListParameter("scrollMode").v());//);
			}
			ms = System.currentTimeMillis();
		} else if (name != null &&(name.equals("angle")||name.equals("rotate"))) {
			if (tc)
				v += tcIncPerSec * tcIncPerSec * tcIncPerSec  * (System.currentTimeMillis() - ms) / 1000;
			v = v % (2 * (float) Math.PI);
			ms = System.currentTimeMillis();
		} else if (name != null && name.equals("gradient")) {
			if (tc){
				v += tcIncPerSec * tcIncPerSec * tcIncPerSec  * (System.currentTimeMillis() - ms) / 1000.0;
				if (v > 0.98 && tcIncPerSec > 0)
					tcIncPerSec = -tcIncPerSec;
				if (v < 0.02 && tcIncPerSec < 0)
					tcIncPerSec = -tcIncPerSec;
			}
			ms = System.currentTimeMillis();
		} else if (name != null && (name.equals("zoom")||name.equals("number"))) {
			if (tc){
				if (name.equals("zoom")){
					v += tcIncPerSec * tcIncPerSec * tcIncPerSec  * v * (System.currentTimeMillis() - ms) / 1000;
				}else if(name.equals("number")){
					v += tcIncPerSec * tcIncPerSec * tcIncPerSec  * (System.currentTimeMillis() - ms) / 100;
				}
			}
			if (v <= min) {
				v = min;
				if (tc){
					tcIncPerSec = -tcIncPerSec;
				}
			}
			if (v >= max) {
				v = max;
				if (tc){
					tcIncPerSec = -tcIncPerSec;
				}
			}
			ms = System.currentTimeMillis();
		} else {
			// normal behaviour
			if (tc) {
				if (tcIncPerSec < System.currentTimeMillis() - ms) {
					v += random(-1 * (this.max - this.min) * scChange / 10.0,
							(this.max - this.min) * scChange / 10.0);
					ms = System.currentTimeMillis();
				}
			}
			v=adjustValue(v);
		}

		this.v = v;
	}

	private float random(float min, float max) {
		return (float) (Math.random() * (max - min) + min);
	}

	private float random(double min, double max) {
		return (float) (Math.random() * (max - min) + min);
	}
	
	public void setScGroup(int group){
		this.scGroup=group;
	}
	
	public int getScGroup(){
		return(scGroup);
	}

}

