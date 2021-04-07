package com.playtech.testassignment.schedulegenerator;

/*
 * This program generates a troop patrol schedule from file or manual input.
 * 
 * If no file is provided or data extraction fails, 
 * the program will prompt manual input in console command line.
 * 
 * The program returns schedule in console. Additionally it also creates a file
 * with the schedule in JSON if needed.
 * 
 * JDK 1.8.0.271 used.
 * 
 * ScheduleGenerator class is the controller, while ScheduleService contains services.
 * Repositories and UI not created. User interaction built in console.
 * 
 * To run the program, start it in your IDE and respond to console prompts.
 */


public class ScheduleGenerator {

	public static void main(String[] args) throws Exception {

		System.out.println("Log: Schedule generator started");

		// reads and validates input data
		ScheduleService.HandleInput();
		
		System.out.println("\nLog: Schedule generating started");
		
		// creates schedule
		ScheduleService.CreateSchedule();
		
		System.out.println("\nLog: Schedule output started");
		
		// TODO output in console and create a file
		ScheduleService.OutputSchedule();

		System.out.println("\nLog: Program succesfully executed");

	}

	// C:\Users\Leo\eclipse-workspace3\ScheduleGenerator\troops1.txt	- original assignment file
	// C:\Users\Leo\eclipse-workspace3\ScheduleGenerator\troops2.txt	- new test file
	// C:\Users\Leo\eclipse-workspace3\ScheduleGenerator\troops3.txt	- single squad unit file
	// C:\Users\Leo\eclipse-workspace3\ScheduleGenerator\troops4.txt	- 4 squad unit file
	// C:\Users\Leo\eclipse-workspace3\ScheduleGenerator\troops5.txt	- short duration file
	// C:\Users\Leo\eclipse-workspace3\ScheduleGenerator\troops6.txt	- 3 squad unit file
	
	// C:/Users/Leo/eclipse-workspace3/ScheduleGenerator				- json output file location 
}
