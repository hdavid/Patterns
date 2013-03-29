import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

public class Config{

	public static int 	  SCREEN   		= -1;// screen number for automatic full screen. put -1 to disable 
	public static int     WIDTH        	= 800;//width of the screen. usually 1024
	public static int     HEIGHT       	= 600;//height of the screen. usually 768
	public static int     X            	= 0;//1280;//window position. if the laptop screen is 1280 wide, then set this to 1280
	public static int     Y            	= 0;//window position usually you can leave this to Zero
	public static boolean ALWAYS_ON_TOP = false;// false or true. must be true for live show on 2nd screen
	public static boolean BORDERS		= true;
	
	public static boolean PREVIEW		= false;
	public static boolean SYPHON		= false;
	public static boolean OFFSCREEN		= false;
	public static String  LAYER_TYPES	= "LayerNineBlockPattern,LayerBars,LayerNoShape,LayerNoise,LayerCurve,LayerQuad,LayerLines";
	
	public static String DEFAULT_PATTERN = "default.all.txt";

	//controllers
	public static boolean MOUSE        	= true;
	public static boolean GUI          	= true;
	public static boolean GUI_VISIBLE  	= true;
	public static boolean GUI_WINDOW   	= true;
	public static boolean KEYBOARD     	= true;

	//Display Settings
	public static boolean CURSOR       	= true;
	public static int     PATTERN_WIDTH	= 30;
	public static int     FRAMERATE    	= 60;
	public static boolean VERTICAL_SYNC	= false;
	public static int     OPENGL_ANTIALIASING= 4;
	public static String  FONT         	= 
		"DINCond-Black-100.vlw";
	
	public static int     DMX_UNIV_SIZE = 3*6;
	public static boolean DMX_LANBOX   	= false;
	public static String  DMX_LANBOX_IP = "192.168.1.77";
	public static int     DMX_LANBOX_PORT= 4777;
	public static boolean DMX_DMXPRO   	= false;
	public static String  DMX_DMXPRO_PORT="COM4";
	public static int     DMX_DMXPRO_BAUDRATE  = 115200;

	//OSC
	public static boolean OSC          	= false;
	public static int    OSC_PORT      	= 32000;
	public static String OSC_BCAST_ADDR	= "192.168.1.255";
	public static boolean OSC_BEAT     	= true;

	//SOUND
	public static boolean SOUND       	= true;
	public static int SAMPLERATE       	= 22050;
	public static int BUFFERSIZE       	= 512;
	public static int FFT_BAND_PER_OCT 	= 12;
	public static int FFT_BASE_FREQ    	= 55;
	public static int BPM              	= 140;
	public static boolean DSP          	= true;//perform sound processing (disable if beat only via OSC)

	//MIDI
	public static boolean RWMIDI		= true;
	public static String MIDI_IN_NAME	= "^(?:mots-in)|^(?:PhotonX25 Unknown.*)|(?:PhotonX25)|(?:.*alesis.*)|(?:.*name.*)|(?:.*Bus 1.*)$";
	
	public static boolean PRINT_THREAD_TIME=false;

	private static Properties properties = null;

	
	
	static{
		loadProperties();
	}

	private static void loadProperties() {

		try {
			System.out.println("Loading config.");
			properties = new Properties();
			//load config file

			InputStream propertiesFile = new FileInputStream("application.properties");
			properties.load(propertiesFile);
			propertiesFile.close();
		} catch (Exception ioe) {
			System.out.println("I/O Exception. could not read "+"application.properties");
			//ioe.printStackTrace();
		}

		if(properties!=null){
			//transfer it into config object.
			Config c = new Config();
			Field fieldlist[] = Config.class.getDeclaredFields();
			for(Field field : fieldlist){
				if(properties.get(field.getName())!=null){
					try{
						if("int".equals(field.getType().getName())){
							field.setInt(c, getInt(field.getName()));
						}else if("boolean".equals(field.getType().getName())){
							field.setBoolean(c, getBoolean(field.getName()));
						}else if((field.getType().getName()).equals("java.lang.String")){
							System.out.println(field.getName() + " "+properties.get(field.getName()));
							field.set(c, getString(field.getName()));
						}else{
							System.out.println("unknown type : "+field.getType().getName());
						}
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}

	}

	private static String getString(String key){
		if(properties==null)loadProperties();
		return(properties.getProperty(key));
	}

	private static boolean getBoolean(String key){
		if(properties==null)loadProperties();
		return("true".equalsIgnoreCase(properties.getProperty(key)));
	}

	private static int getInt(String key){
		if(properties==null)loadProperties();
		try{
			return(Integer.parseInt(properties.getProperty(key)));
		}catch(Exception e){
			return(-1);
		}
	}

}