package db.client;

import com.rstone.db.jni.JNIClient;
import com.rstone.db.jni.condition.SearchCon;
import com.rstone.db.jni.result.TrendResult;
import com.rstone.db.model.FlowData;
import com.rstone.db.model.FlowDataUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class NewESClientFactory {

    private static final Logger logger = Logger.getLogger(NewESClientFactory.class);

    private static NewESClientFactory me = new NewESClientFactory();

    private TransportClient defaultClient;

    private TransportClient defaultWriteOnlyClient;

    private NewESClient defaultDelegateClient;

    private NewESClient defaultDelegateWriteOnlyClient;

    private Map<String, String> defaultClientSettings;

    private TransportAddress[] clusterAddresses;

    private TransportAddress[] clusterAddressesWriteOnly;

    private Map<String, TransportClient> clientPool = Collections.synchronizedMap(new HashMap<String, TransportClient>());

    private boolean smartMode = true;

    private List<Long> latestRetries = Collections.synchronizedList(new LinkedList<Long>());

    private NewESClientFactory() {
    }

    public static final NewESClientFactory me() {
        return me;
    }

    public Map<String, String> getDefaultClientSettings() {
        return defaultClientSettings;
    }

    public void setDefaultClientSettings(Map<String, String> defaultClientSettings) {
        this.defaultClientSettings = defaultClientSettings;
    }

    public TransportAddress[] getClusterAddresses() {
        return clusterAddresses;
    }

    public void setClusterAddresses(TransportAddress[] clusterAddresses) {
        this.clusterAddresses = clusterAddresses;
    }


    public void start(String clusterName, String address) {
        defaultClientSettings = new HashMap<>();
        defaultClientSettings.put("cluster.name", clusterName);
        defaultClientSettings.put("client.transport.sniff", "true");
        defaultClientSettings.put("client.transport.ping_timeout", "60s");
        if (address != null && address.trim().length() > 0) {
            initIpAddress(address);
        } else
            initDefaultIpAddress();
        if (clusterAddresses != null && clusterAddresses.length > 0) {
            connect(false, false);
        }
    }

    private NewESClient connect(boolean create, boolean w) {
        try {
            //创建配置setting
            Settings settings = Settings.builder().put("cluster.name", defaultClientSettings.get("cluster.name"))
                    .put("client.transport.sniff", defaultClientSettings.get("client.transport.sniff"))
                    .put("client.transport.ping_timeout", defaultClientSettings.get("client.transport.ping_timeout"))
                    .build();
            //使用setting创建客户端
            TransportClient client = new PreBuiltTransportClient(settings);

            //给节点配置 ip
            if (smartMode || !w) {
                client.addTransportAddresses(clusterAddresses);
            } else {
                client.addTransportAddresses(clusterAddressesWriteOnly);
            }

            try {
                while (latestRetries.size() > 0 && System.currentTimeMillis() - latestRetries.get(0) >= 3 * 60 * 1000) {
                    latestRetries.remove(0);
                }

                if (latestRetries.size() < 5) {
                    long start = System.currentTimeMillis();
                    latestRetries.add(start);
                    for (int i = 0; i < 5; i++) { //重试并等待，以确保ES服务已启动
                        if (client.connectedNodes().size() > 0) {
                            break;
                        }

                        if (logger.isDebugEnabled())
                            logger.debug("Waiting ES cluster started ......");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {

                        }
                    }
                    if (client.connectedNodes().size() == 0) {
                        client.close();
                        int waiting = (int) ((System.currentTimeMillis() - start) / 1000);
                        logger.error("ES cluster connected fail after waiting " + waiting + "s.");
                        return null;
                    }

                    if (logger.isDebugEnabled())
                        logger.debug(Thread.currentThread().getName() + ": waiting for green status...");
//                    client.admin().cluster().prepareHealth().setWaitForGreenStatus().setTimeout("30s").get();
                    latestRetries.clear();
                } else {
                    if (logger.isDebugEnabled())
                        logger.debug(Thread.currentThread().getName() + ": skip waiting this time.");
                    client.close();
                    return null;
                }

                if (latestRetries.size() >= 5) {
                    latestRetries.remove(0);
                }
            } catch (Exception e) {
                logger.error("ES cluster connect failed, caused by: ", e);
                if (client != null)
                    client.close();
                return null;
            }
            NewESClient delegateClient = new NewESClient(client);
            if (logger.isDebugEnabled())
                logger.debug(Thread.currentThread().getName() + " putting in es connection pool: " + delegateClient.getId());
            clientPool.put(delegateClient.getId(), client);

            if (!create) {
                if (w) {
                    defaultWriteOnlyClient = client;
                    defaultDelegateWriteOnlyClient = delegateClient;
                } else {
                    defaultClient = client;
                    defaultDelegateClient = delegateClient;
                }
            }

            return delegateClient;
        } catch (Exception e) {
            logger.error("NewESClient connect:" + e);
        }
        return null;
    }

    /**
     * 配置默认地址 端口
     */
    private void initDefaultIpAddress() {
        try {
            clusterAddresses = new TransportAddress[]{
                    new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300)
            };
        } catch (UnknownHostException e) {
            logger.error("", e);
        }
    }

    /**
     * 根据参数,配置地址  端口
     *
     * @param address
     */
    private void initIpAddress(String address) {
        String[] addressArr = address.split(",");
        clusterAddresses = new TransportAddress[addressArr.length];
        for (int i = 0; i < addressArr.length; i++) {
            String addr = addressArr[i];
            String ip = addr;
            int port = 9300;
            if (addr.indexOf(":") > -1) {
                String[] pair = addr.split(":");
                ip = pair[0];
                port = Integer.parseInt(pair[1]);
            }
            try {
                InetAddress ia = InetAddress.getByName(ip);
                TransportAddress a = new TransportAddress(ia,9300);
                clusterAddresses[i] = new TransportAddress(InetAddress.getByName(ip), port);
            } catch (UnknownHostException e) {
                logger.error("", e);
            }
        }
    }


    public void stop() {
        for (TransportClient client : clientPool.values()) {
            if (client != null)
                client.close();
        }
        clientPool.clear();
        clusterAddresses = clusterAddressesWriteOnly = null;
        defaultClient = defaultWriteOnlyClient = null;
        defaultDelegateClient = defaultDelegateWriteOnlyClient = null;
    }

    public NewESClient getDefaultDelegateClient() {
        if (!smartMode)
            return getReadOnlyDelegateClient();
        if (null == defaultDelegateClient) {
            ReentrantLock lock = new ReentrantLock(); //总觉得这个地方不对啊
            lock.lock();
            try {
                connect(false, false);
            } finally {
                lock.unlock();
            }
        }
        return defaultDelegateClient;
    }

    public NewESClient getReadOnlyDelegateClient() {
        if (smartMode)
            return getDefaultDelegateClient();
        return defaultDelegateClient;
    }

    public NewESClient getWriteOnlyDelegateClient() {
        if (smartMode)
            return getDefaultDelegateClient();
        return defaultDelegateWriteOnlyClient;
    }

    public NewESClient createDelegateClient() {
        return connect(true, false);
    }

    public NewESClient createReadOnlyClient() {
        if (smartMode)
            return createDelegateClient();
        return connect(true, false);
    }

    public NewESClient createWriteOnlyClient() {
        if (smartMode)
            return createDelegateClient();
        return connect(true, true);
    }

    public void closeClient(NewESClient client) {
        if (client != null) {
            TransportClient tc = clientPool.get(client.getId());
            if (tc != null && tc != defaultClient && tc != defaultWriteOnlyClient) {
                tc.close();
                if (logger.isDebugEnabled())
                    logger.debug(Thread.currentThread().getName() + ": closed es connection.");
                clientPool.remove(client.getId());
            }
        }
    }


    //        String sql = "SELECT  prot, sum(dOctets) as doctets FROM test111 where timeFirst >=  ???  and timeFirst <  ??? and srcaddr = '' " +
//                "and  and dstaddr = ''  and probe_id = ''  group by prot         prot   doctets ";
    public static void main(String[] args) {
        NewESClientFactory.me().start("elasticsearch", "172.16.3.145:9300");
//        init10billion();
//        System.out.println(System.currentTimeMillis());

//        Operation operation =Operation.between("timeFirst",1521683955200L,1521685684491L);
        SearchCon con = new SearchCon();
        con.setIndexName("testindex");
        con.setTypeName("testType");

        List<TrendResult> list = null;
        try {
            list = JNIClient.searchTrend(con,"timeFirst",60000);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(list.size());

    }

//    private static long  getMaxtime(){
//        SearchCon con = new SearchCon();
//        con.setIndexName("testindex");
//        con.setTypeName("testtype");
//
//    }

    private static void init10billion(){
        long count = 0;
        for (int x = 0 ;x<10000;x++){
            List<FlowData> list = FlowDataUtils.get1000();
            try {
                JNIClient.save("testindex", "testtype", list);
                count+=1000;
                System.out.println(count);
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
