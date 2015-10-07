package www.schoolspring.com;
import java.sql.*;
public class DBConnect {
	public static Connection conn = null;
	private static Statement statement =null;
    private PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    static String qwerty;

	public static void main(String[] args) {
		
		Connection testCon=Connect("jdbc:mysql://us-mm-por-11.cleardb.com/ssv2","ssdb","cyPm0tP5b!");
		ResultSet rs=ExcecuteSQLQueryToResultSet(testCon,"Select * from A_Login Limit 100000");
	}
public static Connection Connect(String ConnectionString,String UserName,String Password)
{
	try {
	    try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	    conn = DriverManager.getConnection(ConnectionString,UserName,Password);
	    if(conn!=null)
	    {
            System.out.println("SUCESS!!!");
	    }
	    else
	    {
	    	System.out.println("FAILURE!!!");
	    }
	} catch (SQLException ex) {
	    // handle any errors
	    System.out.println("SQLException: " + ex.getMessage());
	    System.out.println("SQLState: " + ex.getSQLState());
	    System.out.println("VendorError: " + ex.getErrorCode());
	}
	return conn;
}

public static ResultSet ExcecuteSQLQueryToResultSet(Connection dbCon,String Query)
{
	try {
		statement = dbCon.createStatement();
	    resultSet = statement.executeQuery(Query);
	    resultSet.last();
	    int size = resultSet.getRow();
	    resultSet.beforeFirst();     
	    System.out.println("Output:"+size);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return resultSet;


}

public static ResultSet ExcecuteInsert(Connection dbCon,String Query)
{
	try {
		statement = dbCon.createStatement();
	   statement.executeUpdate(Query);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return resultSet;


}

}
