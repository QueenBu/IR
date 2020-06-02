package tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CLIHandler {
    private BufferedReader br;

    public CLIHandler() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    public String readUserInput(String message){
        writeToCL(message);
        return this.readUserInput();
    }

    public String readUserInput(){
        String returnString = null;
        do {
            try {
                returnString = br.readLine();
            } catch ( IOException e ) {
                System.err.println(e.getMessage());
                System.err.println("Could not read your input, try again");
            }
        } while (returnString == null);
        return returnString;
    }

    public void writeToCL(Object text){
        System.out.println(text);
    }

}
