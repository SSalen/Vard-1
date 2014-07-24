package com.ex.augmentedreality;

public class ProcessValueTable {
	
	private int id;
	private float temperature;
	private float pressure;
	private float rpm;
	private float torque;
	private float oil_level;
	
	public ProcessValueTable(){}
	
	public ProcessValueTable(float temperature, float pressure, float rpm, float torque, float oil_level) {
		super();
		this.temperature = temperature;
		this.pressure = pressure;
		this.rpm = rpm;
		this.torque = torque;
		this.oil_level = oil_level;
	}
	
	@Override
	public String toString() {
		return " Temp. = " + temperature + "[*C] \n Pressure = " + pressure + "[bar] \n \u03C9 = " + rpm
				+ "[rad/s] \n Torque = " + torque + "[Nm] \n Oil level = " + oil_level + "[%]";
	}

	public int getId() {
		return id;
	}
	
	public String getTemperature() {
		// TODO Auto-generated method stub
		return String.valueOf(temperature);
	}

	public String getPressure() {
		// TODO Auto-generated method stub
		return String.valueOf(pressure);
	}

	public String getRPM() {
		// TODO Auto-generated method stub
		return String.valueOf(rpm);
	}

	public String getTorque() {
		// TODO Auto-generated method stub
		return String.valueOf(torque);
	}

	public String getOil_Level() {
		// TODO Auto-generated method stub
		return String.valueOf(oil_level);
	}

	
	public void setId(int theID) {
		// TODO Auto-generated method stub
		id = theID;
	}


	public void setTemperature(float parseFloat) {
		// TODO Auto-generated method stub
		temperature = parseFloat;
	}

	public void setPressure(float parseFloat) {
		// TODO Auto-generated method stub
		pressure = parseFloat;
	}

	public void setRPM(float parseFloat) {
		// TODO Auto-generated method stub
		rpm = parseFloat;
	}

	public void setTorque(float parseFloat) {
		// TODO Auto-generated method stub
		torque = parseFloat;
	}

	public void setOil_Level(float parseFloat) {
		// TODO Auto-generated method stub
		oil_level = parseFloat;
	}
	

}
