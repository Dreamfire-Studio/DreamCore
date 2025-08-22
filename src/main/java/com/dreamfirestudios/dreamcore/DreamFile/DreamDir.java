package com.dreamfirestudios.dreamcore.DreamFile;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.zip.*;

public class DreamDir {

    /**
     * Deletes all files in the specified directory. If `cascadeFolders` is true, deletes subdirectories as well.
     */
    public static void deleteAllFiles(File directory, boolean cascadeFolders) {
        if (!directory.exists() || !directory.isDirectory()) return;

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory() && cascadeFolders) {
                deleteAllFiles(file, true);
                file.delete(); // Delete the directory after its contents
            } else {
                file.delete();
            }
        }
    }

    /**
     * Deletes the specified directory if it exists.
     */
    public static void deleteDirectory(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            deleteAllFiles(directory, true);
            directory.delete();
        }
    }

    /**
     * Creates the specified directory if it does not exist.
     */
    public static void createDirectory(File directory) {
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * Returns a list of all files in the specified directory. If `cascadeFolders` is true, includes files from subdirectories.
     */
    public static List<File> returnAllFilesFromDirectory(File directory, boolean cascadeFolders) {
        List<File> fileList = new ArrayList<>();
        if (!directory.exists() || !directory.isDirectory()) return fileList;

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory() && cascadeFolders) {
                fileList.addAll(returnAllFilesFromDirectory(file, true));
            } else {
                fileList.add(file);
            }
        }
        return fileList;
    }

    /**
     * Copies all files from one directory to another, respecting the ignore list.
     */
    public static void CopyAllFiles(File dirA, File dirB, ArrayList<String> ignore){
        try {
            if(!ignore.contains(dirA.getName())) {
                if(dirA.isDirectory()) {
                    if(!dirB.exists())
                        dirB.mkdirs();
                    String files[] = dirA.list();
                    for (String file : files) {
                        File srcFile = new File(dirA, file);
                        File destFile = new File(dirB, file);
                        CopyAllFiles(srcFile, destFile, ignore);
                    }
                } else {
                    InputStream in = new FileInputStream(dirA);
                    OutputStream out = new FileOutputStream(dirB);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) { }
    }

    public static long calculateTotalSize(File directory) {
        if (!directory.exists()) return 0;

        long totalSize = 0;
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory()) {
                totalSize += calculateTotalSize(file);
            } else {
                totalSize += file.length();
            }
        }
        return totalSize;
    }

    public static void moveAllFiles(File srcDir, File destDir, List<String> ignore) {
        if (!srcDir.exists()) return;

        createDirectory(destDir); // Ensure the destination directory exists

        for (File file : Objects.requireNonNull(srcDir.listFiles())) {
            if (ignore.contains(file.getName())) continue;

            File destFile = new File(destDir, file.getName());
            if (file.isDirectory()) {
                moveAllFiles(file, destFile, ignore);
                file.delete(); // Delete empty source directory
            } else {
                if (file.renameTo(destFile)) {
                    System.out.println("Moved file: " + file.getName());
                } else {
                    System.err.println("Failed to move file: " + file.getName());
                }
            }
        }
    }

    public static List<File> searchFiles(File directory, String query, boolean cascadeFolders) {
        List<File> result = new ArrayList<>();
        if (!directory.exists() || !directory.isDirectory()) return result;

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory() && cascadeFolders) {
                result.addAll(searchFiles(file, query, true));
            } else if (file.getName().contains(query)) {
                result.add(file);
            }
        }
        return result;
    }

    public static boolean compareDirectories(File dirA, File dirB) {
        if (!dirA.exists() || !dirB.exists()) return false;

        File[] filesA = dirA.listFiles();
        File[] filesB = dirB.listFiles();

        if (filesA == null || filesB == null || filesA.length != filesB.length) return false;

        for (int i = 0; i < filesA.length; i++) {
            File fileA = filesA[i];
            File fileB = new File(dirB, fileA.getName());

            if (fileA.isDirectory()) {
                if (!compareDirectories(fileA, fileB)) return false;
            } else if (!fileB.exists() || fileA.length() != fileB.length()) {
                return false;
            }
        }
        return true;
    }

    public static void zipDirectory(File directory, File zipFile) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            zipFile(directory, directory.getName(), zos);
        }
    }

    public static void extractZip(File zipFile, File destDir) throws IOException {
        if (!destDir.exists()) destDir.mkdirs();

        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(destDir, zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    newFile.mkdirs();
                } else {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, length);
                        }
                    }
                }
                zis.closeEntry();
            }
        }
    }

    public static List<File> getFilesModifiedAfter(File directory, long timestamp, boolean cascadeFolders) {
        List<File> files = new ArrayList<>();
        if (!directory.exists() || !directory.isDirectory()) return files;

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isDirectory() && cascadeFolders) {
                files.addAll(getFilesModifiedAfter(file, timestamp, true));
            } else if (file.lastModified() > timestamp) {
                files.add(file);
            }
        }
        return files;
    }

    public static List<File> sortFiles(File directory, Comparator<File> comparator) {
        List<File> files = new ArrayList<>(List.of(Objects.requireNonNull(directory.listFiles())));
        files.sort(comparator);
        return files;
    }

    // Example Comparators
    public static Comparator<File> sortByName = Comparator.comparing(File::getName);
    public static Comparator<File> sortBySize = Comparator.comparingLong(File::length);
    public static Comparator<File> sortByLastModified = Comparator.comparingLong(File::lastModified);

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) return;

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zos.putNextEntry(new ZipEntry(fileName));
                zos.closeEntry();
            } else {
                zos.putNextEntry(new ZipEntry(fileName + "/"));
                zos.closeEntry();
            }
            for (File childFile : Objects.requireNonNull(fileToZip.listFiles())) {
                zipFile(childFile, fileName + "/" + childFile.getName(), zos);
            }
            return;
        }

        try (FileInputStream fis = new FileInputStream(fileToZip)) {
            ZipEntry zipEntry = new ZipEntry(fileName);
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                zos.write(buffer, 0, length);
            }
        }
    }
}
