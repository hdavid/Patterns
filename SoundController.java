
import java.util.ArrayList;
import java.util.List;


import processing.core.PApplet;

import ddf.minim.AudioInput;
import ddf.minim.AudioListener;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;


public class SoundController implements Controller {

	private Application app;

	private int[] ranges = new int[]{
			Config.SAMPLERATE / Config.BUFFERSIZE / 4 ,
			Config.SAMPLERATE * 2 / Config.BUFFERSIZE ,
			Config.SAMPLERATE * 20 / Config.BUFFERSIZE ,
	};

	private float repeatDelayBeats = 0.9f;// skip beat for a half of beat
	private int repeatDelay = (int) ((60f / Config.BPM *repeatDelayBeats) * (Config.SAMPLERATE / Config.BUFFERSIZE));
	private float peakDecayRate = 0.991f; // decay slower
	private float linearEQIntercept = 0.8f; // reduced gain at lowest frequency
	private float linearEQSlope = 0.2f; // increasing gain at higher frequencies

	private int currentGroup = 0;
	
	private List<SoundControllerGroup> groups = new ArrayList<SoundControllerGroup>();

	//FFT spectrum
	private FFT fft;
	private AudioInput input;
	private Minim minim;
	
	int index = 0;
	
	private float[][] energyBandHistory;
	private float[][] bandAvgs;
	private float[] zoneEnergy;
	private float[] zoneEnergyPeak;

	public SoundController(Application app) {
		System.out.print("Sound Controller : ");
		this.app = app;
		int numZones = 0;

		if (Config.DSP) {
			minim = new Minim(app);
			input = minim.getLineIn(Minim.STEREO, Config.BUFFERSIZE,Config.SAMPLERATE);
			input.addListener(new MyListener());

			fft = new FFT(Config.BUFFERSIZE, Config.SAMPLERATE);

			fft.logAverages(Config.FFT_BASE_FREQ, Config.FFT_BAND_PER_OCT);
			// fft.noAverages();
			// fft.linAverages(128);
			fft.window(FFT.HAMMING);
			System.out.println("FFT on " + fft.avgSize()
					+ " log bands. samplerate: " + Config.SAMPLERATE + "Hz. "
					+ Config.SAMPLERATE / Config.BUFFERSIZE + " buffers of "
					+ Config.BUFFERSIZE + " samples per second. "
					+ "BPM:"+Config.BPM+"  repeat delay: "+(60f / Config.BPM *repeatDelayBeats)+" ("+repeatDelay+" frames)" );
			numZones = fft.avgSize();
		} else {
			numZones = 1;
		}
		
		zoneEnergyPeak = new float[numZones];
		energyBandHistory = new float[numZones][];
		bandAvgs = new float[ranges.length][];
		
		for (int i = 0; i < numZones; i++) {
			energyBandHistory[i] = new float[ranges[ranges.length-1]];
		}
		for(int i=0;i<ranges.length;i++){
			bandAvgs[i]=new float[energyBandHistory.length];
		}
		zoneEnergy = new float[numZones];
		
		
		for(int i=0;i<6;i++){
			groups.add(new SoundControllerGroup(i+"",numZones, ranges, repeatDelay, this.app));
			SoundControllerGroup group = groups.get(i);
			
			if(i==0){
				for(int j=0;j<group.getActiveBands().length;j++){
					group.getActiveBands()[j]=true;
				}
			}else if(i==1){
				for(int j=0;j<group.getActiveBands().length/5*(i+1);j++){
					group.getActiveBands()[j]=true;
				}
			}else if(i==5){
				for(int j=group.getActiveBands().length/5*(i-1);j<group.getActiveBands().length/5*i+1;j++){
					group.getActiveBands()[j]=true;
				}
			}else{
				for(int j=group.getActiveBands().length/5*(i-1);j<group.getActiveBands().length/5*(i+1)+1;j++){
					group.getActiveBands()[j]=true;
				}
			}
		}

	}

	private void process(float[] sample){

		//process FFT
		fft.forward(sample);

		//increment roundrobin index
		index = (index+1)%ranges[ranges.length-1];
		
		//improve spectrum and historise data !
		for(int i=0;zoneEnergy!=null && i<zoneEnergy.length;i++){
			
			//get FFT avg
			zoneEnergy[i]= fft.getAvg(i) * (linearEQIntercept + i * linearEQSlope);
			
			//fill history
			energyBandHistory[i][index]=zoneEnergy[i];
			
			//compute peak
			if(zoneEnergyPeak[i]>zoneEnergy[i]){
				zoneEnergyPeak[i]*=peakDecayRate;
			}else{
				zoneEnergyPeak[i]=zoneEnergy[i];
			}
			
			//compute per band avgs
			for(int j=0;j<ranges.length;j++){
				bandAvgs[j][i]=Utils.averageV(energyBandHistory[i], index, ranges[j]);
			}
			
		}

		//process groups
		for(SoundControllerGroup group : groups){
			group.process(energyBandHistory,bandAvgs,index);
		}

		//update gui
		if (app.guiController != null) {
			for(int i=0;i<groups.size();i++){
				SoundControllerGroup group = groups.get(i);
				//onbeat
				if(group.isOnBeat()){
					app.guiController.onBeat.put(i,true);
				}
				//Score
				float previousScore=1;
				if(app.guiController.groupScores.get(i)!=null){
					previousScore=app.guiController.groupScores.get(i);
				}
				if(previousScore>group.getScore()){
					app.guiController.groupScores.put(i, previousScore*0.9f);
				}else{
					app.guiController.groupScores.put(i, group.getScore());
				}
				//beatSense
				app.guiController.groupBeatSenses.put(i, group.getBeatSense());
			}
		}
	}
	
	class MyListener implements AudioListener {

		public void samples(float[] sample) {
			if(sample !=null && fft!=null){
				process(sample);
			}
		}

		public void samples(float[] sampleL, float[] sampleR) {
			if (sampleL != null && sampleR != null && fft!=null) {
				//float[] sample = MathUtils.sum(sampleL, sampleR);
				process(sampleL);
			}
		}

	}
	
	public float[] getZoneEnergy(){
		return(this.zoneEnergy);
	}

	public void setOnBeat(int group){
		groups.get(group).setOnBeat();
	}
	
	public void update(){	
	}
	
	
	public void draw(PApplet applet){
		
		boolean scaleEq = true;
			
			int floor = applet.height -40;
			
			int width=580;
			int numZones = zoneEnabled().length;
			int bandWidth = (width-numZones+1)/zoneEnabled().length;

				for (int i = 0; i < numZones; i++) {

					applet.fill(50+1*Utils.ScaleIEC(10f * (float) 
							Math.log(zoneEnergy[i]  / 250f)),50,50);
					applet.noStroke();
					applet.rect(i + 10 + (i * bandWidth + 1), floor, bandWidth,
							-1*((scaleEq ? Utils.ScaleIEC(10f * (float) 
									Math.log(zoneEnergy[i]  / 250f)
									)
									: zoneEnergy[i])));

					// average
					for(int j=0;j<bandAvgs.length;j++){
						applet.fill(120+80*j, 120+80*(2-j), 0);
						applet.rect(i + 10 + (i * bandWidth + 1), floor
							- (scaleEq ? Utils.ScaleIEC(10 * (float) 
									Math.log(bandAvgs[j][i] / 250))
									: bandAvgs[j][i]), bandWidth, 1);
					}
					// peaks
					applet.fill(0, 0, 255);
					applet.rect(i + 10 + (i * bandWidth + 1), floor
							- (scaleEq ? Utils.ScaleIEC(10 * (float) 
									Math.log(zoneEnergyPeak[i] / 250))
									: zoneEnergyPeak[i]), bandWidth, 1);
					

			//	int[] octave= new int[] {0, 2 ,4, 6 ,8,12, 14 ,16, 18 ,20, 22 ,24};

				//sum per octave
				/*for (int i = 0; i < numZones&i<Config.FFT_BAND_PER_OCT; i++) {

					int r = 50;
					int g = 50;
					int b = 50;

					float sum = 0;
					float sumScore = 0;
					int count=0;
					for (int j=0 ;i+j*Config.FFT_BAND_PER_OCT < numZones; j++) {
						if(!currentGroup.matches("[0-9]+") || zoneEnabled.get(currentGroup)!=null&&zoneEnabled.get(currentGroup)[i+j*Config.FFT_BAND_PER_OCT]){
							sum+=zoneEnergy[i+j*Config.FFT_BAND_PER_OCT][playheadMiddleTerm];
							//zoneEnergyVuMeter[j*12]
							sumScore+=zoneScore[i+j*Config.FFT_BAND_PER_OCT][playheadMiddleTerm];
							count++;
						}
					}
					if(count>0){
						sum = sum /count;
						sumScore = sumScore /count;
					}else{
						sum=0;
						sumScore=0;
					}

					r = (int) sumScore* 25 + 50;
					if (i % Config.FFT_BAND_PER_OCT == lastBand % Config.FFT_BAND_PER_OCT) {
						g = 255;
					}
					applet.fill(r, g, b);
					applet.noStroke();
					applet.rect(
							octave[i]*(bandWidth/2+1)+110+(numZones + 10 + (numZones * bandWidth + 1)),
							//bandWidth*2+i + i+i+80+ (i * bandWidth + 1)+ (numZones + 10 + (numZones * bandWidth + 1)),
							10+((i==1||i==3||i==6||i==8||i==10)?-5:0),
							bandWidth,
							ratio
							* ((scaleEq ? ScaleIEC(10f * (float) Math
									.log(sum / 250f))
									:sum )));
				*/
			
			}
	

	}

	
	public void control() {

		if (app.layers != null) {
			for (int i = 0; i < app.layers.length; i++) {
				Layer layer = app.layers[i];
				if (layer != null) {
					layer.soundControl(this);
				}
			}
		}
		
		
		for(int i=0;i<groups.size();i++){
			SoundControllerGroup group = groups.get(i);
			if (group.isOnBeat()&&app.guiController!=null) {
				app.guiController.onBeat.put(i,true);
			}
			if (group.isOnBeat()&&app.midiLaunchpadController!=null) {
				app.midiLaunchpadController.onBeat.put(i,true);
			}
			group.setOnBeat(false);
		}

	}
	
	public boolean[] zoneEnabled(){
		return groups.get(currentGroup).getActiveBands();
	}
	
	public List<SoundControllerGroup> getGroups(){
		return(groups);
	}
	
	public void setCurrentGroup(int group){
		currentGroup=group;
	}
	
	public void setAutoBeatSense(boolean sense){
		for(SoundControllerGroup group : groups){
			group.setAutoBeatSense(sense);
		}
	}
	
	public void setBeatSenseSense(float value){
		for(SoundControllerGroup group : this.groups){
			group.setBeatSenseSense(value);
		}
	}
	public void setBeatSenseSense(int group,float value){
		groups.get(group).setBeatSenseSense(value);
	}
	
	public void stop() {
		if (Config.DSP) {
			input.close();
			minim.stop();
		}
	}




}

