package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 8/8/2015.
 */
public class antiAircraft extends Equipment {

    public String missile;
    public Integer missileMaximumRange;
    public Integer missileMaximumAltitude;
    public Integer missileMinimumAltitude;
    public String gun;
    public Integer gunMaximumRange;
    public Integer gunMaximumAltitude;
    public Integer gunPenetration;

    public antiAircraft() {
        super();
        this.missile = "null";
        this.missileMaximumRange = 0;
        this.missileMaximumAltitude = 0;
        this.missileMinimumAltitude = 0;
        this.gun = null;
        this.gunMaximumRange = 0;
        this.gunMaximumAltitude = 0;
        this.gunPenetration = 0;
    }


    public antiAircraft(String id, String pic, String grp, String ind,
                        String m, Integer mr, Integer ma, Integer mm,
                        String g, Integer gr, Integer ga, Integer gp) {
        super(id, pic, grp, ind);
        this.missile = m;
        this.missileMaximumRange = mr;
        this.missileMaximumAltitude = ma;
        this.missileMinimumAltitude = mm;
        this.gun = g;
        this.gunMaximumRange = gr;
        this.gunMaximumAltitude = ga;
        this.gunPenetration = gp;
    }

    @Override
    public List<String> getFirerEffects(Equipment target, Integer range) {
        List<String> Effects = new ArrayList<>();
        if (!(missileMaximumRange.equals(0))) {
            Effects.add(getEffect(target, range, missile,
                    missileMaximumRange, missileMaximumAltitude, missileMinimumAltitude, 100));}
        if (!(gunMaximumRange.equals(0))) {
            Effects.add(getEffect(target, range, gun,
                    gunMaximumRange, gunMaximumAltitude, 0, gunPenetration));}
        return Effects;
    }

    @Override
    public List<String> getStatistics() {
        List<String> statistics = new ArrayList<>();
        // Updates the displayed range as well as the effects bars for each weapon system

        statistics.add("Surface to Air Missile");
        statistics.add(missile);
        statistics.add("Maximum Range");
        statistics.add(updateRange(missileMaximumRange));
        statistics.add("Maximum Altitude");
        statistics.add(updateRange(missileMaximumAltitude));
        statistics.add("Minimum Altitude");
        statistics.add(updateRange(missileMinimumAltitude));

        statistics.add("Anti-Aircraft Gun");
        statistics.add(gun);
        statistics.add("Maximum Range");
        statistics.add(updateRange(gunMaximumRange));
        statistics.add("Maximum Altitude");
        statistics.add(updateRange(gunMaximumAltitude));
        statistics.add("Penetration");
        statistics.add(gunPenetration.toString() + " mm");
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

    private String getEffect(Equipment target, int range,  String id, int mr, int maxA, int minA, int pen) {
        // if the target's highest altitude is not below the weapon's targetable altitude then it can engage
        if (target.ceiling >= minA) {
            // TODO : Make the note more meaningful if altitude is exceed and give it a red color.
            if (mr < range) { // outside primary weapon range
                return (id + " is out of range");
            } else if (target.ceiling > maxA){
                return (target.id + "'s ceiling exceeds " + id +" max altitude");
            } else {  // inside primary weapon range
                if (target instanceof unmannedAircraft) {
                    return (target.id + "'s heat and radar signatures are too small to be targeted.");
                } else if (pen < target.armorThickness / 2) { // this weapon is ineffective against front and flank armor
                    return (id + " is ineffective");
                } else if (pen < target.armorThickness) { // this weapon can at least penetrate flank armor
                    return (id + " effective against flanks");
                } else { // this weapon is effective
                    return (id + " is effective");
                }
            }
        } else if (target.ceiling < minA) {// if the target's ceiling is below the minimum altitude you will never be able to engage it
            return (id + " cannot engage this target");
        }
        return null;
    }
}
