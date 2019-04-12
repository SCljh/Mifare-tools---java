package mifare;

import java.sql.*;

public class Sql_tool {
	
	
	private Connection getConnection(String dbname) {
		 
        Connection con = null;
 
        try {
        	Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  
            String url = "jdbc:sqlserver://127.0.0.1:1433;databaseName=" + dbname;  
            con = DriverManager.getConnection(url,"sa","ACMer072319");  
            System.out.println("���ݿ����ӳɹ�");  
 
        } catch (ClassNotFoundException e) {
        	System.out.println("���ݿ�����ʧ��");
            return null;
        } catch (SQLException e) {
        	System.out.println("���ݿ�����ʧ��");
            return null;
        }
 
        return con;
    }
	
	public Boolean hasUid(String uid, String dbname){
		
String sql = null;
		
        ResultSet res = null;
 
        Connection con = getConnection(dbname);
 
        Statement state = null;
 
        if (!(con == null)) {
        	
        	sql = "select * from uidTable where uid = '"+uid+"'";
 
            try {
                state = con.createStatement();
                res = state.executeQuery(sql);
                if (res.next()){
    				System.out.println("���ݴ���");
    				return true;
                }
                else {System.out.println("not found"); return false;}
            } catch (SQLException e) {
                return null;
            }
        }
        
        try {
			if (res.next()){
				System.out.println("���ݴ���");
				return true;
			}
			else {System.out.println("���ݲ�����"); return false;}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
	}
	

	
	
	public int insert(String uid, String dbname) {
		
		String sql = null;
		
        int iId = -1;
 
        Connection con = getConnection(dbname);
 
        Statement state = null;
        
        if (hasUid(uid, dbname)){
        	System.out.println("�˿��Ѵ���");
        	return -2;
        }
        
        
       
        sql = "insert into uidTable values (" +uid+ ")";
        if (con != null) {
            try {
                state = con.createStatement();
 
                int res1 = state.executeUpdate(sql);
                
                System.out.println("����ɹ�");
 
                return 1;
            } catch (SQLException e) {
            	System.out.println("����ʧ��"+e.getMessage());
                iId = -1;
            }
 
        }
 
        if (state != null) {
            try {
                state.close();
            } catch (SQLException e) {
            }
        }
 
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
            }
        }
 
        return iId;
    }
	
//	private String url;
//	private Connection con;
//
//	public Sql_tool(String dbname){
//		try {  
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");  
//            this.url = "jdbc:sqlserver://127.0.0.1:1433;databaseName=" + dbname;  
//            this.con = DriverManager.getConnection(url,"sa","ACMer072319");  
//            System.out.println("���ݿ����ӳɹ�");  
//            con.close();  
//        }  
//        catch(Exception e) {  
//            System.out.println("���ݿ�����ʧ��\n" + e.toString());  
//        }
//	}
	
}
