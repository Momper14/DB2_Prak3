package jdbc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {

		Class.forName("oracle.jdbc.driver.OracleDriver");
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@schelling.nt.fh-koeln.de:1521:xe", "dbprak13",
				"dbprak13");

		Statement sm = con.createStatement();
		ResultSet rs = sm.executeQuery("select * from artikel");
		StringBuilder sb = new StringBuilder();
		while (rs.next()) {
			if (sb.length() > 0){
				sb.append("\n");
			}
			sb.append(rs.getInt("artnr")).append(";");
			sb.append(rs.getString("artbez")).append(";");
			sb.append(rs.getString("mge")).append(";");
			sb.append(rs.getDouble("preis"));

			Statement smPos = con.createStatement();
			ResultSet rsPos = smPos.executeQuery("select * from bpos where artnr = " + rs.getInt("artnr"));
			while (rsPos.next()) {
				sb.append(";").append(rsPos.getInt("bstnr"));
				sb.append(";").append(rsPos.getInt("menge"));
			}
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter("ARTIKEL.csv"));
		writer.write(sb.toString());

		writer.close();
		System.out.println("Fertig");
	}

}
