package com.momotiff.com;

import java.sql.*;
import java.sql.DriverManager;

public class labfour {
	public static void main(String[] args) {

		Connection connection = null;

		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/lab4", "postgres", "M123456f");
			if (connection != null) {
				System.out.println("Connection OK");
			} else {
				System.out.println("Connection Failed");
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}
}
