/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanmsn.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Zero
 */
public class Testmain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {
        String str = "";
        File file = new File("C:\\jmy.xml");
        Scanner sc = new Scanner(file, "utf-8");
        int a = 0;
        
            while (sc.hasNext()) {
                str = str + " " + sc.nextLine();
                a++;
            }
        
        System.out.println(str);
        
        //Pattern p = Pattern.compile("<(\\w+)(\\s(\\w*=\".*?\")?)*((/>)|((/*?)>.*?</\\1>))");
        Pattern p = Pattern.compile("<Text(\\s(\\w*=\".*?\")?)*((/>)|((/*?)>.*?</Text>))");
        Matcher m = p.matcher(str);
        System.out.println();
        while(m.find()){
            System.out.println(m.group());
        }
    }
}
