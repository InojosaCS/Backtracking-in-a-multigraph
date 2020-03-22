import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.IOException;
import java.lang.String;
import java.io.*;


public class CambiarTexto
{   
	
    static void modifyFile(String filePath, String oldString, String newString)
    {
        File fileToBeModified = new File(filePath);         
        String oldContent = "";         
        BufferedReader reader = null;         
        FileWriter writer = null;
         
        try
        {
            reader = new BufferedReader(new FileReader(fileToBeModified));
             
            //Reading all the lines of input text file into oldContent
             
            String line = reader.readLine();
             
            while (line != null) 
            {
                oldContent = oldContent + line + System.lineSeparator();
                 
                line = reader.readLine();
            }
             
            //Replacing oldString with newString in the oldContent
             
            String newContent = oldContent.replaceAll(oldString, newString);
             
            //Rewriting the input text file with newContent
             
            writer = new FileWriter(filePath); 
            writer.write(newContent);
            return;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            { 
                reader.close();          
                writer.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
    
    static void RecuperarArchivo(String filePath, String[] ciudades)
    {
        File fileToBeModified = new File(filePath);         
        String oldContent = "";         
        BufferedReader reader = null;         
        FileWriter writer = null;
         
        try
        {
            reader = new BufferedReader(new FileReader(fileToBeModified));
    	
			String line = reader.readLine();
            int k = 0;
            int n = 0;
            
            while (line != null) 
            {	
            	if(k==1) {
            		n = Integer.parseInt(line);
            	}
            	if(k>n+2) {
            		String[] helper = line.split("\\s");
            		line = helper[0] + " " + helper[1] + " " + ciudades[Integer.parseInt(helper[2])] + " " + helper[3];
            	}
                oldContent = oldContent + line + System.lineSeparator();
                 
                line = reader.readLine();
                k++;
            }
             
            //Replacing oldString with newString in the oldContent
             
            String newContent = oldContent;
             
            //Rewriting the input text file with newContent
             
            writer = new FileWriter(filePath); 
            writer.write(newContent);
            return;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            { 
                reader.close();          
                writer.close();
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        }
    }
     
    public static void main(String[] args)
    {   
    	
        //modifyFile(args[0], "Digrafo.java", "epale"); 
    }
}