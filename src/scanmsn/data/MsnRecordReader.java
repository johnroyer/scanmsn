/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanmsn.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Zero
 */
public class MsnRecordReader {

    private File record;
    private String regex = "<Text(\\s(\\w*=\".*?\")?)*((/>)|((/*?)>.*?</Text>))";

//    public MsnRecordReader(String path) {
//        this.record = new File(path);
//        if (!record.exists()) {
//            record = null;
//        }
//    }
//
//    public MsnRecordReader(File f) {
//        this.record = f;
//    }
    
    public MsnRecordReader(){
        record = null;
    }

    public void setPath(String path) {
        this.record = new File(path);
        if (!record.exists()) {
            record = null;
        }
    }

    public void setPath(File f) {
        this.record = f;
        if (!record.exists()) {
            record = null;
        }
    }

    public String read() {
        if (record == null) {
            return "";
        } else {
            try {
                // read from file
                Scanner sc = new Scanner(record, "utf-8");
                String content = "";
                while (sc.hasNextLine()) {
                    content += " " + sc.nextLine();
                }

                // parse content
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(content);
                String output = "";
                while (m.find()) {
                    String tmp = m.group();
                    output += " " + tmp.substring(tmp.indexOf('>')+1, tmp.lastIndexOf('<'));
                    //output += " " + m.group();
                }
                
                //clean XML tag
                
                //clean special character
                
                return output;

            } catch (FileNotFoundException ex) {
                System.out.println("File not found");
                System.out.println(ex);
                Logger.getLogger(MsnRecordReader.class.getName()).log(Level.SEVERE, null, ex);
                return "";
            }
        }
    }
}
