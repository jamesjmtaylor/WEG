package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by James on 8/5/2015.
 */

public class groundWeapon extends Equipment {

    public String priWeapon;
    public Integer priMaximumRange;
    public Integer priPenetration;
    public String altWeapon;
    public Integer altMaximumRange;
    public Integer altPenetration;
    public String atgmWeapon;
    public Integer atgmMaximumRange;
    public Integer atgmPenetration;

    public groundWeapon() {
        super();
        this.priWeapon = "null";
        this.priMaximumRange = 0;
        this.priPenetration = 0;
        this.altWeapon = "null";
        this.altMaximumRange = 0;
        this.altPenetration = 0;
        this.atgmWeapon = "null";
        this.atgmMaximumRange = 0;
        this.atgmPenetration = 0;
    }


    public groundWeapon(String id, String pic, String grp, String ind,
                        String pw, Integer pwr, Integer pwp,
                        String aw, Integer awr, Integer awp,
                        String at, Integer atwr, Integer atwp) {
        super(id, pic, grp, ind);
        this.priWeapon = pw;
        this.priMaximumRange = pwr;
        this.priPenetration = pwp;
        this.altWeapon = aw;
        this.altMaximumRange = awr;
        this.altPenetration = awp;
        this.atgmWeapon = at;
        this.atgmMaximumRange = atwr;
        this.atgmPenetration = atwp;
    }

    @Override
    public  List<String> getFirerEffects(Equipment target, Integer range) {
        List<String> Effects = new ArrayList<>();
        if (!(priMaximumRange.equals(0))) {
        Effects.add(getEffect(target, range, priWeapon, priMaximumRange, priPenetration,
                priWeapon.contains("auto"), priWeapon.contains("APFSDS")));}
        if (!(altMaximumRange.equals(0))) {
        Effects.add(getEffect(target, range, altWeapon, altMaximumRange, altPenetration,
                altWeapon.contains("auto"), altWeapon.contains("APFSDS")));}
        if (!(atgmMaximumRange.equals(0))) {
        Effects.add(getEffect(target, range, atgmWeapon, atgmMaximumRange, atgmPenetration,
                false, false));}
        return Effects;
    }

    @Override
    public List<String> getStatistics() {
        List<String> statistics = new ArrayList<>();
        // Updates the displayed range as well as the effects bars for each weapon system

        statistics.add("Primary Weapon");
        statistics.add(priWeapon);
        statistics.add("Maximum Range");
        statistics.add(updateRange(priMaximumRange));
        statistics.add("Penetration");
        statistics.add(priPenetration.toString() + " mm");

        statistics.add("Secondary Weapon");
        statistics.add(altWeapon);
        statistics.add("Maximum Range");
        statistics.add(updateRange(altMaximumRange));
        statistics.add("Penetration");
        statistics.add(altPenetration.toString() + " mm");

        statistics.add("Anti-Tank Guided Missile");
        statistics.add(atgmWeapon);
        statistics.add("Maximum Range");
        statistics.add(updateRange(atgmMaximumRange));
        statistics.add("Penetration");
        statistics.add(atgmPenetration.toString() + " mm");
        return statistics;
    }

    private String updateRange(Integer range) {
        if (range > 8000) {
            //return (Math.round(((double)range / 1000)) + " kilometers");
            return (range / 1000) + " kilometers";
        } else {
            return range + " meters";
        }
    }

    private String getEffect(Equipment target, int range,  String id, int max, int pen, Boolean auto, Boolean APFSDS) {

        if (max < range) { // outside primary weapon range
            return (id + " is out of range");
        } else if (!(target instanceof littoralShip || target instanceof fixedWing)) {  // inside primary weapon range, just check its not a ship or plane
            if (auto) { // if the weapon is an auto-cannon it receives 10mm increase/decrease in penetration for every 1000m that it is closer/farther away from the target
                pen = pen + (max / 2 - range) / 1000 * 10;
            }
            else if (APFSDS) { // if the weapon is a SABOT it receives 20mm increase/decrease in penetration for every 1000m that it is closer/farther away from the target
                pen = pen + (max / 2 - range) / 1000 * 20;
            }
            if (pen < target.armorThickness/2) { // this weapon is ineffective against front and flank armor
                return (id + " is ineffective");
            } else if (pen < target.armorThickness)  { // this weapon can at least penetrate flank armor
                return (id + " effective against flanks");
            } else { // this weapon is effective
                return (id + " is effective");
            }
        } else {
            return (id + " cannot engage this target");
        }
    }
}
