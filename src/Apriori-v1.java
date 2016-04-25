

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class JavaApplication20 {
    
    public static void ExecQuery(String s, Statement st) throws SQLException
    {
        ResultSet rs = st.executeQuery(s.replace("avg(gpa)","gpa"));
        float val=0;
        int count=0;
        while(rs.next())
        {
            val+=rs.getFloat("gpa");
            count++;
        }
        val = (float) val/count;
        System.out.println("The average gpa is "+val);
    }
    
    public static void PushToDB(String line, Statement s) throws SQLException
    {
       int beg=0;
       for(int i=0;i<line.length()-1;i++)
       {
          
           if(line.charAt(i)==',')
           {
               String temp=line.substring(beg,i);
               if(temp.charAt(0)=='-')
               {
                   s.executeUpdate("insert into fp values ('-');");
                   temp = temp.substring(1);
               }
               s.executeUpdate("insert into fp values ('"+temp+"');");
               beg=i+1;
               
           }
       }
    }
    
    public static void PrintFPs(String[] D, String[] G,String[] Sec, String[] Cre, String[] Loc,Statement s,Statement st, Statement sI) throws SQLException
    {
        ResultSet rs=s.executeQuery("select * from fp");
        System.out.println("Itemset:");
        int temp=0;
        while(rs.next())
        {
            String FItem = rs.getString("fp");
            if(FItem.equals("-"))
                temp=1;
            int beg=0;
                if(temp>0)
                {
                    System.out.println("Itemset:");
                    temp++;
                for(int i=0;i<FItem.length();i++)
                {     
                    if(FItem.charAt(i)==' ')
                    {
                        String no = FItem.substring(beg, i);
                        beg=i+1;
                        ResultSet rst=st.executeQuery("select item,num from map where num='"+no+"'");
                    
                        while(rst.next())
                        {
                        String name =    rst.getString("item");
                        String num  =   rst.getString("num");  
                        //System.out.println(" "+name);
                        
                        }
                    }
          
                    if(i+1==FItem.length())
                    {
                    String no=FItem.substring(beg, i+1);
                    ResultSet rst=st.executeQuery("select item,num from map where num='"+no+"'");
                    while(rst.next())
                        {
                        String name =    rst.getString("item");
                        String num  =   rst.getString("num");  
                        //System.out.println(" "+name);
                        sI.executeUpdate("insert into fpC values ('"+name+"');");
                        }
                    }
                }
                }
               else
                {
                    ResultSet rst=st.executeQuery("select item,num from map where num='"+FItem+"'");
                    while(rst.next())
                    {
                        String name =    rst.getString("item");
                        String num  =   rst.getString("num");  
                        System.out.println(" "+name);
                        
                    }
                }    
        }            
    }
    
    public static boolean isOverlap(String s, Statement st) throws SQLException
    {
        ResultSet rs = st.executeQuery("select * from fp");
        while(rs.next())
        {
            String fp   = rs.getString("fp");
            int bit  = rs.getInt("usedbit");
            
            if(fp.length()>s.length())
            {
                if(fp.contains(s))
                {
                    if(bit==1)
                       return false;
                }
            }
            else
            {
                if(s.contains(fp))
                {
                    if(bit==1)
                       return false;
                    
                }
            }
        }
        return true;
    }
    
    public static void print(Statement st, Statement sc, Statement stmt, Statement O) throws SQLException
    {
     Scanner in = new Scanner(System.in);
     int count=0;
     while(count<3)
     {     
     System.out.println("Enter query!");
     String s = in.nextLine();
     
     String val= new String();
     String fin = new String();
     for(int i=0;i<s.length();i++)
     {
         String res = new String();
         if(s.charAt(i)=='\'')
         {
             i++;
             while(s.charAt(i)!='\'')
             {
                 res+=s.charAt(i);
                 i++;
             }
             ResultSet rs = st.executeQuery("SELECT num from map where item='"+res+"'");
             while(rs.next())
             {
                 val = rs.getString("num");
             }
             fin+=val;
             fin+=' ';
         }
         
     }
          int flag=0;
         ResultSet r = sc.executeQuery("select fp from fp ");
         while(r.next())
         {
             String fp = r.getString("fp");
             if(fp.equals(fin.substring(0, fin.length()-1))&&isOverlap(fin.substring(0, fin.length()-1),O))
             {
                 flag=1;
                 stmt.executeUpdate("update fp set usedbit=1 where fp='"+fp+"'");
                 break;
             }
         }
         if(flag==1)
         { 
             ExecQuery(s,st);
         }   
         else
             System.out.println("Query rejected! ");
         count++;
     }   
   }  
    
    public static void init(Statement s) throws SQLException
    {
        s.executeUpdate("delete from map");
        s.executeUpdate("delete from fp");
        s.executeUpdate("delete from fpC");
    }
    
    public static void makeInput(Statement stc, Statement st) throws SQLException, FileNotFoundException, UnsupportedEncodingException
    {
        ResultSet rs = stc.executeQuery("select DISTINCT(gender) from college");
                
                String[] G = new String[2];
                int i=0;
                while(rs.next())
                {
                    G[i]=rs.getString("gender");
                    st.executeUpdate("insert into map values ('"+G[i]+"','"+(i+1)+"');");
                    i++;      
                }
                
                int tot=i+1;
                
                              rs = stc.executeQuery("select DISTINCT(dept) from college");
                
                String[] D = new String[4];
                i=0;
                while(rs.next())
                {
                    D[i]=rs.getString("dept");
                    st.executeUpdate("insert into map values ('"+D[i]+"','"+(tot+i)+"');");
                    i++;
                }
                
                tot+=i;
                
                rs = stc.executeQuery("select DISTINCT(sec) from college");
                
                String[] Sec = new String[2];
                i=0;
                while(rs.next())
                {
                    Sec[i]=rs.getString("sec");
                    st.executeUpdate("insert into map values ('"+Sec[i]+"','"+(tot+i)+"');");
                    i++;
                }
                
                tot+=i;
                
                rs = stc.executeQuery("select DISTINCT(credits) from college");
                
                String[] Cre = new String[6];
                i=0;
                while(rs.next())
                {
                    Cre[i]=rs.getString("credits");
                    st.executeUpdate("insert into map values ('"+Cre[i]+"','"+(tot+i)+"');");
                    i++;
                }
                
                tot+=i;
                
                rs = stc.executeQuery("select DISTINCT(location) from college");
                
                String[] Loc = new String[230];
                i=0;
                while(rs.next())
                {
                    Loc[i]=rs.getString("location");
                    st.executeUpdate("insert into map values ('"+Loc[i]+"','"+(tot+i)+"');");
                    i++;
                }
                
                
                rs = st.executeQuery("select gender,dept,sec,credits,location from college");
                String[] S = new String[500];
                i=0;
                PrintWriter writer = new PrintWriter("I:\\Text\\S8\\First Review\\data.txt", "UTF-8");
                while(rs.next())
                {
                    String g    = rs.getString("gender");
                    String d    = rs.getString("dept");
                    String sec  = rs.getString("sec");
                    String cre    = rs.getString("credits");
                    String loc    = rs.getString("location");
        
                    String K = new String("0");                   
                    for(int p=0;p<(G.length+D.length+Sec.length+Cre.length+Loc.length)-1;p++)
                        K+="0";

                    char[] P =K.toCharArray(); 
                    int j;
                    for(j=0;j<G.length;j++)
                    {
                        if(g.equals(G[j]))
                            P[j]='1';
                    }
                  int lastpos=j;
                  int k;
                  for(k=0;k<D.length;k++ )
                  {
                      if(d.equals(D[k]))
                            P[k+lastpos]='1';    
                  }
                  
                  lastpos+=k;
                  
                  int ks;
                  for(ks=0;ks<Sec.length;ks++ )
                  {
                      if(sec.equals(D[ks]))
                            P[ks+lastpos]='1';    
                  }
                  
                  lastpos+=ks;
                  
                  int kc;
                  for(kc=0;kc<Cre.length;kc++ )
                  {
                      if(cre.equals(Cre[kc]))
                            P[kc+lastpos]='1';    
                  }
                  
                  lastpos+=kc;
                  
                  int kl;
                  for(kl=0;kl<Loc.length;kl++ )
                  {
                      if(loc.equals(Loc[kl]))
                            P[kl+lastpos]='1';    
                  }
                    
                  S[i] = new String(P);
                 writer.println(S[i].replaceAll(".(?!$)", "$0 "));
                   
                   i++;
                } 
                
                System.out.println(i+" "+(G.length+D.length+Sec.length+Cre.length+Loc.length));
                
                
                writer.close();
                
                writer = new PrintWriter("I:\\Text\\S8\\First Review\\config.txt", "UTF-8");
                writer.println(G.length+D.length+Sec.length+Cre.length+Loc.length);
                writer.println(i);
                writer.println("20");
                
                writer.close();
    }
 

    public static void main(String[] args) throws ClassNotFoundException, SQLException 
    {
        try{
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "amrita123");
                Statement st=c.createStatement();
                Statement stc=c.createStatement();
                Statement s=c.createStatement();
                Statement O=c.createStatement();
                
                init(s);  //To clear the details all the databases
                
                Scanner in = new Scanner(System.in);
                
                makeInput(stc,st); //To make the required input to pass on to the apriori function

        AprioriCalculation ap = new AprioriCalculation();
        ap.aprioriProcess();
        
        

    BufferedReader inp = new BufferedReader(new FileReader("I:\\Text\\S8\\First Review\\output.txt"));
    String line = inp.readLine();
    PushToDB(line,st);
    print(stc,s,st,O);   
        
                                 
    }
        
        
        catch(Exception e)
        {
            e.printStackTrace();
        }}
}
