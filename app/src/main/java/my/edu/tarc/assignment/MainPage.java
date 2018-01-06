package my.edu.tarc.assignment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import my.edu.tarc.assignment.StartShopping.AddItem;
import my.edu.tarc.assignment.StartShopping.SelectShop;
import my.edu.tarc.assignment.TopUp.TopUpMain;
import my.edu.tarc.assignment.Transfer.TransferActivity;


public class MainPage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences userPreferences, cartPreferences;
    TextView textViewUserName, textViewCurrentCredit;
    ScrollView scrollViewMain;
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
        textViewCurrentCredit = (TextView)findViewById(R.id.textViewCurrentCredit);
        scrollViewMain = (ScrollView)findViewById(R.id.main_page_scroll);
        if(!CheckConnection.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
        }

        textViewCurrentCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!CheckConnection.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(MainPage.this, TopUpMain.class);
                    startActivity(intent);
                }
            }
        });
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
                    System.exit(0);
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



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setCheckable(false);
        if (id == R.id.nav_AddItem) {
            if(!CheckConnection.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
            }
            else {
                userPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
                String username = userPreferences.getString("LOGIN_USER", "");
                double credit = Double.parseDouble(userPreferences.getString("CURRENT_CREDIT", "0.00"));
                AlertDialog.Builder builder;
                if (credit < 10) {
                    builder = new AlertDialog.Builder(MainPage.this);
                    builder.setMessage("You should have minimum RM 10.00 in your wallet in order to start shopping! Do you want to top up now?").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!CheckConnection.isConnected(getApplicationContext())) {
                                        Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Intent intent = new Intent(MainPage.this, TopUpMain.class);
                                        startActivity(intent);
                                    }
                                }
                            }).setNegativeButton("No", null).create().show();
                } else {
                    cartPreferences = getSharedPreferences(username, MODE_PRIVATE);
                    Intent intent;
                    String shopID = cartPreferences.getString(AddItem.GET_SHOP, "");
                    if (shopID.isEmpty())
                        intent = new Intent(this, SelectShop.class);
                    else {
                        intent = new Intent(this, AddItem.class);
                        intent.putExtra(SelectShop.INTENT_SHOPID, shopID);
                    }
                    startActivity(intent);
                }
            }
        }
        else if(id == R.id.nav_topup){
            if(!CheckConnection.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
            }
            else {
                Intent intent = new Intent(this, TopUpMain.class);
                startActivity(intent);
            }
        }
        else if(id== R.id.nav_transfer){
            if(!CheckConnection.isConnected(getApplicationContext())) {
                Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
             }
             else {
                userPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
                double credit = Double.parseDouble(userPreferences.getString("CURRENT_CREDIT", "0.00"));
                AlertDialog.Builder builder;
                if (credit < 5) {
                    builder = new AlertDialog.Builder(MainPage.this);
                    builder.setMessage("You should have minimum RM 5.00 in your wallet in order to do transfer! Do you want to top up now?").
                            setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(!CheckConnection.isConnected(getApplicationContext())) {
                                        Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        Intent intent = new Intent(MainPage.this, TopUpMain.class);
                                        startActivity(intent);
                                    }
                                }
                            }).setNegativeButton("No", null).create().show();
                } else {
                    Intent intent = new Intent(this, TransferActivity.class);
                    startActivity(intent);
                }
            }
        }

        else if(id==R.id.nav_logout){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Logout your account?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPreferences.edit();
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(MainPage.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.xml.enter,R.xml.exit);
                }
            }).setNegativeButton("No", null).create().show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        textViewCurrentCredit.setText("WALLET\n\nRM "+userPreferences.getString("CURRENT_CREDIT", "0.00"));
        textViewUserName.setText("Hi, "+userPreferences.getString("LOGIN_USER", "Anonymous"));
        scrollViewMain.fullScroll(ScrollView.FOCUS_UP);
    }
}
