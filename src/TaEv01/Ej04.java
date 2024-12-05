package TaEv01;

import java.sql.*;
import java.util.Scanner;

public class Ej04 {
	
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
		
		try {

			conexion = DriverManager.getConnection(urlConexion, user, pwd);
			listaEventos(conexion);
			obtenerAsistentes(conexion, teclado);
			
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

	public static void listaEventos(Connection conexion) {
		System.out.println("Lista de eventos: ");
		String listadoConsulta = "Select id_evento as id, nombre_evento as evento from eventos;";
		try {
			Statement consulta = conexion.createStatement();
			ResultSet rs = consulta.executeQuery(listadoConsulta);
			while (rs.next()) {
				int id = rs.getInt("id");
				String evento = rs.getString("evento");
				System.out.println(id + ". " + evento );
			}
		} catch(SQLException e) {
			System.err.println("Error " + e.getMessage());
		}
		System.out.println();
	}
	
	public static void obtenerAsistentes(Connection conexion, Scanner teclado) {
		
		System.out.println("Introduce el ID del evento para consultar la cantidad de asistentes: ");
		int id = teclado.nextInt();
		
		try {
			PreparedStatement call = conexion.prepareStatement("select obtener_numero_asistentes(?)");
			call.setInt(1, id);
			ResultSet total = call.executeQuery();
			if (total.next()) {
				int asistentes = total.getInt(1);
				System.out.println("El número de asistentes para el evento seleccionado es: " + asistentes);
			}
		}catch(SQLException e) {
			System.err.println("Error " + e.getMessage());
		}
	}
}
