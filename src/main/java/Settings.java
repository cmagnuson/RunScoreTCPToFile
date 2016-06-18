

public class Settings {

//	private static final Logger log = Logger.getLogger(Settings.class);

	public static final boolean USE_V2_TCP = true;
	
//	static{
//		log.info("Starting load of properties");
//		
//		Properties props = new Properties();
//		try{
//			props.load(new FileInputStream("tcp.properties"));
//			log.trace("Properties file: "+props);
//			
//			DB_SERVER = props.getProperty("DB_SERVER");
//			DB_USERNAME = props.getProperty("DB_USERNAME");
//			DB_PASS = props.getProperty("DB_PASS");
//			DB_PORT = props.getProperty("DB_PORT");
//			DB_DATABASE = props.getProperty("DB_DATABASE");
//			
//			JMS_URL = props.getProperty("JMS_URL");
//			JMS_USERNAME = props.getProperty("JMS_USERNAME");
//			JMS_PASSWORD = props.getProperty("JMS_PASSWORD");
//			
//			USE_V2_TCP = props.getProperty("USE_V2_TCP").equals("true");
//			USE_JMS = props.getProperty("USE_JMS").equals("true");
//
//			REPUBLISH_TO_JMS_ON_UPDATE = props.getProperty("REPUBLISH_TO_JMS_ON_UPDATE").equals("true");
//		}
//		catch(IOException io){
//			log.error("Error loading properties file", io);
//		}
//		
//		log.info("Load of properties complete");
//	}
}
