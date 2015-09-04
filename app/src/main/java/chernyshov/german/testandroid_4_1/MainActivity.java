package chernyshov.german.testandroid_4_1;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class MainActivity extends AppCompatActivity {
    private String dirSD = "GPSdata";
    private String fileName = "";
    private TextView textView;
    private EditText inpFileName;
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            writeDataToFile(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        inpFileName = (EditText) findViewById(R.id.inpFileName);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void writeDataToFile(Location location) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            textView.setText("SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        File sdPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirSD);
        if (!sdPath.exists())
            sdPath.mkdirs();
        File sdFile = new File(sdPath, fileName);
        try {
            FileWriter fileWriter = new FileWriter(sdFile, true);
            fileWriter.write(location.getLongitude() + ","
                    + location.getLatitude() + ","
                    + location.getTime() + "\n");
            fileWriter.close();
            textView.setText("Файл записан на SD: "
                    + sdFile.getAbsolutePath()
                    + " (" + sdFile.length() + " байт)");
            showFile();
        } catch (IOException e) {
            textView.setText(e.toString());
            e.printStackTrace();
        }
    }

    private void showFile() {
        TextView textView2 = (TextView) findViewById(R.id.textView2);
        textView2.setText("");
        fileName = inpFileName.getText().toString();
        if (fileName.equals("")) {
            textView.setText("Ошибка. Файл не выбран");
            return;
        }
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirSD;
        File sdFile = new File(sdPath, fileName);
        if (!sdFile.exists()) {
            textView.setText("Файл не найден " + sdFile.getAbsolutePath());
            return;
        }
        try {
            FileReader fr = new FileReader(sdFile);
            int n = (int) sdFile.length();
            char[] data = new char[n];
            fr.read(data);
            String text = String.valueOf(data);
            fr.close();
            textView2.setText(text);
            ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);
            scrollView.fullScroll(View.FOCUS_DOWN);
        } catch (IOException e) {
            textView.setText(e.toString());
            e.printStackTrace();
        }
    }

    public void readFile(View view) {
        showFile();
    }

    public void start(View view) {
        fileName = inpFileName.getText().toString();
        if (fileName.equals("")) {
            textView.setText("Ошибка. Файл не выбран");
            return;
        }
        textView.setText("Старт");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void stop(View view) {
        textView.setText("Стоп");
        locationManager.removeUpdates(locationListener);
    }

    public void deleteFile(View view) {
        fileName = inpFileName.getText().toString();
        if (fileName.equals("")) {
            textView.setText("Ошибка. Файл не выбран");
            return;
        }
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirSD;
        File sdFile = new File(sdPath, fileName);
        if (!sdFile.exists()) {
            textView.setText("Файл не найден " + sdFile.getAbsolutePath());
            return;
        }
        if (sdFile.delete()) {
            textView.setText("Файл удвлен " + sdFile.getAbsolutePath());
        }
    }
}