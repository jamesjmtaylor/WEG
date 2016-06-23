package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 8/8/2015.
 */
public class fighterAircraft extends aircraft {
    public String gunpod;
    public Integer gunMaximumRange;
    public Integer gunPenetration;
    public String agm;
    public Integer agmMaximumRange;
    public Integer agmPenetration;
    public String aam;
    public Integer aamMaximumRange;
    public String asm;
    public Integer asmMaximumRange;

    public fighterAircraft() {
        super();
        this.gunpod = "null";
        this.gunMaximumRange = 0;
        this.gunPenetration = 0;
        this.agm = "null";
        this.agmMaximumRange = 0;
        this.agmPenetration = 0;
        this.aam = "null";
        this.aamMaximumRange = 0;
        this.aam = "null";
        this.asmMaximumRange = 0;
    }


    public fighterAircraft(String id, String pic, String grp, String ind,
                           Integer s, Integer a, Integer c,
                           String g, Integer gr, Integer gp,
                           String agm, Integer agmr, Integer agmp,
                           String aam, Integer aamr,
                           String asm, Integer asmr) {
        super(id, pic, grp, ind, s, a, c);
        this.gunpod = g;
        this.gunMaximumRange = gr;
        this.gunPenetration = gp;
        this.agm = agm;
        this.agmMaximumRange = agmr;
        this.agmPenetration = agmp;
        this.aam = aam;
        this.aamMaximumRange = aamr;
        this.asm = asm;
        this.asmMaximumRange = asmr;
    }

    @Override
    public List<String> getFirerEffects(Equipment target, Integer range) {
        List<String> Effects = new ArrayList<>();
        if (!(gunMaximumRange.equals(0))) {
            Effects.add(getEffect(target, range, gunpod, gunMaximumRange, gunPenetration));
        }
        if (!(agmMaximumRange.equals(0))) {
            Effects.add(getEffect(target, range, agm, agmMaximumRange, agmPenetration));
        }
        if (!(aamMaximumRange.equals(0))) {
            //Hard-coded value that getEffects will understand as effective against aircraft only
            Effects.add(getEffect(target, range, aam, aamMaximumRange, -1));
        }
        if (!(asmMaximumRange.equals(0))) {
            //Hard-coded value that getEffects will understand as effective against ships only
            Effects.add(getEffect(target, range, asm, asmMaximumRange, -2));
        }
        return Effects;
    }

    @Override
    public List<String> getStatistics() {
        List<String> statistics = new ArrayList<>();
        // Updates the displayed range as well as the effects bars for each weapon system


        statistics.add("Gun Pod");
        statistics.add(gunpod);
        statistics.add("Maximum Range");
        statistics.add(updateRange(gunMaximumRange));
        statistics.add("Penetration");
        statistics.add(gunPenetration.toString() + " mm");

        statistics.add("Air-to-Ground Missile");
        statistics.add(agm);
        statistics.add("Maximum Range");
        statistics.add(updateRange(agmMaximumRange));
        statistics.add("Penetration");
        statistics.add(agmPenetration.toString() + " mm");

        statistics.add("Air-to-Air Missile");
        statistics.add(aam);
        statistics.add("Maximum Range");
        statistics.add(updateRange(aamMaximumRange));

        statistics.add("Air-to-Surface Missile");
        statistics.add(asm);
        statistics.add("Maximum Range");
        statistics.add(updateRange(asmMaximumRange));
        statistics.addAll(super.getStatistics());
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

    private String getEffect(Equipment target, int range, String id, int max, int pen) {
        // Aircraft have one of three cases, they can target ground targets, air targets, or ships
        // TODO : Logic is currently flawed: All weapon types (asm, aam, agm) can engage ground.
        if (target instanceof aircraft) {
            // using an aam or gunpod on aircraft since agm have more than 70mm penetration and gunpods less
            if (pen < 70 && pen >= -1) {
                if (max < range) { // outside primary weapon range
                    return (id + " is out of range");
                } else {  // inside primary weapon range
                    return (id + " is effective");
                }
            }else {
                return (id + " cannot engage this target");
            }
        } else if (target instanceof littoralShip) {
                if (pen == -2) { // using an asm on a ship
                    if (max < range) { // outside primary weapon range
                        return (id + " is out of range");
                    } else {  // inside primary weapon range
                        return (id + " is effective");
                    }
                } else {
                    return (id + " cannot engage this target");
                }
        } else if (target.ceiling == 0 && pen > 0){ // attacking a ground target
                if (max < range) { // outside primary weapon range
                    return (id + " is out of range");
                } else {  // inside primary weapon range
                    if (pen < target.armorThickness / 2) { // this weapon is ineffective against front and flank armor
                        return (id + " is ineffective");
                    } else if (pen < target.armorThickness) { // this weapon can at least penetrate flank armor
                        return (id + " effective against flanks");
                    } else { // this weapon is effective
                        return (id + " is effective");
                    }
                }
        } else {
            return (id + " cannot engage this target");
        }
    }
}

