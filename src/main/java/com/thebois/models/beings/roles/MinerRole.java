package com.thebois.models.beings.roles;

/**
 * The miner digs stone out of rocks.
 */
class MinerRole extends AbstractRole {

    @Override
    public RoleType getType() {
        return RoleType.MINER;
    }

}
