package TaEv01;

import java.sql.*;

public class Ej01 {
	
	/* Método main donde se hace la conexión a la BBDD,
	 * Se realiza la consulta a la BBDD
	 * Se recoge el resultado en un ResulSet
	 * Se procesa el resultado y se muestra por pantalla
	 */
	public static void main(String[] args) {
		//variables propiedades de la conexión
		String baseDatos = "dbeventos";
		String host = "localhost";
		String port = "3306";
		String parAdic = "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
		String urlConexion = "jdbc:mysql://" + host + ":" + port + "/" + baseDatos + parAdic;
		String user = "dbeventos";
		String pwd = "dbeventos";
		
		
		Connection conexion = null;
		Statement consulta = null;
		ResultSet rs = null;
		
		try {

			conexion = DriverManager.getConnection(urlConexion, user, pwd);
			consulta = conexion.createStatement();
			rs = consulta.executeQuery("SELECT eventos.nombre_evento as Evento, (SELECT count(asistentes_eventos.id_evento) "
					+ "FROM asistentes_eventos where asistentes_eventos.id_evento = eventos.id_evento ) as Asistentes,\r\n"
					+ "  ubicaciones.nombre as Ubicación, ubicaciones.direccion as Dirección\r\n"
					+ "	FROM eventos inner join ubicaciones on eventos.id_ubicacion=ubicaciones.id_ubicacion\r\n"
					+ "		inner join asistentes_eventos on eventos.id_evento= asistentes_eventos.id_evento \r\n"
					+ "			group by eventos.id_evento order by  eventos.nombre_evento desc;");
			
			//Cabecera para mostrar por pantalla con formato
			String cabecera = toString("Evento", "Asistentes", "Ubicación", "Dirección");
			System.out.println(cabecera);
			System.out.println("--------------------------------------------------------------------------------------------------------------");
			
			while (rs.next()) {
				String evento = rs.getString("Evento");
				String asistentes = rs.getString("Asistentes");
				String ubicacion = rs.getString("Ubicación");
				String direccion = rs.getString("Dirección");
				
				String imprimir = toString(evento, asistentes, ubicacion, direccion);
				System.out.println(imprimir);
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
				if(rs != null)rs.close();
			} catch(Exception ex) {}
		}
	}
	
	// Método para dar formato a la salida
	public static String toString(String evento, String asistentes, String ubicacion, String direccion) {
		return String.format(
				"%-30s | %-12s | %-32s | %-32s",
				evento, asistentes, ubicacion, direccion);
	}
}



