package org.example.mapper;

import org.example.exception.DataException;

import java.io.IOException;

/**
 * Interface for the classes handling the parsing of the Twitter user and tweet data.
 * DataMapper classes are mapped 1-1 with a type of data (POJO class).
 *
 * @param <E> Generic used to return a specific POJO.
 */
public interface DataMapper<E> {

  /**
   * Parse the data using the file path provided; also perform required
   * validation on data itself and build up the POJO based on the type of data
   * parsed (user or tweet).
   *
   * @param filePath The absolute path to the file to read and parse.
   * @return a POJO based on the type of data parsed.
   * @throws IOException   When an error occurs with the reading of the actual file.
   * @throws DataException When an error occurs with the data itself such as malformed or incorrectly formatted.
   */
  E parseData(String filePath) throws IOException, DataException;

}
