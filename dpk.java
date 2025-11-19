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

set ns [new Simulator]

set namfile [open cie.nam w]
$ns namtrace-all $namfile

set tracefile [open cie.tr w]
$ns trace-all $tracefile

proc finish {} {
    global ns namfile tracefile
    $ns flush-trace
    close $namfile
    close $tracefile
    exec nam cie.nam &
    exec awk -f cie.awk cie.tr &
    exit 0
}

# Create nodes
set n0 [$ns node]
set n1 [$ns node]
set n2 [$ns node]
set n3 [$ns node]
set n4 [$ns node]
set n5 [$ns node]
set n6 [$ns node]
set n7 [$ns node]
set n8 [$ns node]

# Create links
$ns duplex-link $n1 $n0 1Mb 10ms DropTail
$ns duplex-link $n2 $n0 1Mb 10ms DropTail
$ns duplex-link $n3 $n0 1.75Mb 20ms DropTail
$ns duplex-link $n4 $n0 1Mb 10ms DropTail
$ns duplex-link $n5 $n0 1Mb 10ms DropTail
$ns duplex-link $n6 $n0 1Mb 10ms DropTail
$ns duplex-link $n7 $n0 1Mb 20ms DropTail
$ns duplex-link $n8 $n0 1Mb 10ms DropTail

$ns duplex-link-op $n0 $n1 orient right
$ns duplex-link-op $n0 $n2 orient left
$ns duplex-link-op $n0 $n3 orient right-up
$ns duplex-link-op $n0 $n4 orient right-down
$ns duplex-link-op $n0 $n5 orient left-up
$ns duplex-link-op $n0 $n6 orient left-down
$ns duplex-link-op $n0 $n7 orient up
$ns duplex-link-op $n0 $n8 orient down

# Override recv method for Ping agents
Agent/Ping instproc recv {from rtt} {
    $self instvar node_
    puts "node [$node_ id] received ping answer from $from with round-trip-time $rtt ms"
}

# Create Ping agents
set p1 [new Agent/Ping]
set p2 [new Agent/Ping]
set p3 [new Agent/Ping]
set p4 [new Agent/Ping]
set p5 [new Agent/Ping]
set p6 [new Agent/Ping]
set p7 [new Agent/Ping]
set p8 [new Agent/Ping]

# Attach agents to nodes
$ns attach-agent $n1 $p1
$ns attach-agent $n2 $p2
$ns attach-agent $n3 $p3
$ns attach-agent $n4 $p4
$ns attach-agent $n5 $p5
$ns attach-agent $n6 $p6
$ns attach-agent $n7 $p7
$ns attach-agent $n8 $p8

# Attach node references to agents
$p1 set node_ $n1
$p2 set node_ $n2
$p3 set node_ $n3
$p4 set node_ $n4
$p5 set node_ $n5
$p6 set node_ $n6
$p7 set node_ $n7
$p8 set node_ $n8

# Set queue limits
$ns queue-limit $n0 $n4 1
$ns queue-limit $n0 $n5 2
$ns queue-limit $n0 $n6 2

# Connect agents
$ns connect $p1 $p4
$ns connect $p2 $p5
$ns connect $p3 $p6
$ns connect $p7 $p8

# Schedule sends
$ns at 0.2 "$p1 send"
$ns at 0.4 "$p2 send"
$ns at 0.6 "$p3 send"
$ns at 1.0 "$p4 send"
$ns at 1.2 "$p5 send"
$ns at 1.4 "$p6 send"
$ns at 1.6 "$p7 send"

$ns at 2.0 "finish"

$ns run

BEGIN {
    count = 0;
}

{
    event = $1;

    if (event == "d") {
        count++;
    }
}

END {
    printf("No of packets dropped : %d\n", count);
}
