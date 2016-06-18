

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CCTCPRequest implements Runnable {

	private final static String CRLF ="\r\n";
	private static Logger log = Logger.getLogger(CCTCPRequest.class.toString());
	private Socket socket;
	private BufferedReader br;
	private OutputStreamWriter osw;
	
	private static final String SERVER_NAME = "RS-Text";
	private final TimeToFile timeToFile;

	private Set<String> seenLocations = new HashSet<>();
	
	public CCTCPRequest(Socket s){
		this.socket = s;

		try {
			//Get references to sockets input and output streams
			//if(!socket.getInetAddress().getHostAddress().equals("127.0.0.1")){
				log.info("New CC connection from: "+socket.getInetAddress()+":"+socket.getLocalPort());
			//}
			InputStream is = this.socket.getInputStream();
			osw = new OutputStreamWriter(new BufferedOutputStream(this.socket.getOutputStream()));

			//Set up input stream filter
			br = new BufferedReader(new InputStreamReader(is));

		} 
		catch (IOException e) {
			log.log(Level.SEVERE, "Error opening reader/writers on socket", e);
			e.printStackTrace();
		}
		
		timeToFile = new TimeToFile();
		new Thread(timeToFile).start();		
	}

	public void run(){
		try{
			while(!socket.isClosed()){
				processRequest();
			}
		}
		catch(Exception io){
			log.log(Level.SEVERE, "Exception working on socket", io);
		}
		finally {
			try{
				osw.close();
				br.close();
				socket.close();
				if(!socket.getInetAddress().getHostAddress().equals("127.0.0.1")){
					log.info("Socket closed");
				}
			}
			catch (IOException e){
				log.log(Level.SEVERE, "Error closing socket", e);
			}
		}
	}

	private void processRequest() throws Exception { 
		String requestLine = br.readLine();
		log.finer("From Toolkit: " + requestLine);

		if(requestLine==null){
			socket.close();
			return;
		}

		String[] storeRequest = ((String[])requestLine.split("@"));
		if(storeRequest.length>1 && storeRequest[1].equals("Store")){
			//data store request

			String location = storeRequest[0];
			String[] times = processTimes(storeRequest);
			String[] chips = processChips(storeRequest);
			Integer[] laps = processLaps(storeRequest);
			
			boolean success = insertTimes(times, chips, laps, location);

			if(success){
				osw.write(SERVER_NAME+"@AckStore@"+storeRequest[storeRequest.length-2]+"@");
				osw.write(CRLF);
				osw.flush();
			}
			else{
				//is there any applicable error handling for the TCP connection, or retries are enough?
			}
		}
		else if(storeRequest.length>1 && storeRequest[1].equals("Passing")){
			//data store request V2

			String location = storeRequest[0];
			String[] times = processTimesV2(storeRequest);
			String[] chips = processChipsV2(storeRequest);
			Integer[] laps = processLapsV2(storeRequest);

			boolean success = insertTimes(times, chips, laps, location);

			if(success){
				osw.write(SERVER_NAME+"@AckPassing@"+storeRequest[storeRequest.length-2]+"@");
				osw.write(CRLF);
				osw.flush();
			}
			else{
				//is there any applicable error handling for the TCP connection, or retries are enough?
				log.log(Level.SEVERE, "Some error occured in inserting batch of times");
			}
		}
		else if(storeRequest.length>1 && storeRequest[1].equals("Pong")){
			String location = storeRequest[0];
			if(!seenLocations.contains(location)){
				log.info("New location connected: "+location);
				boolean success = timeToFile.addLocation(location);
				if(success){
					seenLocations.add(location);
					osw.write(SERVER_NAME+"@AckPong@" + (Settings.USE_V2_TCP ? "Version2.1@c|t|l@" : ""));
					osw.write(CRLF);
					osw.flush();
				}
			}
		}
		else if(storeRequest.length>3 && storeRequest[3].equals("Marker")){
			osw.write(SERVER_NAME+"@AckMarker@"+storeRequest[storeRequest.length-2]+"@");
			osw.write(CRLF);
			osw.flush();
		}
	}
	
	private boolean insertTimes(String[] times, String[] chips, Integer[] laps, String location){
		ArrayList<ChipTime> chipTimes = new ArrayList<>();
		for(int i=0; i<times.length; i++){
			chipTimes.add(new ChipTime(chips[i], times[i], laps[i], location));
		}
		
		return timeToFile.addTimes(chipTimes);
	}

	private String getParam(String input, String param){
		String[] parts = input.split("\\|");
		for(String s: parts){
			String[] parts2 = s.split("=");
			if(parts2.length>1 && parts2[0].equals(param)){
				return parts2[1].trim();
			}
		}
		log.log(Level.WARNING, "Malformed key/value pair: "+input +" for param: "+param);
		return "";
	}

	private String[] processChipsV2(String[] input){
		String[] ret = new String[input.length-4];
		for(int i=0; i<ret.length; i++){
			ret[i] = getParam(input[i+2], "c");
		}
		return ret;
	}

	private String[] processTimesV2(String[] input){
		String[] ret = new String[input.length-4];
		for(int i=0; i<ret.length; i++){
			ret[i]=getParam(input[i+2], "t");
		}
		return ret;
	}

	private Integer[] processLapsV2(String[] input){
		Integer[] ret = new Integer[input.length-4];
		for(int i=0; i<ret.length; i++){
			ret[i]=Integer.valueOf(getParam(input[i+2],"l"));
		}
		return ret;
	}

	private String[] processChips(String[] input){
		String[] ret = new String[input.length-4];
		for(int i=0; i<ret.length; i++){
			ret[i] = input[i+2].substring(0,7);
		}
		return ret;
	}

	private String[] processTimes(String[] input){
		String[] ret = new String[input.length-4];
		for(int i=0; i<ret.length; i++){
			ret[i]=input[i+2].substring(7,19);
		}
		return ret;
	}

	private Integer[] processLaps(String[] input){
		Integer[] ret = new Integer[input.length-4];
		for(int i=0; i<ret.length; i++){
			ret[i]=Integer.valueOf(input[i+2].substring(24,27).trim());
		}
		return ret;
	}
}


