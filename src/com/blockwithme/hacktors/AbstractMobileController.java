/**
 *
 */
package com.blockwithme.hacktors;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Base class to help implementing MobileController.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public abstract class AbstractMobileController implements MobileController {

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#updatedPosition(boolean)
     */
    @Override
    public void updatedPosition(final boolean changedLevel) {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#updatedDirection()
     */
    @Override
    public void updatedDirection() {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#pickedUp(com.blockwithme.hacktors.Item[])
     */
    @Override
    public void pickedUp(final Item[] array) {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#damaged(int, java.lang.Object)
     */
    @Override
    public void damaged(final int amount, final Object source) {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#dead()
     */
    @Override
    public void dead() {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#attacked(com.blockwithme.hacktors.Mobile, com.blockwithme.hacktors.Item, boolean)
     */
    @Override
    public void attacked(final Mobile other, final Item item,
            final boolean killed) {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#attacked(com.blockwithme.hacktors.Block, com.blockwithme.hacktors.Item, boolean)
     */
    @Override
    public void attacked(final Block block, final Item item,
            final boolean destroyed) {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#itemAdded(com.blockwithme.hacktors.Item)
     */
    @Override
    public void itemAdded(final Item theItem) {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#itemRemoved(com.blockwithme.hacktors.Item)
     */
    @Override
    public void itemRemoved(final Item theItem) {
        // NOP
    }

    /* (non-Javadoc)
     * @see com.blockwithme.hacktors.MobileController#ate(com.blockwithme.hacktors.Item)
     */
    @Override
    public void ate(final Item item) {
        // NOP
    }
}
