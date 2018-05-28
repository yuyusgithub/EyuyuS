package client;

import com.google.gson.Gson;
import db.client.NewESClient;
import db.client.NewESClientFactory;
import db.model.Student;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by 彦祖 .
 */
public class IndexAPI {

    private String indexName = "students";

    private String typeName = "student";

    @Before
    public void setUp() throws Exception {
        long time1 = System.currentTimeMillis();
        System.out.println("time1为" + time1);
        NewESClientFactory.me().start("elasticsearch", "127.0.0.1:9300");

    }

    /**
     * 添加一个对象
     * 返回的数据可能为
     * {
     "_shards": {
     "total": 2, 可用的主分片，和备份分片
     "failed": 0,
     "successful": 2   表示索引操作成功的分片副本的数量。
     },
     "_index": "twitter",
     "_type": "tweet",
     "_id": "1",
     "_version": 1,在指定 version 参数时进行 optimistic concurrency control  ( 乐观并发控制 )。这将控制要对其执行操作的文档的版本。一个用于版本控制的用例的好例子是 performing a transactional read-then-update ( 执行事务读取然后更新 ) 。从初始读取的文档指定版本可以确保在此期间没有发生更改
     "created": true,
     "result": "created"
     }
     * @throws Exception
     */
    @Test
    public void addOne() throws Exception{
        //获取对象数据
        String json = CommonMethod.getStudentJson();
        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName,typeName);
        //给该builder设置一些参数
        requestBuilder.setSource(json, XContentType.JSON);
        requestBuilder.setTimeout("1m");
        //使用builder 发送请求 ，获取response 响应
        IndexResponse response = requestBuilder.get();
        //通过响应获取一些操作
        String result = response.toString();
        System.out.println(result);
    }

    /**
     * 添加一个对象，
     * @throws Exception
     */
    @Test
    public void bulkAdd(){
        //获取对象数据
        String json = CommonMethod.getStudentJson();
        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName,typeName);
        //给该builder设置一些参数
        requestBuilder.setSource(json, XContentType.JSON);
        requestBuilder.setTimeout("1m");
        //使用builder 发送请求 ，获取response 响应

        //使用builder 获取多个index请求
        IndexRequest request1 = requestBuilder.request();
        IndexRequest request2 = requestBuilder.request();

        //创建批量请求bulk   Builder
        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

        //将多个请求放置入bulk 批量请求内等待同意下发
        bulkRequestBuilder.add(request1);
        bulkRequestBuilder.add(request2);
        //bulk请求下发；
        BulkResponse responses =  bulkRequestBuilder.get();
        if (responses.hasFailures()) {
           String failMessage = responses.buildFailureMessage();
            System.out.println("如果有错误信息的话：错误信息为1："+failMessage);
        }else{
            String failMessage = responses.buildFailureMessage();
            System.out.println("如果有错误信息的话：错误信息为2："+failMessage);
        }
    }


    @Test
    public void addOneAndRouting() throws Exception{
        //获取对象数据
        String json = CommonMethod.getStudentJson();
        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName,typeName);
        //给该builder设置一些参数
        requestBuilder.setSource(json, XContentType.JSON);
        requestBuilder.setTimeout("1m");
        //*************这里可以设置一个字符串，es会通过这个字段计算哈希值来确定，将数据存放至哪个分片（如果没有这只的话，这里会默认使用ID计算哈希值）***************//
        requestBuilder.setRouting("routingKey");
        //使用builder 发送请求 ，获取response 响应
        IndexResponse response = requestBuilder.get();
        //通过响应获取一些操作
        String result = response.toString();
        System.out.println(result);
    }

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


}
