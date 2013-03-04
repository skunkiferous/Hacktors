/**
 *
 */
package com.blockwithme.hacktors;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Used to crawl over all mobiles in the game.
 *
 * @author monster
 */
@ParametersAreNonnullByDefault
public interface MobileVisitor {
    /** Visit mobile. */
    void visit(final Mobile mobile);
}
