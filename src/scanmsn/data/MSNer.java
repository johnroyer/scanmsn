/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanmsn.data;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Zero
 */
public class MSNer {
    String id = "";
    int index = 0;
    ArrayList<Integer> wordFreq = new ArrayList<Integer>();
    ArrayList<Integer> phreases = new ArrayList<Integer>();
    HashMap<Integer, Integer> wMap = new HashMap<Integer, Integer>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
    public void add(int wordIndex){
        //check if word is exist
        Integer myIndex = wMap.get(Integer.valueOf(wordIndex));
        if(myIndex==null){
            //not found, add into list
            wordFreq.add(Integer.valueOf(0));
            phreases.add(Integer.valueOf(wordIndex));
            wMap.put(Integer.valueOf(wordIndex), Integer.valueOf(phreases.lastIndexOf(Integer.valueOf(wordIndex))));
        }else{
            //found, freq+1
            int tmp = wordFreq.get(myIndex.intValue()).intValue()+1;
            wordFreq.set(myIndex.intValue(), Integer.valueOf(tmp));
        }
    }
    
    public int getWordAmount(){
        return wordFreq.size();
    }
    
    public int getWordFreq(int wordIndex){
        int freqindex = wMap.get(Integer.valueOf(wordIndex)).intValue();
        return wordFreq.get(freqindex).intValue();
    }
    
    public boolean isExist(int phraseIndex){
        if(wMap.get(Integer.valueOf(phraseIndex))!=null){
            return true;
        }else{
            return false;
        }
    }
    
    public int[] getPhraseIndex(){
        Integer[] indexList = phreases.toArray(new Integer[0]);
        int[] out = new int[indexList.length];
        for(int a=0;a<out.length;a++){
            out[a] = indexList[a].intValue();
        }
        return out;
    }
}
