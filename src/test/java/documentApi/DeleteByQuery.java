package documentApi;

import db.client.NewESClient;
import db.client.NewESClientFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by 彦祖 .
 */
public class DeleteByQuery {


    private String indexName = "students";

    private String typeName = "student";

    @Before
    public void setUp() throws Exception {
        long time1 = System.currentTimeMillis();
        System.out.println("time1为" + time1);
        NewESClientFactory.me().start("elasticsearch", "127.0.0.1:9300");
    }


    /**
     * 跟并发有关，还没看完
     *
     */
    @Test
    public  void deleteByQuery(){

        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        BulkByScrollResponse response = DeleteByQueryAction.INSTANCE.newRequestBuilder(client.getClient())
                .filter(QueryBuilders.matchQuery("s1", "1"))
                .source(indexName)
                .get();
        long deleted = response.getDeleted();
        System.out.println(deleted);
    }
}
