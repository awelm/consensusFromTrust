import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private boolean[] followees;
    private int trustCount;
    private Set<Transaction> proposals;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        trustCount = 0;
    }

    public void setFollowees(boolean[] followees) {
        this.followees = Arrays.copyOf(followees, followees.length);
        for(boolean b: followees)
            if(b)
                trustCount++;
    }

    public void setPendingTransaction(Set<Transaction> pendingTransactions) {
        this.proposals = new HashSet<Transaction>(pendingTransactions);
    }

    /* Each call to this method (except the last call) returns the node's proposals that it
     * will send to its followers. During the last call, it returns the
     * transactions that it belives are the consensus.
    */
    public Set<Transaction> sendToFollowers() {
        return proposals;
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        for(Candidate c: candidates) {
            Transaction tx = c.tx;
            if(followees[c.sender])
                proposals.add(c.tx);
        }
    }
}
