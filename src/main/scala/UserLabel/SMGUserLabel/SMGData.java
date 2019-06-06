package UserLabel.SMGUserLabel;

import com.alibaba.fastjson.JSON;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class SMGData {
    public static ArrayList<String> getData() throws IOException {

        StopRecognition s = new StopRecognition();

        s.insertStopWords("【");
        s.insertStopWords("，");
        s.insertStopWords(",");
        s.insertStopWords("】");
        s.insertStopWords("*");
        s.insertStopWords("Ԁ");
        s.insertStopWords("τ");
        s.insertStopWords("！");
        s.insertStopWords("ȁ");
        s.insertStopWords("Ξ");
        s.insertStopWords("。");
        s.insertStopWords("(");
        s.insertStopWords("）");
        s.insertStopWords("：");
        s.insertStopWords("(");
        s.insertStopWords(")");
        HashMap<String, String> strHashMap = new HashMap<String, String>();
        String stopWordTable = "data/StopWords/StopWords.txt";
        File f = new File(stopWordTable);
        FileInputStream fileInputStream = new FileInputStream(f);
        //读入停用词文件
        BufferedReader StopWordFileBr = new BufferedReader(new InputStreamReader(fileInputStream));
        String stopWord = null;
        for(; (stopWord = StopWordFileBr.readLine()) != null;){
            s.insertStopWords(stopWord);
        }
        StopWordFileBr.close();

        ArrayList<String> list=new ArrayList<String>();
        File file=new File("data/microwu/testdata/mt_00.log.spam.json.txt");
        FileReader fr=new FileReader(file);
        BufferedReader br=new BufferedReader(fr);



        String line=null;
        while ((line=br.readLine())!=null){
            String mobile=JSON.parseObject(line).getString("mobile");
            String content=JSON.parseObject(line).getString("smsContent");


//            Result terms = NlpAnalysis.parse(content).recognition(s);
//            Result terms = BaseAnalysis.parse(content).recognition(s);
//            Result terms = IndexAnalysis.parse(content).recognition(s);
            Result terms = ToAnalysis.parse(content).recognition(s);
//            Result terms = DicAnalysis.parse(content).recognition(s);
            Iterator<Term> iterator = terms.iterator();
            StringBuilder sb=new StringBuilder();
            Boolean flag=true;
            while (iterator.hasNext()){
                String word=iterator.next().getName();
                if(flag){
                    flag=false;
                }else {
                    sb.append(" ");
                }
                sb.append(word);
            }
            System.out.println(sb);
            list.add(mobile+":"+sb.toString());
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        Iterator<String> iterator = getData().iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}
