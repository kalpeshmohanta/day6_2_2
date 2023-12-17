package com.stackroute.service;

import com.stackroute.domain.Blog;
import com.stackroute.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Service indicates annotated class is a service which holds business logic in the Service layer
 */
@Service
@CacheConfig(cacheNames = "blogs")
public class BlogServiceImpl implements BlogService {

    private BlogRepository blogRepository;

    @Autowired
    public void setBlogRepository(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    @Override
    @CacheEvict(allEntries = true)
    public Blog saveBlog(Blog blog) {
        return blogRepository.save(blog);
    }

    @Override
    @Cacheable
    public List<Blog> getAllBlogs() {
        return (List<Blog>) blogRepository.findAll();
    }

    @Override
    //@Cacheable(key = "#blogId")
    @Cacheable
    public Blog getBlogById(int blogId) {
        return blogRepository.findById(blogId).orElse(null);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(allEntries = true),
            @CacheEvict(key = "#blogId")
    })
    public Blog deleteBlogById(int blogId) {
        Blog blog = null;
        Optional<Blog> optional = blogRepository.findById(blogId);
        if (optional.isPresent()) {
            blog = optional.get();
            blogRepository.deleteById(blogId);
        }
        return blog;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(allEntries = true),
            @CacheEvict(key = "#blog.blogId")
    })
    public Blog updateBlog(Blog blog) {
        Blog updatedBlog = null;
        Optional<Blog> optional = blogRepository.findById(blog.getBlogId());
        if (optional.isPresent()) {
            Blog existingBlog = optional.get();
            existingBlog.setBlogContent(blog.getBlogContent());
            updatedBlog = saveBlog(existingBlog);
        }
        return updatedBlog;
    }
}
