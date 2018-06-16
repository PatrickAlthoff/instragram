package de.hshl.softwareprojekt;

import android.graphics.Picture;
import android.icu.util.Output;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class HttpConnection extends AsyncTask<Void, Void, Void> {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static enum MODE {GET,PUT};
    private MODE mode;
    private String dstAddress, message = "", respond = "";
    private Object resultContainer;
    private List<Picture> pictureList = null;
    private String response;
    private Document document;

    HttpConnection(String dstAddress, Object resultContainer){
        this.dstAddress = dstAddress;
        this.resultContainer = resultContainer;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public void setMode(MODE mode){
        this.mode = mode;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        try {
            URL url = new URL(this.dstAddress);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setInstanceFollowRedirects(true);

            if (this.mode == MODE.PUT) {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoInput(true);

                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                outputStream.write(this.message.getBytes());
                outputStream.flush();
            }
            else{
                urlConnection.setRequestMethod("GET");
            }



            int responseCode = urlConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK){
                inputStream = urlConnection.getInputStream();

                if (this.mode == MODE.PUT) {
                    inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    bufferedReader = new BufferedReader(inputStreamReader);

                    String line, result = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line + "\n";
                    }

                    this.response = result;
                }
            }
        }
        catch (IOException exception){
            Log.e(TAG, "getConnection()", exception);
        }
        finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                }
                catch (IOException exception){
                    Log.e(TAG, "bufferedReaer.close()", exception);
                }
            }

            if(inputStreamReader != null){
                try {
                    inputStreamReader.close();
                }
                catch (IOException exception){
                    Log.e(TAG, "inputStreamReader.close()", exception);
                }
            }
            if (inputStream != null){
                try{
                    inputStream.close();
                }
                catch (IOException exception){
                    Log.e(TAG, "inputStream.close()", exception);
                }
            }
            if(urlConnection != null) {
                urlConnection.disconnect();



            }
        }
        return null;
    }

    protected void onPostExecution(Void result) {
        super.onPostExecute(result);
        pictureList = getPictureList();
        if (this.mode == MODE.GET){

        }


    }
    public static Timestamp convertStringToTimestamp(String str_date) {
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("dd/MM/yyyy");
            // you can change format of date
            Date date = formatter.parse(str_date);
            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (ParseException e) {
            System.out.println("Exception :" + e);
            return null;
        }
    }
    public List<Picture> getPictureList(){
        List<Picture> pictures = new Vector<>();

        if(this.document != null) {
            NodeList nodeList = document.getElementsByTagName("picture");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Element node = (Element) nodeList.item(i);
                NodeList childNodes = node.getChildNodes();

                String title = "", description = "", imageData = "";
                Timestamp timestamp = null;

                title = getNodeValue(node, "title");
                description = getNodeValue(node, "description");
                imageData = getNodeValue(node, "imagedata");
                timestamp = convertStringToTimestamp(getNodeValue(node, "timestamp"));


            }
        }
        return pictures;
    }
    private String getNodeValue(Element node, String nodename){
        NodeList childNodes = node.getElementsByTagName(nodename);
        String value = "";

        if(childNodes.getLength() > 0 ){
            value = childNodes.item(0).getTextContent();
        }

        return value;
    }
}
