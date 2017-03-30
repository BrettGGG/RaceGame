package com.example.gonghailong.racegame.game;

/**
 * * 用于在文件中保存程序数据
 * Created by gonghailong on 2017/3/11.
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import java.io.*;

public class FileHelper {

    private static final String TAG = "FileHelper";
    private Context mContext;

    FileHelper(Context _mContext) {
        mContext = _mContext;
    }

    /*public static void saveFile(String str) {
        String filePath = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) { // SD卡根目录的hello.text
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + "hello.txt";
        } else  // 系统下载缓存根目录的hello.text
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + "hello.txt";

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(str.getBytes());
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }*/

    // 在手机本地硬盘中保存信息
    public void save(String fileName, String content) {

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = mContext.openFileOutput(fileName,
                    Context.MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 读取手机硬盘中保存的文件
    public void read(String fileName) {
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = mContext.openFileInput(fileName);
            int len = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
            while ((len = fileInputStream.read(buffer)) != -1) {
                byteArrayInputStream.write(buffer, 0, len);
            }
            String string = new String(byteArrayInputStream.toByteArray());
            Log.d(TAG, string);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    // save information in the SDCard
    public boolean saveToSDCard(String fileName, String content) {

        // judge weather the SDCard exits,and can be read and written
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return false;
        }

        FileOutputStream fileOutputStream = null;
        File file = new File(Environment.getExternalStorageDirectory(),
                fileName);
        try {
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content.getBytes());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    // read the file in the SDCard
    public String readFromSD(String fileName) {
        FileInputStream fileInputStream = null;
        File file = new File(Environment.getExternalStorageDirectory(),
                fileName);
        try {
            fileInputStream = new FileInputStream(file);
            int len = 0;
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
            while ((len = fileInputStream.read(buffer)) != -1) {
                byteArrayInputStream.write(buffer, 0, len);
            }
            String string = new String(byteArrayInputStream.toByteArray());
            return string;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}


