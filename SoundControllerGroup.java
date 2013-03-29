
public class SoundControllerGroup {

	public SoundControllerGroup(String name,int bands,int[] ranges, int repeatDelay){
		this.name = name;
		this.historySize=ranges[ranges.length-1];
		this.activeBands = new boolean[bands];
		this.perBandScores = new float[bands];
		this.energyHistory = new float[historySize];
		this.scoreHistory = new float[historySize];
		this.modulationHistory = new float[historySize];
		this.beatSense=8;
		this.ranges=ranges;
		this.repeatDelay=repeatDelay;
	}

	//settings
	private float energyThreshold= 0.3f;
	private float modulationReleaseSmooth=0.9f;
	private float modulationAttackSmooth=0.8f;
	private boolean autoBeatSense = true;
	public float beatSenseSense=1.2f;
	private int skipFrames=0;
	private int repeatDelay;


	//group data
	private String name;
	private boolean[] activeBands;
	private float[] energyHistory;
	private float[] scoreHistory;
	private float[] perBandScores;
	private float[] modulationHistory;
	private float smoothEnergy;
	private float beatSense;
	private int roundRobinIndex=0;
	private int historySize;
	private int[] ranges;
	private boolean onBeat;

	public void process(float[][] energyBandHistory,float[][] bandAvgs, int index){
		energyThreshold=0.5f;
		//sum energy
		energyHistory[index]=0;
		float score=0;
		int cpt=0;

		for(int i=0;i<energyBandHistory.length;i++){
			float bandScore=0;
			for(int j=0;j<ranges.length;j++){
				if(energyBandHistory[i][index]>energyThreshold * 10 && bandAvgs[j][i]<energyBandHistory[i][index]){
					bandScore += Math.sqrt((energyBandHistory[i][index]-bandAvgs[j][i])/bandAvgs[j][i]*(j+1));
				}
			}
			perBandScores[i]=bandScore;
			if(activeBands[i]){
				cpt++;
				score+=bandScore;
				energyHistory[index]+=energyBandHistory[i][index];
			}
		}
		if(cpt>0){
			energyHistory[index]/=(float)cpt;
			score /= cpt;
			//score++;
		}


		//smooth energy
		float smooth = 0.8f;
		if(smoothEnergy<smooth*energyHistory[index]){
			smoothEnergy*=smooth;
		}else{
			smoothEnergy=energyHistory[index];
		}
		energyThreshold=0.1f;

		//compute score
		if (energyHistory[index] > energyThreshold){
			scoreHistory[index] = score;
			//energyHistory[index]/energyPeakAvg;
			//energyHistory[index]/(avgs[0])
			//*avgs[0]/avgs[1]
			;
		}else{
			scoreHistory[index]=0;
		}

		//compute modulation
		//modulationReleaseSmooth=0.9f;
		//modulationAttackSmooth=0.8f;
		float newMod = scoreHistory[index] * scoreHistory[index] / beatSense / beatSense ;
		float oldMod = modulationHistory[roundRobinIndex];
		if (newMod*(1f-modulationAttackSmooth)+oldMod*modulationReleaseSmooth>oldMod){
			//	modulation.put(group, newMod*(1f-modulationAttackSmooth)+oldMod*modulationReleaseSmooth);
			newMod = newMod*(1f-modulationAttackSmooth)+oldMod*modulationReleaseSmooth;
		}else{
			//	modulation.put(group,modulationReleaseSmooth * oldMod);
			newMod =modulationReleaseSmooth * oldMod;
		}
		if (newMod > 2f){
			newMod= 2f;
		}
		modulationHistory[index]=newMod;

		//onBeat
		if (skipFrames <= 0 && scoreHistory[index] > beatSense) {
			onBeat = true;
			skipFrames = repeatDelay;
		}
		if (skipFrames > 0){
			skipFrames--;
		}

		float avgsScore[] = new float[ranges.length]; 
		//float maxsScore[] = new float[ranges.length]; 
		//float minsScore[] = new float[ranges.length]; 
		for(int i=0;i<ranges.length;i++){
			avgsScore[i]=Utils.average(scoreHistory, index, ranges[i]);
			//maxsScore[i]=MathUtils.max(scoreHistory, index, ranges[i]);
			//minsScore[i]=MathUtils.min(scoreHistory, index, ranges[i]);
		}
		//compute beatSense
		if (autoBeatSense && energyHistory[index]>energyThreshold) {
			// beatSense = beatSense*0.99 +0.01*max*0.6;
			beatSense = beatSense * 0.99f  
					+0.01f*(  avgsScore[ranges.length-1])*2 * beatSenseSense;
			// if(variance(score)>1)
			// beatSense=0.99*beatSense + beatSenseSense*(0.01*variance(score));
			if (beatSense > 19)
				beatSense = 19f;
			if (beatSense < 1.1)
				beatSense = 1.1f;
		}

		//make new index public
		roundRobinIndex = index;
	}

	public float getModulation(){
		return(modulationHistory[roundRobinIndex]);
	}

	public boolean[] getActiveBands(){
		return(activeBands);
	}

	public float getEnergy(){
		return(energyHistory[roundRobinIndex]);
	}

	public float getScore(){
		return(scoreHistory[roundRobinIndex]);
	}

	public float[] getPerBandScores(){
		return(perBandScores);
	}

	public float getBeatSense(){
		return(beatSense);
	}

	public void setOnBeat(){
		onBeat=true;
	}
	public void setOnBeat(boolean onBeat){
		this.onBeat=onBeat;
	}
	public void setAutoBeatSense(boolean autoBeatSense){
		this.autoBeatSense=autoBeatSense;
	}
	public void setBeatSense(float beatSense){
		this.beatSense=beatSense;
	}
	public void setBeatSenseSense(float beatSenseSense){
		this.beatSenseSense=beatSenseSense;
	}
	public String getName(){
		return(this.name);
	}
	public boolean isOnBeat(){
		return(onBeat);
	}

}