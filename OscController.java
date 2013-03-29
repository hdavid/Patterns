import java.util.HashMap;
import oscP5.*;
import netP5.*;

public class OscController {

	private Application app = null;
	private OscP5 oscP5 = null;
	private NetAddress broadcastLocation;
	private String messageType = "/NineBlockPattern";
	//private String name="processing";
	
	private String hostname = "me";
	private int lastMessageSent = 0;
	private HashMap<String, Integer> lastMessagesReceived = new HashMap<String, Integer>();

	public OscController(Application app) {
		this.app = app;
		System.out.println("OSC Controller.");
		// oscP5=new OscP5(applet,OSC_PORT);
		this.broadcastLocation = new NetAddress(Config.OSC_BCAST_ADDR,
				Config.OSC_PORT);
		try {
			this.hostname = java.net.InetAddress.getLocalHost().getHostName();
		} catch (Exception e) {
		}

		/* create a new osc properties object */
		OscProperties properties = new OscProperties();
		properties.setRemoteAddress(Config.OSC_BCAST_ADDR, Config.OSC_PORT);
		properties.setListeningPort(Config.OSC_PORT);
		properties.setDatagramSize(12000);
		oscP5 = new OscP5(app, properties);

	}

	/**
	 * always use this method to build new message it adds our message type,
	 * hostname and message counter.
	 */
	private OscMessage newMessage() {
		OscMessage oscMessage = new OscMessage(this.messageType);
		oscMessage.add(this.hostname);
		oscMessage.add(this.lastMessageSent++);
		return (oscMessage);
	}

	private void broacastMessage(OscMessage oscMessage) {
		this.oscP5.send(oscMessage, broadcastLocation);
	}


	public void broadcastOnBeat() {
		if(Config.OSC_BEAT){
			OscMessage myOscMessage = newMessage();
			myOscMessage.add("onBeat");
			this.broacastMessage(myOscMessage);
		}
	}

	/*public void broadcastLoadPattern(NineBlockPattern p) {
		OscMessage myOscMessage = newMessage();
		myOscMessage.add("loadPattern");
		myOscMessage.add(app.presetManager.saveZipped(p));
		this.broacastMessage(myOscMessage);
	}

	public void broadcastLoadPatternInLayer(NineBlockPattern p, int layer) {
		OscMessage myOscMessage = newMessage();
		myOscMessage.add("loadPatternInLayer");
		myOscMessage.add(layer);
		if (p == null) {
			myOscMessage.add(new byte[0]);
		} else {
		    myOscMessage.add( app.presetManager.saveZipped(p));
		}
		this.broacastMessage(myOscMessage);
	}

	public void broadcastLoadAllPatterns() {

		//OscMessage myOscMessage = newMessage();
		//myOscMessage.add("loadPatternInLayer");
		//myOscMessage.add(app.presetManager.saveAll());
		//this.broacastMessage(myOscMessage);

		for (int i = 0; i < app.layers.length; i++) {
			if (app.layerEnabled[i]) {
				//this.broadcastLoadPatternInLayer(app.layers[i], i);
			} else {
				this.broadcastLoadPatternInLayer(null, i);
			}
		}
	}*/

	public void oscEvent(OscMessage oscMessage) {
		/* check if theOscMessage has the address pattern we are looking for. */
		if (oscMessage.checkAddrPattern(messageType)) {

			String senderHostname = oscMessage.get(0).stringValue();
			if (!senderHostname.equals(hostname)) {

				int messageNumber = oscMessage.get(1).intValue();
				String messageType = oscMessage.get(2).stringValue();
				Integer lastMessageReceived = lastMessagesReceived.get(senderHostname);
				if (lastMessageReceived == null){
					lastMessageReceived = 0;
				}
				if(lastMessageReceived + 1 != messageNumber) {
					System.out
							.println("OSC: missed "
									+ (messageNumber - 1 - lastMessageReceived)
									+ " message"
									+ ((messageNumber - 1 - lastMessageReceived != 1) ? "s"
											: "")
									+" from "+senderHostname);
				}
				lastMessagesReceived.put(senderHostname, messageNumber);

				if (messageType.equals("onBeat")) {
					if (app.soundController != null) {
						//app.soundController.onBeatOsc();
					}

				} else if (messageType.equals("loadPattern")) {
					System.out.println("OSC: received msg : " + messageType);
					if (app.presetManager != null) {
					//	app.layers[app.currentLayer] = app.presetManager
					//			.load(oscMessage.get(3).bytesValue());
					}

				} else if (messageType.equals("loadPatternInLayer")) {
					System.out.println("OSC: received msg : " + messageType);
					if (app.presetManager != null) {
						if (oscMessage.get(4).bytesValue()==null ||oscMessage.get(4).bytesValue().length==0) {
							app.layerEnabled[oscMessage.get(3).intValue()] = false;
							app.layers[oscMessage.get(3).intValue()] = null;
						} else {
							//app.layers[oscMessage.get(3).intValue()] = app.presetManager
							//		.load(oscMessage.get(4).bytesValue());
							app.layerEnabled[oscMessage.get(3).intValue()] = true;
						}
					}
				}else{
					System.out.println("OSC: received unsupported message : '"
							+ messageType + "'");
				}
			}
		} else {
			System.out
					.println("OSC: received unknown osc message. with address pattern "
							+ oscMessage.addrPattern());
			oscMessage.print();
		}
	}
}

