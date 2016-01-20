/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author zj
 */
public class MKService extends SimpleMessaging implements IService {

    private final transient Map<Integer, Map<String, Integer>> namedChannels = Collections.synchronizedMap(new HashMap<>());
    private transient int executorChannel;
    private transient int resultChannel;
    private transient final List<List<Integer>> channelGroups = Collections.synchronizedList(new ArrayList<>());
    private transient int executorChannelGroup;
    private final transient AtomicInteger nextChannelGroup = new AtomicInteger(0);
    private transient final AtomicInteger nextAgentID = new AtomicInteger(0);
    private transient final Map<String, Integer> agentDirectory = Collections.synchronizedMap(new HashMap<>());

    @Override
    public void setup() {
        nextAgentID.set(0);
        nextChannel.set(0);
        nextChannelGroup.set(0);
        namedChannels.clear();
        channelGroups.clear();
        agentDirectory.clear();
        channels.clear();
        executorChannel = newChannel();
        resultChannel = newChannel();
        executorChannelGroup = newChannelGroup();
    }

    @Override
    public int newChannelGroup() {
        int id = nextChannelGroup.getAndIncrement();
        channelGroups.add(Collections.synchronizedList(new ArrayList<>()));
        return id;
    }

    @Override
    public void joinChannelGroup(int groupID, int channel) {
        System.out.println(" Group ID " + executorChannelGroup + "  Group size " + channelGroups.size());
        channelGroups.get(groupID).add(channel);
    }

    @Override
    public void leaveChannelGroup(int groupID, int channel) {
        channelGroups.get(groupID).remove(channel);
    }

    @Override
    public <T extends Serializable> void sendToChannelGroup(int groupID, T message) {
        List<Integer> list = channelGroups.get(groupID);
        synchronized (list) {
            for (Integer i : list) {
                send(i, message);
            }
        }
    }

    @Override
    public void remoteJoinExecutorGroup(int channel) {
        joinChannelGroup(executorChannelGroup, channel);
    }

    @Override
    public <T extends Serializable> void sendToExecutorGroup(T message) {
        sendToChannelGroup(executorChannelGroup, message);
    }

    @Override
    public int newNamedChannel(int agentID, String mailBoxName) {
        int id = newChannel();
        Map<String, Integer> name_id = namedChannels.get(agentID);
        if (name_id == null) {
            name_id = new HashMap<>();
            namedChannels.put(agentID, name_id);
        }
        name_id.put(mailBoxName, id);
        return id;
    }

    @Override
    public <T extends Serializable> void send(int senderID, int channel, T content) {
        send(channel, new MKMessage<T>(senderID, content));
    }

    @Override
    public <T extends Serializable> void send(int senderID, int receiverID, String channelName, T content) {
        Map<String, Integer> name_id = namedChannels.get(receiverID);
        int mailBoxID = name_id.get(channelName);
        send(senderID, mailBoxID, content);
    }

//    @Override
//    public <T extends Serializable> T executorWaitReceive() {
//        return waitReceive(executorMailBoxID);
//    }
    @Override
    public <T extends Serializable> void sendToExecutor(T message) {
        send(executorChannel, message);
    }

    @Override
    public void executorWait(int nRemotes) {
        for (int i = 0; i < nRemotes; i++) {
            waitReceive(executorChannel);
        }
    }

    @Override
    public void deRegisterAgent(String agentName) {
        Integer i = agentDirectory.remove(agentName);
        assert (i != null);
    }

    @Override
    public int registerAgent(String agentName) {
        assert (agentDirectory.get(agentName) == null);
        int id = nextAgentID.getAndIncrement();
        agentDirectory.put(agentName, id);
        return id;
    }

    @Override
    public Integer lookupAgent(String agentName) {
        return agentDirectory.get(agentName);
    }

    @Override
    public Integer[] lookupAgents(String... agentNames) {
        Integer[] ids = new Integer[agentNames.length];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = agentDirectory.get(agentNames[i]);
        }
        return ids;
    }

    @Override
    public int getAgentNumber() {
        return agentDirectory.size();
    }

    @Override
    public synchronized Integer[] lookupAgents(StringFilter filter) {
        List<Integer> list = new ArrayList();
        Set<Map.Entry<String, Integer>> set = agentDirectory.entrySet();
        synchronized (set) {
            for (Map.Entry<String, Integer> entry : set) {
                if (filter.evaluate(entry.getKey())) {
                    list.add(entry.getValue());
                }
            }
        }
        if (list.isEmpty()) {
            return null;
        }
        Integer[] ids = new Integer[list.size()];
        System.out.println("Found " + list.size());
        list.toArray(ids);
        return ids;

    }

    @Override
    public <T extends Serializable> void writeResult(T result) {
        send(resultChannel, result);
    }

    @Override
    public <T extends Serializable> T getResult() {
        return receive(resultChannel);
    }

    @Override
    public <T extends Serializable> List<T> getAllResults() {
        return receiveAll(resultChannel);
    }

}
