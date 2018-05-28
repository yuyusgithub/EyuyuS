package documentApi;

import db.client.NewESClient;
import db.client.NewESClientFactory;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by 彦祖 .
 */
public class UpdateAPI {

    private String indexName = "students";

    private String typeName = "student";

    @Before
    public void setUp() throws Exception {
        long time1 = System.currentTimeMillis();
        System.out.println("time1为" + time1);
        NewESClientFactory.me().start("elasticsearch", "127.0.0.1:9300");
    }

    /**
     * 这个没怎么弄明白，回头再弄
     */
    @Test
    public void updateID1(){
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

        UpdateRequestBuilder updateRequestBuilder =  client.prepareUpdate(indexName,typeName,"1");
        UpdateRequest updateRequest = updateRequestBuilder.request();
        updateRequest.doc(request1);
        client.update(updateRequest);
    }
}
