package org.ctlv.proxmox.generator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.security.auth.login.LoginException;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;
import org.json.JSONException;

public class GeneratorMain {
	
	static Random rndTime = new Random(new Date().getTime());
	public static int getNextEventPeriodic(int period) {
		return period;
	}
	public static int getNextEventUniform(int max) {
		return rndTime.nextInt(max);
	}
	public static int getNextEventExponential(int inv_lambda) {
		float next = (float) (- Math.log(rndTime.nextFloat()) * inv_lambda);
		return (int)next;
	}
	
	public static void main(String[] args) throws InterruptedException, LoginException, JSONException, IOException {
		
	
		long baseID = Constants.CT_BASE_ID;
		int lambda = 30;
		
		
		Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();

		ProxmoxAPI api = new ProxmoxAPI();
		Random rndServer = new Random(new Date().getTime());
		Random rndRAM = new Random(new Date().getTime()); 
		
		long memAllowedOnServer3 = (long) (api.getNode(Constants.SERVER1).getMemory_total() * Constants.MAX_THRESHOLD);
		long memAllowedOnServer4 = (long) (api.getNode(Constants.SERVER2).getMemory_total() * Constants.MAX_THRESHOLD);
	
		long ctID=baseID;
		
		while (true) {
			for (int id=0; id<100; id++) {
				if (ctID==3299) {
					ctID=baseID;
				}
				else {
				 ctID= baseID+id;
				System.out.println("Starting "+ ctID);
			
			// Récupérer les données sur les serveurs
			
			// Listes les CTs par serveur
			for (int i=1; i<=10; i++) {
				String srv ="srv-px"+i;
				try {
					List<LXC> cts = api.getCTs(srv);
					for (LXC lxc : cts) {
						if(lxc.getName().contains("C2")) {
							myCTsPerServer.putIfAbsent(srv, new ArrayList<LXC>());
							myCTsPerServer.get(srv).add(lxc);
						}
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}

			
			// 1. Calculer la quantité de RAM utilisée par mes CTs sur chaque serveur
			long memOnServer3 = 0;
			long memOnServer4 = 0;
			
			if (myCTsPerServer.get(Constants.SERVER1)!= null) {
			
				for (LXC lxc : myCTsPerServer.get(Constants.SERVER1)) {
					memOnServer3 = memOnServer3+(lxc.getMem()) ;
					System.out.println("memOnServer3: "+memOnServer3);

				}
			}
			if (myCTsPerServer.get(Constants.SERVER2)!= null) {
				for (LXC lxc : myCTsPerServer.get(Constants.SERVER2)) {
					memOnServer4 = memOnServer4+ (lxc.getMem());
					System.out.println("memOnServer4: " +memOnServer4);

				}
			}
		
			
			
			// Mémoire autorisée sur chaque serveur
			float memRatioOnServer3 = 0;
			memRatioOnServer3 = memOnServer3/memAllowedOnServer3;
			System.out.println("mem Allowed s3 :"+memAllowedOnServer3);
		
			System.out.println("memRatioS3 :"+memRatioOnServer3);
			
			float memRatioOnServer4 = 0;
			memRatioOnServer4 = memOnServer4/memAllowedOnServer4;
			System.out.println("mem Allowed s4 : "+memAllowedOnServer4);
			
			System.out.println("memRatioS4 :"+memRatioOnServer4);
		
			if (memOnServer3 <= memAllowedOnServer3 && memOnServer4 <= memAllowedOnServer4) {  // Exemple de condition de l'arrêt de la génération de CTs
				
				// choisir un serveur aléatoirement avec les ratios spécifiés 66% vs 33%
				String serverName;
				if (rndServer.nextFloat() < Constants.CT_CREATION_RATIO_ON_SERVER1) {
					serverName = Constants.SERVER1;
					System.out.println("ici");}
				else
					serverName = Constants.SERVER2;
				
				// créer un contenaire sur ce serveur
				
					
				String ctName=Constants.CT_BASE_NAME+ctID;
				System.out.println(ctName);
				String ctIDs = ""+ctID;
				api.createCT(serverName, ctIDs, ctName, Constants.RAM_SIZE[1]);
				while(api.getCT(serverName, ctIDs).getStatus().equals("stopped")) {
					try {
						api.startCT(serverName, ctIDs);
					}catch(IOException e) {
						Thread.sleep(5000);

					}
				}
				System.out.println("Création + lancement fini");
				
				
								
				// planifier la prochaine création
				int timeToWait = getNextEventExponential(lambda); // par exemple une loi expo d'une moyenne de 30sec
				
				// attendre jusqu'au prochain évènement
				Thread.sleep(1000 * timeToWait);
			}
			else {
				System.out.println("Servers are loaded, waiting ...");
				Thread.sleep(Constants.GENERATION_WAIT_TIME* 1000);
			}
		}
		
	}
	}
	}
	}
