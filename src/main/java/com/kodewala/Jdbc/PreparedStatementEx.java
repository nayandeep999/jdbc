package com.kodewala.Jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PreparedStatementEx {

	public static void main(String[] args) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Kodewala", "root", "0000");
			String sql = "insert into users(username,password) values (?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);

			int batchSize = 15;

			for (int i = 0; i < 100; i++) {
				ps.setString(1, "nayandeep-" + i);
				ps.setString(2, "pass" + i);

				ps.addBatch();
				System.out.println("Added to the batch");

				if (i % 15 == 0) {
					System.out.println("Executing batch of " + i);
					ps.executeBatch();
				}
			}
			System.out.println("Executing remaining batch");
			ps.executeBatch();

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

}
