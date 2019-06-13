package Main;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;

import java.io.*;
import Dialog.DownloadDialog;
import Dialog.UpdateDialog;

public class Update {
    String uri="https://api.github.com/repos/lamprose/UKiller/releases/latest";
    String updateStr;
    JFrameMain main;

    public Update(JFrameMain Main){
        main=Main;
    }

    public void CheckUpdate(){
        updateStr=getUpdateInfo();
        onPostResult();
    }

    protected String getUpdateInfo() {
        HttpClient httpclient = HttpClientBuilder.create().build();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                responseString = out.toString("utf-8");
                out.close();
            } else {
                // Close the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (Exception e) {
            return null;
        }
        return responseString;
    }

    protected void onPostResult() {
        String downloadUri="";
        try {
            JSONObject release = new JSONObject(updateStr);

            // Get current version
            String version = "1.4";

            String latestVersion = release.getString("tag_name");
            boolean isPreRelease = release.getBoolean("prerelease");
            if (!isPreRelease && version.compareToIgnoreCase(latestVersion) >= 0) {
                //Your version is ahead of or same as the latest.
                //System.out.println("已是最新版");
                new UpdateDialog(main,true,"已是最新版,无需更新");
            } else {
                String updateLog=release.getString("body");
                new UpdateDialog(main,true,updateLog);
                // Need update.
                downloadUri = release.getJSONArray("assets").getJSONObject(0).getString("browser_download_url");
                DownloadDialog download=new DownloadDialog(main,true,downloadUri);
                // Give up on the fucking DownloadManager. The downloaded apk got renamed and unable to install. Fuck.

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
