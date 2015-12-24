package bfmv.com.ubiquituous;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.zeng1990java.widget.WaveProgressView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import bfmv.com.ubiquituous.model.Galon;
import bfmv.com.ubiquituous.rest.GalonService;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    @ViewById
    Toolbar toolbar;
    @ViewById
    DrawerLayout mDLdrawer;
    @ViewById
    NavigationView mNVnav;
    @ViewById
    WaveProgressView mWPVgalon;

    Retrofit retrofit;
    GalonService galonService;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDLdrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDLdrawer.setDrawerListener(toggle);
        mNVnav.setNavigationItemSelectedListener(this);
        SharedPreferences prefs = getSharedPreferences("bfmv.com.ubiquitous.sharedprefs", Context.MODE_PRIVATE);
        if (prefs.getString("address", null) == null || prefs.getString("sellerNumber", null) == null) {
            Intent intent = new Intent(MainActivity.this, AddSellerActivity_.class);
            startActivity(intent);
            Toast.makeText(this, "Harap isi nomer penjual dan alamat anda", Toast.LENGTH_SHORT).show();
        }
        Intent intentDining = AutoRequestService_.intent(MainActivity.this).get();
        PendingIntent pintent = PendingIntent.getService(MainActivity.this, 0, intentDining, 0);
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 40*1000, pintent);

        retrofit = new Retrofit.Builder().baseUrl("http://ristek.cs.ui.ac.id/galminder/api/").addConverterFactory(GsonConverterFactory.create()).build();
        galonService = retrofit.create(GalonService.class);
        Call<Galon> galonStatusCall = galonService.getGalonStatus("1");
        galonStatusCall.enqueue(new Callback<Galon>() {
            @Override
            public void onResponse(Response<Galon> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    if (response.code() == 200) {
                        Galon galon = response.body();
                        if (galon.getInterpretation().equalsIgnoreCase("biasa") || galon.getInterpretation().equalsIgnoreCase("penuh")) {
                            mWPVgalon.setWaveColor(Color.BLUE);
                            mWPVgalon.setProgress((int) Double.parseDouble(galon.getVolume().split(" ")[0]));
                        } else {
                            mWPVgalon.setWaveColor(Color.RED);
                            mWPVgalon.setProgress((int) Double.parseDouble(galon.getVolume().split(" ")[0]));
                        }
                        mWPVgalon.setMax(19);
                        mWPVgalon.refreshDrawableState();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, AddSellerActivity_.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mDLdrawer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.mDLdrawer);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
