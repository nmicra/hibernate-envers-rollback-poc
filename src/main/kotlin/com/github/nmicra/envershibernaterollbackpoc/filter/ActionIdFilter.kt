package com.github.nmicra.envershibernaterollbackpoc.filter

import com.github.nmicra.envershibernaterollbackpoc.context.RollbackContextProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class ActionIdFilter : OncePerRequestFilter() {

    @Autowired
    lateinit var contextProvider : RollbackContextProvider

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        contextProvider.setActionId(UUID.randomUUID().toString())
        try {
            filterChain.doFilter(request, response)
        } finally {
            contextProvider.clear()
        }
    }
}