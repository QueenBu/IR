package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class FileParser {

    FileParser(){

    }

    public static String parseFile(String path){
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            StringBuilder s = new StringBuilder();
            String currentline;
            while((currentline = br.readLine()) != null){
                s.append(currentline);
            }
            return s.toString();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return "test";
    }


}