/*
 * Licensed to David Pilato (the "Author") under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Author licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package fr.pilato.elasticsearch.crawler.fs.test.integration;

import fr.pilato.elasticsearch.crawler.fs.beans.Doc;
import fr.pilato.elasticsearch.crawler.fs.settings.Fs;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

/**
 * Test store_source crawler setting
 */
public class FsCrawlerTestStoreSourceIT extends AbstractFsCrawlerITCase {

    @Test
    public void test_store_source() throws Exception {
        Fs fs = startCrawlerDefinition()
                .setStoreSource(true)
                .build();
        startCrawler(getCrawlerName(), fs, endCrawlerDefinition(getCrawlerName()), null);

        SearchResponse searchResponse = countTestHelper(new SearchRequest(getCrawlerName()), 1L, null);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            // We check that the field is in _source
            assertThat(hit.getSourceAsMap().get(Doc.FIELD_NAMES.ATTACHMENT), notNullValue());
        }
    }

    @Test
    public void test_do_not_store_source() throws Exception {
        startCrawler();

        countTestHelper(new SearchRequest(getCrawlerName()), 1L, null);

        SearchResponse searchResponse = elasticsearchClient.search(new SearchRequest(getCrawlerName()), RequestOptions.DEFAULT);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            // We check that the field is not part of _source
            assertThat(hit.getSourceAsMap().get(Doc.FIELD_NAMES.ATTACHMENT), nullValue());
        }
    }

    @Test
    public void test_store_source_no_index_content() throws Exception {
        Fs fs = startCrawlerDefinition()
                .setStoreSource(true)
                .setIndexContent(false)
                .build();
        startCrawler(getCrawlerName(), fs, endCrawlerDefinition(getCrawlerName()), null);

        SearchResponse searchResponse = countTestHelper(new SearchRequest(getCrawlerName()), 1L, null);
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            // We check that the field is in _source
            assertThat(hit.getSourceAsMap().get(Doc.FIELD_NAMES.ATTACHMENT), notNullValue());
        }
    }
}
