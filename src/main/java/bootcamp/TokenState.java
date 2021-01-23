package bootcamp;

import com.google.common.collect.ImmutableList;

import net.corda.core.contracts.Amount;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/* Our state, defining a shared fact on the ledger.
 * See src/main/java/examples/ArtState.java for an example. */
//トークンが持つべき箱情報を定意義したJavaクラス
@BelongsToContract(bootcamp.TokenContract.class)
public class TokenState implements ContractState {
    // トークンの発行者
    private final Party issuer;
    // トークンの保有者
    private final Party owner;
    // 送金額
    private final int amount;

    private final List<AbstractParty> participants;

    // コンストラクター
    public TokenState(Party issuer, Party owner, int amount) {
        this.issuer = issuer;
        this.owner = owner;
        this.amount = amount;
        this.participants = new ArrayList<>();
        participants.add(issuer);
        participants.add(owner);
    }

    // ゲッター関数を定義する。
    public Party getIssuer() {
        return issuer;
    }

    public Party getOwner() {
        return owner;
    }

    public int getAmount() { return amount; }

    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return participants;
    }

}