package db.client;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import java.net.InetAddress;
import java.util.*;

public class ESClientFactory1 {


    private static ESClientFactory1 me = new ESClientFactory1();

    private TransportClient defaultClient;

    private TransportClient defaultWriteOnlyClient;

//    private com.rstone.db.client.NewESClient defaultDelegateClient;
//
//    private NewESClient defaultDelegateWriteOnlyClient;

    private Map<String, String> defaultClientSettings;

    private TransportAddress[] clusterAddresses;

    private Map<String,Integer> addressMap = new HashMap<String, Integer>();

    private TransportAddress[] clusterAddressesWriteOnly;

    private Map<String, TransportClient> clientPool = Collections.synchronizedMap(new HashMap<String, TransportClient>());

    private boolean smartMode = true;

    private List<Long> latestRetries = Collections.synchronizedList(new LinkedList<Long>());

    private ESClientFactory1() {
    }

    public static final ESClientFactory1 me() {
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


    /**
     * 入口
     *
     * @param clusterName
     * @param address
     */
    public void start(String clusterName, String address) {

        //配置一些数据参数
        defaultClientSettings = new HashMap<String, String>();
        defaultClientSettings.put("cluster.name", clusterName);
        defaultClientSettings.put("client.transport.sniff", "true");
        defaultClientSettings.put("client.transport.ping_timeout", "60s");

        //将ip 端口数据切割
        if (address != null && address.trim().length() > 0) {
            //如果有数据传入的话，就是用传入的Ip  端口
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
                    addressMap.put(ip,port);
                }
            }
        } else {
            //没有的话，就是用默认的127.0.0.1   9300  配置
           addressMap.put("127.0.0.1",9300);
        }


        //看看有没有使用“聪明模式”，如果使用了……
        if ("false".equalsIgnoreCase(defaultClientSettings.get("client.transport.smart"))) {

            if (clusterAddressesWriteOnly == null || clusterAddressesWriteOnly.length < 1) {
//                logger.warn("Try to use unsmart mode with NewESClient by configuration [client.transport.smart=false], but initial cluster addresses for write only is empty, check configuration [es_transport_addresses_write_only]");
//                logger.warn("Still using smart mode with NewESClient!");
            } else {
                defaultClientSettings.put("client.transport.sniff", "false");
                smartMode = false;
            }
        }

        //执行连接操作
        if (clusterAddresses != null && clusterAddresses.length > 0) {
            connect(false, false);
        }
    }


    private void connect(boolean create, boolean w) {
        try {
            Settings settings = Settings.builder().put("cluster.name", defaultClientSettings.get("cluster.name"))
                    .put("client.transport.sniff", defaultClientSettings.get("client.transport.sniff"))
                    .put("client.transport.ping_timeout", defaultClientSettings.get("client.transport.ping_timeout"))
                    .build();
//            TransportClient client = new PreBuiltTransportClient(settings);
//            TransportAddress xx = clusterAddresses[0];
//            TransportClient client = new PreBuiltTransportClient(settings);
            TransportClient client = new PreBuiltTransportClient(settings);
            client.addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
//            client.addTransportAddress()

            client.listedNodes();
            client.close();
//
//            try {
//                while (latestRetries.size() > 0 && System.currentTimeMillis() - latestRetries.get(0) >= 3 * 60 * 1000){
//                    latestRetries.remove(0);
//                }
//
//                if (latestRetries.size() < 5) {
//                    long start = System.currentTimeMillis();
//                    latestRetries.add(start);
//                    for (int i = 0; i < 5; i++) { //重试并等待，以确保ES服务已启动
//                        if (client.connectedNodes().size() > 0) {
//                            break;
//                        }
//
//                        if (logger.isDebugEnabled())
//                            logger.debug("Waiting ES cluster started ......");
//                        try {
//                            Thread.sleep(2000);
//                        } catch (InterruptedException e) {
//                        }
//                    }
//                    if (client.connectedNodes().size() == 0) {
//                        client.close();
//                        int waiting = (int) ((System.currentTimeMillis() - start) / 1000);
//                        logger.error("ES cluster connected fail after waiting " + waiting + "s.");
//                        return null;
//                    }
//
//                    if (logger.isDebugEnabled())
//                        logger.debug(Thread.currentThread().getName() + ": waiting for green status...");
//                    client.admin().cluster().prepareHealth().setWaitForGreenStatus().setTimeout("30s").get();
//                    latestRetries.clear();
//                } else {
//                    if (logger.isDebugEnabled())
//                        logger.debug(Thread.currentThread().getName() + ": skip waiting this time.");
//                    client.close();
//                    return null;
//                }
//
//                if (latestRetries.size() >= 5) {
//                    latestRetries.remove(0);
//                }
//            } catch (Exception e) {
//                logger.error("ES cluster connect failed, caused by: ", e);
//                if (client != null)
//                    client.close();
//                return null;
//            }
//            NewESClient delegateClient = new NewESClient(client);
//            if (logger.isDebugEnabled())
//                logger.debug(Thread.currentThread().getName() + " putting in es connection pool: " + delegateClient.getId());
//            clientPool.put(delegateClient.getId(), client);
//
//            if (!create) {
//                if (w) {
//                    defaultWriteOnlyClient = client;
//                    defaultDelegateWriteOnlyClient = delegateClient;
//                } else {
//                    defaultClient = client;
//                    defaultDelegateClient = delegateClient;
//                }
//            }
//
//            return delegateClient;
        } catch (Exception e) {
            e.printStackTrace();

        }
//        return null;
    }

//    public void stop() {
//        for (TransportClient client : clientPool.values()) {
//            if (client != null)
//                client.close();
//        }
//        clientPool.clear();
//        clusterAddresses = clusterAddressesWriteOnly = null;
//        defaultClient = defaultWriteOnlyClient = null;
//        defaultDelegateClient = defaultDelegateWriteOnlyClient = null;
//    }

//    public NewESClient getDefaultDelegateClient() {
//        if (!smartMode)
//            return getReadOnlyDelegateClient();
//        if (null == defaultDelegateClient) {
//            ReentrantLock lock = new ReentrantLock();
//            lock.lock();
//            try {
//                connect(false, false);
//            } finally {
//                lock.unlock();
//            }
//        }
//        return defaultDelegateClient;
//    }
//
//    public NewESClient getReadOnlyDelegateClient() {
//        if (smartMode)
//            return getDefaultDelegateClient();
//        return defaultDelegateClient;
//    }
//
//    public NewESClient getWriteOnlyDelegateClient() {
//        if (smartMode)
//            return getDefaultDelegateClient();
//        return defaultDelegateWriteOnlyClient;
//    }
//
//    public NewESClient createDelegateClient() {
//        return connect(true, false);
//    }
//
//    public NewESClient createReadOnlyClient() {
//        if (smartMode)
//            return createDelegateClient();
//        return connect(true, false);
//    }
//
//    public NewESClient createWriteOnlyClient() {
//        if (smartMode)
//            return createDelegateClient();
//        return connect(true, true);
//    }
//
//    public void closeClient(NewESClient client) {
//        if (client != null) {
//            TransportClient tc = clientPool.get(client.getId());
//            if (tc != null && tc != defaultClient && tc != defaultWriteOnlyClient) {
//                tc.close();
//                if (logger.isDebugEnabled())
//                    logger.debug(Thread.currentThread().getName() + ": closed es connection.");
//                clientPool.remove(client.getId());
//            }
//        }
//    }

    public static void main(String[] args) {
        ESClientFactory1.me().start("elasticsearch", "127.0.0.1:9300");
    }

//    public static void main(String[] args) throws Exception{
//        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
//        List<DiscoveryNode> list = client.listedNodes();
//        for (DiscoveryNode node : list){
//
//            System.out.println(node.getAddress());
//        }
//// on shutdown
//
//        client.close();
//    }
}
