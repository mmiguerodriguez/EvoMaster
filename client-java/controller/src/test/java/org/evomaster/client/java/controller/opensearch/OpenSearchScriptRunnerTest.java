package org.evomaster.client.java.controller.opensearch;

import java.io.IOException;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opensearch.client.RestClient;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.rest_client.RestClientTransport;
import org.opensearch.testcontainers.OpensearchContainer;
import org.testcontainers.utility.DockerImageName;

public class OpenSearchScriptRunnerTest {

    private static OpenSearchClient client;
    private static final int OPENSEARCH_PORT = 9200;
    private static final OpensearchContainer<?> opensearch = new OpensearchContainer<>(DockerImageName.parse("opensearchproject/opensearch:latest"));

    @BeforeAll
    public static void initClass() throws Exception {
        opensearch.start();
        int port = opensearch.getMappedPort(OPENSEARCH_PORT);

        final HttpHost host = new HttpHost("localhost", port, "http");
        final BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host), new UsernamePasswordCredentials(opensearch.getUsername(), opensearch.getPassword()));
        final RestClient restClient = RestClient.builder(host).
            setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)).build();
        final OpenSearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new OpenSearchClient(transport);
    }

    @Test
    public void testInsert() throws IOException {
        String index = "testindex";
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(index).build();
        client.indices().create(createIndexRequest);

        IndexData indexData = new IndexData("first_name", "Bruce");
        IndexRequest<IndexData> indexRequest = new IndexRequest.Builder<IndexData>().index(index).id("1").document(indexData).build();
        IndexResponse resp = client.index(indexRequest);

        SearchRequest searchRequest = new SearchRequest.Builder()
            .index(index)
            .query(q -> q.queryString(qs -> qs.fields("lastName").query("Bruce")))
            .build();

        //Search for the document
        SearchResponse<?> searchResponse = client.search(searchRequest, Map.class);

        assertEquals(1, searchResponse.hits().hits().size());

//        //Delete the document
//        client.delete(b -> b.index(index).id("1"));
//
//        // Delete the index
//        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(index).build();
//        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
    }

    public Object getConnection() {
        return client;
    }

    public static class IndexData {
        private String firstName;
        private String lastName;

        public IndexData(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        @Override
        public String toString() {
            return String.format("IndexData{firstName='%s', lastName='%s'}", firstName, lastName);
        }
    }
}
