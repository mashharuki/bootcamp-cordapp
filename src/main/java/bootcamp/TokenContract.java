package bootcamp;

import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.CommandWithParties;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.transactions.LedgerTransaction;
import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;    
import static net.corda.core.contracts.ContractsDSL.requireThat;

import java.util.List;

import examples.ArtContract;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
// スマートコントラクト実装サンプル
public class TokenContract implements Contract {
    // IDの宣言
    public static String ID = "bootcamp.TokenContract";

    // インターフェース用意
    public interface Commands extends CommandData {
        // トークンの発行
        class Issue implements Commands { }
    }

    // 検証メソッド
    // state Cordaではデータを格納する箱を意味する。
    // @param LedgerTransaction tx トランザクション
    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        // コマンド変数を用意する。
        CommandWithParties<TokenContract.Commands> command = requireSingleCommand(tx.getCommands(), TokenContract.Commands.class);
        // トランザクションへの入力
        List<ContractState> inputs = tx.getInputStates();
        // トランザクションから出力
        List<ContractState> outputs = tx.getOutputStates();
        //トークンを発行したときの検証
        if (command.getValue() instanceof TokenContract.Commands.Issue) {
            // トランザクションの内容に対して各種検証を行う。
            requireThat( req -> {
                // トランザクションの入力値をチェックする。
                req.using("Transaction must have no input states.", inputs.isEmpty());
                // トランザクションの出力値をチェックする。
                req.using("Transaction must have exactly one output.", outputs.size() == 1);
                req.using("Output must be a TokenState.", outputs.get(0) instanceof TokenState);
                // トランザクションの出力値を定義する。
                TokenState output = (TokenState) outputs.get(0);
                // トークン発行者についての検証
                req.using("Issuer must be required signer.", command.getSigners().contains(output.getIssuer().getOwningKey()));
                // トークン所有者についての検証
                req.using("Owner must be required signer.", command.getSigners().contains(output.getOwner().getOwningKey()));
                // 保有量の検証
                req.using("Amount must be positive.", output.getAmount() > 0);
                return null;
            });
        } else {
            throw new IllegalArgumentException("Unrecognised command.");
        }
    }
}
