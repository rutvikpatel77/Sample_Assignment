package com.example.android.assignment_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MainActivity extends AppCompatActivity {

    final DatabaseHelper db = new DatabaseHelper(this);

    /*//variables for graphview - start
    String title="Assignment-2";
    String[] verlabels = new String[]{"70", "60", "50", "40", "30", "20","10","0"};
    String[] horlabels = new String[]{"0", "10", "20", "30", "40", "50", "60","70"};
    private float[] values = new float[10]; //remove once x,y,z array fixed

    GraphView gv;
    LinearLayout root;
    */
    // three different arrays to store x,y,z values from accModel once queried from databases
    private float[] xValues = new float[10];
    private float[] yValues = new float[10];
    private float[] zValues = new float[10];

    private int x=1;
    //variables for graphview - end
    private LineGraphSeries<DataPoint> series1,series2,series3;

    //variables for input fields
    EditText pid, age, name;
    RadioGroup gender;
    RadioButton g;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;

       /* //graph view
        root=(LinearLayout) findViewById(R.id.graphview_layout);
        gv=new GraphView(this, values, title, horlabels, verlabels, GraphView.LINE); //TODO:update values for x,y,z*/

        //input fields
        pid = (EditText) findViewById(R.id.pid_editText);
        age = (EditText) findViewById(R.id.age_editText);
        name = (EditText) findViewById(R.id.pname_editText);
        gender = (RadioGroup) findViewById(R.id.radioGroup);

        //Buttons
        Button create_database = (Button) findViewById(R.id.create_db_btn);
        Button run_btn = (Button) findViewById(R.id.run_button);
        Button stop_btn = (Button) findViewById(R.id.stop_button);
        Button upload_btn = (Button) findViewById(R.id.upload_btn);
        GraphView graph = (GraphView) findViewById(R.id.graph);

        series1 = new LineGraphSeries<DataPoint>();
        graph.addSeries(series1);
        series2 = new LineGraphSeries<DataPoint>();
        graph.addSeries(series2);
        series3 = new LineGraphSeries<DataPoint>();
        graph.addSeries(series3);

        Viewport viewport = graph.getViewport();
        viewport.setYAxisBoundsManual(true);
        viewport.setMinY(-5);
        viewport.setMaxY(10);
        viewport.setScrollable(true);

        //TODO:pulls 10 most recent data from database and show in the graph, graph updates each second
        run_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                processLatestAccValues();
                //pass to graphview by calling setvalues method

                for(int i=0;i<10;i++){
                    String st="X value : "+xValues[i]+" Y Value : "+yValues[i]+" Z value : "+zValues[i];
                    Log.d("LAST 10 RETRIEVED",st);
                }

                DataPoint[] dp_x=new DataPoint[10];
                DataPoint[] dp_y=new DataPoint[10];
                DataPoint[] dp_z=new DataPoint[10];

                for(int j=0;j<10;j++){
                    dp_x[j]=new DataPoint (2*j,xValues[j]);
                    dp_y[j]=new DataPoint (2*j,yValues[j]);
                    dp_z[j]=new DataPoint (2*j,zValues[j]);
                }
                series1.resetData(dp_x);
                series1.setColor(Color.GREEN);
                series1.setThickness(6);
                series2.resetData(dp_y);
                series2.setThickness(6);
                series2.setColor(Color.BLUE);
                series3.resetData(dp_z);
                series3.setThickness(6);
                series3.setColor(Color.RED);

            }
        });

        //TODO:clears the graph
        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //make run=false to stop drawing
                //gv.run=false;
            }
        });

        create_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selected = gender.getCheckedRadioButtonId();
                g=(RadioButton) findViewById(selected);

                //create db table name
                String table_name=name.getText()+"_"+pid.getText()+"_"+age.getText()+"_"+g.getText();

                //call dbhandler and create database
                try{
                    db.addTable(table_name);
                    Toast toast = Toast.makeText(getApplicationContext(),table_name,Toast.LENGTH_SHORT);
                    toast.show();
                }
                catch(Exception ex){
                    Toast.makeText(context, "Error in adding table", Toast.LENGTH_SHORT).show();
                }

                Log.d("Rutvilkkk","sdnsuh----------");

                //initiate acc service
                Intent intentService = new Intent(MainActivity.this, AccelerometerService.class);
                startService(intentService);

                /*int selected = gender.getCheckedRadioButtonId();
                g=(RadioButton) findViewById(selected);
                //create db table name
                String table_name=name.getText()+"_"+pid.getText()+"_"+age.getText()+"_"+g.getText();
                //call dbhandler and create database
                try{
                    db.addTable(table_name);
                    Toast toast = Toast.makeText(getApplicationContext(),table_name,Toast.LENGTH_SHORT);
                    toast.show();
                }
                catch(Exception ex){
                    Toast.makeText(context, "Error in adding table", Toast.LENGTH_SHORT).show();
                }
               Log.d("Rutvilkkk","sdnsuhdnusnfusndfsd----------");
                //initiate acc service
                Intent intentService = new Intent(MainActivity.this, AccelerometerService.class);
                startService(intentService);
*/


                /*series.appendData(new DataPoint(0,xValues[0]), true, 10);
                series.appendData(new DataPoint(1,xValues[1]), true, 10);
                series.appendData(new DataPoint(2,xValues[2]), true, 10);
                series.appendData(new DataPoint(3,xValues[3]), true, 10);
                series.appendData(new DataPoint(4,xValues[4]), true, 10);
                series.appendData(new DataPoint(5,xValues[5]), true, 10);
                series.appendData(new DataPoint(6,xValues[6]), true, 10);
                series.appendData(new DataPoint(7,xValues[7]), true, 10);
                series.appendData(new DataPoint(8,xValues[8]), true, 10);
                series.appendData(new DataPoint(9,xValues[9]), true, 10);*/


            }
        });

        //TODO:upload db to server
        upload_btn.setOnClickListener(new View.OnClickListener() {

            // Right now it just displays a row from the table

            @Override
            public void onClick(View v) {
                try {
                    // new UploadTask().execute();
                    processDownloadClick();
                    Toast.makeText(MainActivity.this, "File Uploaded", Toast.LENGTH_SHORT).show();
                }
                catch(Exception e)
                {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private void processLatestAccValues(){
        List<AccelerometerModel> entries = db.getLatestAccEntries();

        if(entries!=null){  //if acc doesnt have any value
            int i=0;
            for(AccelerometerModel e: entries){
                xValues[i]=e.x;
                yValues[i]=e.y;
                zValues[i]=e.z;
                i++;
            }
        }


    }

    private void processDownloadClick(){

        final UploadTask uploadTask2 = new UploadTask(MainActivity.this);
        uploadTask2.execute();

    }

    class UploadTask extends AsyncTask< Integer, Integer, Integer > {
        private Context context;
        public UploadTask(Context context) {
            this.context = context;
        }
        protected void onPostExecute(Integer i) {
            Log.i("msg", "Inside Post exec");
        }

        protected Integer doInBackground(Integer... params) {
            String fileName = "/data/data/com.example.android.assignment_2/databases/assignment2";
            String Server_Address = "https://impact.asu.edu/Appenstance/UploadToServer.php";
            // String Server_Address = "https://impact.asu.edu/CSE535Spring16Folder/UploadToServerGPS.php";
            HttpURLConnection assign_conn = null;
            DataOutputStream out_Stream = null;
            DataInputStream in_Stream = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            //int serverResponseCode;
            //String serverResponse = "";
            int bytesRead,bytesAvailable,bufferSize;

            int maxBufferSize =   1024 * 1024 ;
            File uploadFile = new File(fileName);
            if(!uploadFile.isFile())
            {
                Toast.makeText(MainActivity.this, "File not Found", Toast.LENGTH_SHORT).show();
                return 0;
            }
            else
            {
                try
                {
                    FileInputStream fileInputStream = new FileInputStream(uploadFile);
                    TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                            // Not implemented
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                            // Not implemented
                        }
                    } };

                    try {
                        SSLContext sc = SSLContext.getInstance("TLS");

                        sc.init(null, trustAllCerts, new java.security.SecureRandom());

                        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    } catch (KeyManagementException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    URL serverUrl = new URL(Server_Address);

                    assign_conn = (HttpsURLConnection)serverUrl.openConnection();
                    assign_conn.setDoInput(true);
                    assign_conn.setDoOutput(true);
                    assign_conn.setUseCaches(false);
                    assign_conn.setRequestMethod("POST");
                    assign_conn.setRequestProperty("Connection", "Keep-Alive");
                    assign_conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    assign_conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    assign_conn.setRequestProperty("uploaded_file", fileName);

                    out_Stream = new DataOutputStream(assign_conn.getOutputStream());
                    out_Stream.writeBytes(twoHyphens + boundary + lineEnd);
                    out_Stream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);
                    out_Stream.writeBytes(lineEnd);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte [] buffer = new byte[bufferSize];
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    while(bytesRead > 0)
                    {
                        out_Stream.write(buffer,0,bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer,0,bufferSize);
                    }
                    out_Stream.writeBytes(lineEnd);
                    out_Stream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    fileInputStream.close();
                    out_Stream.flush();
                    out_Stream.close();
                }
                catch(MalformedURLException e)
                {
                    e.printStackTrace();
                    return 0;
                }
                catch(Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
            return 0;
        }
    }


}
