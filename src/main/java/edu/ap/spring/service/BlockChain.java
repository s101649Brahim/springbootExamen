package edu.ap.spring.service;

import java.security.Security;
import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import edu.ap.spring.transaction.Transaction;
import net.minidev.json.JSONObject;

@Service
@Scope("singleton")
public class BlockChain {
	
	private ArrayList<Block> blocks = new ArrayList<Block>();
	private int difficulty = 1;
	
	public BlockChain() {}
	
	public void setSecurity() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); //setup Bouncey castle as a Security Provider	
	}
	
	public ArrayList<Block> getBlocks() {
		return this.blocks;
	}

	public Boolean isChainValid() {
		Block currentBlock; 
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		//loop through blockchain to check hashes:
		for(int i = 1; i < blocks.size(); i++) {
			
			currentBlock = blocks.get(i);
			previousBlock = blocks.get(i-1);
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash()) ){
				System.out.println("# Current Hashes not equal");
				return false;
			}
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash) ) {
				System.out.println("# Previous Hashes not equal");
				return false;
			}
			//check if hash is solved
			if(!currentBlock.hash.substring( 0, difficulty).equals(hashTarget)) {
				System.out.println("# This block hasn't been mined");
				return false;
			}
		}
		System.out.println("Blockchain is valid");
		return true;
	}
	
	public void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		this.blocks.add(newBlock);
	}

	public String toJSON() {
		
		JSONObject jsonObj = new JSONObject();
		for(int i = 0; i < blocks.size(); i++) {
			
			Block currentBlock = blocks.get(i);
			JSONObject blockObj = new JSONObject();
			blockObj.put("hash", currentBlock.hash);
			blockObj.put("merkleRoot", currentBlock.merkleRoot);
			blockObj.put("nonce", currentBlock.nonce);
			blockObj.put("previousHash", currentBlock.previousHash);
			blockObj.put("timeStamp", currentBlock.timeStamp);
			JSONObject[] trs = new JSONObject[currentBlock.getTransactions().size()];
			int j = 0;
			for(Transaction t : currentBlock.getTransactions()) {
				JSONObject tr = new JSONObject();
				tr.put("recipient", t.recipient.toString());
				tr.put("sender", t.sender.toString());
				tr.put("transactionId", t.transactionId);
				tr.put("value", t.value);

				trs[j] = tr;
				j++;
			}
			
			blockObj.put("transactions", trs);
			jsonObj.put("block" + i, blockObj.toString()); 
		}

		return jsonObj.toJSONString();
	}

	public String getLastHash() {
		return this.blocks.get(this.blocks.size() - 1).hash;
	}
}
