package xaos.tiles.entities.items.military;

import xaos.main.Game;
import xaos.tiles.entities.items.Item;
import xaos.tiles.entities.items.ItemManager;
import xaos.tiles.entities.items.ItemManagerItem;
import xaos.tiles.entities.living.LivingEntity;
import xaos.utils.Messages;

import java.awt.*;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class MilitaryItem extends Item implements Externalizable {

    public final static int LOCATION_HEAD = 1;
    public final static int LOCATION_BODY = 2;
    public final static int LOCATION_LEGS = 3;
    public final static int LOCATION_FEET = 4;
    public final static int LOCATION_WEAPON = 5;
    private static final long serialVersionUID = 2994148931507925799L;
    private int attackModifier;
    private int attackSpeedModifier;
    private int defenseModifier;
    private int healthModifier;
    private int damageModifier;
    private int LOSModifier;
    private int movePCTModifier;
    private int walkSpeedModifier;

    private PrefixSuffixData prefix;
    private PrefixSuffixData suffix;

    public MilitaryItem() {
        super();
    }

    public MilitaryItem(String sIniHeader) {
        super(sIniHeader);
        refreshTransients();
    }

    /**
     * Comprueba si el item pasado es mejor que lo que lleve puesto la living
     *
     * @param le   Living a mirar
     * @param item Objeto a comparar
     * @return true si el item pasado es mejor que lo que lleve puesto la living
     */
    public static boolean isBetterItem(LivingEntity le, MilitaryItem item) {
        MilitaryItem itemWeared = null;
        ItemManagerItem imi = ItemManager.getItem(item.getIniHeader());
        itemWeared = switch (imi.getLocation()) {
            case LOCATION_HEAD -> le.getEquippedData().getHead();
            case LOCATION_BODY -> le.getEquippedData().getBody();
            case LOCATION_LEGS -> le.getEquippedData().getLegs();
            case LOCATION_FEET -> le.getEquippedData().getFeet();
            case LOCATION_WEAPON -> le.getEquippedData().getWeapon();
            default -> itemWeared;
        };

        if (itemWeared != null) {
            // Lleva algo, los comparamos
            // S�lo miramos el level del item
            return ItemManager.getItem(itemWeared.getIniHeader()).getLevel() < imi.getLevel();
        }

        return true;
    }

    public String getTileName() {
        if (prefix == null && suffix == null) {
            return super.getTileName();
        } else {
            StringBuilder sBuffer = new StringBuilder();

            if (prefix != null) {
                sBuffer.append("["); //$NON-NLS-1$
                sBuffer.append(prefix.getName());
                sBuffer.append("] "); //$NON-NLS-1$
            }

            sBuffer.append(super.getTileName());

            if (suffix != null) {
                sBuffer.append(" ["); //$NON-NLS-1$
                sBuffer.append(suffix.getName());
                sBuffer.append("]"); //$NON-NLS-1$
            }

            return sBuffer.toString();
        }
    }

    public int getAttackModifier() {
        int iTmp = 0;
        if (prefix != null) {
            iTmp = prefix.getAttack();
        }
        if (suffix != null) {
            iTmp += suffix.getAttack();
        }
        return attackModifier + iTmp;
    }

    public void setAttackModifier(int attackModifier) {
        this.attackModifier = attackModifier;
    }

    public int getAttackSpeedModifier() {
        int iTmp = 0;
        if (prefix != null) {
            iTmp = prefix.getAttackSpeed();
        }
        if (suffix != null) {
            iTmp += suffix.getAttackSpeed();
        }
        return attackSpeedModifier + iTmp;
    }

    public void setAttackSpeedModifier(int attackSpeedModifier) {
        this.attackSpeedModifier = attackSpeedModifier;
    }

    public int getWalkSpeedModifier() {
        int iTmp = 0;
        if (prefix != null) {
            iTmp = prefix.getWalkSpeed();
        }
        if (suffix != null) {
            iTmp += suffix.getWalkSpeed();
        }
        return walkSpeedModifier + iTmp;
    }

    public void setWalkSpeedModifier(int walkSpeedModifier) {
        this.walkSpeedModifier = walkSpeedModifier;
    }

    public int getDefenseModifier() {
        int iTmp = 0;
        if (prefix != null) {
            iTmp = prefix.getDefense();
        }
        if (suffix != null) {
            iTmp += suffix.getDefense();
        }
        return defenseModifier + iTmp;
    }

    public void setDefenseModifier(int defenseModifier) {
        this.defenseModifier = defenseModifier;
    }

    public int getHealthModifier() {
        int iTmp = 0;
        if (prefix != null) {
            iTmp = prefix.getHealthPoints();
        }
        if (suffix != null) {
            iTmp += suffix.getHealthPoints();
        }
        return healthModifier + iTmp;
    }

    public void setHealthModifier(int healthModifier) {
        this.healthModifier = healthModifier;
    }

    public int getDamageModifier() {
        int iTmp = 0;
        if (prefix != null) {
            iTmp = prefix.getDamage();
        }
        if (suffix != null) {
            iTmp += suffix.getDamage();
        }
        return damageModifier + iTmp;
    }

    public void setDamageModifier(int damageModifier) {
        this.damageModifier = damageModifier;
    }

    public int getLOSModifier() {
        int iTmp = 0;
        if (prefix != null) {
            iTmp = prefix.getLOS();
        }
        if (suffix != null) {
            iTmp += suffix.getLOS();
        }
        return LOSModifier + iTmp;
    }

    public void setLOSModifier(int lOSModifier) {
        LOSModifier = lOSModifier;
    }

    public int getMovePCTModifier() {
        int iTmp = 0;
        if (prefix != null) {
            iTmp = prefix.getMovePCT();
        }
        if (suffix != null) {
            iTmp += suffix.getMovePCT();
        }
        return movePCTModifier + iTmp;
    }

    public void setMovePCTModifier(int movePCTModifier) {
        this.movePCTModifier = movePCTModifier;
    }

    public PrefixSuffixData getPrefix() {
        return prefix;
    }

    public void setPrefix(PrefixSuffixData prefix) {
        this.prefix = prefix;
    }

    public PrefixSuffixData getSuffix() {
        return suffix;
    }

    public void setSuffix(PrefixSuffixData suffix) {
        this.suffix = suffix;
    }

    public String getExtendedTilename() {
        ItemManagerItem imi = ItemManager.getItem(getIniHeader());
        StringBuilder sb = new StringBuilder(getTileName());

        if (imi != null && imi.getLevel() > 0) {
            sb.append(Messages.getString("MilitaryItem.1")); //$NON-NLS-1$
            sb.append(imi.getLevel());
            sb.append(")"); //$NON-NLS-1$
        }
        if (getAttackModifier() != 0) {
            sb.append(Messages.getString("MilitaryItem.0")).append(getAttackModifier()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (getDefenseModifier() != 0) {
            sb.append(Messages.getString("MilitaryItem.2")).append(getDefenseModifier()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (getHealthModifier() != 0) {
            sb.append(Messages.getString("MilitaryItem.4")).append(getHealthModifier()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (getDamageModifier() != 0) {
            sb.append(Messages.getString("MilitaryItem.6")).append(getDamageModifier()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        if (getLOSModifier() != 0) {
            sb.append(Messages.getString("MilitaryItem.7")).append(getLOSModifier()).append("]"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        return sb.toString();
    }

    /**
     * Devuelve el color para pintar el item en los menues, se basa en si tiene
     * prefijo y/o sufijo
     *
     * @return el color para pintar el item en los menues, se basa en si tiene
     * prefijo y/o sufijo
     */
    public Color getItemTextColor() {
        Color color = null;
        if (getPrefix() != null) {
            if (getSuffix() != null) {
                color = Color.BLUE;
            } else {
                color = Color.GREEN;
            }
        } else {
            if (getSuffix() != null) {
                color = Color.GREEN;
            }
        }

        return color;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        attackModifier = in.readInt();
        attackSpeedModifier = in.readInt();
        defenseModifier = in.readInt();
        healthModifier = in.readInt();
        damageModifier = in.readInt();
        LOSModifier = in.readInt();
        movePCTModifier = in.readInt();
        walkSpeedModifier = in.readInt();
        if (Game.SAVEGAME_LOADING_VERSION <= Game.SAVEGAME_V11) {
            in.readInt();
        }
        prefix = (PrefixSuffixData) in.readObject();
        suffix = (PrefixSuffixData) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeInt(attackModifier);
        out.writeInt(attackSpeedModifier);
        out.writeInt(defenseModifier);
        out.writeInt(healthModifier);
        out.writeInt(damageModifier);
        out.writeInt(LOSModifier);
        out.writeInt(movePCTModifier);
        out.writeInt(walkSpeedModifier);
        out.writeObject(prefix);
        out.writeObject(suffix);
    }
}
