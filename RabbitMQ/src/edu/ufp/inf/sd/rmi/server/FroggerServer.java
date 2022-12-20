package edu.ufp.inf.sd.rmi.server;


import edu.ufp.inf.sd.rmi.client.ObserverRI;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FroggerServer {

    private SetupContextRMI contextRMI;
    private GameFactoryRI gameFactoryRI;
    private HashMap<String, User> users = new HashMap<>();
    private HashMap<String, GameSessionRI> sessions = new HashMap<>();
    private HashMap<String, ObserverRI> workers = new HashMap<>();

    public static void main(String[] args)  {
        if (args != null && args.length < 3) {
            System.err.println("usage: java [options] edu.ufp.sd.DigLab.server.DigLibServer <rmi_registry_ip> <rmi_registry_port> <service_name>");
            System.exit(-1);
        } else {
            FroggerServer hws = new FroggerServer(args);
            hws.rebindService();
        }
    }

    public FroggerServer(String args[]) {
        try {
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
        } catch (RemoteException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    private void rebindService() {
        try {
            Registry registry = contextRMI.getRegistry();
            if (registry != null) {
                gameFactoryRI = new GameFactoryImpl(this);
                String serviceUrl = contextRMI.getServicesUrl(0);
                registry.rebind(serviceUrl, gameFactoryRI);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
            }
        } catch (RemoteException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }

    }

}
