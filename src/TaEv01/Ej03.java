package TaEv01;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Ej03 {
	
public static void main(String[] args) {
		
		String baseDatos = "dbeventos";
		String host = "localhost";
		String port = "3306";
		String parAdic = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		String urlConexion = "jdbc:mysql://" + host + ":" + port + "/" + baseDatos + parAdic;
		String user = "dbeventos";
		String pwd = "dbeventos";
		
		Scanner teclado = new Scanner(System.in);
		
		String dni = pedirDni(teclado);
		
		Connection conexion = null;
		
		try {

			conexion = DriverManager.getConnection(urlConexion, user, pwd);
			String nuevoAsistente = buscarAsistente(conexion, dni, teclado);
			listaEventos(conexion);
			eleccionEvento(conexion, teclado, nuevoAsistente);
			
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
		teclado.close();
	}

	public static String pedirDni(Scanner teclado) {                    
		 String dni = "";
		 Pattern formatoUsuario = Pattern.compile("[0-9]{8}[a-zA-Z]");
		 Matcher comparaFormato = formatoUsuario.matcher(dni);
		    
		 do {
		     System.out.println("Introduce el DNI del asistente: ");
		     dni = teclado.nextLine();
		     comparaFormato = formatoUsuario.matcher(dni);
		 } while (!comparaFormato.matches());
		    
		 return dni;
	}
	
	public static String buscarAsistente(Connection conexion, String dni, Scanner teclado) {
		String consultaUsuario = "Select nombre from asistentes where dni = ?";
		String nuevoUsuario = "";
		try {
			PreparedStatement pst= conexion.prepareStatement(consultaUsuario);
			pst.setString(1, dni);
			ResultSet rsUsuario = pst.executeQuery();
			
			if(!rsUsuario.next()) {
				System.out.println("No se encontró un asistente con el DNI proporcionado.");
				System.out.println("Introduce el nombre del nuevo asistente: ");
				nuevoUsuario = teclado.nextLine();
				
				if (nuevoUsuario != null) {
					String insertUsuario = "Insert into asistentes values(?, ?)";
					
					PreparedStatement inUsuario= conexion.prepareStatement(insertUsuario);
					inUsuario.setString(1, dni);
					inUsuario.setString(2, nuevoUsuario);
					inUsuario.executeUpdate();
				}
			} else {
				nuevoUsuario = rsUsuario.getString("nombre");
				System.out.println("Estas realizando la reserva para: " + nuevoUsuario);
			}
		} catch(SQLException e) {
			System.err.println("Error " + e.getMessage());
		}
		return nuevoUsuario;
	}
	
	public static void listaEventos(Connection conexion) {
		System.out.println("Lista de eventos: ");
		String listadoConsulta = "SELECT asistentes_eventos.id_evento as id, eventos.nombre_evento as nombre, ubicaciones.capacidad - count(asistentes_eventos.id_evento) as plazas \r\n"
				+ "FROM asistentes_eventos inner join eventos on eventos.id_evento = asistentes_eventos.id_evento \r\n"
				+ "inner join ubicaciones on ubicaciones.id_ubicacion = eventos.id_ubicacion\r\n"
				+ "group by eventos.id_evento;";
		try {
			Statement consulta = conexion.createStatement();
			ResultSet rs = consulta.executeQuery(listadoConsulta);
			while (rs.next()) {
				int id = rs.getInt("id");
				String evento = rs.getString("nombre");
				int plazas = rs.getInt("plazas");
				System.out.println(id + ". " + evento + " - Espacios disponibles: " + plazas);
				
			}
			
		} catch(SQLException e) {
			System.err.println("Error " + e.getMessage());
		}
	}
	
	public static void eleccionEvento(Connection conexion, Scanner teclado, String nuevoAsistente) {
		System.out.println("Elige el numero de evento al que quiere asistir: ");
		int id_evento = teclado.nextInt();
		
		String inUsuarioEvento = "Insert into asistentes_eventos values ((select dni from asistentes where nombre = ?), ?) ;";
		
		String plazasEvento = "SELECT ubicaciones.capacidad - count(asistentes_eventos.id_evento) as plazas \r\n"
				+ "FROM asistentes_eventos inner join eventos on eventos.id_evento = asistentes_eventos.id_evento \r\n"
				+ "inner join ubicaciones on ubicaciones.id_ubicacion = eventos.id_ubicacion where eventos.id_evento = ?\r\n"
				+ "group by eventos.id_evento;";
		int plazas = 0;

		try {
			PreparedStatement numPlazas = conexion.prepareStatement(plazasEvento);
			numPlazas.setInt(1, id_evento);
			ResultSet rs = numPlazas.executeQuery();
			
			if(rs.next()) {
				plazas = rs.getInt("plazas");
			}

			if(plazas != 0) {
				PreparedStatement pst= conexion.prepareStatement(inUsuarioEvento);
				pst.setString(1, nuevoAsistente);
				pst.setInt(2, id_evento);
				pst.executeUpdate();
				System.out.println(nuevoAsistente + " ha sido registrado para el evento seleccionado");
				
			} else {
				System.out.println("No hay plazas disponibles para ese evento");
			}
		} catch(SQLException e) {
			System.out.println("El asistente ya figura en el registro de ese evento.");
			//System.err.println("Error " + e.getMessage());
		}
	}
}
