package com.ranajeetbarik2205.bluetoothapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "File Chooser";
    //Create Objects-------------------------------------------------------
    Button buttonopenDailog, buttonUp, send;
    TextView textFolder;
    EditText dataPath;
    String path = null;
    Uri uri;
    static final int CUSTOM_DIALOG_ID = 0;
    ListView dialog_ListView;
    File root = null, fileroot, curFolder = null;
    private List<String> fileList = new ArrayList<String>();
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    private static final int FILE_SELECT_CODE = 0;
    BluetoothAdapter btAdatper = BluetoothAdapter.getDefaultAdapter();
    //---------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataPath=(EditText)findViewById(R.id.FilePath);
        buttonopenDailog= (Button) findViewById(R.id.opendailog);
        send=(Button)findViewById(R.id.sendBtooth);
        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        curFolder = root;
        buttonopenDailog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPath.setText("");
                //showDialog(CUSTOM_DIALOG_ID);
                //showCustomDialog();
                showFileChooser();
            }
        });


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendViaBluetooth();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
            case CUSTOM_DIALOG_ID:
                dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.dialoglayout);
                dialog.setTitle("File Selector");
                dialog.setCancelable(true);
                dialog.setCanceledOnTouchOutside(true);
                textFolder = (TextView) dialog.findViewById(R.id.folder);
                buttonUp = (Button) dialog.findViewById(R.id.up);
                buttonUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListDir(curFolder.getParentFile());
                    }
                });
                dialog_ListView = (ListView) dialog.findViewById(R.id.dialoglist);
                dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        File selected = new File(fileList.get(position));
                        if (selected.isDirectory()) {
                            ListDir(selected);
                        } else if (selected.isFile()) {
                            getselectedFile(selected);
                        } else {
                            dismissDialog(CUSTOM_DIALOG_ID);
                        }
                    }
                });
                break;
        }
        return dialog;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case CUSTOM_DIALOG_ID:
                ListDir(curFolder);
                break;
        }
    }

    public void getselectedFile(File f){
        dataPath.setText(f.getAbsolutePath());
        fileList.clear();
        dismissDialog(CUSTOM_DIALOG_ID);
    }


    public void ListDir(File f) {
        if (f.equals(root)) {
            buttonUp.setEnabled(false);
        } else {
            buttonUp.setEnabled(true);
        }
        curFolder = f;
        textFolder.setText(f.getAbsolutePath());
        dataPath.setText(f.getAbsolutePath());
        File[] files = f.listFiles();
        fileList.clear();

        for (File file : files) {
            fileList.add(file.getPath());
        }
        ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
        dialog_ListView.setAdapter(directoryList);
    }

    //exit to application---------------------------------------------------------------------------
    public void exit(View V) {
        btAdatper.disable();
        Toast.makeText(this,"*** Now Bluetooth is off... Thanks. ***",Toast.LENGTH_LONG).show();
        finish(); }

    //Method for send file via bluetooth------------------------------------------------------------
    public void sendViaBluetooth() {
        if(!dataPath.equals(null)){
            if (btAdatper == null) {
                Toast.makeText(this, "Device not support bluetooth", Toast.LENGTH_LONG).show();
            } else {
                enableBluetooth();
            }
        }else{
            Toast.makeText(this,"Please select a file.",Toast.LENGTH_LONG).show();
        }
    }

    public void enableBluetooth() {
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }

    //Override method for sending data via bluetooth availability--------------------------


  /*  @Override
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
            Toast.makeText(this, "**********************************\nDeveloper: www.santoshkumarsingh.com\n**********************************", Toast.LENGTH_LONG).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

  /*  private void showCustomDialog() {
        //before inflating the custom alert dialog layout, we will get the current activity viewgroup
        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialoglayout, viewGroup, false);


        //Now we need an AlertDialog.Builder object
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);

        //finally creating the alert dialog and displaying it
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }*/





    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    uri = data.getData();
                    Log.d(TAG, "File Uri: " + uri.toString());
                    // Get the path

                    try {
                        path = getPath(this, uri);
                        dataPath.setText(path);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "File Path: " + path);

                    // Get the file instance


                    // Initiate the upload
                }
                break;

            case REQUEST_BLU:
                if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_SEND);
                    i.setType("*/*");
                   // File file = new File(path);

                    i.putExtra(Intent.EXTRA_STREAM, uri);

                    PackageManager pm = getPackageManager();
                    List<ResolveInfo> list = pm.queryIntentActivities(i, 0);
                    if (list.size() > 0) {
                        String packageName = null;
                        String className = null;
                        boolean found = false;

                        for (ResolveInfo info : list) {
                            packageName = info.activityInfo.packageName;
                            if (packageName.equals("com.android.bluetooth")) {
                                className = info.activityInfo.name;
                                found = true;
                                break;
                            }
                        }
                        //CHECK BLUETOOTH available or not------------------------------------------------
                        if (!found) {
                            Toast.makeText(this, "Bluetooth not been found", Toast.LENGTH_LONG).show();
                        } else {
                            i.setClassName(packageName, className);
                            startActivity(i);
                        }
                    }
                } else {
                    Toast.makeText(this, "Bluetooth is cancelled", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public static String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
