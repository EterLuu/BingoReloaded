package io.github.steaf23.bingoreloaded.gui.creator;

import io.github.steaf23.bingoreloaded.data.TranslationData;
import io.github.steaf23.bingoreloaded.gui.AbstractGUIInventory;
import io.github.steaf23.bingoreloaded.gui.SubMenuUI;
import io.github.steaf23.bingoreloaded.item.InventoryItem;
import io.github.steaf23.bingoreloaded.item.tasks.*;
import io.github.steaf23.bingoreloaded.util.FlexColor;
import io.github.steaf23.bingoreloaded.util.GUIPreset5x9;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.*;

public class ListEditorUI extends SubMenuUI
{
    private AbstractGUIInventory items;
    private AbstractGUIInventory advancements;
    private AbstractGUIInventory statistics;
    private final String listName;

    private static final InventoryItem ITEMS = new InventoryItem(GUIPreset5x9.THREE_CENTER.positions[0], Material.APPLE, TITLE_PREFIX + "Items", "Click to add or remove items");
    private static final InventoryItem ADVANCEMENTS = new InventoryItem(GUIPreset5x9.THREE_CENTER.positions[1], Material.ENDER_EYE, TITLE_PREFIX + "Advancements", "Click to add or remove advancements");
    private static final InventoryItem STATISTICS = new InventoryItem(GUIPreset5x9.THREE_CENTER.positions[2], Material.GLOBE_BANNER_PATTERN, TITLE_PREFIX + "Statistics", "Click to add or remove statistics");

    private static final InventoryItem SAVE = new InventoryItem(49, Material.REDSTONE, "" + ChatColor.RED + ChatColor.BOLD + TranslationData.translate("menu.save_exit"));
    private static final InventoryItem BLANK = new InventoryItem(Material.BLACK_STAINED_GLASS_PANE, " ", "");

    public ListEditorUI(String listName, AbstractGUIInventory parent)
    {
        super("", parent);
        this.listName = listName;
        addMenuOption(ITEMS, createItemPicker());
        addMenuOption(ADVANCEMENTS, createAdvancementPicker());
        addMenuOption(STATISTICS, createStatisticsPicker());
        fillOptions(BLANK.inSlot(45),
                BLANK.inSlot(46),
                BLANK.inSlot(47),
                BLANK.inSlot(48),
                SAVE,
                BLANK.inSlot(50),
                BLANK.inSlot(51),
                BLANK.inSlot(52),
                BLANK.inSlot(53));
    }

    @Override
    public void delegateClick(InventoryClickEvent event, int slotClicked, Player player, ClickType clickType)
    {
        if (slotClicked == SAVE.getSlot())
        {
            close(player);
        }
        super.delegateClick(event, slotClicked, player, clickType);
    }

    public AbstractGUIInventory createStatisticsPicker()
    {
        AbstractGUIInventory statistics = new StatisticPickerUI(this, listName);
        return statistics;
    }

    private static List<InventoryItem> getItemOptions()
    {
        return new ArrayList<>();
    }

    private AbstractGUIInventory createItemPicker()
    {
        Set<Material> glassPanes = new HashSet<>();
        for (FlexColor flexColor : FlexColor.values())
        {
            glassPanes.add(flexColor.glassPane);
        }

        List<BingoTask> tasks = new ArrayList<>();
        for (Material m : Material.values())
        {
            if (!m.name().contains("LEGACY_") && !glassPanes.contains(m) && m.isItem() && !m.isAir())
            {
                tasks.add(new BingoTask(new ItemTask(m, 1)));
            }
        }

        TaskPickerUI itemPicker = new TaskPickerUI(tasks,"Select Items", this, listName)
        {
            @Override
            public void handleClose(final InventoryCloseEvent event)
            {
//                super.handleClose(event);
//                List<ItemTask> tasks = new ArrayList<>();
//                getSelectedItems().forEach((item) -> {
//                    ItemTask newItem = new ItemTask(item.getType(), item.getAmount());
//                    tasks.add(newItem);
//                });
//                TaskListsData.saveTasks(listName, tasks.toArray(BingoTask[]::new));
            }
        };
        return itemPicker;
    }

    private AbstractGUIInventory createAdvancementPicker()
    {
        List<BingoTask> tasks = new ArrayList<>();
//        for (Iterator<Advancement> it = Bukkit.advancementIterator(); it.hasNext(); )
//        {
//            Advancement a = it.next();
//            String key = a.getKey().getKey();
//            if (key.startsWith("recipes/") || key.endsWith("/root"))
//            {
//                continue;
//            }
//
//            AdvancementTask task = new AdvancementTask(a);
//            tasks.add(task);
//            //TODO: move this to BingoTaskBuilder
////            InventoryItem item = new AdvancementListItem(a);
////            new ItemTextBuilder(ChatColor.DARK_PURPLE)
////                    .translate(ItemTextBuilder.getAdvancementDescKey(a))
////                    .text("" + ChatColor.GRAY + "Click to make this item\n appear on bingo cards")
////                    .buildDescription(item);
////            new ItemTextBuilder(ChatColor.AQUA, "italic")
////                    .text("[")
////                    .translate(ItemTextBuilder.getAdvancementTitleKey(a))
////                    .text("]")
////                    .buildName(item);
////            options.add(item);
//        }

        TaskPickerUI advancementPicker = new TaskPickerUI(tasks, "Add Advancements", this, listName)
        {
            @Override
            public void handleClose(final InventoryCloseEvent event)
            {
                super.handleClose(event);
                //TODO: REDO
//                List<AdvancementTask> tasks = new ArrayList<>();
//                getSelectedItems().forEach((item) -> {
//                    if ((item instanceof AdvancementListItem advItem))
//                    {
//                        AdvancementTask slot = new AdvancementTask(advItem.advancement);
//                        tasks.add(slot);
//                    }
//                });
//                BingoTasksData.saveTasks(listName, tasks.toArray(AdvancementTask[]::new));
            }
        };
        return advancementPicker;
    }
}
