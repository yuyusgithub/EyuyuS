package searchApi;

import db.client.NewESClient;
import db.client.NewESClientFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by 彦祖 .
 */
public class CountApi {

    private String indexName = "students";

    private String typeName = "student";

    @Before
    public void setUp() throws Exception {
        long time1 = System.currentTimeMillis();
        System.out.println("time1为" + time1);
        NewESClientFactory.me().start("elasticsearch", "127.0.0.1:9300");
    }

    @Test
    public void getCount() throws Exception {

        //配置查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("name", "student1"));
        queryBuilder.must(QueryBuilders.termQuery("s2", 1));

        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setQuery(queryBuilder);
        //************这里比较重要，将size设置成0，这样不会反悔hit命中数据，只会返回一个数字**************//
        searchRequestBuilder.setSize(0);

        SearchResponse searchResponse = searchRequestBuilder.get();
        long result = searchResponse.getHits().totalHits;
        System.out.println(result);
    }



}
