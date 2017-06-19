package com.me.i18n.translate.splitup;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by mark-4304 on 20-May-17.
 */
public class Configuration implements Constants {

  private String libFilePath;
  private String confFilePath;
  private String updateResourcesFilePath;
  private String outputFilePath;
  private List<String> availableApplicationResourcesFileList;
  private List<String> availableJSApplicationResourcesFileList;
  private List<String> baseFileNameList = new LinkedList<String>() {{
    add("ApplicationResources");
    add("JSApplicationResources");
  }};

  public Configuration(Map<String, String> props) throws NullPointerException, UnsupportedEncodingException {
    this.libFilePath = getProperties(props, LIB_FILE_PATH);
    this.confFilePath = getProperties(props, CONF_FILE_PATH);
    this.updateResourcesFilePath = getProperties(props, UPDATE_RESOUCES_PATH);
    this.outputFilePath = getProperties(props, OUTPUT_FILE_PATH);
    this.availableApplicationResourcesFileList = getAvailableFileList(this.baseFileNameList.get(0));
    this.availableJSApplicationResourcesFileList = getAvailableFileList(this.baseFileNameList.get(1));
  }

  private String getProperties(Map<String, String> props, String key) {
    final String value = props.get(key);
    if (value == null) {
      throw new NullPointerException(key + " is required field to execute the program ...");
    }
    return value;
  }

  public String getLibFilePath() {
    return libFilePath;
  }

  public String getUpdateResourcesFilePath() {
    return updateResourcesFilePath;
  }

  public String getOutputFilePath() {
    return outputFilePath;
  }

  public List<String> getBaseFileNameList() {
    return baseFileNameList;
  }

  public List<String> getOrderList() throws IOException {
    List<String> orderList = new LinkedList<String>();
    final String file = this.confFilePath + File.separator + "resourceBundleOrder.properties";
    Map<String, String> prop = FileOperation.loadPropertiesFile(file);
    int i = 1;
    while (true) {
      String value = prop.get("order" + i++);
      if (value == null){
        break;
      }
      value = value.replace(".",File.separator).trim();
      orderList.add(value);
    }
    return orderList;
  }

  private List<String> getAvailableFileList(String fileName) throws UnsupportedEncodingException {
    List<String> fileList = FileOperation.getFileList(this.updateResourcesFilePath);
    List<String> removeList = new LinkedList<String>();
    for (Iterator<String> iterator = fileList.iterator(); iterator.hasNext(); ) {
      String file = iterator.next();
      String baseFileName = FilenameUtils.getBaseName(file);
      if (!baseFileName.startsWith(fileName)) {
        removeList.add(file);
      }
    }
    fileList.removeAll(removeList);
    return fileList;
  }

  public List<String> getAvailableApplicationResourcesFileList() {
    return availableApplicationResourcesFileList;
  }

  public List<String> getAvailableJSApplicationResourcesFileList() {
    return availableJSApplicationResourcesFileList;
  }

  @Override
  public String toString() {
    return "{\"Configuration\":{"
            + "\n\"libFilePath\":\"" + libFilePath + "\""
            + "\n, \"confFilePath\":\"" + confFilePath + "\""
            + "\n, \"updateResourcesFilePath\":\"" + updateResourcesFilePath + "\""
            + "\n, \"outputFilePath\":\"" + outputFilePath + "\""
            + "\n, \"baseFileNameList\":" + Arrays.asList(baseFileNameList)
            + "\n, \"availableApplicationResourcesFileList\":" + availableApplicationResourcesFileList
            + "\n, \"availableJSApplicationResourcesFileList\":" + availableJSApplicationResourcesFileList
            + "\n}" +
            "\n}";
  }
}
