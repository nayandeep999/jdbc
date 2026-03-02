package com.kodewala.Jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

	public static void main(String[] args) {

		try {
			// loading class to give instruction to which driver to use
			Class.forName("com.mysql.cj.jdbc.Driver");

			// establish connection using url
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/Kodewala", "root", "0000");

			// creating statement to pass the query
			Statement stmt = conn.createStatement();

			// executing query
			int tableCreated = stmt.executeUpdate(
					"create table users(username varchar(50) primary key, password varchar(50) not null)");
			int insertingValues = stmt.executeUpdate(
					"insert into users(username,password) values ('nayandeep99','allowmenot'),('shreeram','loljustguess')");

			ResultSet getTable = stmt.executeQuery("select * from users");
			System.out.println("users created: ");
			while (getTable.next()) {
				String username = getTable.getString("username");
				String password = getTable.getString("password");
				System.out.println("Username: " + username + " Password: " + password);
			}
			getTable.close();

			// create products & insert them
			int products = stmt.executeUpdate(
					"create table products(productId int primary key auto_increment, productName varchar(50) not null, price int not null)");

			int insertProducts = stmt.executeUpdate("insert into products(productName, price) values "
					+ "('Wireless Mouse', 599), " + "('Mechanical Keyboard', 2499), " + "('USB-C Cable', 299), "
					+ "('Laptop Stand', 1299), " + "('Webcam HD', 1899), " + "('Headphones', 3499), "
					+ "('Phone Case', 499), " + "('Power Bank 10000mAh', 1499), " + "('HDMI Cable', 399), "
					+ "('Bluetooth Speaker', 2999)");

			System.out.println("\n=== Products ===");
			ResultSet fetchProducts = stmt.executeQuery("select * from products");
			while (fetchProducts.next()) {
				int productId = fetchProducts.getInt("productId");
				String productName = fetchProducts.getString("productName");
				int price = fetchProducts.getInt("price");

				System.out.println(productId + ". " + productName + " - ₹" + price);
			}
			fetchProducts.close();

			// create orders & insert them
			int orders = stmt.executeUpdate("create table orders(" + "orderId int primary key auto_increment, "
					+ "productId int, " + "username varchar(50), " + "quantity int not null, "
					+ "orderDate datetime default current_timestamp, "
					+ "foreign key (productId) references products(productId), "
					+ "foreign key (username) references users(username)" + ")");

			int purchases = stmt.executeUpdate(
					"insert into orders(productId, username, quantity) values " + "(1, 'nayandeep99', 2), "
							+ "(3, 'nayandeep99', 1), " + "(7, 'nayandeep99', 3), " + "(2, 'shreeram', 1), "
							+ "(5, 'shreeram', 2), " + "(10, 'shreeram', 1), " + "(4, 'nayandeep99', 1), "
							+ "(6, 'shreeram', 2), " + "(8, 'nayandeep99', 1), " + "(9, 'shreeram', 1)");

			// Update password
			int updatePass = stmt.executeUpdate("update users set password='jaishreeram' where username='nayandeep99'");

			// update product price
			int updateProductPrice = stmt.executeUpdate("update products set price=800 where productId=1");

			// updating multiple columns
			int multipleColumnUpdate = stmt
					.executeUpdate("update products set productName='Gaming Mouse',price=1200 where productId=1");

			// update all products (increase price by 10%)
			int updatePrice = stmt.executeUpdate("update products set price=price * 1.10");

			// Read users & passwords
			System.out.println("\n=== Updated Users & Passwords ===");
			ResultSet updatedUsersAndPass = stmt.executeQuery("select * from users");
			while (updatedUsersAndPass.next()) {
				String username = updatedUsersAndPass.getString("username");
				String password = updatedUsersAndPass.getString("password");
				System.out.println("Username: " + username + ", Password: " + password);
			}
			updatedUsersAndPass.close();

			// read updated products
			System.out.println("\n=== Updated Products ===");
			ResultSet fetchUpdatedProducts = stmt.executeQuery("select * from products");
			while (fetchUpdatedProducts.next()) {
				int productId = fetchUpdatedProducts.getInt("productId");
				String productName = fetchUpdatedProducts.getString("productName");
				int price = fetchUpdatedProducts.getInt("price");

				System.out.println(productId + ". " + productName + " - ₹" + price);
			}
			fetchUpdatedProducts.close();

			// inner joins to fetch the orders by nayandeep with total price
			System.out.println("\n=== Orders by Nayandeep ===");
			ResultSet ordersByNayandeep = stmt.executeQuery(
					"select o.orderId, o.orderDate, o.quantity, u.username, p.productName, p.price, (p.price * o.quantity) as totalPrice "
							+ "from orders o " + "inner join products p on o.productId = p.productId "
							+ "inner join users u on o.username = u.username " + "where u.username = 'nayandeep99'");

			while (ordersByNayandeep.next()) {
				int orderId = ordersByNayandeep.getInt("orderId");
				String orderDate = ordersByNayandeep.getString("orderDate");
				int quantity = ordersByNayandeep.getInt("quantity");
				String productName = ordersByNayandeep.getString("productName");
				int price = ordersByNayandeep.getInt("price");
				int totalPrice = ordersByNayandeep.getInt("totalPrice");

				System.out.println("Order #" + orderId + " | Product: " + productName + " | Qty: " + quantity
						+ " | Unit Price: ₹" + price + " | Total: ₹" + totalPrice + " | Date: " + orderDate);
			}
			ordersByNayandeep.close();

			// inner joins to fetch the orders by users
			System.out.println("\n=== Orders by Users ===");
			ResultSet ordersByUsers = stmt.executeQuery(
					"select o.orderId, o.orderDate, o.quantity, u.username, p.productName, p.price, (p.price * o.quantity) as totalPrice "
							+ "from orders o " + "inner join products p on o.productId = p.productId "
							+ "inner join users u on o.username = u.username");

			while (ordersByUsers.next()) {
				int orderId = ordersByUsers.getInt("orderId");
				String orderDate = ordersByUsers.getString("orderDate");
				int quantity = ordersByUsers.getInt("quantity");
				String username2 = ordersByUsers.getString("username");
				String productName = ordersByUsers.getString("productName");
				int price = ordersByUsers.getInt("price");
				int totalPrice = ordersByUsers.getInt("totalPrice");

				System.out.println("Order #" + orderId + " | User: " + username2 + " | Product: " + productName
						+ " | Qty: " + quantity + " | Unit Price: ₹" + price + " | Total: ₹" + totalPrice + " | Date: "
						+ orderDate);
			}
			ordersByUsers.close();

			// Individual Order Totals
			System.out.println("\n=== Individual Order Totals ===");
			ResultSet orderTotals = stmt
					.executeQuery("select o.orderId, o.orderDate, o.quantity, u.username, p.productName, p.price, "
							+ "(p.price * o.quantity) as orderTotal " + "from orders o "
							+ "inner join products p on o.productId = p.productId "
							+ "inner join users u on o.username = u.username");

			while (orderTotals.next()) {
				System.out.println("Order #" + orderTotals.getInt("orderId") + " | " + orderTotals.getString("username")
						+ " | " + orderTotals.getString("productName") + " | Qty: " + orderTotals.getInt("quantity")
						+ " | Total: ₹" + orderTotals.getInt("orderTotal"));
			}
			orderTotals.close();

			// Get total spending per user
			System.out.println("\n=== Total Spending by User ===");
			ResultSet totalByUser = stmt.executeQuery(
					"select u.username, count(o.orderId) as totalOrders, sum(p.price * o.quantity) as totalSpent, avg(p.price) as avgPrice "
							+ "from orders o " + "inner join products p on o.productId = p.productId "
							+ "inner join users u on o.username = u.username " + "group by u.username");

			while (totalByUser.next()) {
				String username1 = totalByUser.getString("username");
				int totalOrders = totalByUser.getInt("totalOrders");
				int totalSpent = totalByUser.getInt("totalSpent");
				double avgPrice = totalByUser.getDouble("avgPrice");

				System.out.println("User: " + username1 + " | Orders: " + totalOrders + " | Total Spent: ₹" + totalSpent
						+ " | Avg Price: ₹" + avgPrice);
			}
			totalByUser.close();

			// fetch affordable products
			System.out.println("\n=== Affordable Products (₹500 - ₹2000) ===");
			ResultSet affordableProducts = stmt
					.executeQuery("select * from products where price between 500 and 2000 order by price");

			while (affordableProducts.next()) {
				int productId = affordableProducts.getInt("productId");
				String productName = affordableProducts.getString("productName");
				int price = affordableProducts.getInt("price");

				System.out.println(productId + ". " + productName + " - ₹" + price);
			}
			affordableProducts.close();

			/*
			 * // DELETE orders first (foreign key dependency) int deletedOrders =
			 * stmt.executeUpdate("delete from orders where username = 'shreeram'");
			 * System.out.println("Deleted " + deletedOrders + " orders");
			 * 
			 * // Then DELETE user int deletedUser =
			 * stmt.executeUpdate("delete from users where username = 'shreeram'");
			 * System.out.println("Deleted " + deletedUser + " user");
			 * 
			 * // Verify deletion ResultSet remainingUsers =
			 * stmt.executeQuery("select * from users");
			 * System.out.println("\n=== Remaining Users ==="); while
			 * (remainingUsers.next()) {
			 * System.out.println(remainingUsers.getString("username")); }
			 * remainingUsers.close();
			 */

			// Close resources
			stmt.close();
			conn.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}