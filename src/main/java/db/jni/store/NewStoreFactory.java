package db.jni.store;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.rstone.db.client.NewESClient;
import com.rstone.db.client.NewESClientFactory;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class NewStoreFactory {

    private Logger logger = Logger.getLogger(NewStoreFactory.class);

    // 创建私有对象
    private static NewESClient client = null;

    //创建批量请求 对象
    private BulkRequestBuilder bulkRequest = null;

    //创建一个本地线程，这个线程会每 X 秒钟将缓存里面的数据写入ES
    private LoadThread loadthread = null;

    private AtomicInteger writenum = new AtomicInteger(0);

    //写入ES的 时间间隔
    private long inserttime = 100;

    //jackson的 对象--json转换器
    private ObjectMapper mapper = new ObjectMapper();

    //构造器
    public NewStoreFactory() {
        init();
    }

    //创建一个本地线程，这个线程会每 X 秒钟将缓存里面的数据写入ES
    public class LoadThread extends Thread {

        private boolean isstop = false;

        public void run() {
            while (!isstop) {
                try {
                    Thread.sleep(inserttime);
                } catch (InterruptedException e) {
                    logger.error("", e);
                }
                commitToDB();
            }
        }

        public void setStop(boolean stop) {
            this.isstop = stop;
        }
    }

    //初始化方法
    private void init() {
        Integer itime = 1;
        if (itime != null)
            inserttime = itime * 1000;
        openClient();
        loadthread = new LoadThread();
        loadthread.start();
    }

    //获取client节点
    private synchronized void openClient() {
        if (client != null) {
            bulkRequest = client.prepareBulk();
            return;
        }
        try {
            client = NewESClientFactory.me().getWriteOnlyDelegateClient();
            bulkRequest = client.prepareBulk();
        } catch (Exception e) {
            logger.info("Open client: " + e.getMessage());
        }
    }

    //将数据提交
    private synchronized void commitToDB() {
        try {
            if (writenum.get() > 0) {
                writenum.set(0);
                if (bulkRequest != null) {
                    bulkRequest.execute();
                    bulkRequest = null;
                    bulkRequest = client.prepareBulk();
                }
            }
        } catch (Exception e) {
            logger.info("Commit event data: " + e.getMessage());
            if (client != null)
                NewESClientFactory.me().closeClient(client);
            client = null;
            openClient();
        }
    }

    //将数据一次插入到批量提交的请求冲，等待提交
    @SuppressWarnings("unchecked")
    public synchronized void save(String indexName, String typeName, Object... objs) {
        if (bulkRequest == null)
            return;
        try {
            if (client != null) {
                for (Object obj : objs) {
                    if (obj instanceof List) {
                        List<Object> list = (List<Object>) obj;
                        for (Object event : list) {
                            add(indexName, typeName, event);
                        }
                    } else {
                        add(indexName, typeName, obj);
                    }
                }
            } else {
                System.out.println("client  NULL");
            }
        } catch (Exception e) {
            writenum.incrementAndGet();
            e.printStackTrace();
            logger.info("Insert event data: ", e);
        }
    }

    //将数据一次插入到批量提交的请求冲，等待提交，并将计数器加以
    private void add(String indexName, String typeName, Object obj) throws Exception {
        Gson gson = new Gson();
        String sss = gson.toJson(obj);
        IndexRequest request = client.prepareIndex(indexName, typeName).setSource(sss, XContentType.JSON).request();
        bulkRequest.add(request);
        writenum.incrementAndGet();
    }

    //关闭存储线程
    public void release() {
        if (loadthread != null)
            loadthread.setStop(true);//这个地方，是不是有问题，线程的可见性？？？
        try {
            if (client != null) {
                if (writenum.get() > 0) {
                    writenum.set(0);
                    synchronized (this) {
                        if (bulkRequest != null)
                            bulkRequest.execute();
                    }
                }
            }
        } catch (Exception e) {
        } finally {
            if (client != null) {
                NewESClientFactory.me().closeClient(client);
                client = null;
            }
        }
    }

}
