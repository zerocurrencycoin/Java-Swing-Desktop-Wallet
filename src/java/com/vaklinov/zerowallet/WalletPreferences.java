package com.vaklinov.zerowallet;

import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import com.eclipsesource.json.*;
import com.vaklinov.zerowallet.OSUtil.OS_TYPE;

/**
 * Preferences reading and writing
 * 
 * @author Ângelo Andrade Cirino <aacirino@gmail.com>
 */

public class WalletPreferences {
	public static final Integer mainnetRPCPortDefault = 23801;
	public static final Integer testnetRPCPortDefault = 23802;
	public static final String  commandLineToolsDirDefault = "/usr/local/bin/zero";
	
	private JsonObject	preferences;
	private Integer		mainnetRPCPort;
	private Integer		testnetRPCPort;
	private String		preferencesDir;
	private String		preferencesFileName;
	private String		commandLineToolsDir;
	private Boolean		mustSave;
	private File		preferencesFile;
	
	public WalletPreferences(OS_TYPE os) {
		String preferencesFileDir = new String();
		mustSave = false;

		try {
			preferencesFileDir = OSUtil.getSettingsDirectory();
		} catch (IOException e2) {
			JOptionPane.showMessageDialog(new JFrame(),
					"There was an error getting the preferences file.\nThe application will now terminate",
					"File Error",
			        JOptionPane.ERROR_MESSAGE);
			e2.printStackTrace();
			System.exit(3);
		}
		Log.info("Preferences file in " + preferencesFileDir + " directory");
		preferencesFile = new File(preferencesFileDir, "ZeroSwingWalletUI.prefs");
		Log.info("Will read the preferences");
		Reader preferencesReader = null;
		try {
			preferencesReader = new FileReader(preferencesFile);
		} catch (FileNotFoundException e) {
			try {
				preferencesFile.createNewFile();
				preferencesReader = new FileReader(preferencesFile);
				mustSave = true;
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(new JFrame(),
						"There was an error creating the preferences file.\nThe application will now terminate",
						"File Error",
				        JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
				System.exit(3);
			}
		}
		
		// Parse the preferences applying default values when absent
		
		try {
			preferences = Json.parse(preferencesReader).asObject();
		} catch (Exception e) {
			// The preferences file was just created and so is empty
			preferences = new JsonObject();
		}
		
		mainnetRPCPort = preferences.getInt("mainnetRPCPort", mainnetRPCPortDefault);
		if (preferences.get("mainnetRPCPort") == null) {
			mustSave = true;
			preferences.add("mainnetRPCPort", mainnetRPCPortDefault);
		}
		
		testnetRPCPort = preferences.getInt("testnetRPCPort", testnetRPCPortDefault);
		if (preferences.get("testnetRPCPort") == null) {
			mustSave = true;
			preferences.add("testnetRPCPort", testnetRPCPortDefault);
		}
		
		String defaultCommandLineToolsDir = new String();
		if (os == OS_TYPE.LINUX || os == OS_TYPE.MAC_OS)
			defaultCommandLineToolsDir = commandLineToolsDirDefault;
		else
			defaultCommandLineToolsDir = System.getenv("APPDATA") + "\\Zero";

		if (!Files.isDirectory(Paths.get(defaultCommandLineToolsDir))) {
			Log.info("Will ask for the directory where the command line tools are");
			JOptionPane.showMessageDialog(new JFrame(),
					"Please select the directory where the command line utils are installed:\nzerod, zero-cli and zero-tx",
					"Directory selection",
			        JOptionPane.INFORMATION_MESSAGE);
			JFileChooser dirChooser = new JFileChooser();
			if (os == OS_TYPE.WINDOWS)
				dirChooser.setCurrentDirectory(new java.io.File("C:"));
			else
				dirChooser.setCurrentDirectory(new java.io.File("/"));
			dirChooser.setDialogTitle("Select the directory where the command line tools are");
			dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			dirChooser.setAcceptAllFileFilterUsed(false);
			while (dirChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
				;
			defaultCommandLineToolsDir = dirChooser.getCurrentDirectory().getAbsolutePath() + (os == OS_TYPE.WINDOWS ? "\\" : "/") + dirChooser.getSelectedFile().getName();
		}

		commandLineToolsDir = preferences.getString("commandLineToolsDir", defaultCommandLineToolsDir);
		if (preferences.get("commandLineToolsDir") == null) {
			mustSave = true;
			preferences.add("commandLineToolsDir", defaultCommandLineToolsDir);
		}
		
		Log.info("Preferences");
		Log.info("mainnetRPCPort : " + Integer.toString(mainnetRPCPort));
		Log.info("testnetRPCPort : " + Integer.toString(mainnetRPCPort));
		Log.info("commandLineToolsDir : " + commandLineToolsDir);
	}
	
	public Integer mainnetRPCPort() {
		return mainnetRPCPort;
	}
	
	public void mainnetRPCPort(Integer mainnetRPCPort) {
		mustSave = true;
		this.mainnetRPCPort = mainnetRPCPort;
	}

	public Integer testnetRPCPort() {
		return testnetRPCPort;
	}

	public void testnetRPCPort(Integer testnetRPCPort) {
		mustSave = true;
		this.testnetRPCPort = testnetRPCPort;
	}

	public String preferencesDir() {
		return preferencesDir;
	}

	public void preferencesDir(String peferencesDir) {
		mustSave = true;
		this.preferencesDir = peferencesDir;
	}

	public String preferencesFileName() {
		return preferencesFileName;
	}

	public void preferencesFileName(String zeroSwingWalletUIName) {
		mustSave = true;
		this.preferencesFileName = zeroSwingWalletUIName;
	}

	public String commandLineToolsDir() {
		return commandLineToolsDir;
	}

	public void commandLineToolsDir(String commandLineToolsDir) {
		mustSave = true;
		this.commandLineToolsDir = commandLineToolsDir;
	}
	
	public void savePreferences() {
		Writer preferencesWriter = null;
		if (mustSave) {
			try {
				Log.info("Will write the preferences");
				Log.info("mainnetRPCPort : " + Integer.toString(mainnetRPCPort));
				Log.info("testnetRPCPort : " + Integer.toString(testnetRPCPort));
				Log.info("commandLineToolsDir : " + commandLineToolsDir);
				preferences.set("mainnetRPCPort", mainnetRPCPort);
				preferences.set("testnetRPCPort", testnetRPCPort);
				preferences.set("commandLineToolsDir", commandLineToolsDir);
				preferencesWriter = new FileWriter(preferencesFile);
				preferences.writeTo(preferencesWriter, WriterConfig.PRETTY_PRINT);
				preferencesWriter.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(new JFrame(),
						"There was an error saving the preferences file.\nThe application will now terminate",
						"Error",
				        JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
		}
	}
}
