/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanmsn.util;

import java.util.ArrayList;
import java.util.Arrays;
import scanmsn.data.MSNer;

/**
 * 利用 K-mean 演算法處理資料的物件，但取得距離的方法需要實作。.
 * @author Zero
 */
public abstract class Kmean {

    private int clusterSize = 2;
    private MSNer[] msner = null;
    private int limit = 100;  //反覆計算重心的次數
    private double variance = 4.0;  //資料集合之標準差在多少內停止計算
    private int[][] result = null;

    public void inputData(MSNer[] data, int clusterSize) {
        msner = data;
    }

    public boolean analysis() {
        result = seperate(msner);
        return false;
    }

    /**
     * 開始分群.
     * @param allData 需要分群的資料集合
     * @param limitation 計算重心的次數上限
     * @param variance 當變異數小於此值時則可停止計算傳回結果
     * @return 傳回分群結果。第一維：群集
     */
    private int[][] seperate(MSNer[] data) {
        int amount = data.length;
        int loopCounter = 0;
        boolean runNext = true;
        int errorLoop = 0;  //分群錯誤次數

        if (data != null && data.length > 1) {

            //build distance table
            double[][] distTable = new double[amount][amount];
            for (int a = 0; a < amount; a++) {
                for (int b = 0; b < amount; b++) {
                    //System.out.println("base=" + a + "  related=" + b);
                    distTable[a][b] = getElementDist(msner[a], msner[b]);
                }
            }

            for (int a = 0; a < amount; a++) {
                for (int b = 0; b < amount; b++) {
                    System.out.printf("%.3f  ", distTable[a][b]);
                }
                System.out.println("");
            }

            //設定重心，任選其中二筆資料當作中心
            double[] clusterCenter1 = new double[amount];
            double[] clusterCenter2 = new double[amount];
            double[][] cc1Record = new double[2][amount];
            double[][] cc2Record = new double[2][amount];
            int p1 = (int) (Math.random() * amount % amount);
            int p2 = (int) (Math.random() * amount % amount);
            while (p1 == p2) {
                p2 = (int) (Math.random() * amount % amount);
            }
            System.out.println("p1=" + p1 + "  p2=" + p2);
            for (int a = 0; a < amount; a++) {
                clusterCenter1[a] = distTable[p1][a];
                clusterCenter2[a] = distTable[p2][a];
            }

            //dump var
            dumpVar(clusterCenter1, "C1");
            dumpVar(clusterCenter2, "C2");


            double[] dist1 = new double[amount];  //distance with clusterCenter 1
            double[] dist2 = new double[amount];  //distance with clusterCenter 2

            ArrayList<Integer> cluster1 = new ArrayList<Integer>();
            ArrayList<Integer> cluster2 = new ArrayList<Integer>();
            cluster1.clear();
            cluster2.clear();
            //while (loopCounter < limit && (currVariance < 0 || currVariance > variance)) {
            while (loopCounter < 6 && runNext) {
                //依照兩重心的位置計算各點的距離並產生 distance table
                for (int a = 0; a < amount; a++) {
                    dist1[a] = getDistByClusterCenter(clusterCenter1, distTable[a]);
                    dist2[a] = getDistByClusterCenter(clusterCenter2, distTable[a]);
                }

                //dump var
//                dumpVar(dist1, "dist1");
//                dumpVar(dist2, "dist2");

                //依照 distance table 和重心將資料分成二堆
                for (int a = 0; a < amount; a++) {
                    if (dist1[a] <= dist2[a]) {
                        cluster1.add(Integer.valueOf(a));
                    } else {
                        cluster2.add(Integer.valueOf(a));
                    }
                }

                //dump var
                dumpVar(cluster1, "第 1 群");
                dumpVar(cluster2, "第 2 群");

                //其中一個群體若為 null
                if (cluster1.size() == 0 || cluster2.size() == 0) {
                    if (errorLoop < 10) {
                        errorLoop++;
                        System.out.println("分群錯誤，重新取重心");
                        p1 = (int) (Math.random() * amount % amount);
                        p2 = (int) (Math.random() * amount % amount);
                        while (p1 == p2) {
                            p2 = (int) (Math.random() * amount % amount);
                        }
                        System.out.println("p1=" + p1 + "  p2=" + p2);
                        for (int a = 0; a < amount; a++) {
                            clusterCenter1[a] = distTable[p1][a];
                            clusterCenter2[a] = distTable[p2][a];
                        }

                        //dump var
                        dumpVar(clusterCenter1, "C1");
                        dumpVar(clusterCenter2, "C2");
                        
                        //clear data
                        cluster1.clear();
                        cluster2.clear();
                        continue;
                    } else {
                        //錯誤次數到達上限，直接傳回未分群結果
                        System.out.println("超過錯誤次數上限");
                        break;
                    }
                }

                //將二堆資料的重心計算出來
                for (int a = 0; a < amount; a++) {
                    clusterCenter1[a] = 0;
                    clusterCenter2[a] = 0;
                }
                //cluster center 1
                for (int a = 0; a < cluster1.size(); a++) {
                    for (int x = 0; x < amount; x++) {
                        clusterCenter1[x] += distTable[cluster1.get(a).intValue()][x];
                    }
                }
                for (int a = 0; a < amount; a++) {
                    clusterCenter1[a] /= cluster1.size();
                }
                //cluster center 2
                for (int a = 0; a < cluster2.size(); a++) {
                    for (int x = 0; x < amount; x++) {
                        clusterCenter2[x] += distTable[cluster2.get(a).intValue()][x];
                    }
                }
                for (int a = 0; a < amount; a++) {
                    clusterCenter2[a] /= cluster2.size();
                }

                //dump var
                System.out.println("新的重心");
                dumpVar(clusterCenter1, "C1");
                dumpVar(clusterCenter2, "C2");

                //break;

                //如果連續 3 次重心都沒有在改變則 runFlag = false
                if (isSame(cc1Record, clusterCenter1) == true && isSame(cc2Record, clusterCenter2)) {
                    System.out.println("三次重心沒有改變，中斷分群");
                    runNext = false;
                } else {
                    cc1Record[0] = Arrays.copyOf(cc1Record[1], cc1Record[1].length);
                    cc1Record[1] = Arrays.copyOf(clusterCenter1, clusterCenter1.length);
                    cc2Record[0] = Arrays.copyOf(cc2Record[1], cc2Record[1].length);
                    cc2Record[1] = Arrays.copyOf(clusterCenter2, clusterCenter2.length);
                    cluster1.clear();
                    cluster2.clear();
                    loopCounter++;
                }
            }

            //產生預備輸出的資料
            ArrayList<ArrayList> out = new ArrayList<ArrayList>();

            if (errorLoop < 10) {
                //檢查群集數量，超過設定值則再做一次分群
                if (cluster1.size() > clusterSize) {
                    System.out.println("第一組需重新分群");
                    //建立 MSNer[]
                    MSNer[] msnCluster = new MSNer[cluster1.size()];
                    for (int a = 0; a < cluster1.size(); a++) {
                        msnCluster[a] = data[cluster1.get(a).intValue()];
                    }
                    int[][] tmp = seperate(msnCluster);
                    for (int a = 0; a < tmp.length; a++) {
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        for (int b = 0; b < tmp[a].length; b++) {
                            list.add(Integer.valueOf(tmp[a][b]));
                        }
                        out.add(list);
                    }
                }else{
                    out.add(cluster1);
                }
                if (cluster2.size() > clusterSize) {
                    System.out.println("第二組需重新分群");
                    //建立 MSNer[]
                    MSNer[] msnCluster = new MSNer[cluster2.size()];
                    for (int a = 0; a < cluster2.size(); a++) {
                        msnCluster[a] = data[cluster2.get(a).intValue()];
                    }
                    int[][] tmp = seperate(msnCluster);
                    for (int a = 0; a < tmp.length; a++) {
                        ArrayList<Integer> list = new ArrayList<Integer>();
                        for (int b = 0; b < tmp[a].length; b++) {
                            list.add(Integer.valueOf(tmp[a][b]));
                        }
                        out.add(list);
                    }
                }else{
                    out.add(cluster2);
                }
            }else{
                //超過錯誤次數，直接輸出未分群結果
            }

            int[][] output = new int[out.size()][];
            for (int a = 0; a < out.size(); a++) {
                ArrayList<Integer> tmp = out.get(a);
                int[] array = new int[tmp.size()];
                for (int b = 0; b < tmp.size(); b++) {
                    array[b] = data[tmp.get(b).intValue()].getIndex();
                }
                output[a] = array;
            }
            System.out.println("return " + output.length + " cluster");
            return output;
        }

        //資料只有 1 筆
        System.out.println("return 1 cluster");
        if (data != null) {
            int[][] output = new int[1][1];
            output[0][0] = data[0].getIndex();
            return output;
        }
        //沒有資料
        System.out.println("return null");
        return null;

    }

    private boolean isSame(double[][] ccRecord, double[] currCC) {
        int size = ccRecord[0].length;
        boolean same = true;
        //dumpVar(ccRecord[0], "cc 0");
        //dumpVar(ccRecord[1], "cc 1");
        for (int a = 0; a < size; a++) {
            if (ccRecord[0][a] != ccRecord[1][a] || ccRecord[1][a] != currCC[a]) {
                same = false;
                break;
            }
        }
        return same;
    }

    private double getDistByClusterCenter(double[] center, double[] point) {
        double sum = 0.0;
        for (int a = 0; a < center.length; a++) {
            sum += Math.pow(center[a] - point[a], 2);
        }
        return Math.sqrt(sum);
    }

    private void dumpVar(double[] array, String name) {
        System.out.print(name + ": ");
        for (int a = 0; a < array.length; a++) {
            System.out.printf("%.3f ", array[a]);
        }
        System.out.println();
    }

    private void dumpVar(ArrayList<Integer> list, String name) {
        System.out.print(name + ": ");
        for (int a = 0; a < list.size(); a++) {
            System.out.print(list.get(a).intValue() + "  ");
        }
        System.out.println();
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setDeviation(float deviation) {
        this.variance = deviation;
    }

    public MSNer[][] getResult() {
        System.out.println("Kmean: " + result.length + " 群");
        MSNer[][] output = new MSNer[result.length][];
        for (int a = 0; a < result.length; a++) {
            int[] tmp = result[a];
            MSNer[] tmpMsn = new MSNer[tmp.length];
            for (int b = 0; b < tmp.length; b++) {
                tmpMsn[b] = msner[tmp[b]];
            }
            output[a] = tmpMsn;
        }
        return output;
    }

    public int getClusterSize() {
        return clusterSize;
    }

    public void setClusterSize(int clusterSize) {
        if(clusterSize < 2){
            this.clusterSize = 2;
        }
        this.clusterSize = clusterSize;
    }

    protected abstract double getElementDist(MSNer base, MSNer related);
}
