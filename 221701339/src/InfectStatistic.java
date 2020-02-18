import java.io.*;
import java.text.Collator;
import java.util.*;
import java.util.regex.*;

class InfectStatistic {
	static String logPath = "";
	static String outPath = "";
	static String date = "";
	static List<String> province = new ArrayList<String>();
	static List<String> type = new ArrayList<String>();
	static List<DataProcessing.InfectSituation>	handledList = new ArrayList<DataProcessing.InfectSituation>();
    static class CommandLine {
        static Command command;
        static Arguments arguments;
        static class Command {
        	static boolean isList;      
        }
        static class Arguments {
        	static boolean isLog;
        	static String logPath;
        	static boolean isOut;
        	static String outPath;
        	static boolean isDate;
        	static String date;
        	static boolean isType;
        	static ArrayList<String> type;
        	static boolean isProvince;
        	static ArrayList<String> province;
        }
        static public void getCommandLine(List<String> list) {
            for (int i = 0; i < list.size(); i++) {
                String argument = list.get(i);
                if(argument.equals("list"))
                	Command.isList = true;
                if(argument.equals("-log")){
                	arguments.isLog = true;
                	arguments.logPath = list.get(i + 1);
                }
                if(argument.equals("-out")){
                	arguments.isOut = true;
                    arguments.outPath=list.get(i + 1);
                }
                
                if(argument.equals("-date")){
                	arguments.isDate = true;
                    arguments.date = list.get(i + 1);
                }
                if(argument.equals("-type")){
                	arguments.isType = true;
                    ArrayList<String> arrayList = new ArrayList<String>();
                    for (int j = i; j < list.size(); j++) {
                        String type = list.get(j);
                        if (!type.substring(0, 1).equals('-')) {
                            if (type.equals("ip")) {
                                arrayList.add("ip");
                            }
                            if (type.equals("sp")) {
                                arrayList.add("sp");
                            }
                            if (type.equals("cure")) {
                                arrayList.add("cure");
                            }
                            if (type.equals("dead")) {
                                arrayList.add("dead");
                            }
                        }
                        else
                        	break;
                    }
                    arguments.type = arrayList;
                }
                if(argument.equals("-province")){
                	 arguments.isProvince = true;
                     ArrayList<String> provinceList = new ArrayList<String>();
                     for (int j = i+1; j < list.size(); j++) {
                         String province = list.get(j);
                         if (!province.substring(0, 1).equals('-')) {
                         	provinceList.add(province);
                         }
                     }
                     arguments.province = provinceList;
                }
            }
            CommandLine.command = command;
            CommandLine.arguments = arguments;
            getArguments();
        }
       static void getArguments(){
    	   if(CommandLine.command.isList){
            	if(CommandLine.arguments.isLog){
            		logPath = CommandLine.arguments.logPath;
            	}
            	else{
            		System.out.println("必须存在log命令");
            	}
            	if(CommandLine.arguments.isOut){
            		outPath = CommandLine.arguments.outPath;
            	}
            	else{
            		System.out.println("必须存在out命令");
            	}
            	if(CommandLine.arguments.isProvince){
            		province.addAll(CommandLine.arguments.province);
            	}
            	else{
            		province = null;
            	}
            	if(CommandLine.arguments.isType){
            		type.addAll(CommandLine.arguments.type);
            	}
            	else{
            		type = null;
            	}
            	if(CommandLine.arguments.isDate){
            		date = arguments.date;
            	}
            }
            else
            	System.out.println("命令错误");
    	   System.out.println(logPath);
       }
       static List<String> getList(String args[]){
       	List<String> list = new ArrayList<String>();
       	int pos = 0;
       	boolean flag = false;
       	boolean flag1 = false;
           for (String temp : args) {
               list.add(temp);
           }
           for (int i  = 0; i < list.size(); i++) {
               String arg = list.get(i);
               if (arg.equals("-date")) {
                   String date = list.get(i + 1);
                   if (date.substring(0, 1).equals("-")) {
                   	pos = i;
                   	flag = true;
                   }
               }
           }
           for (int i  = 0; i < list.size(); i++) {
               String arg = list.get(i);
               if (arg.equals("-date")) {
                  flag1 = true;
               }
           }
           if((flag || !flag1)&&(pos > 0))
           	list.add(pos + 1, "default"); 
           else if((flag || !flag1)&&(pos == 0))
           	date = "default";
       	return list;
       }
    }  
    static class StrRegular{
		static String s1 = "(.*) 新增 感染患者 (\\d*)人";
		static String s2 = "(.*) 新增 疑似患者 (\\d*)人";
		static String s3 = "(.*) 感染患者 流入 (.*) (\\d*)人";
		static String s4 = "(.*) 疑似患者 流入 (.*) (\\d*)人";
		static String s5 = "(.*) 死亡 (\\d*)人";
		static String s6 = "(.*) 治愈 (\\d*)人";
		static String s7 = "(.*) 疑似患者 确诊感染 (\\d*)人";
		static String s8 = "(.*) 排除 疑似患者 (\\d*)人";
		static DataProcessing.InfectSituation ipSituation(String text){
			DataProcessing.InfectSituation infect = new DataProcessing.InfectSituation();
			Pattern provincePattern = Pattern.compile("(.*) 新增");
            Pattern numberPattern = Pattern.compile("感染患者 (.*)人");
            Matcher provinceMatcher = provincePattern.matcher(text);
            Matcher numberMatcher = numberPattern.matcher(text);
            if (provinceMatcher.find()) {
                infect.province = provinceMatcher.group(1);
            }
            if (numberMatcher.find()) {
            	infect.ip = Integer.parseInt(numberMatcher.group(1));
            }
			return infect;
		}
		static DataProcessing.InfectSituation spSituation(String text){
			DataProcessing.InfectSituation infect = new DataProcessing.InfectSituation();
			Pattern provincePattern = Pattern.compile("(.*) 新增");
            Pattern numberPattern = Pattern.compile("疑似患者 (.*)人");
            Matcher provinceMatcher = provincePattern.matcher(text);
            Matcher numberMatcher = numberPattern.matcher(text);
            if (provinceMatcher.find()) {
                infect.province = provinceMatcher.group(1);
            }
            if (numberMatcher.find()) {
            	infect.sp = Integer.parseInt(numberMatcher.group(1));
            }
			return infect;
		}
		static List<DataProcessing.InfectSituation> ipMoveSituation(String text){
			List<DataProcessing.InfectSituation> list =new ArrayList<DataProcessing.InfectSituation>();
			DataProcessing.InfectSituation infect = new DataProcessing.InfectSituation();
			DataProcessing.InfectSituation infect1 = new DataProcessing.InfectSituation();
			Pattern provincePattern = Pattern.compile("(.*) 感染患者");
			Pattern provincePattern1 = Pattern.compile("流入 (.*) \\d+人");
            Pattern numberPattern = Pattern.compile("\\W+ (.*)人");
            Matcher provinceMatcher = provincePattern.matcher(text);
            Matcher provinceMatcher1 = provincePattern1.matcher(text);
            Matcher numberMatcher = numberPattern.matcher(text);
            if (provinceMatcher.find()) {
                infect.province= provinceMatcher.group(1);
            }
            if (provinceMatcher1.find()) {
                infect1.province= provinceMatcher1.group(1);
            }
            if (numberMatcher.find()) {
            	infect.ip = - Integer.parseInt(numberMatcher.group(1));
            	infect1.ip = Integer.parseInt(numberMatcher.group(1));
            }
            list.add(infect);
            list.add(infect1);
			return list;
		}
		static List<DataProcessing.InfectSituation> spMoveSituation(String text){
			List<DataProcessing.InfectSituation> list =new ArrayList<DataProcessing.InfectSituation>();
			DataProcessing.InfectSituation infect = new DataProcessing.InfectSituation();
			DataProcessing.InfectSituation infect1 = new DataProcessing.InfectSituation();
			Pattern provincePattern = Pattern.compile("(.*) 疑似患者");
			Pattern provincePattern1 = Pattern.compile("流入 (.*) \\d+人");
            Pattern numberPattern = Pattern.compile("\\W+ (.*)人");
            Matcher provinceMatcher = provincePattern.matcher(text);
            Matcher provinceMatcher1 = provincePattern1.matcher(text);
            Matcher numberMatcher = numberPattern.matcher(text);
            if (provinceMatcher.find()) {
                infect.province= provinceMatcher.group(1);
            }
            if (provinceMatcher1.find()) {
                infect1.province= provinceMatcher1.group(1);
            }
            if (numberMatcher.find()) {
            	infect.sp = - Integer.parseInt(numberMatcher.group(1));
            	infect1.sp = Integer.parseInt(numberMatcher.group(1));
            }
            list.add(infect);
            list.add(infect1);
			return list;
		}
		static DataProcessing.InfectSituation deadSituation(String text){
			DataProcessing.InfectSituation infect = new DataProcessing.InfectSituation();
			Pattern provincePattern = Pattern.compile("(.*) 死亡");
            Pattern numberPattern = Pattern.compile("死亡 (.*)人");
            Matcher provinceMatcher = provincePattern.matcher(text);
            Matcher numberMatcher = numberPattern.matcher(text);
            if (provinceMatcher.find()) {
                infect.province= provinceMatcher.group(1);
            }
            if (numberMatcher.find()) {
            	infect.dead = Integer.parseInt(numberMatcher.group(1));
            	infect.ip = - infect.dead;
            }
			return infect;
		}
		static DataProcessing.InfectSituation cureSituation(String text){
			DataProcessing.InfectSituation infect = new DataProcessing.InfectSituation();
			Pattern provincePattern = Pattern.compile("(.*) 治愈");
            Pattern numberPattern = Pattern.compile("治愈 (.*)人");
            Matcher provinceMatcher = provincePattern.matcher(text);
            Matcher numberMatcher = numberPattern.matcher(text);
            if (provinceMatcher.find()) {
                infect.province= provinceMatcher.group(1);
            }
            if (numberMatcher.find()) {
            	infect.ip = - Integer.parseInt(numberMatcher.group(1));
            	infect.cure = Integer.parseInt(numberMatcher.group(1));
            }
			return infect;
		}
		static DataProcessing.InfectSituation spToIpSituation(String text){
			DataProcessing.InfectSituation infect = new DataProcessing.InfectSituation();
			Pattern provincePattern = Pattern.compile("(.*) 疑似患者");
            Pattern numberPattern = Pattern.compile("确诊感染 (.*)人");
            Matcher provinceMatcher = provincePattern.matcher(text);
            Matcher numberMatcher = numberPattern.matcher(text);
            if (provinceMatcher.find()) {
                infect.province= provinceMatcher.group(1);
            }
            if (numberMatcher.find()) {
            	infect.sp = - Integer.parseInt(numberMatcher.group(1));
            	infect.ip = - infect.sp;
            }
			return infect;
		}
		static DataProcessing.InfectSituation spRemoveSituation(String text){
			DataProcessing.InfectSituation infect = new DataProcessing.InfectSituation();
			Pattern provincePattern = Pattern.compile("(.*) 排除");
            Pattern numberPattern = Pattern.compile("疑似患者 (.*)人");
            Matcher provinceMatcher = provincePattern.matcher(text);
            Matcher numberMatcher = numberPattern.matcher(text);
            if (provinceMatcher.find()) {
                infect.province= provinceMatcher.group(1);
            }
            if (numberMatcher.find()) {
            	infect.sp = - Integer.parseInt(numberMatcher.group(1));
            }
			return infect;
		}
	}	
    static class DataProcessing {
        static class InfectSituation{
            String province;
            int ip = 0;
            int sp = 0;
            int cure = 0;
            int dead = 0;
            public InfectSituation(String province, int ip, int sp, int cure, int dead) {
                super();
                this.province = province;
                this.ip = ip;
                this.sp = sp;
                this.dead = dead;
            }
            public InfectSituation(){
            }
            @Override
            public String toString(){
                return province + " " + ip +  " " + sp  + " "  + cure + " " + dead;
            }
            public String outPutAll(){
                return province + " " + "感染患者" + ip + "人" + " " + "疑似患者" + sp + "人" + " " +"治愈" + cure + "人" + " " + "死亡" + dead + "人";
            }
        }
        static class ListProcessing{
            public static List<InfectSituation> mergeList (List<InfectSituation> infectList, boolean flag) {
            	List<InfectSituation> list = new ArrayList<InfectSituation>();
                InfectSituation situation1 = new InfectSituation();
                InfectSituation situation2 = new InfectSituation();
                InfectSituation situation3 = new InfectSituation();
                situation3.province = "全国";
                situation1 = infectList.get(0);
                for (InfectSituation infect : infectList) {                                                                                                         
                	situation3.ip = situation3.ip + infect.ip;
                	situation3.sp = situation3.sp + infect.sp;
                	situation3.cure = situation3.cure + infect.cure;
                	situation3.dead = situation3.dead + infect.dead;
                	System.out.println(11211);
                    if (compare(infect, situation1)) {
                        if (situation2.province != null ) {
                        	situation2.ip = situation2.ip + infect.ip;
                        	situation2.sp = situation2.sp + infect.sp;
                        	situation2.cure = situation2.cure + infect.cure;
                        	situation2.dead = situation2.dead + infect.dead;
                        }
                        else if (situation2.province == null) {
                        	situation2 =situation1;
                        }
                    }
                    else {
                        list.add(situation2);
                        situation1 = infect;
                        situation2 = situation1;
                    }
                }
                System.out.println(22222222);
                if (!list.contains(situation1)) {
                    list.add(situation1);
                }
                if (flag != true) {
                	situation3.ip = situation3.ip / 2;
                	situation3.sp = situation3.sp / 2;
                	situation3.dead = situation3.dead / 2;
                	situation3.cure = situation3.cure / 2;
                }
                list.add(situation3);
                return list;
            }
            public static boolean compare(Object o1, Object o2) {
                InfectSituation a = (InfectSituation) o1;
                InfectSituation a2 = (InfectSituation) o2;

                if (a.province.equals(a2.province)) {
                    return true;
                }
                return false;
            }
            static class comparator implements  Comparator {
                public int compare(Object object1, Object object2) {
                    InfectSituation a1 = (InfectSituation) object1; 
                    InfectSituation a2 = (InfectSituation) object2;
                    return a1.province.compareTo(a2.province);
                }
            }
        }
        static List<DataProcessing.InfectSituation> dataGain(String str,List<DataProcessing.InfectSituation> infectList){
        	 if (str.matches(StrRegular.s1)) {
                 infectList.add(StrRegular.ipSituation(str));
             }
             else if (str.matches(StrRegular.s2)) {
                 infectList.add(StrRegular.spSituation(str));
             }
             else if (str.matches(StrRegular.s3)) {
                 infectList.addAll(StrRegular.ipMoveSituation(str));
             }
             else if (str.matches(StrRegular.s4)) {
                 infectList.addAll(StrRegular.spMoveSituation(str));
             }
             else if (str.matches(StrRegular.s5)) {
                 infectList.add(StrRegular.deadSituation(str));
             }
             else if (str.matches(StrRegular.s6)) {
                 infectList.add(StrRegular.cureSituation(str));
             }
             else if (str.matches(StrRegular.s7)) {
                 infectList.add(StrRegular.spToIpSituation(str));
             }
             else if (str.matches(StrRegular.s8)) {
                 infectList.add(StrRegular.spRemoveSituation(str));
             }
        	 return infectList;
        }
        public static void sortProvince(){
            List<DataProcessing.InfectSituation> list = DataInput.dataInput(logPath);
            System.out.println(list);
            List<DataProcessing.InfectSituation> sortedList = new ArrayList<DataProcessing.InfectSituation>();
            Comparator<Object> regular = Collator.getInstance(java.util.Locale.CHINA);
            String[] newList = new String[list.size()];
            String temp = null;
            for(int i = 0 ; i<= list.size()-1 ; i++){
                if (list.get(i).province.equals("全国")){
                    temp = list.get(i).province + " " + list.get(i).ip + " " + list.get(i).sp + " " + 
                    		list.get(i).cure+ " " + list.get(i).dead;
                }
                newList[i] = list.get(i).toString();
            }
            DataProcessing.InfectSituation wholeInfectSituation = new DataProcessing.InfectSituation();
            String[] whole = temp.split(" ");
            wholeInfectSituation.province = whole[0];
            wholeInfectSituation.ip = Integer.parseInt(whole[1]);
            wholeInfectSituation.sp = Integer.parseInt(whole[2]);
            wholeInfectSituation.cure = Integer.parseInt(whole[3]);
            wholeInfectSituation.dead=Integer.parseInt(whole[4]);
            sortedList.add(wholeInfectSituation);
            Arrays.sort(newList,regular);
            for(String infect:newList){
            	DataProcessing.InfectSituation infectSituation = new DataProcessing.InfectSituation();
                String[] arr = infect.split(" ");
                if(arr[0].equals("全国"))
                    continue;
                infectSituation.province = arr[0];
                infectSituation.ip = Integer.parseInt(arr[1]);
                infectSituation.sp = Integer.parseInt(arr[2]);
                infectSituation.cure = Integer.parseInt(arr[3]);
                infectSituation.dead = Integer.parseInt(arr[4]);
                sortedList.add(infectSituation);
                }    
            handledList = sortedList;
        }
        public static List<String> getFiles(String path) {
			List<String> files = new ArrayList<String>();
			File file = new File(path);
			File[] filename = file.listFiles();
        		for (int i = 0; i < filename.length; i++) {
            		if (filename[i].isFile()) {
            		files.add(filename[i].toString());
            	}
        	}
        	return files;
    	}	
    }
    static class DataInput{
    	public static List<DataProcessing.InfectSituation> dataInput(String path)  {
    		StrRegular strRegular = new StrRegular();
            List<DataProcessing.InfectSituation> infectList = new ArrayList<DataProcessing.InfectSituation>();
            List<DataProcessing.InfectSituation> finalInfectList = new ArrayList<DataProcessing.InfectSituation>();
            List<DataProcessing.InfectSituation> infectList1 = new ArrayList<DataProcessing.InfectSituation>();
            File file = new File(path);
            if(file.isFile()) {
                BufferedReader reader = null;
                try {
                	String name = file.getPath();
                    int pos1 = name.lastIndexOf('\\');
                	int pos2 = name.indexOf(".");
                	name = name.substring(pos1+1, pos2);
                	System.out.println(name);
                    reader = new BufferedReader(new FileReader(file));
                    String str = null;
                    while ((str = reader.readLine()) != null) {
                    	infectList = DataProcessing.dataGain(str, infectList);
                    	System.out.println(str);
                    }
                    reader.close();
                    Collections.sort(infectList, new DataProcessing.ListProcessing.comparator());
                    int flag = 1;
                    if(!date.equals("default"))
                    {
                    	flag = name.compareTo(date);
                    }
                    boolean flag1 = false;
                    if(date.equals("default")||flag <= 0) {
                    	flag1 = true;   	
                    }
                    if(flag1){
                    	 finalInfectList = DataProcessing.ListProcessing.mergeList(infectList,true);
                    	 System.out.print(1111111111);
                    }
                   else{
                    	System.out.println("未找到指定日期文件");
                    	System.exit(0);
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                } 
                finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } 
                        catch (IOException e1) {
                        }
                    }
                }
            }
            else if(file.isDirectory()){
                List<String> filePaths = DataProcessing.getFiles(logPath);
                for(String filePath :filePaths){
                	int pos1 = filePath.lastIndexOf('\\');
                	int pos2 = filePath.indexOf(".");
                	String name = filePath.substring(pos1+1, pos2);
                	System.out.println(date);
                    System.out.println(name);
                    int flag = 1;
                    if(!date.equals("default"))
                    {
                    	flag = name.compareTo(date);
                    }
                    boolean flag1 = false;
                    if(date.equals("default")||flag <= 0) {
                    	flag1 = true;   	
                    }
                    else
                    	continue;
                    if(flag1)
                    	infectList1.addAll(dataInput(filePath));
                    else{
                    	System.out.println("未找到指定日期文件");
                    	System.exit(0);
                    }
                }

                Collections.sort(infectList1, new DataProcessing.ListProcessing.comparator());
                finalInfectList = DataProcessing.ListProcessing.mergeList(infectList1,false);
                //finalInfectList = infectList1;
            }
            return finalInfectList;
        }
    }
    static class DataOut {
        public static void dataOutput(){
            List<DataProcessing.InfectSituation> list = handledList;
            BufferedWriter out = null;
            String outPutText = "";
            try{
            	out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath, true)));
            	if(CommandLine.arguments.isProvince&&CommandLine.arguments.isType){
            		for (int i=0; i<list.size(); i++){
            			for (String getProvince : province) {
                            if (list.get(i).province.equals(getProvince)) {
                            	System.out.println(getProvince);
                            	for (String getType : type) {
                                    if (getType.equals("ip")) {
                                    	outPutText += " 感染患者" + list.get(i).ip + "人";
                                    }
                                    if (getType.equals("sp")) {
                                    	outPutText += " 疑似患者" + list.get(i).sp + "人";
                                    }
                                    if (getType.equals("cure")) {
                                    	outPutText += " 治愈" + list.get(i).cure + "人";
                                    }
                                    if (getType.equals("dead")) {
                                    	outPutText += " 死亡" + list.get(i).dead + "人";
                                    }
                                }
                            	System.out.println(outPutText);
                                out.write(list.get(i).province + outPutText + "\n");
                                outPutText = "";	
                            }
                    	}
            		}	
                }
                else if(!CommandLine.arguments.isProvince&&CommandLine.arguments.isType){
                	for (int i=0; i<list.size(); i++){
                		for (String getType : type) {
                            if (getType.equals("ip")) {
                            	outPutText += " 感染患者" + list.get(i).ip + "人";
                            }
                            else if (getType.equals("sp")) {
                            	outPutText += " 疑似患者" + list.get(i).sp + "人";
                            }
                            else if (getType.equals("cure")) {
                            	outPutText += " 治愈" + list.get(i).cure + "人";
                            }
                            if (getType.equals("dead")) {
                            	outPutText += " 死亡" + list.get(i).dead + "人";
                            }
                        }
                		System.out.println(outPutText);
                        out.write(list.get(i).province + outPutText +"\n");
                        outPutText = "";
                	}	
                }
                else if(CommandLine.arguments.isProvince&&!CommandLine.arguments.isType){
                	for (int i=0; i<list.size(); i++){
                		for(String getProvince : province){
                			 if (list.get(i).province.equals(getProvince)) {
                				 out.write(list.get(i).outPutAll() + "\n");
                			 }
                		}
                	}
                }
                else{
                	for (int i=0; i<list.size(); i++){
                		System.out.println(list.get(i));
                		out.write(list.get(i).outPutAll() + "\n");
                	}
                }        
                out.write("// 该文档并非真实数据，仅供测试使用" + "\n");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (out != null) {
                    try {
                        out.close();
                    }
                    catch (IOException e1) {
                    }
                }
            }
        }
    }
    public static class Test{
    	static String[] arg1 = {"list","-date", "-log" , "D:/log/","-out","D:/output.txt","-type","ip"};
    	static String[] arg2 = {"list","-date","2020-01-22", "-log" , "D:/log/","-out","D:/output.txt","-province","福建"};
    	static String[] arg3 = {"list","-date","2020-01-22", "-log" , "D:/log/","-out","D:/output.txt","-type","ip","-province","福建"};
    	static String[] arg4 = {"list","-date","2020-01-22", "-log" , "D:/log/","-out","D:/output.txt","-type","ip","sp"};
    	static String[] arg5 = {"list","-date","2020-01-23", "-log" , "D:/log/","-out","D:/output.txt","-type","ip"};
    	static String[] arg6 = {"list","-date","2020-01-23", "-log" , "D:/log/","-out","D:/output.txt","-province","福建"};
    	static String[] arg7 = {"list","-date", "-log" , "D:/log/","-out","D:/output.txt","-type","ip","-province","福建"};
    	static String[] arg8 = {"list","-date","2020-01-27", "-log" , "D:/log/","-out","D:/output.txt","-type","ip"};
    	static String[] arg9 = {"list","-date","2020-01-27", "-log" , "D:/log/","-out","D:/output.txt","-province","福建"};
    	static String[] arg10 = {"list","-date", "-log" , "D:/log/","-out","D:/output.txt","-type","ip","-province","福建"};
    	
    }
    public static void main(String[] args) throws IOException {
    	 List<String> list =new ArrayList<String>();
    	 String[] arg = Test.arg10;
         list = CommandLine.getList(arg);
         CommandLine.getCommandLine(list);
         System.out.println(CommandLine.arguments.isType);
         System.out.println(CommandLine.arguments.isProvince);
         DataProcessing.sortProvince();
         DataOut.dataOutput();
    }
}
