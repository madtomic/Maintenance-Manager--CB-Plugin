package io.github.jeremgamer.maintenance;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class SmokeDetector {
	
	static Plugin plugin;
	
	@SuppressWarnings("static-access")
	public SmokeDetector(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	Process r;
	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public static void loadLibraries() throws IOException { 
		
		File sigarSmoke = new File(plugin.getDataFolder() + "/SigarSmoke.jar");		
		try (InputStream input = Maintenance.class.getResourceAsStream("SigarSmoke.jar");
				OutputStream output = new FileOutputStream(sigarSmoke)) {
			byte[] buf = new byte[8192];
			int len;
			while ( (len=input.read(buf)) > 0 ) {
				output.write(buf, 0, len);
			}
			output.flush();
			input.close();
			output.close();
		}

		File lib1 = new File(plugin.getDataFolder() + "/sigar-x86-winnt.dll");		
		try (InputStream input = Maintenance.class.getResourceAsStream("sigar-x86-winnt.dll");
				OutputStream output = new FileOutputStream(lib1)) {
			byte[] buf = new byte[8192];
			int len;
			while ( (len=input.read(buf)) > 0 ) {
				output.write(buf, 0, len);
			}
			output.flush();
			input.close();
			output.close();
		}
		

		File lib2 = new File(plugin.getDataFolder() + "/sigar-amd64-winnt.dll");
		try (InputStream input = Maintenance.class.getResourceAsStream("sigar-amd64-winnt.dll");
			OutputStream output = new FileOutputStream(lib2)) {
			byte[] buf = new byte[8192];
			int len;
			while ( (len=input.read(buf)) > 0 ) {
				output.write(buf, 0, len);
			}
			output.flush();
			input.close();
			output.close();
		}
		
		
		File lib3 = new File(plugin.getDataFolder() + "/libsigar-amd64-linux.so");		
		try (InputStream input = Maintenance.class.getResourceAsStream("libsigar-amd64-linux.so");
				OutputStream output = new FileOutputStream(lib3)) {
					byte[] buf = new byte[8192];
					int len;
					while ( (len=input.read(buf)) > 0 ) {
						output.write(buf, 0, len);
					}
					output.flush();
					input.close();
					output.close();
		}
		
		
		File lib4 = new File(plugin.getDataFolder() + "/libsigar-x86-linux.so");		
		try (InputStream input = Maintenance.class.getResourceAsStream("libsigar-x86-linux.so");
				OutputStream output = new FileOutputStream(lib4)) {
					byte[] buf = new byte[8192];
					int len;
					while ( (len=input.read(buf)) > 0 ) {
						output.write(buf, 0, len);
					}
					output.flush();
					input.close();
					output.close();
		}
		
		
		File lib5 = new File(plugin.getDataFolder() + "/libsigar-universal-macosx.dylib");		
		try (InputStream input = Maintenance.class.getResourceAsStream("libsigar-universal-macosx.dylib");
				OutputStream output = new FileOutputStream(lib5)) {
					byte[] buf = new byte[8192];
					int len;
					while ( (len=input.read(buf)) > 0 ) {
						output.write(buf, 0, len);
					}
					output.flush();
					input.close();
					output.close();
		}
		
		
		File lib6 = new File(plugin.getDataFolder() + "/libsigar-universal64-macosx.dylib");		
		try (InputStream input = Maintenance.class.getResourceAsStream("libsigar-universal64-macosx.dylib");
				OutputStream output = new FileOutputStream(lib6)) {
					byte[] buf = new byte[8192];
					int len;
					while ( (len=input.read(buf)) > 0 ) {
						output.write(buf, 0, len);
					}
					output.flush();
					input.close();
					output.close();
		}

	
	}
	
	public void sendRequest(String request) throws IOException {
		File smoke = new File(plugin.getDataFolder() + "/smoke.yml");
		if (!smoke.exists()) {
			smoke.createNewFile();
		}
		FileWriter fw;
		fw = new FileWriter(smoke.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(request);
		bw.close();
		
		try {
			if (OS.contains("win")) {
			Runtime.getRuntime().exec("cmd /c start " + plugin.getDataFolder() + "/SigarSmoke.jar");
			}
			if (OS.contains("mac")) {
			Runtime.getRuntime().exec("start " + plugin.getDataFolder() + "/SigarSmoke.jar");
			}
			if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
			Runtime.getRuntime().exec("xdg-open " + plugin.getDataFolder() + "/SigarSmoke.jar");
			}
			} catch (IOException e) {

			} 
	}
	
	public String getInfo() throws IOException {
		File smokeAnswer = new File(plugin.getDataFolder() + "/smokeAnswer.yml");
    	FileReader fr;
		fr = new FileReader(smokeAnswer.getAbsoluteFile());
		BufferedReader br = new BufferedReader(fr);
		String info = br.readLine();
		br.close();
		smokeAnswer.delete();
		return info;
	}
	
}
