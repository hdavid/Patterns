
public class KeyboardController{

	private Application parent;
	public boolean pressed[] = new boolean[1024];
	public long lastPressedTime [] = new long[1024];

	public Layer clipboard=null;

	//key
	boolean s=false;
	boolean c=false;
	boolean a=false;
	boolean apl=false;
	boolean win=false;

	public KeyboardController(Application app){
		this.parent = app;
		System.out.println("Keyboard Controller.");
	}

	public void keyPressed(int key, int keyCode) {


		if(parent.guiController==null || !parent.guiController.textFieldIsFocus()){
			pressed[key & 0x7f] = true;
			if(keyCode==16) s=true;
			if(keyCode==18) a=true;
			if(keyCode==17) c=true;
			if(keyCode==157) apl=true;
			if(keyCode==524) win=true;
			char cc=  (char) key;

			Layer layer = parent.layers[parent.currentLayer];

			if(layer!=null){
				layer.keyEvent(key, keyCode, cc, a, c, s,win,apl,lastPressedTime);
			}

			/* if(s)System.out.print("Shift ");
			 if(c)System.out.print("Ctrl ");
			 if(a)System.out.print("Alt ");
			 if(apl)System.out.print("Apple ");
			 System.out.println(keyCode);
			 */
			switch(keyCode){


			case 9://tab on mac
				if(parent.guiController!=null){
					parent.guiController.switchTab();
				}
				break;
			case 93://$ on mac

			case 36://'home':
				if(parent.guiController!=null){
					parent.guiController.toggleVisiblity();
				}
				break;

			case 10://'enter':

				break;

				//LAYERS
			case 49://'1':
			case 50://'2':
			case 51://'3':
			case 52://'4':
			case 53://'5':
			case 54://'6':
			case 55://'7':
			case 56://'8':
			case 57://'9':
			case 48://'0':	
				int l = keyCode-49;
				if(l==-1){
					l=9;
				}
				//if(!s&&!c&&a)app.layers[app.currentLayer]=app.presetManager.load(l+1,app.layers[app.currentLayer]);
				if(!s&&c&&!a)parent.layerEnabled[l]=!parent.layerEnabled[l];
				//if(s&&!c&&!a)app.layers[app.currentLayer]=app.presetManager.load(l+11,app.layers[app.currentLayer]);
				//if(s&&c&&!a)app.presetManager.save(l+11,app.layers[app.currentLayer]);
				if(!s&&!c&&!a){
					parent.currentLayer=l;
					if(parent.guiController!=null)
						parent.guiController.currentLayerChanged();
				}

				break;

				//SPACE
			case 32:/*'space: disable pattern*/
				if(!s&&!c&&!a){
					if(layer!=null){
						parent.layerEnabled[parent.currentLayer]=!parent.layerEnabled[parent.currentLayer];
					}else{
						parent.newLayerInCurrentLayer();
					}
				}
				if(s&&!c&&!a){
					parent.layers[parent.currentLayer]=null;
				}
				if(s&&c&&!a){
					//if(app.soundController!=null)
					//app.soundController.draw=!app.soundController.draw;
				}      
				break;

			case 86/*'v' paste*/:  
				if((apl||c)&&!s&&!a){
					if(clipboard==null){
						parent.layerEnabled[parent.currentLayer]=false;
						parent.layers[parent.currentLayer]=null;
					}else{
 						Layer Layer = clipboard.clone();
						FloatParameter bgAlpha = Layer.getFloatParameter("bgAlpha");
						if(bgAlpha!=null){
							if(parent.currentLayer==0)
								bgAlpha.setV(1);
							else
								bgAlpha.setV(0);  
						}
						parent.layers[parent.currentLayer]=Layer;
						if(parent.guiController!=null){
							parent.guiController.autoSelectTab();
						}
					}
				}      
				break;
			case 67/*'c' copy*/:  
				if((apl||c)&&!s&&!a){
					if(parent.layers[parent.currentLayer]==null){
						clipboard=null;
					}else{
						clipboard = parent.layers[parent.currentLayer];
						if(parent.guiController!=null){
							parent.guiController.autoSelectTab();
						}
					}
				}    
				break;

			case 88:/*'x' cut*/
				if((apl||c)&&!s&&!a){
					if(parent.layers[parent.currentLayer]==null){
						clipboard=null;
					}else{
						clipboard = parent.layers[parent.currentLayer];
						parent.layerEnabled[parent.currentLayer]=false;
						parent.layers[parent.currentLayer]=null;
					}
				}    
				break;
			}

			lastPressedTime[keyCode]=System.currentTimeMillis();
		}
	}

	public void keyReleased(int key,int keyCode) {
		pressed[key & 0x7f] = false;
		if(keyCode==16) s=false;
		if(keyCode==18) a=false;
		if(keyCode==17) c=false;
		if(keyCode==157) apl=false;
		if(keyCode==524) win=false;
	}

}


