package ru.mpei.brics.extention.helpers.AgentDetector.Helpers;

import jade.core.AID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AIDHelper {

    private String aidName;
    private String aidHap;
    private String[] aidAddressesArray;

    public AIDHelper(AID myAgent) {
        this.aidName = myAgent.getName();
        this.aidHap = myAgent.getHap();
        this.aidAddressesArray = myAgent.getAddressesArray();
    }


    public AID createAid() {
        AID aid = new AID(aidName, true);
        if (aidAddressesArray != null) {
            for (String addresses : aidAddressesArray) {
                aid.addAddresses(addresses);
            }
        }
        return aid;
    }
}
