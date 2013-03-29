import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rwmidi.*;

public class MIDIController {

	protected Application app;

	//protected MidiBus midiBus;
	//protected MidiIO midiIO;
	//private MidiInput input;
	//private MidiOutput output;


	protected float pickupThresholdValue=0.1f;
	protected boolean usePickupThresholdValue=false;

	protected float pickupThresholdTc=0.1f;
	protected boolean usePickupThresholdTc=false;

	protected float backup;//used for flashes

	public MIDIController(Application app) {
		this.app = app;
		if(Config.RWMIDI){
			System.out.println("Midi Controller : RWMidi.");
			Pattern pattern = java.util.regex.Pattern.compile(Config.MIDI_IN_NAME);
			for(MidiInputDevice in : RWMidi.getInputDevices()){
				Matcher m = pattern.matcher(in.getName());
				if(m.find()){
					System.out.println("  opening  midi in : "+in.getName());
					in.createInput(this);
				}else{
					System.out.println("  ignoring midi in : "+in.getName());
				}
			}
		}
	}

	public void noteOn(int midiChannel, int num, int velocity){
		_noteOn(midiChannel, num, velocity);
	}
	public void noteOff(int midiChannel, int num, int velocity){

	}
	public void controllerIn(int midiChannel, int num, int val) {
		this._controllerIn(midiChannel, num, val);
	}

	public void programChange(rwmidi.ProgramChange programChange){
		System.out.println(programChange.getCommand());
	}

	public void sysexMessage(rwmidi.SysexMessage sysexMessage){
		System.out.println(
				sysexMessage.getCommand()+" "+
						sysexMessage.getChannel()+" "+
						sysexMessage.getData1()+" "+
						sysexMessage.getData2()+" "+
				"");
		//_noteOn(midiChannel, num, velocity);
	}



	protected void changeParameterValue(FloatParameter param, float value){
		changeParameterValue(param,  value,true);
	}
	protected void changeParameterValue(FloatParameter param, float value,boolean setTC){

		if(!usePickupThresholdValue||java.lang.Math.abs(param.v()-value)<pickupThresholdValue*java.lang.Math.abs(param.max-param.min)){
			if(setTC){
				param.tc=false;
			}
			param.setV(value);
		}
	}
	protected void changeParameterTc(FloatParameter param, float value){
		changeParameterTc(param,  value,true);
	}
	protected void changeParameterTc(FloatParameter param, float value,boolean setTC){
		if(!usePickupThresholdTc||java.lang.Math.abs(param.tcIncPerSec-value)<
				pickupThresholdTc*(java.lang.Math.abs(param.tcIncPerSec)+java.lang.Math.abs(value))){
			param.tcIncPerSec=value;
			if(setTC){
				param.tc=true;
			}
		}
	}


	protected void _noteOn(int midiChannel, int num, int velocity){
		//System.out.println(num);
		LayerNineBlockPattern p = null;
		if(app.layers[app.currentLayer] !=null && app.layers[app.currentLayer] instanceof LayerNineBlockPattern ){
			p=((LayerNineBlockPattern)app.layers[app.currentLayer]);
		}
		
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
	}

	protected void _controllerIn(int midiChannel, int num, int val) {
		//System.out.println(midiChannel + " " + num + " " + val);
		LayerNineBlockPattern p = null;
		if(app.layers[app.currentLayer]!=null && app.layers[app.currentLayer] instanceof LayerNineBlockPattern ){
			p=((LayerNineBlockPattern)app.layers[app.currentLayer]);
		}
		if(p!=null){
		if(num==74)changeParameterValue(p.fgB,(127-val)/127f);
		if(num==78)changeParameterValue(p.fgH,val/127f);
		}
		//if(num==79)app.p[app.currentLayer].fgS.v=val/127f;

		if (p != null) {

			switch (midiChannel) {

			case 0:
				if (p != null) {
					switch (num) {
					case 1:
						changeParameterValue(p.size, val / 127f * 4f);
						break;
					case 2:
						changeParameterTc(p.scrollX, (val - 64) * 10);
						break;
					case 3:
						changeParameterTc(p.scrollY, -(val - 64) * 10);
						break;
						// case
						// 4:app.p[app.currentLayer].rotateScrollX;app.p[app.currentLayer].scale=2+((val-1.0)/126f*30);break;
					}
				}
				break;

			case 10:
				if (p != null) {
					switch (num) {
					case 101:
						changeParameterTc(p.scrollX, (val - 64) / 64f * 75f);
						break;
					case 102:
						changeParameterTc(p.scrollY, -(val - 64) / 64f * 75f);
						break;
					case 103:
						changeParameterTc(p.angle, (val - 64) / 127f * 4f);
						break;
					case 104:
						changeParameterTc(p.rotate, (val - 64) / 127f * 4f);

						break;
					case 105:
						if (p.zoom.tcIncPerSec >= 0)
							changeParameterTc(p.zoom, val / 127f * 2f);
						else
							changeParameterTc(p.zoom, -val / 127f * 2f);
						// if(app.p[app.currentLayer].gradient.tcIncPerSec>0)
						// changeParameterTc(app.p[app.currentLayer].gradient,val/126f*4);
						// else
						// changeParameterTc(app.p[app.currentLayer].gradient,-val/126f*4);
						break;

					case 106:
						changeParameterValue(p.scrollX,
								(val - 1.0f) / 126f	* p.width);
						break;
					case 107:
						changeParameterValue(p.scrollY,
								(val - 1.0f) / 126f	* p.height);
						break;
					case 108:
						changeParameterValue(p.angle,
								((val) / 127f * (float) Math.PI));
						break;
					case 109:
						changeParameterValue(p.rotate,
								((val) / 127f * (float) Math.PI) * 2f);
						break;
					case 110:
						changeParameterValue(p.zoom,1 + ((val) / 126f * 20f));
						// changeParameterValue(app.p[app.currentLayer].gradient.v=(val-1)/127f;
						break;

					}
				}
				break;

			case 11:
				if (p != null) {
					switch (num) {
					case 101:
						changeParameterValue(p.fgH, val/127f,false);
						break;
					case 102:
						changeParameterValue(p.fgS, val/127f,false);
						break;
					case 103:
						changeParameterValue(p.fgB, val/127f,false);
						break;
					case 104:
						changeParameterValue(p.fgAlpha, val/127f,false);
						break;
					case 105:
						if (p.gradient.tcIncPerSec >= 0)
							changeParameterTc(p.gradient, val/127f*2f);
						else
							changeParameterTc(p.gradient, -val/127f*2f);

					case 106:
						changeParameterValue(p.bgH, val/127f,false);
						break;
					case 107:
						changeParameterValue(p.bgS, val/127f,false);
						break;
					case 108:
						changeParameterValue(p.bgB, val/127f,false);
						break;
					case 109:
						changeParameterValue(p.bgAlpha, val/127f,false);
						break;
					case 110:
						changeParameterValue(p.gradient, val/127f);
						break;

					}
				}
				break;

			case 12:
				switch (num) {
				case 101:
					p.shapeCorner.v = (int) ((p.shapeCorner.values.length-1)
							* val / 127f);
					break;
				case 102:
					p.shapeSide.v = (int) ((p.shapeSide.values.length-1)
							* val / 127f);
					break;
				case 103:
					p.shapeCenter.v = (int) ((p.shapeCenter.values.length-1)
							* val / 127f);
					break;
				case 104:
					p.size.setV( val / 127f * 4f);
					break;

				case 106:
					p.shapeCornerAngle.v = (int) ((p.shapeCornerAngle.values.length -1)
							* val / 127f)*90;
					break;
				case 107:
					p.shapeSideAngle.v = (int) ((p.shapeSideAngle.values.length-1)
							* val / 127f)*90;
					break;
				case 108:
					p.shapeCenterAngle.v = (int) ((p.shapeCenterAngle.values.length-1)
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
						/*if (p == null) {
							p = new LayerNineBlockPattern(this.app);
							if (app.currentLayer > 0)
								p.bgAlpha.setV(0);
						} else
							app.layerEnabled[app.currentLayer] = !app.layerEnabled[app.currentLayer];
						break;*/
					}
				case 104:
					if (val == 127 && app.soundController != null)
						//app.soundController.onBeat();
					break;
				case 105:
					if (val == 127){
						backup=p.bgB.v();
						p.bgB.setV(1.0f);
					}else{
						p.bgB.setV(backup);
					}
					break;

				case 106:
					if (val == 127 && p != null) {
						p.shapeCorner.sc = !p.shapeCorner.sc;
						p.shapeCornerAngle.sc = !p.shapeCornerAngle.sc;
					}
					break;
				case 107:
					if (val == 127 && p != null) {
						p.shapeSide.sc = !p.shapeSide.sc;
						p.shapeSideAngle.sc = !p.shapeSideAngle.sc;
					}
					break;
				case 108:
					if (val == 127 && p != null) {
						p.shapeCenterAngle.sc = !p.shapeCenterAngle.sc;
						p.shapeCenter.sc = !p.shapeCenter.sc;
					}
					break;
				case 109:
					if (val == 127 && p != null)
						p.fgH.sc = !p.fgH.sc;
					break;
				case 110:
					if (val == 127 && p != null)
						p.bgH.sc = !p.bgH.sc;
					break;

				}
				break;
			}

		}
	}


	//RW MIDI callbacks
	int midiClockCount=0;
	long lastMidiClockTime=0;
	int midiClockBpmFrame=2;
	long[] lastMidiClockTimes = new long[24*midiClockBpmFrame];
	public void noteOnReceived(rwmidi.Note note){
		if(note.getVelocity()!=0)
			noteOn(note.getChannel(),note.getPitch(),note.getVelocity());
		else
			noteOff(note.getChannel(),note.getPitch(),note.getVelocity());
	}
	public void noteOffReceived(rwmidi.Note note){

		noteOff(note.getChannel(),note.getPitch(),note.getVelocity());
	}
	public void controllerChangeReceived(rwmidi.Controller controller){
		controllerIn(controller.getChannel(),controller.getCC(),controller.getValue());
	}
	public void programChangeReceived(rwmidi.ProgramChange programChange){
		programChange(programChange);

	}
	public void sysexReceived(rwmidi.SysexMessage sysexMessage){
		sysexMessage(sysexMessage);
	}

	public void midiClockReceived(Object bla){
		midiClockCount++;
		long now =System.currentTimeMillis();
		if(now-lastMidiClockTime>50){
			midiClockCount=0;
		}
		lastMidiClockTimes[midiClockCount%24]=now;
		float bpm = midiClockBpmFrame*1000f*60f/(now-lastMidiClockTime+(float)(lastMidiClockTimes[midiClockCount%(24*midiClockBpmFrame)]-lastMidiClockTimes[(midiClockCount+1)%(24*midiClockBpmFrame)]));
		if(midiClockCount%24==0&&this.app.soundController!=null){
			System.out.println("midiClock !"+ midiClockCount/24+" bpm:"+bpm);
			//this.app.soundController.onBeat("-2");
		}
		lastMidiClockTime=now;
	}

}

