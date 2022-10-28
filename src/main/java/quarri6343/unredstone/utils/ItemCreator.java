package quarri6343.unredstone.utils;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.ArrayList;
import java.util.List;

/**
 * このクラスの新しいインスタンスを作ることでアイテムの実体を1から作成することができる<br>
 * 引数にアイテムの実体または固有アイテムの型を渡すことで改造することもできる<br>
 */
public class ItemCreator {

    private ItemStack itemStack;

    /**
     * バニラアイテムの型からバニラアイテムの実体/固有アイテムの型を生成する
     * @param material
     */
    @ParametersAreNonnullByDefault
    public ItemCreator(Material material) {
        this.itemStack = new ItemStack(material, 1, (short) 0);
    }

    /**
     * バニラアイテムの実体/固有アイテムの型/固有アイテムの実体を改造する
     * @param itemStack
     */
    @ParametersAreNonnullByDefault
    public ItemCreator(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    /**
     * バニラアイテムの名前を変更する
     * @param name 名前
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator setName(Component name) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.displayName(name);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * バニラアイテムの型を変更する
     * @param type 設定したい型
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator setType(Material type) {
        itemStack.setType(type);
        return this;
    }

    /**
     * このクラスが改造しているバニラアイテムの型を取得する
     * @return バニラアイテムの型
     */
    @Nonnull
    public Material getType() {
        return itemStack.getType();
    }

    /**
     * アイテムの量を設定する
     * @param amount 設定した医療
     * @return このクラス自体
     */
    public ItemCreator setAmount(int amount) {
        itemStack.setAmount(amount);
        return this;
    }

    /**
     * このクラスが改造しているアイテムの量を取得する
     * @return アイテムの量
     */
    public int getAmount() {
        return itemStack.getAmount();
    }

    /**
     * アイテムにブロックデータを設定する
     * @param data 設定したいブロックデータ
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator setData(BlockData data) {
        if (itemStack instanceof BlockDataMeta) {
            ((BlockDataMeta) itemStack).setBlockData(data);
        }
        return this;
    }

    /**
     * アイテムが破壊不可能かどうか設定する
     * @param value 破壊不可能かどうか
     * @return このクラス自体
     */
    public ItemCreator setUnbreakable(boolean value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setUnbreakable(value);
            itemStack.setItemMeta(meta);
            itemStack.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        }
        return this;
    }

    /**
     * アイテムのフラグ(エンチャントを隠すかなど)を設定する
     * @param flags 設定したいフラグ
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator SetFlags(ItemFlag... flags) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.addItemFlags(flags);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * アイテムの説明文を消去する
     * @return このクラス自体
     */
    public ItemCreator clearlores() {
        setLores(new ArrayList<>());
        return this;
    }

    /**
     * 1行のアイテムの説明文を設定する
     * @param lore 設定したい説明文
     * @return このクラス自体
     */
    @ParametersAreNullableByDefault
    public ItemCreator setLore(Component lore){
        List<Component> lores = new ArrayList<>();
        lores.add(lore);
        return setLores(lores);
    }

    /**
     * アイテムの説明文を設定する
     * @param lores 設定したい説明文達
     * @return このクラス自体
     */
    @ParametersAreNullableByDefault
    public ItemCreator setLores(List<Component> lores) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.lore(lores);
        }
        itemStack.setItemMeta(meta);
        return this;
    }

    /**
     * アイテムの説明文を1行追加する
     * @param lore 追加したい説明文
     * @return このクラス自体
     */
    @ParametersAreNullableByDefault
    public ItemCreator addLore(Component lore) {
        if (lore == null) {
            return this;
        }
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            List<Component> lores = meta.lore();
            if (lores == null) {
                lores = new ArrayList<>();
            }
            lores.add(lore);
            meta.lore(lores);
            itemStack.setItemMeta(meta);
        }
        return this;
    }

    /**
     * アイテムの全ての説明文を取得
     * @return アイテムの説明文
     */
    @Nullable
    public List<Component> getLores() {
        return itemStack.getItemMeta().lore();
    }

    @ParametersAreNonnullByDefault
    public ItemCreator addEnchantment(Enchantment enchantment) {
        return addEnchantment(enchantment, 1);
    }

    /**
     * アイテムにエンチャントを付与する
     * @param enchantment 付与したいエンチャント
     * @param level エンチャントのレベル
     * @return このクラス自体
     */
    @ParametersAreNonnullByDefault
    public ItemCreator addEnchantment(Enchantment enchantment, int level) {
        itemStack.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * ビルダーを終了して作成または改造したアイテムを受け取る
     * @return 作成または改造されたアイテム
     */
    @Nonnull
    public ItemStack create() {
        return this.itemStack;
    }

    /**
     * アイテムの名前を取得する
     * @return アイテムの名前
     */
    @Nonnull
    public String getName() {
        return itemStack.getItemMeta().getDisplayName();
    }

    /**
     * アイテムのエンチャントを隠す
     * @return このクラス自体
     */
    public ItemCreator hideEnchantment() {
        itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        return this;
    }

    /**
     * アイテムの属性を隠す
     * @return このクラス自体
     */
    public ItemCreator hideAttributes() {
        itemStack.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        return this;
    }

    /**
     * アイテムの耐久値を減らす/回復させる
     * @param damage 減らしたい/回復させたい耐久値の量
     * @return このクラス自体
     */
    public ItemCreator damage(int damage){
        if(!(itemStack.getItemMeta() instanceof Damageable)){
            return this;
        }

        Damageable damagable = ((Damageable) itemStack.getItemMeta());
        damagable.setDamage(damagable.getDamage() + damage);
        itemStack.setItemMeta(damagable);
        return this;
    }
}
