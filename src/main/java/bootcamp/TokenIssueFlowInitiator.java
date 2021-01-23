package bootcamp;

import com.google.common.collect.ImmutableList;

import co.paralleluniverse.fibers.Suspendable;
import examples.ArtContract;

import net.corda.core.flows.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;
import net.corda.core.contracts.CommandData;

import java.security.PublicKey;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * トランザクションを提案する側のJavaクラス
 * @InitiatingFlow トランザクションを提案する能動的なFlowであることを示している
 * @StartableByRPC RPC経由でこのFlowを呼び出すことが可能であることを示している
 */
@InitiatingFlow
@StartableByRPC
public class TokenIssueFlowInitiator extends FlowLogic<SignedTransaction> {
    // トークンの所有者
    private final Party owner;
    // トークンの保有量
    private final int amount;
    // コンストラクター
    public TokenIssueFlowInitiator(Party owner, int amount) {
        this.owner = owner;
        this.amount = amount;
    }
    // ProgressTrackerを宣言
    private final ProgressTracker progressTracker = new ProgressTracker();

    @Override
    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    @Suspendable
    @Override
    public SignedTransaction call() throws FlowException {
        // 自ノードのノータリーノードを指定する。
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        // We get a reference to our own identity.
        Party issuer = getOurIdentity();
        // TokenState用のオブジェクトを宣言
        TokenState tokenState = new TokenState(issuer, owner, amount);
        // We build our transaction.
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
        // コマンドを用意
        CommandData commandData = new TokenContract.Commands.Issue();
        // トランザクションの組み立てに必要なコマンドを組み込む。
        transactionBuilder.addCommand(commandData, issuer.getOwningKey());
        // トランザクションデータの中にtokenStateを格納する。
        transactionBuilder.addOutputState(tokenState, TokenContract.ID);
        // We check our transaction is valid based on its contracts.
        transactionBuilder.verify(getServiceHub());
        // 署名済みトランザクションを相手に送って検証する。
        FlowSession session = initiateFlow(owner);
        // We sign the transaction with our private key, making it immutable.
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);
        // 送信側にトランザクションを送信する処理
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));
        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));
    }
}
