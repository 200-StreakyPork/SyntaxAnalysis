import java.util.Stack;

/**
 * Created by StreakyPork
 * on 2018/11/13.
 */
public class Analyzer {
    //状态栈
    private Stack<Integer> stateSta = new Stack<>();
    //规约栈
    private Stack<String> symbolSta = new Stack<>();
    //
    private boolean isReduction = true;

    private void initialize(){
        stateSta.push(0);
        symbolSta.push("#");
    }

    public void syntaxAnalyze(char[] inputBelt,String pptFileName,String cfgFileName){
        initialize();
        State stateOp = new State(pptFileName,cfgFileName);
        //String action;
        for(int i=0;i<inputBelt.length;i++){
            //输出分析树
            if(isReduction) {
                myPrintTree(inputBelt,i);
                isReduction = false;
            }

            int nowState = stateSta.peek();
            //状态匹配
            String tmp = stateOp.shiftState(nowState,inputBelt[i]);
            if(tmp==null){
                System.out.println("analyze failed!");
                return;
            }

            char[] op = tmp.toCharArray();
            //遇到S为移点操作，op=Si
            if(op[0]=='S'){
                //action = "Shift";
                //myPrint(inputBelt,i,action);
                stateSta.push(Integer.parseInt(op[1]+""));//新状态进状态栈
                symbolSta.push(inputBelt[i]+"");//读头下的字符进规约栈
            }

            //遇到r为规约操作，op[1]=ri
            else {
                //r0表示accept
                if(Integer.parseInt(op[1]+"")==0){
                    //action = "accept";
                    //myPrint(inputBelt,i,action);
                    System.out.println("analyze successfully!");
                    return;
                }
                String productStr = stateOp.getProduct(Integer.parseInt(op[1]+""));//规约对应的产生式
                if(productStr==null){
                    System.out.println("analyze failed!");
                }
                String[] product = productStr.split("%");
                //action = "Reduced by "+op[1]+":"+product[0]+"->"+product[1];
                //myPrint(inputBelt,i,action);

                String topElement="";
                int len = product[1].length();
                //获取栈顶的若干元素
                for(int j=0;j<len;j++){
                    topElement=symbolSta.pop()+topElement;
                }
                //比较栈顶的若干元素与产生式的右边是否相同，相同则进行规约
                if(topElement.equals(product[1])){
                    //状态栈出栈
                    for(int k=0;k<len;k++){
                        stateSta.pop();
                    }
                    //将产生式的左边压栈
                    symbolSta.push(product[0]);
                    //获取新状态
                    int newState = stateOp.gotoState(stateSta.peek(),Integer.parseInt(op[1]+""));
                    //将状态压栈
                    stateSta.push(newState);
                    if(newState==-1){
                        System.out.println("analyze failed!");
                    }
                    i--;
                }else{
                    System.out.println("analyze failed!");
                }
                isReduction = true;
            }
        }
    }

    private void myPrint(char[] inputBelt, int index, String action){
        String input = "";
        for(int i=index;i<inputBelt.length;i++){
            input+=(inputBelt[i]+"");
        }
        System.out.println("StateStack:"+intStaToString(stateSta)+"  SymbolStack:"+
                charStaToString(symbolSta)+"  input:"+input+"  action:"+action);
    }

    private void myPrintTree(char[] inputBelt, int index){
        String input = "";
        for(int i=index;i<inputBelt.length;i++){
            input+=(inputBelt[i]+"");
        }
        System.out.println(charStaToString(symbolSta)+input);
    }

    private String intStaToString(Stack<Integer> s){
        Stack<Integer> tmp = new Stack<>();
        String result="";
        while(!s.empty()){
            tmp.push(s.pop());
            result = tmp.peek()+""+result;
        }
        while(!tmp.empty()){
            s.push(tmp.pop());
        }
        return result;
    }

    private String charStaToString(Stack<String> s){
        Stack<String> tmp = new Stack<>();
        String result="";
        while(!s.empty()){
            tmp.push(s.pop());
            result=tmp.peek()+result;
        }
        while(!tmp.empty()){
            s.push(tmp.pop());
        }
        return result;
    }
}
