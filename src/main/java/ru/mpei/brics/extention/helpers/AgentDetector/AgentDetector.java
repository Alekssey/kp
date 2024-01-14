package ru.mpei.brics.extention.helpers.AgentDetector;

import jade.core.AID;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.PacketListener;
import org.pcap4j.packet.Packet;
import ru.mpei.brics.extention.helpers.AgentDetector.Helpers.AIDHelper;
import ru.mpei.brics.extention.helpers.AgentDetector.Helpers.PacketBuilderInterface;
import ru.mpei.brics.extention.helpers.AgentDetector.Helpers.PcapHelper;
import ru.mpei.brics.extention.helpers.AgentDetector.Interfaces.DetectorMethodsInterface;
import ru.mpei.brics.extention.helpers.JacksonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


@Slf4j
public class AgentDetector implements DetectorMethodsInterface {


    private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(4);

    private ScheduledFuture<?> sendThread;
    private ScheduledFuture<?> discoverThread;
    private ScheduledFuture<?> deleteThread;
    private boolean discoveringActive = false;
    private boolean sendingActive = false;
    private boolean cleaningActive = false;
    private final PcapHelper pcapHelper;
//    private final JacksonHelper jackson;
//    @Setter
    private AID myAgent;
//    @Setter
    private final String iFace;
    private int port;
    int period;
    private String data = null;
    private final Map<AID, Long> activeAgents = new ConcurrentHashMap<>();
//    private List<MyAgentListener> listeners = new CopyOnWriteArrayList<>();
    private byte[] packet = null;

    public AgentDetector(AID a, String interfaceName, int period, int port) {
//        log.info("All Args Constructor was used for {}", myAgent.getLocalName());
        this.myAgent = a;
        this.iFace = interfaceName;
        this.period = period;
        this.port = port;
        pcapHelper = new PcapHelper(this.iFace, this.period);
//        this.jackson = new JacksonHelper();
        this.startDiscovering();
        this.startSending();
        this.deadAgentRemoving();
    }

    public AgentDetector(){
        this.iFace = "\\Device\\NPF_Loopback";
        this.period = 100;
        port = 1250;
        pcapHelper = new PcapHelper(this.iFace, this.period);
//        this.jackson = new JacksonHelper();
        deadAgentRemoving();
    }

    @Override
    public void startDiscovering() {
        if(!this.discoveringActive){
            log.info("{} start searching agents", myAgent.getLocalName());
            this.discoverThread = pcapHelper.startPacketsCapturing(this.port, new MyPacketListener(), ses);
            this.discoveringActive = true;
        }
    }

    @Override
    public void startSending() {

        if (!this.sendingActive){
            log.info("{} start sending packets", myAgent.getLocalName());
            if(this.data == null){
                AIDHelper ah = new AIDHelper(myAgent);
                this.data = JacksonHelper.toJackson(ah);
            }

            if(this.packet == null){
                this.packet = new PacketBuilderInterface(this.iFace)
                        .addHeader(this.data)
                        .addUdpPart(this.port)
                        .addPayload();
            }

            this.sendThread = ses.scheduleAtFixedRate(() -> this.pcapHelper.sendPacket(this.packet), this.period, this.period, TimeUnit.MILLISECONDS);
            this.sendingActive = true;
        }
    }

    private void deadAgentRemoving() {
        if(!this.cleaningActive){
            this.deleteThread = ses.scheduleAtFixedRate(() -> {
                for(AID aid : this.activeAgents.keySet()){
                    if (System.currentTimeMillis() - activeAgents.get(aid) > 5L * this.period){
                        this.activeAgents.remove(aid);
                        log.warn("Agent {} was died", aid.getLocalName());
//                        listeners.forEach(e -> e.listen(getActiveAgents()));
                    }
                }

            }, 500, 500, TimeUnit.MILLISECONDS);
            this.cleaningActive = true;
        }
    }

    @Override
    public List<AID> getActiveAgents() {
        return new ArrayList<>(activeAgents.keySet());
    }



//    @Override
//    public void subscribeOnChange(MyAgentListener l) {
//        this.listeners.add(l);
//    }

    @Override
    public void stopSending() {
        if(this.sendingActive){
            this.sendThread.cancel(true);
            this.sendingActive = false;
        }
    }

    private String parse(byte[] data){
        if (data.length < 14) return null;
        int offset = (iFace.equals("\\Device\\NPF_Loopback") ? 4 /*local*/ : 14 /*ethernet*/) + 20 /*ipv4*/ + 8 /* udp */;

        byte[]dataByte = new byte[data.length-offset];
        System.arraycopy(data,offset,dataByte,0,dataByte.length);
        return new String(dataByte);
    }

    private class MyPacketListener implements PacketListener {

        public MyPacketListener() {
        }

        @Override
        @SneakyThrows
        public void gotPacket(Packet packet) {

            String strInfo = parse(packet.getRawData());
            AID receivedAID = JacksonHelper.fromJackson(strInfo, AIDHelper.class).createAid();
            if(!(receivedAID.equals(myAgent))){
                boolean alreadyExist = activeAgents.containsKey(receivedAID);
                activeAgents.put(receivedAID, System.currentTimeMillis());
                if (!alreadyExist) log.info("Agent {} find {}", myAgent.getLocalName(), receivedAID.getLocalName());;
            }
        }
    }
}
