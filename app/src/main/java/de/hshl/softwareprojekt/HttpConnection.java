package de.hshl.softwareprojekt;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpConnection extends AsyncTask<Void, Void, String> {
    private static final String TAG = MainActivity.class.getSimpleName();
    public AsyncResponse delegate = null;
    private MODE mode;
    private String dstAddress, message = "";
    private String response;

    HttpConnection(String dstAddress) {
        this.dstAddress = dstAddress;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMode(MODE mode) {
        this.mode = mode;
    }

    //Zuständig für den Upload der XML-Dateien und den Empfängt echo der Php Dateien
    @Override
    protected String doInBackground(Void... voids) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(this.dstAddress);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setInstanceFollowRedirects(true);

            if (this.mode == MODE.PUT) {
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoInput(true);

                OutputStream outputStream = new BufferedOutputStream(urlConnection.getOutputStream());
                outputStream.write(this.message.getBytes());
                outputStream.flush();
            } else {
                urlConnection.setRequestMethod("GET");
            }

            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();

                if (this.mode == MODE.PUT) {
                    inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                    bufferedReader = new BufferedReader(inputStreamReader);

                    String line, result = "";
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }

                    this.response = result;
                }
            }
        } catch (IOException exception) {
            Log.e(TAG, "getConnection()", exception);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException exception) {
                    Log.e(TAG, "bufferedReaer.close()", exception);
                }
            }

            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException exception) {
                    Log.e(TAG, "inputStreamReader.close()", exception);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException exception) {
                    Log.e(TAG, "inputStream.close()", exception);
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();

            }
        }
        return this.response;
    }

    //Enthält Vielzahl von Kennwörtern, die im Echo der Php Dateien mitgesendet werden
    @Override
    protected void onPostExecute(String result) {
        if (result.contains("Is_Ok")) {
            delegate.processFinish(result);
        } else if (result.contains("UserChecked")) {
            delegate.processFinish(result);
        } else if (result.contains("UserNotChecked")) {
            delegate.processFinish(result);
        } else if (result.contains("UserReturn")) {
            delegate.processFinish(result);
        } else if (result.contains("HashReturn")) {
            delegate.processFinish(result);
        } else if (result.contains("HashInput")) {
            delegate.processFinish(result);
        } else if (result.contains("Followed")) {
            delegate.processFinish(result);
        } else if (result.contains("UserPic")) {
            delegate.processFinish(result);
        } else if (result.contains(" : ")) {
            delegate.processFinish(result);
        } else if (result.contains("NoStory")) {
            delegate.processFinish(result);
        } else if (result.contains("FollowExc")) {
            delegate.processFinish(result);
        } else if (result.contains("getKommentare")) {
            delegate.processFinish(result);
        } else if (result.contains("Unfollowed")) {
            delegate.processFinish(result);
        } else if (result.contains("UnfollowNotPossible")) {
            delegate.processFinish(result);
        } else if (result.contains("UserData")) {
            delegate.processFinish(result);
        } else if (result.contains("Post")) {
            delegate.processFinish(result);
        } else if (result.contains("FullPost")) {
            delegate.processFinish(result);
        } else if (result.contains("getPostIDs")) {
            delegate.processFinish(result);
        } else if (result.contains("Beschreibung")) {
            delegate.processFinish(result);
        } else if (result.contains("AllPostIDs")) {
            delegate.processFinish(result);
        } else if (result.contains("UpdatePostIDs")) {
            delegate.processFinish(result);
        } else if (result.contains("PostDeleted")) {
            delegate.processFinish(result);
        } else if (result.contains("NoNewPosts")) {
            delegate.processFinish(result);
        } else if (result.contains("SharedSuccessfully")) {
            delegate.processFinish(result);
        } else if (result.contains("NoHash")) {
            delegate.processFinish(result);
        } else if (result.contains("NoUser")) {
            delegate.processFinish(result);
        }
    }

    public enum MODE {GET, PUT}
}
