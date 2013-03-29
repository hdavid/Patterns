import java.io.BufferedWriter;


public class ListParameter implements Parameter {

	public int v;
	public int[] values;
	public String[] names;
	public boolean sc = false;
	public float scAccumulator = 0.0f;
	public float scAccumulatorInc = 1.0f;
	public boolean tc = false;
	public int tcIncPerSec = 0;
	public long ms = 0;
	final private String name;
	public int scGroup=0;
	public boolean ghost=true;
	public final Layer parent;
	
	public static String[] onOffNames={"off","on"};
	public static int[] onOff={0,1};


	public ListParameter(Layer parent,int v, int[] values,String[] names, String name) {
		this.parent=parent;
		this.v = v;
		this.values = values;
		this.names=names;
		this.name = name;
	}
	
	public ListParameter clone(Layer parent) {
		ListParameter param = new ListParameter(parent,this.v, this.values,this.names, this.name);
		param.sc = this.sc;
		param.scAccumulatorInc = this.scAccumulatorInc;
		param.tc = this.tc;
		param.tcIncPerSec = this.tcIncPerSec;
		param.scGroup=this.scGroup;
		param.ms = this.ms;
		param.ghost=this.ghost;
		return (param);
	}
	public int v() {
		return(v(true));
	}
	
	public int getIndex() {
		for(int i=0;i<values.length;i++){
			if(v==values[i]){
				return(i);
			}
		}
		return(-1);
	}
	
	public void setIndex(int index) {
		if(index>=values.length){
			v=values[0];
		}else{
			v=values[index];
		}
	}
	
	public int v(boolean useGhost) {
		Layer ghost="ghostLayer".equals(this.name)?null:parent.getGhost();
		if(useGhost && ghost!=null && this.ghost){
			return(ghost.getListParameter(this.name).v(false));
		}else{
			return (v);
		}
	}
	public String getName() {
		return (name);
	}
	public void load(String data) {
		String values[] = data.split(";");
		v = Integer.parseInt(values[0]);
		sc = values[1].equals("true");
		tc = values[2].equals("true");
		tcIncPerSec = Integer.parseInt(values[3]);
		if(values.length>4){
			scAccumulatorInc=Float.parseFloat(values[4]);
		}
		if(values.length>5){
			scGroup=Integer.parseInt(values[5]);
		}
		if(values.length>6){
			ghost=values[2].equals("true");
		}
		ms = System.currentTimeMillis();
	}

	public void save(BufferedWriter out) {
		try {
			out.write(this.toString());
		} catch (Exception e) {
		}
	}

	public String toString() {
		return (name + ":" + v + ";" + (sc ? "true" : "false") + ";"
			+ (tc ? "true" : "false") + ";" + tcIncPerSec + ";"+scAccumulatorInc
			+ ";" + scGroup + ";" + ghost
			+"\n"
			);
	}

	public void randomize() {
		this.v = values[(int) (Math.random() * this.values.length)];
	}

	public void doSc(boolean onBeat) {
		if(this.scAccumulator<0)this.scAccumulator=0;
		if (this.sc && onBeat ) {
			if(this.scAccumulatorInc>0)
				this.scAccumulator +=this.scAccumulatorInc;
			else
				this.scAccumulator=1;
			if(this.scAccumulator>=1){
				this.scAccumulator=0;
				if(this.values.length==2){
					if(v==values[0])
						v=values[1];
					else
						v=values[0];
				}else{
					this.randomize();
				}
				
			}
		}
	}

	public void doBump(boolean onBeat) {
	}

	public void doScAlter(boolean onBeat) {
	}

	public void doModulation(float value) {
	}



	public void doTc() {
		if (tc && System.currentTimeMillis() > ms + tcIncPerSec) {
			ms = System.currentTimeMillis();
			randomize();
		}
	}
	
	public void setScGroup(int group){
		this.scGroup=group;
	}
	
	public int getScGroup(){
		return(scGroup);
	}



}

