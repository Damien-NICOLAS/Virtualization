from proxmoxer import ProxmoxAPI
proxmox = ProxmoxAPI('srv-px1.insa-toulouse.fr', user='dnicolas@Ldap-INSA', password='zr4W*Y', verify_ssl=False)

#for node in proxmox.nodes.get():




# Fonction pour retourner les informations du cluster

# for cluster in proxmox.cluster.resources.get():
#     print( '{0} : cpu  {1}% || disk  {2}  bytes || mem {3} bytes || status {4}'
#     .format(cluster['node'],cluster['cpu'], cluster['disk'], cluster['mem'], cluster['status']))
#




node = proxmox.nodes('srv-px1')
print("Creation Start")
node.lxc.create(vmid=9987,
    ostemplate='template:debian-8-turnkey-nodejs_14.2-1_amd64.tar.gz',
    hostname='ct-tpiss-virt-C2-ct2',
    rootfs='vm:3',
    memory=512,
    swap=512,
    cores=1,
    password='secret',
    net0='name=eth0,bridge=vmbr1,ip=dhcp,ip6=dhcp,tag=2028')
print("Creation OK !")



    # print (cluster['node'])
    # print (cluster['cpu'])
    # print( cluster['disk'])
    # print(cluster['mem'])
    # print( cluster['status'])
