/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scanmsn.test;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Zero
 */
public class waitTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        while(true){
            System.out.println("wait 10 seconds");
            try {
                new Object().wait(10000);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

}
