package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

public class SelfishMiner extends BaseMiner implements Miner {
    private Block currentHead, personalHead; // We will have two pointers - the consensus head and the chosen head our specific miner is working on, perhaps discreetly.
    private int networkHashRate; // We will keep track of the entire network's hash rate.
    private int personalHashRate; // We will compare it to our own hash rate.
    private Double percentageHash, previousPercentage;
    private int difference;

    public SelfishMiner(String id, int hashRate, int connectivity) {
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
    percentageHash = (personalHashRate*1.0)/(networkHashRate*1.0);
    Double slope = percentageHash - previousPercentage;
    System.out.println(percentageHash);
    if(isMinerMe) {
          if(personalHead == null){
              this.personalHead = block;
          }
          else if (block != null && block.getHeight() - personalHead.getHeight() > 1) {
              this.personalHead = block;
              this.currentHead = block;
          }
          else if (block != null && block.getHeight() > personalHead.getHeight()) {
              this.personalHead = block;
              // this.currentHead = block;
          }
      }
      else{
          if(personalHead == null || percentageHash >= 0.5) {
              //currentHead = block;
              this.personalHead = block;
          }
          else if( block != null && ( (personalHead.getHeight() - block.getHeight() <=1 && personalHead.getHeight() >= block.getHeight())  || percentageHash >= 0.5) ){
                  this.currentHead = personalHead;
          }
          else if(block != null && personalHead.getHeight() < block.getHeight()) {
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
