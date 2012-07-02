/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanmsn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import scanmsn.data.MSNer;
import scanmsn.data.MsnRecordReader;
import scanmsn.util.KmeanEz;
import scanmsn.util.PhraseDB;

/**
 *
 * @author Zero
 */
public class FlowCtl {

    private PhraseDB phraseDB = null;
    private String[] msnRecordContent = null;
    private String[] msnRecordName = null;
    private MSNer[] msner = null;
    private boolean isRunning = false;
    private String status = "";
    private String errorMessage = "";
    private MSNer[][] result = null;

    /**
     * 
     * @param folder 儲存對話記錄的資料夾位置
     * @return 對話記錄總資料量總數量
     */
    public int readMsnRecord(File folder) {
        int dataSize = 0;
        isRunning = true;
        status = "讀取對話記錄";

        //start to analysis
        Calendar startTime = Calendar.getInstance();
        File fList[] = folder.listFiles();
        LinkedList<File> xmlFile = new LinkedList<File>();
        for (int a = 0; a < fList.length; a++) {
            String name = fList[a].getName();
            //System.out.println(name);
            if (name.endsWith("xml") == true) {
                try {
                    //讀取前二行判定是否為對話記錄
                    //System.out.println(folder.getPath() + "\\" + name);
                    Scanner sc = new Scanner(new File(folder.getPath() + "\\" + name), "UTF-8");
                    if (sc.hasNext()) {
                        sc.nextLine();
                        if (sc.hasNext()) {
                            String tmp = sc.nextLine();
                            if (tmp.indexOf("MessageLog.xsl") != -1) {
                                //加入檔案列表
                                //System.out.println(tmp);
                                xmlFile.add(fList[a]);
                            }
                        }
                    }
                } catch (FileNotFoundException ex) {
                    //Logger.getLogger(ScanMSNView.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("Error occurs when reading: " + folder.getPath() + "\\" + name);
                    return -1;
                }
            }
        }

        //check if recoed is found
        if (xmlFile.size() < 1) {
            return -1;
        }

        //show record info
        msnRecordName = new String[xmlFile.size()];
        for (int a = 0; a < xmlFile.size(); a++) {
            msnRecordName[a] = new String(xmlFile.get(a).getName());
            System.out.println(xmlFile.get(a).getName());
        }

        //read XML
        MsnRecordReader read = new MsnRecordReader();
        msnRecordContent = new String[xmlFile.size()];
        String tmp;
        for (int a = 0; a < msnRecordContent.length; a++) {
            System.out.println("reading... " + (a + 1) + "/" + msnRecordContent.length + " => " + msnRecordName[a]);
            read.setPath(xmlFile.get(a));
            tmp = read.read();
            //System.out.println("Orig = " + tmp);
            msnRecordContent[a] = tmp;
            dataSize += tmp.length();
        }
        System.out.println("");
        isRunning = false;
        status = "";
        Calendar endTime = Calendar.getInstance();
        System.out.println("花費時間 " + (-1 * (startTime.getTimeInMillis() - endTime.getTimeInMillis()) / 1000) + " 秒");

        return dataSize;
    }

    public void doAnalysis(int clusterSize) {
        /*
         * 1. 檢查人數是否超過二人 XD
         * 2. Data Cleaning, 清除無用字詞
         * 3. Data Analysis, CKIP & 將斷詞資料儲存至 MSNer
         * 4. Data Cleaning, 移除全體重複字數最高的 15%
         * 5. Distancing, 計算字詞間的距離
         * 6. K-mean, 分群
         * 7. Organization, 顯示結果並刪除無用的資料
         */
        isRunning = true;
        //if (msnRecordContent.length > 2) {
        if (msnRecordContent.length > 0) {
            //清除無用字詞
            phraseDB = new PhraseDB();
            //System.out.println("清除無用字詞");
            int phraseCount = 0;
            Calendar startTime = Calendar.getInstance();
            for (int a = 0; a < msnRecordContent.length; a++) {
                msnRecordContent[a] = phraseDB.rmSpecial(msnRecordContent[a]);
                phraseCount += msnRecordContent[a].length();
                System.out.println("cleaning.... " + (a + 1) + "/" + msnRecordContent.length);
            }
            Calendar endTime = Calendar.getInstance();
            System.out.println("清除後的文字量 = " + phraseCount);
            System.out.println("花費時間 " + (-1 * (startTime.getTimeInMillis() - endTime.getTimeInMillis()) / 1000) + " 秒");

            //產生斷詞資料
            status = "產生斷詞資料";
            msner = new MSNer[msnRecordContent.length];
            phraseDB = new PhraseDB();
            startTime = Calendar.getInstance();
            for (int a = 0; a < msner.length; a++) {
                //將斷詞結果存入 MSNer
                msner[a] = new MSNer();
                msner[a].setIndex(a);
                msner[a].setId(msnRecordName[a]);
                System.out.println("\n\n處理資料 " + (a + 1) + "/" + msner.length + " :" + msnRecordName[a]);
                msnRecordContent[a].replaceFirst("^ +", "");
                int[] phraseList = phraseDB.parse(msnRecordContent[a]);
                if (phraseList != null) {
                    for (int n = 0; n < phraseList.length; n++) {
                        //System.out.println("n=" + n + " index=" + phraseList[n]);
                        msner[a].add(phraseList[n]);
                    }
                }
            }
            endTime = Calendar.getInstance();
            System.out.println("詞庫目前總共有斷詞 " + phraseDB.getPhraseAmount() + " 筆");
            System.out.println("花費時間 " + (-1 * (startTime.getTimeInMillis() - endTime.getTimeInMillis()) / 1000) + " 秒");

            JOptionPane.showMessageDialog(null, "斷詞完畢\n準備分群", "警告", JOptionPane.ERROR_MESSAGE);

            //K-mean
            startTime = Calendar.getInstance();
            KmeanEz km = new KmeanEz();
            km.inputData(msner, 10);
            km.setClusterSize(clusterSize);
            km.analysis();
            result = km.getResult();
            endTime = Calendar.getInstance();
            System.out.println("花費時間 " + (-1 * (startTime.getTimeInMillis() - endTime.getTimeInMillis()) / 1000) + " 秒");
        } else {
            status = "error";
            errorMessage = "資料數量不足";
        }
        isRunning = false;
    }

    public int getMsnRecordAmount() {
        if (msnRecordContent != null) {
            return msnRecordContent.length;
        } else {
            return 0;
        }
    }

    public void showResult(JTextArea textArea) {
        String outString = "";
        if (result != null) {
            for (int a = 0; a < result.length; a++) {
                outString += "第 " + (a + 1) + " 群：\n";
                for (int b = 0; b < result[a].length; b++) {
                    outString += result[a][b].getIndex() + " : " + result[a][b].getId() + "\n";
                }
                outString += "\n";
            }
        } else {
            outString += "斷詞錯誤";
        }
        textArea.setText(outString);
    }

    public String[] getStatus() {
        if (isRunning == true) {
            String[] out = new String[3];
            int count = 0;
            out[0] = status; //執行狀態
            for (int a = 0; a < msnRecordContent.length; a++) {
                count += msnRecordContent[a].length();
            }
            out[1] = String.valueOf(count); //對話記錄資料量

            //詞庫數量
            if (phraseDB != null) {
                out[2] = String.valueOf(phraseDB.getPhraseAmount());
            } else {
                out[2] = "0";
            }
        }
        return null;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int phraseCount() {
        if (phraseDB == null) {
            return 0;
        }
        return phraseDB.getPhraseAmount();
    }

    public String saveMSNDate(File file) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(msnRecordName.length + "\n");
            String tmp = "";
            for (int a = 0; a < msnRecordContent.length; a++) {
                tmp += msnRecordName[a] + "|+|";
                tmp += msnRecordContent[a];
                tmp += "\n";
                fw.write(tmp);
                tmp = "";
            }
            fw.close();
            return "finished";
        } catch (Exception ex) {
            System.out.print(ex);
            return "error occur when saving data";
        }
    }

    public String loadMSNData(File file) {
        System.out.println("loading data");
        try {
            Scanner sc = new Scanner(file);
            if (sc.hasNext() ) {
                String def = sc.next();
                int amount = 0;
                amount = Integer.parseInt(def);
                System.out.println("amount = " + amount);
                msnRecordContent = new String[amount];
                msnRecordName = new String[amount];
                for (int a = 0; a < amount; a++) {
                    String line = sc.next();
                    if (!line.equals("")) {
                        String[] tmp = line.split("|+|");
                        msnRecordName[a] = tmp[0];
                        msnRecordContent[a] = tmp[1];
                    }
                }
                return "finished loading";
            }
            return "file is empty";
        } catch (Exception ex) {
            System.out.println(ex);
            System.out.println("error occurs when load data");
            return "error occurs when load data";
        }
    }
}
