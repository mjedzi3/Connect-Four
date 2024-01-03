import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;


public class Client extends Thread{
    Socket socketClient;

    ObjectOutputStream out;
    ObjectInputStream in;

    private Consumer<Serializable> callback;
    String ip;
    int port;

    Client(Consumer<Serializable> call, String str, int i){
        callback = call;
        ip = str;
        port = i;
    }

    public void run() {
        try {
            socketClient= new Socket(ip,port);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        while(true) {

            try {
                CFourInfo message = (CFourInfo) in.readObject();
                callback.accept(message);
                in.reset();
                out.reset();
            }
            catch(Exception e) {}
        }

    }

    public void send(CFourInfo data) {
        try {
            out.reset();
            out.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}