import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.bind.annotation.*;


@RestController
@EnableAutoConfiguration
public class Example {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(path="/")
    public String home() {
        return "Hello World!";
    }

    @RequestMapping(path="/users", method=RequestMethod.GET)
    public String users(){
        List<Map<String,Object>> users;
        users = jdbcTemplate.queryForList("select * from users");
        return users.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(Example.class, args);
    }

}
