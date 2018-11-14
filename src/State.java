import java.io.*;

/**
 * Created by StreakyPork
 * on 2018/11/13.
 */
public class State {
    private String pptFileName;
    private String cfgFileName;

    public State(String pptFileName,String cfgFileName){
        this.pptFileName = pptFileName;
        this.cfgFileName = cfgFileName;
    }

    /*
    通过PPT，获取当前状态要返回的移入或者规约，返回为Si或者ri
     */
    public String shiftState(int init, char input){
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(pptFileName));
            String StateI;

            //%%%%为分析表中Action与GOTO的分界线
            while(! (StateI=br.readLine()).equals("%%%%") ){
                String[] whole = StateI.split(" ");
                int nowState = Integer.parseInt(whole[0]);
                //找到目前状态
                if(nowState==init){
                    String[] termArray = whole[1].split("\\|");
                    for(int i=0;i<termArray.length;i++){
                        String[] terms = termArray[i].split("%");
                        char toCmp = (terms[0].toCharArray())[0];
                        //字符与读头指向的字符相匹配
                        if(toCmp==input){
                            return terms[1];
                        }
                    }
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    根据规约的序号找到相应的产生式，返回为：左%右 的形式
     */
    public String getProduct(int reduceIndex){
        BufferedReader brCfg;
        try {
            brCfg = new BufferedReader(new FileReader(cfgFileName));
            String lineCfg;
            int lineNum = -1;
            while( (lineCfg=brCfg.readLine()) !=null){
                lineNum++;
                if(lineNum == reduceIndex){//找到规约对应的产生式
                    return lineCfg.replace("=","%");
                }
            }
            brCfg.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    将符号栈和状态栈栈顶的若干元素出栈后，应当到达的新的将被压进状态栈的状态
     */
    public int gotoState(int initState,int reduceIndex){
        int result=-1;
        BufferedReader brPpt;//读ppt文件
        String[] product=getProduct(reduceIndex).split("%");
        try {
            brPpt = new BufferedReader(new FileReader(pptFileName));
            String left= product[0];
            String linePpt;
            while(!(brPpt.readLine().equals("%%%%"))){}//%%%%为分析表中Action与GOTO的分界线
            while((linePpt = brPpt.readLine())!=null){
                String[] whole = linePpt.split(" ");
                int nowState = Integer.parseInt(whole[0]);
                if(nowState==initState){//找到了对应目前状态的状态
                    String[] change = whole[1].split("\\|");
                    for(int i=0;i<change.length;i++){
                        String toCmp = change[i].split("%")[0];
                        if(toCmp.equals(left)){//找到了initState通过leftCode要GOTO的状态
                            result=Integer.parseInt(change[i].split("%")[1]);
                            return result;
                        }
                    }
                }

            }
            brPpt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
