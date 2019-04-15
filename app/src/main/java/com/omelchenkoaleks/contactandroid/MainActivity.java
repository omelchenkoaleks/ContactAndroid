package com.omelchenkoaleks.contactandroid;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_CONTACT = 1000;

    private TextView mContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContacts = findViewById(R.id.text_tv);


        /*
            метод получает ВСЕ контакты - имя и телефоны
         */
//        getContacts();

    }

    private void getContacts() {
        String phoneNumber = null;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Log.d("happy", " " + CONTENT_URI + " " + ID + " " + DISPLAY_NAME + " " + HAS_PHONE_NUMBER);

        // берем значения: id, name, number контакта
        Uri phoneContentUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String phoneContactId = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String phoneContactNumber = ContactsContract.CommonDataKinds.Phone.NUMBER;

        StringBuffer output = new StringBuffer();
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(CONTENT_URI,
                null, null, null, null);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                // получаем имя
                if (hasPhoneNumber > 0) {
                    output.append("\n Имя: " + name);
                    Cursor phoneCursor = contentResolver.query(
                            phoneContentUri,
                            null,
                            phoneContactId + " = ?",
                            new String[]{contactId},
                            null);

                    // и соответствующий ему номер
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(phoneContactNumber));

                        output.append("\n Телефон: " + phoneNumber);
                    }
                    output.append("\n");
                }
            }
        }
        mContacts.setText(output);
    }

    public void onClick(View view) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor cursor = getContentResolver().query(
                            contactData,
                            null,
                            null,
                            null,
                            null);

                    // получаем имя
                    if (cursor.moveToFirst()) {
                        String name = cursor.getString(
                                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        Log.d("happy", " " + name);
                    }


                    // получаем номер телефона
                    if (cursor.moveToFirst()) {
                        String id = cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        ContactsContract.Contacts._ID));

                        String hasPhone = cursor.getString(
                                cursor.getColumnIndex(
                                        ContactsContract.Contacts.HAS_PHONE_NUMBER));

                        if (hasPhone.equalsIgnoreCase("1")) {
                            Cursor phones =
                                    getContentResolver().query(
                                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                            null,
                                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id,
                                            null,
                                            null);

                            phones.moveToFirst();

                            String phoneNumber = phones.getString(
                                    phones.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER));

                            Log.d("happy", " " + phoneNumber);
                        }
                    }
                }
        }
    }

    //    @Override
//    protected void onActivityResult(int requestCode,
//                                    int resultCode,
//                                    @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        switch (requestCode) {
//            case (PICK_CONTACT):
//                if (resultCode == Activity.RESULT_OK && data != null) {
//                    Uri contactData = data.getData();
//                    Cursor cursor = getContentResolver()
//                            .query(contactData,
//                                    null,
//                                    null,
//                                    null,
//                                    null);
//
//                    if (cursor.moveToFirst()) {
//                        String name = cursor.getString(
//                                cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//                        Log.d("happy", " " + name);
//                    }
//                }
//                break;
//        }
//    }
}
