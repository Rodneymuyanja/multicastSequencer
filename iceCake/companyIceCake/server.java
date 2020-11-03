package companyIceCake;
import java.rmi.Naming;

/**this is main class that creates access to the Sequencer on the server */
public class server {
    public static void main(String[] args) {
        try {
            Sequencer stub = new SequencerImpl();
            //the sequencer is named remoteMethod in this project
            Naming.rebind("rmi://localhost:6789/remoteMethod", stub);
             
        }catch(Exception e){

    }
}
}
