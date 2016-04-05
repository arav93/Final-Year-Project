
/**
 *
 * @author Nathan Magnus, under the supervision of Howard Hamilton
 * Copyright: University of Regina, Nathan Magnus and Su Yibin, June 2009.
 * No reproduction in whole or part without maintaining this copyright notice
 * and imposing this condition on any subsequent users.
 * 
 *
 * File:
 * Input files needed:
 *      1. config.txt - three lines, each line is an integer
 *          line 1 - number of items per transaction
 *          line 2 - number of transactions
 *          line 3 - minsup
 *      2. transa.txt - transaction file, each line is a transaction, items are separated by a space
 */

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Apriori {
    
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
             System.out.println("Query accepted!");
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
 

    public static void main(String[] args) throws ClassNotFoundException, SQLException 
    {
        try{
                Class.forName("org.postgresql.Driver");
                Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "amrita123");
                //Connection d = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "amrita123");
                Statement st=c.createStatement();
                Statement stc=c.createStatement();
                Statement s=c.createStatement();
                Statement O=c.createStatement();
                
                init(s);
                
                Scanner in = new Scanner(System.in);

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
                    
        AprioriCalculation ap = new AprioriCalculation();
        ap.aprioriProcess();
        
        

    BufferedReader inp = new BufferedReader(new FileReader("I:\\Text\\S8\\First Review\\output.txt"));
    String line = inp.readLine();
    PushToDB(line,st);
    print(stc,s,st,O);
    //PrintFPs(D,G,Sec,Cre,Loc,st,stc,s);    
        
                                 
    }
        
        
        catch(Exception e)
        {
            e.printStackTrace();
        }}
}
/******************************************************************************
 * Class Name   : AprioriCalculation
 * Purpose      : generate Apriori itemsets
 *****************************************************************************/
class AprioriCalculation
{
    Vector<String> candidates=new Vector<String>(); //the current candidates
    String configFile="I:\\Text\\S8\\First Review\\config.txt"; //configuration file
    String transaFile="I:\\Text\\S8\\First Review\\data.txt"; //transaction file
    String outputFile="I:\\Text\\S8\\First Review\\output.txt";//output file
    int numItems; //number of items per transaction
    int numTransactions; //number of transactions
    double minSup; //minimum support for a frequent itemset
    String oneVal[]; //array of value per column that will be treated as a '1'
    String itemSep = " "; //the separator value for items in the database

    /************************************************************************
     * Method Name  : aprioriProcess
     * Purpose      : Generate the apriori itemsets
     * Parameters   : None
     * Return       : None
     *************************************************************************/
    public void aprioriProcess() throws FileNotFoundException, UnsupportedEncodingException
    {
        Date d; //date object for timing purposes
        long start, end; //start and end time
        int itemsetNumber=0; //the current itemset being looked at
        //get config
        getConfig();

        //System.out.println("Apriori algorithm has started.\n");

        //start timer
        d = new Date();
        start = d.getTime();

        //while not complete
        do
        {
            //increase the itemset that is being looked at
            itemsetNumber++;

            //generate the candidates
            generateCandidates(itemsetNumber);

            //determine and display frequent itemsets
            calculateFrequentItemsets(itemsetNumber);
            if(candidates.size()!=0)
            {
                //System.out.println("Frequent " + itemsetNumber + "-itemsets");
                //System.out.println(candidates);
            }
        //if there are <=1 frequent items, then its the end. This prevents reading through the database again. When there is only one frequent itemset.
        }while(candidates.size()>1);

        //end timer
        d = new Date();
        end = d.getTime();

        //display the execution time
        System.out.println("Execution time is: "+((double)(end-start)/1000) + " seconds.");
    }
    
     /************************************************************************
     * Method Name  : GetCandidates
     * Purpose      : get the candidates to the main
     * Parameters   : Candidates
     * Return       : Candidates
     *************************************************************************/
    
    public Vector<String> GetCandidates(Vector<String> candidates)
    {
        return candidates;
    }

    /************************************************************************
     * Method Name  : getInput
     * Purpose      : get user input from System.in
     * Parameters   : None
     * Return       : String value of the users input
     *************************************************************************/
    public static String getInput()
    {
        String input="";
        //read from System.in
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //try to get users input, if there is an error print the message
        try
        {
            input = reader.readLine();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return input;
    }

    /************************************************************************
     * Method Name  : getConfig
     * Purpose      : get the configuration information (config filename, transaction filename)
     *              : configFile and transaFile will be change appropriately
     * Parameters   : None
     * Return       : None
     *************************************************************************/
    private void getConfig()
    {
        FileWriter fw;
        BufferedWriter file_out;

        String input="";
        //ask if want to change the config
        /*System.out.println("Default Configuration: ");
        System.out.println("\tRegular transaction file with '" + itemSep + "' item separator.");
        System.out.println("\tConfig File: " + configFile);
        System.out.println("\tTransa File: " + transaFile);
        System.out.println("\tOutput File: " + outputFile);
        System.out.println("\nPress 'C' to change the item separator, configuration file and transaction files");
        System.out.print("or any other key to continue.  ");*/
        //input=getInput();

        if(input.compareToIgnoreCase("c")==0)
        {
            //System.out.print("Enter new transaction filename (return for '"+transaFile+"'): ");
            //input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                transaFile=input;

            //System.out.print("Enter new configuration filename (return for '"+configFile+"'): ");
            //input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                configFile=input;

            //System.out.print("Enter new output filename (return for '"+outputFile+"'): ");
            //input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                outputFile=input;

            //System.out.println("Filenames changed");

            //System.out.print("Enter the separating character(s) for items (return for '"+itemSep+"'): ");
            //input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                itemSep=input;


        }

        try
        {
             FileInputStream file_in = new FileInputStream(configFile);
             BufferedReader data_in = new BufferedReader(new InputStreamReader(file_in));
             //number of items
             numItems=Integer.valueOf(data_in.readLine()).intValue();

             //number of transactions
             numTransactions=Integer.valueOf(data_in.readLine()).intValue();

             //minsup
             minSup=(Double.valueOf(data_in.readLine()).doubleValue());

             //output config info to the user
             //System.out.print("\nInput configuration: "+numItems+" items, "+numTransactions+" transactions, ");
             //System.out.println("minsup = "+minSup+"%");
             //System.out.println();
             minSup/=100.0;

            oneVal = new String[numItems];
           // System.out.print("Enter 'y' to change the value each row recognizes as a '1':");
            if(getInput().compareToIgnoreCase("y")==0)
            {
                //for(int i=0; i<oneVal.length; i++);
                {
                    //System.out.print("Enter value for column #" + (i+1) + ": ");
                    //oneVal[i] = getInput();
                }
            }
            else
                for(int i=0; i<oneVal.length; i++)
                    oneVal[i]="1";

            //create the output file
            fw= new FileWriter(outputFile);
            file_out = new BufferedWriter(fw);
            //put the number of transactions into the output file
            //file_out.write(numTransactions + "\n");
            //file_out.write(numItems + "\n******\n");
            file_out.close();
        }
        //if there is an error, print the message
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

    /************************************************************************
     * Method Name  : generateCandidates
     * Purpose      : Generate all possible candidates for the n-th itemsets
     *              : these candidates are stored in the candidates class vector
     * Parameters   : n - integer value representing the current itemsets to be created
     * Return       : None
     *************************************************************************/
    private void generateCandidates(int n)
    {
        Vector<String> tempCandidates = new Vector<String>(); //temporary candidate string vector
        String str1, str2; //strings that will be used for comparisons
        StringTokenizer st1, st2; //string tokenizers for the two itemsets being compared

        //if its the first set, candidates are just the numbers
        if(n==1)
        {
            for(int i=1; i<=numItems; i++)
            {
                tempCandidates.add(Integer.toString(i));
            }
        }
        else if(n==2) //second itemset is just all combinations of itemset 1
        {
            //add each itemset from the previous frequent itemsets together
            for(int i=0; i<candidates.size(); i++)
            {
                st1 = new StringTokenizer(candidates.get(i));
                str1 = st1.nextToken();
                for(int j=i+1; j<candidates.size(); j++)
                {
                    st2 = new StringTokenizer(candidates.elementAt(j));
                    str2 = st2.nextToken();
                    tempCandidates.add(str1 + " " + str2);
                }
            }
        }
        else
        {
            //for each itemset
            for(int i=0; i<candidates.size(); i++)
            {
                //compare to the next itemset
                for(int j=i+1; j<candidates.size(); j++)
                {
                    //create the strigns
                    str1 = new String();
                    str2 = new String();
                    //create the tokenizers
                    st1 = new StringTokenizer(candidates.get(i));
                    st2 = new StringTokenizer(candidates.get(j));

                    //make a string of the first n-2 tokens of the strings
                    for(int s=0; s<n-2; s++)
                    {
                        str1 = str1 + " " + st1.nextToken();
                        str2 = str2 + " " + st2.nextToken();
                    }

                    //if they have the same n-2 tokens, add them together
                    if(str2.compareToIgnoreCase(str1)==0)
                        tempCandidates.add((str1 + " " + st1.nextToken() + " " + st2.nextToken()).trim());
                }
            }
        }
        //clear the old candidates
        candidates.clear();
        //set the new ones
        candidates = new Vector<String>(tempCandidates);
        tempCandidates.clear();
    }

    /************************************************************************
     * Method Name  : calculateFrequentItemsets
     * Purpose      : Determine which candidates are frequent in the n-th itemsets
     *              : from all possible candidates
     * Parameters   : n - iteger representing the current itemsets being evaluated
     * Return       : None
     *************************************************************************/
    private void calculateFrequentItemsets(int n)
    {
        Vector<String> frequentCandidates = new Vector<String>(); //the frequent candidates for the current itemset
        FileInputStream file_in; //file input stream
        BufferedReader data_in; //data input stream
        FileWriter fw;
        BufferedWriter file_out;

        StringTokenizer st, stFile; //tokenizer for candidate and transaction
        boolean match; //whether the transaction has all the items in an itemset
        boolean trans[] = new boolean[numItems]; //array to hold a transaction so that can be checked
        int count[] = new int[candidates.size()]; //the number of successful matches

        try
        {
                //output file
                fw= new FileWriter(outputFile, true);
                file_out = new BufferedWriter(fw);
                //load the transaction file
                file_in = new FileInputStream(transaFile);
                data_in = new BufferedReader(new InputStreamReader(file_in));

                //for each transaction
                for(int i=0; i<numTransactions; i++)
                {
                    //System.out.println("Got here " + i + " times"); //useful to debug files that you are unsure of the number of line
                    stFile = new StringTokenizer(data_in.readLine(), itemSep); //read a line from the file to the tokenizer
                    //put the contents of that line into the transaction array
                    for(int j=0; j<numItems; j++)
                    {
                        trans[j]=(stFile.nextToken().compareToIgnoreCase(oneVal[j])==0); //if it is not a 0, assign the value to true
                    }

                    //check each candidate
                    for(int c=0; c<candidates.size(); c++)
                    {
                        match = false; //reset match to false
                        //tokenize the candidate so that we know what items need to be present for a match
                        st = new StringTokenizer(candidates.get(c));
                        //check each item in the itemset to see if it is present in the transaction
                        while(st.hasMoreTokens())
                        {
                            match = (trans[Integer.valueOf(st.nextToken())-1]);
                            if(!match) //if it is not present in the transaction stop checking
                                break;
                        }
                        if(match) //if at this point it is a match, increase the count
                            count[c]++;
                    }

                }
                for(int i=0; i<candidates.size(); i++)
                {
                    //  System.out.println("Candidate: " + candidates.get(c) + " with count: " + count + " % is: " + (count/(double)numItems));
                    //if the count% is larger than the minSup%, add to the candidate to the frequent candidates
                    if((count[i]/(double)numTransactions)>=minSup)
                    {
                        frequentCandidates.add(candidates.get(i));
                        //put the frequent itemset into the output file
                        file_out.write(candidates.get(i) + ",");// + count[i]/(double)numTransactions + "\n");
                    }
                }
                file_out.write("-");
                file_out.close();
        }
        //if error at all in this process, catch it and print the error messate
        catch(IOException e)
        {
            System.out.println(e);
        }
        //clear old candidates
        candidates.clear();
        //new candidates are the old frequent candidates
        candidates = new Vector<String>(frequentCandidates);
        frequentCandidates.clear();
    }
}
