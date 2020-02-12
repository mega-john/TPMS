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
    private static String fileDirPath = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/TPMS");
    private static String fileName = "tpms.txt";
    static final String DEFAULT_FILENAME = ("/TPMS/" + fileName);

    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static String getSdCardPath() {
        if (isSdCardExist()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return "Not applicable";
    }

    public static String getDefaultFilePath() {
        File file = new File(Environment.getExternalStorageDirectory(), "tpms.txt");
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        return "Not applicable";
    }

    public static void FileInputStreamFile() {
        try {
            FileInputStream is = new FileInputStream(new File(Environment.getExternalStorageDirectory(), "tpms.txt"));
            byte[] b = new byte[is.available()];
            is.read(b);
            System.out.println("Reading succeeded:" + new String(b));
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
                    System.out.println("Reading succeeded:" + sb.toString());
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
            System.out.println("Write success:");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createFile() {
        String filePath = fileDirPath + "/" + fileName;
        try {
            File dir = new File(fileDirPath);
            if (!dir.exists()) {
                System.out.println("The directory to be stored does not exist");
                if (dir.mkdirs()) {
                    System.out.println("File storage directory has been created");
                } else {
                    System.out.println("Create directory failed");
                }
            }
            File file = new File(filePath);
            if (!file.exists()) {
                System.out.println("The file to be opened does not exist");
                System.out.println("Start reading");
                FileOutputStream fos = new FileOutputStream(file);
                System.out.println("The file has been created");
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
                System.out.println("Write success");
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
