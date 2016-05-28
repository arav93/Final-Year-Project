
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class OverLap {

    public static int getCount(String s, Statement st) throws SQLException
    {
        s = s.replace("avg(gpa)","gpa");
        ResultSet rs = st.executeQuery(s);
        int count=0;
        while(rs.next())
        {
            count++;
        }
        return count;
    }
    
    public static boolean checkQ(String q, String temp, Statement s) throws SQLException
    {
        boolean res = true ;
        q = q.replace("avg(gpa)","gpa");
        
        int count1 = getCount(q,s);
        
        ResultSet rst = s.executeQuery("select * from query");
        
        while(rst.next())
        {
            String Q  = rst.getString("query");
            int count = rst.getInt("count");
            
            if(Q.contains(temp)|| temp.contains(Q))
            {
                if(count1+1==count || count+1==count1)
                {
                    res=false;
                    break;
                }
            }
        }
        
        return res;
    }
    
    public static void main(String[] args) 
    {
        try
        {
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "amrita123");
                Statement st=c.createStatement();
                Scanner in = new Scanner(System.in);
                System.out.println("Enter the query!");
                String q = in.nextLine();
                String temp = new String();
                
                for(int i = 0;i < q.length(); i++)
                {
                    if(q.charAt(i)=='\'' && q.charAt(i-1)=='=')
                    {
                        
                        for(int j = i+1 ; q.charAt(j)!='\'';j++)
                        {
                            temp+=q.charAt(j);
                        }
                        temp+=" ";
                    }
                }
                
                int count = getCount(q,st);
                
                boolean check = checkQ(q,temp,st);
                if(check)
                    st.executeUpdate("insert into query values ( '"+ temp + "' , "+count + ")");
                else
                    System.out.println("Query not allowed!");
                
        }
        
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }
}
