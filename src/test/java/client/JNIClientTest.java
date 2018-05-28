package client;

import com.google.gson.Gson;
import com.rstone.db.client.NewESClientFactory;
import com.rstone.db.jni.JNIClient;
import com.rstone.db.jni.condition.Operation;
import com.rstone.db.jni.condition.SearchCon;
import com.rstone.db.model.Student;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.AvgAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.avg.InternalAvg;
import org.elasticsearch.search.aggregations.metrics.max.Max;
import org.elasticsearch.search.aggregations.metrics.max.MaxAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class JNIClientTest {

    private String indexName = "students";

    private String typeName = "student";

    @Before
    public void setUp() throws Exception {
        long time1 = System.currentTimeMillis();
        System.out.println("time1为" + time1);
        NewESClientFactory.me().start("elasticsearch", "127.0.0.1:9300");

    }


    //        SearchCon con = new SearchCon();
//        con.setIndexName(indexName);
//        con.setTypeName(typeName);
//        con.setOpers();
//        long count = JNIClient.count(con);
//        System.out.println(count);


    /**
     * 根据查询条件 获取符合数据的条目数
      * @throws Exception
     */
    @Test
    public void getCount() throws Exception {

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("name", "student1"));
        queryBuilder.must(QueryBuilders.termQuery("s2", 1001));

        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setPostFilter(queryBuilder);
        searchRequestBuilder.setSize(0);
        searchRequestBuilder.addSort("s1", SortOrder.ASC);

        SearchResponse searchResponse = searchRequestBuilder.get();
        long result = searchResponse.getHits().totalHits;
        System.out.println(result);
    }

    /**
     * 总条目数，没有查询条件
     * @throws Exception
     */
    @Test
    public void getTotalCount() throws Exception {
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setSize(0);
        searchRequestBuilder.addSort("s1", SortOrder.ASC);

        SearchResponse searchResponse = searchRequestBuilder.get();
        long result = searchResponse.getHits().totalHits;
        System.out.println(result);
    }

    /**
     * 分页查询
     * @throws Exception
     */
    @Test
    public void getPageList() throws Exception {
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setFrom(0);
        searchRequestBuilder.setSize(10);
        searchRequestBuilder.addSort("s1", SortOrder.ASC);

        SearchResponse searchResponse = searchRequestBuilder.get();
        SearchHits hits = searchResponse.getHits();
        for (SearchHit hit : hits) {
            String json = hit.getSourceAsString();
            Gson gson = new Gson();
            Student sd = gson.fromJson(json, Student.class);
            System.out.println(sd.getName());
        }
    }

    /**
     * 获取某一字段的最大值，最小值，平均值
     * @throws Exception
     */
    @Test
    public void aggSumMaxMin() throws Exception {
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        if (typeName != null) {
            searchRequestBuilder.setTypes(typeName);
        }
        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);
        MaxAggregationBuilder aggBuilder = AggregationBuilders.max("s1").field("s1");
        searchRequestBuilder.addAggregation(aggBuilder).setSize(0);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        Max agg = searchResponse.getAggregations().get("s1");
        double s = agg.getValue();
        System.out.println(s);
    }

    /**
     * 根据某一个字段进行分组，并获取每组的条目数
     * @throws Exception
     */
    @Test
    public void getAggCount() throws Exception {
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setSize(0);

        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("s1").field("s1");
        termsAggregationBuilder.order(BucketOrder.key(false));
//        termsAggregationBuilder.order(BucketOrder.count(false));
        termsAggregationBuilder.size(20);
        searchRequestBuilder.addAggregation(termsAggregationBuilder);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        InternalTerms internalTerms = searchResponse.getAggregations().get("s1");
        List<Terms.Bucket> list = internalTerms.getBuckets();
        for (Terms.Bucket bucket : list){
            String key = bucket.getKeyAsString();
            long count = bucket.getDocCount();
            System.out.println("key:"+key);
            System.out.println("count:"+count);
        }
    }


    @Test
    public void getMoreAggCount() throws Exception {
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setSize(0);

        TermsAggregationBuilder termsAggregationBuilderS1 = AggregationBuilders.terms("s1").field("s1").order(BucketOrder.aggregation("avg",false));
        AvgAggregationBuilder termsAggregationBuilderS3 = AggregationBuilders.avg("avg").field("s1");
        termsAggregationBuilderS1.subAggregation(termsAggregationBuilderS3);
        searchRequestBuilder.addAggregation(termsAggregationBuilderS1);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        Terms terms = searchResponse.getAggregations().get("s1");
        for (Terms.Bucket bucket : terms.getBuckets()){
            Object s1 = bucket.getKey();
            Long s1Count = bucket.getDocCount();
            InternalAvg internalAvg = bucket.getAggregations().get("avg");
            internalAvg.getValue();
            System.out.println("s1:"+s1);
            System.out.println("s1Count:"+s1Count);
            System.out.println("avg::"+internalAvg.getValue());
        }
    }

    @Test
    public void getHistogramCount() throws Exception{
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setSize(0);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.rangeQuery("s1").gte(500));

        AggregationBuilder aggregation = AggregationBuilders.histogram("s1_count").field("s1").interval(100);
        searchRequestBuilder.setQuery(queryBuilder);
        searchRequestBuilder.addAggregation(aggregation);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        Histogram agg = searchResponse.getAggregations().get("s1_count");
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Number key = (Number) entry.getKey();   // Key
            long docCount = entry.getDocCount();    // Doc count
            System.out.println("key:"+key+",  doc_count:"+docCount);
        }
    }

    @Test
    public void getHistogramMerce() throws Exception{
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
//        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setSize(0);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.rangeQuery("s1").gte(500));

        AggregationBuilder aggregation = AggregationBuilders.histogram("s1_count").field("s1").interval(100);
        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("s1_avg").field("s1");
        aggregation.subAggregation(avgAggregationBuilder);
        searchRequestBuilder.setQuery(queryBuilder);
        searchRequestBuilder.addAggregation(aggregation);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        Histogram agg = searchResponse.getAggregations().get("s1_count");
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Number key = (Number) entry.getKey();   // Key
            long docCount = entry.getDocCount();    // Doc count
            InternalAvg aa = entry.getAggregations().get("s1_avg");
            double dd = aa.getValue();
            System.out.println("dd@@@@@@@@@@@@@:"+dd);
            System.out.println("key:"+key+",  doc_count:"+docCount);
        }
    }

    @Test
    public void getHistogramMerce111() throws Exception{
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch("20180512_artsd");
//        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setSize(0);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.rangeQuery("appeart_time").gte(152600945148L));

        AggregationBuilder aggregation = AggregationBuilders.histogram("appeart_time_tag").field("appeart_time").interval(10000);

        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("lost_pkt_rate_tag").field("lost_pkt_rate");

        TermsAggregationBuilder businessIdAggregationBuilder = AggregationBuilders.terms("business_id_tag").field("business_id").size(200);
        businessIdAggregationBuilder.subAggregation(avgAggregationBuilder);

        aggregation.subAggregation(businessIdAggregationBuilder);
        searchRequestBuilder.setQuery(queryBuilder);
        searchRequestBuilder.addAggregation(aggregation);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        Histogram agg = searchResponse.getAggregations().get("appeart_time_tag");
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Number time_key = (Number) entry.getKey();   // Key
            long time = time_key.longValue();
            System.out.println("时间为："+time);
            Terms  businessAgg = entry.getAggregations().get("business_id_tag");
            for(Terms.Bucket entry1 :businessAgg.getBuckets()){
                Number business_key = (Number) entry1.getKey();
                System.out.println("business_Id:"+business_key);
                InternalAvg aa = entry1.getAggregations().get("lost_pkt_rate_tag");
                double dd = aa.getValue();
                System.out.println("ID:"+business_key.longValue()+"  的平均丢包率："+dd);
            }
        }
    }

    @Test
    public void getHistogramMerce222() throws Exception{
        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch("20180514_artsd");
//        searchRequestBuilder.setTypes(typeName);
        searchRequestBuilder.setSize(0);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.rangeQuery("appeart_time").gte(152600945148L));

        AggregationBuilder aggregation = AggregationBuilders.histogram("appeart_time_tag").field("appeart_time").interval(10000);

        AvgAggregationBuilder avgAggregationBuilder1 = AggregationBuilders.avg("resp_time_avg_tag").field("resp_time_avg");
        AvgAggregationBuilder avgAggregationBuilder2 = AggregationBuilders.avg("conn_time_avg_tag").field("conn_time_avg");
        AvgAggregationBuilder avgAggregationBuilder3 = AggregationBuilders.avg("serv_resp_time_avg_tag").field("serv_resp_time_avg");
        AvgAggregationBuilder avgAggregationBuilder4 = AggregationBuilders.avg("data_trans_time_avg_tag").field("data_trans_time_avg");
        AvgAggregationBuilder avgAggregationBuilder5 = AggregationBuilders.avg("retra_time_avg_tag").field("retra_time_avg");


        aggregation.subAggregation(avgAggregationBuilder1);
        aggregation.subAggregation(avgAggregationBuilder2);
        aggregation.subAggregation(avgAggregationBuilder3);
        aggregation.subAggregation(avgAggregationBuilder4);
        aggregation.subAggregation(avgAggregationBuilder5);
        searchRequestBuilder.setQuery(queryBuilder);
        searchRequestBuilder.addAggregation(aggregation);
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        Histogram agg = searchResponse.getAggregations().get("appeart_time_tag");
        for (Histogram.Bucket entry : agg.getBuckets()) {
            Number time_key = (Number) entry.getKey();   // Key
            long time = time_key.longValue();
            System.out.println("时间为："+time);
            Terms  businessAgg = entry.getAggregations().get("business_id_tag");
            for(Terms.Bucket entry1 :businessAgg.getBuckets()){
                Number business_key = (Number) entry1.getKey();
                System.out.println("business_Id:"+business_key);
                InternalAvg aa = entry1.getAggregations().get("lost_pkt_rate_tag");
                double dd = aa.getValue();
                System.out.println("ID:"+business_key.longValue()+"  的平均丢包率："+dd);
            }
        }
    }

    private boolean  idNaN(double d){
        return (d != d);
    }

//1.52600942E12

//    @Test
//    public void aggTest() throws Exception {
//        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
//        searchRequestBuilder.setTypes(typeName);
//
//        TermsAggregationBuilder aggBuilder = AggregationBuilders.terms("s1s").field("s1").size(0);
//        searchRequestBuilder.addAggregation(aggBuilder);
//        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
////        searchResponse
//        System.out.println("");
//    }
//
//    @Test
//    public void aggCount() throws Exception {
//        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
//        if (typeName != null) {
//            searchRequestBuilder.setTypes(typeName);
//        }
//        searchRequestBuilder.setSearchType(SearchType.QUERY_THEN_FETCH);
////        TermsAggregationBuilder aggregation = AggregationBuilders.terms("agg").field("name.keyword").order(BucketOrder.count(false)).size(20);
//        TermsAggregationBuilder aggregation = AggregationBuilders.terms("agg").field("name.keyword").order(BucketOrder.count(false)).size(20);
//        searchRequestBuilder.addAggregation(aggregation).setSize(100);
//        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
//        Terms terms = searchResponse.getAggregations().get("agg");
//        for (Terms.Bucket bucket : terms.getBuckets()) {
//            String key = (String) bucket.getKey();
//            long count = bucket.getDocCount();
//            System.out.println("key:" + key);
//            System.out.println("count:" + count);
//        }
//    }
//
//
//    @Test
//    public void trendTest() throws Exception {
//        SearchCon con = new SearchCon();
//        con.setIndexName(indexName);
//        con.setTypeName(typeName);
////        Operation operation = Operation.gte("id",40);
////        Operation operation1 = Operation.lte("id",100);
////        con.setOpers(operation,operation1);
//        List<TrendResult> result = JNIClient.searchTrend(con, "id", 10);
//        Assert.assertEquals(6, result.size());
//    }
//
//    @Test
//    public void trendSumTest() throws Exception {
//        SearchCon con = new SearchCon();
//        con.setIndexName(indexName);
//        con.setTypeName(typeName);
//        List<MultiTrendResult> result = JNIClient.searchTrend(con, "id", 10, "age");
//        Assert.assertEquals(6, result.size());
//    }

        @Test
    public void initStudent() throws Exception {
        int count = 1000;
        List<Student> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Student student = new Student("student" + i, 999, 2000 + i, 20000 + i);
            list.add(student);
        }
        JNIClient.save("students", "student", list);
        Thread.sleep(60000);
    }
//
//    @Test
//    public void initStudent1() throws Exception {
//        int count = 1000;
//        List<Student> list = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            Student student = new Student("student" + i, 0, 0, 0);
//            list.add(student);
//        }
//        JNIClient.save("students", "student", list);
//        Thread.sleep(60000);
//    }


    @Test
    public void searchTest() throws Exception {
        SearchCon con = new SearchCon();
        con.setIndexName(indexName);
        con.setTypeName(typeName);
        con.setOpers(Operation.eq("s1", 3));
        List<Student> users = JNIClient.search(Student.class, con);
//        Assert.assertEquals(1L, users.get(0).getId());
        System.out.println("1:" + users.size());
    }

    @Test
    public void countTest() throws Exception {
        SearchCon con = new SearchCon();
        con.setIndexName(indexName);
        con.setTypeName(typeName);
        long count = JNIClient.count(con);
        Assert.assertEquals(11, count);
    }

    @After
    public void tearDown() throws Exception {
//        NewESClientFactory.me().stop();
    }
}
