/**
 *The MIT License (MIT)
 *
 *Copyright (c) 2014 JeremGamer
 *
 *Permission is hereby granted, free of charge, to any person obtaining a copy
 *of this software and associated documentation files (the "Software"), to deal
 *in the Software without restriction, including without limitation the rights
 *to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *copies of the Software, and to permit persons to whom the Software is
 *furnished to do so, subject to the following conditions:
 *
 *The above copyright notice and this permission notice shall be included in all
 *copies or substantial portions of the Software.
 *
 *THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.**/

package io.github.jeremgamer.maintenance;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class SmokeDetector {
	
	static Plugin plugin;
	
	@SuppressWarnings("static-access")
	public SmokeDetector(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	
	public static void loadLibraries() throws IOException { 
		
		File lib1 = new File(new File("").getAbsolutePath() + "/sigar-x86-winnt.dll");		
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
		

		File lib2 = new File(new File("").getAbsolutePath() + "/sigar-amd64-winnt.dll");
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
		
		
		File lib3 = new File(new File("").getAbsolutePath() + "/libsigar-amd64-linux.so");		
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
		
		
		File lib4 = new File(new File("").getAbsolutePath() + "/libsigar-x86-linux.so");		
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
		
		
		File lib5 = new File(new File("").getAbsolutePath() + "/libsigar-universal-macosx.dylib");		
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
		
		
		File lib6 = new File(new File("").getAbsolutePath() + "/libsigar-universal64-macosx.dylib");		
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
	
	public static int getCpuUsage() throws SigarException {
		
		Sigar sigar = new Sigar();
		CpuPerc cpu = sigar.getCpuPerc();
		double cpuUsage = cpu.getUser() * 100 + cpu.getSys() * 100 ;
		int display = (int)cpuUsage;
        return display;
	}
	
	public static int getMemUsagePercent() throws SigarException {
		
		Sigar sigar = new Sigar();
		Mem mem = null;
        try {
            mem = sigar.getMem();
        } catch (SigarException se) {
            se.printStackTrace();
            }
        double MEMUse = mem.getUsedPercent();
		int displayPercent = (int) MEMUse;
        return displayPercent;
	}
	public static long getMemUsage() throws SigarException {
		
		Sigar sigar = new Sigar();
		Mem mem = null;
        try {
            mem = sigar.getMem();
        } catch (SigarException se) {
            se.printStackTrace();
            }
        long MEMUse = mem.getUsed() / 1024 / 1024;
		return MEMUse;
	}
	
}
