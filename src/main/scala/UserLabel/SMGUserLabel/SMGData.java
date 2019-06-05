package UserLabel.SMGUserLabel;

import com.alibaba.fastjson.JSON;

import java.io.*;
import java.util.ArrayList;

public class SMGData {
    public static ArrayList<String> getData() throws IOException {
        ArrayList<String> list=new ArrayList<String>();
        File file=new File("data/microwu/testdata/mt_00.log.spam.json.txt");
        FileReader fr=new FileReader(file);
        BufferedReader br=new BufferedReader(fr);

        String line=null;
        while ((line=br.readLine())!=null){
            String mobile=JSON.parseObject(line).getString("mobile");
            String content=JSON.parseObject(line).getString("smsContent");
            list.add(mobile+":"+content);

        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(getData());
    }
}
