package eu.mourette

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
open class FollowmeApplication {
    @Bean
    open fun objectMapperBuilder(): Jackson2ObjectMapperBuilder = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule())
}

fun main(args: Array<String>) {
    SpringApplication.run(FollowmeApplication::class.java, *args)
}

data class Ping(var msg: String)

@RestController

@RequestMapping("/auth")
class AuthController {
    private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")


    @RequestMapping(method = arrayOf(RequestMethod.POST))
    fun create(@RequestParam("login") login: String, @RequestParam("password") password: String, response: HttpServletResponse) {
        println("create user with ${login} & ${password}")
        response.status = HttpServletResponse.SC_OK
    }
    @RequestMapping(method=arrayOf(RequestMethod.GET))
    fun findByLogin(@RequestParam("login") login: String, @RequestParam("password") password: String, response: HttpServletResponse) {
        println("enter in with ${login} & ${password}")
        response.status = HttpServletResponse.SC_NOT_FOUND
        for (credential in DUMMY_CREDENTIALS) {
            val pieces = credential.split(":".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
            if (pieces[0] == login) {
                // Account exists, return true if the password matches.
                if (pieces[1] == password){
                    response.status = HttpServletResponse.SC_OK
                } else {
                    response.status = HttpServletResponse.SC_UNAUTHORIZED
                }
            }
        }
        println("end with ${response.status}")
    }

    @RequestMapping("secure/ping")
    fun sayHelloSecured() = "hello secured"

}

@Configuration
@EnableWebSecurity
open class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http.authorizeRequests()
                .antMatchers("/", "/ping", "/auth").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()

//        http.csrf().disable()
    }

    @Autowired
    fun configureGlobal(auth: AuthenticationManagerBuilder) {
        auth.inMemoryAuthentication().withUser("user").password("password").roles("USER")
    }

}

@Configuration
open class MvcConfig : WebMvcConfigurerAdapter() {
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/login").setViewName("login")
    }
}
