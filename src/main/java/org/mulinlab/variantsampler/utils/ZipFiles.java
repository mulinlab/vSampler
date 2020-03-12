package org.mulinlab.variantsampler.utils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFiles {
    public static void zipDirectory(File dir, String zipDirName) {
        try {
            List<String> filesListInDir = populateFilesList(dir);
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(String filePath : filesListInDir){
                writeZipFile(new File(filePath), zos);
                zos.closeEntry();
            }
            zos.close();
            fos.close();

            File[] listFiles = dir.listFiles();
            for(File file : listFiles){
                file.delete();
            }
            System.out.println("Deleting Directory. Success = " + dir.delete());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeZipFile(File file, ZipOutputStream zos) throws IOException {
        ZipEntry ze = new ZipEntry(file.getName());
        zos.putNextEntry(ze);
        //read the file and write to ZipOutputStream
        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }
        fis.close();
    }

    private static List<String> populateFilesList(File dir) throws IOException {
        List<String> filesListInDir  = new ArrayList<String>();
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isFile()) filesListInDir.add(file.getAbsolutePath());
            else populateFilesList(file);
        }
        return filesListInDir;
    }

    private static void zipSingleFile(File file, String zipFileName) {
        try {
            //create ZipOutputStream to write to the zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            //add a new Zip Entry to the ZipOutputStream

            writeZipFile(file, zos);
            //Close the zip entry to write to zip file
            zos.closeEntry();
            //Close resources
            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
