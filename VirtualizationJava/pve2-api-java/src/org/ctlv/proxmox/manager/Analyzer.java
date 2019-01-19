package org.ctlv.proxmox.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class Analyzer {
	ProxmoxAPI api;
	Controller controller;
	
	public Analyzer(ProxmoxAPI api, Controller controller) {
		this.api = api;
		this.controller = controller;
	}
	
	public void analyze(Map<String, ArrayList<LXC>> hashMapDesCT) throws LoginException, JSONException, IOException  {

		double[]  tableOfMemTotalServer = {0, 0};
		int i = 0;
		
		System.out.println("Analyze of my CT :");
		for(Map.Entry<String, ArrayList<LXC>> entree : hashMapDesCT.entrySet()) {
			System.out.println(entree.getKey() + " : ");
		
			// Calculer la quantité de RAM utilisée par mes CTs sur chaque serveur

			double cpuServer3  = (double) (api.getNode(entree.getKey()).getCpu());
			double diskServer3 = (double) (api.getNode(entree.getKey()).getRootfs_used());
			double memServer3 = (double) (api.getNode(entree.getKey()).getMemory_used());
			tableOfMemTotalServer[i] += memServer3;
					
			System.out.println("\tCPU usage "+ cpuServer3+"\n\tDisk Usage :"+ diskServer3+"\n\tMemory Usage :"+memServer3);
			for(LXC lxc : entree.getValue()) {
				System.out.println("\t \t CT : "+ lxc.getName() + " : ");
			
				double cpu = lxc.getCpu();
				double disk = lxc.getDisk();
				double mem = lxc.getMem();
				System.out.println("\t \t \t CPU used "+ cpu +"(" + ((cpuServer3 == 0 ) ? 0 :  Math.round(cpu / cpuServer3 *100))+"%)" );
				System.out.println("\t \t \t Disk used "+ disk +"(" + ((diskServer3 == 0) ? 0 : Math.round(disk / diskServer3 *100))+"%)" );
				System.out.println("\t \t \t Memory used "+ mem +"(" +((memServer3 == 0 ) ? 0 : Math.round(mem / memServer3 *100))+"%)" );
				
			}
			i++;
			
		}
		// Analyse et Actions
				// ...
				//To activate if a server have 12% off capacity
				controller.migrateFromTo("srv-px3", "srv-px4");

		System.out.println("Début de l'analyse ...");
	
			
		System.out.println(tableOfMemTotalServer[0] + " "+ tableOfMemTotalServer[1]);
		
		if(tableOfMemTotalServer[0] > Constants.MIGRATION_THRESHOLD) {
			controller.migrateFromTo("srv-px3", "srv-px4");
		}
		if(tableOfMemTotalServer[1] > Constants.MIGRATION_THRESHOLD) {
			controller.migrateFromTo("srv-px4", "srv-px3");
		}
		if(tableOfMemTotalServer[0] > Constants.DROPPING_THRESHOLD) {
			controller.offLoad("srv-px3");
		}
		if(tableOfMemTotalServer[1] > Constants.DROPPING_THRESHOLD) {
			controller.offLoad("srv-px4");
		}
		
		
		
		

		
				
	}

}
