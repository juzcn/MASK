/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.service;

import java.io.Serializable;
import java.util.List;
import mask.agent.Agent;

/**
 *
 * @author zj
 */
public interface IService extends ISimpleMessaging {

    public void setup();

    public int newNamedChannel(int agentID, String mailBoxName);

    public <T extends Serializable> void send(int senderID, int channel, T content);

    public <T extends Serializable> void send(int senderID, int receiverID, String channelName, T content);

    public <T extends Serializable> void sendToExecutor(T message);

    public int newChannelGroup();

    public void joinChannelGroup(int groupID, int channel);

    public void leaveChannelGroup(int groupID, int channel);

    public <T extends Serializable> void sendToChannelGroup(int groupID, T message);

    public void remoteJoinExecutorGroup(int channel);

    public <T extends Serializable> void sendToExecutorGroup(T message);

    public void executorWait(int nRemotes);

    public void deRegisterAgent(String agentName);

    public int getAgentNumber();

    public Integer lookupAgent(String agentName);

    public Integer[] lookupAgents(String... agentNames);

    public Integer[] lookupAgents(StringFilter filter);

    public int registerAgent(String agentName);

    public <T extends Serializable> void writeResult(T result);

    public <T extends Serializable> T getResult();

    public <T extends Serializable> List<T> getAllResults();

    public void logging(Agent agent);

    public Agent[] getLogging();

    public void setChanged(boolean changed);

    public boolean isChanged();

}
