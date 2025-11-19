package cie;

import java.util.Random;

public class CieServer {

    public static void main(String[] args) throws Exception {
        Random rand = new Random();

        int clientSeq = rand.nextInt(900) + 100;

        int serverSeq = rand.nextInt(900) + 100;
        int ack = clientSeq + 1;

        System.out.println("\nServer side packet sent");
        System.out.println("sequence number - " + serverSeq);
        System.out.println("Ack number – " + ack);

        for (int i = 0; i < 3; i++) {
            int prevSeq = serverSeq;
            int receivedSeq = clientSeq + i + 1;

            serverSeq = prevSeq + 1;
            ack = receivedSeq + 1;

            System.out.println("\nServer side packet sent");
            System.out.println("sequence number - " + serverSeq);
            System.out.println("Ack number – " + ack);
        }
    }
}


package cie;

import java.util.Random;

public class CieClient {

    public static void main(String[] args) throws Exception {
        Random rand = new Random();

        int clientSeq = rand.nextInt(900) + 100;
        System.out.println("Client side packet sent");
        System.out.println("sequence number - " + clientSeq);
        System.out.println("Ack number – null");

        int serverSeq = rand.nextInt(900) + 100;
        int serverAck = clientSeq + 1;

        System.out.println("\nCLIENT RECEIVED SERVER FIRST PACKET:");
        System.out.println("Server sequence - " + serverSeq);
        System.out.println("Ack number – " + serverAck);

        for (int i = 0; i < 3; i++) {
            int prevSeq = clientSeq;
            int receivedSeq = serverSeq;

            clientSeq = prevSeq + 1;
            int clientAck = receivedSeq + 1;

            System.out.println("\nClient side packet sent");
            System.out.println("sequence number - " + clientSeq);
            System.out.println("Ack number – " + clientAck);

            serverSeq = serverSeq + 1;
            serverAck = clientSeq + 1;

            System.out.println("\nCLIENT RECEIVED SERVER PACKET:");
            System.out.println("Server sequence - " + serverSeq);
            System.out.println("Ack number – " + serverAck);
        }
    }
}
