package Main;

import net.jimmc.jshortcut.JShellLink;
import java.io.File;

public class Startup {
    JShellLink link=null;
    String programPath;
    String linkPath;

    public Startup(){
        link = new JShellLink();
        link.setName("UKiller");     // 快捷方式名称，可以任意取
        programPath = System.getProperty("user.dir") + "\\UKiller.jar";
        linkPath = System.getProperty("user.home") + "\\AppData\\Roaming\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\";
    }

    public void createLink(){
        link.setFolder(linkPath);       // 快捷方式存放地址
        link.setPath(programPath);      // 快捷方式指向该程序地址
        link.save();
    }

    public void deleteLink(){
        File file = new File(linkPath + link.getName() + ".lnk");
        if(file.exists())
            file.delete();
    }

}
