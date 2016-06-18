
import java.net.ServerSocket;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Driver {

	private static Logger log = Logger.getLogger(Driver.class.toString());
	private static final int CC_PORT = 3097; 

	private static ServerSocket ccListenSocket;

	public static String defaultPath = Paths.get(".").toAbsolutePath().normalize().toString();
	
	public static void main(String[] args){
		Logger.getGlobal().setLevel(Level.INFO);
		System.setProperty("java.util.logging.SimpleFormatter.format", 
	            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
				
		log.info("TCP to File running");
		log.info("Saving to files to: "+defaultPath);
		
		//Establish the cc listen socket
		ccListenSocket = null;
		try{
			ccListenSocket = new ServerSocket(CC_PORT);
		}
		catch(Exception e){
			log.log(Level.SEVERE, "Error creating socket on port "+CC_PORT, e);
			e.printStackTrace();
			System.exit(-1);
		}

		Thread ccAcceptServer = new Thread(new Runnable(){
			public void run(){
				//Process TCP service requests in an infinite loop
				while(true) {
					//listen for TCP connection request
					try{
						CCTCPRequest request = new CCTCPRequest(ccListenSocket.accept());
						Thread thread = new Thread(request);
						thread.start();
					}
					catch(Exception e){
						log.log(Level.SEVERE, "Error accepting CC TCP connection", e);
					}
				}
			}
		});
		ccAcceptServer.start();	
	}
}
