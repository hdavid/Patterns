import java.io.BufferedWriter;


public interface Parameter {
	public void randomize();

	public void doBump(boolean onBeat);

	public void doSc(boolean onBeat);

	public void doScAlter(boolean onBeat);

	public void doTc();

	public void doModulation(float value);

	public void load(String data);

	public void save(BufferedWriter out);

	public String toString();

	public String getName();

	public Parameter clone(Layer parent);
	
	public int getScGroup();
	public void setScGroup(int group);

}

