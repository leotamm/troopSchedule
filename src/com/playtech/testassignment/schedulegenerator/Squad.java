package com.playtech.testassignment.schedulegenerator;

import java.util.HashMap;

public class Squad{
	
	// HashMap entries represent soldiers - key: name, value: trooper or driver
	private HashMap<String, String> squadList;
	private int totalSoldiers;
	private int totalDrivers;
	private int assignedTroopers;
	
	
	public HashMap<String, String> getSquadList() {
		return squadList;
	}
	
	public void setSquadList(HashMap<String, String> squadList) {
		this.squadList = squadList;
	}

	public int getTotalSoldiers() {
		return totalSoldiers;
	}

	public void setTotalSoldiers(int totalSoldiers) {
		this.totalSoldiers = totalSoldiers;
	}

	public int getTotalDrivers() {
		return totalDrivers;
	}

	public void setTotalDrivers(int totalDrivers) {
		this.totalDrivers = totalDrivers;
	}

	public int getAssignedTroopers() {
		return assignedTroopers;
	}

	public void setAssignedTroopers(int assignedTroopers) {
		this.assignedTroopers = assignedTroopers;
	}
	
}
