package org.example.mapper;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

/**
 * Test class for Configuration utility class
 */
public class AbstractDataMapperTest {

  private AbstractDataMapper abstractDataMapper;

  @Before
  public void setupBeforeTest() {
    abstractDataMapper = Mockito.mock(AbstractDataMapper.class, Mockito.CALLS_REAL_METHODS);
  }

  /**
   * Ensure that program errors out when the file is deemed unreadable
   *
   * @throws IOException
   */
  @Test(expected = IOException.class)
  public void testReadFile_UnreadableFile() throws IOException {
    try (MockedStatic<Files> mockedStatic = Mockito.mockStatic(Files.class)) {
      mockedStatic.when(() -> Files.notExists(Mockito.any(Path.class))).thenReturn(false);
      mockedStatic.when(() -> Files.isReadable(Mockito.any(Path.class))).thenReturn(false);

      abstractDataMapper.readFile("/path/to/file");
    }
  }

  /**
   * Use case to ensure program errors out when the file path given does not correlate
   * to an existing file.
   *
   * @throws IOException
   */
  @Test(expected = IOException.class)
  public void testReadFile_NotExists() throws IOException {
    try (MockedStatic<Files> mockedStatic = Mockito.mockStatic(Files.class)) {
      mockedStatic.when(() -> Files.notExists(Mockito.any(Path.class))).thenReturn(true);

      abstractDataMapper.readFile("/path/to/file");
    }
  }

  /**
   * Happy path use case where data is read from the file and returns line data as output
   *
   * @throws IOException
   */
  @Test
  public void testReadFile_HappyPath() throws IOException {
    List<String> dummyData = Arrays.asList("Data1", "Data2", "Data3");
    List<String> output;

    try (MockedStatic<Files> mockedStatic = Mockito.mockStatic(Files.class)) {
      mockedStatic.when(() -> Files.notExists(Mockito.any(Path.class))).thenReturn(false);
      mockedStatic.when(() -> Files.isReadable(Mockito.any(Path.class))).thenReturn(true);
      mockedStatic.when(() -> Files.readAllLines(Mockito.any(Path.class), Mockito.any(Charset.class))).thenReturn(dummyData);

      output = abstractDataMapper.readFile("/path/to/file");
    }

    Assert.assertNotNull(output);
    Assert.assertEquals("Data returned invalid size", 3, output.size());
  }
}
