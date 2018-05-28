package searchApi;

import db.client.NewESClientFactory;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

/**
 * Created by 彦祖 .
 */
public class PostFilterApi {

    private String indexName = "students";

    private String typeName = "student";


    /**
     * 先说说这个postfilter是干什么用的！
     * 在一次请求中可以存在一些query条件；
     * 也可以存在一些，aggregation 聚合条件；
     * 聚合是在query条件的基础上进行的；
     *
     * 如果，我想在query中取出红色，那也同时会影响到aggregation的聚合操作，如果聚合操作是对颜色分组，数据就会有问题；
     * 可是如果不影响颜色分组，hit命中的数据中，又无法去除红色
     *
     * 这个时候就用到了，post filter
     * 他会在aggregation之后，再对hit中的命中数据进行过滤；
     *
     * 在已经计算了聚合之后，post_filter 应用于搜索请求的最后的搜索命中。
     */
    private void readMe(){

    }



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
        queryBuilder.filter(QueryBuilders.termQuery("s2", 2));
        SearchResponse searchResponse = searchRequestBuilder.setQuery(queryBuilder).get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            System.out.println(json);
        }
    }
}
