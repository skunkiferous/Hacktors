package com.blockwithme.hacktors;

import org.apache.commons.lang.ArrayUtils;

import com.blockwithme.util.Enum40;
import com.google.common.base.Preconditions;

/**
 * The data associated with each BlockType.
 *
 * @author monster
 */
public class BlockType extends Enum40<BlockType> {
	public static final BlockType Bedrock = new BlockType(0, true);
	public static final BlockType Empty = new BlockType(0, false);
	public static final BlockType Stone = new BlockType(100, true);
	public static final BlockType Earth = new BlockType(30, true);
	public static final BlockType Tree = new BlockType(30, true);
	public static final BlockType ClosedDoor = new BlockType(20, true);
	public static final BlockType OpenDoor = new BlockType(10, false);
	public static final BlockType ClosedChest = new BlockType(20, false);
	public static final BlockType EmptyChest = new BlockType(10, false);
	public static final BlockType StairsUp = new BlockType(200, false);
	public static final BlockType StairsDown = new BlockType(200, false);
	public static final BlockType Anvil = new BlockType(100, false);
	public static final BlockType Trap = new BlockType(20, false);

	/** All the values */
	public static final BlockType[] VALUES = Enum40.values(BlockType.class);

    /** AN empty array of block types. */
	public static final BlockType[] EMPTY = new BlockType[0];

    /** The life of a block of this type. */
    private final int life;

    /**
     * Are blocks of this type solid?
     * A mobile cannot be located where a solid block is.
     */
    public final boolean solid;

    /**
     * Chooses one block type at random.
     * Note that Bedrock is excluded from the selection.
     */
    public static BlockType choose() {
        // We never generate bedrock!
        return BlockType.VALUES[Util.RND.nextInt(BlockType.VALUES.length - 1) + 1];
    }

    /** Crates a Block type. */
    BlockType(final int theLife, final boolean theSolid) {
    	super(BlockType.class);
        Preconditions.checkArgument(theLife >= 0, "Life must be >= 0");
        life = theLife;
        solid = theSolid;
    }

    /** Finalizes the initialization of an block of this type. */
    public Block postInit(final Block block) {
        Preconditions.checkNotNull(block);
        Preconditions.checkArgument(block.getType() == this);
        block.setLife((life == -1) ? -1 : life);
        if (this == BlockType.ClosedChest) {
            final int count = Util.RND.nextInt(2) + 1;
            Item[] content = block.getContent();
            for (int i = 0; i < count; i++) {
                content = (Item[]) ArrayUtils.add(content, Item.create());
            }
            block.setContent(content);
        }
        return block;
    }
}