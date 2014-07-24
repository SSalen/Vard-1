package com.ex.augmentedreality;

public class ComponentTable {

	private int id; 
	private String SFI;
	private String manufacturer;
	private String LastFix;
	private String FixType;

	public ComponentTable() {
	}

	public ComponentTable(String SFI, String manufacturer,
			String LastFix, String FixType) {
		super();
		this.SFI = SFI;
		this.manufacturer = manufacturer;
		this.LastFix = LastFix;
		this.FixType = FixType;
	}

	@Override
	public String toString() {
		return "•ID = " + id + "\n•Manufacturer:  " + manufacturer + "\n•SFI:  " + SFI
				+ "\n•Last repair:  " + LastFix;
	}

	public int getId() {
		return id;
	}

	public String getSFI() {
		return SFI;
	}

	public String getManufacturer() {
		// TODO Auto-generated method stub
		return manufacturer;
	}

	public String getLastFix() {
		// TODO Auto-generated method stub
		return LastFix;
	}

	public String getFixType() {
		// TODO Auto-generated method stub
		return FixType;
	}

	public void setId(int theID) {
		// TODO Auto-generated method stub
		id = theID;
	}

	public void setSFI(String string) {
		// TODO Auto-generated method stub
		SFI = string;
	}

	public void setManufacturer(String string) {
		// TODO Auto-generated method stub
		manufacturer = string;
	}

	public void setLastFix(String string) {
		// TODO Auto-generated method stub
		LastFix = string;
	}

	public void setFixType(String string) {
		// TODO Auto-generated method stub
		FixType = string;
	}

}
