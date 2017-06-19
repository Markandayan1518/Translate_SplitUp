package com.me.i18n.translate.splitup;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author mark-4304
 */
public class FileOperation {

  public static final String SEPARATOR = "/";  //No I18N
  private static final Logger LOG = Logger.getLogger(FileOperation.class.getName());
  private static final Logger LOGGER = Logger.getLogger(FileOperation.class.getName());
  /**
   * A table of hex digits
   */
  private static final char[] hexDigit = {
          '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
  };
  private static String destinationDir;

  public static String getDestinationDir() {
    return destinationDir;
  }

  public static void setDestinationDir(String destinationDir) {
    FileOperation.destinationDir = destinationDir;
  }

  public static String getFileName(String filePath) {
    String fileName = null;
    try {
      int startIndex = filePath.lastIndexOf(File.separator);
      int endIndex = filePath.lastIndexOf(".");
      fileName = filePath.substring(startIndex + 1, endIndex);
    } catch (StringIndexOutOfBoundsException e) {
      LOGGER.log(Level.SEVERE, "Error Getting File Name : " + filePath, e); //No I18N
    }
    return fileName;
  }

  public static void makeDirectory(String directoryPath) {
    File directory = new File(directoryPath);
    if (!directory.exists()) {
      try {
        if (directory.mkdir()) {
          LOGGER.log(Level.INFO, directory + " : Directory created"); //No I18N
        }
      } catch (SecurityException ex) {
        LOGGER.log(Level.SEVERE, directory.getAbsolutePath(), ex);
      }
    }
  }

  public static String getDownloadFolder(String outputPath) {
    return outputPath.substring(outputPath.lastIndexOf("download")); //No I18N
  }

  public static String getWebappsFolder() {
    return (new File(System.getProperty("user.dir"))).getParent() + File.separator + "webapps"; //No I18N
  }

  public static String getLastPart(String filePath) {
    return filePath;
  }

  public static List<String> getFileList(String folderPath) throws UnsupportedEncodingException {
    List<String> fileList = new LinkedList<String>();
    FileOperation.listFilesForFolder(folderPath, fileList);
    return fileList;
  }

  public static Map loadPropertiesFile(String file) throws IOException {
    Properties prop = new Properties();
    InputStream in = null;
    try {
      in = new FileInputStream(file);
      prop.load(in);
    } catch (FileNotFoundException e) {
      System.err.println("Oops ! we cant find the file: "+ file);
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }finally {
      if (in != null) {
        in.close();
      }
    }
    Map<String, String> map = new TreeMap<String, String>();
    Iterator iterator = prop.keySet().iterator();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      String value = (String) prop.get(key);
      //Handling for EscapeChars
      value = StringEscapeUtils.escapeJava(value);
      map.put(key, value);
    }
    return map;
  }

  public static PrintWriter getWriter(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
    File file = new File(fileName);
    file.getParentFile().mkdirs();
    PrintWriter writer = new PrintWriter(file, "UTF-8");
    return writer;
  }

  public static void generatedReportsInTextFile(String path, String fileName, Map map) throws FileNotFoundException, UnsupportedEncodingException {
    final String fileFormat = ".txt";
    File file = new File(path + File.separator + fileName + fileFormat);
    file.getParentFile().mkdirs();
    PrintWriter writer = new PrintWriter(file, "UTF-8");
    Iterator<String> iterator = map.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      writer.println(key + "=" + map.get(key));
    }
    writer.close();
    LOG.log(Level.INFO, "Generated Report Text File : {0}", file);
  }

  public static void generatedReportsInTextFile(String path, String fileName, Set set) throws FileNotFoundException, UnsupportedEncodingException {
    final String fileFormat = ".txt";
    File file = new File(path + File.separator + fileName + fileFormat);
    file.getParentFile().mkdirs();
    PrintWriter writer = new PrintWriter(file, "UTF-8");
    Iterator<String> iterator = set.iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      writer.println(key);
    }
    writer.close();
    LOG.log(Level.INFO, "Generated Report Text File : {0}", file);
  }

  public static void listFilesForFolder(File folderPath, List fileList) throws UnsupportedEncodingException {
    File folder = new File(URLDecoder.decode(folderPath.toString(), "UTF-8"));
    File[] listFiles = folder.listFiles();
    for (File fileEntry : listFiles) {
      if (fileEntry.isDirectory()) {
        listFilesForFolder(fileEntry, fileList);
      } else {
        String file = fileEntry.toString().trim();
        fileList.add(file);
      }
    }
  }

  public static void listFilesForFolder(String folderPath, List fileList) throws UnsupportedEncodingException {
    listFilesForFolder(new File(folderPath + File.separator), fileList);
  }

  public static String saveConvert(String theString,
                                   boolean escapeSpace,
                                   boolean escapeUnicode) {
    int len = theString.length();
    int bufLen = len * 2;
    if (bufLen < 0) {
      bufLen = Integer.MAX_VALUE;
    }
    StringBuffer outBuffer = new StringBuffer(bufLen);
    for (int x = 0; x < len; x++) {
      char aChar = theString.charAt(x);
      // Handle common case first, selecting largest block that
      // avoids the specials below
      if ((aChar > 61) && (aChar < 127)) {
        if (aChar == '\\') {
          outBuffer.append('\\');
          continue;
        }
        outBuffer.append(aChar);
        continue;
      }
      switch (aChar) {
        case ' ':
          if (x == 0 || escapeSpace) {
            outBuffer.append('\\');
          }
          outBuffer.append(' ');
          break;
        case '\t':
          outBuffer.append('t');
          break;
        case '\n':
          outBuffer.append('n');
          break;
        case '\r':
          outBuffer.append('r');
          break;
        case '\f':
          outBuffer.append('f');
          break;
        case '=': // Fall through
        case ':': // Fall through
        case '#': // Fall through
        case '!':
          outBuffer.append(aChar);
          break;
        default:
          if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode) {
            outBuffer.append('u');
            outBuffer.append(toHex((aChar >> 12) & 0xF));
            outBuffer.append(toHex((aChar >> 8) & 0xF));
            outBuffer.append(toHex((aChar >> 4) & 0xF));
            outBuffer.append(toHex(aChar & 0xF));
          } else {
            outBuffer.append(aChar);
          }
      }
    }
    return outBuffer.toString();
  }

  private static char toHex(int nibble) {
    return hexDigit[(nibble & 0xF)];
  }

  public static String unicodeEscapedForSpace(String string) {
    StringBuilder builder = new StringBuilder();
    for (char ch : string.toCharArray()) {
      if (Character.isSpaceChar(ch)) {
        builder.append(unicodeEscaped(ch));
      } else {
        builder.append(ch);
      }
    }
    return builder.toString();
  }

  public static String unicodeEscaped(char ch) {
    if (ch < 0x10) {
      return "\\u000" + Integer.toHexString(ch);
    } else if (ch < 0x100) {
      return "\\u00" + Integer.toHexString(ch);
    } else if (ch < 0x1000) {
      return "\\u0" + Integer.toHexString(ch);
    }
    return "\\u" + Integer.toHexString(ch);
  }

  public static String unicodeEscaped(Character ch) {
    if (ch == null) {
      return null;
    }
    return unicodeEscaped(ch.charValue());
  }

  public static void getPropertiesFileWriter(String path, String fileName, Map map) throws FileNotFoundException, UnsupportedEncodingException {
    File file = new File(path + File.separator + fileName + ".properties");
    file.getParentFile().mkdirs();
    PrintWriter writer = new PrintWriter(file, "UTF-8");
    Iterator<String> iterator = map.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      String value = (String) map.get(key);
      key = unicodeEscapedForSpace(key);
      value = saveConvert(value, false, true);
      writer.println(key + "=" + value);
    }
    writer.close();
  }

  public static void getPropertiesFileWriterWithoutExtraKeys(String path, String fileName, Map defaultMap, Map map) throws FileNotFoundException, UnsupportedEncodingException {
    File file = new File(path + File.separator + fileName + ".properties");
    file.getParentFile().mkdirs();
    PrintWriter writer = new PrintWriter(file, "UTF-8");
    Iterator<String> iterator = map.keySet().iterator();
    while (iterator.hasNext()) {
      String key = iterator.next();
      String value = (String) map.get(key);
      key = unicodeEscapedForSpace(key);
      value = saveConvert(value, false, true);
      if (defaultMap.containsKey(key)) {
        writer.println(key + "=" + value);
      }
    }
    writer.close();
  }

  public static void moveDirectoryToDirectory(File srcDir, File destDir) throws IOException {
    destDir.mkdirs();
    FileUtils.copyDirectoryToDirectory(srcDir, destDir);
    FileUtils.deleteDirectory(srcDir);
  }

  public static void createFolderForOutput(String folderPath) {
    File file = new File(folderPath);
    file.mkdirs();
  }

  /**
   * Gets current path.
   *
   * @return the current path
   */
  public static String getCurrentPath() {
    URL location = Main.class.getProtectionDomain().getCodeSource()
            .getLocation();
    String path = location.getFile();
    final String parent = new File(path).getParent();
    String result = null;
    try {
      result = URLDecoder.decode(parent, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return result;
  }

  public static String getFolderName(String line) {
    line = line.substring(line.indexOf("\\") + 1);
    try {
      line = line.substring(0, line.indexOf("\\"));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return line;
  }

  public static Map<String, LinkedList<String>> getPropertiesContentWithLineNumberInValue(String fileStr) {
    File file = new File(fileStr);
    Scanner scanner = null;
    Map<String, LinkedList<String>> map = new TreeMap<String, LinkedList<String>>();
    String keyName = null;
    String value = null;
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException ex) {
      LOG.log(Level.SEVERE, "File Not Found", ex);  //No I18N
    }
    int lineNumber = 0;
    //scanning line by line
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      lineNumber++;
      //skip line supports starts with ## or empty line
      if (line.startsWith(" ") || line.startsWith("#") || line.length() < 2) {
        continue;
      }
      //finding key name
      String[] strArray = line.split("=");  //No I18N
      keyName = strArray[0].trim();
      try {
        value = line.substring(strArray[0].length() + 1).trim();
      } catch (Exception e) {
        System.out.println("Line :" + line);
        System.err.println(e);
      }
      LinkedList<String> vauleList = null;
      value = "Line " + lineNumber + " : " + StringEscapeUtils.escapeJava(value);
      keyName = unicodeEscapedForSpace(keyName);
      if (map.containsKey(keyName)) {
        vauleList = map.get(keyName);
      } else {
        vauleList = new LinkedList<String>();
      }
      vauleList.add(value);
      map.put(keyName, vauleList);
      while (line.endsWith("\\") && scanner.hasNext()) //No I18N
      {
        line = scanner.nextLine();
      }
    }
    scanner.close();
    return map;
  }

  public static Map<String, Set<String>> getPropertiesContentWithLineNumberInKey(String fileStr) {
    File file = new File(fileStr);
    Scanner scanner = null;
    Map<String, Set<String>> map = new TreeMap<String, Set<String>>();
    String keyName = null;
    String value = null;
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException ex) {
      LOG.log(Level.SEVERE, "File Not Found", ex);  //No I18N
    }
    int lineNumber = 0;
    //scanning line by line
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      lineNumber++;
      //skip line supports starts with ## or empty line
      if (line.startsWith(" ") || line.startsWith("#") || line.length() < 2) {
        continue;
      }
      //finding key name
      String[] strArray = line.split("=");  //No I18N
      keyName = strArray[0].trim();
      try {
        value = line.substring(strArray[0].length() + 1).trim();
      } catch (Exception e) {
        System.out.println("Line :" + line);
        System.err.println(e);
      }
      Set<String> vauleList = null;
      value = StringEscapeUtils.escapeJava(value);
      keyName = "Line " + lineNumber + " : " + unicodeEscapedForSpace(keyName);
      if (map.containsKey(keyName)) {
        vauleList = map.get(keyName);
      } else {
        vauleList = new LinkedHashSet<String>();
      }
      vauleList.add(value);
      map.put(keyName, vauleList);
      while (line.endsWith("\\") && scanner.hasNext()) //No I18N
      {
        line = scanner.nextLine();
      }
    }
    scanner.close();
    return map;
  }

  public static void copyDirectoryToDirectory(String srcDirStr, String destDirStr) throws IOException {
    File srcDir = new File(srcDirStr);
    File destDir = new File(destDirStr);
    destDir.mkdirs();
    FileUtils.copyDirectoryToDirectory(srcDir, destDir);
  }

  public static Map loadTextFile(String file, String propFile) throws IOException {
    Properties prop = new Properties();
    InputStream in = new FileInputStream(propFile);
    prop.load(in);
    in.close();
    Map<String, String> map = new TreeMap<String, String>();
    Scanner scanner = new Scanner(new File(file));
    while (scanner.hasNext()) {
      String key = (String) scanner.next().trim();
      if (key != null) {
        String value = (String) prop.get(key);
        //Handling for EscapeChars
        value = StringEscapeUtils.escapeJava(value);
        map.put(key, value);
      }
    }
    return map;
  }
}


