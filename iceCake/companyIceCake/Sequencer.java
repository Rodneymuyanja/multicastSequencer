package companyIceCake;

import java.io.IOException;
import java.net.InetAddress;
import java.rmi.*;


public interface Sequencer extends Remote 
{
    // join -- request for "sender" to join sequencer's multicasting service;
    // returns an object specifying the multicast address and the first sequence number to expect
    // this object should be marshalled 
    public SequencerJoinInfo join(String sender,String host)
        throws RemoteException, IOException;// SequencerException;

    // send -- "sender" supplies the msg to be sent, its identifier,
    // and the sequence number of the last received message
   public void send(String sender, byte[] msg, long msgID, long lastSequenceReceived, String host)
        throws RemoteException, IOException;

    // leave -- tell sequencer that "sender" will no longer need its services
   public void leave(String host)
        throws RemoteException;

    // getMissing -- ask sequencer for the message whose sequence number is "sequence"
   public String getMissing(long sequence)
        throws RemoteException;//, SequencerException;

    // heartbeat -- we have received messages up to number "lastSequenceReceived"
   public void heartbeat(String sender, long lastSequenceReceived)
        throws RemoteException;

   public long generateMsgID() throws RemoteException;

   public long generateNextSequence() throws RemoteException;

   public String getSenderName() throws RemoteException;

        
}