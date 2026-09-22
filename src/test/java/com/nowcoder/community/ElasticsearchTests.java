package com.nowcoder.community;

import java.util.ArrayList;
import java.util.List;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchTemplate elasticTemplate;

    @Test
    public void testInsert(){
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));

    }

    @Test
    public void testInsertList(){
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0,100 ) );
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0,100 ) );
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0,100 ) );
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(111, 0,100 ) );
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(112, 0,100 ) );
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(131, 0,100 ) );
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(132, 0,100 ) );
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(133, 0,100 ) );
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(134, 0,100 ) );

    }

    @Test
    public void testUpdate(){
        DiscussPost dp = discussPostMapper.selectDiscussPostById(231);
        dp.setContent("我是老鸟，使劲灌水");
        discussPostRepository.save(dp);

    }

    @Test
    public void testSearchByRepository(){
        HighlightQuery highlightQuery = new HighlightQuery(
                new Highlight(
                        HighlightParameters.builder()
                                .withPreTags("<em>")
                                .withPostTags("</em>")
                                .build(),
                        List.of(new HighlightField("title"), new HighlightField("content"))),
                DiscussPost.class);

        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .multiMatch(m -> m
                                .query("互联网寒冬")
                                .fields("title", "content")
                        )
                )
                .withHighlightQuery(highlightQuery)
                .build();
        SearchHits<DiscussPost> hits = elasticTemplate.search(query, DiscussPost.class);
        System.out.println(hits.getTotalHits());
        for (SearchHit<DiscussPost> hit : hits) {
            System.out.println(hit.getContent());
            System.out.println(hit.getHighlightFields());
        }

    }

    @Test
    public void testSearchByTemplate() {
        Pageable pageable = PageRequest.of(0, 10);

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
                                .query("互联网寒冬")
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

        Page<DiscussPost> page = PageableExecutionUtils.getPage(list, pageable, hits::getTotalHits);

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }

    }
}
