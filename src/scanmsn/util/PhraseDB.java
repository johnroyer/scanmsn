/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package scanmsn.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 中文斷詞資料庫. 記錄所有中文字詞並記下使用率供查詢。
 * @author Zero
 */
public class PhraseDB {

    ArrayList<String> phrase = new ArrayList<String>();
    HashMap<String, Integer> pmap = new HashMap<String, Integer>();
    String spec = "";
    ArrayList<String> word = null;
    String rexEng = "";

    public PhraseDB() {
        //REGEX
        rexEng = "[a-zA-Z0-9]*";

        //半形、全形符號
        spec = "!@#$%^&*()-=+_{}[]:\\\";\'<>?/.,~`|";
        spec += "！＠＃＄％︿＆＊（）＿＋「」＼。，：、；～？﹒…『』‘’“”〝〞【】《》〔〕｛｝︵︶︹︺︷︸︻︼︿﹀︽︾﹁﹂﹃﹄　";
        spec += "☆▲╞╪╡〃≠〤○⊙◎●☆★□▼▽△◇◆♀♂﹡＊※§＠⊕㊣↑↓←→↖↗↙↘˙ˊˇˋ‥﹉﹍﹌﹏︴¯＿—∥∣▕／＼╳╱╲∕﹨";
        spec += "▁▂▃▄▅▆▇█◢◣▏▎▍▌▋▊▉▓◥◤";
        spec += "①②③④⑤⑥⑦⑧⑨⑩㈠㈡㈢㈣㈤㈥㈦㈧㈨㈩ⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩ〡〢〣〤〥＋－×÷√±＝≡≒≦≧＜＞∵∴∞～∩∪∫∮＆⊥∠∟⊿";
        spec += "﹩＄￥〒￠￡ぽ﹪℅％㏕㎜㎝㎞㏎㎡㎎㎏㏄°℃℉㏒㏑";
        spec += "ㄅㄆㄇㄈㄉㄊㄋㄌㄍㄎㄏㄐㄑㄒㄓㄔㄕㄖㄗㄘㄙㄧㄨㄩㄚㄛㄜㄝㄞㄟㄠㄡㄢㄣㄤㄥㄦ";
        spec += "一二三四五六七八九十百千萬";
        spec += "ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ";
        word = new ArrayList<String>();

        //常用無意義詞語
        word.add("傳輸已完成");
        word.add("傳送檔案");
        word.add("離開對話");
        word.add("加入對話");
        word.add("沒完沒了");
        word.add("手分手");
        word.add("先睡了");
        word.add("差不多");
        word.add("晚安");
        word.add("哈囉");
        word.add("再見");
        word.add("睡覺");
        word.add("感謝");
        word.add("只要");
        word.add("噗噗");
        word.add("幹嘛");
        word.add("加油");
        word.add("安安");
        word.add("可以");
        word.add("好像");
        word.add("知道");
        word.add("需要");
        word.add("對吼");
        word.add("打算");
        word.add("其實");
        word.add("這樣");
        word.add("比較");
        word.add("永遠");
        word.add("北七");
        word.add("白目");
        word.add("就是");
        word.add("什麼");
        word.add("掰");
        //動詞
        word.add("不是");
        word.add("是");
        //代名詞
        word.add("我們");
        word.add("他們");
        word.add("你們");
        word.add("他們");
        word.add("我");
        word.add("你");
        word.add("他");
        word.add("他");
        word.add("它");
        word.add("她");
        word.add("牠");
        word.add("其");
        word.add("們");
        //結構助詞
        word.add("的");
        word.add("地");
        word.add("得");
        //語氣助詞
        word.add("呵呵");
        word.add("喝喝");
        word.add("哇靠");
        word.add("靠北");
        word.add("哇");
        word.add("哩");
        word.add("啊");
        word.add("嗎");
        word.add("嘛");
        word.add("呢");
        word.add("吧");
        word.add("呀");
        word.add("唷");
        word.add("囉");
        word.add("阿");
        word.add("哈");
        word.add("嘿");
        word.add("咧");
        word.add("了");
        word.add("哼");
        word.add("噗");
        //關連詞
        word.add("因為");
        word.add("所以");
        word.add("然後");
        word.add("結果");
        word.add("但是");
        word.add("如果");
        //連接詞
        word.add("和");
        word.add("及");
        word.add("或");
        word.add("又");
        word.add("既");
        //嘆詞
        word.add("哎呀");
        word.add("喂");
        word.add("呦");
        word.add("嗨");
        word.add("哼");
        word.add("哦");
        //擬聲詞
        word.add("汪");
        word.add("喵");
    }

    /**
     * 讀取現有的斷詞資料庫.
     * @param path 斷詞資料檔案位置
     */
    public boolean loadDate(File path) {
        try {
            Scanner sc = new Scanner(path);
            while (sc.hasNext()) {
                String def = sc.nextLine();
                if (def.equals("ScanMSNPhraseDB")) {
                    while (sc.hasNext()) {
                        newPhrase(sc.nextLine());
                    }
                    return true;
                } else {
                    //非斷詞資料庫
                    return false;
                }
            }
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
            System.out.println(ex);
            Logger.getLogger(PhraseDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     * 儲存目前的斷詞資料.
     * @param path 斷詞資料檔案位置
     */
    public boolean saveDate(File path) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(path);
            fw.write("ScanMSNPhraseDB\n");
            for (int a = 0; a < phrase.size(); a++) {
                fw.write(phrase.get(a));
                fw.write('\n');
            }
            return true;
        } catch (IOException ex) {
            System.out.println(ex);
            Logger.getLogger(PhraseDB.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                Logger.getLogger(PhraseDB.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * 除去特殊字元。. 會去除的符號有半形、全形標點，以及網址、電子郵件、檔案位置的字串。
     * @param in
     * @return 除去特殊字元後的字串
     */
    public String rmSpecial(String in) {
        //Regex
        in = in.replaceAll(rexEng, "");

        //remove unused words
        for (int a = 0; a < word.size(); a++) {
            in = in.replaceAll(word.get(a), "");
        }

        //remove symbol
        char[] list = spec.toCharArray();
        for (int a = 0; a < list.length; a++) {
            in = in.replace(list[a], ' ');
        }

        in = in.replaceAll("  +", " ");
        return in;
    }

    /**
     * 斷詞.
     * @param in
     * @return
     */
    public int[] parse(String in) {
        //每次送出長度三千的文字，每次間格 5 秒鐘
        //若連線失敗則等候 30 秒重試

        //切割成等分
        String[] tmp = null;
        if (in.length() > 3000) {
            int amount = in.length() % 3000 == 0 ? in.length() / 3000 : (int) (in.length() / 3000) + 1;
            tmp = new String[amount];
            for (int a = 0; a < amount; a++) {
                tmp[a] = in.substring(3000 * a, 3000 * (a + 1) > in.length() ? in.length() : 3000 * (a + 1));
            }
        } else {
            tmp = new String[1];
            tmp[0] = new String(in);
        }

        //送出字串至 CKIP，空字串直接略過
        if (in.length() > 0) {
            ArrayList<String> parsed = new ArrayList<String>();
            int index = 0;
            CKIP ckip = new CKIP();
            while (true) {
                if (index < tmp.length && tmp[index].replaceAll(" ", "").length() > 0) {
                    String[] back = ckip.sendMessage(tmp[index]);
                    if (back != null) {
                        //收到斷詞資料
                        for (int a = 0; a < back.length; a++) {
                            parsed.add(back[a]);
                        }
                        index++;
                        try {
                            System.out.println("斷詞成功，等候 5 秒繼續");
                            Thread.sleep(5 * 1000);
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(PhraseDB.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Thread.sleep failed");
                            System.out.println(ex);
                        }
                    } else {
                        try {
                            //沒收到資料，等 5 秒鐘重試
                            System.out.println("無法使用 CKIP，等候 10 秒重試");
                            Thread.sleep(10 * 1000);
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(PhraseDB.class.getName()).log(Level.SEVERE, null, ex);
                            System.out.println("Thread.sleep failed");
                            System.out.println(ex);
                        }
                    }
                } else {
                    //斷詞完畢
                    break;
                }
            }

            //將 parsed 中的資料存入資料庫
            int[] outList = new int[parsed.size()];
            for (int a = 0; a < parsed.size(); a++) {
                outList[a] = newPhrase(parsed.get(a));
            //System.out.println("parsed: " + parsed.get(a) + " " + outList[a]);
            }
            return outList;
        }
        int[] n = new int[1];
        n[1] = newPhrase(" ");
        return n;
    }

    /**
     * 新增斷詞資料.
     * @param str
     */
    private int newPhrase(String str) {
        int phraseIndex = findPhrase(str);
        if (phraseIndex == -1) {
            phrase.add(str);
            int index = phrase.lastIndexOf(str);
            pmap.put(str, index);
            return phrase.lastIndexOf(str);
        } else {
            return phraseIndex;
        }
    }

    /**
     * 搜尋詞句.
     * @param str
     * @return
     */
    private int findPhrase(String str) {
        Integer result = pmap.get(str);
        if (result != null) {
            return result.intValue();
        }
        return -1;
    }

    /**
     * 使用詞庫索引取得斷詞.
     * @param id 詞庫索引
     * @return 斷詞
     */
    public String getPhraseByIndex(int index) {
        return phrase.get(index);
    }

    /**
     * 取得斷詞數量.
     * @return
     */
    public int getPhraseAmount() {
        return phrase.size();
    }
}
