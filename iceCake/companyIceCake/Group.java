package companyIceCake;

import java.net.*;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.io.*;
import java.rmi.*;

/**this group.java 
 * this acts as a way for the testsequencer to access the server/sequencer 
 * services 
 * 
 * this all done 
 * 
 * for example the join(), send(), leave() and all other sequencer operations 
 * are REMOTELY INVOCKED here .......
 * 
 * 
 * the run() in here is intended to do the receiving of messages
 * 
 * join details are also deserialized from here and the expected sequence is 
 * provided...
 */
public class Group implements Runnable
{

  
    public long expectedSequence;
    MsgHandler handler;
    Thread heartbeat;
    public long currentSequence;
    private String message;
    public int count;
    String missing;
    long MissingSequence;

    public Group(String host, MsgHandler handler, String senderName)  throws GroupException
    {
       // contact Sequencer on "host" to join group,
       // create MulticastSocket and thread to listen on it,
       // perform other initialisations
       try {
            MulticastSocket s = new MulticastSocket(6789);
            InetAddress group = InetAddress.getByName("225.0.0.1");
            Sequencer stub = (Sequencer) Naming.lookup("rmi://localhost:6789/remoteMethod");
            
            SequencerJoinInfo sequencerJoinInfo = stub.join(senderName, "225.0.0.1");

            
            long expectedSequence = this.deserialize(sequencerJoinInfo);
            this.setExpectedSequence(expectedSequence);
           // System.out.println(this.getExpectedSequence());
            

            s.joinGroup(group);
            
            this.handler = handler;
            s.close();
       } catch (Exception e) {
           e.printStackTrace();
       }
    }


    public void setExpectedSequence(long sequence){
        this.expectedSequence = sequence;
       // System.out.println("in setExpectedSequence "+ this.expectedSequence);
    }


    public long getExpectedSequence(){
        return this.expectedSequence;
    }

    public void send(String sendername, byte[] msg, long lastReceiveSeq) throws GroupException
    {
        // send the given message to all instances of Group using the same sequencer
        try {
            
            MulticastSocket s = new MulticastSocket(6789);
            Sequencer stub = (Sequencer) Naming.lookup("rmi://localhost:6789/remoteMethod");

            
            long msgID =  stub.generateMsgID();

            
            stub.send(sendername, msg, msgID, lastReceiveSeq, "225.0.0.1");
            
            System.out.println(new String(msg));
            System.out.println("sent");
            s.close();
        } catch (IOException | NotBoundException e) {

        }
    }

    public void leave()
    {
       // leave group
       try {
        String host = "225.0.0.1";
        MulticastSocket s = new MulticastSocket(6789);
        Sequencer stub = (Sequencer) Naming.lookup("rmi://localhost:6789/remoteMethod");
        stub.leave(host);
        s.close();
       }catch(Exception e){
           e.printStackTrace();
       }
    }



    public void run()
    {
        // repeatedly: listen to MulticastSocket created in constructor, and on receipt
        // of a datagram call "handle" on the instance
        // of Group.MsgHandler which was supplied to the constructor

        try {
            System.out.println("heerrrrreee ");
            MulticastSocket s = new MulticastSocket(6789);
            byte [] buffer = new byte[1000];

            //DatagramPacket p = new DatagramPacket(buffer, buffer.length);
            
        while(true){
            System.out.println("waiting here");
            

            System.out.println("waiting here 1");
            DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);

            s.receive(messageIn);
            String message = this.ReadMessage(messageIn);
            System.out.println("new Message: "+new String(messageIn.getData()));

            long currentSequence = this.currentSequence(messageIn);
            s.close();

         while(true){
            this.expectedSequence = this.getExpectedSequence();
            long SeqDifference = currentSequence - expectedSequence;
        
            if (SeqDifference > 1){
                long missingSeq = currentSequence - 1;
                String missingString = this.getMissing(missingSeq);
                System.out.println("while you was away...."+missingString);
                currentSequence = missingSeq;
            }  
         }


            
        }

        } catch (Exception e) {
            //TODO: handle exception
            
        }
        
    }

    public String getMissing(long MissingSequence){

        String missingString = null;
        try {
           Sequencer stub = (Sequencer) Naming.lookup("rmi://localhost:6789/remoteMethod");
           missingString = stub.getMissing(MissingSequence);
        } catch (Exception e) {
           e.printStackTrace();
        }
       return missingString;
    }

    public long generateNextSequence(){
        long nextSEQ = 1L;
        try {
           Sequencer stub = (Sequencer) Naming.lookup("rmi://localhost:6789/remoteMethod");
           nextSEQ = stub.generateNextSequence();
        } catch (Exception e) {
           e.printStackTrace();
        }
       return nextSEQ;
    }

    public interface MsgHandler
    {
        public void handle(int count, byte[] msg);
    }

    public class GroupException extends Exception
    {
        public GroupException(String s)
        {
            super(s);
        }
    }

    public class HeartBeater extends Thread
    {
        // This thread sends heartbeat messages when required
    }


    
    public long deserialize(SequencerJoinInfo sequencerJoinInfo){

        try {   

            FileInputStream inputStream = new FileInputStream("serialized_details.ser");
            ObjectInputStream inputStream2 = new ObjectInputStream(inputStream);
            sequencerJoinInfo = (SequencerJoinInfo) inputStream2.readObject();
            inputStream2.close();
            
            System.out.println("class found....done deserializing.");
            inputStream.close();
          

        } catch (IOException i) {
            i.printStackTrace();
            
        }catch(ClassNotFoundException c){
            System.out.println("class not found");
        }
        System.out.println("address ="+ sequencerJoinInfo.getAddr());
        System.out.println("expect sequence = "+ sequencerJoinInfo.getSequence());

        long seq = sequencerJoinInfo.getSequence();

        return seq;
    }

       
    public String ReadMessage(DatagramPacket datagramPacket){

        String ReceivedMessage;

        byte [] msg = datagramPacket.getData();

        byte messageBytes = msg[0];

        this.setReceivedMessage(messageBytes);
        ReceivedMessage = this.getMessage();

        return ReceivedMessage;
    }

    public long currentSequence(DatagramPacket datagramPacket){
        
        long currentSequence;
        byte [] msg = datagramPacket.getData();

        byte currentSeqBytes = msg[2];

        this.setCurrentSeq(currentSeqBytes);
        currentSequence = this.getCurrentSeq();

        return currentSequence;

    }

    private void setCurrentSeq(byte currentSeq){
        String seq = String.valueOf(currentSeq);
        this.currentSequence = Long.parseLong(seq);
    }

    public long getCurrentSeq(){
        System.out.println("heerrrrreee in getCurrentSeq");
        return this.currentSequence;
    }

    private void setReceivedMessage(byte mes){
        //shady i know :)
        String msg = ""+mes;
        this.message = msg;
    }
    
    private String getMessage(){
        return this.message;
    }
 
    public String sender(){
        String sender = null;
        try {
            Sequencer stub = (Sequencer) Naming.lookup("rmi://localhost:6789/remoteMethod");
            sender = stub.getSenderName();
        } catch (Exception e) {
            e.printStackTrace();
        }

         return sender;
    }

    public Group(){}



}