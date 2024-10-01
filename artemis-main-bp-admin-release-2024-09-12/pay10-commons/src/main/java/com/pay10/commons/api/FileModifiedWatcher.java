package com.pay10.commons.api;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import com.pay10.commons.util.PropertiesManager;

public class FileModifiedWatcher {
	private static File file;
	private static int pollingInterval;
	private static Timer fileWatcher;
	private static long lastReadTimeStamp = 0L;
	private static final String ymlFileLocation = System.getenv("BPGATE_PROPS");
	private final static YamlPropertySourceLoader loader = new YamlPropertySourceLoader();
	private static Logger logger = LoggerFactory.getLogger(FileModifiedWatcher.class.getName());

	public static boolean init(String _file, int _pollingInterval) {
		file = new File(_file);
		pollingInterval = _pollingInterval; // In seconds

		watchFile();

		return true;
	}

	private static void watchFile() {
		if (null == fileWatcher) {

			logger.info("Started file watch service for application.yml");
			fileWatcher = new Timer();

			fileWatcher.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {

					
					// Changed by shaiwal to always update the yml file without comparing with history
					// This way all servers will have updated files even if it was updated on only one server on production
					
					if (true) {
						
						logger.info("File modified , using updated values for application.yml");

						Resource resource = new FileSystemResource(ymlFileLocation + "application.yml");
						List<PropertySource<?>> propertySource = loadYaml(resource);
						
						for (PropertySource ps : propertySource ) {
							
							
			                if (ps instanceof EnumerablePropertySource) {
			                    for (String key : ((EnumerablePropertySource) ps).getPropertyNames()) {
			                        if (key != null && ps.getProperty(key) != null) {
			                        	PropertiesManager.propertiesMap.put(key, ps.getProperty(key).toString());
			                        }
			                    }
			                }
					}

					
					}

					lastReadTimeStamp = System.currentTimeMillis();
				}
			}, 0, 1000 * pollingInterval);
		}

	}

	private static List<PropertySource<?>> loadYaml(Resource path) {
		if (!path.exists()) {
			throw new IllegalArgumentException("Resource " + path + " does not exist");
		}
		try {
			return	new YamlPropertySourceLoader().load("custom-resource", path);
		}
		catch (IOException ex) {
			throw new IllegalStateException(
					"Failed to load yaml configuration from " + path, ex);
		}
	}
}