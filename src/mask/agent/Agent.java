/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mask.agent;

import mask.executor.MKExecutor;
import mask.world.IWorld;
import mask.executor.RemoteExecutor;
import mask.rununit.RunUnit;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mask.service.IService;
import static mask.utils.Utils.deepCopy;

/**
 *
 * @author zj
 */
public class Agent extends RunUnit implements Serializable {

    protected transient List<Behavior> globalBehaviors = new ArrayList<>();
    protected final transient Map<Enum, List<Behavior>> stateBehaviors = new HashMap<>();
    protected Enum state;
    private static final Map<String, Integer> agentMap = new HashMap<>();
    private AtTimeCondition recordCondition;
    private int time;
    private final String name;
    private final int sequence;
    private final String process;
    private final String className;
    private final int uniqueID;

    public Agent() {
        this(null);
    }

    public Agent(String name) {
        this.className = this.getClass().getSimpleName();
        Integer id;
        synchronized (agentMap) {
            id = agentMap.get(className);
            id = (id == null) ? 0 : id + 1;
            agentMap.put(className, id);
        }
        this.sequence = id;
        this.process = process();

        if (name == null || name.equals("")) {
            this.name = this.process + ":" + this.className + "[" + this.sequence + "]";
        } else {
            this.name = name;
        }
        this.uniqueID = service().registerAgent(this.name);
    }

    private Agent getCopy() {
        if (MKExecutor.getExecutor() instanceof RemoteExecutor) {
            return copy();
        }
        return deepCopy(copy());
    }

    protected Agent copy() {
        return this;
    }

    protected static IWorld world() {
        return MKExecutor.getExecutor().world();
    }

    protected static IService service() {
        return MKExecutor.getExecutor().getService();
    }

    public static int time() {
        return MKExecutor.getExecutor().getTime();
    }

    public static long step() {
        return MKExecutor.getExecutor().getStep();
    }

    public static String process() {
        return MKExecutor.getExecutor().process();
    }

    protected String getProcess() {
        return process;
    }

    protected List<Behavior> getGlobalHehaviors() {
        return globalBehaviors;
    }

    protected void setStateBehaviors(Enum state, Behavior... behaviors) {
        stateBehaviors.put(state, Arrays.asList(behaviors));
    }

    @Override
    public void setup() {
        if (MKExecutor.getExecutor().isLoggingEnabled()) {
            this.recordCondition = new AtTimeCondition(time() + 1);
            this.getGlobalHehaviors().add(new Behavior(recordCondition, () -> logging()));
        }
    }

    protected boolean logging() {
//        System.out.println(time() + " at time begin "+ this);
        service().logging(getCopy());
        time = time();
        this.recordCondition.setAtTime(time() + 1);
        return false;
    }

    @Override
    public void run() {
        if (globalBehaviors != null) {
            for (Behavior b : globalBehaviors) {
                if (b.getCondition().evalue()) {
                    if (b.getHandler().handle()) {
                        service().setChanged(true);
                    }
                }
            }
        }
        List<Behavior> behaviors = stateBehaviors.get(state);
        if (behaviors != null) {
            for (Behavior b : behaviors) {
                if (b.getCondition().evalue()) {
                    if (b.getHandler().handle()) {
                        service().setChanged(true);
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public void stop() {
        if (MKExecutor.getExecutor().isLoggingEnabled()) {
            service().logging(getCopy());
        }
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @return the uniqueID
     */
    public int getUniqueID() {
        return uniqueID;
    }
}
