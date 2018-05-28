package searchApi;

import com.google.gson.Gson;
import db.client.NewESClientFactory;
import db.model.Student;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by 彦祖 .
 */
public class SearchAPi {


    private String indexName = "students";

    private String typeName = "student";

    @Before
    public void setUp() throws Exception {
        long time1 = System.currentTimeMillis();
        System.out.println("time1为" + time1);
        NewESClientFactory.me().start("elasticsearch", "127.0.0.1:9300");
    }


    /**
     * 可通过这个方法查询出详细信息，当然，也可通过制定source属性来确定查询哪些数据
     * 默认情况下，elasticsearch 拒绝将查询超过 1000 个分片的搜索请求。
     * 原因是这样大量的分片使协调节点的工作非常耗费 CPU 和内存。
     * 可以将 action.search.shard_count.limit 集群设置更新为更大的值。
     * @throws Exception
     */
    @Test
    public void getPageList() throws Exception {

        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setFrom(0);
        searchRequestBuilder.setSize(10);
        searchRequestBuilder.addSort("s1", SortOrder.ASC);

        //配置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("s2", 2));

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            System.out.println(json);
        }
    }
}
