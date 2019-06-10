package Thread;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public class Download implements Runnable {
    String url;
    String path;
    int unitProgress;
    public Download(String Uri,String Path){
        url=Uri;
        path=Path;
        unitProgress=0;
    }
    public int getPercent(){
        return unitProgress;
    }
    @Override
    public void run(){
        System.out.println("下载中...");
        InputStream inputStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(10 * 1000);
            File file = new File(path);
            if (!file.getParentFile().exists())
                file.getParentFile().mkdir();
            if (file.exists())
                file.delete();
            file.createNewFile();
            int responseCode = urlConnection.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                inputStream = urlConnection.getInputStream();
                int len = 0;
                byte[] data = new byte[4096];
                int progres = 0; //用于保存当前进度（具体进度）
                int maxProgres = urlConnection.getContentLength();//获取文件
                randomAccessFile = new RandomAccessFile(file, "rwd");
                randomAccessFile.setLength(maxProgres);//设置文件大小
                int unit = maxProgres / 100;//将文件大小分成100分，每一分的大小为unit

                while (-1 != (len = inputStream.read(data))) {
                    randomAccessFile.write(data, 0, len);
                    progres += len;//保存当前具体进度
                    int temp = progres / unit; //计算当前百分比进度
                    if (temp >= 1 && temp > unitProgress) {//如果下载过程出现百分比变化
                        unitProgress = temp;//保存当前百分比
                    }
                }
                inputStream.close();
                System.out.println("下载完成...");
            } else {
                System.out.println("服务器异常...");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != randomAccessFile) {
                    randomAccessFile.close();
                }
            }catch (Exception e1){
                e1.printStackTrace();
            }

        }
    }
}
