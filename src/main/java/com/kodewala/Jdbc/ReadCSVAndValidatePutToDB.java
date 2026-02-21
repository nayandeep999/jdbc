package com.kodewala.Jdbc;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.validator.routines.EmailValidator;

public class ReadCSVAndValidatePutToDB {

	public static boolean validateEmail(String email) {
		return EmailValidator.getInstance().isValid(email);
	}

	public static boolean validateFirstName(String firstName) {
		return firstName.matches("(?i)^[a-z ,.'-]+$");
	}

	public static boolean validateLastName(String LastName) {
		return LastName.matches("(?i)^[a-z ,.'-]+$");
	}

	public static boolean validateCustomerId(String customerId) {
		return customerId.matches("-?\\d+");
	}

	public static void main(String[] args) throws IOException {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection conn = null;
		PreparedStatement insetIntoValidDb = null;
		PreparedStatement insertIntoInvalidDb = null;
		Reader in = null;
		CSVParser parser = null;

		try {

			long startTime = System.currentTimeMillis();

			in = new FileReader("D:/Eclipse-Workplace/Jdbc/src/main/java/customers_unclean.csv");
			CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
			parser = csvFormat.parse(in);

			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Kodewala", "root", "0000");
			String sqlToInsetValidDb = "INSERT INTO customer_info(customer_id, firstName, lastName, email) VALUES (?, ?, ?, ?)";
			String sqlToInserInvalidDb = "INSERT INTO customer_info_invalid(customer_id, firstName, lastName, email) VALUES (?, ?, ?, ?)";
			insetIntoValidDb = conn.prepareStatement(sqlToInsetValidDb);
			insertIntoInvalidDb = conn.prepareStatement(sqlToInserInvalidDb);

			int batchSize = 15;
			int iterationCounterForValidDb = 0;
			int iterationCounterForInvalidValidDb = 0;

			for (CSVRecord record : parser) {
				String customerId = record.get("customer_id");
				String email = record.get("email");
				String firstName = record.get("firstName");
				String lastName = record.get("lastName");

				if (validateCustomerId(customerId) && validateEmail(email) && validateFirstName(firstName)
						&& validateLastName(lastName)) {
					/*
					 * System.out.println(customerId + " " + email + " " + firstName + " " +
					 * lastName);
					 */

					insetIntoValidDb.setString(1, customerId);
					insetIntoValidDb.setString(2, firstName);
					insetIntoValidDb.setString(3, lastName);
					insetIntoValidDb.setString(4, email);

					insetIntoValidDb.addBatch();
					System.out.println("Valid data added to the batch");

					if (iterationCounterForValidDb % batchSize == 0) {
						System.out.println("Executing valid data batch of " + iterationCounterForValidDb);
						insetIntoValidDb.executeBatch();
					}

					// insetIntoValidDb.executeUpdate();

					iterationCounterForValidDb++;

				} else {

					insertIntoInvalidDb.setString(1, customerId);
					insertIntoInvalidDb.setString(2, firstName);
					insertIntoInvalidDb.setString(3, lastName);
					insertIntoInvalidDb.setString(4, email);

					System.out.println("Invalid data added to the batch");
					insertIntoInvalidDb.addBatch();

					if (iterationCounterForInvalidValidDb % batchSize == 0) {
						System.out.println("Executing invalid data batch of " + iterationCounterForInvalidValidDb);
						insertIntoInvalidDb.executeBatch();
					}

					// insertIntoInvalidDb.executeUpdate();

					iterationCounterForInvalidValidDb++;
				}
			}

			System.out.println("Executing remaining batch of Valid & Invalid data");
			insetIntoValidDb.executeBatch();
			insertIntoInvalidDb.executeBatch();

			long endTime = System.currentTimeMillis();
			System.out.println("Total time taken: " + (endTime - startTime) / 1000 + " seconds");

		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			try {
				conn.close();
				insetIntoValidDb.close();
				insertIntoInvalidDb.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
			in.close();
			parser.close();
		}

	}

}
