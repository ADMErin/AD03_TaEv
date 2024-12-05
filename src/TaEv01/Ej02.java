package TaEv01;

import java.sql.*;
import java.util.*;

public class Ej02 {
	
	/* Método main donde se hace la conexión a la BBDD,
	 * Se piden datos por teclado
	 * Se realiza una consulta preparada a la BBDD
	 * Se recoge el resultado en un ResulSet, se muestra y se pide el nuevo dato
	 * Se realiza el update de la BBDD
	 * Se gestionan las excepciones
	 * Se cierra la conexion, las consultas, los resultados y el scanner
	 */

	public static void main(String[] args) {
		
		String baseDatos = "dbeventos";
		String host = "localhost";
		String port = "3306";
		String parAdic = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		String urlConexion = "jdbc:mysql://" + host + ":" + port + "/" + baseDatos + parAdic;
		String user = "dbeventos";
		String pwd = "dbeventos";
		
		Connection conexion = null;
		
		Scanner teclado = new Scanner(System.in);
		System.out.print("Introduce el nombre de la ubicación: ");
		String ubicacion = teclado.nextLine();
		
		PreparedStatement consulta = null;
		ResultSet rs = null;
		int aforo;
		int nuevoAforo;

		try {

			conexion = DriverManager.getConnection(urlConexion, user, pwd);
			consulta = conexion.prepareStatement("Select capacidad from ubicaciones where nombre = ?");
			consulta.setString(1, ubicacion);
			rs = consulta.executeQuery();
			
			if(rs.next()) {
				aforo = rs.getInt("capacidad");
				System.out.println("La capacidad actual de la ubucacion '" + ubicacion + "' es: " + aforo);
				
				//se cierra la consulta y se reutiliza
				consulta.close();
				
				System.out.print("Introduce la nueva capacidad máxima: ");
				nuevoAforo = teclado.nextInt();
				
				consulta = conexion.prepareStatement("update ubicaciones set capacidad = ? where nombre = ?;");
				consulta.setInt(1, nuevoAforo);
				consulta.setString(2, ubicacion);
				int filas = consulta.executeUpdate();
				
				//Se comprueba que se ha actualizado el registro
				if(filas == 1) {
					System.out.println("Capacidad actualizada correctamente");
				} else {
					System.out.println("No se ha actualizado el aforo");
				}
			} else {
				System.out.print("Ubicacion no encontrada");
			}
		}catch (SQLException e) {
			System.out.println("Mensaje " + e.getMessage());
			System.out.println("Estado " + e.getSQLState());
			System.out.println("Codigo especifico " + e.getErrorCode());
		}catch (Exception e) {
			e.printStackTrace(System.err);
		}finally {
			try {
				if(conexion != null) conexion.close();
				if(consulta != null) consulta.close();
				if(rs != null) rs.close();
			} catch(Exception ex) {}
		}
		teclado.close();
	}

}
