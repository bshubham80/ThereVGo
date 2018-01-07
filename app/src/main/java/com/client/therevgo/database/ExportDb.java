package com.client.therevgo.database;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by shubham on 29/6/16.
 */
public class ExportDb {

    public void exportDataBase(Context context , String databaseName , String BaackupDbName) {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "//data//"+context.getPackageName()+"//databases//"+databaseName+"";
                String backupDBPath = BaackupDbName;
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(context, "Exported", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
                e.printStackTrace();
            Toast.makeText(context, "Not Exported", Toast.LENGTH_SHORT).show();
        }
    }
}
