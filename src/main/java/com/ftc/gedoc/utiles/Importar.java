package com.ftc.gedoc.utiles;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ftc.aq.Archivo;
import com.ftc.aq.Conexion;
import com.ftc.modelo.PersonaContacto;

public class Importar {

	public Importar() {
		
	}
	
	public static List<PersonaContacto> importar(String archivo)throws IOException{
		List<PersonaContacto> personas = new ArrayList<PersonaContacto>();
		try{
			List<String> lineas = Archivo.lineasArchivo(archivo);
			int x= 0;
			for (String linea:lineas){
				x++;
				if (linea.contains(",")){
					String[] campos = linea.split("\\,");
					if(campos.length==10){
						//Asignar los valores:
						PersonaContacto p = new PersonaContacto();
						p.setRazonSocial(campos[0]);
						p.setRfc(campos[1]);
						p.setNombre(campos[2]);
						p.setApellidos(campos[3]);
						p.setCorreo(campos[4]);
						p.setTelefonoLocal(campos[5]);
						p.setTelefonoMovil(campos[6]);
						p.setGrupo(campos[7]);
						p.setContrasenia(campos[8]);
						p.setTipo(campos[9]);
						personas.add(p);
					}else{
						System.out.printf("El registro %d es incorrecto, no corresponden las columnas, se esperaban 9 se encontraron %d%n", x, campos.length);
					}
				}
			}
			System.out.printf("Se preparan %d registros para almacenarse en la base de datos.", personas.size());
			int y = 0;
			for(PersonaContacto persona:personas){
				y+=almacenaRegistro(persona);
			}
			System.out.printf("Se almacenaron %d registros en la base de datos.", y);
			return personas;
		}catch(Exception e){
			throw new IOException(e);
		}
	}

	private static Integer almacenaRegistro(PersonaContacto persona) throws Exception{
		Connection conexion = null;
		int resultado=-1;
		try{
			//if(Conexion.validaUsuarioMySql("localhost", "ctrldoce", "sysadmindoce", "Sv6lOu/Vs")){
				conexion = Conexion.getConexion();
				if(conexion!=null){
					String identificador;
					String sql = "SELECT getCodigoApp('PR');";
					PreparedStatement stm = conexion.prepareStatement(sql);
					ResultSet rst = stm.executeQuery();
					if(rst!=null && rst.next()){
						identificador = rst.getString(1);
						sql = "INSERT INTO jpem00t "
								+ "(cdperson,dsrazsoc,dsrfc,dsfolder,dslogo,isowner,intipprs,cdusuari,programa,tmstmp) "
								+ "VALUE"
								+ "(?,?,?,?,'','N',?,getUser(),'CARGA_MASIVA',CURRENT_TIMESTAMP);";
						stm = conexion.prepareStatement(sql);
						stm.setString(1, identificador);
						stm.setString(2, persona.getRazonSocial());
						stm.setString(3, persona.getRfc());
						stm.setString(4, identificador);
						stm.setString(5, persona.getTipo());
						resultado = stm.executeUpdate();
						if(resultado>0){
							String referencia;
							sql = "SELECT getCodigoApp('CT');";
							stm = conexion.prepareStatement(sql);
							rst = stm.executeQuery();
							if(rst!=null && rst.next()){
								referencia = rst.getString(1);																												
								sql = "INSERT INTO jpem10t" +
					            "(cdperson,cdcontac,dsfirst,dslast,dsmail,dstelloc,dstelmov,cdusuari,programa,tmstmp,dsipfrom)" +
					            " VALUES " +
					            " (?,?,?,?,?,?,?,getUser(),'CARGA_MASIVA',CURRENT_TIMESTAMP,'0.0.0.0')";
								stm = conexion.prepareStatement(sql);
								stm.setString(1, identificador);
								stm.setString(2, referencia);
								stm.setString(3, persona.getNombre());
								stm.setString(4, persona.getApellidos());
								stm.setString(5, persona.getCorreo());
								stm.setString(6, persona.getTelefonoLocal());
								stm.setString(7, persona.getTelefonoMovil());
								resultado = stm.executeUpdate();
								if(resultado>0){
									sql = "INSERT INTO jusm01t " +
									"(cdperson,cdcontac,cdidegrp,cdusulog,dsvalenc,instatus,inusumod,dsipfrom,cdusuari,programa,tmstmp)" +
					            	" VALUES " +
					            	"(?,?,?,?,CONCAT(MD5(CONCAT(?,CURRENT_TIMESTAMP)),MD5(?)),'A',0,'0.0.0.0',getUser(),'CARGA_MASIVA',CURRENT_TIMESTAMP);";
					            	stm = conexion.prepareStatement(sql);
					            	stm.setString(1, identificador);
					            	stm.setString(2, referencia);
					            	stm.setString(3, persona.getGrupo());
					            	stm.setString(4, persona.getCorreo());
					            	stm.setString(5, referencia);
					            	stm.setString(6, persona.getContrasenia());
					            	resultado = stm.executeUpdate();
								}
							}else{
								System.out.printf("No se logro obtener un identificador para el cliente.");								
							}
						}else{
							System.out.printf("No se logro registrar al proveedor.");
						}
					}else{
						System.out.printf("No se logro obtener un identificador para el proveedor.");
					}
				}
			//}else{
			//	System.out.printf("No se logro conectar con la base de datos.");
			//}
			return resultado;
		}catch(SQLException e){
			System.out.printf("Hubo una excepcion al registrar el valor Error[%d] = %s:%s", e.getErrorCode(), e.getSQLState(), e.getMessage());
			throw new Exception(e);
		}finally{
			if(conexion!=null && !conexion.isClosed()){
				conexion.close();
			}
		}		
	}
	
}
