/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 * 
 * Botania is Open Source and distributed under a
 * Creative Commons Attribution-NonCommercial-ShareAlike 3.0 License
 * (http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB)
 * 
 * File Created @ [Feb 3, 2014, 9:59:17 PM (GMT)]
 */
package vazkii.botania.client.core.handler;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import vazkii.botania.api.mana.IManaCollector;
import vazkii.botania.api.mana.TileSignature;
import vazkii.botania.client.core.handler.LightningHandler.LightningBolt;
import vazkii.botania.client.gui.lexicon.GuiLexicon;
import vazkii.botania.common.core.handler.ConfigHandler;
import vazkii.botania.common.core.handler.ManaNetworkHandler;
import vazkii.botania.common.item.ItemTwigWand;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class ClientTickHandler {

	public static int ticksWithLexicaOpen = 0;
	public static int pageFlipTicks = 0;
	public static int ticksInGame = 0;

	@SubscribeEvent
	public void clientTickEnd(ClientTickEvent event) {
		if(event.phase == Phase.END) {
			LightningBolt.update();

			if(Minecraft.getMinecraft().theWorld == null)
				ManaNetworkHandler.instance.clear();

			GuiScreen gui = Minecraft.getMinecraft().currentScreen;
			if(gui == null || !gui.doesGuiPauseGame()) {
				ticksInGame++;

				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				if(player != null) {
					ItemStack stack = player.getCurrentEquippedItem();
					if(stack != null && stack.getItem() instanceof ItemTwigWand) {
						List<TileSignature> list = new ArrayList(ManaNetworkHandler.instance.getAllCollectorsInWorld(Minecraft.getMinecraft().theWorld));
						for(TileSignature sig : list) {
							if(!sig.remoteWorld)
								continue;

							TileEntity tile = sig.tile;
							if(tile instanceof IManaCollector)
								((IManaCollector) tile).onClientDisplayTick();
						}
					}
				}
			}

			int ticksToOpen = 10;
			if(gui instanceof GuiLexicon) {
				if(ticksWithLexicaOpen < 0)
					ticksWithLexicaOpen = 0;
				if(ticksWithLexicaOpen < ticksToOpen)
					ticksWithLexicaOpen++;
				if(pageFlipTicks > 0)
					pageFlipTicks--;
			} else {
				pageFlipTicks = 0;
				if(ticksWithLexicaOpen > 0) {
					if(ticksWithLexicaOpen > ticksToOpen)
						ticksWithLexicaOpen = ticksToOpen;
					ticksWithLexicaOpen--;
				}
			}
		}
	}

	@SubscribeEvent
	public void renderTickEnd(RenderTickEvent event) {
		if(event.phase == Phase.END && ConfigHandler.baublesInventoryButton)
			InventoryBaublesButtonHandler.renderBaublesIcon();
	}

	public static void notifyPageChange() {
		if(pageFlipTicks == 0)
			pageFlipTicks = 5;
	}

}
