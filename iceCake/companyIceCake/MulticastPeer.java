package companyIceCake;

import java.net.*;

import java.rmi.NotBoundException;

import java.io.*;

public class MulticastPeer {
    /**this receives and displays messages along with their 
     * ids and sequences...
     * 
     * we improvised this class after noticing that the sequencer was able to 
     * send but the receive() method in the run() run blocks indefinitely
     * without receiving anything 
     * 
     * so the receiving is done here....
     */
    public static void main(String[] args) throws IOException, NotBoundException {
        MulticastSocket s = new MulticastSocket(6789);
        try{

            Group grp = new Group();
            InetAddress group = InetAddress.getByName("225.0.0.1");

            s.joinGroup(group);

            byte [] buffer = new byte[1000];

            while(true){
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                
                s.receive(messageIn);

               // System.out.println("received from "+ messageIn.getAddress());
                System.out.println("received from "+ grp.sender());
                System.out.println(new String(messageIn.getData()));
                
            }
            
        }catch(SocketException e){System.out.println("socket exception "+e.getMessage());
        }catch(IOException e){System.out.println("io exception "+e.getMessage());
        }finally{if(s!=null){
            s.close();
        }}   
    }

}