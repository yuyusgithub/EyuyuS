package searchApi;

import db.client.NewESClient;
import db.client.NewESClientFactory;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
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
public class RequestBodySearchApi {

    private String indexName = "students";

    private String typeName = "student";

    @Before
    public void setUp() throws Exception {
        long time1 = System.currentTimeMillis();
        System.out.println("time1为" + time1);
        NewESClientFactory.me().start("elasticsearch", "127.0.0.1:9300");
    }


    /**
     * 最基础的Query，From ,To,Size,Sort
     */
    @Test
    public void query(){
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        //Size
        searchRequestBuilder.setSize(10);
        //From
        searchRequestBuilder.setFrom(0);
        //Sort
        searchRequestBuilder.addSort("s1", SortOrder.ASC);

        //Query
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("s2", 2));

        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            System.out.println(json);
        }
    }

    /**
     * 查询过滤   Source filtering
     */
    @Test
    public void getID1FilterSource(){
        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        GetRequestBuilder requestBuilder = client.prepareGet(indexName,typeName,"1");
        //******禁止source*********//
        requestBuilder.setFetchSource("*","*2");
        GetResponse response = requestBuilder.get();
        String result = response.getSourceAsString();
        System.out.println(result);
    }




}
