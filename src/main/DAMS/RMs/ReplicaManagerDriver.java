package DAMS.RMs;

public class ReplicaManagerDriver {
    public static void main(String[] args) {
        new DAMS.RMs.RM1.ReplicaManager().run();
        new DAMS.RMs.RM2.ReplicaManager().run();
        new DAMS.RMs.RM3.ReplicaManager().run();
        new DAMS.RMs.RM4.ReplicaManager().run();
    }
}
