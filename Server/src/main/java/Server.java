import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server{

    int count = 0;
    int port;
    ArrayList<ClientThread> clients = new ArrayList<>();
    TheServer server;
    private Consumer<Serializable> callback;
    CFourInfo info = new CFourInfo();

    Server(Consumer<Serializable> call, int i){
        callback = call;
        server = new TheServer();
        server.start();
        port = i;
    }

    public class TheServer extends Thread{
        public void run() {
            try(ServerSocket mysocket = new ServerSocket(port)){
                while(true) {
                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    count++;
                    info.playerNum = count;					// Set the playerNum for new client
                    callback.accept("client #" + count + " has connected to server");
                    clients.add(c);
                    c.start();
                }
            }
            catch(Exception e) {
                callback.accept("Server socket did not launch");
            }
        }
    }

    class ClientThread extends Thread{
        Socket connection;
        ObjectInputStream in;
        ObjectOutputStream out;

        int num;

        ClientThread(Socket s, int i){
            this.connection = s;
            this.num = i+1;
        }

        /* updateClients
         *
         * sends CFourInfo object to clients
         */
        public void updateClients(CFourInfo cFourInfo) {
            for (ClientThread t : clients) {
                try {
                    t.out.reset();
                    t.out.writeObject(cFourInfo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void run(){
            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            }
            catch(Exception e) {
                System.out.println("Streams not open");
            }

            // check number of clients and notifies if 2 players are present
            if (count >= 2) info.have2Players = true;
            else info.have2Players = false;

            updateClients(info);

            while(true) {
                try {
                    // updates clients with each other's movements
                    CFourInfo data = (CFourInfo) in.readObject();
                    info.move = data.move;
                    info.gameOver = data.gameOver;
                    info.x = data.x;
                    info.y = data.y;
                    info.turn = data.turn;
                    info.result = data.result;
                    //info = data;

                    // checks if game is over after each move
                    if (info.gameOver) callback.accept("Game over: " + data.move);
                    else callback.accept("Client " + num + ": " + data.move);

                    // update client 2 with client 1 move
                    if (num == 1) {
                        try {
                            clients.get(1).out.reset();
                            clients.get(1).out.writeObject(info);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    // update client 1 with client 2 move
                    else {
                        try {
                            clients.get(0).out.reset();
                            clients.get(0).out.writeObject(info);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                catch(Exception e) {
                    callback.accept("Error from client: " + num + "....closing down!");
                    clients.remove(this);
                    count--; // updates number of clients connected to server

                    if (count < 2) info.have2Players = false;

                    break;
                }
            }
        }
    }
}