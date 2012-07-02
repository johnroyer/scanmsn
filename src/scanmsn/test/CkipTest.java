/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanmsn.test;

import scanmsn.util.CKIP;

/**
 *
 * @author Zero
 */
public class CkipTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        CKIP c = new CKIP();
        try {
            String output = "加油禮拜一沒有做出來就要準備接受二一的事實了";
            //output += output;
            System.out.println("len = " + output.length());
            c.sendMessage(output);
        } catch (Exception e) {
            System.out.println(e);
        }


    }
}
