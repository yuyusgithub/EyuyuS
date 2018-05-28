package client;

import db.client.NewESClient;
import db.client.NewESClientFactory;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by 彦祖 .
 */
public class GetAPI {

    private String indexName = "students";

    private String typeName = "student";

    @Before
    public void setUp() throws Exception {
        long time1 = System.currentTimeMillis();
        System.out.println("time1为" + time1);
        NewESClientFactory.me().start("elasticsearch", "127.0.0.1:9300");
    }

    /**
     * 测试工具方法，用于生成一个id为1的数据
     * @throws Exception
     */
    @Test
    public void addOneIdIs1() throws Exception{
        //获取对象数据
        String json = CommonMethod.getStudentJson();
        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        IndexRequestBuilder requestBuilder = client.prepareIndex(indexName,typeName);
        //给该builder设置一些参数
        requestBuilder.setSource(json, XContentType.JSON);
        requestBuilder.setTimeout("1m");
        requestBuilder.setId("1");
        //使用builder 发送请求 ，获取response 响应
        IndexResponse response = requestBuilder.get();
        //通过响应获取一些操作
        String result = response.toString();
        System.out.println(result);
    }

    /**
     * 返回ID为1的数据的所有信息
     * 返回信息可能为：  _source 中包含了所有的信息，其实这是不合理的
     * {
         "_index" : "twitter",
         "_type" : "tweet",
         "_id" : "1",
         "_version" : 1,
         "found": true,
         "_source" : {
             "user" : "kimchy",
             "postDate" : "2009-11-15T14:12:12",
             "message" : "trying out Elasticsearch"
         }
     }
     */
    @Test
    public void getID1(){
        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        GetRequestBuilder requestBuilder = client.prepareGet(indexName,typeName,"1");
        GetResponse response = requestBuilder.get();
        String result = response.getSourceAsString();
        System.out.println(result);
    }

    /**
     * 默认来说，get查请求都是实时的，即使数据还买有flash，get请求也会强行flash，可以通过下面****禁用实时；
     */
    @Test
    public void getID1RealTime(){
        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        GetRequestBuilder requestBuilder = client.prepareGet(indexName,typeName,"1");
        //**************//
        requestBuilder.setRealtime(false);
        GetResponse response = requestBuilder.get();
        String result = response.getSourceAsString();
        System.out.println(result);
    }

    /**
     * 禁止source
     */
    @Test
    public void getID1ForbidSource(){
        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        GetRequestBuilder requestBuilder = client.prepareGet(indexName,typeName,"1");
        //******禁止source*********//
        requestBuilder.setFetchSource(false);
        GetResponse response = requestBuilder.get();
        String result = response.getSourceAsString();
        System.out.println(result);
    }

    /**
     * 过滤source字段，只查询自己感兴趣的数据
     */
    @Test
    public void getID1FilterSource(){
        //获取客户端
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        GetRequestBuilder requestBuilder = client.prepareGet(indexName,typeName,"1");
        //******禁止source*********//
        requestBuilder.setFetchSource("*","s2");
        GetResponse response = requestBuilder.get();
        String result = response.getSourceAsString();
        System.out.println(result);
    }





}
