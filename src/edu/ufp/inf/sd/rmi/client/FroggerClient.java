package edu.ufp.inf.sd.rmi.client;

import edu.ufp.inf.sd.rmi.client.frogger.Main;
import edu.ufp.inf.sd.rmi.server.*;
import edu.ufp.inf.sd.rmi.util.rmisetup.SetupContextRMI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FroggerClient extends JFrame {

    private DB db;
    private String username;
    private SetupContextRMI contextRMI;
    private GameFactoryRI gameFactoryRI;
    private GameSessionRI gameSessionRI;
    private FroggerGameRI froggerGameRI;
    private ObserverRI observerRI;

    public FroggerClient(String args[]) throws RemoteException {
        this.db = DB.getInstance();

        initContext(args);


        login();
        //this.gameSessionRI = gameFactoryRI.login("admin", "admin");


        System.out.println("SESSIONS:");
        System.out.println(gameFactoryRI.getSessions().toString());

        froggerGameRI = gameSessionRI.createGame(1);
        initObserver(args);


        dashboard();

    }

    private void initContext(String args[]){
        try {
            SetupContextRMI.printArgs(this.getClass().getName(), args);
            String registryIP = args[0];
            String registryPort = args[1];
            String serviceName = args[2];
            contextRMI = new SetupContextRMI(this.getClass(), registryIP, registryPort, new String[]{serviceName});
            gameFactoryRI = (GameFactoryRI)lookupService();
        } catch (RemoteException e) {
            Logger.getLogger(FroggerClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private void initObserver(String args[]) {
        try {
            observerRI = new ObserverImpl(this.gameSessionRI.getUsername(), this, this.froggerGameRI);
        } catch (Exception e) {
            Logger.getLogger(ObserverImpl.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private Remote lookupService() {
        try {
            Registry registry = contextRMI.getRegistry();
            if (registry != null) {
                String serviceUrl = contextRMI.getServicesUrl(0);
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "going to lookup service @ {0}", serviceUrl);
                gameFactoryRI = (GameFactoryRI) registry.lookup(serviceUrl);
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "registry not bound (check IPs). :(");
            }
        } catch (RemoteException | NotBoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return gameFactoryRI;
    }

    private void login() throws RemoteException {
        String username = JOptionPane.showInputDialog("Username: ");
        String password = JOptionPane.showInputDialog("Password: ");
        this.gameSessionRI = gameFactoryRI.login(username, password);
        this.username = username;
    }

    private void dashboard() throws RemoteException {

        //construct components
        listUsers = new JList (getSessions());
        listCriados = new JList (getGamesCriados());
        listDecorrer = new JList (getGamesDecorrer());
        novoJogo = new JButton ("Novo Jogo");
        atualizarUsers = new JButton ("Atualizar");
        atualizarCriados = new JButton ("Atualizar C");
        atualizarDecorrer = new JButton ("Atualizar D");
        jcomp8 = new JLabel ("Users Online");
        jcomp9 = new JLabel ("Jogos Criados");
        jcomp10 = new JLabel ("Jogos a Decorrer");
        okCriados = new JButton ("OK");
        okDecorrer = new JButton ("OK");

        //adjust size and set layout
        setPreferredSize (new Dimension (600, 480));
        setLayout (null);

        //add components
        add (listUsers);
        add (listCriados);
        add (listDecorrer);
        add (novoJogo);
        add (atualizarUsers);
        //add (atualizarCriados);
        //add (atualizarDecorrer);
        add (jcomp8);
        add (jcomp9);
        add (jcomp10);
        add (okCriados);
        add (okDecorrer);

        //set component bounds (only needed by Absolute Positioning)
        listUsers.setBounds (30, 35, 510, 95);
        listCriados.setBounds (30, 155, 400, 100);
        listDecorrer.setBounds (30, 285, 400, 100);
        novoJogo.setBounds (165, 390, 100, 25);
        atualizarUsers.setBounds (450, 10, 100, 25);
        atualizarCriados.setBounds (335, 130, 100, 25);
        atualizarDecorrer.setBounds (335, 260, 100, 25);
        jcomp8.setBounds (30, 5, 100, 25);
        jcomp9.setBounds (35, 130, 100, 25);
        jcomp10.setBounds (30, 260, 110, 25);
        okCriados.setBounds (435, 230, 100, 25);
        okDecorrer.setBounds (430, 360, 100, 25);

        this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible (true);

        atualizarUsers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    updateAll();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        } );

        atualizarCriados.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    updateGamesCriados();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        } );

        atualizarDecorrer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    updateGamesDecorrer();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        } );

        novoJogo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                try {
                    createGame();
                    froggerGameRI.startGame();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        } );

        okCriados.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (listCriados.getSelectedValue() == null) {
                        JOptionPane.showMessageDialog(null, "Por favor selecione um jogo");
                    } else {
                        FroggerGameRI game = (FroggerGameRI) listCriados.getSelectedValue();
                        game.attach(observerRI);
                        game.startGame();
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        } );

        okDecorrer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (listDecorrer.getSelectedValue() == null) {
                        JOptionPane.showMessageDialog(null, "Por favor selecione um jogo");
                    } else {
                        FroggerGameRI game = (FroggerGameRI) listDecorrer.getSelectedValue();
                        game.attach(observerRI);
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        } );

    }

    //TODO: Alterar
    public void startGame() throws RemoteException {
        froggerGameRI.attach(this.observerRI);
    }

    public FroggerGameRI createGame() throws RemoteException {
        int dificuldade = Integer.parseInt(JOptionPane.showInputDialog("Insira a dificuldade: "));
        froggerGameRI = gameSessionRI.createGame(dificuldade);
        return froggerGameRI;
    }

    public DefaultListModel getSessions() throws RemoteException {
        DefaultListModel model = new DefaultListModel();
        HashMap<String, GameSessionRI> sessions = gameFactoryRI.getSessions();
        model.addAll(sessions.keySet());
        return model;
    }

    public void updateAll() throws RemoteException {
        updateUsers();
        updateGamesDecorrer();
        updateGamesCriados();
    }

    public void updateUsers() throws RemoteException {
        listUsers.setModel(getSessions());
    }

    public DefaultListModel getGamesDecorrer() throws RemoteException {
        DefaultListModel model = new DefaultListModel();
        HashMap<String, GameSessionRI> sessions = gameFactoryRI.getSessions();

        for (GameSessionRI session : sessions.values()) {
            HashMap<String, FroggerGameRI> games = session.getGames();
            for (FroggerGameRI game : games.values()) {
                model.addElement(game);
            }
        }

        return model;
    }

    public void updateGamesDecorrer() throws RemoteException {
        listDecorrer.setModel(getGamesDecorrer());
    }

    public DefaultListModel getGamesCriados() throws RemoteException {
        DefaultListModel model = new DefaultListModel();
        HashMap<String, GameSessionRI> sessions = gameFactoryRI.getSessions();

        for (GameSessionRI session : sessions.values()) {
            if (Objects.equals(session.getUsername(), this.username)) {
                HashMap<String, FroggerGameRI> games = session.getGames();
                for (FroggerGameRI game : games.values()) {
                    model.addElement(game);
                }
            }
        }

        return model;
    }

    public void updateGamesCriados() throws RemoteException {
        listCriados.setModel(getGamesCriados());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (args.length >= 3) {
                    try {
                        new FroggerClient(args).setVisible(true);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println(FroggerClient.class + ": call must have the following args: <rmi_ip> <rmi_port> <rmi_service_prefix>");
                }
            }
        });
    }

    private JList listUsers;
    private JList listCriados;
    private JList listDecorrer;
    private JButton novoJogo;
    private JButton atualizarUsers;
    private JButton atualizarCriados;
    private JButton atualizarDecorrer;
    private JLabel jcomp8;
    private JLabel jcomp9;
    private JLabel jcomp10;
    private JButton okCriados;
    private JButton okDecorrer;



    //FROGGER GAME



}
