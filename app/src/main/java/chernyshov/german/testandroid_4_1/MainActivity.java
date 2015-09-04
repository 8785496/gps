package chernyshov.german.testandroid_4_1;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    final String dirSD = "GPSdata";
    final String fileName = "file1.txt";
    private TextView textView;
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            writeDataToFile(location);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
        public void onProviderEnabled(String provider) {}
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
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

    public void writeFile(View view) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            textView.setText("SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/" + dirSD);
        //sdPath = new File(sdPath.getAbsolutePath());
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, fileName);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile, true));
            // пишем данные
            bw.write("Содержимое файла на SD" + "\n");
            // закрываем поток
            bw.close();
            //Log.d(LOG_TAG, "Файл записан на SD: " + sdFile.getAbsolutePath());
            textView.setText("Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            textView.setText(e.toString());
            e.printStackTrace();
        }
    }

    private void writeDataToFile (Location location) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            textView.setText("SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        File sdPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirSD);
        if (!sdPath.exists())
            sdPath.mkdirs();
        File sdFile = new File(sdPath, fileName);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile, true));
            bw.write("Содержимое файла на SD" + "\n");
            bw.close();
            textView.setText("Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            textView.setText(e.toString());
            e.printStackTrace();
        }
    }

    public void readFile(View view) {
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirSD;
        File sdFile = new File(sdPath, fileName);
        if (!sdFile.exists()) {
            textView.setText("Файл не найден " + sdFile.getName());
            return;
        }
        try {
            FileReader fr = new FileReader(sdFile);
            int n = (int) sdFile.length();
            char[] data = new char[n];
            fr.read(data);
            String text = String.valueOf(data);
            fr.close();
            textView.setText(text);
        } catch (IOException e) {
            textView.setText(e.toString());
            e.printStackTrace();
        }
    }
}