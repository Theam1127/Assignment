package my.edu.tarc.assignment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences userPreferences, cartPreferences;
    TextView textViewUserName, textViewCurrentCredit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        userPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        View header = navigationView.getHeaderView(0);
        textViewUserName = (TextView)header.findViewById(R.id.TextViewUserName);
        textViewCurrentCredit = (TextView)header.findViewById(R.id.textViewCurrentCredit);
        textViewCurrentCredit.setText("Wallet: "+userPreferences.getString("CURRENT_CREDIT", "0.00"));
        textViewUserName.setText("Hi, "+userPreferences.getString("LOGIN_USER", "Anonymous"));
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Exit EasyShop?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPreferences.edit();
                    editor.clear();
                    editor.commit();
                    finish();
                }
            }).setNegativeButton("No", null).create().show();
        }
    }

    @Override
    public void onDestroy(){
        userPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = userPreferences.edit();
        editor.clear();
        editor.commit();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_page, menu);
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


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_AddItem) {
            userPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
            String username = userPreferences.getString("LOGIN_USER", "");
            cartPreferences = getSharedPreferences(username, MODE_PRIVATE);
            Intent intent;
            String shopID = cartPreferences.getString(AddItem.GET_SHOP,"");
            if(shopID.isEmpty())
                intent = new Intent(this, SelectShop.class);
            else {
                intent = new Intent(this, AddItem.class);
                intent.putExtra(SelectShop.INTENT_SHOPID, shopID);
            }
            startActivity(intent);
            item.setChecked(false);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        textViewCurrentCredit.setText("Wallet: "+userPreferences.getString("CURRENT_CREDIT", "0.00"));
        textViewUserName.setText("Hi, "+userPreferences.getString("LOGIN_USER", "Anonymous"));
    }
}
