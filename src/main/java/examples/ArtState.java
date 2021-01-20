package examples;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.Party;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// Cordaのソースコードを書くためのサンプルコード
// Like all states, implements `ContractState`.
@BelongsToContract(ArtContract.class)
public class ArtState implements ContractState {
    // The attributes that will be stored on the ledger as part of the state.
    // 著者
    private final String artist;
    // タイトル
    private final String title;
    // 鑑定者
    private final Party appraiser;
    // 所有者
    private final Party owner;

    // The constructor used to create an instance of the state.
    public ArtState(String artist, String title, Party appraiser, Party owner) {
        this.artist = artist;
        this.title = title;
        this.appraiser = appraiser;
        this.owner = owner;
    }

    // Overrides `participants`, the only field defined by `ContractState`.
    // Defines which parties will store the state.
    @NotNull
    @Override
    public List<AbstractParty> getParticipants() {
        return ImmutableList.of(appraiser, owner);
    }

    // Getters for the state's attributes.
    public String getArtist() {
        return artist;
    }

    public String getTitle() {
        return title;
    }

    public Party getAppraiser() {
        return appraiser;
    }

    public Party getOwner() {
        return owner;
    }
}