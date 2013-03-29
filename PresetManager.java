import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PresetManager {

	private Application parent;

	public PresetManager(Application parent) {
		this.parent = parent;
		//load the default pattern
		if (Config.DEFAULT_PATTERN != null) {
			load(new File(Config.DEFAULT_PATTERN));
		}
	}

	public void asyncFileOperation(String what){
		AsyncFileOperationThread asyncFileOperationThread =new AsyncFileOperationThread(what,this);
		asyncFileOperationThread.start();
	}
	
	public class AsyncFileOperationThread extends Thread {
		private String what;
		PresetManager presetManager;
	    public AsyncFileOperationThread(String what,PresetManager presetManager) {
	    	super();
	    	this.what=what;
	    	this.presetManager=presetManager;
	    }
		public void run() {
			if("save".equalsIgnoreCase(what)){
				parent.selectOutput("Save Current Layer","save",null,presetManager);
			}else if("saveAll".equalsIgnoreCase(what)){
				parent.selectOutput("Save ALL Layers", "saveAll", null, presetManager);
			}
		}
	}
	


	
	public void load(File file) {
		load(file, parent.currentLayer);
	}

	public void load(File file, int layer) {
		System.out.print("loading layers from '" + file.getName() + "' : ");
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;
			List<String> content = new ArrayList<String>();
			while ((str = in.readLine()) != null) {
				content.add(str);
			}
			in.close();
			 parse(content);

			System.out.println("ok");
		} catch (java.io.FileNotFoundException ee) {
			System.out.println("file not found.");
		} catch (Exception eee) {
			System.out.println("error while loading file.");
			eee.printStackTrace();
		}
	}
	
	public void parse(List<String> data){
		boolean activated = false;
		String layerText=null;
		int layerIndex=-1;
		boolean first=true;
		for(String line : data){
			
			if(line.equals("end:")){
				Layer layer = Layer.loadLayer(parent, layerText);
				if(layerIndex==-1){
					parent.layers[parent.currentLayer]=layer;
				}else{
					if(first){
						for(int i=0;i<parent.layers.length;i++){
							parent.layers[i]=null;
							parent.layerEnabled[i]=false;
							first=false;
						}
					}
					parent.layers[layerIndex]=layer;
					parent.layerEnabled[layerIndex]=activated;
				}
				//reset
				layerIndex=-1;
				activated = false;
				layerText=null;
			}else if(line.startsWith("layer:")){
				String[] splits = line.split("\\:");
				layerIndex=Integer.parseInt(splits[1]);
				activated=splits[2].equalsIgnoreCase("true");
			}else{
				//support old stupid naming :)
				line = line.replaceAll("^sizee", "size");
				line = line.replaceAll("^inverted", "bar1");
				//accumulate layer data
				layerText=(layerText==null?"":layerText)+line+"\n";
			}
		}
		if(layerText!=null){
				parent.layers[parent.currentLayer]=Layer.loadLayer(parent, layerText);
		}
	}
	
	public void save(File file) {
		this.save(file,parent.layers[parent.currentLayer]);
	}
	
	void save(File file, Layer layer) {
		if(file!=null){
			try {
				if (!file.getCanonicalPath().endsWith(".txt")){
					file = new File(file+".txt");
				}
				System.out.print("saving " + layer.getType()+"("+layer.getName()+") into '"
						+ file.getName() + "' : ");
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				out.write(layer.toString());
				out.close();
				System.out.println("ok");
			} catch (Exception e) {
				System.out.println("error while saving to '" + file.getName() + "'");
			}
		}
	}
	
	public void saveAll(File file) {
		if(file!=null){
			try {
				if (!file.getCanonicalPath().endsWith(".all.txt")){
					file = new File(file+".all.txt");
				}
				System.out.print("saving all layers into '"+ file.getName() + "' : ");
				BufferedWriter out = new BufferedWriter(new FileWriter(file));
				for(int i=0;i<parent.layers.length;i++){
					Layer layer = parent.layers[i];
					if(layer!=null){
						out.write("layer:"+i+":"+parent.layerEnabled[i]+"\n");
						out.write(layer.toString());
						out.write("end:\n");
					}
				}
				out.close();
				System.out.println("ok");
			} catch (Exception e) {
				System.out.println("error while saving to '" + file.getName() + "'");
			}
		}
	}

	

}

