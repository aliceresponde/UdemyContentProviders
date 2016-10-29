package com.example.alice.udemycontentproviders;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_MSN = 100;
    private static final int REQUEST_CODE_CONTACTS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * https://developer.android.com/training/permissions/requesting.html
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_MSN:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readMSN(new View(this));
                } else {
                    Toast.makeText(this, "Read MSM is required", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_CODE_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts(new View(this));
                } else {
                    Toast.makeText(this, "Read Contacts is required", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return;
    }

    /**
     * public static final Uri CONTENT_URI = Uri.parse("content://sms");
     *
     * @param view
     */
    public void readMSN(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            boolean canReadMSM = ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS") == PackageManager.PERMISSION_GRANTED;
            if (canReadMSM) {
                Cursor cursor = null;
                cursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);
                    String body = cursor.getString(bodyIndex);
                    Log.d("MSN", "Body: " + body);
                }
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_SMS"}, REQUEST_CODE_MSN);
            }
        } else {
            Toast.makeText(this, "Action requires KitKat or later version", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * public static final String AUTHORITY = "com.android.contacts";
     * public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
     * public static final Uri CONTENT_URI = Uri.withAppendedPath(AUTHORITY_URI, "contacts");
     *
     * @param view
     */
    public void readContacts(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            boolean canReadContacts = ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_CONTACTS") == PackageManager.PERMISSION_GRANTED;
            Log.i("Contacts", "canReadC?? " + canReadContacts);

            if (canReadContacts) {
                Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
                while (cursor.moveToNext()) {
                    int nameIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                    String name = cursor.getString(nameIndex);
                    Log.d("Contacts", "name: " + name);
                }
            } else {
                Log.i("Contacts", "Request permission");
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_CONTACTS"}, REQUEST_CODE_CONTACTS);
            }
        }else{
            Toast.makeText(this, "Action requires KitKat or later version", Toast.LENGTH_SHORT).show();
        }
    }
}
