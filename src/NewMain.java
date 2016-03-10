
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ARAVIND
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "amrita123");
                Statement st=c.createStatement();
                Statement sti=c.createStatement();
                
                ResultSet rst = sti.executeQuery("select * from college");
                
                
                Double clu1=6.7,clu2=8.2;

               //System.out.println("Name Roll Dept GPA Cluster"); 
               
               //int count=0;
               int ki;
                
                while(rst.next())    //To find the total number of tuples
                {
                    String name = rst.getString("Name");
                    String roll = rst.getString("roll");
                    String dept = rst.getString("dept");
                    Double gpa   = rst.getDouble("gpa");
                    Integer clu = rst.getInt("Cluster");
                    
                    //System.out.println(name +" "+ roll + " "+ " "+ dept + " "+gpa+" "+clu);
                    
                    //count++;
                    
                    
                        Double k,p;
                        
                        if(clu1 > gpa )
                            k= clu1-gpa;
                        else
                            k=gpa-clu1;
                        
                         if(clu2 > gpa )
                            p= clu2-gpa;
                        else
                            p=gpa-clu2;
                         
                         if(k>p)
                            ki=2;
                         else
                             ki=1;
                        
                           // ResultSet rs = 
                  st.executeUpdate("UPDATE college SET Cluster="+ ki +"where roll='"+roll+"'");
                    
                    
                } 

       
    }   catch (Exception ex) {
           ex.printStackTrace();
        }
    }
}
