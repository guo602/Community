package com.nowcoder.community.service;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticTemplate;

    public void saveDiscussPost(DiscussPost post){
        discussPostRepository.save(post);
    }

    public void deleteDiscussPost(int id){
        discussPostRepository.deleteById(id);
    }

    public Page<DiscussPost> searchDiscussPost(String keyword, int current,int limit){
        Pageable pageable = PageRequest.of(current, limit);

        HighlightQuery highlightQuery = new HighlightQuery(
                new Highlight(
                        HighlightParameters.builder()
                                .withPreTags("<em>")
                                .withPostTags("</em>")
                                .build(),
                        List.of(new HighlightField("title"), new HighlightField("content"))),
                DiscussPost.class);

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .query(keyword)
                                .fields("title", "content")
                        )
                )
                .withSort(s -> s.field(f -> f.field("type").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("score").order(SortOrder.Desc)))
                .withSort(s -> s.field(f -> f.field("createTime").order(SortOrder.Desc)))
                .withPageable(pageable)
                .withHighlightQuery(highlightQuery)
                .build();

        SearchHits<DiscussPost> hits = elasticTemplate.search(searchQuery, DiscussPost.class);

        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : hits) {
            // 5.x 里 template 已经按 @Document 实体映射好了，不用再手写 source map
            DiscussPost post = hit.getContent();

            // 处理高亮显示的结果：有片段就覆盖掉原标题/正文
            String title = hit.getHighlightField("title").stream().findFirst().orElse(null);
            if (title != null) {
                post.setTitle(title);
            }

            String content = hit.getHighlightField("content").stream().findFirst().orElse(null);
            if (content != null) {
                post.setContent(content);
            }

            list.add(post);
        }
        // Page<DiscussPost> page = PageableExecutionUtils.getPage(list, pageable, hits::getTotalHits);
        return PageableExecutionUtils.getPage(list, pageable, hits::getTotalHits);

    }
}
