/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 *
 * @author varietytubechan
 */
public class Server extends javax.swing.JFrame {
    ArrayList clientOutputStreams;
    ArrayList<String> onlineUsers;

    public class ClientHandler implements Runnable	{
        BufferedReader reader;
        Socket sock;
        PrintWriter client;


        public ClientHandler(Socket clientSocket, PrintWriter user) {
            // new inputStreamReader and then add it to a BufferedReader
            client = user;
            try {
                sock = clientSocket;
                InputStreamReader isReader = new InputStreamReader(sock.getInputStream());
                reader = new BufferedReader(isReader);
            } // end try
            catch (Exception ex) {
                outputPane.append("Error beginning StreamReader. \n");
            } // end catch

        } // end ClientHandler()

        public void run() {
            String message, connect = "Connect", disconnect = "Disconnect", chat = "Chat" ;
            String[] data;

            try {
                while ((message = reader.readLine()) != null) {
                    outputPane.append("Received: " + message + "\n");
                    data = message.split(":");
                    for (String token:data) {
                        outputPane.append(token + "\n");
                    }

                    if (data[2].equals(connect)) {
                        tellEveryone((data[0] + ":" + data[1] + ":" + chat));
                        userAdd(data[0]);

                    } else if (data[2].equals(disconnect)) {

                        tellEveryone((data[0] + ":has disconnected." + ":" + chat));
                        userRemove(data[0]);

                    } else if (data[2].equals(chat)) {

                        tellEveryone(message);

                    } else {
                        outputPane.append("No Conditions were met. \n");
                    }


                } // end while
            } // end try
            catch (Exception ex) {
                outputPane.append("Lost a connection. \n");
                ex.printStackTrace();
                clientOutputStreams.remove(client);
            } // end catch
        } // end run()
    } // end class ClientHandler
    /** Creates new form ServerWindow */
    public Server() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        outputPane = new javax.swing.JTextArea();
        startButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("House Server");

        outputPane.setColumns(20);
        outputPane.setEditable(false);
        outputPane.setLineWrap(true);
        outputPane.setRows(5);
        outputPane.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setViewportView(outputPane);

        startButton.setText("Start");
        startButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startButtonActionPerformed(evt);
            }
        });

        stopButton.setText("Stop");
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(startButton, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(stopButton, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(startButton)
                                        .addComponent(stopButton))
                                .addContainerGap(19, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>

    private void startButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        Thread starter = new Thread(new ServerStart());
        starter.start();

        outputPane.append("Server started. \n");
    }

    private void stopButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:

        tellEveryone("Server:is stopping and all users will be disconnected.\n:Chat");
        outputPane.append("Server stopping... \n");

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }


    public class ServerStart implements Runnable {
        public void run() {
            clientOutputStreams = new ArrayList();
            onlineUsers = new ArrayList();

            try {
                ServerSocket serverSock = new ServerSocket(4004);

                while (true) {
                    Socket clientSock = serverSock.accept();
                    PrintWriter writer = new PrintWriter(clientSock.getOutputStream());
                    clientOutputStreams.add(writer);
                    Thread listener = new Thread(new ClientHandler(clientSock, writer));
                    listener.start();
                    outputPane.append("Got a connection. \n");
                }
            }
            catch (Exception ex)
            {
                outputPane.append("Error making a connection. \n");
            }
        }
    }

    public void userAdd (String data) {
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        outputPane.append("Before " + name + " added. \n");
        onlineUsers.add(name);
        outputPane.append("After " + name + " added. \n");
        String[] tempList = new String[(onlineUsers.size())];
        onlineUsers.toArray(tempList);

        for (String token:tempList) {

            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void userRemove (String data) {
        String message, add = ": :Connect", done = "Server: :Done", name = data;
        onlineUsers.remove(name);
        String[] tempList = new String[(onlineUsers.size())];
        onlineUsers.toArray(tempList);

        for (String token:tempList) {

            message = (token + add);
            tellEveryone(message);
        }
        tellEveryone(done);
    }

    public void tellEveryone(String message) {
        Iterator it = clientOutputStreams.iterator();

        while (it.hasNext()) {
            try {

                    PrintWriter writer = (PrintWriter) it.next();
                    writer.println(message);
                    outputPane.append("Sending: " + message + "\n");
                    writer.flush();
                    outputPane.setCaretPosition(outputPane.getDocument().getLength());
            }
            catch (Exception ex) {
                outputPane.append("Error telling everyone. \n");
            }
        }
    }



    // Variables declaration - do not modify
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea outputPane;
    private javax.swing.JButton startButton;
    private javax.swing.JButton stopButton;
    // End of variables declaration

}