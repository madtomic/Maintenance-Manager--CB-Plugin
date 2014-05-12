package io.github.jeremgamer.sigarsmoke;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFrame;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


@SuppressWarnings("serial")
public class SigarSmoke extends JFrame {
	
	static String request;
	
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
	
	static String path = new File("").getAbsolutePath() + "\\plugins\\MaintenanceManager\\smoke.yml";
	static String path2 = new File("").getAbsolutePath() + "\\plugins\\MaintenanceManager\\smokeAnswer.yml";
	
	public static void answer() throws IOException, SigarException {


	}
	
	public SigarSmoke() throws IOException, SigarException {
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);		
		this.setLocationRelativeTo(null);
		this.setVisible(false);
	}
	
	public static void main ( String args[] ) throws IOException, SigarException {
		new SigarSmoke();
		File smoke = new File(path);
		File smokeAnswer = new File(path2);
		smokeAnswer.setReadable(false);
		smokeAnswer.createNewFile();
    	FileReader fr;
		FileWriter fw;
		fr = new FileReader(smoke.getAbsoluteFile());
		BufferedReader br = new BufferedReader(fr);	
		request = br.readLine();		
		br.close();
		
		fw = new FileWriter(smokeAnswer.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		
		if ( request.contains("cpu") ) {
			String answer = String.valueOf(getCpuUsage());
			bw.write(answer);
			bw.flush();
			bw.close();	
		}
		if ( request.contains("ram") ) {
			String answer = String.valueOf(getMemUsagePercent()) + "% || " + String.valueOf(getMemUsage()) + "MB";
			bw.write(answer);
			bw.flush();
			bw.close();	
		}
		smokeAnswer.setReadable(true);
		smokeAnswer.setReadOnly();
		System.exit(0);
	}
}
