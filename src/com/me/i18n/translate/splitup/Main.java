package com.me.i18n.translate.splitup;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.LineIterator;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main implements Constants {

  public static void main(String[] args) throws Exception {
    Map<String, String> props = FileOperation.loadPropertiesFile(FileOperation.getCurrentPath() + File.separator + CONFIGURATION_FILE);
    Configuration configuration = new Configuration(props);
    System.out.println(configuration);
    slitpUp(configuration, configuration.getBaseFileNameList().get(0), configuration.getAvailableApplicationResourcesFileList());
    slitpUp(configuration, configuration.getBaseFileNameList().get(1), configuration.getAvailableJSApplicationResourcesFileList());
    System.out.println("Translated SplitUp has completed & Available at \' " + configuration.getOutputFilePath() + " \'");
  }

  private static void slitpUp(Configuration configuration, String baseFileName, List fileList) throws IOException {
    PrintWriter logWriter = FileOperation.getWriter(configuration.getOutputFilePath() + File.separator + LOG_FILE);
    for (Iterator<String> iterator = configuration.getOrderList().iterator(); iterator.hasNext(); ) {
      String orderFilePath = iterator.next();
      logWriter.println("#############################################################");
      logWriter.println("\t\t\t" + orderFilePath);
      logWriter.println("#############################################################\n\n");
      for (Iterator<String> iter = fileList.iterator(); iter.hasNext(); ) {
        String file = iter.next();
        String fileName = FilenameUtils.getName(file);
        logWriter.println("------------------------------------------");
        logWriter.println("\t\t" + fileName);
        logWriter.println("------------------------------------------");
        Map<String, String> properties = FileOperation.loadPropertiesFile(file);
        File theFile = new File(configuration.getLibFilePath() + File.separator + orderFilePath + File.separator + baseFileName + PROPERTIES_EXT);
        PrintWriter writer = FileOperation.getWriter(configuration.getOutputFilePath() + File.separator + OUTPUT + File.separator + orderFilePath + File.separator + fileName);
        LineIterator it = FileUtils.lineIterator(theFile, ENCODING);
        try {
          while (it.hasNext()) {
            String line = it.nextLine().trim();
            if (line.startsWith("#") || line.length() < 2) {
              writer.println(line);
              continue;
            }
            if (line.contains("=")) {
              String keyName = line.substring(0, line.indexOf("="));
              if (properties.keySet().contains(keyName)) {
                String value = properties.get(keyName);
                value = FileOperation.saveConvert(value, false, true);
                writer.println(keyName + "=" + value);
              } else
                logWriter.println(keyName);
            }
          }
        } finally {
          LineIterator.closeQuietly(it);
          writer.close();
        }
      }
    }
    logWriter.close();
  }
}


