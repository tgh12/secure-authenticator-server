import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/register")
public class RegisterHandler extends HttpServlet {
	
	private Connection connect = null;
	private PreparedStatement findUsername = null, storeAccount = null;
	private ResultSet resultSet = null;
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) 
  		throws IOException{
	  try
	    {               	    	
	        System.out.println("Register Request Received");
		  
		  	Enumeration<?> keys = request.getParameterNames();
	    	
	    	String username = "", passwordHash = "", confirmPasswordHash = "";
	        
	        String salt = BCrypt.gensalt();
	        
	        for(; keys.hasMoreElements();) {
	        	String key = (String) keys.nextElement();
	        	
	        	if(key.equals("registerUsername")) {
	        		username = request.getParameterValues(key)[0];
	        	} else if(key.equals("registerPassword")) {
	        		passwordHash = BCrypt.hashpw(request.getParameterValues(key)[0], salt);
	        	} else if(key.equals("confirmPassword")) {
	        		confirmPasswordHash = BCrypt.hashpw(request.getParameterValues(key)[0], salt);
	        	}
	        }
	        
	        Class.forName("org.gjt.mm.mysql.Driver");
        	connect = DriverManager.getConnection("jdbc:mysql://localhost/prototype", "root", "");
        	
        	//Check for existing username
        	findUsername = connect.prepareStatement("select USERNAME from PROTOTYPE.ACCOUNT where USERNAME=?");
        	findUsername.setString(1, username);
            
        	resultSet = findUsername.executeQuery();
        	
        	//If the query returns nothing, the username is available for registration
        	if(!resultSet.next()) { 
        		if(passwordHash.equals(confirmPasswordHash)) {
    	        	storeAccount = connect.prepareStatement("insert into PROTOTYPE.ACCOUNT values(default,?,?,?)");
    	        	
    	        	storeAccount.setString(1, username);
    	        	storeAccount.setString(2, passwordHash);
    	        	storeAccount.setString(3, "");
    	        	
    	        	storeAccount.executeUpdate();
    	        	
    	        	connect.close();
    	        	
    	        	//Return confirm page: Username and password were enrolled
    	        	request.getRequestDispatcher("/registerSuccess.html").include(request, response);
        			
    	        	
    	        } else {
    	        	//Return error page: Passwords are not identical
    	        	request.getRequestDispatcher("/passwordMismatch.html").include(request, response);
    	        }
        	} else {
        		//Return error page: Username is already taken
        		request.getRequestDispatcher("/usernameExists.html").include(request, response);
        	}
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	        
	        // invalid request
	        response.getWriter().println("ERROR:Error processing request: "+e.getMessage());
	    }
  }
}
