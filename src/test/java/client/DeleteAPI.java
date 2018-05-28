package client;

import db.client.NewESClient;
import db.client.NewESClientFactory;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by 彦祖 .
 */
public class DeleteAPI {

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
        requestBuilder.setId("1");
        //使用builder 发送请求 ，获取response 响应
        IndexResponse response = requestBuilder.get();
        //通过响应获取一些操作
        String result = response.toString();
        System.out.println(result);
    }

    @Test
    public void deleteByID1(){
        NewESClient client = NewESClientFactory.me().getReadOnlyDelegateClient();
        //通过索引和类型获取  index请求builder
        DeleteRequestBuilder requestBuilder = client.prepareDelete();
        requestBuilder.setIndex(indexName);
        requestBuilder.setType(typeName);
        requestBuilder.setId("1");
        DeleteResponse deleteResponse =  requestBuilder.get();
        String result = deleteResponse.toString();
        System.out.println(result);
    }


}
