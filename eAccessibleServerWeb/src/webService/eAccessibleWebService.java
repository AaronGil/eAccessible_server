package webService;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.naming.InitialContext;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import pojo.Local;
import pojo.TipoLocal;
import pojo.Accessibilitat;
import pojo.Caracteristica;
import pojo.CaracteristicaTipoLocal;
import pojo.InformacioAccessibilitatLocal;


@WebService
public class eAccessibleWebService {
 
	@WebMethod
	public void altaLocal(Local local, List<Accessibilitat> accessibilitat) {
		
		System.out.println("Abans COMPROVACIONS local");
		comprovacionsAltaLocal(local, accessibilitat);
		
		Connection connection = null;
       
        String strStatus = new String();
      
        try{
            InitialContext cxt = new InitialContext();
            if ( cxt != null ){
                DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");
                if ( ds == null ) strStatus = "Error while obtaining the dataSource";
                else{
                    connection = ds.getConnection();
                  
					String query = "insert into eaccessible.local (codilocal,coditipolocal,codicarrer,nomcarrer,nomvia,numero,nomlocal,observacions,verificat) values('"+local.getCodiLocal()+"','"+local.getCodiTipoLocal()+"','"+local.getCodiCarrer()+"','"+local.getNomCarrer()+"','"+local.getNomVia()+"','"+local.getNumero()+"',UPPER('"+local.getNomLocal()+"'),'"+local.getObservacions()+"','"+local.getVerificat()+"')";
                    Statement stm = connection.createStatement();
                    stm.executeUpdate(query);
                    
                  
                    for(int i = 0; i < accessibilitat.size(); i++) {                     
                    	stm.executeUpdate("insert into eaccessible.accessibilitat (codiaccessibilitat,codilocal,codicaracteristica,valor,verificat) values('"+accessibilitat.get(i).getCodiAccessibilitat()+"','"+local.getCodiLocal()+"','"+accessibilitat.get(i).getCodiCaracteristica()+"','"+accessibilitat.get(i).getValor()+"','"+accessibilitat.get(i).getVerificat()+"')");
                    }
                    connection.close();
                    stm.close();
                   
                }
            }
        }  catch (Exception e) {
           
            e.printStackTrace();
            
        } finally {
        	try {
        		connection.close();
        		
        	} catch(Exception e) {
        		e.printStackTrace();
        		System.out.println("Can't close connection to Database");
        	}
        }
		
	}

	private void comprovacionsAltaLocal(Local local, List<Accessibilitat> accessibilitat) {
		
		if (local.getCodiTipoLocal() <= 0) {
			throw new IllegalArgumentException();
		} 

		if (local.getCodiCarrer() <= 0) {
			throw new IllegalArgumentException();
		}

		if (local.getNumero() <= 0) {
			throw new IllegalArgumentException();
		}

		if (local.getNomCarrer().isEmpty()) {
			throw new IllegalArgumentException();
		}

		if (local.getNomVia().isEmpty()) {
			throw new IllegalArgumentException();
		}

		if (local.getNomLocal().isEmpty()) {
			throw new IllegalArgumentException();
		}
		
		//Iterem per totes les caracteristiques d'accesibilitat
		for(int i = 0; i < accessibilitat.size(); i++) {
			
			if(accessibilitat.get(i).getCodiCaracteristica() <= 0) {
				throw new IllegalArgumentException();
			}
			
			if(accessibilitat.get(i).getValor() < 0) {
				throw new IllegalArgumentException();
			}
			
			if(accessibilitat.get(i).getVerificat().isEmpty()) {
				throw new IllegalArgumentException();
			}
		}
	}
	
	public void validacioLocal(int codiLocal) {
		
		Connection connection = null;
	       
        String strStatus = new String();
        
        try {
        	InitialContext cxt = new InitialContext();
            if ( cxt != null ){
                DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");
                if ( ds == null ) strStatus = "Error while obtaining the dataSource";
                else{
                	
                }
                   
                    connection = ds.getConnection();
                    String query = "update eaccessible.local set verificat='Y' where codilocal="+codiLocal;
                    Statement stm = connection.createStatement();
                    stm.executeUpdate(query);
                  
                    connection.close();
                    stm.close();
                }
            
        } catch(Exception e) {
        	e.printStackTrace();
        	System.out.println("No connection to Database");
        } finally {
        	try {
        		connection.close();
        		
        	} catch(Exception e) {
        		e.printStackTrace();
        		System.out.println("Can't close connection to Database");
        	}
        }
		
	}
	
	@WebMethod
    public void baixaLocal(int codiLocal) {

        Connection connection = null;

        String strStatus = new String();

        try{
            InitialContext cxt = new InitialContext();
            if ( cxt != null ){
                DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");
                if ( ds == null ) strStatus = "Error while obtaining the dataSource";
                else{

                    connection = ds.getConnection();
                    String query = "delete from eaccessible.accessibilitat where codiLocal="+codiLocal;
                    Statement stm = connection.createStatement();
                    stm.executeUpdate(query);

                    String query2 = "delete from eaccessible.local where codiLocal="+codiLocal;
                    stm.executeUpdate(query2);

                    connection.close();
                    stm.close();
                }
            }
        }  catch (Exception e) {

            e.printStackTrace();

        } finally {
            try {
                connection.close();

            } catch(Exception e) {
                System.out.println("Can't close connection to Database");
            }
        }

    }
	
	@WebMethod
	    public List<Local> cercaLocalPerTipoLocal(int codiTipoLocal) {
	 

	        comprovacionsCercaTipoLocal(codiTipoLocal);
	        
	        Connection connection = null;
	       
	        String strStatus = new String();
	        List<Local> llistaLocals= new ArrayList<Local>();
	      
	        try{
	            InitialContext cxt = new InitialContext();
	            if ( cxt != null ){
	                DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");
	                if ( ds == null ) strStatus = "Error while obtaining the dataSource";
	                else{
	                    connection = ds.getConnection();
	                    
	                    String query = "select coditipolocal,codicarrer,nomcarrer,nomvia,codilocal,nomlocal,numero,observacions,verificat from eAccessible.local where coditipolocal="+codiTipoLocal;
	                    Statement stm = connection.createStatement();
	                    ResultSet rs = stm.executeQuery(query);
	                    if(rs.next()) {
	                    	while(rs.next()) {
	                    		Local local = new Local();
	                    		local.setCodiTipoLocal(rs.getInt("coditipolocal"));
	                    		local.setCodiCarrer(rs.getInt("codicarrer"));
	                    		local.setNomCarrer(rs.getString("nomcarrer"));
	                    		local.setNomVia(rs.getString("nomvia"));
	                    		local.setCodiLocal(rs.getInt("codilocal"));
	                    		local.setNomLocal(rs.getString("nomlocal"));
	                    		local.setNumero(rs.getInt("numero"));
	                    		local.setObservacions(rs.getString("observacions"));
	                    		local.setVerificat(rs.getString("verificat"));
	                    		llistaLocals.add(local);
	                    	} 
	                    } else {
	                    	System.out.println("Error. No hi ha dades disponibles");
	                    }
	                    
	  
	                    connection.close();
	                    stm.close();
	                }
	            }
	        }  catch (Exception e) {
	           
	            e.printStackTrace();
	            
	        } finally {
	            try {
	                connection.close();
	                
	            } catch(Exception e) {
	                e.printStackTrace();
	                System.out.println("Can't close connection to Database");
	            }
	        }
	        return llistaLocals;
	        
	    }
	 
	    private void comprovacionsCercaTipoLocal(int codiTipoLocal) {
	        
	        if (codiTipoLocal <= 0) {
	            throw new IllegalArgumentException();
	        } 

	    }
	    
	    @WebMethod
	    public List<Local> cercaLocalPerTipoLocalNomLocalNumero(int codiTipoLocal, String nomLocal, int numero) {
	 

	        comprovacionsCercaTipoLocalNomLocalNumero(codiTipoLocal, nomLocal, numero);
	        
	        Connection connection = null;
	       
	        String strStatus = new String();
	        List<Local> llistaLocals= new ArrayList<Local>();
	      
	        try{
	            InitialContext cxt = new InitialContext();
	            if ( cxt != null ){
	                DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");
	                if ( ds == null ) strStatus = "Error while obtaining the dataSource";
	                else{
	                    connection = ds.getConnection();
	                    
	                    String query = "select coditipolocal,codicarrer,nomcarrer,nomvia,codilocal,nomlocal,numero,observacions,verificat from eaccessible.local where coditipolocal="+codiTipoLocal+" AND nomlocal LIKE UPPER('%"+nomLocal+"%') AND numero="+numero;
	                    Statement stm = connection.createStatement();
	                    ResultSet rs = stm.executeQuery(query);
	                    System.out.println("query=" +query);
	                    
	                    	while(rs.next()) {
	                    		Local local = new Local();
	                    		local.setCodiTipoLocal(rs.getInt("coditipolocal"));
	                    		local.setCodiCarrer(rs.getInt("codicarrer"));
	                    		local.setNomCarrer(rs.getString("nomcarrer"));
	                    		local.setNomVia(rs.getString("nomvia"));
	                    		local.setCodiLocal(rs.getInt("codilocal"));
	                    		local.setNomLocal(rs.getString("nomlocal"));
	                    		local.setNumero(rs.getInt("numero"));
	                    		local.setObservacions(rs.getString("observacions"));
	                    		local.setVerificat(rs.getString("verificat"));
	                    		llistaLocals.add(local);
	                    		
	                    	}
	                    	//COMPROVAR AMB EL SIZE SI ESTA BUIT
	                    
	  
	                    connection.close();
	                    stm.close();
	                }
	            }
	        }  catch (Exception e) {
	           
	            e.printStackTrace();
	            
	        } finally {
	            try {
	                connection.close();
	                
	            } catch(Exception e) {
	                e.printStackTrace();
	                System.out.println("Can't close connection to Database");
	            }
	        }
	        return llistaLocals;
	        
	    }
	 
	    private void comprovacionsCercaTipoLocalNomLocalNumero(int codiTipoLocal, String nomLocal, int numero) {
	        
	        if (codiTipoLocal <= 0) {
	            throw new IllegalArgumentException();
	        }
	        if (nomLocal.isEmpty()) {
	            throw new IllegalArgumentException();
	        } 
	        if (numero <= 0) {
	            throw new IllegalArgumentException();
	        } 
	 
	    }
	    
	 
	    @WebMethod
	    public List<CaracteristicaTipoLocal> informacioCaracteristicaTipoLocal (int codiTipoLocal) {
	 
	        
	        Connection connection = null;
	       
	        String strStatus = new String();
	        List<CaracteristicaTipoLocal> llistaCaracteristicaTipoLocal= new ArrayList<CaracteristicaTipoLocal>();
	      
	        try{
	            InitialContext cxt = new InitialContext();
	            if ( cxt != null ){
	                DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");
	                if ( ds == null ) strStatus = "Error while obtaining the dataSource";
	                else{
	                    connection = ds.getConnection();
	                    
	                    String query = "select codicaracteristicatipolocal, codicaracteristica, coditipolocal from eaccessible.caracteristicatipolocal where coditipolocal="+codiTipoLocal;
	                    Statement stm = connection.createStatement();
	                    ResultSet rs = stm.executeQuery(query);
	                    if(rs.next()) {
	                    	while(rs.next()) {
	                    		CaracteristicaTipoLocal caracteristicaTipoLocal = new CaracteristicaTipoLocal();
	                    		caracteristicaTipoLocal.setCodiCaracteristicaTipoLocal(rs.getInt("codicaracteristicatipolocal"));
	                    		caracteristicaTipoLocal.setCodiCaracteristica(rs.getInt("codicaracteristica"));
	                    		caracteristicaTipoLocal.setCodiTipoLocal(rs.getInt("coditipolocal"));
	                    		llistaCaracteristicaTipoLocal.add(caracteristicaTipoLocal);
	                    		
	                    	}
	                    } else {
	                    	System.out.println("Error. No hi ha dades disponibles");
	                    }
	                    
	                    connection.close();
	                    stm.close();
	                }
	            }
	        }  catch (Exception e) {
	           
	            e.printStackTrace();
	            
	        } finally {
	            try {
	                connection.close();
	                
	            } catch(Exception e) {
	                e.printStackTrace();
	                System.out.println("Can't close connection to Database");
	            }
	        }
	        return llistaCaracteristicaTipoLocal;
	        
	    }

	    
	    @WebMethod
	    public Caracteristica informacioCaracteristica(int codiCaracteristica) {

	       Connection connection = null;
	       String strStatus = new String();
	       Caracteristica caracteristica = new Caracteristica();


	       try{


	           InitialContext cxt = new InitialContext();

	           if ( cxt != null ){

	                DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");

	                if ( ds == null ) strStatus = "Error while obtaining the dataSource";

	               else{

	                   connection = ds.getConnection();

	                   String query = "select codicaracteristica, nomcaracteristicaca, nomcaracteristicaes, nomcaracteristicaen, tipo, codinivell  from eaccessible.caracteristica where caracteristica.codicaracteristica="+codiCaracteristica;
	 
	                   Statement stm = connection.createStatement();

	                   ResultSet rs = stm.executeQuery(query);


	                   if(rs.next()) {

		                       caracteristica.setCodiCaracteristica(rs.getInt("codicaracteristica"));
		                       caracteristica.setNomCaracteristicaCA(rs.getString("nomcaracteristicaca"));
		                       caracteristica.setNomCaracteristicaES(rs.getString("nomcaracteristicaes"));
		                       caracteristica.setNomCaracteristicaEN(rs.getString("nomcaracteristicaen"));
		                       caracteristica.setTipo(rs.getInt("tipo"));
		                       caracteristica.setCodiNivell(rs.getInt("codinivell"));
		 
	                   } else {

	                    System.out.println("Error. No hi ha dades disponibles");

	                   }

	                   connection.close();

	                   stm.close();

	               }

	           }

	       }  catch (Exception e) {
 
	            e.printStackTrace();   

	       } finally {

	           try {

	               connection.close();

	           } catch(Exception e) {

	               e.printStackTrace();

	                System.out.println("Can't close connection to Database");

	           }
 

	       }
	       return caracteristica;
	     
	   }
	    
	    @WebMethod
	    public List<TipoLocal> llistarTipusLocals() {
	        
	       Connection connection = null;
	       String strStatus = new String();
	       
	       List<TipoLocal> tipoLocalList = new ArrayList<TipoLocal>();

	       try{

	           InitialContext cxt = new InitialContext();

	           if ( cxt != null ){

	                DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");

	                if ( ds == null ) strStatus = "Error while obtaining the dataSource";

	               else{

	                   connection = ds.getConnection();

	                   String query = "select coditipolocal, nomtipolocalca, nomtipolocales, nomtipolocalen from eaccessible.tipolocal";

	                   Statement stm = connection.createStatement();

	                   ResultSet rs = stm.executeQuery(query);

	                   while(rs.next()) {

	                       TipoLocal tipoLocal = new TipoLocal();

	                       tipoLocal.setCodiTipoLocal(rs.getInt("coditipolocal"));
	                       tipoLocal.setNomTipoLocalCA(rs.getString("nomtipolocalca"));
	                       tipoLocal.setNomTipoLocalES(rs.getString("nomtipolocales"));
	                       tipoLocal.setNomTipoLocalEN(rs.getString("nomtipolocalen"));
	                       tipoLocalList.add(tipoLocal);

	                   }

	                   connection.close();

	                   stm.close();

	               }

	           }

	       }  catch (Exception e) {
	 
	            e.printStackTrace();   

	       } finally {

	           try {

	               connection.close();

	           } catch(Exception e) {

	               e.printStackTrace();

	                System.out.println("Can't close connection to Database");

	           }

	       }
	        return tipoLocalList;

	   }
	   
	    @WebMethod
		public int ultimCodiLocal() {
	    	Connection connection = null;
		    String strStatus = new String();
			
			int ultimCodiLocal = 0;
			
			try{	
				InitialContext cxt = new InitialContext();
				if ( cxt != null ){
					DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");
					if ( ds == null ) strStatus = "Error while obtaining the dataSource";
					else{
						connection = ds.getConnection();
						
						String query = "select MAX(codilocal) codilocal from eaccessible.local";
						Statement stm = connection.createStatement();
						ResultSet rs = stm.executeQuery(query);
						
						rs.next();
						ultimCodiLocal = rs.getInt("codilocal");
					
						connection.close();
						stm.close();
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				
			} finally {

		           try {

		               connection.close();

		           } catch(Exception e) {

		               e.printStackTrace();

		                System.out.println("Can't close connection to Database");

		           }

		       }
			
			return ultimCodiLocal+1;
			
		}
	    
	    @WebMethod
	    public int  ultimCodiAccessibilitat(){
	    	Connection connection = null;
		    String strStatus = new String();
			
			
			int ultimCodiAccessibilitat = 0;
			
			try{	
				InitialContext cxt = new InitialContext();
				if ( cxt != null ){
					DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");
					if ( ds == null ) strStatus = "Error while obtaining the dataSource";
					else{
						connection = ds.getConnection();
						
						String query = "select MAX(codiAccessibilitat) codiAccessibilitat  from eaccessible.accessibilitat";
						Statement stm = connection.createStatement();
						ResultSet rs = stm.executeQuery(query);
						rs.next();
						ultimCodiAccessibilitat = rs.getInt("codiAccessibilitat");
					
						connection.close();
						stm.close();
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			} finally {

		           try {

		               connection.close();

		           } catch(Exception e) {

		               e.printStackTrace();

		                System.out.println("Can't close connection to Database");

		           }

		       }
			
			return ultimCodiAccessibilitat+1;
		}
	    
	    @WebMethod
	    public List<InformacioAccessibilitatLocal> llistarAccessibilitatLocal(int codiLocal) {

	    	 

	        Connection connection = null;

	    

	        String strStatus = new String();

	    

	        List<InformacioAccessibilitatLocal> llistaCaracteristicaValor = new ArrayList<InformacioAccessibilitatLocal>();

	        try{

	            InitialContext cxt = new InitialContext();

	            if ( cxt != null ){

	                   DataSource ds = (DataSource) cxt.lookup( "java:jboss/PostgresXA2");


	                   if ( ds == null ) strStatus = "Error while obtaining the dataSource";
	                
	                   else{
	                    connection = ds.getConnection();


	                     String query = "select accessibilitat.codilocal, accessibilitat.codicaracteristica codicaracteristica, caracteristica.nomcaracteristicaca nomcaracteristicaca,accessibilitat.valor valor from eaccessible.local INNER JOIN eaccessible.accessibilitat ON local.codilocal = accessibilitat.codilocal INNER JOIN eaccessible.caracteristica ON accessibilitat.codicaracteristica = caracteristica.codicaracteristica AND local.codilocal="+codiLocal;

	                    Statement stm = connection.createStatement();

	                    ResultSet rs = stm.executeQuery(query);

	                    while(rs.next()) {


	                    	InformacioAccessibilitatLocal caracteristicaValor = new InformacioAccessibilitatLocal();

	                    	caracteristicaValor.setCodiLocal(rs.getInt("codilocal"));
	                    	caracteristicaValor.setCodiCaracteristica(rs.getInt("codicaracteristica"));
	                        caracteristicaValor.setNomCaracteristicaCA(rs.getString("nomcaracteristicaca"));
	                        caracteristicaValor.setValor(rs.getInt("valor"));
	                        
	                         
	                           
	                        llistaCaracteristicaValor.add(caracteristicaValor);

	                    }

	                    connection.close();
	                    stm.close();
	                }

	           }

	        }  catch (Exception e) {
	              e.printStackTrace();   

	       } finally {

	            try {
	                connection.close();
	            } catch(Exception e) {
	                e.printStackTrace();
	                System.out.println("Can't close connection to Database");
	            }
	        }

	          return llistaCaracteristicaValor;

	    }
}
