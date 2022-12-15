package org.example.mapper;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Abstract class for shared methods for the DataMapper classes such as reading the
 * input file's contents.
 */
@Log4j2
public abstract class AbstractDataMapper {

  protected List<String> readFile(String filePathStr) throws IOException {
    Path filePath = Path.of(filePathStr);

    log.debug("Checking if file " + filePathStr + " exists");
    if (Files.notExists(filePath)) {
      throw new IOException("File " + filePathStr + " does not exist");
    }

    log.debug("Checking if file " + filePathStr + " is readable");
    if (!Files.isReadable(filePath)) {
      throw new IOException("File " + filePathStr + " is not readable");
    }

    log.debug("Reading data in file " + filePathStr);
    return Files.readAllLines(filePath, StandardCharsets.UTF_8);
  }

}
