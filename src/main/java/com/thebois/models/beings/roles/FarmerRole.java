package com.thebois.models.beings.roles;

/**
 * The farmer harvests crops.
 */
class FarmerRole extends AbstractRole {

    @Override
    public RoleType getType() {
        return RoleType.FARMER;
    }

}