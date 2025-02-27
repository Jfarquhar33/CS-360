package com.example.farquharcs360project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;

import android.widget.Button;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;


public class MainActivity extends AppCompatActivity implements AddItemFragment.OnItemAddedListener {

    private static final int REQUEST_CODE_SEND_SMS = 101;
    private DatabaseHelper dbHelper;
    private InventoryAdapter adapter;
    private List<Item> itemList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadInventory();
        requestSmsPermission();

        Button settingsButton = findViewById(R.id.buttonSettings);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    private void loadInventory() {
        itemList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllItems();
        Log.d("InventoryDebug", "loadInventory() called");
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                int stock = cursor.getInt(2);
                itemList.add(new Item(id, name, stock));
                Log.d("InventoryDebug", "Item: " + name + ", Stock: " + stock);
            } while (cursor.moveToNext());
        } else {
            Log.d("InventoryDebug", "No items found in inventory");
        }

        cursor.close();
        Log.d("InventoryDebug", "Items retrieved: " + itemList.size());

        adapter = new InventoryAdapter(this, itemList, dbHelper);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.updateItems(itemList);
    }

    @Override
    public void onItemAdded() {
        loadInventory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            // Open the AddItemFragment dialog
            AddItemFragment addItemFragment = new AddItemFragment();
            addItemFragment.show(getSupportFragmentManager(), "AddItemFragment");
            return true;
        } else if (item.getItemId() == R.id.action_settings) {
            // Open the SettingsActivity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_SEND_SMS);
        }
    }

    // This should be fine to delete, but leaving in case there is something I missed
    private void showSmsPermissionDialog() {
        SmsPermissionDialogFragment dialog = new SmsPermissionDialogFragment();
        dialog.show(getSupportFragmentManager(), "SmsPermissionDialog");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void sendLowStockSms(String itemName) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSmsEnabled = prefs.getBoolean("sms_notifications", false);

        if (!isSmsEnabled) {
            Log.d("SMS_TEST", "SMS notifications are disabled. No message sent.");
            return;
        }

        String phoneNumber = "1234567890";
        String message = "Alert: " + itemName + " is out of stock!";

        if (Build.MODEL.contains("Emulator") || Build.PRODUCT.contains("sdk")) {
            Log.d("SMS_TEST", "Simulated SMS to: " + phoneNumber + " | Message: " + message);
            Toast.makeText(this, "Simulated SMS Sent!", Toast.LENGTH_SHORT).show();
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, "Low stock alert sent!", Toast.LENGTH_SHORT).show();
        }
    }


}