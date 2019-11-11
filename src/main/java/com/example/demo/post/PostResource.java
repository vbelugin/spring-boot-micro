package com.example.demo.post;

import com.example.demo.user.UserNotFoundException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class PostResource {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/jpa/posts")
    public MappingJacksonValue retrieveAllUsers() {
        List<Post> list = postRepository.findAll();
        return filteredResponse(list);
    }

    @GetMapping("/jpa/posts/{id}")
    public MappingJacksonValue retrieveUser(@PathVariable int id) {
        Optional<Post> post = postRepository.findById(id);
        if (!post.isPresent()) {
            throw new UserNotFoundException("id-" + id);
        }
        Resource<Post> entity = new Resource<>(post.get());

        ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        entity.add(linkTo.withRel("all-posts"));

        return filteredResponse(entity);
    }

    @PostMapping("/jpa/posts")
    public ResponseEntity<Object> createUser(@Valid @RequestBody Post post) {
        Post savedPost = postRepository.save(post);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPost.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/jpa/posts/{id}")
    public void deleteUser(@PathVariable int id) {
        postRepository.deleteById(id);
    }

    private MappingJacksonValue filteredResponse(Object entity, String... fields) {
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.serializeAllExcept(fields);

        FilterProvider filters = new SimpleFilterProvider().addFilter("PostFilter", filter);
        MappingJacksonValue mapping = new MappingJacksonValue(entity);
        mapping.setFilters(filters);

        return mapping;
    }

}
