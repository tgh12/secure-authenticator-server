import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/enroll")
public class EnrollHandler extends HttpServlet {
	
	private Connection connect = null;
	private PreparedStatement findUsername = null, storeSeed = null;
	private ResultSet resultSet = null;
  
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
    		throws IOException {
    	try {               	    	
    		System.out.println("Enroll Request Received");  
		  
    		Enumeration<?> keys = request.getParameterNames();
	    	
    		String username = "", password = "";
	        
    		for(; keys.hasMoreElements();) {
	        	String key = (String) keys.nextElement();
	        	
	        	if(key.equals("enrollUsername")) {
	        		username = request.getParameterValues(key)[0];
	        	} else if(key.equals("enrollPassword")) {
	        		password = request.getParameterValues(key)[0];
	        	}
    		}
	        
	        Class.forName("org.gjt.mm.mysql.Driver");
        	connect = DriverManager.getConnection("jdbc:mysql://localhost/prototype", "root", "");
        	
        	//Check for existing account
        	findUsername = connect.prepareStatement("select * from PROTOTYPE.ACCOUNT where USERNAME=?");
        	findUsername.setString(1, username);
            
        	resultSet = findUsername.executeQuery();
        	
        	//If the query returns a username, verify
        	if(resultSet.next()) {        		
        		if(username.equals(resultSet.getString(2))) {        			
        			if(BCrypt.checkpw(password, resultSet.getString(3))) {
        				//Generate TOTP Seed
        				String seed;
        				SecureRandom random = new SecureRandom();
        				seed = new BigInteger(160,random).toString(32);
        				
        				//Store seed in account
        				storeSeed = connect.prepareStatement("update PROTOTYPE.ACCOUNT set OTPSEED=? where USERNAME=?");
        				storeSeed.setString(1, seed);
        				storeSeed.setString(2, username);
        				
        				storeSeed.executeUpdate();
        				
        				response.setContentType("text/plain");
        		        PrintWriter out = response.getWriter();
        		        
        		        //Build response String
        		        String message = String.format("error:false|seed:%s|",seed);
        		        
        		        out.println(message);
        			} else { //Incorrect Password
        				response.setContentType("text/plain");
        		        PrintWriter out = response.getWriter();
        		        
        		        //Build response String
        		        String message = String.format("error:Incorrect Password|seed:null");
        		        
        		        out.println(message);
        			}
        		} else { //Just in case it wasn't caught the first time
        			response.setContentType("text/plain");
    		        PrintWriter out = response.getWriter();
    		        
    		        //Build response String
    		        String message = String.format("error:Incorrect Username|seed:null");
    		        
    		        out.println(message);
        		}
        	} else { ////Incorrect Username
        		response.setContentType("text/plain");
		        PrintWriter out = response.getWriter();
		        
		        //Build response String
		        String message = String.format("error:Incorrect Username|seed:null");
		        
		        out.println(message);
        	}
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	        
	        // invalid request
	        response.getWriter().println("ERROR:Error processing request: "+e.getMessage());
	    }
  }
  
  public String appendZeros(String s) {
	  if(s.length() < 6) {
		  return(appendZeros("0".concat(s)));
	  } else {
		  return s;
	  }
  }
}
