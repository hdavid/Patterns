/*import java.io.File;



public class MIDIControllerTracks extends MIDIController{

	public MIDIControllerTracks(Application app) {
		super(app);
	}


	protected void _noteOn(int midiChannel, int num, int velocity){
		
		NineBlockPattern p = null;
		if(app.layers[app.currentLayer] instanceof LayerNineBlockPattern){
			p = ((LayerNineBlockPattern)app.layers[app.currentLayer]).model;
		}
		
		NineBlockPattern p0 = null;
		if(app.layers[app.currentLayer] instanceof LayerNineBlockPattern){
			p0 = ((LayerNineBlockPattern)app.layers[0]).model;
		}
		
		switch(midiChannel){

		case 0:
			switch (num){
			case 48:if(app.layers[0]!=null)app.layerEnabled[0]=!app.layerEnabled[0];break;
			case 50:if(app.layers[1]!=null)app.layerEnabled[1]=!app.layerEnabled[1];break;
			case 52:if(app.layers[2]!=null)app.layerEnabled[2]=!app.layerEnabled[2];break;
			case 53:if(app.layers[3]!=null)app.layerEnabled[3]=!app.layerEnabled[3];break;
			case 55:if(app.layers[4]!=null)app.layerEnabled[4]=!app.layerEnabled[4];break;
			case 57:if(app.layers[5]!=null)app.layerEnabled[5]=!app.layerEnabled[5];break;
			case 59:if(app.layers[6]!=null)app.layerEnabled[6]=!app.layerEnabled[6];break;
			case 60:if(app.layers[7]!=null)app.layerEnabled[7]=!app.layerEnabled[7];break;
			case 62:if(app.layers[8]!=null)app.layerEnabled[8]=!app.layerEnabled[8];break;
			case 64:if(app.layers[9]!=null)app.layerEnabled[9]=!app.layerEnabled[9];break;

			case 49:if(p!=null)p.fgB.setV(0);break;
			case 51:if(p!=null)p.fgB.setV(0.1f);break;
			case 54:if(p!=null)p.fgB.setV(0.3f);break;
			case 56:if(p!=null)p.fgB.setV(0.5f);break;
			case 58:if(p!=null)p.fgB.setV(0.65f);break;
			case 61:if(p!=null)p.fgB.setV(0.80f);break;
			case 63:if(p!=null)p.fgB.setV(1);break;


			case 48+24:if(app.layers[0]!=null)app.layerEnabled[0]=!app.layerEnabled[0];break;
			case 50+24:if(app.layers[1]!=null)app.layerEnabled[1]=!app.layerEnabled[1];break;
			case 52+24:if(app.layers[2]!=null)app.layerEnabled[2]=!app.layerEnabled[2];break;
			case 53+24:if(app.layers[3]!=null)app.layerEnabled[3]=!app.layerEnabled[3];break;
			case 55+24:if(app.layers[4]!=null)app.layerEnabled[4]=!app.layerEnabled[4];break;
			case 57+24:if(app.layers[5]!=null)app.layerEnabled[5]=!app.layerEnabled[5];break;
			case 59+24:if(app.layers[6]!=null)app.layerEnabled[6]=!app.layerEnabled[6];break;
			case 60+24:if(app.layers[7]!=null)app.layerEnabled[7]=!app.layerEnabled[7];break;
			case 62+24:if(app.layers[8]!=null)app.layerEnabled[8]=!app.layerEnabled[8];break;
			case 64+24:if(app.layers[9]!=null)app.layerEnabled[9]=!app.layerEnabled[9];break;

			case 49+24:if(p!=null)p.fgH.setV(0);break;
			case 51+24:if(p!=null)p.fgH.setV(0.1f);break;
			case 54+24:if(p!=null)p.fgH.setV(0.2f);break;
			case 56+24:if(p!=null)p.fgH.setV(0.4f);break;
			case 58+24:if(p!=null)p.fgH.setV(0.57f);break;
			case 61+24:if(p!=null)p.fgH.setV(0.7f);break;
			case 63+24:if(p!=null)p.fgH.setV(0.8f);break;
			}
			break;
		case 1:
			//handle my stuff here
			switch (num){
				case 36:app.presetManager.loadAll(new File("/Users/mots/Desktop/uprising/34 uprising.txt.all.txt"));
				
				case 48:if(p0!=null)p0.angle.tcIncPerSec=-app.layers[0].angle.tcIncPerSec;break;
				case 49:if(p0!=null)p0.angle.tc=!app.layers[0].angle.tc;break;
				case 50:if(p0!=null)p0.zoom.tcIncPerSec=-app.layers[0].zoom.tcIncPerSec;break;
				case 51:if(p0!=null)p0.zoom.tc=!app.layers[0].zoom.tc;break;
				case 52:if(p0!=null)p0.bgAlpha.doBump(true);break;//redflash
				case 53:if(p0!=null)p0.bgB.doBump(true);break;//redflash
				case 55:if(app.layers[1]!=null)app.layers[1].bgAlpha.doBump(true);break;//blue bass
				case 60:if(p0!=null)p0.rotate.v+=Math.PI/24f;break;
				case 62:if(app.layers[0]!=null)app.layerEnabled[0]=!app.layerEnabled[0];break;
					
				case 67:if(app.layers[2]!=null)app.layers[2].fgAlpha.doBump(true);break;//purple flash lines
				
				default:
					System.out.println("missing mapping : "+num);
				break;			
			}
			break;
		}

	}

	protected void _controllerIn(int midiChannel, int num, int val) {
		if(num==74)changeParameterValue(app.layers[app.currentLayer].fgB,(127-val)/127f);
		if(num==78)changeParameterValue(app.layers[app.currentLayer].fgH,val/127f);
		//if(num==79)app.p[app.currentLayer].fgS.v=val/127f;

		if (app.layers != null) {

			switch (midiChannel) {

			case 0:
				if (app.layers[app.currentLayer] != null) {
					switch (num) {
					case 1:
						changeParameterValue(app.layers[app.currentLayer].size, val / 127f * 4f);
						break;
					case 2:
						changeParameterTc(app.layers[app.currentLayer].scrollX, (val - 64) * 10);
						break;
					case 3:
						changeParameterTc(app.layers[app.currentLayer].scrollY, -(val - 64) * 10);
						break;
						// case
						// 4:app.p[app.currentLayer].rotateScrollX;app.p[app.currentLayer].scale=2+((val-1.0)/126f*30);break;
					}
				}
				break;

			case 10:
				if (app.layers[app.currentLayer] != null) {
					switch (num) {
					case 101:
						changeParameterTc(app.layers[app.currentLayer].scrollX, (val - 64) / 64f * 75f);
						break;
					case 102:
						changeParameterTc(app.layers[app.currentLayer].scrollY, -(val - 64) / 64f * 75f);
						break;
					case 103:
						changeParameterTc(app.layers[app.currentLayer].angle, (val - 64) / 127f * 4f);
						break;
					case 104:
						changeParameterTc(app.layers[app.currentLayer].rotate, (val - 64) / 127f * 4f);

						break;
					case 105:
						if (app.layers[app.currentLayer].zoom.tcIncPerSec >= 0)
							changeParameterTc(app.layers[app.currentLayer].zoom, val / 127f * 2f);
						else
							changeParameterTc(app.layers[app.currentLayer].zoom, -val / 127f * 2f);
						// if(app.p[app.currentLayer].gradient.tcIncPerSec>0)
						// changeParameterTc(app.p[app.currentLayer].gradient,val/126f*4);
						// else
						// changeParameterTc(app.p[app.currentLayer].gradient,-val/126f*4);
						break;

					case 106:
						changeParameterValue(app.layers[app.currentLayer].scrollX,
								(val - 1.0f) / 126f	* app.layers[app.currentLayer].width);
						break;
					case 107:
						changeParameterValue(app.layers[app.currentLayer].scrollY,
								(val - 1.0f) / 126f	* app.layers[app.currentLayer].height);
						break;
					case 108:
						changeParameterValue(app.layers[app.currentLayer].angle,
								((val) / 127f * (float) Math.PI));
						break;
					case 109:
						changeParameterValue(app.layers[app.currentLayer].rotate,
								((val) / 127f * (float) Math.PI) * 2f);
						break;
					case 110:
						changeParameterValue(app.layers[app.currentLayer].zoom,1 + ((val) / 126f * 20f));
						// changeParameterValue(app.p[app.currentLayer].gradient.v=(val-1)/127f;
						break;

					}
				}
				break;

			case 11:
				if (app.layers[app.currentLayer] != null) {
					switch (num) {
					case 101:
						changeParameterValue(app.layers[app.currentLayer].fgH, val/127f,false);
						break;
					case 102:
						changeParameterValue(app.layers[app.currentLayer].fgS, val/127f,false);
						break;
					case 103:
						changeParameterValue(app.layers[app.currentLayer].fgB, val/127f,false);
						break;
					case 104:
						changeParameterValue(app.layers[app.currentLayer].fgAlpha, val/127f,false);
						break;
					case 105:
						if (app.layers[app.currentLayer].gradient.tcIncPerSec >= 0)
							changeParameterTc(app.layers[app.currentLayer].gradient, val/127f*2f);
						else
							changeParameterTc(app.layers[app.currentLayer].gradient, -val/127f*2f);

					case 106:
						changeParameterValue(app.layers[app.currentLayer].bgH, val/127f,false);
						break;
					case 107:
						changeParameterValue(app.layers[app.currentLayer].bgS, val/127f,false);
						break;
					case 108:
						changeParameterValue(app.layers[app.currentLayer].bgB, val/127f,false);
						break;
					case 109:
						changeParameterValue(app.layers[app.currentLayer].bgAlpha, val/127f,false);
						break;
					case 110:
						changeParameterValue(app.layers[app.currentLayer].gradient, val/127f);
						break;

					}
				}
				break;

			case 12:
				switch (num) {
				case 101:
					app.layers[app.currentLayer].shapeCorner.v = (int) ((app.layers[app.currentLayer].shapeCorner.values.length-1)
							* val / 127f);
					break;
				case 102:
					app.layers[app.currentLayer].shapeSide.v = (int) ((app.layers[app.currentLayer].shapeSide.values.length-1)
							* val / 127f);
					break;
				case 103:
					app.layers[app.currentLayer].shapeCenter.v = (int) ((app.layers[app.currentLayer].shapeCenter.values.length-1)
							* val / 127f);
					break;
				case 104:
					app.layers[app.currentLayer].size.setV(val / 127f * 4f);
					break;

				case 106:
					app.layers[app.currentLayer].shapeCornerAngle.v = (int) ((app.layers[app.currentLayer].shapeCornerAngle.values.length -1)
							* val / 127f)*90;
					break;
				case 107:
					app.layers[app.currentLayer].shapeSideAngle.v = (int) ((app.layers[app.currentLayer].shapeSideAngle.values.length-1)
							* val / 127f)*90;
					break;
				case 108:
					app.layers[app.currentLayer].shapeCenterAngle.v = (int) ((app.layers[app.currentLayer].shapeCenterAngle.values.length-1)
							* val / 127f)*90;
					break;					


				case 110:
					if (app.soundController != null) {
						//app.soundController.beatSenseSense = val / 127f * 5f;
					}

					break;
				case 109:
					if (app.soundController != null) {
						//if (val <= 125)
						//	app.soundController.beatSense.put(SoundController.defaultGroup, val / 127f * 60 + 1);
						//app.soundController.autoBeatSense = val >= 125;
					}
					break;
				}
				break;

			case 13:
				switch (num) {
				case 101:
					if (app.currentLayer > 0 && val == 127)
						app.currentLayer--;
					break;
				case 102:
					if (app.currentLayer < 9 && val == 127)
						app.currentLayer++;
					break;
				case 103:
					if (val == 127) {
						if (app.layers[app.currentLayer] == null) {
							app.layers[app.currentLayer] = new NineBlockPattern(this.app);
							if (app.currentLayer > 0)
								app.layers[app.currentLayer].bgAlpha.setV(0);
						} else
							app.layerEnabled[app.currentLayer] = !app.layerEnabled[app.currentLayer];
						break;
					}
				case 104:
					if (val == 127 && app.soundController != null)
						//app.soundController.onBeat();
					break;
				case 105:
					if (val == 127){
						backup=app.layers[app.currentLayer].bgB.vReal();
						app.layers[app.currentLayer].bgB.setV(1.0f);
					}else{
						app.layers[app.currentLayer].bgB.setV(backup);
					}
					break;

				case 106:
					if (val == 127 && app.layers[app.currentLayer] != null) {
						app.layers[app.currentLayer].shapeCorner.sc = !app.layers[app.currentLayer].shapeCorner.sc;
						app.layers[app.currentLayer].shapeCornerAngle.sc = !app.layers[app.currentLayer].shapeCornerAngle.sc;
					}
					break;
				case 107:
					if (val == 127 && app.layers[app.currentLayer] != null) {
						app.layers[app.currentLayer].shapeSide.sc = !app.layers[app.currentLayer].shapeSide.sc;
						app.layers[app.currentLayer].shapeSideAngle.sc = !app.layers[app.currentLayer].shapeSideAngle.sc;
					}
					break;
				case 108:
					if (val == 127 && app.layers[app.currentLayer] != null) {
						app.layers[app.currentLayer].shapeCenterAngle.sc = !app.layers[app.currentLayer].shapeCenterAngle.sc;
						app.layers[app.currentLayer].shapeCenter.sc = !app.layers[app.currentLayer].shapeCenter.sc;
					}
					break;
				case 109:
					if (val == 127 && app.layers[app.currentLayer] != null)
						app.layers[app.currentLayer].fgH.sc = !app.layers[app.currentLayer].fgH.sc;
					break;
				case 110:
					if (val == 127 && app.layers[app.currentLayer] != null)
						app.layers[app.currentLayer].bgH.sc = !app.layers[app.currentLayer].bgH.sc;
					break;

				}
				break;
			}

		}
	}

}*/

