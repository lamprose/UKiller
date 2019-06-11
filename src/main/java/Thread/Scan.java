import javax.swing.*;
import java.io.*;

import org.ini4j.Profile;
import org.ini4j.Wini;

public class Scan implements Runnable {
    private String diskPath;
    private JTextArea logComponents;
    private int setting=0;
    public Scan(String DiskPath,JTextArea LogComponents,int Setting){
        diskPath=DiskPath;
        logComponents=LogComponents;
        setting=Setting;
        logComponents.setText("");
    }
    @Override
    public void run() {
        scanDisk();
    }
    public void scanDisk(){
        logComponents.append("开始扫描.......\n");
        File autoRunFile=new File(diskPath+File.separator+"autorun.inf");
        if(!autoRunFile.exists()){
            logComponents.append("扫描完成.\n\n");
            logComponents.append("------------------------------扫描报告------------------------------\n");
            logComponents.append("ERROR:"+autoRunFile.getPath()+"文件不存在!\n");
            logComponents.append("------------------------------扫描完成------------------------------\n\n\n");
            return;
        }
        try{
            File autoRunFileLower=toLower(autoRunFile);
            Wini ini=new Wini(autoRunFileLower);
            Profile.Section section=ini.get("autorun");
            String[] virusPath=new String[3];
            int[] virusNum={0,0,0,0};
            boolean haveVirus=false;
            boolean deleteVirus=false;
            logComponents.append("------------------------------扫描报告------------------------------\n");
            if(section==null){
                logComponents.append("ERROR:"+autoRunFile.getPath()+"不存在病毒\n");
                logComponents.append("------------------------------扫描完成------------------------------\n\n\n");
                return;
            }
            else {
                virusPath[0]=ini.get("autorun","open").trim();
                virusPath[1]=ini.get("autorun","shell\\open\\command").trim();
                virusPath[2]=ini.get("autorun","shell\\explore\\command").trim();
                logComponents.append("磁盘启动时运行命令行:"+virusPath[0]+"\n");
                logComponents.append("磁盘启动时运行文件:"+ini.get("autorun","shellexecute").trim()+"\n");
                logComponents.append("磁盘右键菜单执行命令行:"+virusPath[1]+"\n");
                logComponents.append("磁盘右键菜单文本:"+ini.get("autorun","shell\\open")+"\n");
                logComponents.append("磁盘右键资源管理器菜单文本:"+ini.get("autorun","shell\\explore")+"\n");
                logComponents.append("磁盘右键资源管理器时运行的程序:"+virusPath[2]+"\n");
                logComponents.append("------------------------------扫描完成------------------------------\n\n\n");
                for(int i=0;i<3;i++){
                    if(virusPath[i]==null||"".equals(virusPath[i])){
                        virusNum[0]++;
                    }else{
                        haveVirus=true;
                        virusNum[checkVirusType(virusPath[i])]++;
                        if((setting>>1)%2==1){
                            File virusFile=new File(diskPath+File.separator+virusPath[i]);
                            if(virusFile.exists()&&virusFile.isFile()){
                                virusFile.delete();
                                if(!virusFile.exists())
                                    deleteVirus=true;
                            }
                        }
                    }
                }
                if(!haveVirus)
                    logComponents.append("病毒不存在!\n");
                else {
                    logComponents.append("------------------------------病毒报告------------------------------\n");
                    logComponents.append("总共有"+(virusNum[2]+virusNum[3])+"个病毒,其中I型病毒"+virusNum[2]+"个,II型病毒"+virusNum[3]+"个.\n");
                    logComponents.append("------------------------------报告结束------------------------------\n");
                }
            }
            if(deleteVirus){
                logComponents.append("病毒调用文件已自动删除！\n");
            }
            if((setting>>1)%2==1){
                autoRunFile.delete();
                if(!autoRunFile.exists())
                    logComponents.append("autorun.inf已经自动删除！\n\n\n");
            }

            autoRunFileLower.delete();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    private File toLower(File source) throws Exception{

        FileInputStream fis = new FileInputStream(source);
        InputStreamReader isr = new InputStreamReader(fis,"utf-8");
        BufferedReader reader = new BufferedReader(isr);
        File target=new File(diskPath+File.separator+"autorun_lower.inf");
        if(!target.exists())
            target.createNewFile();
        FileOutputStream fos = new FileOutputStream(target,false);
        OutputStreamWriter osw = new OutputStreamWriter(fos,"utf-8");
        BufferedWriter writer = new BufferedWriter(osw);
        String line = null;
        while((line = reader.readLine()) != null){
            writer.append(line.toLowerCase()).append("\r\n");
        }
        writer.close();
        reader.close();
        return target;
    }
    private int checkVirusType(String virusFileName){
        if(virusFileName==null)
            return 0;
        if(virusFileName.indexOf(".vbs")!=-1||virusFileName.indexOf(".bat")!=-1)
            return 3;
        else if(virusFileName.indexOf(".exe")!=-1||virusFileName.indexOf(".pif")!=-1||virusFileName.indexOf(".scr")!=-1||virusFileName.indexOf(".com")!=-1)
            return 2;
        return 1;
    }
}
