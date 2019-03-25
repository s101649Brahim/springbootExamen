package edu.ap.spring;

import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.ap.spring.service.Block;
import edu.ap.spring.service.BlockChain;
import edu.ap.spring.service.Wallet;
import edu.ap.spring.transaction.Transaction;

@Controller
public class BlockChainController {

    @Autowired
    private BlockChain bChain;
    @Autowired
    private Wallet coinbase;
    private Transaction genesisTransaction;

    private Wallet walletA, walletB;

    public void init() {
        walletA = new Wallet("walletA");
        walletB = new Wallet("walletB");
        bChain.setSecurity();
        coinbase.generateKeyPair();
        walletA.generateKeyPair();
        walletB.generateKeyPair();

        // create genesis transaction, which sends 100 coins to walletA:
        genesisTransaction = new Transaction(coinbase.getPublicKey(), walletA.getPublicKey(), 100f);
        genesisTransaction.generateSignature(coinbase.getPrivateKey()); // manually sign the genesis transaction
        genesisTransaction.transactionId = "0"; // manually set the transaction id

        // creating and Mining Genesis block
        Block genesis = new Block();
        genesis.setPreviousHash("0");
        genesis.addTransaction(genesisTransaction, bChain);
        bChain.addBlock(genesis);
    }

    public void transaction1() {
        Block block = new Block();
        block.setPreviousHash(bChain.getLastHash());

        try {
            block.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f), bChain);
        } catch (Exception e) {
        }

        bChain.addBlock(block);
    }

    @GetMapping("/balance/{input}")
    public String balance(Model model, @PathVariable("input") String input) {
        init();
        transaction1();
        if (walletA.toString() == input) {
            model.addAttribute("wallet", walletA.getBalance());
        } else if (walletB.toString() == input) {
            model.addAttribute("wallet", walletB.getBalance());
        }
        return "wallet";
    }

}
