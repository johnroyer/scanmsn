/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanmsn.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zero
 */
public class CKIP {

    private String ip = "";
    private int port = 0;
    private int timeout = 0;
    private String userID = "";
    private String password = "";
    private Socket s = null;
    private String outputXML = "";

    public CKIP() {
        ip = "140.109.19.104";
        port = 1501;
        timeout = 300;
        userID = "";
        password = "";

        outputXML = "<?xml version=\"1.0\" ?>";
        outputXML += "<wordsegmentation version=\"0.1\">";
        outputXML += "<option showcategory=\"1\" />";
        outputXML += "<authentication username=\"" + userID + "\" password=\"" + password + "\" />";
        outputXML += "<text>##inputText##</text></wordsegmentation>";
    }

    private void connect() {
        try {
            s = new Socket(ip, port);
            s.setSoTimeout(timeout * 1000);

        } catch (UnknownHostException ex) {
            System.out.println("Can not find host:" + ip);
            Logger.getLogger(CKIP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("IO error");
            System.out.println(ex);
            Logger.getLogger(CKIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 斷線.
     */
    private void disconeect() {
        try {
            s.close();
            s = null;
        } catch (IOException ex) {
            System.out.println("IO error");
            System.out.println(ex);
            Logger.getLogger(CKIP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 傳送需要做斷詞的文字，但必須事先將特殊符號去除(XML tag).
     * @return 斷詞完成的字詞，陣列中一個元素為一個詞.
     * @param msg 需要斷詞的文句
     * @return
     */
    public String[] sendMessage(String msg) {
        String[] output = null;
        connect();
        System.out.println("in: " + msg);
        String sep = System.getProperty("line.separator");
        msg += sep;
        msg = outputXML.replaceAll("##inputText##", msg);

        // to big5
        try {
            String str = new String(outputXML.getBytes(), "big5");
        //System.out.println("output: " + str);
        } catch (UnsupportedEncodingException ex) {
            System.out.println(ex);
            Logger.getLogger(CKIP.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            s.getOutputStream().write(msg.getBytes());

            InputStreamReader streamReader = new InputStreamReader(s.getInputStream());
            BufferedReader reader = new BufferedReader(streamReader);
            String ckipResult = reader.readLine();
//                System.out.println("reponse: " + ckipResult + "\n");
            output = parseResponed(ckipResult);
            
        } catch (IOException ex) {
            Logger.getLogger(CKIP.class.getName()).log(Level.SEVERE, null, ex);
        }
        disconeect();

        return output;
    }

    /**
     * 處理 CKIP 傳回的資料.
     * @return
     */
    private String[] parseResponed(String in) {
        if (in.length() > 0) {
            //System.out.println("in=\"" + in + "\"");
            int start = in.indexOf("<sentence>") + 10;
            int end = in.indexOf("</sentence>");
            if (start >= 0 && end >= 0) {
                String result = in.substring(start, end);
                String[] array = result.split("　");  //全形空白  = ="
                ArrayList<String> list = new ArrayList<String>();
                for (int a = 0; a < array.length; a++) {
                    if (!array[a].equals("")) {
                        String out = array[a].replaceAll("\\(.*\\)", "");
                        list.add(out);
                        System.out.print(out + "  ");
                    }
                }
                System.out.println("");
                return list.toArray(new String[1]);
            }
            return new String[1];
        } else {
            return new String[1];
        }
    }
}
