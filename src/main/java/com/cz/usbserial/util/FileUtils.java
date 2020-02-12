package com.cz.usbserial.util;

import android.content.Context;
import android.os.Environment;

import com.cz.usbserial.tpms.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


public class FileUtils {
    private static String fileDirPath = (String.valueOf(Environment.getExternalStorageDirectory().getAbsolutePath()) + "/TPMS");
    private static String fileName = "tpms.txt";
    static final String DEFAULT_FILENAME = ("/TPMS/" + fileName);

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static String getSdCardPath() {
        if (isSdCardExist()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "\u4e0d\u9002\u7528";
    }

    public static String getDefaultFilePath() {
        File file = new File(Environment.getExternalStorageDirectory(), "tpms.txt");
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return "\u4e0d\u9002\u7528";
    }

    public static void FileInputStreamFile() {
        try {
            FileInputStream is = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "tpms.txt"));
            byte[] b = new byte[is.available()];
            is.read(b);
            System.out.println("\u8bfb\u53d6\u6210\u529f\uff1a" + new String(b));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String BufferReaderFile() {
        if (!isSdCardExist()) {
            return "";
        }
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(Environment.getExternalStorageDirectory(), DEFAULT_FILENAME)));
            StringBuffer sb = new StringBuffer();
            while (true) {
                String readline = br.readLine();
                if (readline == null) {
                    br.close();
                    System.out.println("\u8bfb\u53d6\u6210\u529f\uff1a" + sb.toString());
                    return sb.toString();
                }
                System.out.println("readline:" + readline);
                sb.append(readline);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static void FileOutputStreamFile() {
        try {
            FileOutputStream fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory(), DEFAULT_FILENAME));
            fos.write("I am a chinanese!".getBytes());
            fos.close();
            System.out.println("\u5199\u5165\u6210\u529f\uff1a");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createFile() {
        String filePath = String.valueOf(fileDirPath) + "/" + fileName;
        try {
            File dir = new File(fileDirPath);
            if (!dir.exists()) {
                System.out.println("\u8981\u5b58\u50a8\u7684\u76ee\u5f55\u4e0d\u5b58\u5728");
                if (dir.mkdirs()) {
                    System.out.println("\u5df2\u7ecf\u521b\u5efa\u6587\u4ef6\u5b58\u50a8\u76ee\u5f55");
                } else {
                    System.out.println("\u521b\u5efa\u76ee\u5f55\u5931\u8d25");
                }
            }
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("\u8981\u6253\u5f00\u7684\u6587\u4ef6\u4e0d\u5b58\u5728");
                System.out.println("\u5f00\u59cb\u8bfb\u5165");
                FileOutputStream fos = new FileOutputStream(file);
                System.out.println("\u5df2\u7ecf\u521b\u5efa\u8be5\u6587\u4ef6");
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void BufferedWriterFile(String info) {
        if (isSdCardExist()) {
            try {
                createFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(new File(Environment.getExternalStorageDirectory(), DEFAULT_FILENAME)));
                bw.write(info);
                bw.flush();
                System.out.println("\u5199\u5165\u6210\u529f");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String getFromRaw(Context context) {
        String res = "";
        try {
            InputStream in = context.getResources().openRawResource(R.raw.vender);
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            res = getString(buffer, "UTF-8");
            in.close();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }
    }

    private static String getString(final byte[] data, final String charset) {
        return getString(data, 0, data.length, charset);
    }

    private static String getString(final byte[] data, final int offset, final int length, final String charset) {
        try {
            return new String(data, offset, length, charset);
        } catch (final UnsupportedEncodingException e) {
            return new String(data, offset, length);
        }
    }
}
