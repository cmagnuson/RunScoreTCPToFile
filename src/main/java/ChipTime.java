
public class ChipTime {

	private final String chipCode;
	private final String time;
	private final Integer lap;
	private final String locationName;
	
	public ChipTime(String chipCode, String time, Integer lap, String locationName) {
		String chipCodeTemp = chipCode;
		try {
			Integer i = Integer.parseInt(chipCode);
			chipCodeTemp = ""+i;
		}
		catch(NumberFormatException nfe){
			//ignore
		}
		this.chipCode = chipCodeTemp;
		this.time = time;
		this.lap = lap;
		this.locationName = locationName;
	}

	public final String getChipCode() {
		return chipCode;
	}

	public final String getTime() {
		return time;
	}

	public final Integer getLap() {
		return lap;
	}

	public final String getLocationName() {
		return locationName;
	}
	
	public String toFormattedString() {
		return "RSBCI,"+chipCode+","+ time +","+ locationName +"\r\n";
	}
}
