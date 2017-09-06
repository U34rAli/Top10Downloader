package com.example.umar.top10downloader;

import android.Manifest;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //    TextView textView;
    Button btnParse;
    ListView listApps;
    private String mFileContents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        textView = (TextView)findViewById(R.id.textView);
        btnParse = (Button) findViewById(R.id.btnParse);
        btnParse.setEnabled(false);
        btnParse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add parse activation code
                ParseApplications parseApplications = new ParseApplications(mFileContents);
                parseApplications.process();
                ArrayAdapter<Application> arrayAdapter = new ArrayAdapter<Application>(MainActivity.this,
                        R.layout.list_item, parseApplications.getApplications());
                listApps.setAdapter(arrayAdapter);
            }
        });
        listApps = (ListView) findViewById(R.id.xmlListView);
        DownloaderData downloaderData = new DownloaderData();
        downloaderData.execute(getString(R.string.URLPath));

    }


    // Params , Progress , Result
    private class DownloaderData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            mFileContents = downloadXMLFile(strings[0]);
            if (mFileContents == null) {
                Log.d(getString(R.string.DownloadData), "Error Downloading");
            }
            return mFileContents;
        }

        private String downloadXMLFile(String urlPath) {
            StringBuilder tempBuffer = new StringBuilder();
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(getString(R.string.DownloadData), "The response code was " + response);
                InputStream is = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);

                int charRead;
                char[] inputBuffer = new char[500];

                while (true) {
                    charRead = isr.read(inputBuffer);
                    if (charRead <= 0) {
                        break;
                    }
                    tempBuffer.append(String.copyValueOf(inputBuffer, 0, charRead));
                }
                return tempBuffer.toString();

            } catch (IOException e) {
                Log.d(getString(R.string.DownloadData), "IO Exception reading Data: " + e.getMessage());
                e.printStackTrace();
            } catch (SecurityException e) {
                Log.d(getString(R.string.DownloadData), "Security Exception Need Permission: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("DownloadData", "Result was: " + s);
            btnParse.setEnabled(true);
//            textView.setText(s);
        }
    }

}
