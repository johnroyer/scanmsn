/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanmsn.util;

import java.util.Arrays;
import scanmsn.data.MSNer;

/**
 *
 * @author Zero
 */
public class KmeanEz extends Kmean {

    @Override
    protected double getElementDist(MSNer base, MSNer related) {
        int[] basePhraseIndex = base.getPhraseIndex();
        int[] relatedPhraseIndex = related.getPhraseIndex();
        int duplicated = 0;
        int summation = 0;
        Arrays.sort(basePhraseIndex);
        Arrays.sort(relatedPhraseIndex);
        int a = 0, b = 0;
        for (a = 0; a < basePhraseIndex.length; a++) {
            while (b < relatedPhraseIndex.length && relatedPhraseIndex[b] < basePhraseIndex[a]) {
                summation++;
                b++;
            }
            if (b < relatedPhraseIndex.length && basePhraseIndex[a] == relatedPhraseIndex[b]) {
                //a++;
                b++;
                duplicated++;
            }
            summation++;
        }
        if (b < relatedPhraseIndex.length) {
            summation += relatedPhraseIndex.length - b;
        }
        //System.out.println(base.getId() + "  vs.  " + related.getId());
        //System.out.println("duplicated=" + duplicated + "   sum=" + summation);
        if(summation==0)
            summation=1;
        return (1f - (float) duplicated / (float) summation);
    }

    
}
