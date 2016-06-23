package jamestaylor.weg.Equipment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by James on 8/5/2015.
 */
// TODO : complete class (currently just copy and paste of fighting vehicle
public class fixedWing extends fighterAircraft {

    public Integer runway;

    public fixedWing() {
        super();
        this.runway = 0;
    }

    public fixedWing(String id, String pic, String grp, String ind,
                     Integer s, Integer a, Integer c,
                     String g, Integer gr, Integer gp,
                     String agm, Integer agmr, Integer agmp,
                     String aam, Integer aamr,
                     String asm, Integer asmr, Integer runway, Integer armor) {
        super(id, pic, grp, ind, s, a, c, g, gr, gp, agm, agmr, agmp, aam, aamr, asm, asmr);
        this.runway = runway;
        this.armorThickness = armor;

    }

    @Override
    public List<String> getFirerEffects(Equipment target, Integer range){
        return super.getFirerEffects(target, range);
    }

    @Override
    public List<String> getStatistics() {
        List<String> statistics = new ArrayList<>();
        statistics.addAll(super.getStatistics()); // call to the fighterAircraft to get its statistics first
        statistics.add("Minimum Runway Length");
        statistics.add(runway.toString() + " meters");

        return statistics;
    }

}