package edu.nyu.crypto.csci3033.miners;

import edu.nyu.crypto.csci3033.blockchain.Block;
import edu.nyu.crypto.csci3033.blockchain.NetworkStatistics;

public class FeeSnipingMiner extends BaseMiner implements Miner {
    private Block currentHead;
    private Double percentageHash;
    private int networkHashRate = 0;
    private int blockCount=0;
    private int personalHashRate;
    private Double avgBlockValue=7.3;
    private Double expectedAvgReturn;
    private boolean feather;

    public FeeSnipingMiner(String id, int hashRate, int connectivity) {
        super(id, hashRate, connectivity);
        this.personalHashRate = hashRate;
    }

    @Override
    public Block currentlyMiningAt() {
        return currentHead;
    }

    @Override
    public Block currentHead() {
        return currentHead;
    }

    @Override
    public void blockMined(Block block, boolean isMinerMe) {
      blockCount++;
      percentageHash = (1.0*personalHashRate) / (1.0*networkHashRate);
      expectedAvgReturn = avgBlockValue * percentageHash;
      avgBlockValue = ( (avgBlockValue*(1.0*blockCount-1)) + block.getBlockValue())/ (blockCount*1.0);
      // System.out.println(avgBlockValue);
      // results in 7.3, so expected return per block is 7.3 * percentageHash.
      // if expected return of having to remine a block + getting the next block > 7.3 * percentageHash^2, then do it!
      // System.out.println(networkHashRate);
      if (networkHashRate == 0) {
        this.currentHead = block;
        return;
      }
      Double thisBlockReturn = 1.0*block.getBlockValue() * percentageHash;
        if(isMinerMe) {
            if (block.getHeight() > currentHead.getHeight() || currentHead == null) {
                this.currentHead = block;
            }
        }
        else{
            if (currentHead == null) {
                this.currentHead = block;
            }
            else if (block != null && block.getHeight() - currentHead.getHeight() > 2) {
                this.currentHead = block;
                feather = false;
            }
            else {
              if( thisBlockReturn * percentageHash > 2*expectedAvgReturn ) {
                Block backStep = block.getPreviousBlock();
                if(backStep!=null){
                  this.currentHead = backStep;
                  feather = true;
                }
                else if (feather==false){
                  this.currentHead = block;
                }
              }
              else { this.currentHead = block;}
            }
          }
    }


    @Override
    public void initialize(Block genesis, NetworkStatistics networkStatistics) {
        this.currentHead = genesis;
    }

    @Override
    public void networkUpdate(NetworkStatistics statistics) {
          this.networkHashRate = statistics.getTotalHashRate(); // This is relevant information.
    }
}
