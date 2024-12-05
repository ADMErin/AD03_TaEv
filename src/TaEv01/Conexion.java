package TaEv01;

import java.sql.*;

public class Conexion {
	
public static void main(String[] args) {
		
		String baseDatos = "dbeventos";
		String host = "localhost";
		String port = "3306";
		String parAdic = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		String urlConexion = "jdbc:mysql://" + host + ":" + port + "/" + baseDatos + parAdic;
		String user = "dbeventos";
		String pwd = "dbeventos";
		
		Connection conexion = null;

		try {
			conexion = DriverManager.getConnection(urlConexion, user, pwd);
			System.out.println("Conexion realizada");
			
		}catch (SQLException e) {
			System.out.println("Mensaje " + e.getMessage());
			System.out.println("Estado " + e.getSQLState());
			System.out.println("Codigo especifico " + e.getErrorCode());
		}catch (Exception e) {
			e.printStackTrace(System.err);
		}finally {
			try {
				if(conexion != null) conexion.close();
			} catch(Exception ex) {}
		}
	}
}
