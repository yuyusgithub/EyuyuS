package db.jni;

import com.fasterxml.jackson.databind.ObjectMapper;
import db.client.NewESClientFactory;
import db.jni.condition.AggCon;
import db.jni.condition.Operation;
import db.jni.condition.SearchCon;
import db.jni.result.AggResult;
import db.jni.result.MultiTrendResult;
import db.jni.result.TrendResult;
import db.jni.store.NewStoreFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.action.admin.indices.get.GetIndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.terms.InternalTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JNIClient {

    private static Logger log = Logger.getLogger(JNIClient.class);

    private static ObjectMapper mapper = new ObjectMapper();

    private static NewStoreFactory store = new NewStoreFactory();

    /**
     * 保存数据
     *
     * @param indexName
     * @param typeName
     * @param event
     */
    public static void save(String indexName, String typeName, Object... event) throws Exception {
        if (indexName == null || typeName == null || event == null)
            return;
        store.save(indexName, typeName, event);
    }



    /**
     * 删除索引
     * 根据索引名称，删除索引数据
     * @param indexName
     * @throws Exception
     */
    public static void delete(String... indexName) throws Exception {
        if (indexName == null)
            return;
        NewESClientFactory.me().getDefaultDelegateClient().admin().indices().prepareDelete(indexName).execute().actionGet();
    }

    /**
     * 查询所有索引
     *
     * @return
     * @throws Exception
     */
    public static String[] showIndex() throws Exception {
        GetIndexResponse response = NewESClientFactory.me().getDefaultDelegateClient().admin().indices().prepareGetIndex().get();
        return response.indices();
    }

    /**
     * 根据查询条件，查询数据数量
     *
     * @param con
     * @return
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    public static long count(SearchCon con) throws Exception {
        long result = 0;
        if (con == null) {
            return result;
        }
        String[] indexes = con.getIndexName();
        String type = con.getTypeName();
        QueryBuilder queryBuilder = getQueryBuilder(con);

        SearchRequestBuilder searchRequestBuilder = NewESClientFactory.me().getReadOnlyDelegateClient().getClient().prepareSearch(indexes);
        if (StringUtils.isNotEmpty(type)) {
            searchRequestBuilder.setTypes(type);
        }
        searchRequestBuilder.setPostFilter(queryBuilder);
        searchRequestBuilder.setSearchType(SearchType.DEFAULT);
        searchRequestBuilder.setSize(0);
        SearchResponse searchResponse = searchRequestBuilder.get();

		result = searchResponse.getHits().totalHits;
        return result;
    }


    public static List<AggResult> group(AggCon aggCon) throws Exception {
        if (aggCon == null)
            return null;
        SearchRequestBuilder builder = getSearchRequestBuilder(aggCon);
        if (builder == null)
            return null;
        String[] aggFields = aggCon.getAggField();
        if (aggFields == null)
            return null;
        int length = aggFields.length;
        if (length < 1)
            return null;
        List<AggResult> result = new ArrayList<>();
        try {
            TermsAggregationBuilder aggBuilder = AggregationBuilders.terms(aggFields[0]).field(aggFields[0]);
            for (int i = 1; i < length; i++) {
                aggBuilder = aggBuilder.subAggregation(AggregationBuilders.terms(aggFields[i]).field(aggFields[i]));
            }
//            builder.addAggregation(aggBuilder.order(BucketOrder.count(false)).size(aggCon.getLimit()));
            builder.addAggregation(aggBuilder);
            SearchResponse sr = builder.execute().actionGet();
            List<Terms.Bucket> bucketList = ((InternalTerms) sr.getAggregations().get(aggFields[0])).getBuckets();
            addAggResult(bucketList, result, new AggResult());
        } catch (Exception e) {
            log.error("", e);
        }

        return result;
    }

    /**
     * 查询
     *
     * @param clazz
     * @param con
     * @return
     * @throws Exception
     */
    public static <T> List<T> search(Class<T> clazz, SearchCon con) throws Exception {
        if (con == null)
            return null;
        SearchRequestBuilder builder = getSearchRequestBuilder(con);
        if (builder == null)
            return null;
        List<T> result = new ArrayList<>();
        try {
            SearchResponse response = builder.execute().actionGet();
            SearchHits searchHits = response.getHits();
            SearchHit[] hits = searchHits.getHits();
            for (int i = 0; i < hits.length; i++) {
                SearchHit hit = hits[i];
                String json = hit.getSourceAsString();
                T io = mapper.readValue(json, clazz);
                result.add(io);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return result;
    }

    /**
     * 查询趋势数据
     *
     * @param con
     * @param field
     * @param interval
     * @return
     * @throws Exception
     */
    public static List<TrendResult> searchTrend(SearchCon con, String field, long interval) throws Exception {
        SearchRequestBuilder builder = getSearchRequestBuilder(con);
        if (builder == null)
            return null;
        List<TrendResult> result = new ArrayList<>();
        try {
            HistogramAggregationBuilder dhb = AggregationBuilders.histogram("trend")
                    .field(field)
                    .interval(interval)
                    .minDocCount(0);
            builder.addAggregation(dhb);
            Histogram ih = builder.execute().actionGet().getAggregations().get("trend");
            List bucketList = ih.getBuckets();
            if (bucketList != null && bucketList.size() > 0) {
                for (Object obj : bucketList) {
                    InternalHistogram.Bucket bucket = (InternalHistogram.Bucket) obj;
                    TrendResult tr = new TrendResult();
                    tr.setTime(Long.parseLong(bucket.getKeyAsString()));
                    tr.setValue(bucket.getDocCount());
                    result.add(tr);
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
        return result;
    }

    /**
     * 查询趋势数据，严格按照时间返回数据
     *
     * @param con
     * @param field
     * @param startTime
     * @param endTime
     * @param interval
     * @return
     * @throws Exception
     */
    public static List<TrendResult> searchTrend(SearchCon con, String field, long startTime, long endTime, long interval) throws Exception {
        SearchRequestBuilder builder = getSearchRequestBuilder(con);
        if (builder == null)
            return null;
        List<TrendResult> result = new ArrayList<>();
        try {
            DateHistogramAggregationBuilder dhb = AggregationBuilders.dateHistogram("trend")
                    .field(field)
                    .interval(interval)
                    .extendedBounds(new ExtendedBounds(startTime, endTime))
                    .minDocCount(0);
            builder.addAggregation(dhb);
            InternalHistogram ih = builder.execute().actionGet().getAggregations().get("trend");
            List bucketList = ih.getBuckets();
            if (bucketList != null && bucketList.size() > 0) {
                for (Object obj : bucketList) {
                    InternalHistogram.Bucket bucket = (InternalHistogram.Bucket) obj;
                    TrendResult tr = new TrendResult();
                    tr.setTime(Long.parseLong(bucket.getKeyAsString()));
                    tr.setValue(bucket.getDocCount());
                    result.add(tr);
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
        return result;
    }

    /**
     * 查询趋势数据做sum操作
     *
     * @param con
     * @param field
     * @param interval
     * @param sumField
     * @return
     * @throws Exception
     */
    public static List<MultiTrendResult> searchTrend(SearchCon con, String field, long interval, String... sumField) throws Exception {
        SearchRequestBuilder builder = getSearchRequestBuilder(con);
        if (builder == null)
            return null;
        List<MultiTrendResult> result = new ArrayList<>();
        try {
            DateHistogramAggregationBuilder dhb = AggregationBuilders.dateHistogram("trend")
                    .field(field)
                    .interval(interval)
                    .minDocCount(0);
            if (sumField != null && sumField.length > 0) {
                for (String sf : sumField)
                    dhb.subAggregation(AggregationBuilders.sum(sf).field(sf));
            } else {
                return result;
            }
            builder.addAggregation(dhb);
            InternalHistogram ih = builder.execute().actionGet().getAggregations().get("trend");
            List bucketList = ih.getBuckets();
            if (bucketList != null && bucketList.size() > 0) {
                int size = sumField.length;
                for (Object obj : bucketList) {
                    InternalHistogram.Bucket bucket = (InternalHistogram.Bucket) obj;
                    MultiTrendResult tr = new MultiTrendResult();
                    tr.setTime(Long.parseLong(bucket.getKeyAsString()));
                    Aggregations aggs = bucket.getAggregations();
                    double[] r = new double[size];
                    for (int i = 0; i < size; i++) {
                        InternalSum sum = (InternalSum) aggs.getAsMap().get(sumField[i]);
                        r[i] = sum.getValue();

                    }
                    tr.setValue(r);
                    result.add(tr);
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
        return result;
    }

    /**
     * 查询趋势数据做sum操作，严格按照时间返回数据
     *
     * @param con
     * @param field
     * @param startTime
     * @param endTime
     * @param interval
     * @param sumField
     * @return
     * @throws Exception
     */
    public static List<MultiTrendResult> searchTrend(SearchCon con, String field, long startTime, long endTime, long interval, String... sumField) throws Exception {
        SearchRequestBuilder builder = getSearchRequestBuilder(con);
        if (builder == null)
            return null;
        List<MultiTrendResult> result = new ArrayList<>();
        try {
            DateHistogramAggregationBuilder dhb = AggregationBuilders.dateHistogram("trend")
                    .field(field)
                    .interval(interval)
                    .extendedBounds(new ExtendedBounds(startTime, endTime))
                    .minDocCount(0);
            if (sumField != null && sumField.length > 0) {
                for (String sf : sumField)
                    dhb.subAggregation(AggregationBuilders.sum(sf).field(sf));
            } else {
                return result;
            }
            builder.addAggregation(dhb);
            InternalDateHistogram ih = builder.execute().actionGet().getAggregations().get("trend");
            List bucketList = ih.getBuckets();
            if (bucketList != null && bucketList.size() > 0) {
                int size = sumField.length;
                for (Object obj : bucketList) {
                    InternalDateHistogram.Bucket bucket = (InternalDateHistogram.Bucket) obj;
                    MultiTrendResult tr = new MultiTrendResult();
                    tr.setTime(Long.parseLong(bucket.getKeyAsString()));
                    Aggregations aggs = bucket.getAggregations();
                    double[] r = new double[size];
                    for (int i = 0; i < size; i++) {
                        InternalSum sum = (InternalSum) aggs.getAsMap().get(sumField[i]);
                        r[i] = sum.getValue();

                    }
                    tr.setValue(r);
                    result.add(tr);
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
        return result;
    }

    /**
     * 分组统计
     *
     * @return
     * @throws Exception
     */




    private static void addAggResult(List<Terms.Bucket> bucketList, List<AggResult> result, AggResult ar) {
        if (bucketList != null && bucketList.size() > 0) {
            LinkedList<Object> temp = new LinkedList<Object>();
            temp.addAll(ar.getAggList());
            for (Terms.Bucket bucket : bucketList) {
                Map<String, Aggregation> map = bucket.getAggregations().asMap();
                LinkedList<Object> t1 = new LinkedList<Object>();
                t1.addAll(temp);
                t1.add(new Object[]{bucket.getKeyAsString()});
                if (map.size() > 0) {
                    Aggregation agg = map.get(map.keySet().iterator().next());
                    List<Terms.Bucket> list = ((InternalTerms) agg).getBuckets();
                    AggResult art = new AggResult();
                    art.getAggList().addAll(t1);
                    addAggResult(list, result, art);
                } else {
                    ar = new AggResult();
                    ar.getAggList().addAll(t1);
                    ar.setCount(bucket.getDocCount());
                    result.add(ar);
                }
            }
        }
    }

    /**
     * 根据查询条件，构建SearchRequestBuilder
     * @param con
     * @return
     */
    private static SearchRequestBuilder getSearchRequestBuilder(SearchCon con) {
        if (con == null)
            return null;

        String[] indexName = con.getIndexName();
        String typeName = con.getTypeName();
        if (indexName == null)
            return null;
        QueryBuilder queryBuilder = getQueryBuilder(con);
        SearchRequestBuilder builder = NewESClientFactory.me().getReadOnlyDelegateClient().prepareSearch(indexName);
        if (typeName != null)
            builder.setTypes(typeName);
        builder.setSearchType(SearchType.QUERY_THEN_FETCH);
        builder.setPostFilter(queryBuilder);
        if (con.getCursor() != null && con.getLimit() != null){
            builder.setFrom(con.getCursor()).setSize(con.getLimit());
        }
        String sortField = con.getSortField();
        boolean asc = con.isAsc();
        if (sortField != null)
            builder.addSort(sortField, asc ? SortOrder.ASC : SortOrder.DESC);
        return builder;
    }


    /**
     * 构建 QueryBuilder
     * @param con
     * @return
     */
    private static QueryBuilder getQueryBuilder(SearchCon con) {
        //组装查询条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        Operation[] opers = con.getOpers();
        if (opers != null && opers.length > 0) {
            for (Operation oper : opers) {
                switch (oper.getOper()) {
                    //小于
                    case LT:
                        queryBuilder.must(QueryBuilders.rangeQuery(oper.getField()).lt(oper.getValue()));
                        break;
                    //大于
                    case GT:
                        queryBuilder.must(QueryBuilders.rangeQuery(oper.getField()).gt(oper.getValue()));
                        break;
                    //小于等于
                    case LTE:
                        queryBuilder.must(QueryBuilders.rangeQuery(oper.getField()).lte(oper.getValue()));
                        break;
                    //大于等于
                    case GTE:
                        queryBuilder.must(QueryBuilders.rangeQuery(oper.getField()).gte(oper.getValue()));
                        break;
                    //等于
                    case EQ:
                        queryBuilder.must(QueryBuilders.termQuery(oper.getField(), oper.getValue()));
                        break;
                    //不等于
                    case NEQ:
                        queryBuilder.mustNot(QueryBuilders.termQuery(oper.getField(), oper.getValue()));
                        break;
                    //范围，大于等于oper.getStart() && 小于oper.getEnd()
                    case BETWEEN:
                        queryBuilder.must(QueryBuilders.rangeQuery(oper.getField()).gte(oper.getStart()).lt(oper.getEnd()));
                        break;
                    //前缀，类似sql的like
                    case PREFIX:
                        queryBuilder.must(QueryBuilders.prefixQuery(oper.getField(), oper.getValue().toString()));
                        break;
                    //全属性模糊查询
                    case MATCHALL:
                        queryBuilder.must(QueryBuilders.matchQuery("_all", oper.getValue()));
                        break;
                    //类似于OR操作
                    case SHOULD:
                        queryBuilder.should(QueryBuilders.termQuery(oper.getField(), oper.getValue()));
                        break;
                    default:
                        break;
                }
            }
        }
        return queryBuilder;
    }



    public static void main(String[] args) {
        NewESClientFactory.me().start("elasticsearch", "127.0.0.1:9300");
        System.out.println(111);
    }


}
