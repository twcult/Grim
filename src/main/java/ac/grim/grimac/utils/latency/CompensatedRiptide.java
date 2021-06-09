package ac.grim.grimac.utils.latency;

import ac.grim.grimac.player.GrimPlayer;

import java.util.concurrent.ConcurrentLinkedQueue;

public class CompensatedRiptide {
    // We use this class in case the anticheat thread falls behind and the player uses riptide multiple times
    // A bit excessive but might as well do it when everything else supports the anticheat falling behind

    // The integers represent the expiration of the riptide event
    ConcurrentLinkedQueue<Integer> lagCompensatedRiptide = new ConcurrentLinkedQueue<>();
    GrimPlayer player;

    public CompensatedRiptide(GrimPlayer player) {
        this.player = player;
    }

    public void addRiptide() {
        lagCompensatedRiptide.add(player.packetStateData.packetLastTransactionReceived);
    }

    public boolean getCanRiptide() {
        int lastTransactionReceived = player.lastTransactionBeforeLastMovement;

        do {
            Integer integer = lagCompensatedRiptide.peek();

            // There is no possibility for a riptide
            if (integer == null)
                return false;

            // If the movement's transaction is greater than the riptide's transaction
            // Remove the riptide possibility to prevent players from "storing" riptides
            // For example, a client could store riptides to activate in pvp
            if (integer < lastTransactionReceived) {
                lagCompensatedRiptide.poll();
                continue;
            }

            // Riptide possibility hasn't expired or been used
            return true;
        } while(true);
    }
}
