package edu.brown.cs.student.main.server.csvfuncs;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Class for loading csv files. */
public class LoadCSV {

  /**
   * Loads a CSV file from the specified file path.
   *
   * @param filepath The file path of the CSV file to load.
   * @throws FileNotFoundException If the file does not exist or is not in the correct directory.
   * @return the name of the file that was loaded
   */
  public String loadCSV(String filepath) throws FileNotFoundException {
    Path path = Paths.get(filepath).toAbsolutePath();
    Path dataDirectory = Paths.get("data/resources").toAbsolutePath();

    // If the filepath isn't in our specified data folder, throw an exception
    if (!path.startsWith(dataDirectory)) {
      throw new FileNotFoundException("File not in resources folder");
    }
    // Same for if it doesn't exist
    if (!path.toFile().exists()) {
      throw new FileNotFoundException("File not found at the specified path");
    }

    // Get the filename
    String filename = path.getFileName().toString();

    // Remove extension if present
    int dotIndex = filename.lastIndexOf('.');
    if (dotIndex > 0) { // Ensure there's a dot and it's not at the start
      filename = filename.substring(0, dotIndex); // Remove the extension
    }

    // Return the filename without extension
    return filename;
  }
}
