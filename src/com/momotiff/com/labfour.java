// CS 4350.01 - Lab 4: Pomona Transit System
// Mohraiel Matta
// Tiffany Truong

package com.momotiff.com;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class labfour {
	private Connection connection;

	public labfour() {
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/lab 4", "postgres", "______");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Display Schedule
	public void displayTripSchedule(String startLocation, String destination, String date) {
		String query = "SELECT * FROM TripOffering WHERE TripNumber IN " +
				"(SELECT TripNumber FROM Trip WHERE StartLocationName = ? AND DestinationName = ?) " +
				"AND Date = CAST(? AS DATE)";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, startLocation);
			stmt.setString(2, destination);
			stmt.setString(3, date);

			ResultSet rs = stmt.executeQuery();

			if (!rs.next()) {
				System.out.println("No trips found for the specified location, destination, and date.");
			} else {
				do {
					System.out.println("Trip Number: " + rs.getInt("TripNumber") +
							", Date: " + rs.getDate("Date") +
							", Scheduled Start Time: " + rs.getTime("ScheduledStartTime") +
							", Scheduled Arrival Time: " + rs.getTime("ScheduledArrivalTime") +
							", Driver: " + rs.getString("DriverName") +
							", Bus ID: " + rs.getInt("BusID"));
				} while (rs.next());
			}

		} catch (SQLException e) {
		}
	}

	// Deletes a trip offering
	public void deleteTripOffering(int tripNumber, String date, String startTime) {
		String query = "DELETE FROM TripOffering WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, tripNumber);
			LocalDate localdate = LocalDate.parse(date);
			stmt.setDate(2, Date.valueOf(localdate));
			LocalTime localtime = LocalTime.parse(startTime);
			stmt.setTime(3, Time.valueOf(localtime));
			int rowsAffected = stmt.executeUpdate();
			System.out.println("Deleted " + rowsAffected + " trip offering(s).");
		} catch (SQLException e) {
			System.out.println("Error deleting trip offering: " + e.getMessage());
		}
	}

	// Add a new trip offering
	public void addTripOffering(int tripNumber, String date, String startTime, String arrivalTime,
			String driverName, int busID) {
		String query = "INSERT INTO TripOffering (TripNumber, Date, ScheduledStartTime, ScheduledArrivalTime, DriverName, BusID) "
				+
				"VALUES (?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, tripNumber);
			LocalDate localdate = LocalDate.parse(date);
			stmt.setDate(2, Date.valueOf(localdate));
			LocalTime localstarttime = LocalTime.parse(startTime);
			stmt.setTime(3, Time.valueOf(localstarttime));
			LocalTime localarrivaltime = LocalTime.parse(arrivalTime);
			stmt.setTime(4, Time.valueOf(localarrivaltime));
			stmt.setString(5, driverName);
			stmt.setInt(6, busID);
			stmt.executeUpdate();
			System.out.println("Trip offering added successfully.");
		} catch (SQLException e) {
			System.out.println("Error adding trip offering: " + e.getMessage());
		}
	}

	// Change driver from trip offering
	public void changeDriverForTripOffering(int tripNumber, String date, String startTime, String newDriverName) {
		String query = "UPDATE TripOffering SET DriverName = ? WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, newDriverName);
			stmt.setInt(2, tripNumber);
			LocalDate localdate = LocalDate.parse(date);
			stmt.setDate(3, Date.valueOf(localdate));
			LocalTime localstarttime = LocalTime.parse(startTime);
			stmt.setTime(4, Time.valueOf(localstarttime));
			stmt.executeUpdate();
			System.out.println("Driver updated successfully.");
		} catch (SQLException e) {
			System.out.println("Error changing driver: " + e.getMessage());
		}
	}

	// Change bus from trip offering
	public void changeBusForTripOffering(int tripNumber, String date, String startTime, int newBusID) {
		String query = "UPDATE TripOffering SET BusID = ? WHERE TripNumber = ? AND Date = ? AND ScheduledStartTime = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, newBusID);
			stmt.setInt(2, tripNumber);
			LocalDate localdate = LocalDate.parse(date);
			stmt.setDate(3, Date.valueOf(localdate));
			LocalTime localstarttime = LocalTime.parse(startTime);
			stmt.setTime(4, Time.valueOf(localstarttime));
			stmt.executeUpdate();
			System.out.println("Bus updated successfully.");
		} catch (SQLException e) {
			System.out.println("Error changing bus: " + e.getMessage());
		}
	}

	// Display the stops of a given trip
	public void displayTripStops(int tripNumber) {
		String query = "SELECT * FROM TripStopInfo WHERE TripNumber = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, tripNumber);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				System.out.println("Stop Number: " + rs.getInt("StopNumber") +
						", Sequence Number: " + rs.getInt("SequenceNumber") +
						", Driving Time: " + rs.getTime("DrivingTime"));
			}
		} catch (SQLException e) {
			System.out.println("Error fetching trip stops: " + e.getMessage());
		}
	}

	// Display the weekly schedule of a given driver
	public void displayWeeklyDriverSchedule(String driverName, String startDate, String endDate) {
		// Correcting the date comparison with explicit casting
		String query = "SELECT * FROM TripOffering WHERE DriverName = ? AND Date BETWEEN CAST(? AS DATE) AND CAST(? AS DATE)";

		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			// Set query parameters
			stmt.setString(1, driverName);
			stmt.setString(2, startDate); // These are still strings, but the query casts them to DATE
			stmt.setString(3, endDate);

			// Execute query and process results
			ResultSet rs = stmt.executeQuery();
			System.out.println("Weekly Schedule for Driver: " + driverName);
			boolean found = false;

			while (rs.next()) {
				found = true;
				System.out.println("Trip Number: " + rs.getInt("TripNumber") +
						", Date: " + rs.getDate("Date") +
						", Scheduled Start Time: " + rs.getTime("ScheduledStartTime") +
						", Scheduled Arrival Time: " + rs.getTime("ScheduledArrivalTime"));
			}

			if (!found) {
				System.out.println("No trips found for the specified driver and date range.");
			}

		} catch (SQLException e) {
			System.out.println("Error fetching weekly schedule: " + e.getMessage());
		}
	}

	// Add a new driver
	public void addNewDriver(String driverName, String telephoneNumber) {
		String query = "INSERT INTO Driver (DriverName, DriverTelephoneNumber) VALUES (?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1, driverName);
			stmt.setString(2, telephoneNumber);
			stmt.executeUpdate();
			System.out.println("Driver added successfully.");
		} catch (SQLException e) {
			System.out.println("Error adding new driver: " + e.getMessage());
		}
	}

	// Add a new bus
	public void addNewBus(int busID, String model, int year) {
		String query = "INSERT INTO Bus (BusID, Model, Year) VALUES (?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, busID);
			stmt.setString(2, model);
			stmt.setInt(3, year);
			stmt.executeUpdate();
			System.out.println("Bus added successfully.");
		} catch (SQLException e) {
			System.out.println("Error adding new bus: " + e.getMessage());
		}
	}

	// Delete a bus
	public void deleteBus(int busID) {
		String query = "DELETE FROM Bus WHERE BusID = ?";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, busID);
			stmt.executeUpdate();
			System.out.println("Bus deleted successfully.");
		} catch (SQLException e) {
			System.out.println("Error deleting bus: " + e.getMessage());
		}
	}

	// Record the actual data of a given trip offering
	public void recordActualTripData(int tripNumber, String date, String scheduledStartTime, int stopNumber,
			String scheduledArrivalTime,
			String actualStartTime,
			String actualArrivalTime,
			int numberOfPassengerIn,
			int numberOfPassengerOut) {
		String query = "INSERT INTO ActualTripStopInfo (TripNumber, Date, ScheduledStartTime, StopNumber, ScheduledArrivalTime, "
				+
				"ActualStartTime, ActualArrivalTime, NumberOfPassengerIn, NumberOfPassengerOut) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setInt(1, tripNumber);
			// convert date string to 'YYYY-MM-DD' format
			LocalDate localdate = LocalDate.parse(date);
			stmt.setDate(2, Date.valueOf(localdate));
			// convert all time string to the 'HH:MM:SS' format
			LocalTime localscheduledStartTime = LocalTime.parse(scheduledStartTime);
			stmt.setTime(3, Time.valueOf(localscheduledStartTime));
			stmt.setInt(4, stopNumber);
			LocalTime localscheduledArrivalTime = LocalTime.parse(scheduledArrivalTime);
			stmt.setTime(5, Time.valueOf(localscheduledArrivalTime));
			LocalTime localactualStartTime = LocalTime.parse(actualStartTime);
			stmt.setTime(6, Time.valueOf(localactualStartTime));
			LocalTime localactualArrivalTime = LocalTime.parse(actualArrivalTime);
			stmt.setTime(7, Time.valueOf(localactualArrivalTime));
			stmt.setInt(8, numberOfPassengerIn);
			stmt.setInt(9, numberOfPassengerOut);
			stmt.executeUpdate();
			System.out.println("Actual trip data recorded successfully.");
		} catch (SQLException e) {
			System.out.println("Error recording actual trip data: " + e.getMessage());
		}
	}

	// Main method with a command-line interface (CLI)
	public static void main(String[] args) {
		labfour app = new labfour();
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("\nPomona Transit System - Main Menu");
			System.out.println("1. Display Trip Schedule");
			System.out.println("2. Delete Trip Offering");
			System.out.println("3. Add Trip Offering");
			System.out.println("4. Change Driver for Trip Offering");
			System.out.println("5. Change Bus for Trip Offering");
			System.out.println("6. Display Trip Stops");
			System.out.println("7. Display Weekly Schedule for a Given Driver");
			System.out.println("8. Add a New Driver");
			System.out.println("9. Add a New Bus");
			System.out.println("10. Delete a Bus");
			System.out.println("11. Record Actual Trip Data");
			System.out.println("0. Exit");

			System.out.print("Choose an option: ");
			int choice = scanner.nextInt();

			switch (choice) {
				case 1:
					System.out.print("Enter start location: ");
					scanner.nextLine(); // Consume newline
					String startLocation = scanner.nextLine();
					System.out.print("Enter destination: ");
					String destination = scanner.nextLine();
					System.out.print("Enter date (YYYY-MM-DD): ");
					String date = scanner.nextLine();
					app.displayTripSchedule(startLocation, destination, date);
					break;
				case 2:
					System.out.print("Enter trip number: ");
					int tripNumber = scanner.nextInt();
					System.out.print("Enter date (YYYY-MM-DD): ");
					scanner.nextLine(); // Consume newline
					String tripDate = scanner.nextLine();
					System.out.print("Enter scheduled start time (HH:MM:SS): ");
					String scheduledStartTime = scanner.nextLine();
					app.deleteTripOffering(tripNumber, tripDate, scheduledStartTime);
					break;
				case 3:
					System.out.print("Enter trip number: ");
					int tripNum = scanner.nextInt();
					System.out.print("Enter date (YYYY-MM-DD): ");
					scanner.nextLine(); // Consume newline
					String addDate = scanner.nextLine();
					System.out.print("Enter scheduled start time (HH:MM:SS): ");
					String addStartTime = scanner.nextLine();
					System.out.print("Enter scheduled arrival time (HH:MM:SS): ");
					String addArrivalTime = scanner.nextLine();
					System.out.print("Enter driver name: ");
					String driverName = scanner.nextLine();
					System.out.print("Enter bus ID: ");
					int busID = scanner.nextInt();
					app.addTripOffering(tripNum, addDate, addStartTime, addArrivalTime, driverName, busID);
					break;
				case 4:
					System.out.print("Enter trip number: ");
					int changeTripNum = scanner.nextInt();
					System.out.print("Enter date (YYYY-MM-DD): ");
					scanner.nextLine(); // Consume newline
					String changeDriverDate = scanner.nextLine();
					System.out.print("Enter scheduled start time (HH:MM:SS): ");
					String changeDriverStartTime = scanner.nextLine();
					System.out.print("Enter new driver name: ");
					String newDriver = scanner.nextLine();
					app.changeDriverForTripOffering(changeTripNum, changeDriverDate, changeDriverStartTime, newDriver);
					break;
				case 5:
					System.out.print("Enter trip number: ");
					int changeBusTripNum = scanner.nextInt();
					System.out.print("Enter date (YYYY-MM-DD): ");
					scanner.nextLine(); // Consume newline
					String changeBusDate = scanner.nextLine();
					System.out.print("Enter scheduled start time (HH:MM:SS): ");
					String changeBusStartTime = scanner.nextLine();
					System.out.print("Enter new bus ID: ");
					int newBusID = scanner.nextInt();
					app.changeBusForTripOffering(changeBusTripNum, changeBusDate, changeBusStartTime, newBusID);
					break;
				case 6:
					System.out.print("Enter trip number: ");
					int tripStopsTripNum = scanner.nextInt();
					app.displayTripStops(tripStopsTripNum);
					break;
				case 7:
					System.out.print("Enter driver name: ");
					scanner.nextLine(); // Consume newline after nextInt()
					String driverNameForSchedule = scanner.nextLine(); // Input driver name
					System.out.print("Enter start date (YYYY-MM-DD): ");
					String startDriverDate = scanner.nextLine(); // Input start date
					System.out.print("Enter end date (YYYY-MM-DD): ");
					String endDriverDate = scanner.nextLine(); // Input end date
					app.displayWeeklyDriverSchedule(driverNameForSchedule, startDriverDate, endDriverDate); // Call to
																											// display
																											// schedule
					break;
				case 8:
					System.out.print("Enter new driver name: ");
					scanner.nextLine(); // Consume newline
					String newDriverName = scanner.nextLine();
					System.out.print("Enter driver phone number: ");
					String driverPhoneNumber = scanner.nextLine();
					app.addNewDriver(newDriverName, driverPhoneNumber);
					break;
				case 9:
					System.out.print("Enter bus ID: ");
					int busIDToAdd = scanner.nextInt();
					System.out.print("Enter bus model: ");
					scanner.nextLine();
					String newBusModel = scanner.nextLine();
					System.out.print("Enter bus year: ");
					int busYear = scanner.nextInt();
					app.addNewBus(busIDToAdd, newBusModel, busYear);
					break;
				case 10:
					System.out.print("Enter bus ID: ");
					int busIDToDelete = scanner.nextInt();
					app.deleteBus(busIDToDelete);
					break;
				case 11:
					System.out.print("Enter trip number: ");
					int recordTripNumber = scanner.nextInt();
					System.out.print("Enter date (YYYY-MM-DD): ");
					scanner.nextLine();
					String recordTripDate = scanner.nextLine();
					System.out.print("Enter scheduled start time (HH:MM:SS): ");
					String recordStartTime = scanner.nextLine();
					System.out.print("Enter stop number: ");
					int stopNumber = scanner.nextInt();
					System.out.print("Enter scheduled arrival time (HH:MM:SS): ");
					scanner.nextLine(); // Consume newline
					String recordScheduledArrival = scanner.nextLine();
					System.out.print("Enter actual start time (HH:MM:SS): ");
					String recordActualStart = scanner.nextLine();
					System.out.print("Enter actual arrival time (HH:MM:SS): ");
					String recordActualArrival = scanner.nextLine();
					System.out.print("Enter number of passengers in: ");
					int recordPassengerIn = scanner.nextInt();
					System.out.print("Enter number of passengers out: ");
					int recordPassengerOut = scanner.nextInt();
					app.recordActualTripData(recordTripNumber, recordTripDate, recordStartTime, stopNumber,
							recordScheduledArrival,
							recordActualStart, recordActualArrival, recordPassengerIn, recordPassengerOut);
					break;
				case 0:
					System.out.println("Exiting...");
					scanner.close();
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
					break;
			}
		}
	}
}