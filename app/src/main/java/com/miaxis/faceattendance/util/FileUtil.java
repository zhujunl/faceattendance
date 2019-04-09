package com.miaxis.faceattendance.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class FileUtil {

    public static final String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "miaxis";
    public static final String MAIN_PATH = PATH + File.separator + "faceattendance";
    public static final String LICENCE_PATH = PATH + File.separator + "FaceId_ST" + File.separator + "st_lic.txt";
    public static final String IMG_PATH = MAIN_PATH + File.separator + "zzFaces";
    public static final String FACE_IMG_PATH = MAIN_PATH + File.separator + "faceImg";
    public static final String MODEL_PATH = MAIN_PATH + File.separator + "zzFaceModel";
    public static final String LOG_FILE = MAIN_PATH + File.separator + "log.txt";

    public static void initDirectory() {
        File path = new File(FileUtil.PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.MAIN_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.MODEL_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.IMG_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.FACE_IMG_PATH);
        if (!path.exists()) {
            path.mkdirs();
        }
        path = new File(FileUtil.LOG_FILE);
        if (!path.exists()) {
            try {
                path.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readLicence(String licencePath) {
        File lic = new File(licencePath);
        return readFileToString(lic);
    }

    public static void copyAssetsFile(Context context, String fileSrc, String fileDst) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = context.getAssets().open(fileSrc);
            File file = new File(fileDst);
            os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = is.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFileToString(File file) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String readLine;
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 根据byte数组生成文件
     *
     * @param bytes 生成文件用到的byte数组
     */
    public static void createFileWithByte(byte[] bytes, String filePath) throws IOException {
        File file = new File(filePath);
        // 创建FileOutputStream对象
        FileOutputStream outputStream = null;
        // 创建BufferedOutputStream对象
        BufferedOutputStream bufferedOutputStream = null;
        // 如果文件存在则删除
        if (file.exists()) {
            file.delete();
        }
        // 在文件系统中根据路径创建一个新的空文件
        file.createNewFile();
        // 获取FileOutputStream对象
        outputStream = new FileOutputStream(file);
        // 获取BufferedOutputStream对象
        bufferedOutputStream = new BufferedOutputStream(outputStream);
        // 往文件所在的缓冲输出流中写byte数据
        bufferedOutputStream.write(bytes);
        // 刷出缓冲输出流，该步很关键，要是不执行flush()方法，那么文件的内容是空的。
        bufferedOutputStream.flush();
        // 关闭创建的流对象
        outputStream.close();
        bufferedOutputStream.close();

    }

    public static void saveBitmap(String fileName, Bitmap bitmap) throws IOException {
        File avaterFile = new File(fileName);//设置文件名称
        if (avaterFile.exists()) {
            avaterFile.delete();
        }
        avaterFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(avaterFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    public static byte[] inputStream2ByteArray(String filePath) {
        InputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(filePath);
            data = toByteArray(in);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024 * 4];
        int n = 0;
        while ((n = in.read(buffer)) != -1) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

    public static String getLocalMac() {
        String mac = null;
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    mac = str.trim();// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return mac;
    }

    public static void deletefile(String filePath) {
        try {
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDirectoryFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File mFile : files) {
                deleteDirectoryFile(mFile);
            }
        } else if (file.exists()) {
            file.delete();
        }
    }

    public static String pathToBase64(String path) {
        try {
            byte[] bytes = toByteArray(path);
            String str = Base64.encodeToString(bytes, Base64.NO_WRAP);
            bytes = null;
            System.gc();
            return str;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    public static byte[] toByteArray(String filename) throws Exception {

        File f = new File(filename);
        if (!f.exists()) {
            throw new FileNotFoundException(filename);
        }
        BufferedInputStream in = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length())) {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeStringToFile(String path, String content) {
        File file = new File(path);
        FileOutputStream fos = null;
        OutputStreamWriter writer = null;
        try {
            fos = new FileOutputStream(file);
            writer = new OutputStreamWriter(fos, Charset.forName("utf-8"));
            writer.write(content);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
