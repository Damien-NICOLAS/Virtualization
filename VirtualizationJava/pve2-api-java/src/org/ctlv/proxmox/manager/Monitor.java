package org.ctlv.proxmox.manager;

import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

import com.sun.org.apache.xpath.internal.axes.HasPositionalPredChecker;

public class Monitor implements Runnable {

	Analyzer analyzer;
	ProxmoxAPI api;
	
	public Monitor(ProxmoxAPI api, Analyzer analyzer) {
		this.api = api;
		this.analyzer = analyzer;
	}
	

	@Override
	public void run() {
		
		HashMap<String, ArrayList<LXC>> hashMapDesCT;
		
		while(true) {
			
			System.out.println("Starting ...");
			
			// Récupérer les données sur les serveurs
			
			hashMapDesCT = new HashMap<String, ArrayList<LXC>>();
			// Listes les CTs par serveur
			for (int i=1; i<=10; i++) {
				String srv ="srv-px"+i;
				try {
					List<LXC> cts = api.getCTs(srv);
					for (LXC lxc : cts) {
						if(lxc.getName().contains("C2")) {
							hashMapDesCT.putIfAbsent(srv, new ArrayList<LXC>());
							hashMapDesCT.get(srv).add(lxc);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			System.out.println("CT trouvés : ");
			for(Map.Entry<String, ArrayList<LXC>> entree : hashMapDesCT.entrySet()) {
				System.out.println(entree.getKey());
				for(LXC lxc : entree.getValue()) {
					System.out.println("\t"+ lxc.getName());
				}
			}
			// Lancer l'analyse
			try {
				analyzer.analyze(hashMapDesCT);
			} catch (LoginException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			System.out.println("End");
			// attendre une certaine période
			try {
				Thread.sleep(Constants.MONITOR_PERIOD * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}
