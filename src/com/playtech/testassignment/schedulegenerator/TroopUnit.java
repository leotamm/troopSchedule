package com.playtech.testassignment.schedulegenerator;

import java.util.HashMap;

public class TroopUnit {
	
	private int patrolTimeStart;
	private int patrolTimeEnd;
	private int squadCount;
	private int soldierCount;
	private int driverCount;
	private HashMap<Integer, Squad> squadMap;
	
	
	public int getPatrolTimeStart() {
		return patrolTimeStart;
	}
	
	public void setPatrolTimeStart(int patrolTimeStart) {
		this.patrolTimeStart = patrolTimeStart;
	}
	
	public int getPatrolTimeEnd() {
		return patrolTimeEnd;
	}
	
	public void setPatrolTimeEnd(int patrolTimeEnd) {
		this.patrolTimeEnd = patrolTimeEnd;
	}

	public int getSquadCount() {
		return squadCount;
	}

	public void setSquadCount(int squadCount) {
		this.squadCount = squadCount;
	}

	public int getSoldierCount() {
		return soldierCount;
	}

	public void setSoldierCount(int soldierCount) {
		this.soldierCount = soldierCount;
	}

	public int getDriverCount() {
		return driverCount;
	}

	public void setDriverCount(int driverCount) {
		this.driverCount = driverCount;
	}

	public HashMap<Integer, Squad> getSquadMap() {
		return squadMap;
	}

	public void setSquadMap(HashMap<Integer, Squad> squadMap) {
		this.squadMap = squadMap;
	}




}
