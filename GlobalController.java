
public class GlobalController{

	private Application app;
	private float ratio=1.1f;
	
	public GlobalController(Application app){
		this.app=app;
	}

	void incSpeed(){
		
		for(Layer layer : app.layers){
			if(layer!=null){
				for(Parameter parameter : layer.getParameters()){
					if(parameter instanceof FloatParameter){
						((FloatParameter)parameter).tcIncPerSec*=ratio;
						((FloatParameter)parameter).bumpAttack/=ratio;
						((FloatParameter)parameter).bumpFade/=ratio;
						((FloatParameter)parameter).scAlterAccumulatorInc*=ratio;
						((FloatParameter)parameter).scAccumulatorInc*=ratio;
					}else if(parameter instanceof ListParameter){
						((ListParameter)parameter).scAccumulatorInc/=ratio;
						
					}
				}
			}
		}
		
	}
	
	void decSpeed(){
		for(Layer layer : app.layers){
			if(layer!=null){
				for(Parameter parameter : layer.getParameters()){
					if(parameter instanceof FloatParameter){
						((FloatParameter)parameter).tcIncPerSec/=ratio;
						((FloatParameter)parameter).bumpAttack*=ratio;
						((FloatParameter)parameter).bumpFade*=ratio;
						((FloatParameter)parameter).scAlterAccumulatorInc/=ratio;
						((FloatParameter)parameter).scAccumulatorInc/=ratio;
					}else if(parameter instanceof ListParameter){
						((ListParameter)parameter).scAccumulatorInc*=ratio;
					}
				}
			}
		}
	}
	
	void decBump(){
		for(Layer layer : app.layers){
			if(layer!=null){
				for(Parameter parameter : layer.getParameters()){
					if(parameter instanceof FloatParameter){
						((FloatParameter)parameter).bumpChange/=ratio;
						((FloatParameter)parameter).modulatePct/=ratio;
						((FloatParameter)parameter).scChange/=ratio;
					}else if(parameter instanceof ListParameter){

					}
				}
			}
		}
	}
	
	void incBump(){
		for(Layer layer : app.layers){
			if(layer!=null){
				for(Parameter parameter : layer.getParameters()){
					if(parameter instanceof FloatParameter){
						((FloatParameter)parameter).bumpChange*=ratio;
						((FloatParameter)parameter).modulatePct*=ratio;
						((FloatParameter)parameter).scChange*=ratio;
					}else if(parameter instanceof ListParameter){
			
					}
				}
			}
		}
	}
}


