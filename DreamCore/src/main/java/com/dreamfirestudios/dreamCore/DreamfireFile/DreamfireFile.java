package com.dreamfirestudios.dreamCore.DreamfireFile;

import com.dreamfirestudios.dreamCore.DreamfireFile.DreamfireDir;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class DreamfireFile {

    /**
     * Deletes the specified file if it exists.
     *
     * @param sourceFile The file to delete.
     */
    public static void DeleteFile(File sourceFile) {
        if (!sourceFile.exists()) return;
        sourceFile.delete();
    }

    /**
     * Creates a new file at the specified directory.
     * If the directory does not exist, it will be created.
     *
     * @param directory  The directory in which to create the file.
     * @param sourceFile The name of the file to create.
     * @throws IOException If the file cannot be created.
     */
    public static void CreateFile(String directory, String sourceFile) throws IOException {
        DreamfireDir.createDirectory(new File(directory));
        var newFile = new File(directory + "/" + sourceFile);
        newFile.createNewFile();
    }

    /**
     * Reads all lines from a file and returns them as a List of strings.
     *
     * @param file The file to read from.
     * @return A List of strings, each representing a line in the file.
     * @throws IOException If the file cannot be read.
     */
    public static List<String> ReadFile(File file) throws IOException {
        if (!file.exists()) throw new FileNotFoundException("File not found: " + file.getPath());
        return Files.readAllLines(file.toPath());
    }

    /**
     * Writes the specified lines to a file. Overwrites the file if it already exists.
     *
     * @param file  The file to write to.
     * @param lines A List of strings to write to the file.
     * @throws IOException If the file cannot be written.
     */
    public static void WriteFile(File file, List<String> lines) throws IOException {
        Files.write(file.toPath(), lines);
    }

    /**
     * Appends the specified lines to a file. Creates the file if it does not exist.
     *
     * @param file  The file to append to.
     * @param lines A List of strings to append to the file.
     * @throws IOException If the file cannot be written.
     */
    public static void AppendToFile(File file, List<String> lines) throws IOException {
        if (!file.exists()) file.createNewFile();
        try (FileWriter writer = new FileWriter(file, true)) {
            for (String line : lines) {
                writer.write(line + System.lineSeparator());
            }
        }
    }

    /**
     * Copies a file to a new location.
     *
     * @param sourceFile      The source file to copy.
     * @param destinationFile The destination file.
     * @throws IOException If the file cannot be copied.
     */
    public static void CopyFile(File sourceFile, File destinationFile) throws IOException {
        if (!sourceFile.exists()) throw new FileNotFoundException("Source file not found: " + sourceFile.getPath());
        Files.copy(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Moves a file to a new location.
     *
     * @param sourceFile      The source file to move.
     * @param destinationFile The destination file.
     * @throws IOException If the file cannot be moved.
     */
    public static void MoveFile(File sourceFile, File destinationFile) throws IOException {
        if (!sourceFile.exists()) throw new FileNotFoundException("Source file not found: " + sourceFile.getPath());
        Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Returns the size of a file in bytes.
     *
     * @param file The file whose size to retrieve.
     * @return The size of the file in bytes.
     */
    public static long GetFileSize(File file) {
        if (!file.exists()) return 0;
        return file.length();
    }

    /**
     * Retrieves the last modified timestamp of a file.
     *
     * @param file The file to check.
     * @return The last modified timestamp of the file.
     */
    public static long GetLastModified(File file) {
        if (!file.exists()) return 0;
        return file.lastModified();
    }

    /**
     * Searches for a file by name within a specified directory (non-recursive).
     *
     * @param directory The directory to search in.
     * @param fileName  The name of the file to search for.
     * @return The file if found, or null if not found.
     */
    public static File SearchFile(File directory, String fileName) {
        if (!directory.exists() || !directory.isDirectory()) return null;
        for (File file : directory.listFiles()) {
            if (file.getName().equalsIgnoreCase(fileName)) {
                return file;
            }
        }
        return null;
    }

    /**
     * Lists all files in a directory (non-recursive).
     *
     * @param directory The directory to list files from.
     * @return A List of Files in the directory.
     */
    public static List<File> ListFiles(File directory) {
        List<File> files = new ArrayList<>();
        if (!directory.exists() || !directory.isDirectory()) return files;
        for (File file : directory.listFiles()) {
            if (file.isFile()) files.add(file);
        }
        return files;
    }

    /**
     * Reads a file as a single string.
     *
     * @param file The file to read.
     * @return The contents of the file as a single string.
     * @throws IOException If the file cannot be read.
     */
    public static String ReadFileAsString(File file) throws IOException {
        if (!file.exists()) throw new FileNotFoundException("File not found: " + file.getPath());
        return new String(Files.readAllBytes(file.toPath()));
    }

    /**
     * Writes a single string to a file. Overwrites the file if it already exists.
     *
     * @param file    The file to write to.
     * @param content The content to write to the file.
     * @throws IOException If the file cannot be written.
     */
    public static void WriteFileFromString(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes());
    }
}
