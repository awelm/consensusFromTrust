import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Arrays;

/* CompliantNode refers to a node that follows the rules (not malicious)*/
public class CompliantNode implements Node {

    private boolean[] followees;
    private Set<Transaction> proposals;
    private HashMap<Integer, HashSet<Transaction>> nodeTransactions;
    private HashMap<Integer, HashSet<Transaction> > previousProposals; 

    public CompliantNode(double p_graph, double p_malicious, double p_txDistribution, int numRounds) {
        previousProposals = new HashMap<Integer, HashSet<Transaction>>();
        nodeTransactions = new HashMap<Integer, HashSet<Transaction>>();
    }

    public void setFollowees(boolean[] followees) {
        this.followees = Arrays.copyOf(followees, followees.length);
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
        // first iteration of receiveFromFollowees
        if(previousProposals.isEmpty())
        {
            for(Candidate c: candidates) {
                if (!previousProposals.containsKey(c.sender))
                    previousProposals.put(c.sender, new HashSet<Transaction>());
                previousProposals.get(c.sender).add(c.tx);
            }
        }

        // gather current proposals
        HashMap<Integer, HashSet<Transaction> > currentProposals = new HashMap<Integer, HashSet<Transaction> >();
        HashSet<Integer> senders = new HashSet<Integer>();
        for(Candidate c: candidates) {
                senders.add(c.sender);
                if (!currentProposals.containsKey(c.sender))
                    currentProposals.put(c.sender, new HashSet<Transaction>());
                currentProposals.get(c.sender).add(c.tx);
        }

        // add current proposals to node->transaction mapping history
        for(Integer key : currentProposals.keySet()) {
            int nodeId = key;
            HashSet<Transaction> currentNodeProposals = currentProposals.get(nodeId);
            for(Transaction currT : currentNodeProposals) {
                if (nodeTransactions.containsKey(nodeId) == false)
                    nodeTransactions.put(nodeId, new HashSet<Transaction>());
                nodeTransactions.get(nodeId).add(currT);
            }
        }

        // blacklist a followee if they were inactive in current round
        for(int x = 0; x < followees.length; x++)
            if(followees[x] && senders.contains(x) == false) {
                followees[x] = false;
                // remove any proposals that came from blacklisted followee
                HashSet<Transaction> txToBeDeleted = nodeTransactions.get(x);
                if (txToBeDeleted != null)
                    for(Transaction t : txToBeDeleted)
                        proposals.remove(t);
            }

        // remove revoked transactions from poposal list
        for(int i = 0; i < followees.length; i++) {
            if (followees[i] == false)
                continue;
            
            // compare current proposals to previous proposals
            HashSet<Transaction> currentProposalSet = currentProposals.get(i);
            HashSet<Transaction> previousProposalSet = previousProposals.get(i);

            for(Transaction t: previousProposalSet) {
                if(currentProposalSet.contains(t) == false)
                    proposals.remove(t);
            }
        }

        for(Candidate c: candidates) {
            Transaction tx = c.tx;
            if(followees[c.sender])
                proposals.add(c.tx);
        }

        previousProposals = currentProposals;
    }
}
