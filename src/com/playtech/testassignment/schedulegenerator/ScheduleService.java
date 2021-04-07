package com.playtech.testassignment.schedulegenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JsonArray;
import org.json.simple.JsonObject;


public class ScheduleService {

	private static File readFile;

	@SuppressWarnings("resource")
	static void HandleInput() throws Exception {

		System.out.println("Log: Retrieving source file");

		// try accessing input file. if no file, switch to manual input
		Scanner scanner = new Scanner(System.in);
		System.out.println("\nPlease provide the troops source file with full path: ");	
		readFile = new File (scanner.nextLine());

		System.out.println("Log: Accessing file " + String.valueOf(readFile));

		try {
			CheckInputFile(readFile);

		} catch (Exception e) {
			ManualInput("cannot read input file");
		}
	}

	private static void CheckInputFile(File inputFile) throws Exception {

		// check if input txt type. if not, go for manual input
		try {
			String fileExtension = String.valueOf(inputFile).split("\\.")[1];

			if (fileExtension.equals("txt")) {
				ListTheUnit(inputFile);

			} else {
				ManualInput("input file not .txt type");
			}

		} catch (Exception e) {
			ManualInput("cannot determine input file type");
			System.out.println(e.getStackTrace());
		}
	}


	private static TroopUnit troopUnit = new TroopUnit();
	private static Squad squad;


	@SuppressWarnings("resource")
	private static void ListTheUnit(File troopFile) throws FileNotFoundException {

		// get patrol start and end times
		Scanner troopScanner = new Scanner (troopFile);

		String firstLine = troopScanner.nextLine();
		String [] timeBreakdown = String.valueOf(firstLine).split("\\s+");

		int startPatrol = (Integer.valueOf(timeBreakdown[0].split("\\:")[0]));
		int endPatrol = (Integer.valueOf(timeBreakdown[2].split("\\:")[0]));

		troopUnit.setPatrolTimeStart(startPatrol);
		troopUnit.setPatrolTimeEnd(endPatrol);


		// populate squad lists
		HashMap<Integer, Squad> squadMap = new HashMap<Integer, Squad>();
		HashMap<String, String> squadList = new HashMap<String, String>();

		int squadCounter = 0;
		int squadSoldierCounter = 0;
		int squadDriverCounter = 0;

		while (troopScanner.hasNextLine()) {

			String inputLine = troopScanner.nextLine();

			if (inputLine.isEmpty()) {

				// write latest squad to squad map
				if (squadList.size() > 0) {
					squad.setSquadList(squadList);
					squad.setTotalSoldiers(squadSoldierCounter);
					squad.setTotalDrivers(squadDriverCounter);
					squad.setAssignedTroopers(0);

					squadMap.put(squadCounter, squad);

					squad = new Squad();
					squadList = new HashMap<String, String>();

				}
				squadCounter += 1;

				// re-initiate squad
				squad = new Squad();
				squadSoldierCounter = 0;
				squadDriverCounter = 0;

			} else {

				// check if soldier is river
				if (inputLine.contains("Driver") || inputLine.contains("driver")) {
					String [] driverBreakdown = inputLine.split("\\s+");

					// write driver to squad map
					String driverName = driverBreakdown[0] + " " + driverBreakdown[1];
					squadList.put(driverName, "driver");

					squadDriverCounter ++;
					squadSoldierCounter ++;

				} else {

					// write trooper to squad map
					squadList.put(inputLine, "trooper");
					squadSoldierCounter ++;
				}
			}
		}

		// after input file last line, write the last open squad to squad map	
		if (squadList.size() > 0) {
			squad.setSquadList(squadList);
			squad.setTotalSoldiers(squadSoldierCounter);
			squad.setTotalDrivers(squadDriverCounter);
			squad.setAssignedTroopers(0);

			squadMap.put(squadCounter, squad);

		}
		// finalize troopUnit object
		troopUnit.setSquadMap(squadMap);
		troopUnit.setSquadCount(squadCounter);

		System.out.println("Log: File correct");

		CheckTheUnit();
	}


	private static void CheckTheUnit() {

		System.out.println("Log: Checking input data");

		// validate input troopUnit object data
		DecimalFormat df = new DecimalFormat("0000");

		int reportStart = Integer.valueOf(troopUnit.getPatrolTimeStart())*100;
		int reportEnd = Integer.valueOf(troopUnit.getPatrolTimeEnd())*100;
		int squadSoldierCount = 0;
		int totalSoldierCount = 0;
		int totalDriverCount = 0;

		HashMap<Integer, Squad> countThisSquadMap = troopUnit.getSquadMap();
		Squad countThisSquad;
		int outputSquadCounter = 1;	

		for(int i = 0; i < countThisSquadMap.size(); i++) {

			countThisSquad = new Squad();		
			countThisSquad = countThisSquadMap.get(i+1);

			HashMap<String, String> soldiersInSquadList;
			soldiersInSquadList = countThisSquad.getSquadList();

			System.out.println("\nSquad " + String.valueOf(outputSquadCounter));
			outputSquadCounter ++ ;

			for(String soldier : soldiersInSquadList.keySet()) {
			
				if (soldiersInSquadList.get(soldier).equals("driver")) {
					totalDriverCount ++ ;
				}

				squadSoldierCount ++ ;
				totalSoldierCount ++ ;

				System.out.println("Soldier " + String.valueOf(squadSoldierCount) + ": \t" +  soldier + ", " + soldiersInSquadList.get(soldier));
			}
			
			squadSoldierCount = 0;		
		}

		System.out.println("\nLog: New unit confirmed. Patrol starts at " + df.format(reportStart) + 
				" and ends at " + df.format(reportEnd) + " military time. Total " + 
				String.valueOf(troopUnit.getSquadCount()) + " squad(s) with " +
				String.valueOf(totalSoldierCount) + " soldiers (including " + 
				String.valueOf(totalDriverCount) + " drivers).");

		troopUnit.setSoldierCount(totalSoldierCount);
		troopUnit.setDriverCount(totalDriverCount);
	}


	@SuppressWarnings("resource")
	private static void ManualInput(String message) {

		// collect input data manually
		System.out.println("Log: Starting manual input, because " + message + ". Please provide the following information!");

		// requests squad count, accepts only values of 1 and greater
		int manualSquadCount = 0;
		int totalDriversInUnit = 0;

		String scheduleDuration;
		int startHour = -1;
		int endHour = -1;

		System.out.println("\nSchedule start and end time presented in text format:[start_hour] to [end_hour]");
		System.out.println("Example of schedue time:20 to 06");

		// request patrol start and end times, accepts only full valid hours
		while(startHour > 24 || startHour < 0 && endHour > 24 || endHour < 0) {

			Scanner scanner0 = new Scanner(System.in);
			System.out.println("Enter schedule start and end time: ");
			scheduleDuration = scanner0.nextLine();

			String [] scheduleBreakdown = scheduleDuration.split("\\s+");

			startHour = (Integer.valueOf(scheduleBreakdown[0]));
			endHour = (Integer.valueOf(scheduleBreakdown[2]));
		}

		troopUnit.setPatrolTimeStart(startHour);
		troopUnit.setPatrolTimeEnd(endHour);

		HashMap<String, String> manualSquadList;
		Squad manualSquad = new Squad();
		HashMap<Integer,Squad> manualSquadMap = new HashMap<Integer, Squad>();

		// requests squad count, accepts any number greater or equal to 1
		while(manualSquadCount <= 0) {

			Scanner scanner1 = new Scanner(System.in);
			System.out.println("\nEnter the number of squads: ");
			manualSquadCount = scanner1.nextInt();

			for(int i = 0; i < manualSquadCount; i++) {

				int manualSquadSoldierCount = 0;
				System.out.print("-- test_10, received squad count " + String.valueOf(manualSquadCount));

				// requests squad size, accepts only sizes 5-12
				while(manualSquadSoldierCount < 4 || manualSquadSoldierCount > 13) {

					System.out.print("-- test_11, current squad soldier count " + String.valueOf(manualSquadSoldierCount));

					Scanner scanner2 = new Scanner(System.in);
					System.out.println("\nEnter the number of soldiers in squad " + String.valueOf(i+1) + ": ");
					manualSquadSoldierCount = scanner2.nextInt();

					System.out.print("-- test_12 ");

					System.out.println("\nSoldier data in this text format:[rank] [name] (Driver), only if applicable");
					System.out.println("Example of a regular soldier:Sgt. Kawalsky");
					System.out.println("Example of a driver:Pvt. Siler (Driver)");

					manualSquadList = new HashMap<String, String>();
					String manualSoldierName;

					int manualTotalSoldiers = 0;
					int manualTotalDrivers= 0;

					System.out.println("-- test_13 ");

					// requests soldier name, checks if soldier is driver or regular trooper
					for(int j = 0; j < manualSquadSoldierCount; j++) {

						Scanner scanner3 = new Scanner(System.in);
						System.out.println("Enter soldier " + String.valueOf(j+1) + "(" + 
								String.valueOf(manualSquadSoldierCount) + ") of squad " + String.valueOf(i+1) + ": ");
						manualSoldierName = scanner3.nextLine();

						// write driver to squad map
						if (manualSoldierName.contains("Driver") || manualSoldierName.contains("driver")) {

							String [] driverBreakdown = manualSoldierName.split("\\s+");
							String driverName = driverBreakdown[0] + " " + driverBreakdown[1];
							manualSquadList.put(driverName, "driver");

							manualTotalDrivers ++;
							manualTotalSoldiers ++;
							totalDriversInUnit ++;

							// write trooper to squad map
						} else {

							manualSquadList.put(manualSoldierName, "trooper");
							manualTotalSoldiers ++;
						}
					}
					
					// write squad list to squad
					manualSquad.setSquadList(manualSquadList);
					manualSquad.setTotalSoldiers(manualTotalSoldiers);
					manualSquad.setTotalDrivers(manualTotalDrivers);
					manualSquad.setAssignedTroopers(0);
					System.out.print("-- test_14 ");
				}
				
				//write squad to squad map
				manualSquadMap.put(i + 1, manualSquad);
				manualSquad = new Squad();
			}
			
			//write squad map to troop unit and add squad count
			troopUnit.setSquadMap(manualSquadMap);
			troopUnit.setSquadCount(manualSquadCount);


			// check if minimum requirement of 2 drivers per unit met
			if(totalDriversInUnit < 2) {
				ManualInput("troop unit needs at least 2 drivers and you entered " + String.valueOf(totalDriversInUnit));
			}
		}
		CheckTheUnit();
	}


	private static UnitSchedule unitSchedule = new UnitSchedule();
	private static ScuadSchedule squadSchedule;

	public static void CreateSchedule() {

		// calculate schedule length
		int scheduleLenght = 0;
		double schedulePatrolDutyPerSquad = 0d;
		double scheduleSoldierDutyHours = 0d;
		int truncatedDutyHours;

		int scheduleSoldierCount = troopUnit.getSoldierCount();
		int scheduleDriverCount = troopUnit.getDriverCount();
		int scheduleStartHour = troopUnit.getPatrolTimeStart();
		int scheduleEndHour = troopUnit.getPatrolTimeEnd();
		int scheduleSquadCount = troopUnit.getSquadCount();

		HashMap<Integer, ScuadSchedule> unitScheduleMap = new HashMap<Integer, ScuadSchedule>();

		HashMap<Integer, Squad> scheduleSquadMap = troopUnit.getSquadMap();
		Squad scheduleInputSquad;

		boolean topToDown = true;

		if (scheduleEndHour > scheduleStartHour) {
			scheduleLenght = scheduleEndHour - scheduleStartHour;
		} else {
			scheduleLenght = 24 - scheduleStartHour + scheduleEndHour;
		}

		schedulePatrolDutyPerSquad = (double) (scheduleLenght / scheduleSquadCount);

		if (scheduleLenght % scheduleSquadCount >= 1 && scheduleSquadCount <= 3) {
			schedulePatrolDutyPerSquad ++ ;
		}


		// check if drivers participate in patrols
		if (scheduleLenght > 6) {
			scheduleSoldierDutyHours = (double) (scheduleSquadCount * scheduleLenght + scheduleLenght * 2) / scheduleSoldierCount;
		} else {
			scheduleSoldierDutyHours = (double) (scheduleSquadCount * scheduleLenght + scheduleLenght * 2) / (scheduleSoldierCount-scheduleDriverCount);
		}


		// calculate optimal patrol shift duration
		truncatedDutyHours = (int) scheduleSoldierDutyHours;

		DecimalFormat twoDigits = new DecimalFormat("00");

		System.out.println("Log: Schedule duration: " + String.valueOf(scheduleLenght) + " hours, duty per squad: " + 
				String.valueOf(schedulePatrolDutyPerSquad) + " hours. Calculated duty hours per soldier: " + String.valueOf(scheduleSoldierDutyHours) + 
				" approcimated to " + String.valueOf(truncatedDutyHours));


		// split into multiple squads
		for(int i = 0; i < scheduleSquadCount; i ++ ) {

			squadSchedule = new ScuadSchedule();
			ArrayList<String> patrolHour = new ArrayList<String>();
			ArrayList<String> patrolStoveSoldier = new ArrayList<String>();
			ArrayList<String> patrolSoldiers = new ArrayList<String>();

			ArrayList<String> scheduleSortedSquadOrder = new ArrayList<String>();
			ArrayList<String> scheduleForSquad;

			scheduleInputSquad = new Squad();
			scheduleInputSquad = scheduleSquadMap.get(i + 1);

			HashMap<String, String> scheduleInputSquadMap = scheduleInputSquad.getSquadList();


			// create 2 schedule start list paths: with and without drivers
			// 1. path with drivers
			if (scheduleLenght > 6) {

				// create a start list of soldiers in the order they will be assigned to schedules: 
				// drivers first, followed by troopers x 3 for extra tight patrol schedules
				Deque<String> scheduleSortedSquadDeque = new ArrayDeque<String>();
				for(String scheduleSoldier : scheduleInputSquadMap.keySet()) {

					if(scheduleInputSquadMap.get(scheduleSoldier).equals("driver")) {
						scheduleSortedSquadDeque.addFirst(scheduleSoldier + " (Driver)");
					} else {
						scheduleSortedSquadDeque.addLast(scheduleSoldier);
					}				
				}

				Iterator<String> iterator = scheduleSortedSquadDeque.iterator();
				
				while (iterator.hasNext()) {
					scheduleSortedSquadOrder.add(iterator.next());					
				}

				for(int ii = 0; ii < 4; ii ++ ) {
					for(String scheduleSoldier : scheduleInputSquadMap.keySet()) {
						if(scheduleInputSquadMap.get(scheduleSoldier).equals("trooper")) {
							scheduleSortedSquadOrder.add(scheduleSoldier);
						}
					}
				}

				// 2. path without drivers
			} else {

				// create an elongated list of troopers, we need a lot of them on patrols
				for(int iii = 0; iii < 5; iii ++ ) {
					
					for(String scheduleSoldier : scheduleInputSquadMap.keySet()) {
						if(scheduleInputSquadMap.get(scheduleSoldier).equals("trooper")) {
							scheduleSortedSquadOrder.add(scheduleSoldier);
						}		
					}
				}
			}


			// populate hour sequence 22 -> 23 -> 00 -> 01 and displaying as double digits + :00
			for(int j = 0; j < scheduleLenght; j ++) {
				
				int tempHour = scheduleStartHour + j;
				if(scheduleStartHour + j > 23) {
					tempHour = scheduleStartHour + j - 24;
				}
				
				StringBuilder doubleDigitNumber = new StringBuilder();
				doubleDigitNumber.append(twoDigits.format(tempHour));
				doubleDigitNumber.append(":00");
				patrolHour.add(doubleDigitNumber.toString());
				patrolStoveSoldier.add("-");
				patrolSoldiers.add("-");
			}


			// populate patrol schedule
			scheduleForSquad = new ArrayList<String>();
			String thisPair = "";

			int squadListDutyOrder = 0;

			// divide patrol hours by optimal shift lengths, man with two soldiers
			for (int jj = 0 ; jj < schedulePatrolDutyPerSquad; jj ++) {

				StringBuilder squadPatrolPair = new StringBuilder();

				if (jj % truncatedDutyHours == 0) {

					String firstSoldier = scheduleSortedSquadOrder.get(squadListDutyOrder);
					String secondSoldier = scheduleSortedSquadOrder.get(squadListDutyOrder+1);

					while(firstSoldier.equals(secondSoldier)) {
						squadListDutyOrder ++;
						secondSoldier = scheduleSortedSquadOrder.get(squadListDutyOrder+1);
					}

					squadPatrolPair.append(firstSoldier);
					squadPatrolPair.append(", ");
					squadPatrolPair.append(secondSoldier);
					thisPair = squadPatrolPair.toString();
					squadListDutyOrder ++ ;
				}

				scheduleForSquad.add(thisPair);
			}

			if(topToDown) {
				int listIndex = 0;
				if (i > 1) {
					listIndex += truncatedDutyHours;
					if (scheduleSquadCount == 3) {
						listIndex += 2;
					}
				}

				for(String soldierPair : scheduleForSquad) {
					patrolSoldiers.set(listIndex, soldierPair);
					listIndex ++ ;
				}			

			} else {
				int listIndex = patrolSoldiers.size() - 1;
				if (i > 1) {
					listIndex -= truncatedDutyHours;
				}
				if (scheduleLenght % scheduleSquadCount != 0 && scheduleSquadCount - 1 < 2) {
					scheduleForSquad.remove(scheduleForSquad.size()-1);
				}

				for(String soldierPair : scheduleForSquad) {
					patrolSoldiers.set(listIndex, soldierPair);
					listIndex -- ;
				}			
			}


			// populate stove schedule, by the order of squadListDutyOrder in 1-hour shifts
			if(topToDown) {

				for(int stoveHour = 0; stoveHour < scheduleLenght; stoveHour ++) {
					String trooperNextInLine = scheduleSortedSquadOrder.get(squadListDutyOrder);
					String troopersInPatrol = patrolSoldiers.get(stoveHour);

					while(troopersInPatrol.contains(trooperNextInLine)) {
						squadListDutyOrder ++;
						trooperNextInLine = scheduleSortedSquadOrder.get(squadListDutyOrder);
					}

					patrolStoveSoldier.set(stoveHour, trooperNextInLine);
					squadListDutyOrder ++;
				}

			} else {

				for(int stoveHour = scheduleLenght; stoveHour > 0 ; stoveHour --) {
					String trooperNextInLine = scheduleSortedSquadOrder.get(squadListDutyOrder);
					String troopersInPatrol = patrolSoldiers.get(stoveHour-1);

					while(troopersInPatrol.contains(trooperNextInLine)) {
						squadListDutyOrder ++;
						trooperNextInLine = scheduleSortedSquadOrder.get(squadListDutyOrder);
					}

					patrolStoveSoldier.set(stoveHour-1, scheduleSortedSquadOrder.get(squadListDutyOrder));
					squadListDutyOrder ++;
				}		
			}

			squadSchedule.setPatrolHour(patrolHour);
			squadSchedule.setPatrolSoldiers(patrolSoldiers);
			squadSchedule.setPatrolStoveSoldier(patrolStoveSoldier);

			unitScheduleMap.put(i + 1, squadSchedule);
			unitSchedule.setScheduleMap(unitScheduleMap);			

			topToDown = !topToDown;
		}
	}


	private static ScuadSchedule outputSquadSchedule;
	private static JsonObject jsonObject = new JsonObject();

	@SuppressWarnings("resource")
	public static void OutputSchedule() {

		// disassemble troop unit to squads
		HashMap<Integer, ScuadSchedule> outputScheduleMap = new HashMap<Integer, ScuadSchedule>();
		outputScheduleMap = unitSchedule.getScheduleMap();

		outputSquadSchedule = new ScuadSchedule();

		ArrayList<String> outputPatrolHour;
		ArrayList<String> outputPatrolStoveSoldier;
		ArrayList<String> outputPatrolSoldiers;
		
		JsonArray troopsArray = new JsonArray();
		JsonArray squadArray = new JsonArray();

		// output squad schedules
		// create JSON object in the background and output if needed later
		for (int i = 0; i < outputScheduleMap.size(); i ++) {

			outputSquadSchedule = outputScheduleMap.get(i+1);

			outputPatrolHour = new ArrayList<String>();
			outputPatrolStoveSoldier = new ArrayList<String>();
			outputPatrolSoldiers = new ArrayList<String>();

			outputPatrolHour = outputSquadSchedule.getPatrolHour();
			outputPatrolStoveSoldier = outputSquadSchedule.getPatrolStoveSoldier();
			outputPatrolSoldiers = outputSquadSchedule.getPatrolSoldiers();

			System.out.println("\nSQUAD #" + String.valueOf(i + 1) + " TIMETABLE");
			System.out.println("Time\tStove-Watch\tPatrol");

			squadArray = new JsonArray();
			JsonArray timeArray = new JsonArray();
			JsonArray stoveArray = new JsonArray();
			JsonArray patrolArray = new JsonArray();

			for (int j = 0; j < outputPatrolHour.size(); j ++) {
				
				JsonObject time = new JsonObject();
				JsonObject stove = new JsonObject();
				JsonObject patrol = new JsonObject();

				System.out.println(String.valueOf(outputPatrolHour.get(j)) + "\t" + String.valueOf(outputPatrolStoveSoldier.get(j)) + 
						"\t" + String.valueOf(outputPatrolSoldiers.get(j)));

				time.put("time", String.valueOf(outputPatrolHour.get(j)));
				stove.put("stove", String.valueOf(outputPatrolStoveSoldier.get(j)));
				patrol.put("patrol", String.valueOf(outputPatrolSoldiers.get(j)));
				
				timeArray.add(time);
				stoveArray.add(stove);
				patrolArray.add(patrol);
			}
			
			squadArray.add(timeArray);
			squadArray.add(stoveArray);
			squadArray.add(patrolArray);
			
			troopsArray.add(squadArray);
		}
		
		jsonObject.put("Troops",troopsArray);
			
		
		// check if JSON file is needed
		Scanner scannerNeedJSON = new Scanner(System.in);
		System.out.println("\nDo you need a JSON file of the schedue? y/n");
		String reply = scannerNeedJSON.nextLine();

		if (reply.equals("y")) {
			OutputJSON();
		}
	}

	@SuppressWarnings("resource")
	private static void OutputJSON() {

		// create and save schedule as JSON file 	
		Scanner fileLocationScanner = new Scanner(System.in);
		System.out.println("\nPlease provide full path for output file: ");
		String userPath = fileLocationScanner.nextLine();
		
		if (userPath.isEmpty()) {		
			System.out.println("Let's try again...");
			OutputJSON();
		}

		try {
			File file = new File(userPath + "/troop_schedule.json");
			file.createNewFile();
			
			FileWriter newFile = new FileWriter(userPath + "/troop_schedule.json");
			newFile.write(jsonObject.toJson());
			newFile.flush();
			newFile.close();

		} catch (IOException e) {
			System.out.println("Log: Creating JSON failed");
			e.printStackTrace();
		}
	}

}
