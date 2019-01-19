import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.ctlv.proxmox.api.Constants;
import org.ctlv.proxmox.api.ProxmoxAPI;
import org.ctlv.proxmox.api.data.LXC;

public class CleanProxMox {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		
		Map<String, List<LXC>> myCTsPerServer = new HashMap<String, List<LXC>>();

		ProxmoxAPI api = new ProxmoxAPI();
				
	
			
		for (int i=1; i<=10; i++) {
			String srv ="srv-px"+i;
			try {
				List<LXC> cts = api.getCTs(srv);
				for (LXC lxc : cts) {
					if(lxc.getName().contains("C2")) {
						System.out.println("DELETE "+lxc.getName());
						if(lxc.getStatus().equals("stopped")) {
							api.deleteCT(srv, lxc.getVmid());

						}else {
							api.stopCT(srv, lxc.getVmid());
							Thread.sleep(5000);
							api.deleteCT(srv, lxc.getVmid());
						}						
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done !");

	}

}
