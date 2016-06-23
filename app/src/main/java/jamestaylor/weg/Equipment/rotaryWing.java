package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 8/5/2015.
 */
// TODO : complete class (currently just copy and paste of fighting vehicle
public class rotaryWing extends fighterAircraft {

    public String rocketPod;
    public Integer rocketMaxRange;
    public Integer slingLoad;

    public rotaryWing() {
        super();
        this.rocketPod = "null";
        this.rocketMaxRange = 0;
        this.slingLoad = 0;
    }

    public rotaryWing(String id, String pic, String grp, String ind,
                      Integer s, Integer a, Integer c,
                      String g, Integer gr, Integer gp,
                      String agm, Integer agmr, Integer agmp,
                      String aam, Integer aamr,
                      String asm, Integer asmr,
                      String rp, Integer rpmr,
                      Integer sl, Integer armor) {
        super(id, pic, grp, ind, s, a, c, g, gr, gp, agm, agmr, agmp, aam, aamr, asm, asmr);
        this.rocketPod = rp;
        this.rocketMaxRange = rpmr;
        this.slingLoad = sl;
        this.armorThickness = armor;

    }

    @Override
    public List<String> getFirerEffects(Equipment target, Integer range){
        List<String> Effects = new ArrayList<>();
        Effects.addAll(super.getFirerEffects(target, range));
        if (!(rocketMaxRange.equals(0))) {
            if (target.ceiling > 0){
                Effects.add(rocketPod + " cannot engage this target");
            } else if (rocketMaxRange < range) { // outside primary weapon range
                Effects.add(rocketPod + " is out of range");
            } else if (target.armorThickness > 50){  // inside primary weapon range
                Effects.add(rocketPod + " is ineffective");
            } else {
                Effects.add(rocketPod + " is effective");
            }
        }
        return Effects;
    }

    @Override
    public List<String> getStatistics() {
        List<String> statistics = new ArrayList<>();

        statistics.add("Rocket Pod");
        statistics.add(rocketPod);
        statistics.add("Rocket Maximum Range");
        statistics.add(updateRange(rocketMaxRange));
        statistics.addAll(super.getStatistics()); // call to the groundWeapon to get its statistics
        statistics.add("Slingload capacity");
        statistics.add(slingLoad.toString() + " mt");

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
}