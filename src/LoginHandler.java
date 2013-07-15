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


@WebServlet("/login")
public class LoginHandler extends HttpServlet {
	
	private Connection connect = null;
	private PreparedStatement findUsername = null;
	private ResultSet resultSet = null;
  
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) 
  		throws IOException{
	  try
	    {               	    	
	        Enumeration<?> keys = request.getParameterNames();
	    	
	    	String username = "", password = "", otp = "";
	        
	        for(; keys.hasMoreElements();) {
	        	String key = (String) keys.nextElement();
	        	
	        	if(key.equals("loginUsername")) {
	        		username = request.getParameterValues(key)[0];
	        	} else if(key.equals("loginPassword")) {
	        		password = request.getParameterValues(key)[0];;
	        	} else if(key.equals("OTP")) {
	        		otp = request.getParameterValues(key)[0];
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
        				if(!otp.equals("")) { //Modify this condition in the future to check OTP 
        					request.getRequestDispatcher("/loginSuccess.html").include(request, response);
        				} else {
        					request.getRequestDispatcher("/wrongOTP.html").include(request, response);
        				} 
        			} else {
        				request.getRequestDispatcher("/passwordIncorrect.html").include(request, response);
        			}
        		} else { //Just in case it wasn't caught the first time
        			request.getRequestDispatcher("/invalidUsername.html").include(request, response);
        		}
        	} else {
        		request.getRequestDispatcher("/invalidUsername.html").include(request, response);
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
