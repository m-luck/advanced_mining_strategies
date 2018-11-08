package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

public class MajorityMiner extends BaseMiner implements Miner {
    private Block currentHead, personalHead; // We will have two pointers - the consensus head and the chosen head our specific miner is working on, perhaps discreetly.
    private int networkHashRate; // We will keep track of the entire network's hash rate.
    private int personalHashRate; // We will compare it to our own hash rate.
    private Double percentageHash, previousPercentage;
    private int difference;

    public MajorityMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
        this.personalHashRate = hashRate;
        this.previousPercentage = (1.0*personalHashRate) / (1.0*networkHashRate);
    }

    @Override
    public Block currentlyMiningAt() {
        return personalHead; // This now can potentially differ from the CompliantMiner's head.
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {

        percentageHash = (1.0*personalHashRate) / (1.0*networkHashRate);
        Double slope = percentageHash - previousPercentage;

        difference = block.getHeight() - personalHead.getHeight();
        // System.out.println(networkHashRate);
        if(isMinerMe) {
            if (block.getHeight() > currentHead.getHeight()) {
                this.currentHead = block;
                // System.out.println(block.getMinedBy());
                this.personalHead = block; // Update both from now on.
            }
        }
        else{
            if (currentHead == null) {
                currentHead = block;
                this.currentHead = block;
                this.personalHead = block;
              } else if(block != null && ( (slope < (difference*-0.0009) && percentageHash <= 0.50 && difference > 2))) {
                this.currentHead = block;
                this.personalHead = block;

            }
        }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
        this.personalHead = genesis;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
      this.networkHashRate = statistics.getTotalHashRate(); // This is relevant information.
    }
}
