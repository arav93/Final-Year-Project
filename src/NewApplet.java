

import java.applet.Applet;
import java.awt.Button;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;


/**
 *
 * @author ARAVIND
 */
public class NewApplet extends Applet implements ActionListener 
{
Button executeButton;
TextField tf,nf;
String message;

@Override
public void init() {
    executeButton = new Button("Execute");
    tf = new TextField(30);
    nf = new TextField(30);
    executeButton.addActionListener((ActionListener)this);
    add(tf);
    add(executeButton);
    add(nf);
}
@Override
public void actionPerformed(ActionEvent e) 
{
    if ( e.getSource() == executeButton ) 
    {
       String s= tf.getText();
       
       try{
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "amrita123");
                Statement st=c.createStatement();
                Statement sti=c.createStatement();
                
                ResultSet rst = sti.executeQuery("select * from college");
                
                
                boolean flag=true;
                
                if(s.contains(" gpa ") ||  s.contains(" * ") || s.contains(",gpa ") || s.contains(" gpa,"))
                    flag=false;
                
                //Altering the query
                if(s.contains("average(gpa)"))
                    s=s.replace("average(gpa)", "gpa");
 
                ResultSet rs=st.executeQuery(s);
                
                int tot=0;
                
                
                while(rst.next())    //To find the total number of tuples
                    tot++;
                
                int count=0;
                
                
                
                
                while(rs.next())
                {
                    count++;
                }
                
               rs=st.executeQuery(s); 
               
               int gpai=0;
               
             if(count> 1 && count != (tot-1) && flag)
             {
                 nf.setText("Query result printed!");
              while(rs.next())
                {
                    float gpa = rs.getShort("gpa");
                    
                    gpai+=gpa;
                    
                }
                    gpai=gpai/count;
                    nf.setText("The result is " + gpai);
             }
              else
                 nf.setText("Query rejected!");

       
    }   catch (Exception ex) {
           ex.printStackTrace();
        }
}
}
}