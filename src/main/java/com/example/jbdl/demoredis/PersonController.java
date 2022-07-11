package com.example.jbdl.demoredis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
public class PersonController {

    private static final String PERSON_REDIS_PREFIX = "per::";
    private static final String PERSON_REDIS_LIST_KEY = "per_list";

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

//    --------- These set of APIs store values as a string ---------------------------------

    @GetMapping("/value")
    public Person getValue(@RequestParam("id") int personId) {
        // TODO: Get value from the redis database (cache) for the corresponding key
        String key = getPersonKey(personId);

        return (Person) redisTemplate.opsForValue().get(key);
    }

    @PostMapping("/value")
    public void saveValue(@Valid @RequestBody Person person,
                          @RequestParam(value = "expiry", required = false) Integer expiry) {
        // TODO: Save the key value pair in the redis database (cache)
        String key = getPersonKey(person.getId());

        if (expiry == null) {
            redisTemplate.opsForValue().set(key, person);
        } else {
            redisTemplate.opsForValue().set(key, person, expiry, TimeUnit.SECONDS);
        }

    }

    private String getPersonKey(Integer personId) {
        return PERSON_REDIS_PREFIX + personId;
    }


    //    --------- These set of APIs store values as a list of strings ---------------------------------

    @PostMapping("/add/left")
    public long addToLeftPersonList(@Valid @RequestBody Person person) {
        return redisTemplate.opsForList()
                 .leftPush(PERSON_REDIS_LIST_KEY, person);
    }

    @PostMapping("/add/right")
    public long addRightPersonList(@Valid @RequestBody Person person) {
        return redisTemplate.opsForList().rightPush(PERSON_REDIS_LIST_KEY, person);
    }

    @DeleteMapping("/remove/right")
    public List<Person> removePersonFromRight(@RequestParam(value = "count", required = false, defaultValue = "1")
                                              Integer count){

        List<Object> result = redisTemplate.opsForList().rightPop(PERSON_REDIS_LIST_KEY, count);
        return result.stream()
                .map(obj -> (Person) obj)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/remove/left")
    public List<Person> removePersonFromLeft(@RequestParam(value = "count", required = false, defaultValue = "1")
                                             Integer count) {
        List<Object> result = redisTemplate.opsForList().leftPop(PERSON_REDIS_LIST_KEY, count);

        return result.stream()
                .map(Person.class::cast)
                .collect(Collectors.toList());
    }

    @GetMapping("/people")
    public List<Person> getPersonList(@RequestParam(value = "start", required = false, defaultValue = "0") int start,
                                      @RequestParam(value = "end", required = false, defaultValue = "-1") int end) {
        List<Object> result = redisTemplate.opsForList().range(PERSON_REDIS_LIST_KEY, start, end);
        return result.stream()
                .map(Person.class::cast)
                .collect(Collectors.toList());
    }




}
