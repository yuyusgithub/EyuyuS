package db.client;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.explain.ExplainRequest;
import org.elasticsearch.action.explain.ExplainRequestBuilder;
import org.elasticsearch.action.explain.ExplainResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.termvectors.MultiTermVectorsRequest;
import org.elasticsearch.action.termvectors.MultiTermVectorsRequestBuilder;
import org.elasticsearch.action.termvectors.MultiTermVectorsResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.threadpool.ThreadPool;

import java.util.List;
import java.util.UUID;

public class NewESClient {

    private String id;

    private TransportClient client;

    public NewESClient(TransportClient client) {
        this.client = client;
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public TransportClient getClient() {
        return client;
    }

    public TransportClient addTransportAddress(TransportAddress transportAddress) {
        return client.addTransportAddress(transportAddress);
    }

    public TransportClient addTransportAddresses(TransportAddress... transportAddress) {
        return client.addTransportAddresses(transportAddress);
    }

    public AdminClient admin() {
        return client.admin();
    }

    public void bulk(BulkRequest request, ActionListener<BulkResponse> listener) {
        client.bulk(request, listener);
    }

    public ActionFuture<BulkResponse> bulk(BulkRequest request) {
        return client.bulk(request);
    }

    public List<DiscoveryNode> connectedNodes() {
        return client.connectedNodes();
    }

    public void delete(DeleteRequest request, ActionListener<DeleteResponse> listener) {
        client.delete(request, listener);
    }

    public ActionFuture<DeleteResponse> delete(DeleteRequest request) {
        return client.delete(request);
    }

    public void explain(ExplainRequest request, ActionListener<ExplainResponse> listener) {
        client.explain(request, listener);
    }

    public ActionFuture<ExplainResponse> explain(ExplainRequest request) {
        return client.explain(request);
    }

    public List<DiscoveryNode> filteredNodes() {
        return client.filteredNodes();
    }

    public void get(GetRequest request, ActionListener<GetResponse> listener) {
        client.get(request, listener);
    }

    public ActionFuture<GetResponse> get(GetRequest request) {
        return client.get(request);
    }

    public void index(IndexRequest request, ActionListener<IndexResponse> listener) {
        client.index(request, listener);
    }

    public ActionFuture<IndexResponse> index(IndexRequest request) {
        return client.index(request);
    }

    public List<DiscoveryNode> listedNodes() {
        return client.listedNodes();
    }

    public void multiGet(MultiGetRequest request, ActionListener<MultiGetResponse> listener) {
        client.multiGet(request, listener);
    }

    public ActionFuture<MultiGetResponse> multiGet(MultiGetRequest request) {
        return client.multiGet(request);
    }

    public void multiSearch(MultiSearchRequest request, ActionListener<MultiSearchResponse> listener) {
        client.multiSearch(request, listener);
    }

    public ActionFuture<MultiSearchResponse> multiSearch(MultiSearchRequest request) {
        return client.multiSearch(request);
    }

    public void multiTermVectors(MultiTermVectorsRequest request, ActionListener<MultiTermVectorsResponse> listener) {
        client.multiTermVectors(request, listener);
    }

    public ActionFuture<MultiTermVectorsResponse> multiTermVectors(MultiTermVectorsRequest request) {
        return client.multiTermVectors(request);
    }

//    public void percolate(PercolateRequest request, ActionListener<PercolateResponse> listener) {
//        client.percolate(request, listener);
//    }
//
//    public ActionFuture<PercolateResponse> percolate(PercolateRequest request) {
//        return client.percolate(request);
//    }

    public TransportClient removeTransportAddress(TransportAddress transportAddress) {
        return client.removeTransportAddress(transportAddress);
    }

    public void search(SearchRequest request, ActionListener<SearchResponse> listener) {
        client.search(request, listener);
    }

    public ActionFuture<SearchResponse> search(SearchRequest request) {
        return client.search(request);
    }

    public void searchScroll(SearchScrollRequest request, ActionListener<SearchResponse> listener) {
        client.searchScroll(request, listener);
    }

    public ActionFuture<SearchResponse> searchScroll(SearchScrollRequest request) {
        return client.searchScroll(request);
    }

    public Settings settings() {
        return client.settings();
    }

    //用于自动填充
//	public void suggest(SuggestRequest request, ActionListener<SuggestResponse> listener) {
//		client.suggest(request, listener);
//	}
//
//	public ActionFuture<SuggestResponse> suggest(SuggestRequest request) {
//		return client.suggest(request);
//	}

    public ThreadPool threadPool() {
        return client.threadPool();
    }

    public void update(UpdateRequest request, ActionListener<UpdateResponse> listener) {
        client.update(request, listener);
    }

    public ActionFuture<UpdateResponse> update(UpdateRequest request) {
        return client.update(request);
    }

    public ActionFuture<ClearScrollResponse> clearScroll(ClearScrollRequest request) {
        return client.clearScroll(request);
    }

    public void clearScroll(ClearScrollRequest request, ActionListener<ClearScrollResponse> listener) {
        client.clearScroll(request, listener);
    }

//    public ActionFuture<DeleteIndexedScriptResponse> deleteIndexedScript(DeleteIndexedScriptRequest request) {
//        return client.deleteIndexedScript(request);
//    }
//
//    public void deleteIndexedScript(DeleteIndexedScriptRequest request,
//                                    ActionListener<DeleteIndexedScriptResponse> listener) {
//        client.deleteIndexedScript(request, listener);
//    }
//
//    public ActionFuture<GetIndexedScriptResponse> getIndexedScript(GetIndexedScriptRequest request) {
//        return client.getIndexedScript(request);
//    }
//
//    public void getIndexedScript(GetIndexedScriptRequest request, ActionListener<GetIndexedScriptResponse> listener) {
//        client..getIndexedScript(request, listener);
//    }
//
//    public ActionFuture<MultiPercolateResponse> multiPercolate(MultiPercolateRequest request) {
//        return client.multiPercolate(request);
//    }
//
//    public void multiPercolate(MultiPercolateRequest request, ActionListener<MultiPercolateResponse> listener) {
//        client.multiPercolate(request, listener);
//    }

    public BulkRequestBuilder prepareBulk() {
        return client.prepareBulk();
    }

    public ClearScrollRequestBuilder prepareClearScroll() {
        return client.prepareClearScroll();
    }

    public DeleteRequestBuilder prepareDelete() {
        return client.prepareDelete();
    }

    public DeleteRequestBuilder prepareDelete(String index, String type, String id) {
        return client.prepareDelete(index, type, id);
    }

//    public DeleteIndexedScriptRequestBuilder prepareDeleteIndexedScript() {
//        return client.prepareDeleteIndexedScript();
//    }
//
//    public DeleteIndexedScriptRequestBuilder prepareDeleteIndexedScript(String scriptLang, String id) {
//        return client.prepareDeleteIndexedScript(scriptLang, id);
//    }

    public ExplainRequestBuilder prepareExplain(String index, String type, String id) {
        return client.prepareExplain(index, type, id);
    }

    public GetRequestBuilder prepareGet() {
        return client.prepareGet();
    }

    public GetRequestBuilder prepareGet(String index, String type, String id) {
        return client.prepareGet(index, type, id);
    }

//    public GetIndexedScriptRequestBuilder prepareGetIndexedScript() {
//        return client.prepareGetIndexedScript();
//    }
//
//    public GetIndexedScriptRequestBuilder prepareGetIndexedScript(String scriptLang, String id) {
//        return client.prepareGetIndexedScript(scriptLang, id);
//    }

    public IndexRequestBuilder prepareIndex() {
        return client.prepareIndex();
    }

    public IndexRequestBuilder prepareIndex(String index, String type) {
        return client.prepareIndex(index, type);
    }

    public IndexRequestBuilder prepareIndex(String index, String type, String id) {
        return client.prepareIndex(index, type, id);
    }

    public MultiGetRequestBuilder prepareMultiGet() {
        return client.prepareMultiGet();
    }

//    public MultiPercolateRequestBuilder prepareMultiPercolate() {
//        return client.prepareMultiPercolate();
//    }

    public MultiSearchRequestBuilder prepareMultiSearch() {
        return client.prepareMultiSearch();
    }

    public MultiTermVectorsRequestBuilder prepareMultiTermVectors() {
        return client.prepareMultiTermVectors();
    }

//    public PercolateRequestBuilder preparePercolate() {
//        return client.preparePercolate();
//    }
//
//    public PutIndexedScriptRequestBuilder preparePutIndexedScript() {
//        return client.preparePutIndexedScript();
//    }
//
//    public PutIndexedScriptRequestBuilder preparePutIndexedScript(String scriptLang, String id, String source) {
//        return client.preparePutIndexedScript(scriptLang, id, source);
//    }

    public SearchRequestBuilder prepareSearch(String... indices) {
        return client.prepareSearch(indices);
    }

    public SearchScrollRequestBuilder prepareSearchScroll(String scrollId) {
        return client.prepareSearchScroll(scrollId);
    }

//    public SuggestRequestBuilder prepareSuggest(String... indices) {
//        return client.prepareSuggest(indices);
//    }

    public UpdateRequestBuilder prepareUpdate() {
        return client.prepareUpdate();
    }

    public UpdateRequestBuilder prepareUpdate(String index, String type, String id) {
        return client.prepareUpdate(index, type, id);
    }

//    public ActionFuture<PutIndexedScriptResponse> putIndexedScript(PutIndexedScriptRequest request) {
//        return client.putIndexedScript(request);
//    }
//
//    public void putIndexedScript(PutIndexedScriptRequest request, ActionListener<PutIndexedScriptResponse> listener) {
//        client.putIndexedScript(request, listener);
//    }

}
