/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package scanmsn.test;

import scanmsn.data.MSNer;
import scanmsn.util.KmeanEz;

/**
 *
 * @author Zero
 */
public class KmeanTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MSNer[] msner = new MSNer[6];
        msner[0] = new MSNer();  //1, 2, 3, 4
        msner[0].setIndex(0);
        msner[0].setId("0");
        msner[0].add(1);
        msner[0].add(2);
        msner[0].add(3);
        msner[0].add(4);
        msner[1] = new MSNer();  //1, 2, 3, 5
        msner[1].setIndex(1);
        msner[1].setId("1");
        msner[1].add(1);
        msner[1].add(2);
        msner[1].add(3);
        msner[1].add(5);
        msner[2] = new MSNer();  //1, 2, 3, 5
        msner[2].setIndex(2);
        msner[2].setId("2");
        msner[2].add(1);
        msner[2].add(2);
        msner[2].add(3);
        msner[2].add(5);
        
        msner[3] = new MSNer();  //6, 7, 8, 9
        msner[3].setIndex(3);
        msner[3].setId("3");
        msner[3].add(6);
        msner[3].add(7);
        msner[3].add(8);
        msner[3].add(9);
        msner[4] = new MSNer();  //7, 8
        msner[4].setIndex(4);
        msner[4].setId("4");
        msner[4].add(7);
        msner[4].add(8);
        msner[5] = new MSNer();  //6, 7, 8, 9, 10
        msner[5].setIndex(5);
        msner[5].setId("5");
        msner[5].add(6);
        msner[5].add(7);
        msner[5].add(8);
        msner[5].add(9);
        msner[5].add(10);
        
        KmeanEz km = new KmeanEz();
        km.inputData(msner, 2);
        km.analysis();
        MSNer[][] result = km.getResult();
        
        System.out.println("\ndata dump:");
        if(result!=null){
            for(int a=0;a<result.length;a++){
                System.out.println("第 " + a + " 群：");
                for(int b=0;b<result[a].length;b++){
                    System.out.println(result[a][b].getIndex() + " : " + result[a][b].getId());
                }
                System.out.println();
            }
        }
    }

}
