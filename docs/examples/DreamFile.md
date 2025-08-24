# DreamFile & DreamDir — Developer Guide with Examples

The **DreamFile** and **DreamDir** utilities form the backbone of file and directory management in the DreamCore framework. They provide safe, convenient, and reusable ways to interact with the filesystem, so you don’t need to constantly re‑implement boilerplate logic.

This guide walks through each core function, written from the perspective of someone completely new to the system.

---

## 📁 DreamDir — Directory Utilities

### `deleteAllFiles(File directory, boolean cascadeFolders)`

Deletes all files inside a given directory.

* If `cascadeFolders` is `true`, it will also remove files from subfolders and delete those folders.
* If `false`, only files directly inside the directory are deleted, subfolders are left untouched.

> ✅ Use this if you want to clear a folder’s contents but keep the folder itself.

---

### `deleteDirectory(File directory)`

Deletes the directory itself (and all of its contents).

* Internally uses `deleteAllFiles` to clean the folder first.

> ✅ Use this when you want to completely remove a directory.

---

### `createDirectory(File directory)`

Ensures the directory exists.

* If it doesn’t, it will create it (including any missing parent directories).

> ✅ Use this before saving files to make sure the path is valid.

---

### `returnAllFilesFromDirectory(File directory, boolean cascadeFolders)`

Returns a `List<File>` of all files in the directory.

* If `cascadeFolders` is `true`, includes files from subdirectories.
* If `false`, only returns files directly inside the directory.

> ✅ Great for scanning through all assets or configs in a folder.

---

### `CopyAllFiles(File dirA, File dirB, ArrayList<String> ignore)`

Copies everything from one directory to another.

* `ignore` allows you to skip files or folders by name.
* Subdirectories are handled recursively.

> ✅ Use this for backups or duplicating resource folders.

---

### `calculateTotalSize(File directory)`

Calculates the **total size** (in bytes) of a directory.

* Includes all subdirectories recursively.

> ✅ Useful for showing storage usage or before zipping large directories.

---

### `moveAllFiles(File srcDir, File destDir, List<String> ignore)`

Moves files from one directory to another.

* `ignore` lets you skip specific files or directories.
* Subfolders are created in the destination if needed.

> ✅ Use this when archiving logs or migrating data.

---

### `searchFiles(File directory, String query, boolean cascadeFolders)`

Searches for files by name.

* If `cascadeFolders` is `true`, the search includes subdirectories.
* Matches if the filename **contains** the query string.

> ✅ Use this to quickly find files with partial matches (e.g., `"config"`).

---

### `compareDirectories(File dirA, File dirB)`

Compares two directories.

* Returns `true` if they have the same structure and files with the same sizes.

> ✅ Use this to verify backups or sync operations.

---

### `zipDirectory(File directory, File zipFile)`

Compresses a directory into a `.zip` file.

* Recursively includes all files and subdirectories.

> ✅ Perfect for packaging logs, configs, or backups.

---

### `extractZip(File zipFile, File destDir)`

Extracts a `.zip` file into a target directory.

* Creates missing directories automatically.

> ✅ Use this to unpack resources or deploy archives.

---

### `getFilesModifiedAfter(File directory, long timestamp, boolean cascadeFolders)`

Returns all files modified **after** a given timestamp.

* Useful for detecting recently changed files.

> ✅ Use this for incremental backups or monitoring changes.

---

### `sortFiles(File directory, Comparator<File> comparator)`

Sorts all files in a directory based on the provided comparator.

* Example comparators provided:

    * `sortByName`
    * `sortBySize`
    * `sortByLastModified`

> ✅ Use this for listing files in UI menus or prioritizing file processing order.

---

## 📄 DreamFile — File Utilities

### `DeleteFile(File sourceFile)`

Deletes a file if it exists.

> ✅ Use this for cleanup or temporary files.

---

### `CreateFile(String directory, String sourceFile)`

Creates a file inside a directory.

* Creates the directory if it doesn’t exist.

> ✅ Use this for writing config or data files safely.

---

### `ReadFile(File file)`

Reads all lines from a file into a `List<String>`.

> ✅ Use this for configs or text-based assets.

---

### `WriteFile(File file, List<String> lines)`

Writes a list of strings into a file.

* Overwrites existing contents.

> ✅ Use this for saving logs, configs, or reports.

---

### `AppendToFile(File file, List<String> lines)`

Appends lines to the end of a file.

* Creates the file if it doesn’t exist.

> ✅ Use this for logging systems.

---

### `CopyFile(File sourceFile, File destinationFile)`

Copies one file to another location.

> ✅ Use this for making backups or duplicating templates.

---

### `MoveFile(File sourceFile, File destinationFile)`

Moves one file to another location.

* Overwrites if destination exists.

> ✅ Use this for renaming or reorganizing files.

---

### `GetFileSize(File file)`

Returns the size of the file in bytes.

> ✅ Use this for monitoring storage or validating limits.

---

### `GetLastModified(File file)`

Gets the last modified timestamp of a file.

> ✅ Use this for change detection.

---

### `SearchFile(File directory, String fileName)`

Finds a file by **exact name** inside a directory (non‑recursive).

> ✅ Use this for quick lookups in a flat folder.

---

### `ListFiles(File directory)`

Lists all files (non‑recursive) in a directory.

> ✅ Use this for showing available configs or resources.

---

### `ReadFileAsString(File file)`

Reads an entire file into a single string.

> ✅ Use this for JSON, XML, or other structured files.

---

### `WriteFileFromString(File file, String content)`

Writes a string into a file.

* Overwrites existing content.

> ✅ Use this for serialized data or generated output.

---

## 💡 Suggestions for Future Improvements

* Consider splitting **DreamFile** and **DreamDir** into interfaces + implementations for testability.
* Replace `System.out` and `System.err` with a logging framework (SLF4J / Log4j).
* Standardize naming (`DeleteFile` vs `deleteDirectory`) — pick consistent casing.
* Add async variants (`CompletableFuture`) for large file operations.
* Provide exception logging rather than empty `catch` blocks.
* Replace `ArrayList<String>` ignores with `Set<String>` for faster lookups.
* Add unit tests for zip/unzip, move, and compare operations.