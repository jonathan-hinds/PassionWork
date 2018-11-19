import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpDownloadUtility {
    private static final int BUFFER_SIZE = 4096;

    /**
     * Downloads a file from a URL
     * @param fileURL HTTP URL of the file to be downloaded
     * @param saveDir path of the directory to save the file
     * @throws
     */
    public static void downloadFile(String fileURL, String saveDir, String fileName) throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
        httpConn.setReadTimeout(8000);
        httpConn.setInstanceFollowRedirects(true);
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            download(httpConn, saveDir, fileName);
        } else if(responseCode == HttpURLConnection.HTTP_MOVED_TEMP || responseCode == HttpURLConnection.HTTP_MOVED_PERM){
            boolean redirect = true;
            while(redirect) {
                System.out.println("redirected");
                System.out.println(responseCode);
                //handle one redirect
                String newURl = httpConn.getHeaderField("Location");
                String cookies = httpConn.getHeaderField("Set-Cookie");

                url = new URL(newURl);
                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
                httpConn.addRequestProperty("Cookie", cookies);
                httpConn.setReadTimeout(8000);
                httpConn.setInstanceFollowRedirects(true);
                //response code of the new connection
                responseCode = httpConn.getResponseCode();
                if(responseCode != HttpURLConnection.HTTP_MOVED_PERM && responseCode != HttpURLConnection.HTTP_MOVED_TEMP){
                    redirect = false;
                    System.out.println(responseCode);
                }
            }
            download(httpConn, saveDir, fileName);
        }else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
    }

    private static void download(HttpURLConnection httpConn, String saveDir, String fileName){
        try {
            //String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
        } catch(IOException e){
            e.printStackTrace();
        }

        System.out.println("File downloaded");
    }
}