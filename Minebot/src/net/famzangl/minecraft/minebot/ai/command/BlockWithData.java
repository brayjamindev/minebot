package net.famzangl.minecraft.minebot.ai.command;

import java.awt.image.ColorModel;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import net.famzangl.minecraft.minebot.ai.ColoredBlockItemFilter;
import net.famzangl.minecraft.minebot.ai.path.world.BlockMetaSet;
import net.famzangl.minecraft.minebot.ai.path.world.BlockSet;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;

/**
 * This is the object wrapper for a block (id) and meta value pair. You can use
 * it to make the code cleaner.
 * 
 * It also provides methods to convert from blockid+meta to simple block id or
 * meta.
 * 
 * @see BlockWithDataOrDontcare
 * @author Michael Zangl
 *
 */
public class BlockWithData extends BlockWithDataOrDontcare {

	private static class NicerMetaValue {
		BlockSet forBlocks;
		String[] values;

		public NicerMetaValue(BlockSet forBlocks, String[] values) {
			super();
			if (values.length != 16) {
				throw new IllegalArgumentException();
			}
			this.forBlocks = forBlocks;
			this.values = values;
		}

		@Override
		public String toString() {
			return "NicerMetaValue [forBlocks=" + forBlocks + ", values="
					+ Arrays.toString(values) + "]";
		}
	}

	private static ArrayList<NicerMetaValue> nicerMeta = new ArrayList<BlockWithData.NicerMetaValue>();

	static {
		String[] colors = new String[16];
		for (EnumDyeColor color : EnumDyeColor.values()) {
			colors[color.getMetadata()] = color.getName();
		}
		nicerMeta.add(new NicerMetaValue(
				ColoredBlockItemFilter.COLORABLE_BLOCKS, colors));
	}

	static BlockWithDataOrDontcare fromNiceMeta(Block block, String meta) {
		for (NicerMetaValue n : nicerMeta) {
			if (n.forBlocks.containsAny(block.getIdFromBlock(block))) {
				int index = ArrayUtils.indexOf(n.values, meta);
				if (index >= 0) {
					return new BlockWithData(block, index);
				}
			}
		}
		throw new IllegalArgumentException("Could not understand meta value: "
				+ meta + " for " + block);
	}

	public BlockWithData(int blockWithMeta) {
		super(blockWithMeta);
	}

	public BlockWithData(Block block, int meta) {
		this(Block.getIdFromBlock(block), meta);
	}

	public BlockWithData(int blockId, int meta) {
		this(toBlockWithMeta(blockId, meta));
	}

	public static int toBlockWithMeta(int blockId, int meta) {
		return (blockId << 4) | (meta & 0xf);
	}

	@Override
	public String toBlockString() {
		return getBlockString() + ":" + getMetaString();
	}

	private String getMetaString() {
		int value = getMetaValue();
		int blockId = getBlockId();
		for (NicerMetaValue n : nicerMeta) {
			if (n.forBlocks.containsAny(blockId)) {
				String str = n.values[value];
				if (str != null) {
					return str;
				}
			}
		}
		return value + "";
	}

	public int getMetaValue() {
		return blockIdWithMeta & 0xf;
	}

	@Override
	public BlockSet toBlockSet() {
		return new BlockMetaSet(blockIdWithMeta);
	}

	@Override
	public boolean containedIn(BlockSet blockSet) {
		return blockSet.containsWithMeta(blockIdWithMeta);
	}

	public int getBlockWithMeta() {
		return blockIdWithMeta;
	}
}