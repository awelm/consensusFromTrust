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
    private Set<Transaction> consensus;
    private boolean proposalStage;

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        proposalStage = true;
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

    /* The first time this method is called, it returns its proposals that it
     * will send to its followers. The second time its called, it returns the
     * transactions that it belives are the consensus.
    */
    public Set<Transaction> sendToFollowers() {
        if(proposalStage) { // return proposal list
            proposalStage = !proposalStage;
            return proposals;
        }
        else { // return consensus list
            proposalStage = !proposalStage;
            return consensus;
        }
    }

    public void receiveFromFollowees(Set<Candidate> candidates) {
        HashMap<Transaction, Integer> votes = new HashMap<Transaction, Integer>();
        consensus = new HashSet<Transaction>();

        //include your own proposals when determining consensus
        for(Transaction t: proposals)
            votes.put(t,1);

        // save trusted followees votes
        for(Candidate c: candidates)
        {
            if(followees[c.sender]) {
                int count = 0;
                if (votes.get(c.tx) != null)
                    count = votes.get(c.tx);
                votes.put(c.tx, count+1);
            }
        }

        for (Map.Entry<Transaction, Integer> entry : votes.entrySet()) {
            if(entry.getValue() >= 0) //TODO: temp, remove this
                consensus.add(entry.getKey());
        } 
    }
}
