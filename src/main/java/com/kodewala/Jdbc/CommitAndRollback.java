package com.kodewala.Jdbc;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class InsufficientBalanceException extends Exception {
	public InsufficientBalanceException(String message) {
		super(message);
	}
}

public class CommitAndRollback {

	public static void fundTransfer(String from, String to, int amount, Connection con)
			throws SQLException, InsufficientBalanceException {

		System.out.println(from + " sending " + amount + " rupees to " + to);

		PreparedStatement ps = con.prepareStatement("select amount from balance where user_id = ?");
		ps.setString(1, from);
		ResultSet fromBalanceRs = ps.executeQuery();
		int fromBalance = 0;

		if (!fromBalanceRs.next()) {
			throw new SQLException("User not found");
		}

		fromBalance = fromBalanceRs.getInt("amount");

		if (fromBalance < amount) {
			throw new InsufficientBalanceException("Not enough balance to send money");
		}

		// debit amount from the 'from' account
		ps = con.prepareStatement("update balance set amount = amount - ? where user_id = ?");
		ps.setInt(1, amount);
		ps.setString(2, from);
		ps.executeUpdate();

		// credit amount to 'to' account
		ps = con.prepareStatement("update balance set amount = amount + ? where user_id = ?");
		ps.setInt(1, amount);
		ps.setString(2, to);
		ps.executeUpdate();

		fromBalanceRs.close();
		ps.close();

	}

	public static void main(String[] args) {

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		Connection con = null;
		Statement st = null;
		PreparedStatement ps = null;

		try {

			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/Kodewala", "root", "0000");
			String createTable = "create table if not exists balance(user_id varchar(50) primary key, amount int)";
			st = con.createStatement();
			st.execute(createTable);

			String insertData = "insert into balance(user_id, amount) values(?, ?)";
			ps = con.prepareStatement(insertData);

			ps.setString(1, "user789");
			ps.setInt(2, 1000);
			ps.execute();

			ps.setString(1, "user101112");
			ps.setInt(2, 1000);
			ps.execute();

			System.out.println("Sent 1000 Rupees to both the user");

			con.setAutoCommit(false);

			CommitAndRollback.fundTransfer("user789", "user101112", 200, con);

			con.commit();

			System.out.println("Money transferred successfully");

		} catch (InsufficientBalanceException | SQLException e) {
			try {
				if (con != null)
					con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			System.out.println(e.getMessage());
		} finally {
			try {
				if (st != null)
					st.close();
				if (ps != null)
					ps.close();
				if (con != null)
					con.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

	}

}
